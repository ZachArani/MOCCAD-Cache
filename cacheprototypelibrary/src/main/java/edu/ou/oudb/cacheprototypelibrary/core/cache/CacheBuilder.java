package edu.ou.oudb.cacheprototypelibrary.core.cache;

import edu.ou.oudb.cacheprototypelibrary.core.cachemanagers.CacheContentManager;
import edu.ou.oudb.cacheprototypelibrary.core.cachemanagers.CacheReplacementManager;
import edu.ou.oudb.cacheprototypelibrary.core.cachemanagers.CacheResolutionManager;


/**
 * @author Mikael Perrin
 * @since 1.0
 * Define an abstract CacheBuilder
 */
public class CacheBuilder<K extends Sizeable,V extends Sizeable> {

	/** Constant defined for an UNSET attribute */
	public final static int UNSET = -1;
	
	/** Constant defined for a DEFAULT_MAX_SIZE attribute */
	public final static int DEFAULT_MAX_SIZE = Integer.MAX_VALUE;
	
	/** Constant defined for a DEFAULT_MAX_SEGMENT attribute */
	public final static int DEFAULT_MAX_SEGMENT = Integer.MAX_VALUE;
	
	
	/** Maximum capacity for the cache */
	private int maxSize = UNSET;
	/** Maximum number of segments */
	private int maxSegment = UNSET;
	
	private CacheContentManager<K,V> mCContentManager = null;
	private CacheResolutionManager<K,V> mCResolutionManager = null;
	private CacheReplacementManager<K> mCReplacementManager = null;
	
	/**
	 * Creation of the cache builder
	 * @return a new instance of a cache builder
	 */
	public static <K extends Sizeable,V extends Sizeable> CacheBuilder<K,V> newBuilder()
	{
		return new CacheBuilder<K,V>();
	}
	
	/**
	 * Method used to build an estimation cache
	 * @return a new estimation cache
	 */
	public Cache<K,V> build()
	{
		return new Cache<K,V>(this);
	}
	
	/**
	 * setter for the CacheContentManager property
	 * @param inCCM the input cache content manager 
	 * @return the current instance of cache builder
	 */
	public final CacheBuilder<K,V> setCacheContentManager(CacheContentManager<K,V> inCCM)
	{
		mCContentManager = inCCM;
		return this;
	}
	
	/**
	 * setter for the CacheResolutionManager property
	 * @param inCResM the input cache resolution manager 
	 * @return the current instance of cache builder
	 */
	public final CacheBuilder<K,V> setCacheResolutionManager(CacheResolutionManager<K,V> inCResM)
	{
		mCResolutionManager = inCResM;
		return this;
	}
	
	/**
	 * setter for the CacheReplacementManager property
	 * @param inCRepM the input cache replacement manager 
	 * @return the current instance of cache builder
	 */
	public final CacheBuilder<K,V> setCacheReplacementManager(CacheReplacementManager<K> inCRepM)
	{
		mCReplacementManager = inCRepM;
		return this;
	}
	
	
	/**
	 * getter of CacheContentManager
	 * @return the instance of cache content manager
	 */
	final CacheContentManager<K,V> getCacheContentManager()
	{
		return mCContentManager;
	}
	
	/**
	 * getter of CacheResolutionManager
	 * @return the instance of cache resolution manager
	 */
	final CacheResolutionManager<K,V> getCacheResolutionManager()
	{
		return mCResolutionManager;
	}
	
	/**
	 * getter of CacheReplacementManager
	 * @return the instance of cache replacement manager
	 */
	final CacheReplacementManager<K> getCacheReplacementManager()
	{
		return mCReplacementManager;
	}
	
	/**
	 * @return the maxSize
	 */
	final int getMaxSize() {
		return (this.maxSize == UNSET)? DEFAULT_MAX_SIZE: this.maxSize;
	}
	
	/**
	 * @param maxSize the maxCapacity to set
	 */
	public final CacheBuilder<K, V> setMaxSize(final int maxSize) {
		if (this.maxSize != UNSET)
		{
			throw new IllegalStateException("Maximum count already set");
		}
		if (maxSize >= 0)
		{
			this.maxSize = maxSize;
		}
		else
		{
			throw new IllegalArgumentException("Maximum count cannot be negative");
		}
		
		return this;
	}
	
	/**
	 * @return the maxSegment
	 */
	final int getMaxCount() {
		return (this.maxSegment == UNSET)? DEFAULT_MAX_SEGMENT: this.maxSegment;
	}
	
	/**
	 * @param maxSegment the maxSegment to set
	 */
	public final CacheBuilder<K,V> setMaxSegment(int maxSegment) {
		if (maxSegment >= 0)
		{
			this.maxSegment = maxSegment;
		}
		else
		{
			throw new IllegalStateException("Maximum number of segment cannot be negative");
		}
		
		return this;
	}
	
	
	
	
}
