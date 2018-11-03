package github.slimrpc.core.io.cmd;

import com.alibaba.fastjson.JSONObject;
import github.slimrpc.core.io.constant.MsgTypeConstant;

public class WellcomeCommand implements WampCommandBase {

	private int msgType = MsgTypeConstant.wellcome;
	private String session;

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	@Override
	public Object[] fieldToArray() {
		return new Object[]{msgType, session};
	}

	@Override
	public String toCommandJson() {
		return JSONObject.toJSONString(fieldToArray());
	}
}
