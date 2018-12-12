package ws;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.sun.security.ntlm.Client;

import server.AppServer;

@ServerEndpoint("/play")
public class ClientWebSocket {
	
	private AppServer server;

	private static ConcurrentLinkedQueue<Session> peers = new ConcurrentLinkedQueue<>();
	
	public ClientWebSocket(AppServer server) {
		this.server = server;
	}

	@OnOpen
	public void onOpen(Session session) {
		System.out.println("onOpen::" + session.getId());
		peers.add(session);
	}
	
	@OnClose
	public void onClose(Session session) {
		System.out.println("onClose::" + session.getId());
		server.removePlayerBySession(session);
		peers.remove(session);
	}

	@OnMessage
	public void onMessage(String message, Session session) {
         server.messageReceived(session, message);
	}

	@OnError
	public void onError(Throwable t) {
		System.out.println("onError::" + t.getMessage());
	}

	public void send(Session session, String msg) {
		try {
			session.getBasicRemote().sendText(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendToAll(String msg) {
		peers.forEach( session -> { send(session,msg); });
	}

}
