package server;

import java.util.List;

import game.Game;
import game.Player;

public interface IGameServer {

	void playerDisconnected(Player player);
	void addPlayer(Player player);	
	void removePlayer(Player player);
	List<Player> getPlayers();
	Player findPlayerByName(String palyerName);
	Game createGame(Player player, Player opponent);
	Game createGame(Player player);
	Game updateGappedWordLetter(Player player, String letter);
	Game getGameByPlayerName(String palyerName);
	Game updateWord(Player player, String word);
	List<Game> getListOfGames();
	
}
