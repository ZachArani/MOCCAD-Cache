package edu.ou.oudb.cacheprototypelibrary.querycache.exception;

public class JSONParserException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public JSONParserException()
	{
		super();
	}
	
	public JSONParserException(String detailedMessage) {
		super(detailedMessage);
	}

}
