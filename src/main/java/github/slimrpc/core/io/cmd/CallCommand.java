package github.slimrpc.core.io.cmd;

import com.alibaba.fastjson.JSONObject;
import github.slimrpc.core.io.constant.MsgTypeConstant;

import java.util.concurrent.atomic.AtomicLong;

public class CallCommand implements WampCommandBase {
	private final int msgType = MsgTypeConstant.call;
	//某次请求的唯一标识符,用于结果回调
	private long requestId = 1;
	private JSONObject options = new JSONObject();
	//调用的接口名
	private String itfName;
	//调用接口的方法签名
	private String methodSign;
	private Object[] args;
	//自增变量,线程共享
	static AtomicLong requestIdPool = new AtomicLong(1);

	/**
	 * 对于每一个client端来说,自增的值,都是唯一的,
	 * 每次启动为1,每调用一次+1,
	 * 对于同client端来说,发起的每次调用,requestId都是唯一的.
	 */
	public CallCommand(){
		requestId = requestIdPool.incrementAndGet();
	}

	@Override
	public int getMsgType() {
		return msgType;
	}

	public long getRequestId() {
		return requestId;
	}

	public void setRequestId(long requestId) {
		this.requestId = requestId;
	}

	public JSONObject getOptions() {
		return options;
	}

	public void setOptions(JSONObject options) {
		this.options = options;
	}

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

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	@Override
	public Object[] fieldToArray() {
		return new Object[]{msgType, requestId, options, itfName,methodSign, args};
	}

	@Override
	public String toCommandJson() {
		return JSONObject.toJSONString(fieldToArray());
	}

}
