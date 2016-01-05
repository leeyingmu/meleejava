package com.melee.meleejava.web.exceptions;

import java.util.HashMap;
import java.util.Map;

import com.melee.meleejava.utils.JsonUtils;
import com.melee.meleejava.web.MeleeHttpResponse;

public class MeleeHTTPException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	private int code;
	private String message;
	private String description;
	
	public MeleeHTTPException(int code) {
		this.code = code;
		this.message = this.getClass().getSimpleName();
		this.description = null;
	}
	
	public MeleeHTTPException(int code, String description) {
		this.code = code;
		this.message = this.getClass().getSimpleName();
		this.description = description;
	}
	
	public MeleeHttpResponse getResponse() {
		Map<String, Object> r = new HashMap<String, Object>();
		Map<String, Object> meta = new HashMap<String, Object>();
		meta.put("code", this.code);
		meta.put("message", this.message);
		if (this.description != null) {
			meta.put("description", this.description);
		}
		r.put("meta", meta);
		return new MeleeHttpResponse(200, JsonUtils.toJsonString(r));
	}
	
}
