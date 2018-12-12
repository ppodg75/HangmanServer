package game;

public class GuessResult {
		
	private LetterGuessResult letterGuessResult;
	private GameStatus gameStatus;
	private String gappedWord;
	private long countUniqueLetters = 0;
	private int countHit = 0;
	private int countMissed = 0;	

	private GuessResult(Builder builder) {
		this.countHit = builder.countHit;
	}
	
	public static Builder builder( ) {
		return new GuessResult.Builder();
	}
	
	public static class Builder {		
		
		private LetterGuessResult letterGuessResult;
		private GameStatus gameStatus;
		private String gappedWord;
		private int countHit = 0;
		private int countMissed = 0;	
		
		private Builder() {			
		}
		
		public Builder countHit(int value) {
			this.countHit = value;
			return this;
		}
		
		public Builder gameStatus(GameStatus value) {
			this.gameStatus = value;
			return this;
		}
		
		public Builder letterGuessResult(LetterGuessResult value) {
			this.letterGuessResult = value;
			return this;
		}
		
		public Builder gappedWord(String value) {
			this.gappedWord = value;
			return this;
		}
		
		public Builder countMissed(int value) {
			this.countMissed = value;
			return this;
		}			
				
		
		public GuessResult build() {
			return new GuessResult(this);
		}
		
	}

}
