package com.webserver.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ServerContext {
	private static Map<String, String> SERVLET_MAPPING = new HashMap<String, String>();
	
	static {
		initServletMapping();
	}
	
	/**
	 * ����servlet�����ļ�
	 */
	private static void initServletMapping() {
		try {
			SAXReader reader = new SAXReader();
			Document doc = reader.read("config/servlets.xml");
			Element root = doc.getRootElement();
			List<Element> list = root.elements();
			for(Element e : list) {
				String key = e.attributeValue("url");
				String value = e.attributeValue("className");
				SERVLET_MAPPING.put(key, value);
			}
			System.out.println("SERVLET_MAPPING: " + SERVLET_MAPPING);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ����servlet��keyֵ��ȡ��Ӧ��ʵ��Servlet
	 * @param key
	 * @return
	 */
	public static String getMapping(String key) {
		return SERVLET_MAPPING.get(key);
	}
}
