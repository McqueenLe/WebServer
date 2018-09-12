package com.webserver.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HttpResponse {
	private int statusCode = 200;
	private Socket socekt;
	private OutputStream outputStream;
	private File file;
	private Map<String, String> headers = new HashMap<String, String>();
	
	public HttpResponse(Socket socket) {
		try {
			this.socekt = socket;
			this.outputStream = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ������Ӧ
	 */
	public void flush() {
		sendStatusLine();
		sendHeaders();
		sendContent();
	}
	
	/**
	 * ����״̬��
	 */
	private void sendStatusLine() {
		String line = "HTTP/1.1 " + statusCode + " " + HttpContext.getStatusReason(statusCode);
		println(line);
		System.out.println("����״̬��: " + line);
	}
	
	/**
	 * ������Ӧͷ
	 */
	private void sendHeaders() {
		/**
		 * ��Ϊ����headers���Ϸ��͸�����header
		 */
		Set<Entry<String, String>> entrySet = headers.entrySet();
		for(Entry<String, String> entry : entrySet) {
			String line = entry.getKey() + ": " + entry.getValue();
			println(line);
		}
		println("");
		System.out.println("��Ӧͷ�������!");
	}
	
	/**
	 * ��������
	 */
	private void sendContent() {
		if(file != null) {
			try (FileInputStream fis = new FileInputStream(file)) {
				byte[] bytes = new byte[10 * 1024];
				int d = -1;
				while((d = fis.read(bytes)) != -1) {
					outputStream.write(bytes, 0, d);
				}
				System.out.println("��Ϣ���ݷ������");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * ��ָ�������ʽ������Ϣ
	 * @param line
	 */
	private void println(String line) {
		try {
			outputStream.write(line.getBytes("ISO8859-1"));
			outputStream.write(13); // д�س�CR
			outputStream.write(10); // д����LF
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		if(file != null) {
			headers.put("Content-Length", file.length()+"");
			String name = file.getName();
			int index = name.lastIndexOf(".") + 1;
			String type = HttpContext.getMimeType(name.substring(index));
			headers.put("Content-Type", type);
		}
		this.file = file;
	}
	
}
