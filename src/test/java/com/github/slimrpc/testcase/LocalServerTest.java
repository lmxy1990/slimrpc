package com.github.slimrpc.testcase;

import github.slimrpc.core.api.SlimRpcServer;
import com.github.slimrpc.service.UserService;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.github.slimrpc.test.domain.User;

@ContextConfiguration({"/localServerSide/spring-context.xml"})
public class LocalServerTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	UserService userService;
	@Autowired
	private SlimRpcServer slimRpcServer ;

	@Test
	public void test() throws Exception {
		Assert.assertNotNull("注入", userService);
		User user = new User();
		user.setDisplayName("mayun");
		User result = userService.registerUser(user, "23456");
		System.err.println(result.getDisplayName() + "\nid:" + result.getId());

		slimRpcServer.setBeanName("2222");

		System.out.println(1);
		Thread.sleep(60000);
		
		user.setDisplayName("jojo");
		result = userService.registerUser(user, "789");
		System.err.println(result.getDisplayName() + "\nid:" + result.getId());
	}
}
