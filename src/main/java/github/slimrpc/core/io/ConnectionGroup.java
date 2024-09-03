package github.slimrpc.core.io;

import github.slimrpc.core.constant.SiteConfigConstant;
import github.slimrpc.core.domain.TlsConfig;
import github.slimrpc.core.event.ClientDaemonThreadEvent;
import github.slimrpc.core.event.TimerAndEventDaemonThread;
import github.slimrpc.core.io.custom.RpcConnection;
import github.slimrpc.core.io.manager.ClientCookieManager;
import github.slimrpc.core.io.manager.CookieStoreManager;
import github.slimrpc.core.metadata.MetaHolder;
import github.slimrpc.core.metadata.ServerMeta;
import github.slimrpc.core.util.IpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class ConnectionGroup {
    static Logger log = LoggerFactory.getLogger(ConnectionGroup.class);

    private List<ServerMeta> serverMetaList = new CopyOnWriteArrayList<>();
    private Map<String, String> siteConfig;
    List<RpcConnection> connectionList = new CopyOnWriteArrayList<>();
    SSLContext sslContext = null;
    MetaHolder metaHolder;
    BlockingQueue<ClientDaemonThreadEvent> bossThreadEventQueue;
    TimerAndEventDaemonThread daemonThread;

    ClientCookieManager cookieManager;

    public ConnectionGroup() {
    }

    public void start() {
    }

    public void startClient(List<String> serverList, TlsConfig tlsConfig, Map<String, String> siteConfig, MetaHolder metaHolder) {
        bossThreadEventQueue = new LinkedBlockingQueue<ClientDaemonThreadEvent>(100);
        daemonThread = new TimerAndEventDaemonThread(3000, bossThreadEventQueue);

        this.siteConfig = siteConfig;
        this.metaHolder = metaHolder;

        Assert.notEmpty(serverList, "serverList is empty");

        for (String serverConfig : serverList) {
            Assert.isTrue(IpUtil.isIPAndPort(serverConfig), "serverConfig is not ip:port format");
            // 端口
            ServerMeta serverMeta = new ServerMeta(IpUtil.getIp(serverConfig), IpUtil.getPort(serverConfig));
            serverMetaList.add(serverMeta);
            if (serverList.size() == 1) {
                serverMetaList.add(serverMeta);
                serverMetaList.add(serverMeta);
            }
        }

        // 初始化sslContext
        if (tlsConfig != null) {
            initSslContext(tlsConfig);
        }

        CookieStoreManager cookieStoreManager = new CookieStoreManager(siteConfig.get(SiteConfigConstant.client_connectionName), siteConfig.get(SiteConfigConstant.client_fixture_savePath));
        cookieManager = new ClientCookieManager(cookieStoreManager);
        cookieManager.start();

        RpcConnection lastConnection = null;
        for (ServerMeta serverMeta : serverMetaList) {
            RpcConnection connection = new RpcConnection(serverMeta, this.metaHolder, sslContext, cookieManager);
            connection.startClient();
            connectionList.add(connection);
            daemonThread.addTimerJob(new KeepConnectionJob(connection));
            lastConnection = connection;
        }
        //建立链接
        lastConnection.makeConnectionInCallerThread();

        daemonThread.addTimerJob(new FlushCookieJob());
        daemonThread.start();
    }

    public void startServer() {
        bossThreadEventQueue = new LinkedBlockingQueue<ClientDaemonThreadEvent>(100);
        daemonThread = new TimerAndEventDaemonThread(1000, bossThreadEventQueue);
    }

    public void initSslContext(TlsConfig tlsConfig) {
        if (tlsConfig != null && sslContext == null) {
            try {
                //用于身份认证的证书(也可以使用某个指定的证书文件)
                KeyStore keyStore = KeyStore.getInstance("JKS");
                keyStore.load(Files.newInputStream(Paths.get(tlsConfig.getTlsKeyStoreFilePath())), tlsConfig.getTlsKeyStorePassword().toCharArray());
                KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                keyManagerFactory.init(keyStore, tlsConfig.getTlsKeyPassword().toCharArray());

                //用于验证对方数据
                KeyStore certStore = KeyStore.getInstance("JKS");
                certStore.load(Files.newInputStream(Paths.get(tlsConfig.getTlsCertStorePath())), tlsConfig.getTlsCertStorePassword().toCharArray());
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(certStore);

                sslContext = SSLContext.getInstance("TLS");
                sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
            } catch (Throwable ex) {
                String errMsg = "{tlsKeyStoreFilePath:\"" + tlsConfig.getTlsKeyStoreFilePath() + "\"}";
                log.error(errMsg, ex);
                throw new RuntimeException(errMsg, ex);
            }
        }
    }

    public void addConnection(RpcConnection connection) {
        connectionList.add(connection);
    }

    public boolean isRpcWithServerOk() {
        for (RpcConnection connection : connectionList) {
            if (connection.isConnectionOk()) {
                return true;
            }
        }

        return false;
    }

    public void sendRpcOneWay(String interfaceName, String methodSign, Object[] args) {
        RpcConnection findConnection = findConnection();
        findConnection.sendRpcOneWay(interfaceName, methodSign, args);
    }

    public void broadcastRpcOneWay(String interfaceName, String methodSign, Object[] args) {
        for (RpcConnection connection : connectionList) {
            try {
                connection.sendRpcOneWay(interfaceName, methodSign, args);
            } catch (Throwable ex) {
                log.error("{msg:\"log for continue loop\"}", ex);
            }
        }
    }

    /**
     * RPC调用
     *
     * @param interfaceName 接口名
     * @param methodSign    方法签名
     * @param args          参数
     * @param returnType    返回类型
     * @param timeoutInMs   超时时间
     * @return
     * @throws Throwable
     */
    public Object sendRpc(String interfaceName, String methodSign, Object[] args, Type returnType, long timeoutInMs) throws Throwable {
        RpcConnection connection = findConnection();
        if (connection == null) {
            throw new Throwable("can not find success connection! is server all fail?");
        }
        return connection.sendRpc(interfaceName, methodSign, args, returnType, timeoutInMs);
    }

    /**
     * 1.负载均衡,随机调用.
     * 2.链路检查,确保链路正常.
     *
     * @return
     */
    private RpcConnection findConnection() {
        RpcConnection selectedConnection = null;
        Random rand = new Random();
        for (int i = 0; i < connectionList.size(); i++) {
            int index = rand.nextInt(connectionList.size());
            RpcConnection connection = connectionList.get(index);
            if (connection.isConnectionOk()) {
                selectedConnection = connection;
                break;
            } else {
                log.error("{msg:\"some connection not ok in random choice\"}");
            }
        }
        if (selectedConnection == null) {
            for (RpcConnection connection : connectionList) {
                if (connection.isConnectionOk()) {
                    selectedConnection = connection;
                    break;
                } else {
                    log.error("{msg:\"some connection not ok in loop\"}");
                }
            }
        }

        if (selectedConnection == null) {
            log.error("{msg:\"this connection maybe not ok\"}");
            int index = rand.nextInt(connectionList.size());
            selectedConnection = connectionList.get(index);
        }
        return selectedConnection;
    }

    public void close() {
        //连接关闭
        for (RpcConnection connection : connectionList) {
            connection.close();
        }
        //守护进程关闭
        if (daemonThread != null) {
            try {
                daemonThread.close();
            } catch (Throwable ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }

    class KeepConnectionJob implements Runnable {
        private RpcConnection connection;

        public KeepConnectionJob(RpcConnection connection) {
            super();
            this.connection = connection;
        }

        @Override
        public void run() {
            if (connection.isNeedOpenConnection()) {
                connection.makeConnectionInCallerThread();
            }
        }
    }

    class FlushCookieJob implements Runnable {

        @Override
        public void run() {
            cookieManager.flushCookieToDisk();
        }

    }
}
