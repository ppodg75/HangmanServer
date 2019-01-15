package game;

import java.time.LocalDateTime;
import java.util.Optional;

import utils.WordGenerator;

public class Game {

	private static final int MAX_MISSED_LETTERS = 8;
	private static final WordGenerator wordGenerator = new WordGenerator();
	private long gameId = 0;
	private static long seqGameId = 0;

	private Player[] players = new Player[2];
	private Player theWinner = null;
	private int wordPlayer = 0;
	private String theWord;
	private long countUniqueLetters = 0;
	private int countMissed = 0;
	private String usedLetters;
	private int maxMissedLetters;
	private GameStatus gameStatus = GameStatus.CREATED;
	private LocalDateTime lastActivity;

	public Game() {
		gameId = ++seqGameId;
		updateLastActivity();
	}

	private void updateLastActivity() {
		lastActivity = LocalDateTime.now();
		if (players[0] != null) {
			players[0].updateLastActivity();
		}
		if (players[1] != null) {
			players[1].updateLastActivity();
		}
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
		return Optional.ofNullable(theWinner).orElse(new Player("")).getName();
	}

	public long getGameId() {
		return gameId;
	}

	public LocalDateTime getLastActivity() {
		return lastActivity;
	}

	public void init() {
		System.out.println("Server.Game.init: ");
		theWord = "";
		theWinner = null;
		countMissed = 0;
		usedLetters = "";
		if (!getWordPlayer().isComputer()) {
			gameStatus = GameStatus.WAIT_FOR_WORD;
		} else {
			updateWord(wordGenerator.getNewWord());
		}
		
		getWordPlayer().setStatus(PlayerStatus.PLAYING);
		getGuessPlayer().setStatus(PlayerStatus.PLAYING);
		updateLastActivity();
	}

	public void updateWord(String theWord) {
		System.out.println("Server.Game.updateWord: " + theWord);
		this.theWord = theWord.toUpperCase();
		this.gameStatus = GameStatus.PLAY;
		initCounters();
		updateLastActivity();
	}

	private void initCounters() {
		maxMissedLetters = MAX_MISSED_LETTERS;
		countUniqueLetters = countUniqueCharacters(theWord);
	}

	public long countUniqueCharacters(String input) {
		return input.chars().distinct().count();
	}

	public boolean letterHit(char letter) {
		return theWord.chars().anyMatch(c -> letter == c);
	}

	public String getGappedWord() {
		if (theWord == null || theWord.isEmpty()) {
			return "";
		}
		StringBuilder gappedWord = new StringBuilder();
		for (int i = 0; i < theWord.length(); i++) {
			char wordChar = theWord.charAt(i);
			if (letterHasBeenUsed(wordChar)) {
				gappedWord.append(String.valueOf(wordChar));
			} else {
				gappedWord.append('_');
			}
		}
		return gappedWord.toString();
	}

	public boolean wordGuessed() {
		return getGappedWord().equals(theWord);
	}

	public boolean missesReachMaximum() {
		return countMissed == maxMissedLetters;
	}

	public boolean letterHasBeenUsed(char letter) {
		return usedLetters.chars().anyMatch(c -> c == letter);
	}

	public void tryLetter(char letter) {
		if (!letterHasBeenUsed(letter)) {
			usedLetters = new StringBuilder().append(usedLetters).append(letter).toString();
			if (letterHit(letter)) {
				if (wordGuessed()) {
					Player wordPlayer = getWordPlayer();
					Player guessPlayer = getGuessPlayer();
					guessPlayer.addPoints(countUniqueLetters);
					guessPlayer.incWin();
					getWordPlayer().incLost();
					theWinner = guessPlayer;
					guessPlayer.endGame();
					wordPlayer.endGame();
					gameStatus = GameStatus.END;
				}
			} else {
				countMissed++;
				if (missesReachMaximum()) {
					Player wordPlayer = getWordPlayer();
					Player guessPlayer = getGuessPlayer();
					wordPlayer.addPoints(countUniqueLetters);
					getGuessPlayer().incLost();
					wordPlayer.incWin();
					theWinner = wordPlayer;
					wordPlayer.endGame();
					guessPlayer.endGame();
					gameStatus = GameStatus.END;
				}
			}
		}
	}

	public void endGameBeforeBecouseOfPlayer(Player player) {
		Player guessPlayer = getGuessPlayer();
		Player wordPlayer = getWordPlayer();
		if (gameStatus == GameStatus.PLAY) {
			if (player == guessPlayer) {
				wordPlayer.addPoints(1);
			} else {
				guessPlayer.addPoints(countUniqueLetters);
			}
		}
		guessPlayer.endGame();
		wordPlayer.endGame();
		gameStatus = GameStatus.END;
	}

	public Game guessLetter(String letter) {
		updateLastActivity();
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
	
	public boolean withComputer() {
		return getWordPlayer().isComputer(); 
	}

	@Override
	public String toString() {
		return String.format("Game: wordPlayerName=%s, guessPlayerName=%s", getWordPlayer(), getGuessPlayer());
	}
}
