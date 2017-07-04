package com.controller;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.ServletException;
import com.Router.annotation.Path;

public class HomeController extends Controller{
	
	public HomeController() {
		System.out.println("Le home controller vient d'etre instancié");
	}
	
	@Path("posts")
	public void getPosts() throws IOException, ServletException{
		//render("/WEB-INF/posts.jsp");
		response.getWriter().append("Affichage de la liste des postes");
	}
	
	@Path("post/:id")
	public void getPost(String id) throws IOException{
		request.setAttribute("id_post", id);
		//render("/WEB-INF/post.jsp");
		response.getWriter().append("Affichage du post " + id);
		Class[] annotated;
		try {
			annotated = getClasses("/");
			for(Class<?> c: annotated){
				response.getWriter().append("\n " + c.getName());
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	 /**
	 * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
	 *
	 * @param packageName The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private static Class[] getClasses(String packageName) throws ClassNotFoundException, IOException {
	    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	    assert classLoader != null;
	    String path = packageName.replace('.', '/');
	    Enumeration<URL> resources = classLoader.getResources(path);
	    List<File> dirs = new ArrayList<File>();
	    while (resources.hasMoreElements()) {
	        URL resource = resources.nextElement();
	        dirs.add(new File(resource.getFile()));
	    }
	    ArrayList<Class> classes = new ArrayList<Class>();
	    for (File directory : dirs) {
	        classes.addAll(findClasses(directory, packageName));
	    }
	    return classes.toArray(new Class[classes.size()]);
	}
	
	/**
	 * Recursive method used to find all classes in a given directory and subdirs.
	 *
	 * @param directory   The base directory
	 * @param packageName The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	
	private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
	    List<Class> classes = new ArrayList<Class>();
	    if (!directory.exists()) {
	        return classes;
	    }
	    File[] files = directory.listFiles();
	    for (File file : files) {
	        if (file.isDirectory()) {
	            assert !file.getName().contains(".");
	            classes.addAll(findClasses(file, packageName + "." + file.getName()));
	        } else if (file.getName().endsWith(".class")) {
	            classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
	        }
	    }
	    return classes;
	}
	@Path("post/:id/:comment/bb/:d")
	public void getPosts(String id, String c, String f) throws IOException{
		request.setAttribute("id_post", id);
		//render("/WEB-INF/post.jsp");
		response.getWriter().append("Affichage du post " + id + " du commentaire " + c + " du " + f);
	}
	
	
	
	
}
