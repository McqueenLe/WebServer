package com.webserver.core;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * WebServer����
 * @author xiangyang
 *
 */
public class WebServer {
	private ServerSocket server;
	private ExecutorService threadPool; // �̳߳�
	
	/**
	 * ���췽����������ʼ�������
	 */
	public WebServer() {
		try {
			System.out.println("�������������...");
			server = new ServerSocket(8088);
			// ����һ����СΪ50���̳߳�
			threadPool = Executors.newFixedThreadPool(50);
			System.out.println("������������");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ����˿�ʼ�����ķ���
	 */
	public void start() {
		try {
			/**
			 * ��ʱֻ����ͻ��˵�һ������j
			 */
			while(true) {
				System.out.println("�ȴ��ͻ�������...");
				Socket socket = server.accept();
				System.out.println("һ���ͻ���������!");
				// ����һ���̴߳���ÿͻ�������
				ClientHandler handler = new ClientHandler(socket);
				// ��handler�����̳߳ش���
				threadPool.execute(handler);
//				Thread thread = new Thread(handler);
//				thread.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		WebServer server = new WebServer();
		server.start();
	}
}
