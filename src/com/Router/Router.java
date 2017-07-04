package com.Router;


import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.Router.annotation.Path;
import com.Router.exception.RouterConfigException;
import com.controller.Controller;

public class Router {
	public static final String CONTROLLER_PACKAGE = "com.controller";
	private HttpServletRequest request;
	private HttpServletResponse response;
	private String url;
	private String method;
	private String path ;
	private List<Route> routes = new ArrayList<Route>();
	String route;
	
	public Router(HttpServletRequest request, HttpServletResponse response){
		this.request = request;
		this.response = response;
		this.method = request.getMethod();
		this.url = request.getPathInfo();
		url = url.replaceAll("^/", "").replaceAll("/$", "");
		path = request.getPathInfo();
	
	}
	
	public boolean get(String route,String url){
		
		route = route.replaceAll("^/", "").replaceAll("/$", "");
		String[] routeTab =route.split("/");
		String[] urlTab = url.split("/");
		this.route = route;
		System.out.println("\nURL: " + url + "; LONGUEUR: " + urlTab.length);
		System.out.println("Route: " + route + "; LONGUEUR: " + routeTab.length + "\n");
		
		//matcheRoute(route);
		
		if(route.equals(url)){
			System.out.println("Route égales");
			return true;
		}
		
		if(routeTab.length != urlTab.length){
			return false;
		}
		
		for(int i = 0; i < routeTab.length; i++){
			if(routeTab[i].startsWith(":") && urlTab[i].trim().equals("")){
				return false;
			}else if(!routeTab[i].startsWith(":") && !urlTab[i].trim().equals(routeTab[i])){
				return false;
			}
		}
		System.out.println("ROUTE RETENUE: " + route);
		return true;
	}
	
	public void matcheRoute() throws RouterException{
		String[] routeTab = route.split("/");
		for(String s: routeTab){
			if(s.substring(1, s.length()).contains(":")){
				throw new RouterException("Mauvais format de route");
			}
		}
	}
	
	public Boolean hasParams(){
		String[] routeTab = route.split("/");
		for(String s: routeTab){
			if(s.charAt(0) == ':'){
				return true;
			}
		}
		return false;
	}
	
	public List<String> params(){
		List<String> params = new ArrayList<String>();
		String[] routeTab = route.split("/");
		for(String s: routeTab){
			if(s.charAt(0) == ':'){
				params.add(s.substring(1, s.length()));
			}
		}
		return params;
	}
	
	public Map<String, String> ParamsValue(){
		Map<String, String> values = new HashMap<String, String>();
		for(String param:params()){
			values.put(param, getParams(param));
			System.out.println("\n" + param + ": " + getParams(param));
		}
		return values;
	}
	
	Object[] Values(){
		
		String[] value = new String[params().size()];
		int i = 0;
		for(String param:params()){
			value[i] =  getParams(param);
			System.out.println("::" + value[i]);
			i++;
		}
		return value;
		
	}
	
	
	public String getParams(String name){
		String[] routeTab = route.split("/");
		String[] urlTab = getMethodUrl().split("/");
		int position = -1;
		int iterator = 0;
		for(String s: routeTab){
			if(s.startsWith(":") && s.substring(1, s.length()).equals(name)){
				position = iterator;
			}
			iterator++;
		}
		if(position > -1 && urlTab.length >= position){
			return urlTab[position];
		}
		
		return null;
	}

	public void execute() throws ServletException, IOException {
		try {
			System.out.println("PACKAGE CONTROLLER: " + RouterUtil.getControllerPackages()[0]);
		} catch (RouterConfigException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Controller controller = getController();
		Class<?> controllerClass = getControllerClass();
		
		String MethodPath = url.split("/")[1];
		Method methods[] = controllerClass.getDeclaredMethods();
		for(Method method: methods){
			
			Path path = method.getAnnotation( Path.class);
			if(path != null){
				if(get(path.value(), getMethodUrl())){
					System.out.println();
					System.out.println("Méthode Retenue: " + method.getName());
					System.out.println("Route retenue: " + path.value());
					System.out.println("URL reçue: " + getMethodUrl());
					if(params().size() > 0){
						ParamsValue();
					}
					System.out.println();
					try {
						callRemote(controller, method.getName(), Values());
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					/*Method m;
					try {
						//m = controllerClass.getMethod(method.getName(), Object....getClass());
						method.invoke(controller, Values());
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						System.out.println("MAUVAIS ARGUMENT");
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
					
					
				}
				
			}
		}
	}

	private String getMethodUrl() {
		String[] urlTab = url.split("/");
		String methodUrl = "";
		if(urlTab.length > 1){
			for(int i = 1; i < urlTab.length; i++){
				methodUrl += urlTab[i] + "/";
			}
			return methodUrl.replaceAll("/$", "");
		}
		return null;
	}

	private Class<?> getControllerClass(){
		String controllerPath = url.split("/")[0];
		System.out.println("Controller path: " + controllerPath);
		try {
			return  Class.forName("com.controller." + controllerPath + "Controller");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	private Controller getController() {
		Controller controller = null;
		try {
			Class<?> controllerClass = getControllerClass();
			controllerClass.getConstructor();
			controller = (Controller) controllerClass.newInstance();
			
			
			controller.setRequest(request);
			controller.setResponse(response);
			
			
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return controller;
	}
	Object callRemote(Object instance, String sMethod, Object... arguments) throws Exception {
	    Class<?>[] argumentTypes = createArgumentTypes(arguments);
	    Method method = instance.getClass().getMethod(sMethod, argumentTypes );
	    Object[] argumentsWithSession = createArguments(arguments);
	    return method.invoke(instance, argumentsWithSession);
	}

	Object[] createArguments(Object[] arguments) {
	    Object[] args = new Object[arguments.length];
	    System.arraycopy(arguments, 0, args, 0, arguments.length);
	    return args;
	}

	Class<?>[] createArgumentTypes(Object[] arguments) {
	    Class[] types = new Class[arguments.length];
	    for (int i = 0; i < arguments.length; i++) {
	        types[i] = arguments[i].getClass();
	    }
	    return types;
	}
	
}
