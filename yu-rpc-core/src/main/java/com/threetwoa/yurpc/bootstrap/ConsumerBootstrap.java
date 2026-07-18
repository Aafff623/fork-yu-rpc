package com.threetwoa.yurpc.bootstrap;

import com.threetwoa.yurpc.RpcApplication;

/**
 * 服务消费者启动类（初始化）
 *
 * @author threetwoa
 */
public class ConsumerBootstrap {

    /**
     * 初始化
     */
    public static void init() {
        // RPC 框架初始化（配置和注册中心）
        RpcApplication.init();
    }

}
