package com.urix.servlet;

import com.urix.authorization.IAuthorization;

public class UserAuthorization implements IAuthorization{

	@Override
	public boolean isAuthenticate() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public int getRole() {
		return 1;
	}

	@Override
	public String getRequestAuthMessage() {
		return "Vous n'avez pas les accrédiation suffisante pour accéder à cette page";
	}

	@Override
	public String getViolationRoleMessage() {
		return "Veuillez vous connectez pour accéder à cette page";
	}

}
