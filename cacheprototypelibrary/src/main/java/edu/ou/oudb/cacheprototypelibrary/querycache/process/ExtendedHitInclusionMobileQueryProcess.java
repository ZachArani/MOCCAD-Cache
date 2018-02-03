package edu.ou.oudb.cacheprototypelibrary.querycache.process;

import edu.ou.oudb.cacheprototypelibrary.core.cache.Cache;
import edu.ou.oudb.cacheprototypelibrary.core.process.Process;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.QuerySegment;
import edu.ou.oudb.cacheprototypelibrary.utils.StatisticsManager;

public class ExtendedHitInclusionMobileQueryProcess implements Process<Query,QuerySegment>{

	private Query mCacheQuery = null;
	private Cache<Query,QuerySegment> mCache = null;
	
	/**
	 * Constructor
	 * @param cache the cache query on which to process the inputQuery
	 */
	public ExtendedHitInclusionMobileQueryProcess(Query cacheQuery, Cache<Query,QuerySegment> cache)
	{
		mCacheQuery = cacheQuery;
		mCache = cache;
	}
	
	@Override
	public QuerySegment run(Query inputQuery) {
		QuerySegment segmentToBeEvaluated = null;
		QuerySegment result = null;
		
		System.out.println("Run Extended Hit: " + inputQuery.toSQLString());
		System.out.println("On Segment: " + mCacheQuery.toSQLString());
		
		segmentToBeEvaluated = mCache.get(mCacheQuery);
		
		if (segmentToBeEvaluated != null)
		{
            //<editor-fold desc="LOG START MOBILE PROCESS">
            long startMobileProcessTime = StatisticsManager.startMobileProcess();
            //</editor-fold>
			
			result = segmentToBeEvaluated.filter(inputQuery);

            //<editor-fold desc="LOG STOP MOBILE PROCESS">
            StatisticsManager.stopMobileProcess(startMobileProcessTime);
            //</editor-fold>
            //<editor-fold desc="LOG newQueryProcessedOnMobile">
            StatisticsManager.newQueryProcessedOnMobile();
            //</editor-fold>
		}
		
		
		
		return result;
	}

}
