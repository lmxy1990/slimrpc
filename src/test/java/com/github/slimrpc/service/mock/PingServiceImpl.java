package com.github.slimrpc.service.mock;

import com.github.slimrpc.service.PingService;

public class PingServiceImpl implements PingService {

	@Override
	public String echo(String msg) {
		return "echo:"+ msg;
	}

}
