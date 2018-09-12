package com.webserver.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.webserver.http.HttpResponse;
import com.webserver.http.HttpRequest;

public class LoginServlet extends HttpServlet{

	@Override
	public void service(HttpRequest request, HttpResponse response) throws Exception{
		/**
		 * 先获取客户端发送过来的参数并验证
		 */
		String username = request.getParameters("name");
		String pwd = request.getParameters("pwd");
		if(username == null || pwd == null) {
			throw new RuntimeException("参数错误");
		}
		
		/**
		 * 从文件获取存储的用户登录信息
		 */
		Map<String, String> map = new HashMap<String, String>();
		File file = new File("user.dat");
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis, "gbk");
		BufferedReader br = new BufferedReader(isr);
		String line = null;
		while((line = br.readLine()) != null) {
			String[] str = line.split(":");
			if(str.length > 1) {
				map.put(str[0], str[1]);
			} else {
				map.put(str[0], null);
			}
		}
		
		/**
		 * 将客户端传上来的参数与读取出来的用户信息进行比较
		 */
		String name = map.get("用户名");
		String password = map.get("密码");
		System.out.println("用户名: " + name + " 密码：" + password);
		
		if(name == null || password == null) {
			throw new RuntimeException("未找到此用户");
		} 
		if(name.equals(username) && pwd.equals(password)) {
			System.out.println("登录成功");
			forward("webapp/myweb/login_success.html", request, response);
		} else {
			System.out.println("用户名或密码错误");
			forward("webapp/myweb/login_fail.html", request, response);
		}
		
	}
}
