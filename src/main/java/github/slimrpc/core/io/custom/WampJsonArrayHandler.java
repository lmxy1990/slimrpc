package github.slimrpc.core.io.custom;

import com.alibaba.fastjson.JSONArray;
import github.slimrpc.core.io.handler.WampCommandBaseHandler;
import github.slimrpc.core.metadata.MetaHolder;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@Sharable
public class WampJsonArrayHandler extends ChannelInboundHandlerAdapter {
	private MetaHolder metaHolder;

	public WampJsonArrayHandler(MetaHolder metaHolder) {
		this.metaHolder = metaHolder;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (!(msg instanceof JSONArray)) {
			super.channelRead(ctx, msg);
		}
		JSONArray jsonArray = (JSONArray) msg;
		WampCommandBaseHandler commandHandler = new WampCommandBaseHandler(metaHolder, ctx, jsonArray);
		metaHolder.getThreadPool().submit(commandHandler);
	}
}
