package com.threetwoa.yurpc.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.threetwoa.yurpc.RpcApplication;
import com.threetwoa.yurpc.exception.RpcException;
import com.threetwoa.yurpc.model.RpcRequest;
import com.threetwoa.yurpc.model.RpcResponse;
import com.threetwoa.yurpc.model.ServiceMetaInfo;
import com.threetwoa.yurpc.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Vertx TCP 请求客户端
 *
 * <p>失败语义见 ADR-0002：连接、编码、解码和响应等待全部有界，任何一步失败都会
 * 异常完成 Future，不允许调用方无限挂起。</p>
 *
 * @author threetwoa
 */
public class VertxTcpClient {

    /**
     * 等待响应的超时时间（毫秒）
     */
    private static final long RESPONSE_TIMEOUT_MILLIS = 10 * 1000L;

    /**
     * 共享 Vertx 实例（每次请求新建会导致 event loop 线程泄漏，必须复用）
     */
    private static final Vertx VERTX = Vertx.vertx();

    /**
     * 发送请求
     *
     * @param rpcRequest
     * @param serviceMetaInfo
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws InterruptedException, ExecutionException {
        // 发送 TCP 请求
        NetClient netClient = VERTX.createNetClient();
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        netClient.connect(serviceMetaInfo.getServicePort(), serviceMetaInfo.getServiceHost(),
                result -> {
                    if (!result.succeeded()) {
                        // 连接失败必须异常完成 Future，否则调用方会无限挂起
                        responseFuture.completeExceptionally(
                                new RpcException("连接 TCP 服务失败: " + serviceMetaInfo.getServiceAddress()));
                        return;
                    }
                    NetSocket socket = result.result();
                    // 发送数据
                    // 构造消息
                    ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
                    ProtocolMessage.Header header = new ProtocolMessage.Header();
                    header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                    header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                    header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
                    header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                    // 生成全局请求 ID
                    header.setRequestId(IdUtil.getSnowflakeNextId());
                    protocolMessage.setHeader(header);
                    protocolMessage.setBody(rpcRequest);

                    // 编码请求
                    try {
                        Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
                        socket.write(encodeBuffer);
                    } catch (IOException e) {
                        responseFuture.completeExceptionally(new RpcException("协议消息编码错误"));
                        return;
                    }

                    // 接收响应
                    TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(socket,
                            buffer -> {
                                try {
                                    ProtocolMessage<RpcResponse> rpcResponseProtocolMessage =
                                            (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                                    // 校验响应归属：requestId 必须与当前请求一致（ADR-0002）
                                    if (rpcResponseProtocolMessage.getHeader().getRequestId() != header.getRequestId()) {
                                        responseFuture.completeExceptionally(new RpcException("响应 requestId 与请求不匹配"));
                                        return;
                                    }
                                    // 服务端业务异常：非 OK 状态转换为异常，交给重试与容错层
                                    if (rpcResponseProtocolMessage.getHeader().getStatus() != ProtocolMessageStatusEnum.OK.getValue()) {
                                        RpcResponse errorResponse = rpcResponseProtocolMessage.getBody();
                                        responseFuture.completeExceptionally(new RpcException("服务调用异常: "
                                                + (errorResponse != null ? errorResponse.getMessage() : "未知错误")));
                                        return;
                                    }
                                    responseFuture.complete(rpcResponseProtocolMessage.getBody());
                                } catch (Exception e) {
                                    // decoder 等抛出的 RuntimeException 也要兜住，否则 future 永不完成
                                    responseFuture.completeExceptionally(new RpcException("协议消息解码错误"));
                                }
                            }
                    );
                    socket.handler(bufferHandlerWrapper);

                });

        try {
            // 有界等待响应，超时快速失败
            return responseFuture.get(RESPONSE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new RpcException("RPC 响应超时: " + serviceMetaInfo.getServiceAddress());
        } finally {
            // 记得关闭连接（一连接一请求；Vertx 实例可能被共享，不在此销毁）
            netClient.close();
        }
    }
}
