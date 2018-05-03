package edu.ou.oudb.cacheprototypelibrary;

import android.support.annotation.Nullable;

import edu.ou.oudb.cacheprototypelibrary.core.cachemanagers.CacheReplacementManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.PriorityQueue;
import android.util.Log;



import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;


/**
 * @author Zachary Arani
 * @since 2.0
 * CacheReplacementManager that used the created Least Frequently Used with respect to Size and QEP QEPScore (LFUSQEP)
 */
public class LFUSQEPCacheReplacementManager implements CacheReplacementManager<Query>{

    private PriorityQueue<LFUSQEPCacheEntry> mEntriesPriorityQueue = null;

    private double lastReset = 0; //How many hits since the last Frequency Reset
    private double hitsUntilRestart = 10; //How many hits need to happen before lastReset rolls back to 0

    public LFUSQEPCacheReplacementManager() {Log.i("LFUSQEP", "STARTED NEW MANAGER"); mEntriesPriorityQueue = new PriorityQueue<LFUSQEPCacheEntry>(); }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean update(Query q) //Update Frequency
    {
        LFUSQEPCacheEntry qHolder = new LFUSQEPCacheEntry();
        qHolder.setQuery(q);
        boolean inQueue = mEntriesPriorityQueue.contains(qHolder);
        boolean ret = false;
        Log.i("LFUSQEP","Updating Query");
        if (mEntriesPriorityQueue.contains(qHolder))
        {
            lastReset = (lastReset <=hitsUntilRestart) ? ++lastReset : 0; //if lastRest is less than or  equal to hitsUntilRestart, then increment, Else set it back to zero.
            LFUSQEPCacheEntry curCacheEntry = getEntry(q);
            curCacheEntry.incUseInPeriod(); //Increments UseInPeriod
            mEntriesPriorityQueue.add(curCacheEntry); //Put back into the queue
            updateFrequencies(); //Update all scores/Frequencies in cache
            updateScores();
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
        Log.i("LFUSQEP CACHE", "ADDING QUERY");
        LFUSQEPCacheEntry qHolder = new LFUSQEPCacheEntry();
        qHolder.setQuery(q);
        boolean ret;
        if (mEntriesPriorityQueue.contains(qHolder))
        {
            update(q);
            ret = false;
            return ret;
        }
        lastReset = (lastReset <= hitsUntilRestart) ? ++lastReset : 0; //if lastRest is less than or  equal to hitsUntilRestart, then increment, Else set it back to zero.
        LFUSQEPCacheEntry cacheEntry = new LFUSQEPCacheEntry();
        cacheEntry.setQuery(q);
        cacheEntry.setQEPScore(score);
        cacheEntry.incUseInPeriod();
        mEntriesPriorityQueue.add(cacheEntry);
        updateFrequencies(); //Now that we've added to the cache, update all frequencies/scores
        updateScores();

        ret = true;

        return ret;
    }
//TODO: ADD SET SCORE AND SET FREQUENCY THAT UPDATE THOSE FOR EVERYTHING IN THE QUEUE
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
        Log.i("LFUSQEP QUERY", "REMOVING QUERY");
        LFUSQEPCacheEntry qHolder = new LFUSQEPCacheEntry();
        qHolder.setQuery(q);
        if (!mEntriesPriorityQueue.contains(qHolder))
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
        ArrayList<LFUSQEPCacheEntry> queryHolder = new ArrayList<LFUSQEPCacheEntry>(); //Holds queries while we iterate through queue
        LFUSQEPCacheEntry holder = null;
        while(mEntriesPriorityQueue.size() !=0)
        {
            holder = mEntriesPriorityQueue.poll();
            if(holder.getQuery().equals(q))
                break;
            queryHolder.add(holder);
            holder = null;
        }
        for(LFUSQEPCacheEntry tmp : queryHolder)
            mEntriesPriorityQueue.add(tmp); //Re-add all the things we took out of the queue back in
        return holder; //If holder actually found a value, it'll return it. Else, Null
    }

    /**
     * Updates the frequency for all items in the Cache
     */
    public final void updateFrequencies()
    {
        for(LFUSQEPCacheEntry entry : mEntriesPriorityQueue)
        {
            entry.setFrequency();
        }
    }

    /**
     * Updates the scores for all items in the Cache
     */
    public final void updateScores()
    {
        for(LFUSQEPCacheEntry entry : mEntriesPriorityQueue)
        {
            entry.setScore();
        }
    }


    class LFUSQEPCacheEntry implements Comparable<LFUSQEPCacheEntry>
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
         * Sets teh freqency as useInPeriod/lastReset
         */
        public final void setFrequency()
        {
            frequency = useInPeriod/lastReset;
        }

        /**
         *
         * @return how many times this query has been used in the current period
         */
        public final double getUseInPeriod()
        {
            return useInPeriod;
        }

        /**
         * Increments the useInPeriod variable by one
         */
        public final void incUseInPeriod()
        {
            ++useInPeriod;
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
         * @param other Some other cache entry
         * @return the difference between the first and second cache entry's QEP QEPScore
         */
        @Override
        public int compareTo(LFUSQEPCacheEntry other)
        {
            return (int)(this.getQEPScore()-other.getQEPScore());
        }

        /**
         * @param o Object to compare
         * @return If the queries are equal
         */
        @Override
        public boolean equals(Object o)
        {
            if(o instanceof LFUSQEPCacheEntry)
            {
                if(((LFUSQEPCacheEntry)(o)).getQuery() == this.getQuery())
                    return true;
            }
            return false;
        }
    }

}
