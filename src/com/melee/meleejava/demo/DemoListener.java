package com.melee.meleejava.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContextEvent;

import com.melee.meleejava.utils.Config;
import com.melee.meleejava.utils.MeleeLogger;
import com.melee.meleejava.web.BaseListener;


public class DemoListener extends BaseListener {
 
	private MeleeLogger logger = Config.logger;
	
	@Override
	public void destroyed(ServletContextEvent ctx) {
		logger.info("in demo listener destroyed");
	}

	@Override
	public void initialized(ServletContextEvent ctx) {
		logger.info("in demo listener initialized");
		List<Map<String, String>> users = Config.getInstance().getDefault(new ArrayList<Map<String, String>>(), "users");
		Map<String, Map<String, String>> musers = new HashMap<String, Map<String, String>>();
		for (Map<String, String> u: users) {
			musers.put(u.get("uid"), u);
		}
	}
}
