package server;

import java.util.List;

import dto.GameDto;
import game.Game;
import game.Player;

public interface IGameServer {

	void playerDisconnected(Player player);
	void addPlayer(Player player);	
	void removePlayer(Player player);
	List<Player> getPlayers();
	Player findPlayerByName(String playerName);
	Player findPlayerById(long palyerId);
	Game createGame(Player player, Player opponent);
	Game createGame(Player player);
	Game updateGappedWordLetter(Player player, String letter);
	Game getGameByPlayerName(String playerName);
	Game findGameByPlayer(Player player);
	Game updateWord(Player player, String word);
	List<Game> getListOfGames();
	
}
