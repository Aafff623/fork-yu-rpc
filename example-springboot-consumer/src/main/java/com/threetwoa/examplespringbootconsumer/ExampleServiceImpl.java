package com.threetwoa.examplespringbootconsumer;

import com.threetwoa.example.common.model.User;
import com.threetwoa.example.common.service.UserService;
import com.threetwoa.yurpc.springboot.starter.annotation.RpcReference;
import org.springframework.stereotype.Service;

/**
 * 示例服务实现类
 *
 * @author threetwoa
 */
@Service
public class ExampleServiceImpl {

    /**
     * 使用 Rpc 框架注入
     */
    @RpcReference
    private UserService userService;

    /**
     * 测试方法
     */
    public void test() {
        User user = new User();
        user.setName("threetwoa");
        User resultUser = userService.getUser(user);
        System.out.println(resultUser.getName());
    }

}
