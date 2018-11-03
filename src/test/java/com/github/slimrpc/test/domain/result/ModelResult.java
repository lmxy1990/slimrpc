package com.github.slimrpc.test.domain.result;

public class ModelResult<T> {
	private boolean success = true;
	private T model;

	public ModelResult() {
	}

	public ModelResult(T model) {
		this.model = model;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public T getModel() {
		return model;
	}

	public void setModel(T result) {
		this.model = result;
	}

	public ModelResult<T> putResult(T result) {
		this.model = result;
		return this;
	}
}
