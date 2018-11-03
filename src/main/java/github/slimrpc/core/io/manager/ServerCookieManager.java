package github.slimrpc.core.io.manager;


import github.slimrpc.core.domain.Cookie;

import java.util.HashMap;
import java.util.Map;

public class ServerCookieManager {
	private static final ThreadLocal<Map<String, Cookie>> receiveCookieMap = new ThreadLocal<Map<String, Cookie>>();
	private static final ThreadLocal<Map<String, Cookie>> setCookieMap = new ThreadLocal<Map<String, Cookie>>();

	public static void setCookieMap(Map<String, Cookie> cookieMap) {
		setCookieMap.set(cookieMap);
	}
	
	public static Map<String, Cookie> getSetCookieMap() {
		return setCookieMap.get();
	}

	public static void attachCookieToUpStreamInDetail(Cookie cookie) {
		Map<String, Cookie> map = setCookieMap.get();
		if (map == null) {
			map = new HashMap<String, Cookie>();
			setCookieMap.set(map);
		}
		map.put(cookie.getName(), cookie);
	}

	public static void copyCookieFromDownStreamToUpStream() {

	}

	public static void setReceiveCookieMap(Map<String, Cookie> cookieMap) {
		receiveCookieMap.set(cookieMap);
	}

	public static Map<String, Cookie> getReceiveCookieMap() {
		return receiveCookieMap.get();
	}

	public static void clearSetCookieMap() {
		setCookieMap.remove();
	}

	public static void clearReceiveCookieMap() {
		receiveCookieMap.remove();
	}
}
