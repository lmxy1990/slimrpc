package github.slimrpc.core.metadata;

import java.lang.reflect.Type;

public class ApiProxyMeta {
	//接口类名
	private String itfName;
	//方法签名
	private String methodSign;
	private Type returnType;
	private Class<?>[] parameterTypes;
	private boolean paramTypeGeneric;


	public String getItfName() {
		return itfName;
	}

	public void setItfName(String itfName) {
		this.itfName = itfName;
	}

	public String getMethodSign() {
		return methodSign;
	}

	public void setMethodSign(String methodSign) {
		this.methodSign = methodSign;
	}

	public Type getReturnType() {
		return returnType;
	}

	public void setReturnType(Type returnType) {
		this.returnType = returnType;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public boolean isParamTypeGeneric() {
		return paramTypeGeneric;
	}

	public void setParamTypeGeneric(boolean paramTypeGeneric) {
		this.paramTypeGeneric = paramTypeGeneric;
	}

}
