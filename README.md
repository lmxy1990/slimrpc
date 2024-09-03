# slimrpc
---
基于netty,jdk本身的动态代理.结合spring封装出一个简单高可靠性的rpc框架.
目标:一切从简,轻量级,高可维护性.
使用只需要依赖jar包,简单配置即可.

## 特点
1. 简洁
2. 高效
3. 去中心化
4. 双向调用
5. 客户端/服务端双向认证


## demo
---

1.添加依赖

```
<dependency>
  <groupId>io.github.lmxy1990</groupId>
  <artifactId>slimrpc-core</artifactId>
  <version>1.0.0</version>
</dependency>
```

2.创建接口

```
public interface UserService {

    String sayHello(String name) ;


    UserResult login(UserOption userOption) ;

}
```
3.服务端实现接口

```
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

```

4. 服务端,添加配置

```
    <!--扫描路径,也可以直接配置-->
    <context:component-scan base-package="github.slimrpc" />


    <!--开启rpc 服务-->
    <bean class="github.slimrpc.core.api.RpcProxyServer" init-method="start" destroy-method="close" >
        <property name="listenPort" value="6300" />
    </bean>
```

5. 客户端,添加配置

```
    <!--rpc服务器配置-->
    <bean name="rpc" class="github.slimrpc.core.api.RpcProxyFactoryBean" init-method="start" destroy-method="close" >
        <property name="serverList">
            <props>
                <prop key="127.0.0.1" >6300</prop>
            </props>
        </property>
    </bean>

    <!--创建bean-->
    <bean name="userService" factory-bean="rpc" factory-method="createConsumerProxy">
        <!--接口路径-->
        <constructor-arg value="github.slimrpc.service.UserService" />
    </bean>
    
```
 
 6. 启动服务端

```
@ContextConfiguration({"/spring-context-server.xml"})
public class TestServer extends AbstractJUnit4SpringContextTests {


    @Test
    public void testStartServer(){

        try {
            Thread.sleep(100000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}

```

7.客户端发起请求

 ```
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
         UserOption option  = new UserOption("zhangsan","32") ;
 
         UserResult login = userService.login(option);
 
         System.out.println(login.getLoginInfo());
 
     }
 
 
 }
 
 ```
 



