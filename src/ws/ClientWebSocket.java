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

import jersey.repackaged.com.google.common.base.Optional;
import server.IAppServer;
import utils.WordCodeDecode;

@ApplicationScoped
@ServerEndpoint("/play")
public class ClientWebSocket implements IClientWebSocket {

	private static final String OPERATION_HELLO = "hello";
	private static final String OPERATION_BYEBYE = "byebye";

	// Opis
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
				String playerName = getPlayerIdBySession(session);
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
		String playerId = getDataFromMessage(message);
		playersSessions.put(playerId, session);
		synchronizeSessionPlayers();
	}
	
	@OnClose
	public void onClose(Session session) {
		System.out.println("WS:onClose::" + session.getId());
		String playerId = getPlayerIdBySession(session);
		playersSessions.remove(playerId);
		peers.remove(session);
	}
	
	private void removePlayerSession(String message, Session session) {
		System.out.println("removePlayerSession " + message+ " > session: "+session.getId());
		String playerId = getDataFromMessage(message) ;
		playersSessions.remove(playerId);
		peers.remove(session);
		synchronizeSessionPlayers();
		server.removePlayerById(Long.valueOf(playerId));	
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
		playerSessionsToRemove.forEach(playerId -> playersSessions.remove(playerId));
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
	
	public void sendToPlayer(long playerId, String msg) {
		System.out.println("WS:sendToPlayer::" + playerId + " > " + msg);
		Session session = getSessionByPlayerId(String.valueOf(playerId));
		send(session, msg);
	}

	public void sendToAll(String msg) {
		System.out.println("WS:sendToAll::" + msg);
		peers.forEach(session -> {
			send(session, msg);
		});
	}

	private Session getSessionByPlayerId(String playerId) {
		return playersSessions.get(playerId);
	}

	private String getPlayerIdBySession(Session session) {
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
