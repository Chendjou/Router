package com.urix.authorization;

import java.lang.reflect.Method;

public class Authorization {

	private IAuthorization authorizationImpl;

	public IAuthorization getAuth() {
		return authorizationImpl;
	}

	public void setAuth(IAuthorization auth) {
		this.authorizationImpl = auth;
	}
	
	public Authorization(IAuthorization auth){
		this.authorizationImpl = auth;
	}
	
	public void authorize(Class<?> controllerClass) throws AuthorizationException{
		Protected protec = controllerClass.getAnnotation(Protected.class);
		if(protec != null){
			if(!authorizationImpl.isAuthenticate()){
				throw new AuthorizationException(authorizationImpl.getRequestAuthMessage());
			}
			int[] roles = protec.role();
			boolean hasRole = false;
			
			for(int role :roles){
				if(role == authorizationImpl.getRole()){
					hasRole = true;
				}
			}
			
			if(!hasRole){
				throw new AuthorizationException(authorizationImpl.getViolationRoleMessage());
			}
		}
	}
	
	public void authorize(Method method) throws AuthorizationException{
		Protected protec = method.getAnnotation(Protected.class);
		if(protec != null){
			if(!authorizationImpl.isAuthenticate()){
				throw new AuthorizationException(authorizationImpl.getRequestAuthMessage());
			}
			int[] roles = protec.role();
			boolean hasRole = false;
			
			for(int role:roles){
				if(role == authorizationImpl.getRole()){
					hasRole = true;
				}
			}
			
			if(!hasRole){
				throw new AuthorizationException(authorizationImpl.getViolationRoleMessage());
			}
		}
		
	}
	
}
