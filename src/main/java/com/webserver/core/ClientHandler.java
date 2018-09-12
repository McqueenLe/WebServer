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
 * ����ͻ�������
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
			 * �����̣�
			 * 1.��������
			 * 2.��������
			 * 3.������Ӧ
			 */
			
			// 1.��������
			HttpRequest httpRequest = new HttpRequest(socket);
			
			// 2.��Ӧ����
			HttpResponse response = new HttpResponse(socket);
			String url = httpRequest.getRequestUrl();
			System.out.println("url: " + url);
			String servletName = ServerContext.getMapping(url);
			System.out.println("ServletName: " + servletName);
			
			/**
			 * ���÷����ȡ��Ӧ��Servlet,��ִ������service����
			 */
			if(servletName != null) {
				Class cla = Class.forName(servletName);
				System.out.println("���ڼ���class��" + servletName);
				HttpServlet servlet = (HttpServlet) cla.newInstance();
				servlet.service(httpRequest, response);
			} else {
				File file = new File("webapp" + url);
				System.out.println("PATH: " + file.getPath());
				if(file.exists()) {
					response.setFile(file);
					System.out.println("����Դ���ҵ�");
	 			} else {
					response.setFile(new File("webapp/root/404.html"));
					response.setStatusCode(404);
					System.out.println("����Դδ�ҵ�");
				}
			}
			
			// ��Ӧ�ͻ���
			response.flush();
		} catch (EmptyRequestException e) {
			System.out.println("����Ϊ��");
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
