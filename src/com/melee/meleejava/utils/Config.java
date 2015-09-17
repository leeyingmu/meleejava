package com.melee.meleejava.utils;

import java.util.HashMap;
import java.util.Map;

public class Config extends HashMap<String, Object> {
	
	private static final long serialVersionUID = 1L;
	
	public static final MeleeLogger logger = MeleeLogger.getLogger("sys");
	
	private static Config instance = null;
	
	public static Config getInstance() {
		if (instance == null) {
			instance = new Config();
		}
		return instance;
	}
	
	
	public void init(java.util.Map<? extends String,? extends Object> m) {
		super.putAll(m);
		logger.info("melee config inited with: " + JsonUtils.toJsonString(m));
	}
	
	
	@SuppressWarnings("unchecked")
	/**
	 * 连续从配置文件中找到目标
	 * @param keys
	 * @return
	 */
	public <T> T get(String... keys) {
		if (keys == null || keys.length == 0) {
			return null;
		}
		if (keys.length == 1) {
			return (T) this.get(keys[0]);
		}
		Map<String, Object> map = (Map<String, Object>) this.get(keys[0]);
		for (int i=1; i<keys.length-1; i++) {
			map = (Map<String, Object>) map.get(keys[i]);
			if (map == null) {
				map = new HashMap<String, Object>();
				break;
			}
		}
		return (T) map.get(keys[keys.length-1]);
	}
	
	public Map<String, String> getSigKeys() {
		Map<String, String> keys = this.get("main", "request", "sigkeys");
		if (keys == null) {
			keys = new HashMap<String, String>();
		}
		return keys;
	}
	
	public Map<String, Object> getRDSPoolConfig() {
		Map<String, Object> c = this.get("main", "rds", "pool_config");
		if (c == null) {
			c = new HashMap<String, Object>();
		}
		// default configures
		if (!c.containsKey("driver")) {
			c.put("driver", "org.gjt.mm.mysql.Driver");
		}
		if (!c.containsKey("min_pool")) {
			c.put("min_pool", 2);
		}
		if (!c.containsKey("max_pool")) {
			c.put("max_pool", 10);
		}
		if (!c.containsKey("idle_timeout")) {
			c.put("idle_timeout", 3600000);
		}
		return c;
	}
	
	public Map<String, String> getRDSBinds() {
		Map<String, String> binds = this.get("main", "rds", "binds");
		if (binds == null) {
			binds = new HashMap<String, String>();
		}
		return binds;
	}
}
