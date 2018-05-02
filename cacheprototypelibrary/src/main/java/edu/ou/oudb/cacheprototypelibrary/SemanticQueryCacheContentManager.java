package edu.ou.oudb.cacheprototypelibrary;

import android.util.Log;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.ou.oudb.cacheprototypelibrary.core.cachemanagers.CacheContentManager;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.QuerySegment;
import edu.ou.oudb.cacheprototypelibrary.querycache.trimming.QueryCacheQueryTrimmer;
import edu.ou.oudb.cacheprototypelibrary.querycache.trimming.QueryCacheQueryTrimmer.QueryTrimmingResult;
import edu.ou.oudb.cacheprototypelibrary.querycache.trimming.QueryTrimmingType;
import edu.ou.oudb.cacheprototypelibrary.querycache.trimming.RenEtAlQueryCacheQueryTrimmer;

/**
 * @author Mikaël Perrin
 * @since 1.0
 * Definition of a Content Manager for the query cache defined ine the semantic cache
 */
public class SemanticQueryCacheContentManager implements CacheContentManager<Query,QuerySegment>{

	private Map<Query, QuerySegment> mSegments = null;
	private RenEtAlQueryCacheQueryTrimmer mTrimmer = null;
	
	// count do not consider structure wrapping data
	private long mSize = 0;
	
	public SemanticQueryCacheContentManager()
	{
		mSegments = new HashMap<Query, QuerySegment>();
		mTrimmer = new RenEtAlQueryCacheQueryTrimmer();
	}

	@Override
	public QuerySegment add(Query query, QuerySegment segment) {
		
		mSize += query.size();
		mSize += segment.size();
		
		return mSegments.put(query, segment);
	}
	
	
	@Override
	public QueryTrimmingResult lookup(Query query) {
		QueryCacheQueryTrimmer.QueryTrimmingResult curResult = new QueryCacheQueryTrimmer.QueryTrimmingResult();
		QueryCacheQueryTrimmer.QueryTrimmingResult bestResult = new QueryCacheQueryTrimmer.QueryTrimmingResult();
		
		Entry<Query, QuerySegment> curSegment;
		Iterator<Entry<Query, QuerySegment>> it = mSegments.entrySet().iterator();
		curResult.type = QueryTrimmingType.CACHE_MISS;
		
		if (mSegments.containsKey(query)) //quick access
		{
			bestResult.type = QueryTrimmingType.CACHE_HIT;
			bestResult.probeQuery = query;
		}
		else
		{
			bestResult.type = QueryTrimmingType.CACHE_MISS;
			bestResult.remainderQuery = query;
		}
		while (it.hasNext() && bestResult.type != QueryTrimmingType.CACHE_HIT)
		{
			curSegment = it.next();
			curResult = mTrimmer.evaluate(query,curSegment.getKey());
			
			// if accuracy is better 
			if (curResult.type.ordinal() < bestResult.type.ordinal())
			{
				switch(curResult.type)
				{
				case CACHE_HIT: // never happens
					bestResult = curResult;
					break;
				case CACHE_EXTENDED_HIT_EQUIVALENT:
					bestResult = curResult;
					break;
				case CACHE_EXTENDED_HIT_INCLUDED:
					// if the old result type is not a cache_extended_hit
					// or if the new entry has less tuples to be processed
					if (bestResult.type != QueryTrimmingType.CACHE_EXTENDED_HIT_INCLUDED
						||(	bestResult.type == QueryTrimmingType.CACHE_EXTENDED_HIT_INCLUDED
							&& mSegments.get(curResult.probeQuery).getNbTuples()
								< mSegments.get(bestResult.probeQuery).getNbTuples()))
					{
						bestResult = curResult;
					}
					break;
				case CACHE_HORIZONTAL:
					// if the new entry has more tuples that can be taken from cache
					if (bestResult.type != QueryTrimmingType.CACHE_HORIZONTAL
						||	(bestResult.type == QueryTrimmingType.CACHE_HORIZONTAL
					&& mSegments.get(curResult.probeQuery).getNbTuples()
						> mSegments.get(bestResult.probeQuery).getNbTuples()))
					{
						bestResult = curResult;
					}
					break;
				case CACHE_VERTICAL:
					// if the new entry has more tuples that can be taken from cache
					if (bestResult.type != QueryTrimmingType.CACHE_VERTICAL
							||	(bestResult.type == QueryTrimmingType.CACHE_VERTICAL
							&& mSegments.get(curResult.probeQuery).getNbTuples()
							> mSegments.get(bestResult.probeQuery).getNbTuples()))
					{
						bestResult = curResult;
					}
					break;
				case CACHE_HYBRID:
					// if the new entry has more tuples that can be taken from cache
					if (bestResult.type != QueryTrimmingType.CACHE_HYBRID
							||	(bestResult.type == QueryTrimmingType.CACHE_HYBRID
							&& mSegments.get(curResult.probeQuery).getNbTuples()
							> mSegments.get(bestResult.probeQuery).getNbTuples()))
					{
						bestResult = curResult;
					}
					break;
				case CACHE_MISS:
					break;
				}
			
			} // end if
			
		} // end while
		
		return bestResult;
	}

	@Override
	public QuerySegment get(Query query) {
		
		return mSegments.get(query);
	}

	@Override
	public boolean remove(Query q) {
		QuerySegment removedQuerySegment = null;
		boolean removed = false;
		
		if ((removedQuerySegment = mSegments.remove(q)) != null)
		{
			mSize -= q.size();
			mSize -= removedQuerySegment.size();
			removed = true;
		}
		
		return removed;
	}
	
	@Override
	public boolean removeAll(Collection<Query> queries) {
		boolean removed = false;
		
		for (Query q: queries)
		{
			removed = remove(q);
			if (removed == false)
				return false;
		}
		
		return removed;
	}

	@Override
	public long size() {
		
		Log.d("BYTE_SIZE", "" + mSize);
		
		return mSize;
	}

	@Override
	public int count() {
		int size = mSegments.size();
		Log.d("NB SEGMENT", "" + size);
		return size;
	}

	public void decCount(){
		mSegments.size();
	}

	@Override
	public Set<Query> getEntrySet() {
		return mSegments.keySet();
	}

	@Override
	public boolean contains(Query key) {
		return mSegments.containsKey(key);
	}


}
