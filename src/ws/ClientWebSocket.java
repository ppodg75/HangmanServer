package ws;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import server.IAppServer;

@ApplicationScoped
@ServerEndpoint("/play")
public class ClientWebSocket implements IClientWebSocket {

	@Inject
	private IAppServer server;
	
	private Random random = new Random();

	public ClientWebSocket() {
		System.out.println("cws created");
	}

	private static ConcurrentLinkedQueue<Session> peers = new ConcurrentLinkedQueue<>();
	private static ConcurrentHashMap<String, Session> playersUIDs = new ConcurrentHashMap<>();

	@OnOpen
	public void onOpen(Session session) {
		System.out.println("onOpen::" + session.getId());
		String UID = generateUID();
		server.initPlayer(session, UID);
		send(session, "UID#"+UID);
		peers.add(session);
	}
	
	private String generateUID() {		
		return "UID"+String.valueOf(random.nextInt(2000)+30000);
	}

	@OnClose
	public void onClose(Session session) {
		System.out.println("onClose::" + session.getId());
		server.removePlayerBySession(session);
		peers.remove(session);
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		System.out.println("on message:" + message);
		if (server == null) {
			System.out.println("Server is NULL");
		} else {
			System.out.println("Forwarding message to app server");
//			if (!isRefreshPlayer(message)) {
			  server.messageReceived(session, message);
//			}
		}
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
		peers.forEach(session -> {
			send(session, msg);
		});
	}

}
