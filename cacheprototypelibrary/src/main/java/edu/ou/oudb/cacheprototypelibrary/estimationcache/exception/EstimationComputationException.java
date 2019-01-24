package edu.ou.oudb.cacheprototypelibrary.estimationcache.exception;

public class EstimationComputationException extends Exception{

	private static final long serialVersionUID = 1L;

	public EstimationComputationException()
	{
		super();
	}
	
	public EstimationComputationException(String detailedMessage) {
		super(detailedMessage);
	}
}
