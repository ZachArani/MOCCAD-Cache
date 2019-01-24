package edu.ou.oudb.cacheprototypelibrary.core.cachemanagers;

import java.util.Collection;

import edu.ou.oudb.cacheprototypelibrary.core.cache.Sizeable;




/**
 * @author Mikael Perrin
 * @since 1.0
 * ReplacementManager used to insert a new EstimationCache entry in the cache
 * with respect to some replacement policy, HashMap is recommended
 */
public interface CacheReplacementManager<K extends Sizeable>{
	
	/**
	 * add the metadata used for the replacement policy
	 * @param key the key 
	 * @return true if added, false otherwise
	 */
	public boolean add(K key);

	/**
	 * add the metadata used for the replacement as well as a QEP score
	 * @param key the key
	 * @param score the QEP score to be attached
	 * @return true if added, false otherwise
	 */
	public boolean add(K key, double score);
	
	/**
	 * remove the metadata used for the replacement policy
	 * @param key the key 
	 * @return true if removed, false otherwise
	 */

    /**
     *
     * @param key
     * @param score
     * @param scoreModifier POINT to modify QEP score by for cache entry
     * @return
     */
	public boolean add(K key, double score, double scoreModifier);

	public boolean remove(K key);
	
	/**
	 * remove the metadata used for the replacement policy
	 * @param keys the keys 
	 * @return true if removed, false otherwise
	 */
	public boolean removeAll(Collection<K> keys);

	/**
	 * Clears the entire cache
	 * @return true if cleared, false otherwise
	 */
	public boolean clear();
	
	/**
	 * update the metadata used for the replacement policy
	 * @param key the key 
	 * @return true if updated, false otherwise
	 */
	public boolean update(K key);

	/**
	 *
	 * @param key
	 * @param scoreModifier update query with new score modifier
	 * @return
	 */
	public boolean update(K key, double scoreModifier);
	
	/**
	 * The method returning the entry to be replaced
	 * @return the key to be replaced in the content manager
	 */
	public K replace();
}
