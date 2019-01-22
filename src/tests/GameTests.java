package tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.Test;

import game.Game;
import game.GameStatus;
import game.Player;
import game.PlayerStatus;
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
	
	@Test
	public void gappedWordChangedAfterLetterHit() {
		Player player1 = new Player("Piotr");
		Player player2 = new Player("Wojtek");
		
		Game game = new Game();
		game.setGuessPlayer(player1);
		game.setWordPlayer(player2);
		game.init();
		game.updateWord("HAMAK");
		String gappedWord = game.getGappedWord();		
		game.guessLetter("H");
		String gappedWordAfterHit = game.getGappedWord();
		
		assertFalse(gappedWord.equals(gappedWordAfterHit));
	}
	
	@Test
	public void gappedWordNotChangedAfterLetterMiss() {
		Player player1 = new Player("Piotr");
		Player player2 = new Player("Wojtek");
		
		Game game = new Game();
		game.setGuessPlayer(player1);
		game.setWordPlayer(player2);
		game.init();
		game.updateWord("HAMAK");
		String gappedWord = game.getGappedWord();		
		game.guessLetter("Z");
		String gappedWordAfterHit = game.getGappedWord();
		
		assertTrue(gappedWord.equals(gappedWordAfterHit));		
	}
	
	@Test
	public void gappedWordNotChangedAfterTheSameLetter() {
		Player player1 = new Player("Piotr");
		Player player2 = new Player("Wojtek");
		
		Game game = new Game();
		game.setGuessPlayer(player1);
		game.setWordPlayer(player2);
		game.init();
		game.updateWord("HAMAK");
		String gappedWord = game.getGappedWord();		
		game.guessLetter("H");
		String gappedWordAfterHit = game.getGappedWord();
		game.guessLetter("H");
		String gappedWordAfterTheSameLetter = game.getGappedWord();
		
		assertTrue(gappedWordAfterHit.equals(gappedWordAfterTheSameLetter));		
	}
	
	@Test 
	public void guessPlayerReachMaximumMissesAndLost() {
		Player player1 = new Player("Piotr");
		Player player2 = new Player("Wojtek");
		
		Game game = new Game();
		game.setGuessPlayer(player1);
		game.setWordPlayer(player2);
		game.init();
		game.updateWord("HAMAK");
		for(String s : Arrays.asList("S","L","O","X","Y","T","R","Z")) {
			game.guessLetter(s);	
		}
		assertTrue(game.getGuessPlayer().getCountLosts() > 0);
		assertTrue(game.getWordPlayer().getCountWins() > 0);		
	}
			
	@Test 
	public void guessPlayerHitAllLetters() {
		Player player1 = new Player("Piotr");
		Player player2 = new Player("Wojtek");
		
		Game game = new Game();
		game.setGuessPlayer(player1);
		game.setWordPlayer(player2);
		game.init();
		game.updateWord("HAMAK");
		for(String s : Arrays.asList("H","A","M","K")) {
			game.guessLetter(s);	
		}
		assertTrue(game.getGuessPlayer().getCountWins() > 0);
		assertTrue(game.getWordPlayer().getCountLosts() > 0);		
	}
		
	@Test 
	public void testPlayersInGame() {
		Player player1 = new Player("Piotr");
		Player player2 = new Player("Wojtek");
		Player player3 = new Player("Krzysztof");
		
		Game game = new Game();
		game.setGuessPlayer(player1);
		game.setWordPlayer(player2);
		game.init();
		
		assertTrue(game.playerIn(player1));
		assertTrue(game.playerIn(player2));
		assertFalse(game.playerIn(player3));
	}
	
	@Test 
	public void testOppenent() {
		Player player1 = new Player("Piotr");
		Player player2 = new Player("Wojtek");
		
		Game game = new Game();
		game.setGuessPlayer(player1);
		game.setWordPlayer(player2);
		game.init();
		
		assertTrue(game.getOpponent(player1) == player2);		
		
	}
	
	@Test
	public void endGameBecauseOpponentEndGame() {
		Player player1 = new Player("Piotr");
		Player player2 = new Player("Wojtek");
		
		Game game = new Game();
		game.setGuessPlayer(player1);
		game.setWordPlayer(player2);	
		game.init();
		assertTrue(game.getGameStatus() == GameStatus.WAIT_FOR_WORD);
		assertTrue(player1.getStatus() == PlayerStatus.PLAYING);
		assertTrue(player2.getStatus() == PlayerStatus.PLAYING);
		
		game.endGameBeforeBecouseOfPlayer(player1);
		assertTrue(game.getGameStatus() == GameStatus.END);
		assertTrue(player1.getStatus() == PlayerStatus.CREATED);
		assertTrue(player2.getStatus() == PlayerStatus.CREATED);
	}
		
	
}
