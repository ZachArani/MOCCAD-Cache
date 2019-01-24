package edu.ou.oudb.cacheprototypelibrary.querycache.process;

import edu.ou.oudb.cacheprototypelibrary.connection.DataAccessProvider;
import edu.ou.oudb.cacheprototypelibrary.core.process.Process;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.DownloadDataException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.JSONParserException;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.QuerySegment;
import edu.ou.oudb.cacheprototypelibrary.utils.StatisticsManager;


public class CloudQueryProcess implements Process<Query,QuerySegment> {

	
	private DataAccessProvider mDataAccessProvider = null;
	
	public CloudQueryProcess(DataAccessProvider dataAccessProvider)
	{
		mDataAccessProvider = dataAccessProvider;
	}

	//FIXME: Query is not correct, see SemanticQueryCacheResolutionManager and StandardEstimationCacheResolutionManager
	@Override
	public QuerySegment run(Query query) throws DownloadDataException, JSONParserException {
		
		// statistics are computed in the Cloud data access provider
		QuerySegment segment = mDataAccessProvider.process(query);

        StatisticsManager.newQueryProcessedOnCloud();
		
		return segment;
	}

}
