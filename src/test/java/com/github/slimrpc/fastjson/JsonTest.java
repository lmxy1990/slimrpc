package com.github.slimrpc.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.slimrpc.test.domain.User;
import com.github.slimrpc.test.domain.result.ModelResult;
import com.google.gson.Gson;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonTest {

	@Test
	public void test_genericsList() {
		List<User> userList = new ArrayList<>();
		User user = new User();
		user.setId(102);
		user.setDisplayName("lokki");
		userList.add(user);

		user = new User();
		user.setId(101);
		user.setDisplayName("zhoufeng");
		userList.add(user);

		String json = JSON.toJSONString(userList, SerializerFeature.WriteClassName);
		System.err.println(json);

		Object obj = JSON.parse(json);
		List list = (List) obj;
		System.err.println(list.get(0).getClass());
	}

	@Test
	public void test_genericsTypeReference() {
		TypeReference typeReference = new TypeReference<ModelResult<User>>() {
		};
		System.err.println(typeReference.getType());
	}

	@Test
	public void test_genericsReflect() throws Throwable {
		Class<?> clazz = Class.forName("com.github.slimrpc.service.GenericeResultService");
		Method method = clazz.getMethod("queryUser", null);

		Type returnType = method.getGenericReturnType();
		// Type[] typeList =
		System.err.println(returnType);
	}

	@Test
	public void test_gsonGenerics() throws Throwable {
		Class<?> clazz = Class.forName("com.github.slimrpc.service.GenericeResultService");
		Method method = clazz.getMethod("queryUser", null);
		String resultJson = "{\"model\":{\"displayName\":\"lokki\",\"features\":\"{}\",\"flagBit\":0,\"id\":0},\"success\":true}";

		Type returnType = method.getGenericReturnType();

		Gson gson = new Gson();
		ModelResult<User> result = gson.fromJson(resultJson, returnType);
		System.err.println(result.getModel().getDisplayName());
	}

	@Test
	public void test_parseEmptyArray() {
		JSONArray array = JSONArray.parseArray("[]");
		System.err.println(array);
		System.err.println(array.toJSONString());
	}

	@Test
	public void test_parseEmptyObject() {
		JSONObject obj = JSON.parseObject("{}");
		System.err.println("obj:" + obj);
		System.err.println("toString:" + obj.toJSONString());
	}

	@Test
	public void test_writeClassNameWithLongNoL() {
		Object[] array1 = new Object[] { 2, "string" };
		String json = JSON.toJSONString(array1, SerializerFeature.WriteClassName);
		System.out.println(json);
	}

}
