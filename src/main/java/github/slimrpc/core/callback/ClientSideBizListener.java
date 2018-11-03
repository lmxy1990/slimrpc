package github.slimrpc.core.callback;

import java.util.EventListener;

public interface ClientSideBizListener extends EventListener {
	public void connectedBizCallback();
}
