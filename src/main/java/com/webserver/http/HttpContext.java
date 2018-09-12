package com.webserver.http;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class HttpContext {
	private static Map<Integer, String> STATUS_CODE_REASON_MAPPING = new HashMap<Integer, String>();
	private static Map<String, String> MIME_MAPPING = new HashMap<String, String>();
	
	static {
		initStatusMapping();
		initMimeMapping();
	}
	
	/**
	 * 初始化状态代码与描述的映射
	 */
	private static void initStatusMapping() {
		STATUS_CODE_REASON_MAPPING.put(200, "OK");
		STATUS_CODE_REASON_MAPPING.put(201, "Created");
		STATUS_CODE_REASON_MAPPING.put(202, "Accepted");
		STATUS_CODE_REASON_MAPPING.put(204, "No Content");
		STATUS_CODE_REASON_MAPPING.put(301, "Moved Permanently");
		STATUS_CODE_REASON_MAPPING.put(302, "Moved Temporarily");
		STATUS_CODE_REASON_MAPPING.put(304, "Not Modified");
		STATUS_CODE_REASON_MAPPING.put(400, "Bad Request");
		STATUS_CODE_REASON_MAPPING.put(401, "Unauthorized");
		STATUS_CODE_REASON_MAPPING.put(403, "Forbidden");
		STATUS_CODE_REASON_MAPPING.put(404, "Not Found");
		STATUS_CODE_REASON_MAPPING.put(500, "Internal Server Error");
		STATUS_CODE_REASON_MAPPING.put(501, "Not Implemented");
		STATUS_CODE_REASON_MAPPING.put(502, "Bad Gateway");
		STATUS_CODE_REASON_MAPPING.put(503, "Service Unavailable");
	}
	
	/**
	 * 初始化介质类型映射
	 */
	private static void initMimeMapping() {
//		MIME_MAPPING.put("html", "text/html");
//		MIME_MAPPING.put("png", "image/png");
//		MIME_MAPPING.put("gif", "image/gif");
//		MIME_MAPPING.put("jpg", "image/jpeg");
//		MIME_MAPPING.put("css", "text/css");
//		MIME_MAPPING.put("js", "application/javascript");
		
		/**
		 * 将获取mime-type改为从xml配置文件获取，解析xml文件
		 */
		try {
			SAXReader reader = new SAXReader();
			Document doc = reader.read(new File("config/web.xml"));
			Element root = doc.getRootElement();
			List<Element> list = root.elements("mime-mapping");
			for(Element element : list) {
				String key = element.elementText("extension");
				String value = element.elementText("mime-type");
				MIME_MAPPING.put(key, value);
			}
			System.out.println("MIME_MAPPING大小为：" + MIME_MAPPING.size());
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据code获取描述详情
	 * @param code
	 * @return
	 */
	public static String getStatusReason(int code) {
		return STATUS_CODE_REASON_MAPPING.get(code);
	}
	
	/**
	 * 根据给定的后缀名获取只当的介质类型
	 * @param ext
	 * @return
	 */
	public static String getMimeType(String ext) {
		return MIME_MAPPING.get(ext);
	}
	
	public static void main(String[] args) {
		initMimeMapping();
	}
}
