package github.slimrpc.core.constant;

public class ClientDaemonThreadEventType {
	public static final byte noEvent = 0;

	@Deprecated
	public static final byte channelConnecting = 1;

	@Deprecated
	public static final byte channelConnected = 2;
	public static final byte channelDisconnected = 3;
	public static final byte closeDaemonThread = 4;
}
