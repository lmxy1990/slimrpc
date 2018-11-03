package github.slimrpc.core.io.cmd;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import github.slimrpc.core.io.constant.MsgTypeConstant;

public class ExceptionErrorCommand implements WampCommandBase {
	private int msgType = MsgTypeConstant.error;
	private int requestType = MsgTypeConstant.call;
	private long requestId = 0;
	private String details = "{}";
	private String errorUri = "java.lang.RuntimeException";
	private String[] exceptionResult = new String[2];
	
	public ExceptionErrorCommand(long requestId, Throwable ex){
		this.requestId = requestId;
		errorUri = ex.getClass().getName();
		exceptionResult[0] = ex.getMessage();
		Throwable target = ex.getCause();
		exceptionResult[1] = target != null ? target.toString() : null;
	}

	@Override
	public int getMsgType() {
		return msgType;
	}

	public int getRequestType() {
		return requestType;
	}

	public void setRequestType(int requestType) {
		this.requestType = requestType;
	}

	public long getRequestId() {
		return requestId;
	}

	public void setRequestId(long requestId) {
		this.requestId = requestId;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getErrorUri() {
		return errorUri;
	}

	public void setErrorUri(String errorUri) {
		this.errorUri = errorUri;
	}

	public String[] getExceptionResult() {
		return exceptionResult;
	}

	public void setExceptionResult(String[] exceptionResult) {
		this.exceptionResult = exceptionResult;
	}

	@Override
	public Object[] fieldToArray() {
		return new Object[]{msgType, requestType, requestId, details, errorUri, exceptionResult};
	}

	@Override
	public String toCommandJson() {
		return JSONObject.toJSONString(fieldToArray(), SerializerFeature.WriteClassName);
	}

}
