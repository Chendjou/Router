package com.urix.router;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.urix.router.exception.RouteParamsException;

public class Route {
	public static final String ROUTE_METHOD_POST 	= "POST";
	public static final String ROUTE_METHOD_GET 	= "GET";
	private String path;
	private Method method;
	private String name;
	private String httpMethod;
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path.toLowerCase().replaceAll("^/", "").replaceAll("/$", "");
	}
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getHttpMethod() {
		return httpMethod;
	}
	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}
	
	public Route(){}
	
	public boolean matche(String url){
		url = url.toLowerCase().replaceAll("^/", "").replaceAll("/$", "");
		String route = this.path;
		String[] routeTab =route.split("/");
		String[] urlTab = url.split("/");
		
		if(route.equals(url)){
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
	
	public Boolean hasParams(){
		String[] routeTab = path.split("/");
		for(String s: routeTab){
			if(s.charAt(0) == ':'){
				return true;
			}
		}
		return false;
	}
	
	public List<String> paramsName(){
		List<String> params = new ArrayList<String>();
		if(path.length() > 1){
			String[] routeTab = path.split("/");
			for(String s: routeTab){
				if(s.charAt(0) == ':'){
					params.add(s.substring(1, s.length()));
				}
			}
		}
		return params;
	}
	
	public Map<String, String> ParamsValue(String url){
		Map<String, String> values = new HashMap<String, String>();
		for(String param:paramsName()){
			values.put(param, getParams(param, url));
		}
		return values;
	}
	
	Object[] Values(String url){
		
		String[] value = new String[paramsName().size()];
		int i = 0;
		for(String param:paramsName()){
			value[i] =  getParams(param, url);
			i++;
		}
		return value;
	}
	
	Object[] Values(String url, Method method) throws RouterException{
		String[] stringValue = (String[]) Values(url);
		Object[] values = new Object[stringValue.length];
		
		for(int i = 0; i< values.length; i++){
			Class<?> type = method.getParameterTypes()[i];
				try {
					values[i] = castStringToNumber(stringValue[i], type);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RouteParamsException(e.getMessage()  + "\n\n" +
							"Le paramètre " + stringValue[i] + " l'url n'a pas pu etre convertir en " + type.getName());
				}
			}
		return values;
	}
	
	public String getParams(String name, String url){
		String[] routeTab = path.split("/");
		String[] urlTab = url.split("/");
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

	public Object castStringToNumber(String string, Class<?> type) throws Exception{
		Object value = null;
		if(type.equals(Character.class)){
			return string.charAt(0);
		}
		if(type.equals(String.class)){
			return string;
		}
		Map<Class<?>, Class<?>> numbersType = new HashMap<Class<?>,Class<?>>();
		numbersType.put(int.class, Integer.class);
		numbersType.put(double.class,Double.class);
		numbersType.put(float.class,Float.class);
		numbersType.put(long.class,Long.class);
				
		if(numbersType.containsKey(type)){
			throw new RouteParamsException("Le type primitif ne sont supportés en paramètre des méthode d'un controller");
		}
		if(numbersType.containsValue(type)){
			Method m;
			try {
				m = type.getMethod("valueOf", String.class);
				value = m.invoke(m, string);
				return value;
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				
				e.printStackTrace();
			}
		}
		throw new Exception("La chaine " + string + " n'a pas pu etre convertir en " + type.getName());
	}
}
