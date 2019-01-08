package game;

import java.util.Optional;

public class Game {
	
	private static final int MAX_MISSED_LETTERS = 8;

	private Player[] players = new Player[2];
	private Player theWinner = null;
	private int wordPlayer = 0;
	private String theWord;	
	private long countUniqueLetters = 0;
	private int countMissed = 0;
	private String usedLetters;
	private int maxMissedLetters;
	private GameStatus gameStatus = GameStatus.CREATED;

	public Game() {
	}

	public String getTheWord() {
		return theWord;
	}

	public int getCountMissed() {
		return countMissed;
	}

	public String getUsedLetters() {
		return usedLetters;
	}

	public GameStatus getGameStatus() {
		return gameStatus;
	}
	
	public void setWordPlayer(Player player) {
		players[wordPlayer] = player;
	}
	
	public void setGuessPlayer(Player player) {
		players[1 - wordPlayer] = player;
	}	

	public Player getWordPlayer() {
		return players[wordPlayer];
	}

	public Player getGuessPlayer() {
		return players[1 - wordPlayer];
	}
	
	public Player getWinner() {
		return theWinner;
	}
	
	public String getWinnerName() {
		return Optional.ofNullable(theWinner).orElse( new Player("") ).getName();
	}
		
	public void init(boolean replay) {
		if (!getWordPlayer().isComputer()) {
			if (replay) { wordPlayer = 1 - wordPlayer; }
			theWord = "";
			gameStatus = GameStatus.WAIT_FOR_WORD;
		} else {
			theWord = WordGenerator.getNewWord();
			gameStatus = GameStatus.PLAY;
		}	
		theWinner = null;
		getWordPlayer().setStatus(PlayerStatus.PLAYING);
		getGuessPlayer().setStatus(PlayerStatus.PLAYING);
	}

	public void updateWord(String theWord) {
		this.theWord = theWord.toUpperCase();	
		initCounters();
	}

	private void initCounters() {
		maxMissedLetters = MAX_MISSED_LETTERS;
		countMissed = 0;
		usedLetters = "";
		countUniqueLetters = countUniqueCharacters(theWord);		
	}

	public long countUniqueCharacters(String input) {
		return input.chars().distinct().count();
	}

	public String getGappedWord() {
		if (theWord == null || theWord.isEmpty()) {
			return "";
		}
		StringBuilder gappedWord = new StringBuilder();
		theWord.chars().forEach( wordChar -> {
			  if (letterHasBeenUsed(wordChar)) {
				  gappedWord.append(wordChar); 
			  } else {
				  gappedWord.append('_');
			  }  
		});
		return gappedWord.toString();
	}

	public boolean letterHasBeenUsed(int letter) {
		return usedLetters.chars().anyMatch(c -> c==letter);
	}

	public boolean letterHit(int letter) {
		return theWord.chars().anyMatch( c -> letter==c );
	}

	public boolean wordGuessed() {
		return getGappedWord().equals(theWord);
	}

	public boolean missesReachMaximum() {
		return countMissed == maxMissedLetters;
	}

	public void tryLetter(Character letter) {
		if (!letterHasBeenUsed(letter)) {
			usedLetters = new StringBuilder().append(usedLetters).append(letter).toString();
			if (letterHit(letter)) {				
				if (wordGuessed()) {
					getGuessPlayer().addPoints(countUniqueLetters);
					getGuessPlayer().incWin();
					getWordPlayer().incLost();
					theWinner = getGuessPlayer();
					gameStatus = GameStatus.END;
				}
			} else {
				countMissed++;
				if (missesReachMaximum()) {
					getWordPlayer().addPoints(1);
					getGuessPlayer().incLost();
					getWordPlayer().incWin();
					theWinner = getWordPlayer();
					gameStatus = GameStatus.END;
				}
			}
		}
	}

	public Game guessLetter(String letter) {
		if (!letter.isEmpty()) {
			tryLetter(letter.charAt(0));
		}
		return this;
	}

	public boolean playerIn(Player player) {
		return players[0] == player || players[1] == player;
	}

	public Player getOpponent(Player player) {
		if (players[0] == player) {
			return players[1];
		} else if (players[1] == player) {
			return players[0];
		} else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return String.format("Game: wordPlayerName=%s, guessPlayerName=%s", getWordPlayer(), getGuessPlayer());
	}
}
