package com.github.slimrpc.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.time.LocalDateTime;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String displayName;
	private long flagBit;
	private JSONObject features = new JSONObject();
	private LocalDateTime createTime = LocalDateTime.now();
	private LocalDateTime updateTime = LocalDateTime.now();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public long getFlagBit() {
		return flagBit;
	}

	public void setFlagBit(long flagBit) {
		this.flagBit = flagBit;
	}

	public JSONObject getFeatures() {
		return features;
	}

	public void setFeatures(JSONObject features) {
		this.features = features;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	public LocalDateTime getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(LocalDateTime updateTime) {
		this.updateTime = updateTime;
	}
	
	public String toString() {
		return JSON.toJSONString(this);
	}

}
