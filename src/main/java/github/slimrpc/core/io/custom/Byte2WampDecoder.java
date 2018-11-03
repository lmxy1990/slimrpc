package github.slimrpc.core.io.custom;

import com.alibaba.fastjson.JSONArray;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

@Sharable
public class Byte2WampDecoder extends MessageToMessageDecoder<ByteBuf> {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        final byte[] array;
        final int offset;
        final int length = msg.readableBytes();

        if (msg.hasArray()) {
            array = msg.array();
            offset = msg.arrayOffset() + msg.readerIndex();
        } else {
            array = new byte[length];
            msg.getBytes(msg.readerIndex(), array, 0, length);
            offset = 0;
        }

		
        JSONArray jsonArray = JSONArray.parseArray(new String(array, offset, length, "UTF-8"));
        out.add(jsonArray);
	}

}
