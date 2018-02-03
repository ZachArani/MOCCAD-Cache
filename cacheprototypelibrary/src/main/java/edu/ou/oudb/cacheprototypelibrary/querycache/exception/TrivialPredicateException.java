package edu.ou.oudb.cacheprototypelibrary.querycache.exception;

/**
 * @author Mikael Perrin
 * @since 1.0
 * Exception launched when a predicate is found to be trivial.
 * ex XopX or CopC
 */
public class TrivialPredicateException extends Exception {
	private static final long serialVersionUID = 1L;

	public TrivialPredicateException()
	{
		super();
	}
	
	public TrivialPredicateException(String detailedMessage) {
		super(detailedMessage);
	}
	
}
