package com.threetwoa.yurpc.exception;

/**
 * 自定义异常类
 *
 * @author threetwoa
 */
public class RpcException extends RuntimeException {

    public RpcException(String message) {
        super(message);
    }

}
