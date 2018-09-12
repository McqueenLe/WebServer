package com.webserver.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest implements Serializable{

	private static final long serialVersionUID = 1L;
	private Map<String, String> headers = new HashMap<String, String>();
	private Map<String, String> params = new HashMap<String, String>();
	
	/**
	 * �����������Ϣ����
	 */
	private String method; // ��������
	private String url; // ��Դ·��
	
	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	private String protocol; // Э�鼰�汾
	private String requestUrl; // url�е����󲿷�
	private String queryString; // url�еĲ�������
	
	/**
	 * ��Ϣͷ�����Ϣ����
	 */
	
	/**
	 * ��Ϣ���������Ϣ����
	 */
	
	
	
	// �ͻ������������Ϣ
	private Socket socket;
	private InputStream in;
	
	
	/**
	 * ��ʼ������
	 */
	public HttpRequest(Socket socket) throws EmptyRequestException{
		try {
			this.socket = socket;
			this.in = socket.getInputStream();
			/**
			 * ��������
			 * 1������������
			 * 2����������ͷ
			 * 3��������Ϣ����
			 */
			parseRequestLine();
			parseHeaders();
			parseContent();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (EmptyRequestException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ����������
	 * @throws IOException 
	 */
	private void parseRequestLine() throws EmptyRequestException {
		System.out.println("��ʼ����������...");
		String s = readLine();
		/**
		 * ͨ���հ��ַ��ָ��ȡ�����ַ���
		 * �˴������±�Խ�硣��ʱ����HTTPЭ������ͻ��˷���һ�����������������ʱͨ���ո�
		 * ��ֺ��ǵò����������ݵġ�
		 */
		String[] results = s.split("\\s");
		if(results.length < 3) {
			throw new EmptyRequestException();
		}
		method = results[0];
		url = results[1];
		parseUrl();
		protocol = results[2];
		System.out.println("method: " + method);
		System.out.println("url: " + url);
		System.out.println("protocol: " + protocol);
		System.out.println("�����н������!");
	}
	
	/**
	 * ������Ϣͷ�����̣�
	 * ѭ������readLine()��������ȡÿһ����Ϣͷ����readLine��������ֵΪ���ַ���ʱֹͣѭ��
	 * (��Ϊ���ؿ��ַ���˵��������ȡ����CRLF������ʱ��Ϊ��Ϣͷ�����ı�־)
	 * �ڶ�ȡ��ÿ����Ϣͷ�󣬸���": "��ð�ſո񣩽��в�֣�
	 * ������Ϣͷ��������Ϊkey����Ϣͷ��Ӧ��ֵ��Ϊvalue���浽����headers
	 * ���Map����ɽ�������
	 */
	private void parseHeaders() {
		System.out.println("��ʼ������Ϣͷ...");
		String line = null;
		while(true) {
			line = readLine();
			if("".equals(line)) {
				break;
			}
			String[] results = line.split(": ");
			headers.put(results[0],	results[1]);
		}
		System.out.println(headers);
		System.out.println("��Ϣͷ�������!");
	}
	
	/**
	 * ����url
	 * ��ΪЯ��������url�Ͳ�Я��������url
	 */
	private void parseUrl() {
		if(url.indexOf("?") != -1) {
			// ����"?"�ָ�url��������
			String[] results = url.split("\\?");
			requestUrl = results[0];
			queryString = results[1];
			// ������������
			parseParameters(queryString);
		} else {
			requestUrl = url;
		}
		System.out.println("requestUrl: " + requestUrl);
		System.out.println("queryString: " + queryString);
		System.out.println("url: " + url);
		System.out.println("parameters: " + params.toString());
	}
	
	/**
	 * ��������
	 */
	public void parseParameters(String line) {
		try {
			line = URLDecoder.decode(line, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// ��ȡ����������֮����ͨ��&�������ָ�
		String[] parameters = line.split("&");
		// �������齫�����ֱ���key-value����ʽ�浽map��
		for(String string : parameters) {
			String[] strings = string.split("=");
			if(strings.length > 1) {
				params.put(strings[0], strings[1]);
			} else {
				params.put(strings[0], null);
			}
		}
		System.out.println("�ͻ����ϴ��Ĳ�����" + params);
	}
	
	/**
	 * ��������
	 * 1.���ж��Ƿ���Content-Type
	 * 2.����У���˵�����Ĳ����в�����Ȼ�����Conent-Length����ȡ���ݵĳ���
	 * 3.����Content-Length���ȵ��ֽ����飬������ȡ������������
	 * 4.�����鰴ָ������ת��ΪString�ַ���Ȼ��ָ����������Ҫ�Ĳ���
	 */
	private void parseContent() {
		System.out.println("��ʼ������Ϣ����...");
		if(headers.containsKey("Content-Length")) {
			String type = headers.get("Content-Type");
			if(null != type && "application/x-www-form-urlencoded".equals(type)) {
				int len = Integer.parseInt(headers.get("Content-Length")); 
				byte[] bytes = new byte[len];
				try {
					in.read(bytes);
					String line = new String(bytes, "ISO8859-1");
					parseParameters(line);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("��Ϣ���Ľ������!");
	}
	
	/**
	 * ��ȡһ���ַ�������������ȡCR��LFʱֹͣ����֮ǰ��������һ���ַ�����ʽ���ء�
	 * @return
	 * @throws IOException
	 */
	public String readLine() {
		StringBuilder sBuilder = new StringBuilder();
		int d = -1;
		char c1='a'; // ��ʾ�ϴζ�ȡ���ַ�
		char c2='a'; // ��ʾ���ζ�ȡ���ַ�
		try {
			while((d = in.read()) != -1) {
				c2 = (char) d;
				if(c1 == 13 && c2 == 10) {
					break;
				}
				sBuilder.append(c2);
				c1 = c2;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sBuilder.toString().trim();
	}
	
	/**
	 * ���ݸ�������Ϣͷ�����ֻ�ȡ��Ӧ��Ϣͷ��ֵ
	 * @param name
	 * @return
	 */
	public String getHeader(String name) {
		return headers.get(name);
	}
	
	/**
	 * ��ȡ����
	 * @param key
	 * @return
	 */
	public String getParameters(String key) {
		return params.get(key);
	}

	public String getMethod() {
		return method;
	}

	public String getUrl() {
		return url;
	}

	public String getProtocol() {
		return protocol;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
}
