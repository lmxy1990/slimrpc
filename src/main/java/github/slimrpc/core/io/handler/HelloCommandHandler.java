package github.slimrpc.core.io.handler;

import com.alibaba.fastjson.JSONArray;
import github.slimrpc.core.domain.UserSession;
import github.slimrpc.core.io.cmd.WelcomeCommand;
import github.slimrpc.core.metadata.MetaHolder;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HelloCommandHandler {
	static Logger log = LoggerFactory.getLogger(HelloCommandHandler.class);

	static Map<String, UserSession> sessionPool = new ConcurrentHashMap<>(300);

	public static void processCommand(MetaHolder metaHolder, ChannelHandlerContext channelCtx, JSONArray jsonArray) {
		String realm = jsonArray.getString(1);
		if (realm.equals("newSession")) {
			UserSession userSession = new UserSession();
			String sessionId = UUID.randomUUID().toString();
			if (sessionPool.putIfAbsent(sessionId, userSession) != null) {
				log.warn("{msg:'uuid碰撞'}");
				channelCtx.close();
			}
			WelcomeCommand wellcome = new WelcomeCommand();
			wellcome.setSession(sessionId);
			channelCtx.writeAndFlush(wellcome);
			// TODO call sessionListener
		} else {
			String sessionId = realm;
			WelcomeCommand wellcome = new WelcomeCommand();
			wellcome.setSession(sessionId);
			channelCtx.writeAndFlush(wellcome);
			channelCtx.writeAndFlush(wellcome);

		}
	}
}
