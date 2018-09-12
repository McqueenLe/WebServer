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
	 * 请求行相关信息定义
	 */
	private String method; // 方法类型
	private String url; // 资源路径
	
	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	private String protocol; // 协议及版本
	private String requestUrl; // url中的请求部分
	private String queryString; // url中的参数部分
	
	/**
	 * 消息头相关信息定义
	 */
	
	/**
	 * 消息正文相关信息定义
	 */
	
	
	
	// 客户端连接相关信息
	private Socket socket;
	private InputStream in;
	
	
	/**
	 * 初始化请求
	 */
	public HttpRequest(Socket socket) throws EmptyRequestException{
		try {
			this.socket = socket;
			this.in = socket.getInputStream();
			/**
			 * 解析请求
			 * 1：解析请求行
			 * 2：解析请求头
			 * 3：解析消息正文
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
	 * 解析请求行
	 * @throws IOException 
	 */
	private void parseRequestLine() throws EmptyRequestException {
		System.out.println("开始解析请求行...");
		String s = readLine();
		/**
		 * 通过空白字符分割读取出的字符串
		 * 此处可能下标越界。这时由于HTTP协议允许客户端发送一个空请求过来，而这时通过空格
		 * 拆分后是得不到三项内容的。
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
		System.out.println("请求行解析完毕!");
	}
	
	/**
	 * 解析消息头的流程：
	 * 循环调用readLine()方法，读取每一个消息头，当readLine方法返回值为空字符串时停止循环
	 * (因为返回空字符串说明单独读取到了CRLF，而这时作为消息头结束的标志)
	 * 在读取到每个消息头后，根据": "（冒号空格）进行拆分，
	 * 并将消息头的名字作为key，消息头对应的值作为value保存到属性headers
	 * 这个Map中完成解析工作
	 */
	private void parseHeaders() {
		System.out.println("开始解析消息头...");
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
		System.out.println("消息头解析完毕!");
	}
	
	/**
	 * 解析url
	 * 分为携带参数的url和不携带参数的url
	 */
	private void parseUrl() {
		if(url.indexOf("?") != -1) {
			// 先用"?"分割url成两部分
			String[] results = url.split("\\?");
			requestUrl = results[0];
			queryString = results[1];
			// 解析参数部分
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
	 * 解析参数
	 */
	public void parseParameters(String line) {
		try {
			line = URLDecoder.decode(line, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// 获取到参数部分之后再通过&符号来分割
		String[] parameters = line.split("&");
		// 遍历数组将参数分别以key-value的形式存到map中
		for(String string : parameters) {
			String[] strings = string.split("=");
			if(strings.length > 1) {
				params.put(strings[0], strings[1]);
			} else {
				params.put(strings[0], null);
			}
		}
		System.out.println("客户端上传的参数：" + params);
	}
	
	/**
	 * 解析正文
	 * 1.先判断是否有Content-Type
	 * 2.如果有，则说明正文部分有参数。然后解析Conent-Length，获取数据的长度
	 * 3.创建Content-Length长度的字节数组，用流读取并放入数组中
	 * 4.将数组按指定编码转换为String字符，然后分割解析我们想要的参数
	 */
	private void parseContent() {
		System.out.println("开始解析消息正文...");
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
		System.out.println("消息正文解析完毕!");
	}
	
	/**
	 * 读取一行字符串，当连续读取CR，LF时停止并将之前的内容以一行字符串形式返回。
	 * @return
	 * @throws IOException
	 */
	public String readLine() {
		StringBuilder sBuilder = new StringBuilder();
		int d = -1;
		char c1='a'; // 表示上次读取的字符
		char c2='a'; // 表示本次读取的字符
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
	 * 根据给定的消息头的名字获取对应消息头的值
	 * @param name
	 * @return
	 */
	public String getHeader(String name) {
		return headers.get(name);
	}
	
	/**
	 * 获取参数
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
