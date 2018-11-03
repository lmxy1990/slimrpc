package com.github.slimrpc.service;

import com.github.slimrpc.test.domain.User;
import com.github.slimrpc.test.domain.result.ModelResult;

public interface GenericeResultService {
	//@GenericsResult(returnType = User.class)
	ModelResult<User> queryUser();
}
