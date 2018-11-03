package com.github.slimrpc.commandjson;


import github.slimrpc.core.io.cmd.CallCommand;
import org.junit.Test;

public class CallCommandTest {

	@Test
	public void test_setCookieWithJsonString(){
		CallCommand callCmd = new CallCommand();
		callCmd.getOptions().put("json", "{\'Cookie\':\"value\"}");
		System.err.println(callCmd.toCommandJson());
	}
}
