package server;

import java.util.List;

import javax.websocket.Session;

import game.Player;

public interface IAppServer {

//	List<Player> getPlayers();
//	void removePlayerByName(String playerName);	
	void messageReceived(String playerName, String message);
//	Player findUserByName(String user);	
//	void createPlayer(String userName);
	void sendMessageOpponentDisconnected(Player opponent, String palyerName);
	
}
