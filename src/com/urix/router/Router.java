package com.urix.router;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.urix.authorization.Authorization;
import com.urix.authorization.AuthorizationException;
import com.urix.authorization.IAuthorization;
import com.urix.controller.Controller;
import com.urix.router.annotation.HttpController;
import com.urix.router.annotation.Path;

public class Router {
	private HttpServletRequest request;
	private HttpServletResponse response;
	private String url;
	private String ControllerUrl;
	private Class<?> controllerClass;
	private String methodUrl;
	private String httpMethod;
	private Route route;
	private Authorization authorization;

	List<Class<?>> controllerClassList = new ArrayList<Class<?>>();
	private Map<Class<?>, ArrayList<Route>> routes = new HashMap<Class<?>, ArrayList<Route>>();
	private static Router instance = null;
	
	public Router(HttpServletRequest request, HttpServletResponse response) throws RouterException{
		this.request = request;
		this.response = response;
		this.authorization = new Authorization(null);
	
		updateControllerList();
		updateRoutesList();
	}
	
	

	public void refresh() throws RouterException{
		this.httpMethod = request.getMethod();
		this.url = request.getPathInfo();
		url = url.replaceAll("^/", "").replaceAll("/$", "");
		request.getPathInfo();
		setControllerUrl();
		setRouteAndUrlMethod();
		
	}
	
	public static Router getInstance(HttpServletRequest request, HttpServletResponse response, IAuthorization authorizationImpl) throws RouterException{
		
		if(instance == null){
			instance = new Router(request, response);
			System.out.println("NOUVELLE INSTANCIATION");
		}
		instance.refresh();
		if(instance.authorization.getAuth() == null){
			instance.authorization.setAuth(authorizationImpl);
		}
		return instance;
	}


	public void execute() throws RouterException, AuthorizationException{
		Controller controller = getController();
		authorization.authorize(controllerClass);
		
		
			if(route.paramsName().size() > 0){
				route.ParamsValue(methodUrl);
			}
			
			try {
				System.out.println("METHODE: " + route.getMethod());
				authorization.authorize(route.getMethod());
				callRemote(controller, route.getMethod().getName(), route.Values(methodUrl, route.getMethod()));
			
			} catch (Exception e) {
				
				e.printStackTrace();
				throw new RouterException(e.fillInStackTrace()+ "\n\n" + e.getMessage());
			}
		}
	
