package com.github.slimrpc.testcase;

import github.slimrpc.core.api.SlimRpcClient;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.github.slimrpc.test.domain.User;
import com.github.slimrpc.test.domain.option.LoginOption;
import com.github.slimrpc.test.domain.result.ModelResult;

import com.github.slimrpc.service.UserService;

@ContextConfiguration({ "/crossIdc/spring-context-client.xml" })
public class TlsClientTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	private UserService userService;
	@Autowired
	private SlimRpcClient rpcClient ;

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
}
