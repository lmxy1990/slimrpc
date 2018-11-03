package com.github.slimrpc.test.domain;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

public class User implements Serializable {
	private static final long serialVersionUID = -5128432017050518465L;

	private long id;
	private String displayName;
	private long flagBit;
	private String features = "{}";
	transient private JSONObject featuresJson = null;

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

	public String getFeatures() {
		return features;
	}

	public void setFeatures(String features) {
		this.features = features;
	}

	public JSONObject getFeaturesJson() {
		return featuresJson;
	}

	public void setFeaturesJson(JSONObject featuresJson) {
		this.featuresJson = featuresJson;
	}

}
