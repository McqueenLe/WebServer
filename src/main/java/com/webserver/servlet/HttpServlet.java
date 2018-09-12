package com.webserver.servlet;

import java.io.File;

import com.webserver.http.HttpResponse;
import com.webserver.http.HttpRequest;

/**
 * 所有servelt超类，封装了公有的service方法和跳转
 * @author xiangyang
 *
 */
public abstract class HttpServlet {
	public abstract void service(HttpRequest request, HttpResponse response) throws Exception;
	
	public void forward(String path, HttpRequest request, HttpResponse response) {
		File file = new File(path);
		response.setFile(file);
	}
	
	
}
