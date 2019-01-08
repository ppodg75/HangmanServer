package ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

	private static final String OPERATION_HELLO = "hello";
	private static final String OPERATION_BYEBYE = "byebye";

	@Inject
	private IAppServer server;

	public ClientWebSocket() {
		System.out.println("ClientWebSocket created");
	}

	private static ConcurrentLinkedQueue<Session> peers = new ConcurrentLinkedQueue<>();
	private static ConcurrentHashMap<String, Session> playersSessions = new ConcurrentHashMap<>();

	@OnOpen
	public void onOpen(Session session) {
		System.out.println("WS:onOpen::" + session.getId());
		peers.add(session);
	}

	@OnClose
	public void onClose(Session session) {
		System.out.println("WS:onClose::" + session.getId());
		peers.remove(session);
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		System.out.println("WS:onMessage:" + message);
		if (server == null) {
			System.out.println("Server is NULL");
		} else {
			System.out.println("WS:Forwarding message to app server");
			
			if (isMessageByeBye(message)) {
				removePlayerSession(message, session);
			} else
			if (isMessageHello(message)) {
				updatePlayerSession(message, session);
			} else {
				String playerName = getPlayerNameBySession(session);
				if (!playerName.isEmpty() ) {
				   server.messageReceived(playerName, message);
				}
			}
		}
	}

	@OnError
	public void onError(Throwable t) {
		System.out.println("WS:onError::" + t.getMessage());
	}

	private boolean isMessageHello(String message) {		
		return OPERATION_HELLO.equals(getOperationFromMessage(message));
	}

	private boolean isMessageByeBye(String message) {		
		return OPERATION_BYEBYE.equals(getOperationFromMessage(message));
	}
	
	private void updatePlayerSession(String message, Session session) {
		System.out.println("WS:updatePlayerSession " + message+ " > session: "+session.getId());
		String playerName = getDataFromMessage(message);
		playersSessions.put(playerName, session);
		synchronizeSessionPlayers();
	}
	
	private void removePlayerSession(String message, Session session) {
		System.out.println("removePlayerSession " + message+ " > session: "+session.getId());
		String playerName = getDataFromMessage(message);
		playersSessions.remove(playerName);
		peers.remove(session);
		server.removePlayerByName(playerName);
		synchronizeSessionPlayers();
	}

	private String getOperationFromMessage(String message) {
		String[] msgItems = message.split("#");
		if (msgItems.length > 0) {
			return msgItems[0];
		} else {
			return "";
		}
	}

	private String getDataFromMessage(String message) {
		String[] msgItems = message.split("#");
		if (msgItems.length > 1) {
			return msgItems[1];
		} else {
			return "";
		}
	}

	private void synchronizeSessionPlayers() {
		System.out.println("WS:synchronizeSessionPlayers ");
		List<String> playerSessionsToRemove = new ArrayList<>();
		for (Map.Entry<String, Session> entry : playersSessions.entrySet()) {
			if (peerNotExists(entry.getValue())) {
				playerSessionsToRemove.add(entry.getKey());
			}
		}
		playerSessionsToRemove.forEach(playerName -> playersSessions.remove(playerName));
	}
	
	private boolean peerNotExists(Session session) {
		return !peers.stream().anyMatch( peer -> peer.getId().equals(session.getId()) );
	}

	private void send(Session session, String msg) {
		System.out.println("WS:send (toSession)::" + session.getId() + " > " + msg);
		try {
			session.getBasicRemote().sendText(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendToPlayer(String playerName, String msg) {
		System.out.println("WS:sendToPlayer::" + playerName + " > " + msg);
		Session session = getSessionByPlayerName(playerName);
		send(session, msg);
	}

	public void sendToAll(String msg) {
		System.out.println("WS:sendToAll::" + msg);
		peers.forEach(session -> {
			send(session, msg);
		});
	}

	private Session getSessionByPlayerName(String playerName) {
		return playersSessions.get(playerName);
	}

	private String getPlayerNameBySession(Session session) {
		System.out.println("WS:getPlayerNameBySession::" + session.getId() );
		if (session != null) {
			for (Map.Entry<String, Session> entry : playersSessions.entrySet()) {
				if (entry.getValue().getId().equals(session.getId())) {
					return entry.getKey();
				}
			}
		}
		return new String("");
	}

}
