package github.slimrpc.core.metadata;

import com.alibaba.fastjson.JSON;

public class ServerMeta {
	private String serverName;
	private int port;
	private int connectErrorCount;
	private int connectSuccessCount;

	public ServerMeta(String serverName, int port) {
		this.serverName = serverName;
		this.port = port;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public int getPort() {
		return port;
	}

	public void setPort(short port) {
		this.port = port;
	}

	public long getConnectErrorCount() {
		return connectErrorCount;
	}

	public void setConnectErrorCount(int connectErrorCount) {
		this.connectErrorCount = connectErrorCount;
	}

	public void addConnectErrorCount() {
		if (connectErrorCount > Integer.MAX_VALUE - 10000) {
			connectErrorCount = 0;
		}
		connectErrorCount++;
	}

	public String toString() {
		return JSON.toJSONString(this);
	}

	public int getConnectSuccessCount() {
		return connectSuccessCount;
	}

	public void setConnectSuccessCount(int connectSuccessCount) {
		this.connectSuccessCount = connectSuccessCount;
	}

	public void addConnectSuccessCount() {
		if (connectSuccessCount > Integer.MAX_VALUE - 10000) {
			connectSuccessCount = 0;
		}
		this.connectSuccessCount++;
	}
}
