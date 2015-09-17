package com.melee.meleejava.web;

import java.io.File;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.melee.meleejava.rds.RDSPool;
import com.melee.meleejava.utils.Config;
import com.melee.meleejava.utils.MeleeLogger;
import com.melee.meleejava.utils.Utils;

public abstract class BaseListener implements ServletContextListener {

	private static final MeleeLogger logger = Config.logger;
	private static final String WEB_ROOT_PATH = "WEB_ROOT_PATH";
	
	@Override
	public void contextDestroyed(ServletContextEvent ctx) {
		Config.getInstance().clear();
		
		//调用自定义销毁
		destroyed(ctx);
		
		logger.info("listener", "melee server stoped");
	}

	@Override
	public void contextInitialized(ServletContextEvent ctx) {
		logger.info("listener", "melee server starting ...");
		// 保存上下文路径
		Config.getInstance().put(WEB_ROOT_PATH, ctx.getServletContext().getRealPath("/"));
		// 首先初始化配置文件
		Map<String, Object> m = null;
		try {
			m = Utils.getYamlObj(Utils.getFileContent(ctx.getServletContext().getRealPath("/") + "WEB-INF" + File.separator + "config.yaml"));
			Config.getInstance().init(m);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		// 初始化关系数据库
		RDSPool.init();
		
		//调用自定义初始化
		initialized(ctx);
		
		logger.info("listener", "melee server started");
	}
	
	
	public abstract void destroyed(ServletContextEvent ctx);
	public abstract void initialized(ServletContextEvent ctx);
	
}
