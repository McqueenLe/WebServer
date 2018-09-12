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
		System.out.println("开始注册");
		String username = request.getParameters("name");
		String password = request.getParameters("pwd");
		String nickname = request.getParameters("nickname");
		
		/**
		 * 写入到user.dat文件中
		 */
		File file = new File("user.dat");
		FileOutputStream fos = new FileOutputStream(file);
		OutputStreamWriter os = new OutputStreamWriter(fos, "GBK");
		BufferedWriter bf = new BufferedWriter(os);
		PrintWriter pw = new PrintWriter(bf);
		pw.println("用户名:" + username);
		pw.println("密码:" + password);
		pw.println("昵称:" + nickname);
		pw.flush();
		pw.close();
		System.out.println("注册完毕");
		
		// 返回客户端注册成功消息
		forward("webapp/myweb/reg_success.html", request, response);
	}
}
