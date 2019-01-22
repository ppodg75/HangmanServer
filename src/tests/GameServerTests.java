package tests;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import game.Game;
import game.Player;
import game.PlayerStatus;
import server.GameServer;

public class GameServerTests {

	private static GameServer gameServer;

	@BeforeClass
	public static void init() {
		gameServer = new GameServer();
	}

	@Test
	public void shoulAddPlayer() {
		Player player1 = new Player("Krzysztof");
		gameServer.addPlayer(player1);
		assertTrue(gameServer.findPlayerByName(player1.getName()) != null);
	}

	@Test
	public void shouldRemovePlayer() {
		Player player1 = new Player("Krzysztof");
		gameServer.addPlayer(player1);
		assertTrue(gameServer.findPlayerByName(player1.getName()) != null);
	}

	@Test
	public void createGameWithTwoPlayers() {
		Player player1 = new Player("Piotr");
		Player player2 = new Player("Wojtek");
		Game game = gameServer.createGame(player1, player2);
		assertTrue(game.playerIn(player1));
		assertTrue(game.playerIn(player2));
	}

	@Test
	public void gameWithComputerAndPlayerIsGuess() {
		Player player1 = new Player("Piotr");
		Game game = gameServer.createGame(player1);

		assertTrue(game.getGuessPlayer() == player1);
		assertTrue(game.getWordPlayer().isComputer());
	}

	@Test
	public void findPlayerWhoIsInGame() {
		Player player1 = new Player("Piotr");
		gameServer.addPlayer(player1);
		assertTrue(gameServer.findPlayerById(player1.getPlayerId()) != null);
	}

	@Test
	public void listPlayersContainsGamePlayers() {
		Player player1 = new Player("Ola");
		Player player2 = new Player("Ula");
		gameServer.addPlayer(player1);
		gameServer.addPlayer(player2);
		Game game = gameServer.createGame(player1, player2);
		List<Player> list = gameServer.getPlayers();
		assertTrue(list.contains(player1));
		assertTrue(list.contains(player2));
	}

	@Test
	public void playerUpdateGappedWordLetter() {
		Player player1 = new Player("Ola");
		Player player2 = new Player("Ula");
		gameServer.addPlayer(player1);
		gameServer.addPlayer(player2);
		Game game = gameServer.createGame(player1, player2);
		game.init();
		game.updateWord("HAMAK");
		gameServer.updateGappedWordLetter(game.getGuessPlayer(), "A");
		assertTrue(game.getGappedWord().equals("_A_A_"));
	}

	@Test
	public void playerSetGuessedWord() {
		Player player1 = new Player("Ola");
		Player player2 = new Player("Ula");
		gameServer.addPlayer(player1);
		gameServer.addPlayer(player2);
		Game game = gameServer.createGame(player1, player2);
		game.init();
		game = gameServer.updateWord(game.getWordPlayer(), "HAMAK");
		assertTrue(game.getTheWord().equals("HAMAK"));
	}
	
	@Test
	public void playerEndGame() {
		Player player1 = new Player("Ola");
		Player player2 = new Player("Ula");
		gameServer.addPlayer(player1);
		gameServer.addPlayer(player2);
		Game game = gameServer.createGame(player1, player2);
		game.init();
		assertTrue(player1.getStatus() == PlayerStatus.PLAYING);
		gameServer.playerEndGame(player1);
		assertTrue(player1.getStatus() == PlayerStatus.CREATED);
		
	}
	

}
