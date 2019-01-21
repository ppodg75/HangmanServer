package tests;

import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;

import org.junit.Test;

import game.Game;
import game.Player;
import server.GameServer;

public class GameTests {
	
	@Test
	public void gamesGetContinousIds() {
		Game game1  = new Game();
		Game game2  = new Game();
		
		assertTrue(game1.getGameId() > 0);
		assertTrue(game2.getGameId() == game1.getGameId()+1);
	}
	
	@Test 
	public void gameAcvtivityHasChange() {
		Game game  = new Game();
		LocalDateTime lastActivity = game.getLastActivity();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		game.updateWord("TEST");		
		assertTrue(game.getLastActivity().isAfter(lastActivity));
	}
	
	@Test 
	public void gameHasWordAndGuessPlayers() {
		Player player1 = new Player("Piotr");
		Player player2 = new Player("Wojtek");
		
		Game game = new Game();
		game.setGuessPlayer(player1);
		game.setWordPlayer(player2);
		
		game.init();
		
		assertTrue(game.getWordPlayer().getName().equals(player1.getName()) || game.getWordPlayer().getName().equals(player2.getName()));
		assertTrue(game.getGuessPlayer().getName().equals(player2.getName()) || game.getGuessPlayer().getName().equals(player1.getName()));
	}
	

			
			

}
