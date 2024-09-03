package github.slimrpc.core.api;

import github.slimrpc.core.callback.ClientSideBizListener;
import github.slimrpc.core.config.RpcProperties;
import github.slimrpc.core.constant.SiteConfigConstant;
import github.slimrpc.core.io.ConnectionGroup;
import github.slimrpc.core.metadata.MetaHolder;
import github.slimrpc.core.rpc.StubSkeletonManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 客户端API
 */
public class RpcProxyFactoryBean implements BeanNameAware, Closeable {
    static Logger log = LoggerFactory.getLogger(RpcProxyFactoryBean.class);

    private final RpcProperties rpcProperties;
    private final Map<String, String> cookieSiteConfig = new ConcurrentHashMap<String, String>();

    MetaHolder metaHolder = new MetaHolder();
    ConnectionGroup connectionGroup = new ConnectionGroup();
    private String beanName;

    public RpcProxyFactoryBean(RpcProperties rpcProperties) {
        this.rpcProperties = rpcProperties;
    }

    public void start() {
        metaHolder.setFeature1(rpcProperties.getTargetFeature());
        ArrayBlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(300);
        ExecutorService threadPool = new ThreadPoolExecutor(4, 80, 60, TimeUnit.SECONDS, workQueue);
        // 加上rejectHandle
        metaHolder.setThreadPool(threadPool);

        cookieSiteConfig.put(SiteConfigConstant.client_connectionName, this.beanName);

        connectionGroup.startClient(rpcProperties.getTargetServerIPAndPortList(), rpcProperties.getTargetTlsConfig(), cookieSiteConfig, metaHolder);
    }

    public <T> T createProxyBean(Class<?> clazz) {
        return StubSkeletonManager.createConsumerProxy(connectionGroup, clazz, metaHolder);
    }

    /**
     * 客户端提供给服务端调用的bean,这个用的比较少.
     * 需要运行在start()前
     *
     * @param clazz       接口
     * @param serviceImpl 实例
     */
    public void createProvider(Class<?> clazz, Object serviceImpl) {
        StubSkeletonManager.createProvider(clazz, serviceImpl, metaHolder);
    }


    public boolean isRpcWithServerOk() {
        return connectionGroup.isRpcWithServerOk();
    }


    public void setClientSideConnectedBizListener(ClientSideBizListener listener) {

    }

    @Override
    public void close() throws IOException {
        connectionGroup.close();
    }


    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }
}
