package github.slimrpc.core.rpc;

import com.alibaba.fastjson.JSONObject;
import github.slimrpc.core.exception.TimeoutException;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;

public class CallResultFuture {
    private Object result;
    private JSONObject detail;
    Object lock = new Object();

    private Type returnType;

    private String errorMsg;

    public CallResultFuture(Type returnType) {
        this.returnType = returnType;
    }

    public void waitReturn(long timeoutInMs) {
        synchronized (lock) {
            try {
                lock.wait(timeoutInMs);
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        if (hasException()) {
            throw new RuntimeException(errorMsg);
        }

        if (result == null && returnType != null) {
            throw new TimeoutException("{timeoutInMs:" + timeoutInMs + "}");
        }
        if (result instanceof Throwable) {
            Throwable e = (Throwable) result;
            throw new RuntimeException(e);
        }
    }

    public void putResultAndReturn(Object result) {
        this.result = result;
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    public void returnWithVoid() {
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    public void returnWithErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    public Object getResult() {
        return result;
    }

    public JSONObject getDetail() {
        return detail;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public void setDetail(JSONObject detail) {
        this.detail = detail;
    }

    public Type getReturnType() {
        return returnType;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    public boolean hasException() {
        return StringUtils.isEmpty(result) && !StringUtils.isEmpty(errorMsg);
    }

}
