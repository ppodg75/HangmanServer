package server;

import java.util.List;

import javax.websocket.Session;

import game.Player;

public interface IAppServer {

	List<Player> getPlayers();
	void removePlayerBySession(Session session);
	void messageReceived(Session session, String message);
}
