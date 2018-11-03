package com.github.slimrpc.commandjson;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.slimrpc.test.domain.User;
import com.github.slimrpc.test.domain.option.RegisterOption;
import github.slimrpc.core.io.cmd.CallCommand;
import github.slimrpc.core.io.constant.MsgTypeConstant;
import com.github.slimrpc.service.mock.UserServiceImpl;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Random;

public class CommandJsonTest {
	Random rand = new Random();



	@Test
	public void testFindInterfaceByClass(){
		UserServiceImpl service = new UserServiceImpl() ;
		Class<?>[] interfaces = service.getClass().getInterfaces();
		String name = interfaces.getClass().getName();

		System.out.println(name);
	}





	@Test
	public void test_CallCommand_序列化_期望成功() {
		CallCommand cmd = new CallCommand();
		cmd.setRequestId(rand.nextLong());
		cmd.setItfName("");
		cmd.setMethodSign("");
		User user = new User();
		user.setId(6688);
		user.setDisplayName("lokki");
		RegisterOption registerOption = new RegisterOption();
		cmd.setArgs(new Object[] { user, "psw", registerOption });
		String json = JSONObject.toJSONString(cmd.fieldToArray());
		System.out.println(json);
	}

	@Test
	public void test_CallCommand_反序列化_期望成功() {
		String jsonText = "[48,4881004229002152578,\"{}\",\"/userService/registerUser/v20140308/\",[{\"displayName\":\"lokki\",\"features\":\"{}\",\"flagBit\":0,\"id\":6688},\"psw\",{}]]";
		JSONArray array = JSONObject.parseArray(jsonText);
		int i = 0;
		int msgType = array.getIntValue(i++);
		switch (msgType) {
		case MsgTypeConstant.call:
			CallCommand callCmd = new CallCommand();
			assert array.size() >= 5;
			callCmd.setRequestId(array.getLongValue(i++));
			callCmd.setOptions(new JSONObject());

			// 以下需要 JSONArray.getArray(int index, Type[] types)更直接的功能
			List<Object> args = JSONObject.parseArray(array.getString(i++), new Type[] { User.class, String.class, RegisterOption.class });
			/* 真实环境代码
			Class[] clazzList = StaticHolder.getProviderHolder().get(callCmd.getProcedureUri()).getMethod().getParameterTypes();
			args = JSONObject.parseArray(array.getString(i), clazzList);
			*/
			for(Object arg : args){
				System.out.println(arg);
			}
			break;

		case MsgTypeConstant.result:
			break;

		case MsgTypeConstant.error:
			break;

		default:
		}
	}
}
