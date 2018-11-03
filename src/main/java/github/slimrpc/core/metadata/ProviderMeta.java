package github.slimrpc.core.metadata;

import java.lang.reflect.Method;

public class ProviderMeta {
	//class实例
	private Object serviceImpl;
	//对应方法
	private Method method;
	//返回值(是否循环调用代理类)
	private boolean returnTypeGeneric;


	public Object getServiceImpl() {
		return serviceImpl;
	}

	public void setServiceImpl(Object serviceImpl) {
		this.serviceImpl = serviceImpl;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public boolean isReturnTypeGeneric() {
		return returnTypeGeneric;
	}

	public void setReturnTypeGeneric(boolean returnTypeGeneric) {
		this.returnTypeGeneric = returnTypeGeneric;
	}
}
