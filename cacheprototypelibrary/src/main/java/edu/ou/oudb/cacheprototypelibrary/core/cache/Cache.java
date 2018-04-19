package edu.ou.oudb.cacheprototypelibrary.core.cache;

import java.net.ConnectException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;

import edu.ou.oudb.cacheprototypelibrary.core.cachemanagers.CacheContentManager;
import edu.ou.oudb.cacheprototypelibrary.core.cachemanagers.CacheReplacementManager;
import edu.ou.oudb.cacheprototypelibrary.core.cachemanagers.CacheResolutionManager;
import edu.ou.oudb.cacheprototypelibrary.core.process.Process;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.DownloadDataException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.JSONParserException;

/**
 * @author Mikael Perrin
 * @since 1.0
 * Definition of the cache structure
 */
public class Cache<K extends Sizeable,V extends Sizeable>{

	/** The maximum count for the cache */
	protected int mMaxSize;
	
	/** The maximum number of segment for the cache */
	protected int mMaxCount;

	/** EstimationCacheContentManager */
	protected final CacheContentManager<K,V> mCContentManager;
	
	/** EstimationCacheResolutionManager */
	protected final CacheResolutionManager<K,V> mCResolutionManager;
	
	/** EstimationCacheReplacementManager */
	protected final CacheReplacementManager<K> mCReplacementManager;

    /**
     * Package visibility constructor
     * @param builder The Cache builder containing all the parameters to be set
     */
	Cache(CacheBuilder<K,V> builder) {
		mMaxSize = builder.getMaxSize();
		mMaxCount = builder.getMaxCount();
		mCContentManager = builder.getCacheContentManager();
		mCReplacementManager = builder.getCacheReplacementManager();
		mCResolutionManager = builder.getCacheResolutionManager();
	}

    /**
     * Cache look up method
     * @param key the sought key
     * @return the corresponding trimming result
     */
	public Object lookup(K key)
	{
		return mCContentManager.lookup(key);
	}

    /**
     * add a segment to the cache
     * @param key the key to be added
     * @param value the value to be added
     * @return The previous value for the key if it existed
     */
	public V add(K key, V value)
	{
		if(mCReplacementManager != null)
		{
			mCReplacementManager.add(key);
		}
		
		return mCContentManager.add(key, value);
	}


	public V add(K key, V value, double score)
	{
		return mCContentManager.add(key, value);
	}

    /**
     * remove all the given keys
     * @param keys the keys to be removed
     * @return true if removed, false otherwise
     */
	public boolean removeAll(Collection<K> keys)
	{
		if(mCReplacementManager != null)
		{
			mCReplacementManager.removeAll(keys);
		}
		
		return mCContentManager.removeAll(keys);
	}

    /**
     * remove the given key
     * @param key the key to be removed
     * @return true if removed, false otherwise
     */
	public boolean remove(K key)
	{
		if(mCReplacementManager != null)
		{
			mCReplacementManager.remove(key);
		}
		
		
		return mCContentManager.remove(key);
	}

    /**
     * retrieve the value for a given key
     * @param key the given key
     * @return the resulting value
     */
	public V get(K key)
	{
		if (mCReplacementManager != null)
		{
			mCReplacementManager.update(key);
		}
		
		return mCContentManager.get(key);
	}

    /**
     * add a process to the resolution manager's process queue
     * @param key the key for which the process is done (e.g. a query)
     * @param process the corresponding process
     * @return true if added, false otherwise
     */
	public boolean addProcess(K key, Process<K,V> process)
	{
		return mCResolutionManager.addProcess(key, process);
	}

    /**
     * add a list of processes to the resolution manager's process queue
     * @param processes the processes to be added
     * @return true if added, false otherwise
     */
	public boolean addProcesses(Map<K, Process<K,V>> processes)
	{
		boolean added = true;
		
		Iterator<Entry<K, Process<K, V>>> it = processes.entrySet().iterator();
		Entry<K, Process<K,V>> curProcess = null;
		while(it.hasNext() && added)
		{
			curProcess = it.next();
			
			added = addProcess(curProcess.getKey(),curProcess.getValue());
		}
		
		return added;
	}

    /**
     * Process the resolution manager processes
     * @return the result as a value type
     * @throws ConnectException
     * @throws DownloadDataException
     * @throws JSONParserException
     */
	public V process() throws ConnectException, DownloadDataException, JSONParserException
	{
		return mCResolutionManager.process();
	}

    /**
     * find which segment needs to be replaced according to the replacement policy
     * @return the key to be replaced in the content manager
     */
	public K replace()
	{
		return mCReplacementManager.replace();
	}

    /**
     * tells if a key is contained in the cache
     * @param key the looked up key
     * @return true if found, false otherwise
     */
	public boolean containsKey(K key)
	{
		return mCContentManager.contains(key);
	}


	public CacheContentManager<K,V> getCacheContentManager()
	{
		return mCContentManager;
	}
	
	public CacheReplacementManager<K> getCacheReplacementManager()
	{
		return mCReplacementManager;
	}
	
	public CacheResolutionManager<K,V> getCacheResolutionManager()
	{
		return mCResolutionManager;
	}

	
	/**
	 * Compute the count of the data contained in the cache,
	 * without the references, and object additional metadata sizes
	 * @return the memory count of the cache
	 */
	protected long size()
	{
		return mCContentManager.size();
	}

	protected int count()
	{
		return mCContentManager.count();
	}

    /**
     * Determines if the cache reached is maximum number of segments
     * @return true if full, false otherwise
     */
	public boolean isCountFull()
	{
		return (mCContentManager.count() >= mMaxCount);
	}

    /**
     * Setter for the maximum number of segment to be contained in the cache
     * @param maxCount the maximum number of segments
     */
	public void setMaxCount(int maxCount)
	{
		if (maxCount > 0)
		{
			while(count() > maxCount)
			{
				// remove the segment chosen by the replacementmanager
				remove(replace());
			}
			mMaxCount = maxCount;
		}
	}

    /**
     * Setter for the maximum cache byte size
     * @param maxSize the maximum size
     */
	public void setMaxSize(int maxSize)
	{
		if (maxSize > 0)
		{
			while(size() > maxSize)
			{
				// remove the segment chosen by the replacementmanager
				remove(replace());
			}
			mMaxSize = maxSize;
		}
	}

    /**
     * given a key value pair, it tells if it can be contained in cache or if it needs to call the
     * replacement manager (even if it means it has to be the only segment in the cache)
     * @param key the key to be added
     * @param value the value to be added
     * @return true if it can be contained, false otherwise
     */
	public boolean canContainSegment(K key, V value)
	{
		return (mMaxSize >= key.size()+value.size());
	}

    /**
     * given a key value pair, it tells if those can be added to the cache without overflowing the
     * cache and without deleting any segment
     * @param key the key to be added
     * @param value the value to be added
     * @return true if it the key and value overflow in the case they would be added,
     * false otherwise
     */
	public boolean wouldBeOverflowedBy(K key, V value)
	{
		return (count() + key.size() + value.size() > mMaxSize);
	}
}
