package github.slimrpc.core.rpc;

import github.slimrpc.core.io.ConnectionGroup;
import github.slimrpc.core.metadata.ApiProxyMeta;
import github.slimrpc.core.util.Methods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * 客户端调用代理类
 */
public class RpcInvocationHandler implements InvocationHandler {
	Logger log = LoggerFactory.getLogger(RpcInvocationHandler.class);

	private final ConnectionGroup connectionGroup;
	Map<Method, ApiProxyMeta> apiHolder = new HashMap<>();

	public RpcInvocationHandler(ConnectionGroup connectionGroup) {
		super();
		this.connectionGroup = connectionGroup;
	}

	public <T> T generateProxy(Class<?> clazz) {
		String clazzName = clazz.getName();
		Method[] methods = clazz.getMethods() ;
        for (Method method : methods) {
            String methodSign = Methods.methodSign(method);

            ApiProxyMeta apiMeta = new ApiProxyMeta();
            apiMeta.setItfName(clazzName);
            apiMeta.setMethodSign(methodSign);
            apiMeta.setParameterTypes(method.getParameterTypes());
            apiMeta.setReturnType(method.getGenericReturnType());
            apiHolder.put(method, apiMeta);
        }
		Object proxy = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, this);
		return (T) proxy;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// 过滤掉hashCode()/toString()/equals等本地方法
		if (StubSkeletonManager.checkRpcMethod(method)){
			return null ;
		}

		ApiProxyMeta meta = apiHolder.get(method);
		return connectionGroup.sendRpc(meta.getItfName(),meta.getMethodSign(), args, meta.getReturnType(), 300000);
	}

}
