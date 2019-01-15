package server;

import game.Player;

public interface IAppServer {

	void removePlayerById(long playerId);	
	
	void sendMessagePlayerDisconnected(Player opponent);
	void sendMessageOpponentEndGame(Player opponent);
	void sendGoToPage(Player opponent, String page);
	
	void wordUpdated(Player playerName);	
	void sendLetter(Player playerNamer, String letter);
	void sendRefreshListPlayersToAll();	
	
}
