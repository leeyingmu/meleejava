package com.melee.meleejava.demo;

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
	}
	
}
