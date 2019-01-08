package server;

import game.Player;

public interface IAppServer {

	void removePlayerByName(String playerName);	
	void messageReceived(String playerName, String message);

	void sendMessageOpponentDisconnected(Player opponent, String palyerName);
	void sendGoToPage(Player opponent, String page);
	
	void wordUpdated(Player playerName);	
	void sendLetter(Player playerNamer, String letter);
	void sendRefreshListPlayersToAll();	
	
}