	private Controller getController() {
		Controller controller = null;
		try {
			controllerClass.getConstructor();
			controller = (Controller) controllerClass.newInstance();
			
			
			controller.setRequest(request);
			controller.setResponse(response);
			controller.init();
			
			
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return controller;
	}
	Object callRemote(Object instance, String sMethod, Object... arguments) throws Exception {
	    Class<?>[] argumentTypes = createArgumentTypes(arguments);
	    Method method = instance.getClass().getMethod(sMethod, argumentTypes);
	    Object[] argumentsWithSession = createArguments(arguments);
	    return method.invoke(instance, argumentsWithSession);
	}

	Object[] createArguments(Object[] arguments) {
	    Object[] args = new Object[arguments.length];
	    System.arraycopy(arguments, 0, args, 0, arguments.length);
	    return args;
	}

	Class<?>[] createArgumentTypes(Object[] arguments) {
	    Class<?>[] types = new Class[arguments.length];
	    for (int i = 0; i < arguments.length; i++) {
	        types[i] = arguments[i].getClass();
	    }
	    return types;
	}
	
	public void setControllerUrl() throws RouterException{
		boolean getted = false;
		String controllerRoute = null;
		for(Class<?> controller: controllerClassList){
			HttpController httpController = controller.getAnnotation(HttpController.class);
			controllerRoute = httpController.route().replaceAll("^/", "").toLowerCase();
			if(url.toLowerCase().startsWith(controllerRoute)){
				this.ControllerUrl = controllerRoute;
				this.controllerClass = controller;
				getted = true;
			}
		}
		if(!getted){
			response.setStatus(404);
			throw new RouterException("Aucun controller n'a été trouvé pour url entrée\"" + controllerRoute + "\"");
		}
	}
	
	public void setRouteAndUrlMethod() throws RouterException{
		boolean getted = false;
		String restUrl = url.substring(ControllerUrl.length()).toLowerCase().replaceAll("^/", "").replaceAll("/$", "");
		for(Route route: routes.get(controllerClass)){
			if(route.matche(restUrl) && route.getHttpMethod().equals(httpMethod)){
				this.methodUrl = restUrl;
				this.route = route;
				getted = true;
				break;
			}
		}
		if(!getted){
			response.setStatus(404);

			throw new RouterException("Aucune route correspondante à "
					+ "url entrée:\"" +restUrl + "\" n'a été trouvé dans le controller " + controllerClass.getName());
		}
	}
	
	
	private void updateControllerList() throws RouterException{
		String src = request.getServletContext().getRealPath("/WEB-INF/router.config.xml");
		String[] controllerPackages = null;
		
		controllerPackages = RouterUtil.getControllerPackages(src);
		
		for(String packageName: controllerPackages){
			Class<?> classes[] = null;
			classes = RouterUtil.getClasses(packageName);
			for(Class<?> c : classes){
				HttpController httpController = c.getAnnotation(HttpController.class);
				if(httpController != null){
					if(!c.getSuperclass().equals(Controller.class)){
						throw new RouterException("Le controlleur " + c.getName() + " doit hérité de " + Controller.class);
					}
					checkifControllerNameExist(c);
					controllerClassList.add(c);
				}
			}
		}
	}
	
	private void updateRoutesList() throws RouterException{
		ArrayList<Route> controllerRoutes = new ArrayList<Route>();
		Route route = null;
		for(Class<?> controller: controllerClassList){
			routes.put(controller, controllerRoutes);
			for(Method method: controller.getDeclaredMethods()){
				Path path = method.getAnnotation(Path.class);
				if(path != null){
					route = new Route();
					checkifMethodNameExist(path.name(), controller);
					route.setMethod(method);
					route.setPath(path.route().toLowerCase());
					route.setHttpMethod(path.httpMethod());
					if(path.name().equals("default")){
						route.setName(null);
					}else{
						route.setName(path.name());
					}

					if(route.paramsName().size() != route.getMethod().getParameterCount()){
						throw new RouterException("La méthode " + route.getMethod() + "n'a pas le nombre de paramètres passés dans l'url");
					}
					//System.out.println("\n\t" + route.getPath() + "(" + route.getHttpMethod() + ")");
					//System.out.println("\t" + route.getMethod());
					controllerRoutes.add(route);
				}
			}
		}
	}
	
	public String generateUrl(String controllerName, String routeName, Object... parameters) throws RouterException{
		String url = null;
		Class<?> controllerClass = null;
		Route urlRoute = null;
		for(Class<?> c: controllerClassList){
			HttpController httpController = c.getAnnotation(HttpController.class);
			if(httpController.name().equals(controllerName)){
				url = httpController.route();
				if(!url.startsWith("/")){
					url = "/" + url;
				}
				controllerClass = c;
			}
			
		}
		if(url == null){
			throw new RouterException("Le controlleur de nom " + controllerName + " n'existe pas");
		}
		
		for(Route route: routes.get(controllerClass)){
			if(route.getName() != null && route.getName().equals(routeName)){
				urlRoute = route;
				url += "/"+route.getPath();
			}
		}
		
		if(urlRoute == null){
			throw new RouterException("La route de nom " + routeName + " n'existe pas dans le controlleur " + controllerClass.getName());
		}
		
		if(urlRoute.paramsName().size() != parameters.length){
			throw new RouterException("La route de nom " + routeName + " ne compte pas " + parameters.length + " paramètres " +
		"mais plutot " + route.paramsName().size()); 
		}
		
		for(int i = 0; i < parameters.length; i++){
			url = url.replaceAll(":" + urlRoute.paramsName().get(i), parameters[i].toString());
		}
		return url;
	}
	
	public void checkifControllerNameExist(Class<?> controllerClass) throws RouterException{
		HttpController httpController = controllerClass.getAnnotation(HttpController.class);
		String name = httpController.name();
		for(Class<?> c:controllerClassList){
			httpController = c.getAnnotation(HttpController.class);
			if(httpController.name().equalsIgnoreCase(name)){
				throw new RouterException("Les controlleurs '" + c + "' et '" + controllerClass + "' portent le meme nom.");
			}
		}
	}
	
	public void checkifMethodNameExist(String name, Class<?> controllerClass) throws RouterException{
		for(Route route :routes.get(controllerClass)){
			if(route.getName() != null && route.getName().equalsIgnoreCase(name)){
				throw new RouterException("Deux routes du controlleur '" + controllerClass + "' portent le meme nom: '" + route.getName() +"'");
			}
		}
	}
}
