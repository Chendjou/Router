package com.urix.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.urix.authorization.IAuthorization;
import com.urix.router.Router;


public class RouterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		IAuthorization auth = new UserAuthorization();
		Router router = Router.getInstance(request, response, auth);
		request.getServletContext().setAttribute("ROUTER", router);
		router.execute();
		/*String url = null;
		url = router.generateUrl("home", "posts");
		response.getWriter().append("\nURl générée: " + url);
		
		
		url = router.generateUrl("home", "post", 1);
		response.getWriter().append("\nURl générée: " + url);
		
		url = router.generateUrl("home", "pos", 1, "chie", "1");
		response.getWriter().append("\nURl générée: " + url);
		
		url = router.generateUrl("user", "pos", 1, "chie", "1");
		response.getWriter().append("\nURl générée: " + url);
		
		url = router.generateUrl("user", "get","1");
		response.getWriter().append("\nURl générée: <a href='" + url + "'> Usercontroller</a>" );
		
		*/
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

}
