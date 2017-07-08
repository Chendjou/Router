package com.urix.router;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.urix.router.exception.RouterConfigException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class RouterUtil {
	static String root = System.getProperty("user.dir");
	
	public static final String DEFAULT_ROUTER_CONFIG_PATH = root + "\router.config.xml";
	public static final String ROUTER_CONFIG_ROOT_ELEMENT = "router";
	public static final String ROUTER_CONFIG_CHILD_ELEMENT = "controller-package";
	

	public static String[] getControllerPackages(String path) throws RouterConfigException{
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final Document document = builder.parse(new File(path));
			final Element root = document.getDocumentElement();
			if(!root.getTagName().equals(ROUTER_CONFIG_ROOT_ELEMENT)){
				throw new RouterConfigException("L'élement racine \"router\" n'a pas été trouvé dans le fichier de configuration");
			}
			final NodeList ControllerpackagesNodeList = document.getElementsByTagName(ROUTER_CONFIG_CHILD_ELEMENT);
			int length = ControllerpackagesNodeList.getLength();
			String[] controllerPackages = new String[length];
			
			for(int i = 0; i < length; i++){
				Element controllerPackage = (Element) ControllerpackagesNodeList.item(i);
				controllerPackages[i] = controllerPackage.getTextContent();
			}
			return controllerPackages;
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static String[] getControllerPackages() throws RouterConfigException{
		return getControllerPackages(DEFAULT_ROUTER_CONFIG_PATH);
	}
	
	/**
	 * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
	 *
	 * @param packageName The base package
	 * @return The classes
	 * @throws RouterException 
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static Class<?>[] getClasses(String packageName) throws RouterException{
	    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	    assert classLoader != null;
	    String path = packageName.replace('.', '/');
	    Enumeration<URL> resources;
		try {
			resources = classLoader.getResources(path);
		} catch (IOException e) {
			throw new RouterConfigException("Le class Loader n'a pas pu etre chargé");
		}
	    List<File> dirs = new ArrayList<File>();
	    while (resources.hasMoreElements()) {
	        URL resource = resources.nextElement();
	        dirs.add(new File(resource.getFile()));
	    }
	    ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
	    for (File directory : dirs) {
	        try {
				classes.addAll(findClasses(directory, packageName));
			} catch (ClassNotFoundException e) {
				throw new RouterConfigException("Une érreur s'est produite lors de la recherche des classes controlleur");
			}
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
	
	private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
	    List<Class<?>> classes = new ArrayList<Class<?>>();
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
}
