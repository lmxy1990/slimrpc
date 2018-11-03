package github.slimrpc.core.metadata;

import github.slimrpc.core.constant.Feature1;
import github.slimrpc.core.rpc.CallResultFuture;
import github.slimrpc.core.rpc.RpcInvocationHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;


/**
 * 数据持有者
 */
public class MetaHolder {
    /**
     * 代理类映射的提供map.
     * 第一层:接口全名 --> 提供map
     * 第二层,方法名+参数个数
     *
     */
    private Map<String, Map<String,ProviderMeta>> providerHolder = new HashMap<>();
    private Map<String, RpcInvocationHandler> clientProxyHolder = new HashMap<>();
    private Map<Long, CallResultFuture> requestPool = new ConcurrentHashMap<>();
    private long feature1 = Feature1.clientFeature_needKeepConnection;
    private ExecutorService threadPool;
    // for QOS
    private ExecutorService sysThreadPool;


    public Map<String, Map<String,ProviderMeta>> getProviderHolder() {
        return providerHolder;
    }

    public Map<String, RpcInvocationHandler> getClientProxyHolder() {
        return clientProxyHolder;
    }

    public Map<Long, CallResultFuture> getRequestPool() {
        return requestPool;
    }

    public void setRequestPool(Map<Long, CallResultFuture> requestPool) {
        this.requestPool = requestPool;
    }

    public ExecutorService getThreadPool() {
        return threadPool;
    }

    public void setThreadPool(ExecutorService threadPool) {
        this.threadPool = threadPool;
    }

    public long getFeature1() {
        return feature1;
    }

    public void setFeature1(long feature1) {
        this.feature1 = feature1;
    }
}
