package github.slimrpc.core.io.cmd;


import github.slimrpc.core.io.constant.MsgTypeConstant;

public class HelloCommand {
	private int msgType = MsgTypeConstant.hello;
	private String realm = "newSession";

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public String getRealm() {
		return realm;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

}
