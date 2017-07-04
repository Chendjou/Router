package com.Router.annotation;


public enum ROLE {
	ADMIN(2),
	USER(1);
	
	private int role;
	
	ROLE(int role){
		this.role = role;
	}
	
	public String toString(){
		return String.valueOf(role);
	}
}
