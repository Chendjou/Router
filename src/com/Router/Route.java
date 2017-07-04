package com.Router;

public class Route {
	private String path;
	private String type;
	
	Route(String path){
		this.path = path;
	}
	
	public void SetType(String type){
		this.type = type;
	}
	
	public String getType(){
		return type;
	}

	public boolean matche(String url) {
		
		return false;
	}


}
