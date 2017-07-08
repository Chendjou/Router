package com.urix.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.urix.router.Router;
import com.urix.router.RouterException;

public abstract class Controller {
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	
	public HttpServletRequest getRequest() {
		return request;
	}
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	public HttpServletResponse getResponse() {
		return response;
	}
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	public void render(String url) throws ServletException, IOException{
		request.getServletContext().getRequestDispatcher(url).forward(request, response);
	}
	public abstract void init();
	
	public String generateUrl(String controllerName, String routeName, Object... parameters) throws RouterException{
		Router router = (Router) request.getServletContext().getAttribute("ROUTER");
		return router.generateUrl(controllerName, routeName, parameters);
	}
	public String generateAbsoluteUrl(String controllerName, String routeName, Object... parameters) throws RouterException{
		Router router = (Router) request.getServletContext().getAttribute("ROUTER");
		String url = router.generateUrl(controllerName, routeName, parameters);
		url = request.getContextPath() + request.getServletPath() + url;
		return url;
	}
	
}
