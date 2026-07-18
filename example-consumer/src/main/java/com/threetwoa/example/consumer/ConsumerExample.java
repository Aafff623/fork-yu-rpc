package com.threetwoa.example.consumer;

import com.threetwoa.example.common.model.User;
import com.threetwoa.example.common.service.UserService;
import com.threetwoa.yurpc.bootstrap.ConsumerBootstrap;
import com.threetwoa.yurpc.proxy.ServiceProxyFactory;

/**
 * 服务消费者示例
 *
 * @author threetwoa
 */
public class ConsumerExample {

    public static void main(String[] args) {
        // 服务提供者初始化
        ConsumerBootstrap.init();

        // 获取代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("threetwoa");
        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }
    }
}
