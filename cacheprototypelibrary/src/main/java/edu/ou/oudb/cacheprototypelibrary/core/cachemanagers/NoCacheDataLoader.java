package edu.ou.oudb.cacheprototypelibrary.core.cachemanagers;

import android.content.Context;

import java.util.List;

import edu.ou.oudb.cacheprototypelibrary.connection.DataAccessProvider;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.DownloadDataException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.JSONParserException;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.QuerySegment;
import edu.ou.oudb.cacheprototypelibrary.utils.StatisticsManager;

public class NoCacheDataLoader extends DataLoader<Query,QuerySegment> {

	public NoCacheDataLoader(Context context, DataAccessProvider dataAccessProvider) {
		super(context,dataAccessProvider);
	}

	@Override
	public List<List<String>> load(Query query) throws DownloadDataException, JSONParserException {

        //<editor-fold desc="LOG newPosedQuery">
        StatisticsManager.newPosedQuery(query.toSQLString());
        //</editor-fold>
        //<editor-fold desc="LOG START QUERY PROCESS">
        long startProcessTime = StatisticsManager.startQueryProcess();
        //</editor-fold>
		
		QuerySegment result = mDataAccessProvider.process(query);

        //<editor-fold desc="LOG STOP QUERY PROCESS">
        StatisticsManager.stopQueryProcess(startProcessTime);
        //</editor-fold>
        //<editor-fold desc="LOG newProcessedQuery">
        StatisticsManager.newProcessedQuery();
        //</editor-fold>
		
		return result.getTuples();
		
	}

	@Override
	public void setUsingReplacement(boolean useReplacement) {
        /* we do not want any replacement, thus we override with this empty method */
	}

}
