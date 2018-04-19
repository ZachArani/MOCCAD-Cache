package edu.ou.oudb.cacheprototypelibrary;

import java.net.ConnectException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.util.Log;
import android.util.Pair;

import edu.ou.oudb.cacheprototypelibrary.core.cachemanagers.CacheResolutionManager;
import edu.ou.oudb.cacheprototypelibrary.core.process.Process;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.DownloadDataException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.JSONParserException;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.QuerySegment;

/**
 * @author Mikael Perrin
 * @since 1.0
 * ResolutionManager used to process the QueryCache if it is less expensive do so,
 * or on the cloud if the data are not available in cache, or if it is less expensive
 * to be processed on the cloud.
 */
public class SemanticQueryCacheResolutionManager implements CacheResolutionManager<Query, QuerySegment> {

    private Queue<Pair<Query, Process<Query, QuerySegment>>> mProcessQueue = new LinkedList<Pair<Query, Process<Query, QuerySegment>>>();

    @Override
    public QuerySegment process() throws ConnectException, DownloadDataException, JSONParserException {
        QuerySegment resultSegment = new QuerySegment();
        QuerySegment curSegment = null;
        Pair<Query, Process<Query, QuerySegment>> curP = null;

        //FIXME: There is a pb here if we do a query and the second one is: SELECT * FROM patients;
        while (!mProcessQueue.isEmpty()) {
            curP = mProcessQueue.poll();
            System.out.println("Query in SemanticQueryCacheResolutionManager: " + curP.first.toSQLString());
            /*curP.first is Query
			* curP.second is Process<Query,QuerySegment>*/
            curSegment = curP.second.run(curP.first);
            resultSegment.addAllTuples(curSegment);
        }
        Log.d("TEST",resultSegment.toString());
        return resultSegment;
    }
    @Override
    public boolean addProcess(Query key, Process<Query, QuerySegment> process) {
        return mProcessQueue.add(new Pair<Query, Process<Query, QuerySegment>>(key, process));
    }

}
