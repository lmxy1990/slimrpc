package github.slimrpc.core.io.custom;

import github.slimrpc.core.io.cmd.WampCommandBase;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

@Sharable
public class Wamp2ByteBufEncoder extends MessageToByteEncoder<WampCommandBase> {

    @Override
    protected void encode(ChannelHandlerContext ctx, WampCommandBase msg, ByteBuf out) throws Exception {
        out.writeCharSequence(msg.toCommandJson(), StandardCharsets.UTF_8);
    }
}
