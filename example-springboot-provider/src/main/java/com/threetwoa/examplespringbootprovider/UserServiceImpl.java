package com.threetwoa.examplespringbootprovider;

import com.threetwoa.example.common.model.User;
import com.threetwoa.example.common.service.UserService;
import com.threetwoa.yurpc.springboot.starter.annotation.RpcService;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 *
 * @author threetwoa
 */
@Service
@RpcService
public class UserServiceImpl implements UserService {

    public User getUser(User user) {
        System.out.println("用户名：" + user.getName());
        return user;
    }
}
