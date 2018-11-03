package github.slimrpc.core.api;

import github.slimrpc.core.callback.ClientSideBizListener;
import github.slimrpc.core.constant.SiteConfigConstant;
import github.slimrpc.core.domain.TlsConfig;
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
public class SlimRpcClient implements BeanNameAware, Closeable {
    static Logger log = LoggerFactory.getLogger(SlimRpcClient.class);

    private long feature1 = 0;
    private TlsConfig tlsConfig = null;
    //域名-->端口
    private Map<String, String> serverList;
    private Map<String, String> siteConfig = new ConcurrentHashMap<String, String>();
    private String beanName;
    private String cookieStoreManagerClass = "github.slimrpc.core.io.manager.CookieStoreManager";
    private boolean needStoreCookieToDisk = false;

    MetaHolder metaHolder = new MetaHolder();
    ConnectionGroup connectionGroup = new ConnectionGroup();


    public SlimRpcClient() {
    }

    public void start() {
        metaHolder.setFeature1(feature1);
        ArrayBlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(300);
        ExecutorService threadPool = new ThreadPoolExecutor(4, 80, 60, TimeUnit.SECONDS, workQueue);// TODO
        // 加上rejectHandle
        metaHolder.setThreadPool(threadPool);

        siteConfig.put(SiteConfigConstant.client_connectionName, this.beanName);

        connectionGroup.startClient(serverList, tlsConfig, siteConfig, metaHolder);
    }

    public <T> T createConsumerProxy(Class<?> clazz) {
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

    public Map<String, String> getServerList() {
        return serverList;
    }

    public void setServerList(Map<String, String> serverList) {
        this.serverList = serverList;
    }

    public void setFeature1(long connectionFeature) {
        this.feature1 = connectionFeature;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    public void setSiteConfig(Map<String, String> siteConfig) {
        this.siteConfig = siteConfig;
    }

    public void setClientSideConnectedBizListener(ClientSideBizListener listener) {

    }

    public void setTlsConfig(TlsConfig tlsConfig) {
        this.tlsConfig = tlsConfig;
    }

    @Override
    public void close() throws IOException {
        connectionGroup.close();
    }


}
