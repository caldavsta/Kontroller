package application;

public class PadAppException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3857040572602536467L;

	public PadAppException(String message){
		super(message);
	}
	
	public PadAppException(String message, Throwable throwable){
		super(message, throwable);
	}

}
