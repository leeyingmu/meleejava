package com.melee.meleejava.web;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.melee.meleejava.utils.Config;
import com.melee.meleejava.utils.JsonUtils;
import com.melee.meleejava.utils.MeleeLogger;
import com.melee.meleejava.utils.Utils;
import com.melee.meleejava.web.exceptions.BadRequest;
import com.melee.meleejava.web.exceptions.MeleeHTTPException;
import com.melee.meleejava.web.exceptions.ServerError;
import com.melee.meleejava.web.exceptions.SignatureError;

public abstract class BaseServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	protected static final MeleeLogger logger = Config.logger;
	
	private ThreadLocal<Mac> mac = new ThreadLocal<Mac>();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		long start = System.currentTimeMillis();
		resp.setHeader("Cache-Control", "max-age=0");
		resp.setCharacterEncoding("utf-8");
		resp.setContentType("text/json;charset=UTF-8");
		
		Map<String, String> parameterMap = null;
		Map<String, Object> content = null; //request data
		Map<String, Object> data = null;     //response data
		MeleeHttpResponse r = null;
		try {
			parameterMap = getParameterMap(req);
			logger.debug("===>>>", req.getMethod(), req.getRequestURI(), parameterMap);
			
			content = getContent(req);
			data = process(req, resp, content);
			
			if (data != null) {
				r = MeleeHttpResponse.getSuccessResponse(data);
			} else {
				//说明子类已经自己处理了返回操作，不需要在此再处理。
			}
			
		} catch (MeleeHTTPException e) {
			r = e.getResponse();
		} catch (Throwable t) {
			logger.error(t);
			r = new ServerError(String.valueOf(t)).getResponse();
		} finally {
			if (r != null) {
				this.sendResponse(resp, r);
				logger.info("<<<===", req.getMethod(), req.getRequestURI(), (System.currentTimeMillis()-start), parameterMap, r.getResult());
			}
		}
	}
	
	/**
	 * 验证请求签名，并将请求的content转为字典对象
	 * @param req
	 * @return
	 * @throws MeleeHTTPException
	 * @throws Exception
	 */
	private Map<String, Object> getContent(HttpServletRequest req) throws MeleeHTTPException, Exception {
		String content = req.getParameter("content");
		if (Utils.isEmpty(content)) {
			return null;
		} 
		String signature = req.getParameter("signature");
		String sig_kv = req.getParameter("sig_kv");
		String timestamp = req.getParameter("timestamp");
		if (!Utils.all(signature, sig_kv, timestamp)) {
			throw new BadRequest("request parameters incomplate");
		}
		if (System.currentTimeMillis()-Long.parseLong(timestamp) > 5*60*1000) {
			//5分钟前的请求，正常情况下不可能出现
			logger.warn("request expired", req.getRequestURI(), content, timestamp, sig_kv, signature);
			throw new BadRequest("request expired");
		}
		String sig_key = Config.getInstance().getSigKeys().get(sig_kv);
		if (Utils.isEmpty(sig_key)) {
			throw new BadRequest("request sig_kv not found");
		}
		String signatureServer = Utils.hmac(getMac(), sig_key, content+timestamp);
		if (!signature.equalsIgnoreCase(signatureServer)) {
			logger.error("request signature error", req.getRequestURI(), content, timestamp, sig_kv, sig_key, signature, signatureServer);
			throw new SignatureError("request signature error");
		}
		
		return JsonUtils.getJsonObj(content);
	}
	
	protected Map<String, String> getParameterMap(HttpServletRequest req) {
		Enumeration<String> names = req.getParameterNames();
		Map<String, String> map = new HashMap<String, String>();
		while (names != null && names.hasMoreElements()) {
			String name = names.nextElement();
			map.put(name, req.getParameter(name));
		}
		return map;
	}
	
	/**
	 * 将返回内容写回response输出流
	 * @param resp
	 * @param r
	 */
	public void sendResponse(HttpServletResponse resp, MeleeHttpResponse r) {
		PrintWriter out = null;
		try {
			out = resp.getWriter();
			out.write(r.getResult());
			if (r.getStatusCode() != 200) {
				resp.setStatus(r.getStatusCode());
			}
		} catch (Throwable t) {
			logger.error(t);
		} finally {
			close(out);
		}
	}
	
	private void close(Closeable is ) {
		try {
			if (is != null) {
				is.close();
			}
		} catch (IOException e) {
			logger.error(e, e);
		}
	}
	
	protected Mac getMac() throws Exception {
		Mac instance = mac.get();
		if (instance == null) {
			instance = Mac.getInstance("HMACSHA256");
			mac.set(instance);
		}
		return instance;
	}
	
	protected abstract Map<String, Object> process(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> content) throws MeleeHTTPException, Exception;
	
}
