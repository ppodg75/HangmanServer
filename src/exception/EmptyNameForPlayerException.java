package exception;

public class EmptyNameForPlayerException extends RuntimeException {
	
	public EmptyNameForPlayerException() {
		super("Empty name for player!");
	}

}
