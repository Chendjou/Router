package com.Router;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.Router.exception.RouterConfigException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class RouterUtil {
	public static final String DEFAULT_ROUTER_CONFIG_PATH = "/WEB-INF/router.config.xml";
	public static final String ROUTER_CONFIG_ROOT_ELEMENT = "router";
	public static final String ROUTER_CONFIG_CHILD_ELEMENT = "controller-package";
	

	public static String[] getControllerPackages(String path) throws RouterConfigException{
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final Document document = builder.parse(new File(path));
			final Element root = document.getDocumentElement();
			if(root.getTagName().equals(ROUTER_CONFIG_ROOT_ELEMENT)){
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
}
