package seproj.shrimpsnack.addon.sim;

public class SocketSIMConnectionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4683966344636285047L;

	public SocketSIMConnectionException() {
		super();
	}

	public SocketSIMConnectionException(String message) {
		super(message);
	}

	public SocketSIMConnectionException(Throwable cause) {
		super(cause);
	}

	public SocketSIMConnectionException(String message, Throwable cause) {
		super(message, cause);
	}

	public SocketSIMConnectionException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
