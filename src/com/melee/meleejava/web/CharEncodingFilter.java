package com.melee.meleejava.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import com.melee.meleejava.utils.Config;
import com.melee.meleejava.utils.MeleeLogger;

@WebFilter(filterName="CharEncodingFilter", urlPatterns="*")
public class CharEncodingFilter implements Filter {

	private MeleeLogger logger = Config.logger;
	protected String encoding = "utf-8";

	public void destroy() {
		this.encoding = null;
	}

	/**
	 * Select and set (if specified) the character encoding to be used to
	 * interpret request parameters for this request.
	 * 
	 * @param request
	 *            The servlet request we are processing
	 * @param result
	 *            The servlet response we are creating
	 * @param chain
	 *            The filter chain we are processing
	 * 
	 * @exception IOException
	 *                if an input/output error occurs
	 * @exception ServletException
	 *                if a servlet error occurs
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		// Conditionally select and set the character encoding to be used
		request.setCharacterEncoding(encoding);
		logger.debug("filter", "character encoding filter", request.getServerName());
		// Pass control on to the next filter
		chain.doFilter(request, response);
	}

	public void init(FilterConfig filterConfig) throws ServletException {
	}

}

