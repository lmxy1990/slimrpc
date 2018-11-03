package com.github.slimrpc.service.mock;

import com.github.slimrpc.test.domain.User;
import com.github.slimrpc.test.domain.option.LoginOption;
import com.github.slimrpc.test.domain.option.RegisterOption;
import com.github.slimrpc.test.domain.result.ModelResult;
import github.slimrpc.core.domain.Cookie;
import github.slimrpc.core.io.annotation.EnableSlimRpc;
import github.slimrpc.core.io.manager.ServerCookieManager;
import com.github.slimrpc.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;

@EnableSlimRpc
public class UserServiceImpl implements UserService {
	Logger log = LoggerFactory.getLogger(getClass());

	public User registerUser(User user, String password) {
		System.err.println("service:" + "/userservice/register/");
		System.err.println("userName:" + user.getDisplayName());
		user.setId((new Random()).nextLong());
		user.setDisplayName("centerserver_ok_" + user.getDisplayName());
		return user;
	}

	@Override
	public User registerUser(User user, String password, RegisterOption option) {
		return user;
	}

	@Override
	public ModelResult<User> login(User user, String password, LoginOption option) {
		long uuid;
		try {
			uuid = SecureRandom.getInstanceStrong().nextLong();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}

		Cookie cookie = new Cookie();
		cookie.setName("loginToken");
		cookie.setValue(uuid + "");
		cookie.setMaxAge(3000);
		ServerCookieManager.attachCookieToUpStreamInDetail(cookie);
		return new ModelResult(user);
	}

	@Override
	public User loginNoGenericsResult(User user, String password, LoginOption option) {
		long uuid;
		try {
			uuid = SecureRandom.getInstanceStrong().nextLong();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}

		Map<String, Cookie> cookieMap = ServerCookieManager.getReceiveCookieMap();
		for (Map.Entry<String, Cookie> entry : cookieMap.entrySet()) {
			System.err.println("cookieName:" + entry.getValue().getName());
		}
		Cookie cookie = new Cookie();
		cookie.setName("loginToken");
		cookie.setValue(uuid + "");
		cookie.setMaxAge(3000);
		ServerCookieManager.attachCookieToUpStreamInDetail(cookie);
		return user;
	}
}
