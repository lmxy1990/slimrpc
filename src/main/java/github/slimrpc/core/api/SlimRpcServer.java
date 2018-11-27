package github.slimrpc.core.api;

import github.slimrpc.core.constant.SiteConfigConstant;
import github.slimrpc.core.domain.ProviderClazz;
import github.slimrpc.core.domain.TlsConfig;
import github.slimrpc.core.io.ConnectionGroup;
import github.slimrpc.core.io.annotation.EnableSlimRpc;
import github.slimrpc.core.io.custom.Byte2WampDecoder;
import github.slimrpc.core.io.custom.RpcConnection;
import github.slimrpc.core.io.custom.Wamp2ByteBufEncoder;
import github.slimrpc.core.io.custom.WampJsonArrayHandler;
import github.slimrpc.core.io.manager.ClientCookieManager;
import github.slimrpc.core.io.manager.CookieStoreManager;
import github.slimrpc.core.metadata.MetaHolder;
import github.slimrpc.core.rpc.StubSkeletonManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.io.Closeable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 服务端开启RPC入口类
 */
public class SlimRpcServer implements BeanNameAware, Closeable {
    static Logger log = LoggerFactory.getLogger(SlimRpcServer.class);

    private int listenPort;
    private TlsConfig tlsConfig = null;
    private Map<String, String> siteConfig = new ConcurrentHashMap<String, String>();
    private String beanName;

    EventLoopGroup bossEventLoop = new NioEventLoopGroup();
    EventLoopGroup workerEventLoop = new NioEventLoopGroup();
    SSLContext sslContext = null;
    ConnectionGroup connectionGroup = new ConnectionGroup();
    MetaHolder metaHolder = new MetaHolder();
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 采用spring配置加入
     *
     * @param providerClazzes
     */
    public SlimRpcServer(List<ProviderClazz> providerClazzes) {
        for (int i = 0; i < providerClazzes.size(); i++) {
            ProviderClazz providerClazz = providerClazzes.get(i);
            if (providerClazz.clazz == null || providerClazz.bean == null) {
                log.error("interface config has empty!please check provider config!");
                continue;
            }
            this.createProvider(providerClazz.clazz, providerClazz.bean);
        }
    }

    public SlimRpcServer() {
    }

    /**
     * 开启RPC服务
     */
    public void start() {
        //初始化服务端提供的代理类
        this.initRpcServer();
        //netty start
        ArrayBlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(300);
        ExecutorService threadPool = new ThreadPoolExecutor(4, 80, 60, TimeUnit.SECONDS, workQueue);
        // 加上rejectHandle
        metaHolder.setThreadPool(threadPool);

        siteConfig.put(SiteConfigConstant.client_connectionName, this.beanName);

        CookieStoreManager cookieStoreManager = new CookieStoreManager(siteConfig.get(SiteConfigConstant.client_connectionName), siteConfig.get(SiteConfigConstant.client_fixture_savePath));
        ClientCookieManager cookieManager = new ClientCookieManager(cookieStoreManager);
        cookieManager.start();

        connectionGroup.initClientSslContext(tlsConfig, sslContext);
        connectionGroup.startServer();

        ServerBootstrap nettyBoot = new ServerBootstrap();
        //http黏包处理.分包,lengthFieldLength 数据格式长度.1=1byte=8位=256,2=16位,3=24位,4=32位,8=64位,true表示长度位会添加到值中
        final LengthFieldPrepender frameEncoder = new LengthFieldPrepender(4, true);
        //FrameDecoder
        LengthFieldBasedFrameDecoder frameDecoder = new LengthFieldBasedFrameDecoder(1000000, 0, 4, -4, 4);
        //编码,Message -> ByteBuf
        final Wamp2ByteBufEncoder msgEncoder = new Wamp2ByteBufEncoder();
        //解码,ByteBuf -> Message
        final Byte2WampDecoder msgDecoder = new Byte2WampDecoder();
        //handler
        final WampJsonArrayHandler msgHandler = new WampJsonArrayHandler(metaHolder);

        nettyBoot.group(bossEventLoop, workerEventLoop)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        //pipeline.addLast(new LoggingHandler(LogLevel.INFO));

                        if (sslContext != null) {
                            SSLEngine tlsEngine = sslContext.createSSLEngine();
                            tlsEngine.setNeedClientAuth(true);
                            tlsEngine.setUseClientMode(false);
                            SslHandler tlsHandler = new SslHandler(tlsEngine, false);
                            pipeline.addLast(tlsHandler);
                        }
                        pipeline.addLast(frameEncoder)// encoder顺序要保证
                                .addLast(frameDecoder)
                                .addLast(msgEncoder)
                                .addLast(msgDecoder)
                                .addLast(msgHandler);
                        RpcConnection rpcConnection = new RpcConnection(ch, metaHolder, cookieManager);
                        connectionGroup.addConnection(rpcConnection);
                    }
                });
        try {
            //启动
            ChannelFuture channelFuture = nettyBoot.bind(listenPort).sync();
        } catch (Throwable ex) {
            log.error("{bindPort:" + listenPort + "}", ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void close() {
        bossEventLoop.shutdownGracefully();
        workerEventLoop.shutdownGracefully();
    }

    /**
     * 需要运行在start()前
     *
     * @param clazz 接口
     * @param bean  实现类
     */
    private void createProvider(Class<?> clazz, Object bean) {
        StubSkeletonManager.createProvider(clazz, bean, metaHolder);
    }


    /**
     * 创建调用客户端的代理类(服务端调用客户端.一般不用)
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T createConsumerProxy(Class<?> clazz) {
        return StubSkeletonManager.createConsumerProxy(connectionGroup, clazz, metaHolder);
    }


    public void setTlsConfig(TlsConfig tlsConfig) {
        this.tlsConfig = tlsConfig;
    }

    public void setListenPort(int serverPort) {
        this.listenPort = serverPort;
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }


    /**
     * 注解的bean,自动加入
     *
     */
    private void initRpcServer() {
        String[] beansName = applicationContext.getBeanDefinitionNames();
        for (int i = 0; i < beansName.length; i++) {
            String beanName = beansName[i];
            Object bean = applicationContext.getBean(beanName);
            EnableSlimRpc slimRpc = AnnotationUtils.findAnnotation(bean.getClass(), EnableSlimRpc.class);
            if (slimRpc == null) continue;
            Class<?>[] clazzes = bean.getClass().getInterfaces();
            if (clazzes == null || clazzes.length == 0) continue;
            Arrays.stream(clazzes).forEach(c -> this.createProvider(c, bean));
        }
    }

}
