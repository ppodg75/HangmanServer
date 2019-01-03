package server;

import java.util.List;

import game.Player;

public interface IGameServer {

	void playerDisconnected(Player player);
	void addPlayer(Player player);	
	List<Player> getPlayers();
	Player findPlayerByName(String palyerName);
	
}
