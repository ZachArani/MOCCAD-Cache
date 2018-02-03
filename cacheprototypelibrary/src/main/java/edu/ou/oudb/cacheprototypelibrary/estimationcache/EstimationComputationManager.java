package edu.ou.oudb.cacheprototypelibrary.estimationcache;

import java.net.ConnectException;

import android.support.annotation.Nullable;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.DownloadDataException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.JSONParserException;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;

/**
 * @author Mikael Perrin
 * @since 1.0
 * Used to estimate the cost to process a query
 */
public interface EstimationComputationManager {
	
	/**
	 * Determine money time and energy consumption for the given query
	 * @param q the query to be processed
	 * @return the estimation result or null if impossible to estimate
	 * @throws java.net.ConnectException
	 * @throws DownloadDataException 
	 * @throws JSONParserException 
	 */
	@Nullable
	public Estimation estimate(Query q) throws ConnectException, JSONParserException, DownloadDataException;
}
