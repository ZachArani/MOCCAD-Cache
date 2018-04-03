package edu.ou.oudb.cacheprototypelibrary;

import android.support.annotation.Nullable;

import edu.ou.oudb.cacheprototypelibrary.core.cachemanagers.CacheReplacementManager;

import java.util.Collection;
import java.util.PriorityQueue;



import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;


/**
 * @author Zachary Arani
 * @since 2.0
 * CacheReplacementManager that used the created Least Frequently Used with respect to Size and QEP QEPScore (LFUSQEP)
 */
public class LFUSQEPCacheReplacementManager implements CacheReplacementManager<Query>{

    private PriorityQueue<LFUSQEPCacheEntry> mEntriesPriorityQueue = null;

    private int lastReset = 0; //How many hits since the last Frequency Reset

    public LFUSQEPCacheReplacementManager() { mEntriesPriorityQueue = new PriorityQueue<LFUSQEPCacheEntry>(); }


    @Override
    public boolean update(Query q) //Update Frequency
    {
        boolean ret = false;

        if (mEntriesPriorityQueue.contains(q))
        {
            LFUSQEPCacheEntry curCacheEntry = getEntry(q);
            curCacheEntry.incUseInPeriod(); //Increments UseInPeriod
            curCacheEntry.setFrequency(curCacheEntry.getUseInPeriod()/lastReset); //Sets frequency to how many times it's been used in last period
            curCacheEntry.setScore();
            mEntriesPriorityQueue.add(curCacheEntry);
            ret = true;
        }

        return ret;
    }

    @Override
    public boolean add(Query q)
    {
        boolean ret;

        if (mEntriesPriorityQueue.containsKey(q))
        {
            update(q);
            ret = false;
        }
        else
        {
            LFUSQEPCacheEntry cacheEntry = new LFUSQEPCacheEntry();
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

            mEntriesPriorityQueue.put(q, cacheEntry);

            ret = true;
        }

        return ret;
    }

    @Nullable
    @Override
    public Query replace() {
        return begin.getQuery();
    }


    @Override
    public boolean remove(Query q) {

        boolean ret;

        if (!mEntriesPriorityQueue.containsKey(q))
        {
            ret = false;
        }
        else // cannot be empty
        {
            LFUSQEPCacheEntry entryToRemove = mEntriesPriorityQueue.get(q);

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


            mEntriesPriorityQueue.remove(q);

            ret = true;
        }

        return ret;
    }

    @Override
    public boolean removeAll(Collection<Query> queries) {
        boolean removed = true;

        for(Query q: queries)
        {
            removed = remove(q);
        }

        return removed;
    }

    /**
     * Removes and returns the desired entry from the Priority Queue or null otherwise.
     * @param q The Query to search by
     * @return CacheEntry which is passed
     */
    private LFUSQEPCacheEntry getEntry(Query q){
        for(int k = 0; k<mEntriesPriorityQueue.size(); k++){
            LFUSQEPCacheEntry temp = mEntriesPriorityQueue.poll();
            if(temp.getQuery().equals(q))
                return temp;
            mEntriesPriorityQueue.add(temp);
        }
        return null; //Not found
    }

    class LFUSQEPCacheEntry
    {
        private Query mQuery;
        public LFUSQEPCacheEntry prev;
        public LFUSQEPCacheEntry next;
        private double QEPScore = 0; //QEP Score
        private double frequency = 0; //Frequency using LFUPP (Least Frequently used with respect to periodicity and priority
        private int useInPeriod = 0; //How many times this query has been hit in this frequency temporality
        private double score = 0; //The actual score we use for cache Management. Formulated as (Frequency * QEP Score)/Size
        /**
         * @return the query
         */
        public final Query getQuery() {
            return this.mQuery;
        }

        /**
         * @param mQuery the query to set
         */
        public final void setQuery(Query q)
        {
            if (q != null)
            {
                this.mQuery = q;
            }
        }

        /**
         *
         * @return the QEP QEPScore
         */
        public final double getQEPScore()
        {
            return QEPScore;
        }

        /**
         *
         * @param the QEP QEPScore to set
         */
        public final void setQEPScore(double qep)
        {
            QEPScore = qep;
        }


        /**
         *
         * @return the frequency of the query using LFUPP (Least Frequently used with respect to Periodicity and Priority)
         */
        public final double getFrequency()
        {
            return frequency;
        }

        /**
         *
         * @param set the frequency of the query using LFUPP (Least Frequently used with respect to Periodicity and Priority)
         */
        public final void setFrequency(double f)
        {
            frequency = f;
        }

        /**
         *
         * @return how many times this query has been used in the current period
         */
        public final int getUseInPeriod()
        {
            return useInPeriod;
        }

        /**
         * Increments the useInPeriod variable by one
         */
        public final void incUseInPeriod()
        {
            useInPeriod++;
        }

        /**
         * Clears the useInPeriod variable
         */
        public final void clearUseInPeriod()
        {
            useInPeriod = 0;
        }

        /**
         *
         * @return the algorithim's score. Formulated as (Frequency * QEP)/Size
         */
        public final double getScore()
        {
            return score;
        }

        /**
         * Sets the score based on currently existing frequency, size, and QEP Score as formulate (Frequency * QEP Score)/Size
         */
        public final void setScore()
        {
            score = (frequency * QEPScore)/mQuery.size();
        }
        /**
         *
         * @param first cache entry
         * @param second cache entry
         * @return the difference between the first and second cache entry's QEP QEPScore
         */
        public int compare(LFUSQEPCacheEntry first, LFUSQEPCacheEntry second)
        {
            return (int)(first.getQEPScore()-second.getQEPScore());
        }
    }

}
