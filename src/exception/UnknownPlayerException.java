package exception;

public class UnknownPlayerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UnknownPlayerException(String name) {
		super("Unknown player with name: "+name);
	}
	
}
