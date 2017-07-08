package com.urix.controller;
import java.io.IOException;
import javax.servlet.ServletException;

import com.urix.authorization.Protected;
import com.urix.router.annotation.HttpController;
import com.urix.router.annotation.Path;

@HttpController(route="/home", name="home")
public class HomeController extends Controller{
	@Path(route="/")
	public void home() throws IOException{
		response.getWriter().append("Bienvenue sur mon router");
	}
	
	@Path(route="posts", name="posts")
	@Protected(role = 10)
	public void getPosts() throws IOException, ServletException{
		//render("/WEB-INF/posts.jsp");
		response.getWriter().append("Affichage de la liste des postes");
	}
	
	@Path(route="post/:id", name="post")
	public void getPost(Integer id) throws IOException, ServletException{
		request.setAttribute("id_post", id);
		String url = generateAbsoluteUrl("user", "get","1");
		url = ("\nURl générée: <a href='" + url + "'> Usercontroller</a>" );
		request.setAttribute("url", url);
		render("/WEB-INF/post.jsp");
		
	}
	
	 
	@Path(route="post/:id/:comment/bb/:d", name="post5")
	public void getPosts(String id, String c, String f) throws IOException{
		request.setAttribute("id_post", id);
		//render("/WEB-INF/post.jsp");
		response.getWriter().append("Affichage du post " + id + " du commentaire " + c + " du " + f);
	}
	@Override
	public void init() {
		System.out.println("Je viens d'etre initialisér");
		
	}
	
	
	
	
}
