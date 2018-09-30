package edu.ou.oudb.cacheprototypelibrary;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import android.support.annotation.Nullable;
import edu.ou.oudb.cacheprototypelibrary.core.cachemanagers.CacheReplacementManager;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;


/**
 * @author Mikael Perrin
 * @since 1.0
 * EstimationCacheContentManager with LRU replacement policy
 */
public class LRUCacheReplacementManager implements CacheReplacementManager<Query>{
	
	private Map<Query, LRUCacheEntry> mEntriesHashMap = null;
	
	private LRUCacheEntry begin;
	private LRUCacheEntry end;

	public LRUCacheReplacementManager()
	{
		mEntriesHashMap = new HashMap<Query, LRUCacheEntry>();
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public boolean update(Query q) {
		
		boolean ret = false;
		
		if (mEntriesHashMap.containsKey(q))
		{
			LRUCacheEntry curCacheEntry = mEntriesHashMap.get(q);
			
			if(end != curCacheEntry)
			{
				// if the entry is not the first one
				if (curCacheEntry.prev != null)
				{
					curCacheEntry.prev.next = curCacheEntry.next;
				}
				else
				{
					begin = curCacheEntry.next;
				}
				
				
				curCacheEntry.next.prev = curCacheEntry.prev;
				
				curCacheEntry.prev = end;
				end.next = curCacheEntry;
				end = curCacheEntry;
			}
			
			ret = true;
		}
		
		return ret;
	}

	/**
	 *
	 * @param q
	 * @param scoreModifier update query with new score modifier
	 * @return update(query)
	 */
	@Override
	public boolean update(Query q, double scoreModifier){
		return update(q);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public boolean add(Query q)
	{
		boolean ret;
		
		if (mEntriesHashMap.containsKey(q))
		{
			update(q);
			ret = false;
		}
		else
		{
			LRUCacheEntry cacheEntry = new LRUCacheEntry();
			cacheEntry.mQuery = q;
			
			if (end != null)
			{
				end.next = cacheEntry;
				cacheEntry.prev = end;
				cacheEntry.next = null;
				end = cacheEntry;
			}
			else
			{
				cacheEntry.next = null;
				cacheEntry.prev = null;
				begin = cacheEntry;
				end = cacheEntry;
			}
			
			mEntriesHashMap.put(q, cacheEntry);
			
			ret = true;
		}
		
		return ret;
	}

    /**
     * {@inheritDoc}
     */
	@Override
    public boolean add(Query key, double score){
	    return add(key);
    }

	/**
	 *
	 * @param key
	 * @param score
	 * @param scoreModifier POINT to modify QEP score by for cache entry
	 * @return result of add(key)
	 */
    @Override
    public boolean add(Query key, double score, double scoreModifier){
		return add(key);
	}


    /**
     * {@inheritDoc}
     */
	@Nullable
	@Override
	public Query replace() {	
		return begin.getQuery();
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public boolean remove(Query q) {
		
		boolean ret;
		
		if (!mEntriesHashMap.containsKey(q))
		{
			ret = false;
		}
		else // cannot be empty
		{
			LRUCacheEntry entryToRemove = mEntriesHashMap.get(q);
			
			if (entryToRemove == begin && entryToRemove == end)
			{
				begin = null;
				end = null;
			}
			else
			{
				if (entryToRemove != end)
				{
					entryToRemove.next.prev = entryToRemove.prev;
				}
				else
				{
					end = entryToRemove.prev;
				}
				
				if (entryToRemove != begin)
				{
					entryToRemove.prev.next = entryToRemove.next;
				}
				else
				{
					begin = entryToRemove.next;
				}
			}
			
			
			mEntriesHashMap.remove(q);
			
			ret = true;
		}
		
		return ret;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public boolean removeAll(Collection<Query> queries) {
		boolean removed = true;
		
		for(Query q: queries)
		{
			removed = remove(q);
		}
		
		return removed;
	}
	
	class LRUCacheEntry
	{
		private Query mQuery;
		public LRUCacheEntry prev;
		public LRUCacheEntry next;
		
		
		/**
		 * @return the query
		 */
		public final Query getQuery() {
			return this.mQuery;
		}
		
		/**
		 * @param q the query to set
		 */
		public final void setQuery(Query q) 
		{
			if (q != null)
			{
				this.mQuery = q;
			}
		}
	}

}
