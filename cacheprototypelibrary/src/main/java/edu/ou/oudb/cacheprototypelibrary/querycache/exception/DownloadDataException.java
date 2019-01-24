package edu.ou.oudb.cacheprototypelibrary.querycache.exception;

public class DownloadDataException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public DownloadDataException()
	{
		super();
	}
	
	public DownloadDataException(String detailedMessage) {
		super(detailedMessage);
	}

}
