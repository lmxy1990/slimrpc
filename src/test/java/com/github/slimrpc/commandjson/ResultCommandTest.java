package com.github.slimrpc.commandjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import github.slimrpc.core.domain.Cookie;
import github.slimrpc.core.io.cmd.ResultCommand;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ResultCommandTest {
	@Test
	public void test_toCommandJson() {
		ResultCommand resultCmd = new ResultCommand(1256, "'''");
		Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();
		Cookie longinUserIdCookie = new Cookie("longinUserId", "53771", -1);
		cookieMap.put(longinUserIdCookie.getName(), longinUserIdCookie);
		Cookie mkey = new Cookie("mkey", "g3Myb8", 60 * 20);
		cookieMap.put(mkey.getName(), mkey);
		
		resultCmd.getDetails().put("Set-Cookie", cookieMap);
		String json = resultCmd.toCommandJson();
		System.err.println(json);
		
		test_parseSetCookie(json);
	}
	
	private void test_parseSetCookie(String json){
		JSONArray resultArray = JSON.parseArray(json);
		JSONObject cookieMap = resultArray.getJSONObject(2).getJSONObject("Set-Cookie");
		System.err.println(cookieMap.get("longinUserId"));
	}
	
	
}
