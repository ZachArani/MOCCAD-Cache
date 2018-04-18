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


    /**
     * {@inheritDoc}
     */
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
            mEntriesPriorityQueue.add(curCacheEntry); //Put back into the queue
            ret = true;
        }

        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(Query q, double score)
    {
        boolean ret;

        if (mEntriesPriorityQueue.contains(q))
        {
            update(q);
            ret = false;
            return ret;
        }

        LFUSQEPCacheEntry cacheEntry = new LFUSQEPCacheEntry();
        cacheEntry.setQuery(q);
        cacheEntry.setQEPScore(score);
        cacheEntry.incUseInPeriod();
        cacheEntry.setScore();
        mEntriesPriorityQueue.add(cacheEntry);
        ret = true;

        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(Query q)
    {
        boolean ret = false;
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public Query replace() {
        return mEntriesPriorityQueue.peek().getQuery();
    }

    /**
     * @param q Kind of a misnomer. By the nature of the LFUSQEP, we only care to dequeue the front of the priority queue for each remove, irrespective of query requested.
     */
    @Override
    public boolean remove(Query q) {

        boolean ret;

        if (!mEntriesPriorityQueue.contains(q))
        {
            ret = false;
            return ret;
        }
        else // cannot be empty
        {
            mEntriesPriorityQueue.poll();
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
         * @param q the query to set
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
         * @param qep the QEP QEPScore to set
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
         * @param f set  the frequency of the query using LFUPP (Least Frequently used with respect to Periodicity and Priority)
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
