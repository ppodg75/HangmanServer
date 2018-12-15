package server;

import java.util.List;

import javax.websocket.Session;

import game.Player;

public interface IAppServer {

	List<Player> getPlayers();
	void removePlayerBySession(Session session);
	void messageReceived(Session session, String message);
	Player findUserByName(String user);
	Player findUserByUID(String UID);
	void initPlayer(Session session, String UID);
	void updatePlayer(String  UID, String userName);
	void sendMessageOpponentDisconnected(Player opponent, String palyerName);
	
}
