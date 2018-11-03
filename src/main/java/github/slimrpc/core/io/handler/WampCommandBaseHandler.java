package github.slimrpc.core.io.handler;

import com.alibaba.fastjson.JSONArray;
import github.slimrpc.core.io.constant.MsgTypeConstant;
import github.slimrpc.core.metadata.MetaHolder;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WampCommandBaseHandler implements Runnable {

	static Logger log = LoggerFactory.getLogger(WampCommandBaseHandler.class);

	private MetaHolder metaHolder;
	private JSONArray jsonArray;
	private ChannelHandlerContext channelCtx;

	public WampCommandBaseHandler(MetaHolder metaHolder, ChannelHandlerContext channelCtx, JSONArray jsonArray) {
		this.metaHolder = metaHolder;
		this.channelCtx = channelCtx;
		this.jsonArray = jsonArray;
	}

	@Override
	public void run() {
		int commandType = jsonArray.getIntValue(0);
		try {
			runInCatch(commandType);
		} catch (Throwable ex) {
			log.error("{commandType:" + commandType + "}", ex);
		}
	}

	private void runInCatch(int commandType) {
		switch (commandType) {
		case MsgTypeConstant.call:
			CallCommandHandler.processCommand(metaHolder, channelCtx, jsonArray);
			break;
		case MsgTypeConstant.result:
			ResultCommandHandler.processCommand(metaHolder, channelCtx, jsonArray);
			break;
		case MsgTypeConstant.error:
			ErrorCommandHandler.processCommand(metaHolder, channelCtx, jsonArray);
			break;
		case MsgTypeConstant.hello:
			HelloCommandHandler.processCommand(metaHolder, channelCtx, jsonArray);
			break;
		case MsgTypeConstant.wellcome:
			WellcomeCommandHandler.processCommand(metaHolder, channelCtx, jsonArray);
			break;
		}
	}

}
