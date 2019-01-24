package edu.ou.oudb.cacheprototypelibrary.querycache.trimming;

/**
 * @author Mikael Perrin
 * @since 1.0
 * Enum used to know which time of cache hit/miss occured
 * Important:KEEP THE ITEMS IN THEIR DECREASING ORDER OF ACCURACY
 * THE LOWER VALUE THE ENUM IS, THE LESS COMPUTATION IT REQUIRES TO PROCESS THE CACHE
 */
public enum QueryTrimmingType {
	CACHE_HIT,
	CACHE_EXTENDED_HIT_EQUIVALENT,
	CACHE_EXTENDED_HIT_INCLUDED,
	CACHE_HORIZONTAL,
	CACHE_VERTICAL,
	CACHE_HYBRID,
	CACHE_MISS
}
