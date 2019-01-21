package tests;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import game.Game;
import game.Player;
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
		assertTrue(gameServer.findPlayerByName(player1.getName())!=null);
	}
	
	@Test
	public void shouldRemovePlayer() {
		Player player1 = new Player("Krzysztof");		
		gameServer.addPlayer(player1);
		assertTrue(gameServer.findPlayerByName(player1.getName())!=null);
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
}
