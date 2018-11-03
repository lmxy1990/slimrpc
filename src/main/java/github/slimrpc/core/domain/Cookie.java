package github.slimrpc.core.domain;

import com.alibaba.fastjson.JSONAware;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Cookie implements JSONAware {
	private String name;
	private String value;
	private int maxAge = -1;

	public Cookie() {
	}

	public Cookie(String cookieName, String cookieValue, int maxAge) {
		super();
		this.name = cookieName;
		this.value = cookieValue;
		this.maxAge = maxAge;
	}

	public String getName() {
		return name;
	}

	public void setName(String cookieName) {
		this.name = cookieName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String cookieValue) {
		this.value = cookieValue;
	}

	public int getMaxAge() {
		return maxAge;
	}

	/**
	 * 
	 * @param maxAge
	 *            TimeUnit.SECOND
	 */
	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}

	public String toString() {
		return JSONObject.toJSONString(this);
	}

	@Override
	public String toJSONString() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		map.put("value", value);
		map.put("maxAge", maxAge);
		return JSONObject.toJSONString(map);
	}
}
