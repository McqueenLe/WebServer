package com.webserver.servlet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import com.webserver.http.HttpResponse;
import com.webserver.http.HttpRequest;

public class RegServlet extends HttpServlet {

	@Override
	public void service(HttpRequest request, HttpResponse response) throws Exception {
		System.out.println("��ʼע��");
		String username = request.getParameters("name");
		String password = request.getParameters("pwd");
		String nickname = request.getParameters("nickname");
		
		/**
		 * д�뵽user.dat�ļ���
		 */
		File file = new File("user.dat");
		FileOutputStream fos = new FileOutputStream(file);
		OutputStreamWriter os = new OutputStreamWriter(fos, "GBK");
		BufferedWriter bf = new BufferedWriter(os);
		PrintWriter pw = new PrintWriter(bf);
		pw.println("�û���:" + username);
		pw.println("����:" + password);
		pw.println("�ǳ�:" + nickname);
		pw.flush();
		pw.close();
		System.out.println("ע�����");
		
		// ���ؿͻ���ע��ɹ���Ϣ
		forward("webapp/myweb/reg_success.html", request, response);
	}
}
