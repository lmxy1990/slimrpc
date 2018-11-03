package github.slimrpc.core.io.handler;

import com.alibaba.fastjson.JSONArray;
import github.slimrpc.core.metadata.MetaHolder;
import github.slimrpc.core.rpc.CallResultFuture;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorCommandHandler {
	static Logger log = LoggerFactory.getLogger(ErrorCommandHandler.class);

	public static void processCommand(MetaHolder metaHolder, ChannelHandlerContext channelCtx, JSONArray jsonArray) {
		long requestId = jsonArray.getLongValue(2);
		CallResultFuture callResult = metaHolder.getRequestPool().get(requestId);
		if (callResult == null) {
			log.error("{msg:'receive timeout result, maybe server method too slow', requestId:" + requestId + "}");
			return;
		}

		String errorMsg = jsonArray.getObject(5, String[].class)[0];
		if (errorMsg == null) {
			errorMsg = jsonArray.getObject(5, String[].class)[1];
		}

		if (errorMsg == null) {
			errorMsg = "Unexpected error occur.";
		}

		callResult.returnWithErrorMsg(errorMsg);

	}
}
