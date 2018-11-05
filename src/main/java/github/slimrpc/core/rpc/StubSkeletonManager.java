package github.slimrpc.core.rpc;

import github.slimrpc.core.io.ConnectionGroup;
import github.slimrpc.core.metadata.MetaHolder;
import github.slimrpc.core.metadata.ProviderMeta;
import github.slimrpc.core.util.Methods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class StubSkeletonManager {
    static Logger log = LoggerFactory.getLogger(StubSkeletonManager.class);

    /**
     * 将服务端提供的bean注入到接口中
     *
     * @param connectionGroup 链接
     * @param clazz           接口
     * @param metaHolder      元持有者
     * @return
     */
    public static <T> T createConsumerProxy(ConnectionGroup connectionGroup, Class<?> clazz, MetaHolder metaHolder) {
        String clazzName = clazz.getName();
        RpcInvocationHandler proxyHandler = metaHolder.getClientProxyHolder().get(clazzName);
        if (proxyHandler == null) {// FIXME
            proxyHandler = new RpcInvocationHandler(connectionGroup);
            metaHolder.getClientProxyHolder().put(clazzName, proxyHandler);
        }
        return proxyHandler.generateProxy(clazz);
    }

    /**
     * 服务端创建提供者
     *
     * @param clazz   接口
     * @param serviceImpl 实例
     * @param metaHolder  元持有者
     */
    public static void createProvider(Class<?> clazz, Object serviceImpl, MetaHolder metaHolder) {
        String clazzName = clazz.getName();
        Class<?> callClass = serviceImpl.getClass();
        Class<?> annotationClass = null;
        String classInfo = callClass.toString();
        //循环代理
        boolean isProxy = classInfo.contains("EnhancerByCGLIB") || classInfo.contains("com.sun.proxy.$Proxy");
        if (isProxy) {
            String className = classInfo.substring(0, classInfo.indexOf("@"));
            try {
                annotationClass = Class.forName(className);
                Method[] methodList = annotationClass.getMethods() ;
                for (Method method : methodList) {
                    //rpc方法过滤
                    if (!checkRpcMethod(method)) continue;

                    ProviderMeta meta = new ProviderMeta();
                    String methodSign = Methods.methodSign(method);
                    meta.setServiceImpl(serviceImpl);
                    try {
                        meta.setMethod(callClass.getMethod(method.getName(), method.getParameterTypes()));
                        meta.setReturnTypeGeneric(true);

                        //存放到提供类
                        Map<String, Map<String, ProviderMeta>> providerHolder = metaHolder.getProviderHolder();
                        Map<String, ProviderMeta> metaMap = providerHolder.get(clazzName);
                        if (metaMap == null || metaMap.size() == 0) {
                            metaMap = new HashMap<>();
                        }
                        metaMap.put(methodSign, meta);
                        providerHolder.put(clazzName, metaMap);
                    } catch (NoSuchMethodException | SecurityException e) {
                        log.error("{fullInfo:'" + classInfo + "', className:'" + clazzName + "'}", e);
                    }
                }
            } catch (ClassNotFoundException e) {
                log.error("{fullInfo:'" + classInfo + "', className:'" + className + "'}", e);
            }
        } else {
            Method[] methodList = callClass.getMethods() ;
            for (Method method : methodList) {
                //rpc方法过滤
                if (!checkRpcMethod(method)) continue;

                String methodSign = Methods.methodSign(method);
                ProviderMeta meta = new ProviderMeta();
                meta.setMethod(method);
                meta.setServiceImpl(serviceImpl);

                //存放到提供类
                Map<String, Map<String, ProviderMeta>> providerHolder = metaHolder.getProviderHolder();
                Map<String, ProviderMeta> metaMap = providerHolder.get(clazzName);
                if (metaMap == null || metaMap.size() == 0) {
                    metaMap = new HashMap<>();
                }
                metaMap.put(methodSign, meta);
                providerHolder.put(clazzName, metaMap);
            }
        }
    }

    /**
     * 检查是否是RPC方法
     *
     * @param method
     * @return
     */
    public static boolean checkRpcMethod(final Method method) {
        //本地方法不代理
        if ("toString".equals(method.getName())) return false;
        if ("hashCode".equals(method.getName())) return false;
        if ("notifyAll".equals(method.getName())) return false;
        if ("equals".equals(method.getName())) return false;
        if ("wait".equals(method.getName())) return false;
        if ("getClass".equals(method.getName())) return false;
        if ("notify".equals(method.getName())) return false;
        return true;
    }

}
