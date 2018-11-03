package github.slimrpc.core.io.cmd;

import com.alibaba.fastjson.JSONAware;

public class CallCmdCookie implements JSONAware {
	
	private String content;
	
	public CallCmdCookie(String content){
		this.content = content;
	}

	@Override
	public String toJSONString() {
		return content;
	}

}
