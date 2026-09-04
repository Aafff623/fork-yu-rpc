package com.threetwoa.yurpc.fault.tolerant;

import com.threetwoa.yurpc.exception.RpcException;
import com.threetwoa.yurpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 转移到其他服务节点 - 容错策略
 *
 * @author threetwoa
 */
@Slf4j
public class FailOverTolerantStrategy implements TolerantStrategy {

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // 转移调用未实现前明确失败，返回 null 会导致调用方 NPE（ADR-0002 决策 7）
        log.error("fail-over 转移调用失败", e);
        throw new RpcException("fail-over 转移调用失败: " + e.getMessage());
    }
}
