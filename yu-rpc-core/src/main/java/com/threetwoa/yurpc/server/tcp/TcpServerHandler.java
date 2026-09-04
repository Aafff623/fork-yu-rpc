package com.threetwoa.yurpc.server.tcp;

import com.threetwoa.yurpc.model.RpcRequest;
import com.threetwoa.yurpc.model.RpcResponse;
import com.threetwoa.yurpc.protocol.*;
import com.threetwoa.yurpc.registry.LocalRegistry;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 服务端 TCP 连接处理器，负责协议解码、服务调用与响应编码。
 *
 * <p>拆包与粘包处理由 {@link TcpBufferHandlerWrapper} 完成。本类只处理完整帧，
 * 并将业务异常写入 {@link RpcResponse}，使客户端容错层能够统一判断失败。</p>
 *
 * @author threetwoa
 */
public class TcpServerHandler implements Handler<NetSocket> {

    /**
     * 处理请求
     *
     * @param socket the event to handle
     */
    @Override
    public void handle(NetSocket socket) {
        TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(socket, buffer -> {
            // 只有完整协议帧才会进入此回调；解码失败说明双方协议不兼容或数据损坏。
            ProtocolMessage<RpcRequest> protocolMessage;
            try {
                protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
            } catch (IOException e) {
                throw new RuntimeException("协议消息解码错误");
            }
            RpcRequest rpcRequest = protocolMessage.getBody();
            ProtocolMessage.Header header = protocolMessage.getHeader();

            // 处理请求
            // 构造响应结果对象
            RpcResponse rpcResponse = new RpcResponse();
            // 状态默认成功；业务异常时改为非 OK，让客户端容错层能统一识别失败（ADR-0002）
            byte status = (byte) ProtocolMessageStatusEnum.OK.getValue();
            try {
                // 本地注册表保存实现类型；每次请求创建实例，要求服务实现可无参构造。
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                // 封装返回结果
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
                status = (byte) ProtocolMessageStatusEnum.BAD_RESPONSE.getValue();
            }

            // 复用请求头中的 requestId，客户端据此将异步响应关联到原请求。
            header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
            header.setStatus(status);
            ProtocolMessage<RpcResponse> responseProtocolMessage = new ProtocolMessage<>(header, rpcResponse);
            try {
                Buffer encode = ProtocolMessageEncoder.encode(responseProtocolMessage);
                socket.write(encode);
            } catch (IOException e) {
                throw new RuntimeException("协议消息编码错误");
            }
        });
        socket.handler(bufferHandlerWrapper);
    }

}
