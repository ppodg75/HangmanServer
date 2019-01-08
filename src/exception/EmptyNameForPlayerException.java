package exception;

public class EmptyNameForPlayerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EmptyNameForPlayerException() {
		super("Empty name for player!");
	}

}
