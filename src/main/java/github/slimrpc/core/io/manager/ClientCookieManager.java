package github.slimrpc.core.io.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import github.slimrpc.core.domain.Cookie;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClientCookieManager {

	volatile String cookieCacheForReturnToServer = "[]";
	volatile Calendar cookieCacheExpiredTime;
	volatile boolean needFlushToDisk = false;
	Map<String, Cookie> memoryCookieStore = new ConcurrentHashMap<String, Cookie>();
	
	private CookieStoreManager storeManager;

	public ClientCookieManager(CookieStoreManager cookieStoreManager) {
		this.storeManager = cookieStoreManager;
	}
	
	public void start(){
		cookieCacheForReturnToServer = storeManager.loadCookieFromStore();
		List<Cookie> cookieList = JSONArray.parseArray(cookieCacheForReturnToServer, Cookie.class);
		for(Cookie cookie : cookieList){
			memoryCookieStore.put(cookie.getName(), cookie);
		}
	}

	public void processSetCookie(Cookie[] cookieList) {
		for (Cookie cookie : cookieList) {
			if (cookie.getMaxAge() == 0) {
				memoryCookieStore.remove(cookie.getName());
				continue;
			}
			if (cookie.getMaxAge() > 0) {
				memoryCookieStore.put(cookie.getName(), cookie);
				Calendar expiredTime = Calendar.getInstance();
				expiredTime.add(Calendar.SECOND, cookie.getMaxAge());
				if (expiredTime.before(cookieCacheExpiredTime)) {
					cookieCacheExpiredTime = expiredTime;
				}
			}
		}
		this.cookieCacheForReturnToServer = generateCallCmdCookieJson();
		needFlushToDisk = true;
	}

	public void delCookie(List<String> cookieNameList) {
		for (String name : cookieNameList) {
			memoryCookieStore.remove(name);
		}
		this.cookieCacheForReturnToServer = memoryCookieStore.toString();
		needFlushToDisk = true;
	}

	public String getCookieForSendToServer() {
		Calendar nowTime = Calendar.getInstance();
		if (nowTime.after(cookieCacheExpiredTime)) {
			cookieCacheForReturnToServer = generateCallCmdCookieJson();
		}
		return cookieCacheForReturnToServer;
	}

	public void flushCookieToDisk() {
		if (needFlushToDisk) {
			List<Cookie> cookieList = new ArrayList<Cookie>();
			for(Cookie cookie : memoryCookieStore.values()){
				if(cookie.getMaxAge() > 0){
					cookieList.add(cookie);
				}
			}
			storeManager.flushCookieToStore(JSON.toJSONString(cookieList.toArray()));
			needFlushToDisk = false;
		}
	}

	private String generateCallCmdCookieJson() {
		List<Cookie> cookieList = new ArrayList<Cookie>();
		Iterator<Cookie> it = memoryCookieStore.values().iterator();
		for (; it.hasNext();) {
			Cookie cookie = it.next();
			Calendar nowTime = Calendar.getInstance();
			if (nowTime.after(cookieCacheExpiredTime)) {
				it.remove();
			} else {
				cookieList.add(cookie);
			}
		}
		return JSON.toJSONString(cookieList.toArray());
	}
}
