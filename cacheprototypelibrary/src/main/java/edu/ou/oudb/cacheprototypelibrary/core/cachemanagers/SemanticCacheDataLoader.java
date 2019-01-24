package edu.ou.oudb.cacheprototypelibrary.core.cachemanagers;

import android.content.Context;

import java.net.ConnectException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ou.oudb.cacheprototypelibrary.connection.DataAccessProvider;
import edu.ou.oudb.cacheprototypelibrary.core.cache.Cache;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.DownloadDataException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.JSONParserException;
import edu.ou.oudb.cacheprototypelibrary.querycache.process.CloudQueryProcess;
import edu.ou.oudb.cacheprototypelibrary.querycache.process.ExtendedHitInclusionMobileQueryProcess;
import edu.ou.oudb.cacheprototypelibrary.querycache.process.HitMobileQueryProcess;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.QuerySegment;
import edu.ou.oudb.cacheprototypelibrary.querycache.trimming.QueryCacheQueryTrimmer.QueryTrimmingResult;
import edu.ou.oudb.cacheprototypelibrary.querycache.trimming.QueryTrimmingType;
import edu.ou.oudb.cacheprototypelibrary.utils.StatisticsManager;

public class SemanticCacheDataLoader extends DataLoader<Query,QuerySegment> {

	private Cache<Query,QuerySegment> mQueryCache = null;
	private boolean mUsingReplacement = true;
		

	public SemanticCacheDataLoader(Context context, DataAccessProvider dataAccessProvider,Cache<Query,QuerySegment> queryCache, boolean useReplacement) {
		super(context, dataAccessProvider);
		
		mQueryCache = queryCache;
		mUsingReplacement = useReplacement;
				
	}

