package github.slimrpc.core.config;


import github.slimrpc.core.api.RpcProxyFactoryBean;
import github.slimrpc.core.api.RpcProxyServer;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "spring.cloud.rpc.config.enabled", matchIfMissing = true)
public class SlimRpcConfigAutoConfiguration {


    /**
     * 初始化加载配置
     *
     * @param context
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(value = RpcProperties.class, search = SearchStrategy.CURRENT)
    public RpcProperties rpcConfigProperties(ApplicationContext context) {
        if (context.getParent() != null
                && BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
                context.getParent(), RpcProperties.class).length > 0) {
            return BeanFactoryUtils.beanOfTypeIncludingAncestors(context.getParent(),
                    RpcProperties.class);
        }
        return new RpcProperties();
    }


    @Bean
    @ConditionalOnMissingBean(value = RpcProxyServer.class, search = SearchStrategy.CURRENT)
    public RpcProxyServer rpcProxyServer(RpcProperties rpcProperties, ApplicationContext applicationContext) {
        RpcProxyServer rpcProxyServer = new RpcProxyServer(rpcProperties, applicationContext);
        rpcProxyServer.start();
        return rpcProxyServer;
    }

    @Bean
    @ConditionalOnMissingBean(value = RpcProxyFactoryBean.class, search = SearchStrategy.CURRENT)
    public RpcProxyFactoryBean rpcProxyFactoryBean(RpcProperties rpcProperties) {
        RpcProxyFactoryBean factoryBean = new RpcProxyFactoryBean(rpcProperties);
        factoryBean.start();
        return factoryBean;
    }


}
