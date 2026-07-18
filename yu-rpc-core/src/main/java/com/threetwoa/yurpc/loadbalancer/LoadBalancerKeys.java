package com.threetwoa.yurpc.loadbalancer;

/**
 * 负载均衡器键名常量
 *
 * @author threetwoa
 */
public interface LoadBalancerKeys {

    /**
     * 轮询
     */
    String ROUND_ROBIN = "roundRobin";

    String RANDOM = "random";

    String CONSISTENT_HASH = "consistentHash";

}
