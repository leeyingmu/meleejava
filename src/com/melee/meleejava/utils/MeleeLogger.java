package com.melee.meleejava.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class MeleeLogger {
	
	private Logger logger;
	
	public MeleeLogger(Logger logger) {
		this.logger = logger;
	}
	
	public void debug(Object... msgs) { this.log(Level.DEBUG, msgs); }
	public void info(Object... msgs) { this.log(Level.INFO, msgs); }
	public void warn(Object... msgs) { this.log(Level.WARN, msgs); }
	public void error(Object... msgs) { this.log(Level.ERROR, msgs); }
	
	private void log(Level l, Object... msgs) {
		switch(l) {
			case DEBUG:
				this.logger.debug(this.formatMsgs(msgs));
				break;
			case INFO:
				this.logger.info(this.formatMsgs(msgs));
				break;
			case WARN:
				this.logger.warn(this.formatMsgs(msgs));
				break;
			case ERROR:
				this.logger.error(this.formatMsgs(msgs));
				break;
			default:
				throw new RuntimeException("not supported logger level");
		}
	}
	
	private String formatMsgs(Object... msgs) {
		if (msgs == null || msgs.length == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (Object o: msgs) {
			if (o instanceof List || o instanceof Map) {
				sb.append("|$").append(JsonUtils.toJsonString(o));
				
			} else if (o instanceof Object[]) {
				List<Object> l = new ArrayList<Object>();
				for (Object oo: (Object[]) o) {
					l.add(oo);
				}
				sb.append("|$").append(JsonUtils.toJsonString(l));
				
			} else if (o instanceof Throwable) {
				sb.append("######" + o + "\n");
				for (StackTraceElement st: ((Throwable)o).getStackTrace()) {
					sb.append("######\tat " + st + "\n");
				}
				
			} else {
				sb.append("|").append(String.valueOf(o));
				
			}
		}
		return sb.toString();
	}
	
	
	private enum Level {
		DEBUG, INFO, WARN, ERROR
	}
	
	// one logger only has one log appender
	private static final Map<String, MeleeLogger> loggers = new HashMap<String, MeleeLogger>();
	public static MeleeLogger getLogger(String name) {
		if (!loggers.containsKey(name)) {
			loggers.put(name, new MeleeLogger(Logger.getLogger(name)));
		}
		return loggers.get(name);
	}
	
}
