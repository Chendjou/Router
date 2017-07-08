package com.urix.controller;

import java.io.IOException;

import com.urix.authorization.Protected;
import com.urix.router.annotation.HttpController;
import com.urix.router.annotation.Path;

@HttpController(name = "user", route = "user")
public class UserController extends Controller{
	public UserController() throws IOException{
		
	}
	
	@Path(route = "/add", httpMethod="POST")
	public void add() throws IOException{
		response.getWriter().append("Page d'ajout des utilisateur");
	}
	@Path(route = "/add", httpMethod="GET")
	public void addPost() throws IOException{
		response.getWriter().append("Page d'ajout des utilisateur");
	}
	
	@Path(route = "/get/:p", name="get")
	@Protected(role={5, 9})
	public void get(String id) throws IOException{
		response.getWriter().append("Page de recherche d'un utilisateur");
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

}
