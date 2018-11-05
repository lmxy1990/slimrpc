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


1. 服务端,添加配置

<code>
    <!--bean-->
    <bean name="userServiceImpl" class="com.github.slimrpc.service.mock.UserServiceImpl"/>


    <!--RPC 服务-->
    <bean name="rpcServer" class="github.slimrpc.core.api.SlimRpcServer" init-method="start" destroy-method="close">
        <!--监听端口-->
        <property name="listenPort" value="6200"/>
    </bean>
</code>

2. 客户端,添加配置

<code>
    <bean name="rpcClient" class="github.slimrpc.core.api.SlimRpcClient" init-method="start" destroy-method="close">
        <property name="serverList">
            <props>
                <prop key="localhost">6200</prop>
            </props>
        </property>
    </bean>

    <!--创建prxoy bean-->
    <bean name="userService" factory-bean="rpcClient" factory-method="createConsumerProxy">
        <constructor-arg value="com.github.slimrpc.service.UserService"/>
    </bean>
</code>
 
 3. Test
 
<code>
 
	@Autowired
	private UserService userService;

	@Test
	public void testClient() throws Throwable {
		User user = new User();
		user.setDisplayName("mayun");
		LoginOption option = new LoginOption();

		// Thread.sleep(1000 * 5);

		ModelResult<User> loginResult = null;
		try {
			loginResult = userService.login(user, "123password", option);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.err.println("login:" + loginResult.getModel().getDisplayName());
		Thread.sleep(1000 * 30);
	}
</code>
 
 
 



