package edu.ou.oudb.cacheprototypelibrary.querycache.exception;

/**
 * @author Mikael Perrin
 * @since 1.0
 * Exception launched when a predicated is found invalid
 */
public class InvalidPredicateException extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidPredicateException()
	{
		super();
	}
	
	public InvalidPredicateException(String detailedMessage) {
		super(detailedMessage);
	}
	
}
