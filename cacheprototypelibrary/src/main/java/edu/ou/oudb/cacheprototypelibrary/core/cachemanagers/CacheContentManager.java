package edu.ou.oudb.cacheprototypelibrary.core.cachemanagers;

import java.util.Collection;
import java.util.Set;

import edu.ou.oudb.cacheprototypelibrary.core.cache.Sizeable;

/**
 * @author Mikael Perrin
 * @since 1.0
 * ContentManager used to evaluate the estimation cache
 */
public interface CacheContentManager<K extends Sizeable,V extends Sizeable>{
	
	/**
	 * handle the bind of a key and its value to the cache
	 * @param key the key to be added
	 * @param value the value to be added
	 * @return true if inserted, false otherwise
	 */
	public V add(K key, V value);
	
	
	/**
	 * Method is used to analyze the cache and find 
	 * which entry corresponds to the given query
	 * @param key the sought query
	 * @return the result of the evaluation
	 */
	public Object lookup(K key);
	
	/**
	 * Check if a key is contained in the cache content
	 * @param key
	 * @return
	 */
	public boolean contains(K key);
	
	/**
	 * Retrieve directly an entry
	 * @param key
	 * @return the Value for this entry
	 */
	public V get(K key);
	
	/**
	 * Method is used to remove the given keys
	 * @param keys the keys to be removed.
	 * @return true if removed, false otherwise
	 */
	public boolean removeAll(Collection<K> keys);

    /**
     * remove an entry
     * @param key the entry-to-be-removed's key
     * @return true if removed, false otherwise
     */
	public boolean remove(K key);

	/**
	 * Removes all entries from cache
	 * @return true if cleared. false otherwise
	 */
	public boolean clear();

    /**
     * @return the set of keys
     */
	public Set<K> getEntrySet();

    /**
     * The size of the cache
     * @return the size in bytes
     */
	public long size();

    /**
     * count of segments
     * @return the number of segments
     */
	public int count();
	
}
