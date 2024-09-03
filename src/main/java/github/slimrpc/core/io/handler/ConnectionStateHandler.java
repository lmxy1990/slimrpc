package github.slimrpc.core.io.handler;

import github.slimrpc.core.constant.ConnectionStatusConstant;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionStateHandler extends ChannelDuplexHandler {
    static Logger log = LoggerFactory.getLogger(ConnectionStateHandler.class);

    private AtomicInteger connectionStatus;

    public ConnectionStateHandler(AtomicInteger connectionStatus) {
        super();
        this.connectionStatus = connectionStatus;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        boolean success = connectionStatus.compareAndSet(ConnectionStatusConstant.connecting,ConnectionStatusConstant.activated);
        if (success) {
        } else {
            log.error("{msg:'channelActive() compareAndSet fail', connLifecycleState:" + connectionStatus.get() + "}");
        }

        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("{msg:'channelInactive()', connectionStatus:" + connectionStatus.get() + "}");

        connectionStatus.set(ConnectionStatusConstant.disconnected);
        ctx.fireChannelInactive();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("{msg:'exceptionCaught', connectionStatus:" + connectionStatus.get() + "}");

        connectionStatus.set(ConnectionStatusConstant.disconnected);
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }
}
