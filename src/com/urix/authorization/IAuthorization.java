package com.urix.authorization;

public interface IAuthorization {
	String REQUEST_AUTHENTICATION_MESSAGE = null;
	boolean isAuthenticate();
	int getRole();
	String getRequestAuthMessage();
	String getViolationRoleMessage();
}
