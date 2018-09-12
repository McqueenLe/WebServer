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
		 * �Ȼ�ȡ�ͻ��˷��͹����Ĳ�������֤
		 */
		String username = request.getParameters("name");
		String pwd = request.getParameters("pwd");
		if(username == null || pwd == null) {
			throw new RuntimeException("��������");
		}
		
		/**
		 * ���ļ���ȡ�洢���û���¼��Ϣ
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
		 * ���ͻ��˴������Ĳ������ȡ�������û���Ϣ���бȽ�
		 */
		String name = map.get("�û���");
		String password = map.get("����");
		System.out.println("�û���: " + name + " ���룺" + password);
		
		if(name == null || password == null) {
			throw new RuntimeException("δ�ҵ����û�");
		} 
		if(name.equals(username) && pwd.equals(password)) {
			System.out.println("��¼�ɹ�");
			forward("webapp/myweb/login_success.html", request, response);
		} else {
			System.out.println("�û������������");
			forward("webapp/myweb/login_fail.html", request, response);
		}
		
	}
}
