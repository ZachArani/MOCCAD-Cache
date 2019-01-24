package edu.ou.oudb.cacheprototypelibrary.querycache.exception;

/**
 * @author Mikael Perrin
 * @since 1.0
 * Exception launched when a cycle is found within a graph
 */
public class CycleFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public CycleFoundException()
	{
		super();
	}
	
	public CycleFoundException(String detailedMessage) {
		super(detailedMessage);
	}
	
}
