package exception;

public class UnknownPlayerException extends RuntimeException {
	
	public UnknownPlayerException(String name) {
		super("Unknown player with name: "+name);
	}
	
}
