package com.threetwoa.yurpc.server.tcp;

import com.threetwoa.yurpc.protocol.ProtocolConstant;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;

/**
 * TCP 消息处理器包装
 * 装饰者模式，使用 recordParser 对原有的 buffer 处理能力进行增强
 *
 * @author threetwoa
 */
public class TcpBufferHandlerWrapper implements Handler<Buffer> {

    /**
     * 解析器，用于解决半包、粘包问题
     */
    private final RecordParser recordParser;

    /**
     * 当前连接（bodyLength 非法时需要主动关闭，避免 parser 卡死在错误模式）
     */
    private final NetSocket socket;

    public TcpBufferHandlerWrapper(NetSocket socket, Handler<Buffer> bufferHandler) {
        this.socket = socket;
        recordParser = initRecordParser(bufferHandler);
    }

    @Override
    public void handle(Buffer buffer) {
        recordParser.handle(buffer);
    }

    /**
     * 初始化解析器
     *
     * @param bufferHandler
     * @return
     */
    private RecordParser initRecordParser(Handler<Buffer> bufferHandler) {
        // 构造 parser
        RecordParser parser = RecordParser.newFixed(ProtocolConstant.MESSAGE_HEADER_LENGTH);

        parser.setOutput(new Handler<Buffer>() {
            // 初始化
            int size = -1;
            // 一次完整的读取（头 + 体）
            Buffer resultBuffer = Buffer.buffer();

            @Override
            public void handle(Buffer buffer) {
                // 1. 每次循环，首先读取消息头
                if (-1 == size) {
                    // 读取消息体长度
                    size = buffer.getInt(13);
                    // 长度必须有合理上界，防止非法长度导致内存溢出
                    if (size < 0 || size > ProtocolConstant.MAX_BODY_LENGTH) {
                        // 先关闭连接，避免 parser 卡在错误的 fixedSizeMode 继续读取脏数据
                        socket.close();
                        throw new RuntimeException("消息 bodyLength 非法: " + size);
                    }
                    parser.fixedSizeMode(size);
                    // 写入头信息到结果
                    resultBuffer.appendBuffer(buffer);
                } else {
                    // 2. 然后读取消息体
                    // 写入体信息到结果
                    resultBuffer.appendBuffer(buffer);
                    // 已拼接为完整 Buffer，执行处理
                    bufferHandler.handle(resultBuffer);
                    // 重置一轮
                    parser.fixedSizeMode(ProtocolConstant.MESSAGE_HEADER_LENGTH);
                    size = -1;
                    resultBuffer = Buffer.buffer();
                }
            }
        });

        return parser;
    }
}
