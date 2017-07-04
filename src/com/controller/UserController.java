package com.controller;

import java.io.IOException;

import com.Router.annotation.GET;
import com.Router.annotation.Path;

@Path("/user")
public class UserController extends Controller{
	public UserController() throws IOException{
		
	}
	
	@Path("/add")
	@GET
	public void add() throws IOException{
		response.getWriter().append("Page d'ajout des utilisateur");
	}
	
	@Path("/get")
	public void get() throws IOException{
		response.getWriter().append("Page de recherche d'un utilisateur");
	}

}
