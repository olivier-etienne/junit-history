package com.francetelecom.orangetv.junithistory.server.service;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.francetelecom.orangetv.junithistory.server.manager.ShowHtmlManager.HttpAddress;

public abstract class AbstractServlet extends HttpServlet implements IMyServices {

	private static final long serialVersionUID = 1L;

	private ServletContext servletContext;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	// --------------------------------------- protected methods
	protected HttpAddress getHttpAddress(HttpServletRequest request) {

		return new HttpAddress(request.getLocalPort(), request.getLocalAddr());
	}

	protected String getSessionId(HttpServletRequest request) {
		return request.getSession(true).getId();
	}

	protected HttpSession getSession(HttpServletRequest request) {
		return request.getSession(true);
	}

}
