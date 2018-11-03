package com.github.slimrpc.service;

import com.github.slimrpc.test.domain.User;
import com.github.slimrpc.test.domain.option.LoginOption;
import com.github.slimrpc.test.domain.option.RegisterOption;
import com.github.slimrpc.test.domain.result.ModelResult;

public interface UserService {
	
	public User registerUser(User user, String password);

	public User registerUser(User user, String password, RegisterOption option);
	
	public ModelResult<User> login(User user, String password, LoginOption option);
	
	public User loginNoGenericsResult(User user, String password, LoginOption option);
}
