package com.threetwoa.yurpc.fault.tolerant;

import com.threetwoa.yurpc.exception.RpcException;
import com.threetwoa.yurpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 降级到其他服务 - 容错策略
 *
 * @author threetwoa
 */
@Slf4j
public class FailBackTolerantStrategy implements TolerantStrategy {

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // 降级服务未接入前明确失败，返回 null 会导致调用方 NPE（ADR-0002 决策 7）
        log.error("fail-back 降级失败", e);
        throw new RpcException("fail-back 降级失败: " + e.getMessage());
    }
}
