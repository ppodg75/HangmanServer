package game;

import java.util.regex.Pattern;

public class Game {
	
	private Player[] players = new Player[2];	
	private int wordPlayer = 0;
	private String theWord;
	private String gappedWord;
	private long countUniqueLetters = 0;
	private int countHit = 0;
	private int countMissed = 0;
	private String usedLetters;
	private int maxMissedLetters;
	private GameStatus gameStatus = GameStatus.INIT;
	
	public Game(Player player1, Player player2) {
		players[0] = player1;
		players[1] = player2;
	}	

	public Player getWordPlayer() {
		return players[wordPlayer];
	}
	
	public Player getGuessPlayer() {
		return players[1-wordPlayer];
	}
	
	public void Start(String theWord, Player wordPlayer, int maxMissedLetters) {
		this.theWord = theWord.toUpperCase();
		this.wordPlayer = (wordPlayer == players[0])?0:1;
		this.maxMissedLetters = maxMissedLetters;
		init();
	}
	
	private void init() {
		countHit = 0;
		countMissed = 0;
		countUniqueLetters = countUniqueCharacters(theWord);
		gappedWord = gappedWord(theWord);
	}
	
	public static long countUniqueCharacters(String input) {
	   return input.chars().distinct().count();
	}
	
	public static String gappedWord(String w) {
		Pattern p = Pattern.compile(".",   Pattern.DOTALL );
	    return p.matcher(w).replaceAll("_");
	}	
	
	public boolean letterHasBeenUsed(Character letter) {
		return usedLetters.chars().anyMatch(letter::equals);
	}
	
	public boolean letterHit(Character letter) {
		return theWord.chars().anyMatch(letter::equals);
	}
	
	public boolean wordGuessed() {
		return gappedWord.equals(theWord);
	}
	
	public boolean missesReachMaximum() {
		return countMissed==maxMissedLetters;
	}	
	
	public LetterGuessResult tryLetter(Character letter) {		
		if (letterHasBeenUsed(letter)) {
			return LetterGuessResult.LETTER_USED;
		} else {
			usedLetters = new StringBuilder().append(usedLetters).append(letter).toString();
			if (letterHit(letter)) {
				countHit++;
				if (wordGuessed()) {
					getGuessPlayer().addPoints(countUniqueLetters);
					gameStatus = GameStatus.WIN;
				}
				return LetterGuessResult.LETTER_HIT;
			} else {
				countMissed++;
				if (missesReachMaximum()) {
					getWordPlayer().addPoints(1);
					gameStatus = GameStatus.LOST;
				}
				return LetterGuessResult.LETTER_MISSED;
			}				
		}
	}
	
	public GuessResult guess(Character letter) {
		LetterGuessResult result = tryLetter(letter);
		if (GameStatus.LOST == gameStatus) {
			getGuessPlayer().incLost();
		}
		if (GameStatus.WIN == gameStatus) {
			getGuessPlayer().incLost();
		}		
		GuessResult.Builder builder = GuessResult
										.builder()
										.letterGuessResult(result)
										.countMissed(countMissed)
										.countHit(countHit)
										.gameStatus(gameStatus)
										.gappedWord(gappedWord)
										;

		return builder.build();
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
}
