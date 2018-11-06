package github.slimrpc.service.impl;

import github.slimrpc.core.io.annotation.EnableSlimRpc;
import github.slimrpc.domain.UserOption;
import github.slimrpc.domain.UserResult;
import github.slimrpc.service.UserService;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Component;

/**
 * UserServiceImpl
 * <p>
 * 释义:
 *
 * @author: xinyi.pan
 * @create: 2018-11-06 09:39
 **/
@Component
@EnableSlimRpc
public class UserServiceImpl implements UserService {


    @Override
    public String sayHello(String name) {
        return "hello ," + name;
    }


    @Override
    public UserResult login(UserOption userOption) {

        System.out.println("用户:" + userOption.getName());
        System.out.println("年龄:" + userOption.getAge() + "正在请求登录");
        UserResult result = new UserResult();
        result.setName(userOption.getName());
        result.setLoginInfo("登录信息:" + RandomStringUtils.randomAlphabetic(20));
        return result;
    }
}
