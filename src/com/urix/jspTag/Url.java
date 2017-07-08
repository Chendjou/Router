package com.urix.jspTag;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class Url extends SimpleTagSupport{
	private String controller;
	private String route;
	public void setController(String controller){
		this.controller = controller;
	}
	
	public void setRoute(String route){
		this.route = route;
	}
	
	StringWriter sw = new StringWriter();
	@Override
	public void doTag() throws JspException, IOException {
			String r = (String) this.getJspContext().getAttribute("ROUTE");
			JspWriter out = getJspContext().getOut();
			out.print(controller);
			getJspBody().invoke(sw);
			getJspContext().getOut().println(sw.toString() + r.toString());
			PageContext pageContext = (PageContext) getJspContext();
			HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
	}
}
