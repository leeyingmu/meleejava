package com.melee.meleejava.web;

import java.util.HashMap;
import java.util.Map;

import com.melee.meleejava.utils.JsonUtils;

public class MeleeHttpResponse {
	
	
	protected int statusCode;
	protected String result;
	
	public MeleeHttpResponse(int statusCode, String result) {
		this.statusCode = statusCode;
		this.result = result;
	}

	public int getStatusCode() {
		return this.statusCode;
	}

	public String getResult() {
		return this.result;
	}
	
	public static MeleeHttpResponse getSuccessResponse(Map<String, Object> data) {
		Map<String, Object> r = new HashMap<String, Object>();
		Map<String, Object> meta = new HashMap<String, Object>();
		meta.put("code", 200);
		meta.put("message", "ok");
		r.put("meta", meta);
		if (data != null) {
			r.put("data", data);
		}
		return new MeleeHttpResponse(200, JsonUtils.toJsonString(r));
	}
	
}
