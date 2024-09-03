package github.slimrpc.core.config;

import github.slimrpc.core.domain.TlsConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ConfigurationProperties(prefix = RpcProperties.PREFIX)
public class RpcProperties {

    public static final String PREFIX = "slim.rpc";

    // common config
    private boolean needStoreCookieToDisk = false;
    // cookie manager
    private final String cookieStoreManagerClass = "github.slimrpc.core.io.manager.CookieStoreManager";


    // my config
    private int port = 0;
    private long myFeature = 0;
    private TlsConfig myTlsConfig = null;
    private Map<String, String> myCookieSiteConfig = new ConcurrentHashMap<String, String>();

    // target config
    private long targetFeature = 0;
    // 代理类扫描路径
    private String scanPackage = "github.slimrpc.service";

    // tls config
    private TlsConfig targetTlsConfig = null;
    private List<String> targetServerIPAndPortList; // 服务端IP:端口
    private Map<String, String> targetCookieSiteConfig = new ConcurrentHashMap<String, String>();


    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isNeedStoreCookieToDisk() {
        return needStoreCookieToDisk;
    }

    public void setNeedStoreCookieToDisk(boolean needStoreCookieToDisk) {
        this.needStoreCookieToDisk = needStoreCookieToDisk;
    }

    public String getCookieStoreManagerClass() {
        return cookieStoreManagerClass;
    }

    public long getMyFeature() {
        return myFeature;
    }

    public void setMyFeature(long myFeature) {
        this.myFeature = myFeature;
    }

    public TlsConfig getMyTlsConfig() {
        return myTlsConfig;
    }

    public void setMyTlsConfig(TlsConfig myTlsConfig) {
        this.myTlsConfig = myTlsConfig;
    }

    public Map<String, String> getMyCookieSiteConfig() {
        return myCookieSiteConfig;
    }

    public void setMyCookieSiteConfig(Map<String, String> myCookieSiteConfig) {
        this.myCookieSiteConfig = myCookieSiteConfig;
    }

    public long getTargetFeature() {
        return targetFeature;
    }

    public void setTargetFeature(long targetFeature) {
        this.targetFeature = targetFeature;
    }

    public TlsConfig getTargetTlsConfig() {
        return targetTlsConfig;
    }

    public void setTargetTlsConfig(TlsConfig targetTlsConfig) {
        this.targetTlsConfig = targetTlsConfig;
    }

    public List<String> getTargetServerIPAndPortList() {
        return targetServerIPAndPortList;
    }

    public void setTargetServerIPAndPortList(List<String> targetServerIPAndPortList) {
        this.targetServerIPAndPortList = targetServerIPAndPortList;
    }

    public Map<String, String> getTargetCookieSiteConfig() {
        return targetCookieSiteConfig;
    }

    public void setTargetCookieSiteConfig(Map<String, String> targetCookieSiteConfig) {
        this.targetCookieSiteConfig = targetCookieSiteConfig;
    }

    public String getScanPackage() {
        return scanPackage;
    }

    public void setScanPackage(String scanPackage) {
        this.scanPackage = scanPackage;
    }
}
