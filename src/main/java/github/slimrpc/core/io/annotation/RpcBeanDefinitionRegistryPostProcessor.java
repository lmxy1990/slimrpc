package github.slimrpc.core.io.annotation;

import github.slimrpc.core.api.RpcProxyFactoryBean;
import github.slimrpc.core.config.RpcProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Set;

public class RpcBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

    private final RpcProperties rpcProperties;
    private final RpcProxyFactoryBean rpcProxyFactoryBean;

    public RpcBeanDefinitionRegistryPostProcessor(RpcProperties rpcProperties, RpcProxyFactoryBean rpcProxyFactoryBean) {
        this.rpcProperties = rpcProperties;
        this.rpcProxyFactoryBean = rpcProxyFactoryBean;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        // 动态创建bean
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        // 指定要扫描的注解
        scanner.addIncludeFilter(new AnnotationTypeFilter(SlimRpc.class));
        // 指定要扫描的包路径
        Set<BeanDefinition> annotatedClasses = scanner.findCandidateComponents(rpcProperties.getScanPackage());

        for (BeanDefinition beanDefinition : annotatedClasses) {

            BeanDefinition consumerProxy = null;
            try {
                consumerProxy = rpcProxyFactoryBean.createProxyBean(Class.forName(beanDefinition.getBeanClassName()));
            } catch (ClassNotFoundException e) {
                System.out.println("Class not found: " + e.getMessage());
                continue;
            }
            registry.registerBeanDefinition(beanDefinition.getBeanClassName() + "Impl", consumerProxy);
            System.out.println("Found class with annotation: " + beanDefinition.getBeanClassName());
        }
    }


}
