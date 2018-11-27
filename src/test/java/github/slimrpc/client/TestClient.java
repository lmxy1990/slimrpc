package github.slimrpc.client;

import github.slimrpc.domain.UserOption;
import github.slimrpc.domain.UserResult;
import github.slimrpc.service.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * TestClient
 * <p>
 * 释义:
 *
 * @author: xinyi.pan
 * @create: 2018-11-06 09:42
 **/

@ContextConfiguration({"/spring-context-client.xml"})
public class TestClient extends AbstractJUnit4SpringContextTests {

    @Autowired
    @Qualifier("userService")
    private UserService userService ;


    @Test
    public void testSayHello(){

        String mayun = userService.sayHello("mayun");

        System.out.println(mayun);

    }


    @Test
    public void testMyClassParam() {
        UserOption option  = new UserOption("张三","32") ;

        UserResult login = userService.login(option);

        System.out.println(login.getLoginInfo());

    }


}
