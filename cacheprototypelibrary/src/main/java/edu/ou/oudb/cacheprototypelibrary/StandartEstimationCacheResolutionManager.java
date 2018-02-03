package edu.ou.oudb.cacheprototypelibrary;

import java.net.ConnectException;
import java.util.LinkedList;
import java.util.Queue;

import android.util.Pair;
import edu.ou.oudb.cacheprototypelibrary.core.cachemanagers.CacheResolutionManager;
import edu.ou.oudb.cacheprototypelibrary.core.process.Process;
import edu.ou.oudb.cacheprototypelibrary.estimationcache.Estimation;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.DownloadDataException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.JSONParserException;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;

/**
 * @author Mikael Perrin
 * @since 1.0
 * Class allowing to retrieve the estimation items in the estimation cache by getting the
 * value of the given query(key).
 */
public class StandartEstimationCacheResolutionManager implements CacheResolutionManager<Query,Estimation> {

	private Queue<Pair<Query,Process<Query,Estimation>>> mProcessQueue = new LinkedList<Pair<Query,Process<Query,Estimation>>>();
	
	@Override
	public Estimation process() throws ConnectException, DownloadDataException, JSONParserException {
		Estimation estimation = new Estimation();
		Estimation curEstimation = null;
		Pair<Query,Process<Query,Estimation>> curP = null;

		//FIXME: Same pb -> Look result of SOP
		while(!mProcessQueue.isEmpty())
		{
			curP = mProcessQueue.poll();
			curEstimation = curP.second.run(curP.first);
//			System.out.println("AH: " + curP.first.toSQLString());
			estimation.add(curEstimation);
		}

		return estimation;
	}

	

	@Override
	public boolean addProcess(Query query, Process<Query, Estimation> process) {
		return mProcessQueue.add(new Pair<Query,Process<Query,Estimation>>(query,process));
	}
}
