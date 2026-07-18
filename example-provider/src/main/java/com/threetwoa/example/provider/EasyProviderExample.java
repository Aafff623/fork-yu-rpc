package com.threetwoa.example.provider;

import com.threetwoa.example.common.service.UserService;
import com.threetwoa.yurpc.registry.LocalRegistry;
import com.threetwoa.yurpc.server.HttpServer;
import com.threetwoa.yurpc.server.VertxHttpServer;

/**
 * 简易服务提供者示例
 *
 * @author threetwoa
 */
public class EasyProviderExample {

    public static void main(String[] args) {
        // 注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // 启动 web 服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8080);
    }
}
