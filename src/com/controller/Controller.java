package com.controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Controller {
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
		System.out.println("METHODE DE RENDU APPELE SUR " + url);
		request.getServletContext().getRequestDispatcher(url).forward(request, response);
		System.out.println("MAIS PAS DE RENDu");
	}
	
	
	
	
}
