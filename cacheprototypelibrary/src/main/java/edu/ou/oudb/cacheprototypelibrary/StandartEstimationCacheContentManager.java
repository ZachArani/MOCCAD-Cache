package edu.ou.oudb.cacheprototypelibrary;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.ou.oudb.cacheprototypelibrary.core.cachemanagers.CacheContentManager;
import edu.ou.oudb.cacheprototypelibrary.estimationcache.Estimation;
import edu.ou.oudb.cacheprototypelibrary.estimationcache.trimming.EstimationTrimmingType;
import edu.ou.oudb.cacheprototypelibrary.metadata.ObjectSizer;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;

/**
 * @author Mikael Perrin
 * @since 1.0
 * The ContentManager used to analyze the Estimation Cache by checking whether or not
 * the query is contained in the estimation cache keys.
 */
public class StandartEstimationCacheContentManager implements CacheContentManager<Query,Estimation>{
	
	private Map<Query, Estimation> mSegments = null;

	private long mSize = 0;
	
	public StandartEstimationCacheContentManager()
	{
		mSegments = new HashMap<Query,Estimation>();
		mSize = ObjectSizer.getObjectSize32bits();
	}
	
	@Override
	public Estimation add(Query query, Estimation segment) {
		
		mSize += query.size();
		mSize += segment.size();
		
		return mSegments.put(query, segment);
	}


	@Override
	public EstimationTrimmingResult lookup(Query query) {
		EstimationTrimmingResult result = new EstimationTrimmingResult();
		
		if (mSegments.containsKey(query))
		{
			result.type = EstimationTrimmingType.CACHE_HIT;			
		}
		else
		{
			result.type = EstimationTrimmingType.CACHE_MISS;
		}

		return result;
	}
	
	@Override
	public Estimation get(Query query) {
		return mSegments.get(query);
	}
	
	@Override
	public boolean remove(Query q)
	{
		Estimation removedEstimation = null;
		boolean removed = false;
		if((removedEstimation = mSegments.remove(q)) != null)
		{
			mSize -= removedEstimation.size();
			mSize -= q.size();
			removed = true;
		}
		return removed;
	}
	
	@Override
	public boolean removeAll(Collection<Query> queries) {
		boolean removed = true;
		
		for(Query q: queries)
		{
			removed = (removed && remove(q));
		}
		
		return removed;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean clear(){
		mSize = 0;
		mSegments.clear();
		return true;
	}
	/**
	 * @author Mikael Perrin
	 * @since 1.0
	 * TrimmingResult returned after lookup on estimation cache
	 */
	public class EstimationTrimmingResult {
		/** type of trimming for the estimation */
		public EstimationTrimmingType type;
	}

	@Override
	public long size() {
		return mSize;
	}

	@Override
	public int count() {
		return mSegments.size();
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
