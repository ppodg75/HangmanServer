package server;

import java.util.List;

import game.Player;

public interface IGameServer {

	void playerDisconnected(Player player);
	Player createPlayer(String name, String UID);
	Player findPlayerByUID(String UID);
	List<Player> getPlayers();
	Player findPlayerByName(String user);
	
}
