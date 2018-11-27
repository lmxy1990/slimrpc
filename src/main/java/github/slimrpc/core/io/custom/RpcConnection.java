package github.slimrpc.core.io.custom;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import github.slimrpc.core.constant.ConnectionStatusConstant;
import github.slimrpc.core.constant.Feature1;
import github.slimrpc.core.domain.Cookie;
import github.slimrpc.core.event.ClientDaemonThreadEvent;
import github.slimrpc.core.exception.TimeoutException;
import github.slimrpc.core.io.cmd.CallCmdCookie;
import github.slimrpc.core.io.cmd.CallCommand;
import github.slimrpc.core.io.handler.ConnectionStateHandler;
import github.slimrpc.core.io.manager.ClientCookieManager;
import github.slimrpc.core.metadata.MetaHolder;
import github.slimrpc.core.metadata.ServerMeta;
import github.slimrpc.core.rpc.CallResultFuture;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.ssl.SslHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class RpcConnection {
    static Logger log = LoggerFactory.getLogger(RpcConnection.class);

    private ServerMeta serverConfig;
    private SSLContext sslContext;
    private MetaHolder metaHolder;
    private volatile Channel channel = null;
    private NioEventLoopGroup eventLoopGroup;
    private byte reconnectCountWhenSendRpc = 3;
    private boolean needKeepConnection = true;
    AtomicInteger connectionStatus = new AtomicInteger(ConnectionStatusConstant.disconnected);
    BlockingQueue<ClientDaemonThreadEvent> ioThreadEventQueue;

    // MetaHolder metaHolder = new MetaHolder();
    private Map<String, String> siteConfig = new ConcurrentHashMap<String, String>(1);

    private ClientCookieManager cookieManager;

    /**
     * client 端用这个
     *
     * @param serverConfig
     * @param metaHolder
     * @param sslContext
     * @param cookieManager
     */
    public RpcConnection(ServerMeta serverConfig, MetaHolder metaHolder, SSLContext sslContext, ClientCookieManager cookieManager) {
        this.serverConfig = serverConfig;
        this.metaHolder = metaHolder;
        this.sslContext = sslContext;
        this.cookieManager = cookieManager;
    }

    /**
     * server 端用这个
     *
     * @param channel
     */
    public RpcConnection(Channel channel, MetaHolder metaHolder, ClientCookieManager cookieManager) {
        this.channel = channel;
        this.metaHolder = metaHolder;
        this.cookieManager = cookieManager;
    }

    public void startClient() {
        if ((metaHolder.getFeature1() & Feature1.clientFeature_needReturnIdcSetCookieToUserClient) > 0) {

        }
    }

    public void makeConnectionInCallerThread() {
        connect();
    }

    private void connect() {
        if (!connectionStatus.compareAndSet(ConnectionStatusConstant.disconnected, ConnectionStatusConstant.connecting)) {
            return;
        }

        if (eventLoopGroup != null) {
            try {
                eventLoopGroup.shutdownGracefully().sync();
                eventLoopGroup = null;
            } catch (InterruptedException e) {
                log.error("释放netty资源出现异常", e);
            }

        }

        eventLoopGroup = new NioEventLoopGroup();
        //分包
        final LengthFieldPrepender frameEncoder = new LengthFieldPrepender(4, true);
        //组包
        final LengthFieldBasedFrameDecoder frameDecoder = new LengthFieldBasedFrameDecoder(1000000, 0, 4, -4, 4);
        //encode
        final Wamp2ByteBufEncoder msgEncode = new Wamp2ByteBufEncoder();
        //decode
        final Byte2WampDecoder msgDecode = new Byte2WampDecoder();
        //handler
        final WampJsonArrayHandler msgHandler = new WampJsonArrayHandler(metaHolder);
        //connect status
        final ConnectionStateHandler connectionStateHandler = new ConnectionStateHandler(connectionStatus);

        Bootstrap clientBoot = new Bootstrap();
        clientBoot.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 15 * 1000) // 超时时间
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        //pipeline.addLast(new LoggingHandler(LogLevel.INFO));

                        if (sslContext != null) {
                            SSLEngine tlsEngine = sslContext.createSSLEngine();
                            tlsEngine.setNeedClientAuth(true);
                            tlsEngine.setUseClientMode(true);
                            SslHandler tlsHandler = new SslHandler(tlsEngine, false);
                            pipeline.addLast(tlsHandler);
                        }
                        pipeline.addLast(connectionStateHandler)
                                .addLast(frameEncoder)// encoder顺序要保证
                                .addLast(frameDecoder)
                                .addLast(msgEncode)
                                .addLast(msgDecode)
                                .addLast(msgHandler);
                    }
                });

        try {
            //链接
            ChannelFuture channelFuture = clientBoot.connect(serverConfig.getServerName(), serverConfig.getPort());
            channel = channelFuture.channel();
            channelFuture.addListener((ChannelFutureListener) future -> {
                if (future.isDone() && future.cause() != null) {
                    connectionStatus.set(ConnectionStatusConstant.disconnected);
                    log.error("{serverName:\"" + serverConfig.getServerName() + "\", serverPort:" + serverConfig.getPort() + "}", future.cause());
                } else {
                    log.info("{msg:\"connected\", serverName:\"" + serverConfig.getServerName() + "\", serverPort:" + serverConfig.getPort() + "}");
                    serverConfig.addConnectSuccessCount();
                }
            });

            boolean isSuccess = channelFuture.awaitUninterruptibly(15, TimeUnit.SECONDS);
            if (!isSuccess) {
                serverConfig.addConnectErrorCount();
                channelFuture.cancel(true);
                channel = null;
            }
        } catch (Throwable ex) {
            channel = null;
            log.error(serverConfig.toString(), ex);
            serverConfig.addConnectErrorCount();
        }
    }

    public boolean isConnectionOk() {
        if (channel == null) {
            return false;
        }
        if (this.sslContext != null) { // TLS hava self heartbeat mechanism
            return channel.isActive();
        }
        if (connectionStatus.get() == ConnectionStatusConstant.actived && channel.isActive()) {
            return true;
        }
        return false;
    }

    public boolean isNeedOpenConnection() {
        if (channel == null) {
            return true;
        }
        if (connectionStatus.get() == ConnectionStatusConstant.disconnected) {
            return true;
        }
        return false;
    }

    public void close() {
        clossResource();
    }

    public synchronized void clossResource() {
        if (channel != null) {
            try {
                channel.close();
            } catch (Throwable ex) {
                log.error(ex.getMessage(), ex);
            } finally {
                channel = null;
            }
        }
    }

    public void sendRpcOneWay(String interfaceName, String methodSign, Object[] args) {
        CallCommand callCmd = new CallCommand();
        callCmd.setItfName(interfaceName);
        callCmd.setMethodSign(methodSign);
        callCmd.setArgs(args);
        channel.write(callCmd);
    }

    public Object sendRpc(String interfaceName, String methodSign, Object[] args, Type returnType, long timeoutInMs) {
        CallCommand callCmd = new CallCommand();
        callCmd.setItfName(interfaceName);
        callCmd.setMethodSign(methodSign);
        callCmd.setArgs(args);

        String cookieJson = cookieManager.getCookieForSendToServer();
        if (cookieJson != null && cookieJson.length() > 0) {
            CallCmdCookie callCmdCookie = new CallCmdCookie(cookieJson);
            callCmd.getOptions().put("Cookie", callCmdCookie);
        }

        CallResultFuture future = new CallResultFuture(returnType);
        metaHolder.getRequestPool().put(callCmd.getRequestId(), future);
        try {
            boolean sended = false;
            for (int i = 0; i < 150; i++) {
                if (channel == null || !channel.isActive()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    continue;
                } else {
                    channel.writeAndFlush(callCmd);
                    sended = true;
                    break;
                }
            }
            if (sended) {
                future.waitReturn(timeoutInMs);
                processSetCookie(future.getDetail());

                return future.getResult();
            } else {
                throw new TimeoutException("timeout exceed 300000ms");// TODO
            }
        } finally {
            metaHolder.getRequestPool().remove(callCmd.getRequestId());
        }
    }

    private void processSetCookie(JSONObject detail) {
        JSONArray setCookieList = detail.getJSONArray("Set-Cookie");
        if (setCookieList != null) {
            Cookie[] cookieList = new Cookie[setCookieList.size()];
            for (int i = 0; i < setCookieList.size(); i++) {
                cookieList[i] = setCookieList.getObject(i, Cookie.class);
            }
            cookieManager.processSetCookie(cookieList);
        }
    }
}
