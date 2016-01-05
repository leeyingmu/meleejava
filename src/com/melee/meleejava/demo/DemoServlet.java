package com.melee.meleejava.demo;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.melee.meleejava.utils.JsonUtils;
import com.melee.meleejava.web.BaseServlet;
import com.melee.meleejava.web.MeleeHttpResponse;
import com.melee.meleejava.web.exceptions.MeleeHTTPException;

public class DemoServlet extends BaseServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected Map<String, Object> process(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> content)
			throws MeleeHTTPException, Exception {
		/**
		 * demo servlet, return the input parameters
		 */
		if (content == null) {
			//如果不希望meleejava框架将返回信息写回response输出流，则自己处理，并返回null即可。
			this.sendResponse(resp, new MeleeHttpResponse(200, JsonUtils.toJsonString(getParameterMap(req))));
			return null;
		}
		
		/**
		 * 返回非null的字典对象，meleejava框架会自动作为200 ok返回值中的data字段返回给客户端，如：
		 * {
		 *   'meta': {'code': 200, 'message': 'ok'},
		 *   'data': content
		 * }
		 */
		return content;
	}

}
