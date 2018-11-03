package com.github.slimrpc.testcase;

import github.slimrpc.core.api.SlimRpcServer;
import github.slimrpc.core.io.annotation.EnableSlimRpc;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration({ "/crossIdc/spring-context-server.xml" })
public class TlsServerTest extends AbstractJUnit4SpringContextTests {
	@Autowired
	private SlimRpcServer rpcServer ;
	@Autowired
	private ApplicationContext applicationContext;
	@Test
	public void testServer() throws Throwable {

		String[] beansName = applicationContext.getBeanDefinitionNames();
		for (int i = 0; i < beansName.length; i++) {
			String beanName = beansName[i];
			Object bean = applicationContext.getBean(beanName);
			EnableSlimRpc slimRpc = AnnotationUtils.findAnnotation(bean.getClass(), EnableSlimRpc.class);
			if (slimRpc == null) continue;
			Class<?>[] interfaces = bean.getClass().getInterfaces();
			Class<?> anInterface = bean.getClass().getInterfaces()[0];
		}

		Thread.sleep(1000 * 60 * 2000);
	}
}
