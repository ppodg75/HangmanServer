package service;

import server.AppServer;

public class ServerService {
	
	private final AppServer server;
	
	public ServerService() {
		server = new AppServer();
	}
	
	public void run() {		
		while (true) {
			try {
				Thread.sleep(1000);
				System.out.println("Server ok!");
			} catch (InterruptedException e) { 
				e.printStackTrace();
			}
		}
	}

}
