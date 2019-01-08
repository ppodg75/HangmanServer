package dto;

import java.io.Serializable;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import game.Game;

public class GameDto implements Serializable {

	private static final long serialVersionUID = 75264711556227767L;

	private String theWord;
	private String gappedWord;
	private int countMissed = 0;
	private String usedLetters;
	private String gameStatus;
	private String winner;

	public GameDto(String theWord, String gappedWord, int countMissed, String usedLetters, String gameStatus,
			String winner) {
		this.theWord = theWord;
		this.gappedWord = gappedWord;
		this.countMissed = countMissed;
		this.usedLetters = usedLetters;
		this.gameStatus = gameStatus;
		this.winner = winner;
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

	public static GameDto of(Game game) {
		if (game != null) {
			return new GameDto(game.getTheWord()
					, game.getGappedWord()
					, game.getCountMissed()
					, game.getUsedLetters()
					, game.getGameStatus().name()
					, game.getWinnerName()
					);
		} else
			return null;

	}

}
