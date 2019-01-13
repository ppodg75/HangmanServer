package dto;

import java.io.Serializable;

import game.Game;
import utils.WordCodeDecode;

public class GameDto implements Serializable {

	private static final long serialVersionUID = 75264711556227767L;

	private String theWord;
	private String gappedWord;
	private int countMissed = 0;
	private String usedLetters;
	private String gameStatus;
	private String winner;
	private String playerWordName;
	private String playerGuessName;

	public GameDto(String theWord, String gappedWord, int countMissed, String usedLetters, String gameStatus,
			String winner, String playerWordName, String playerGuessName) {
		this.theWord = theWord;
		this.gappedWord = gappedWord;
		this.countMissed = countMissed;
		this.usedLetters = usedLetters;
		this.gameStatus = gameStatus;
		this.winner = winner;
		this.playerWordName = playerWordName;
		this.playerGuessName = playerGuessName;
	}

	public String getTheWord() {
		return theWord;
	}

	public void setTheWord(String theWord) {
		this.theWord = theWord;
	}

	public String getGappedWord() {
		return gappedWord;
	}

	public void setGappedWord(String gappedWord) {
		this.gappedWord = gappedWord;
	}

	public int getCountMissed() {
		return countMissed;
	}

	public void setCountMissed(int countMissed) {
		this.countMissed = countMissed;
	}

	public String getUsedLetters() {
		return usedLetters;
	}

	public void setUsedLetters(String usedLetters) {
		this.usedLetters = usedLetters;
	}

	public String getGameStatus() {
		return gameStatus;
	}

	public void setGameStatus(String gameStatus) {
		this.gameStatus = gameStatus;
	}

	public String getWinner() {
		return winner;
	}

	public void setWinner(String winner) {
		this.winner = winner;
	}

	public String getPlayer1Name() {
		return playerWordName;
	}

	public void setPlayer1Name(String player1Name) {
		this.playerWordName = player1Name;
	}

	public String getPlayer2Name() {
		return playerGuessName;
	}

	public void setPlayer2Name(String player2Name) {
		this.playerGuessName = player2Name;
	}

	public static GameDto of(Game game) {
		if (game != null) {
			return new GameDto(WordCodeDecode.codePolishWordToWordWithSpecs(game.getTheWord()),
					WordCodeDecode.codePolishWordToWordWithSpecs(game.getGappedWord()), game.getCountMissed(),
					game.getUsedLetters(), game.getGameStatus().name(),
					WordCodeDecode.codePolishWordToWordWithSpecs(game.getWinnerName()),
					WordCodeDecode.codePolishWordToWordWithSpecs(game.getWordPlayer().getName()),
					WordCodeDecode.codePolishWordToWordWithSpecs(game.getGuessPlayer().getName()));
		} else
			return null;

	}
	
	@Override
	public String toString() {
		return String.format("player word: %s, player guess, %s, state: %s, word: %s",playerWordName, playerGuessName, gameStatus, theWord);
	}

}
