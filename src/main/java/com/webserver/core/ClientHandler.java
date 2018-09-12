package com.webserver.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.webserver.http.HttpResponse;
import com.webserver.http.EmptyRequestException;
import com.webserver.http.HttpContext;
import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;
import com.webserver.servlet.HttpServlet;
import com.webserver.servlet.LoginServlet;
import com.webserver.servlet.RegServlet;

/**
 * 处理客户端请求
 * @author xiangyang
 *
 */
public class ClientHandler implements Runnable {
	private Socket socket;
	
	public ClientHandler(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		try {
			/**
			 * 主流程：
			 * 1.解析请求
			 * 2.处理请求
			 * 3.发送响应
			 */
			
			// 1.解析请求
			HttpRequest httpRequest = new HttpRequest(socket);
			
			// 2.响应请求
			HttpResponse response = new HttpResponse(socket);
			String url = httpRequest.getRequestUrl();
			System.out.println("url: " + url);
			String servletName = ServerContext.getMapping(url);
			System.out.println("ServletName: " + servletName);
			
			/**
			 * 利用反射获取对应的Servlet,并执行它的service方法
			 */
			if(servletName != null) {
				Class cla = Class.forName(servletName);
				System.out.println("正在加载class：" + servletName);
				HttpServlet servlet = (HttpServlet) cla.newInstance();
				servlet.service(httpRequest, response);
			} else {
				File file = new File("webapp" + url);
				System.out.println("PATH: " + file.getPath());
				if(file.exists()) {
					response.setFile(file);
					System.out.println("该资源已找到");
	 			} else {
					response.setFile(new File("webapp/root/404.html"));
					response.setStatusCode(404);
					System.out.println("该资源未找到");
				}
			}
			
			// 响应客户端
			response.flush();
		} catch (EmptyRequestException e) {
			System.out.println("请求为空");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	
}
