package edu.ou.oudb.cacheprototypelibrary.querycache.trimming;

import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;

/**
 * @author Mikael Perrin
 * @since 1.0
 * Definition of the Query trimming tool for the query cache
 */
public interface QueryCacheQueryTrimmer {
	
	/**
	 * @author Mikael Perrin
	 * @since 1.0
	 * Query trimming result for query cache
	 */
	public class QueryTrimmingResult{
		public QueryTrimmingType type = null;
		public Query probeQuery = null;
		public Query entryQuery = null;
		public Query remainderQuery = null;
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((entryQuery == null) ? 0 : entryQuery.hashCode());
			result = prime * result
					+ ((probeQuery == null) ? 0 : probeQuery.hashCode());
			result = prime
					* result
					+ ((remainderQuery == null) ? 0 : remainderQuery.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			QueryTrimmingResult other = (QueryTrimmingResult) obj;
			if (entryQuery == null) {
				if (other.entryQuery != null) {
					return false;
				}
			} else if (!entryQuery.equals(other.entryQuery)) {
				return false;
			}
			if (probeQuery == null) {
				if (other.probeQuery != null) {
					return false;
				}
			} else if (!probeQuery.equals(other.probeQuery)) {
				return false;
			}
			if (remainderQuery == null) {
				if (other.remainderQuery != null) {
					return false;
				}
			} else if (!remainderQuery.equals(other.remainderQuery)) {
				return false;
			}
			if (type != other.type) {
				return false;
			}
			return true;
		}
		
		
	}
	
	/**
	 * Method used to evaluate the cache
	 * @param q the query
	 * @param s the query cache segment
	 * @return the query trimming result
	 */
	public QueryTrimmingResult evaluate(Query inputQuery, Query segmentQuery);
	
}
