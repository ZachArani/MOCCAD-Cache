package edu.ou.oudb.cacheprototypelibrary.querycache.exception;

/**
 * @author Mikael Perrin
 * @since 1.0
 * Exception launched when a query trimming is 
 * found to be more complex than polynomial complexity.
 */
public class NPHardProblemException extends Exception {
	private static final long serialVersionUID = 1L;

	public NPHardProblemException() {
		super();
	}
	
	public NPHardProblemException(String detailedMessage) {
		super(detailedMessage);
	}
}