	@Override
	public List<List<String>> load(Query query) throws ConnectException, DownloadDataException, JSONParserException {

        QuerySegment result = null;
		QueryTrimmingResult qtResult = null;

        //<editor-fold desc="LOG newPosedQuery">
        StatisticsManager.newPosedQuery(query.toSQLString());
        //</editor-fold>
        //<editor-fold desc="LOG START QUERY PROCESS">
        long startQueryProcessTime = StatisticsManager.startQueryProcess();
        //</editor-fold>
        //<editor-fold desc="LOG START CACHE ANALYSIS">
        long startCacheAnalysisTime = StatisticsManager.startCacheAnalysis();
        //</editor-fold>

		// look up into cache to see what type of cache hit / miss we can do.
		qtResult = (QueryTrimmingResult) mQueryCache.lookup(query);

		switch(qtResult.type)
		{
		case CACHE_HIT:
            //<editor-fold desc="LOG newQueryCacheExactHit">
            StatisticsManager.newQueryCacheExactHit();
            //</editor-fold>
			mQueryCache.addProcess(query, new HitMobileQueryProcess(mQueryCache));
			break;
		case CACHE_EXTENDED_HIT_EQUIVALENT:
            //<editor-fold desc="LOG newQueryCacheExtendedHit">
            StatisticsManager.newQueryCacheExtendedHit();
            //</editor-fold>
			if (qtResult.entryQuery != null)
			{
				mQueryCache.addProcess(qtResult.entryQuery, new HitMobileQueryProcess(mQueryCache));
			}
			else
			{
				throw new IllegalArgumentException("CACHE_EXTENDED_HIT_EQUIVALENT: no probe query");
			}
			break;
		case CACHE_EXTENDED_HIT_INCLUDED:
            //<editor-fold desc="LOG newQueryCacheExtendedHit">
            StatisticsManager.newQueryCacheExtendedHit();
            //</editor-fold>
			if (qtResult.probeQuery != null && qtResult.entryQuery != null)
			{
				mQueryCache.addProcess(qtResult.probeQuery, new ExtendedHitInclusionMobileQueryProcess(qtResult.entryQuery,mQueryCache));
			}
			else
			{
				throw new IllegalArgumentException("CACHE_EXTENDED_HIT_INCLUDED: no probe query");
			}
			break;
		case CACHE_HORIZONTAL:
		case CACHE_VERTICAL:
		//<editor-fold desc="LOG newQueryCachePartialHit">
		StatisticsManager.newQueryCachePartialHit();
		//</editor-fold>
		if (qtResult.remainderQuery != null && qtResult.probeQuery != null && qtResult.entryQuery != null)
		{
			mQueryCache.addProcess(qtResult.probeQuery, new ExtendedHitInclusionMobileQueryProcess(qtResult.entryQuery,mQueryCache));
			mQueryCache.addProcess(qtResult.remainderQuery, new CloudQueryProcess(mDataAccessProvider));
		}
		else
		{
			throw new IllegalArgumentException("CACHE_PARTIAL_HIT: no probe query and/or remainder query");
		}
		break;
		case CACHE_HYBRID:
		//<editor-fold desc="LOG newQueryCachePartialHit">
		StatisticsManager.newQueryCachePartialHit();
		//</editor-fold>
		if (qtResult.remainderQuery != null && qtResult.probeQuery != null && qtResult.entryQuery != null && qtResult.remainderQuery2 != null)
		{
			mQueryCache.addProcess(qtResult.probeQuery, new ExtendedHitInclusionMobileQueryProcess(qtResult.entryQuery,mQueryCache));
			mQueryCache.addProcess(qtResult.remainderQuery, new CloudQueryProcess(mDataAccessProvider));
			mQueryCache.addProcess(qtResult.remainderQuery2, new CloudQueryProcess((mDataAccessProvider)));
		}
		else
		{
			throw new IllegalArgumentException("CACHE_PARTIAL_HIT: no probe query and/or remainder query");
		}
		break;
		case CACHE_MISS:
		//<editor-fold desc="LOG newQueryCacheMiss">
		StatisticsManager.newQueryCacheMiss();
		//</editor-fold>
		mQueryCache.addProcess(query, new CloudQueryProcess(mDataAccessProvider));
		break;
		default:
			throw new IllegalArgumentException("Case not handled exception");
		}


        //<editor-fold desc="LOG STOP CACHE ANALYSIS">
        StatisticsManager.stopCacheAnalysis(startCacheAnalysisTime);
        //</editor-fold>
        //<editor-fold desc="LOG START QUERY EXECUTION">
        long startQueryExecutionTime = StatisticsManager.startQueryExecution();
        //</editor-fold>
		
		//PROCESS THE QUERY PLAN
        //result = new QuerySegment();
		result = mQueryCache.process();

        //<editor-fold desc="LOG STOP QUERY EXECUTION">
        StatisticsManager.stopQueryExecution(startQueryExecutionTime);
        //</editor-fold>
        //<editor-fold desc="LOG START CACHE REPLACMENT">
        long startCacheReplacementTime = StatisticsManager.startCacheReplacement();
        //</editor-fold>
		
		// REPLACEMENT
		if(mUsingReplacement && !mQueryCache.containsKey(query))
		{
			if (mQueryCache.canContainSegment(query, result))
			{
				Set<Query> queriesToBeRemoved = new HashSet<Query>();
				//if an entry in cache contains the result of the probe query
				// we replace that entry with the most recent one
				if (qtResult.entryQuery != null
					&& (qtResult.type == QueryTrimmingType.CACHE_HORIZONTAL
						|| qtResult.type == QueryTrimmingType.CACHE_VERTICAL
						|| qtResult.type == QueryTrimmingType.CACHE_HYBRID))
				{
					queriesToBeRemoved.add(qtResult.entryQuery);
				}
				
				if (qtResult.entryQuery == null
				||	(qtResult.entryQuery != null
					&& qtResult.type != QueryTrimmingType.CACHE_EXTENDED_HIT_INCLUDED
					&& qtResult.type != QueryTrimmingType.CACHE_EXTENDED_HIT_EQUIVALENT))
				{
					while(mQueryCache.isCountFull())
					{
						queriesToBeRemoved.add(mQueryCache.replace());
					}
					
					mQueryCache.removeAll(queriesToBeRemoved);
					
					while(mQueryCache.wouldBeOverflowedBy(query, result))
					{
						mQueryCache.remove(mQueryCache.replace());
					}
					
					// INSERTION
					mQueryCache.add(query, result);
                    //<editor-fold desc="LOG NEW QUERY CACHE REPLACEMENT">
                    StatisticsManager.newQueryCacheReplacement();
                    //</editor-fold>
				}
			}
			//else we do not insert in cache.
		}

        //<editor-fold desc="LOG STOP CACHE REPLACEMENT">
        StatisticsManager.stopCacheReplacement(startCacheReplacementTime);
        //</editor-fold>
        //<editor-fold desc="LOG STOP QUERY PROCESS">
        StatisticsManager.stopQueryProcess(startQueryProcessTime);
        //</editor-fold>

        //<editor-fold desc="LOG newProcessedQuery">
        StatisticsManager.newProcessedQuery();
        //</editor-fold>

		return result.getTuples();
	}
	
	
	/**
	 * @return the useReplacement
	 */
	public boolean isUsingReplacement() {
		return this.mUsingReplacement;
	}

	/**
	 * @param useReplacement the useReplacement to set
	 */
	public void setUsingReplacement(boolean useReplacement) {
		this.mUsingReplacement = useReplacement;
	}
}
