package github.slimrpc.core.io.handler;

import com.alibaba.fastjson.JSONArray;
import github.slimrpc.core.metadata.MetaHolder;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WellcomeCommandHandler {
	static Logger log = LoggerFactory.getLogger(WellcomeCommandHandler.class);

	public static void processCommand(MetaHolder metaHolder, ChannelHandlerContext channelCtx, JSONArray jsonArray) {
	}
}
