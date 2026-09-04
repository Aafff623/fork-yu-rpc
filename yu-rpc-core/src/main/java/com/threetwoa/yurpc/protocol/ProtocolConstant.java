package com.threetwoa.yurpc.protocol;

/**
 * 协议常量
 *
 * @author threetwoa
 */
public interface ProtocolConstant {

    /**
     * 消息头长度
     */
    int MESSAGE_HEADER_LENGTH = 17;

    /**
     * 协议魔数
     */
    byte PROTOCOL_MAGIC = 0x1;

    /**
     * 协议版本号
     */
    byte PROTOCOL_VERSION = 0x1;

    /**
     * 消息体最大长度（10MB，防止非法长度导致内存溢出）
     */
    int MAX_BODY_LENGTH = 10 * 1024 * 1024;
}
