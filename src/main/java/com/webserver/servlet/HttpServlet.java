package com.webserver.servlet;

import java.io.File;

import com.webserver.http.HttpResponse;
import com.webserver.http.HttpRequest;

/**
 * ����servelt���࣬��װ�˹��е�service��������ת
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
