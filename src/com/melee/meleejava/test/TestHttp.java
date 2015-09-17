package com.melee.meleejava.test;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;

import com.melee.meleejava.utils.JsonUtils;
import com.melee.meleejava.utils.Utils;

import junit.framework.TestCase;

public class TestHttp extends TestCase {

	private String sig_kv = "1";
	private String sig_key = "meleedefaulttestsignaturekeys123";
	
	public void testNoContent() throws Exception {
		String url = "http://127.0.0.1:8080/meleejava/demo?a=1&b=2";
		Map<String, String> r = JsonUtils.getJsonObj(Utils.get(url));
		this.assertEquals("1", r.get("a"));
		this.assertEquals("2", r.get("b"));
	}
	
	public void testWithContent() throws NoSuchAlgorithmException, Exception {
		String url = "http://127.0.0.1:8080/meleejava/demo";
		Map<String, Integer> content = new HashMap<String, Integer>();
		content.put("a", 1);
		content.put("b", 2);
		String contentStr = JsonUtils.toJsonString(content);
		long timestamp = System.currentTimeMillis();
		String signature = Utils.hmac(getMac(), sig_key, contentStr+timestamp);
		String params = "content="+Utils.urlEncode(contentStr)+"&timestamp=" + timestamp + "&sig_kv=1" + "&signature=" + signature;
		
		Map<String, String> r = JsonUtils.getJsonObj(Utils.postForm(url, params));
		Map<String, Long> data = JsonUtils.getJsonObj(r, "data");
		this.assertEquals(1, data.get("a").intValue());
		this.assertEquals(2, data.get("b").intValue());
	}
	
	public Mac getMac() throws NoSuchAlgorithmException {
		return Mac.getInstance("HMACSHA256");
	}
	
}
