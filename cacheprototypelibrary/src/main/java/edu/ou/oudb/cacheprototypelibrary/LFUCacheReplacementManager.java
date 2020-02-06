package edu.ou.oudb.cacheprototypelibrary;

import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.PriorityQueue;

import edu.ou.oudb.cacheprototypelibrary.core.cachemanagers.CacheReplacementManager;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;


/**
 * @author Zachary Arani
 * @since 2.0
 * CacheReplacementManager that used the created Least Frequently Used with respect to Size and QEP QEPScore (LFUSQEP)
 */
public class LFUCacheReplacementManager implements CacheReplacementManager<Query>{

    private PriorityQueue<LFUCacheEntry> mEntriesPriorityQueue = null;

    private double lastReset = 0; //How many hits since the last Frequency Reset
    private double hitsUntilRestart = 40; //How many hits need to happen before lastReset rolls back to 0

    public LFUCacheReplacementManager() {Log.i("LFU", "STARTED NEW MANAGER"); mEntriesPriorityQueue = new PriorityQueue<LFUCacheEntry>(); }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean update(Query q) //Update Frequency
    {
        LFUCacheEntry qHolder = new LFUCacheEntry();
        qHolder.setQuery(q);
        boolean inQueue = mEntriesPriorityQueue.contains(qHolder);
        boolean ret = false;
        Log.i("LFU","Updating Query");
        if (mEntriesPriorityQueue.contains(qHolder))
        {
            LFUCacheEntry curCacheEntry = getEntry(q);
            curCacheEntry.incFrequency(); //Increments UseInPeriod
            mEntriesPriorityQueue.add(curCacheEntry); //Put back into the queue
            ret = true;
        }

        return ret;
    }

    /**
     * (@inheritDoc)
     */
    @Override
    public boolean update(Query q, double scoreModifier){
        return update(q);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(Query q, double score)
    {
        Log.i("LFU CACHE", "ADDING QUERY");
        LFUCacheEntry qHolder = new LFUCacheEntry();
        qHolder.setQuery(q);
        boolean ret;
        if (mEntriesPriorityQueue.contains(qHolder))
        {
            update(q);
            ret = false;
            return ret;
        }
        LFUCacheEntry cacheEntry = new LFUCacheEntry();
        cacheEntry.setQuery(q);
        cacheEntry.incFrequency();
        mEntriesPriorityQueue.add(cacheEntry);

        ret = true;

        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(Query q, double score, double scoreModifier){
        return add(q, score);
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
        Log.i("LFU QUERY", "REMOVING QUERY");
        LFUCacheEntry qHolder = new LFUCacheEntry();
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
     * {@inheritDoc}
     */
    @Override
    public boolean clear(){
        mEntriesPriorityQueue.clear();
        return true;
    }

    /**
     * Removes and returns the desired entry from the Priority Queue or null otherwise.
     * @param q The Query to search by
     * @return CacheEntry which is passed
     */
    private LFUCacheEntry getEntry(Query q){
        ArrayList<LFUCacheEntry> queryHolder = new ArrayList<LFUCacheEntry>(); //Holds queries while we iterate through queue
        LFUCacheEntry holder = null;
        while(mEntriesPriorityQueue.size() !=0)
        {
            holder = mEntriesPriorityQueue.poll();
            if(holder.getQuery().equals(q))
                break;
            queryHolder.add(holder);
            holder = null;
        }
        for(LFUCacheEntry tmp : queryHolder)
            mEntriesPriorityQueue.add(tmp); //Re-add all the things we took out of the queue back in
        return holder; //If holder actually found a value, it'll return it. Else, Null
    }




    class LFUCacheEntry implements Comparable<LFUCacheEntry>
    {
        private Query mQuery;
        private double frequency = 0; //Frequency using LFUPP (Least Frequently used with respect to periodicity and priority
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
         * @return the frequency of the query using LFUPP (Least Frequently used with respect to Periodicity and Priority)
         */
        public final double getFrequency()
        {
            return frequency;
        }

        /**
         * Sets teh freqency as useInPeriod/lastReset
         */
        public final void incFrequency() {frequency = frequency+1;}

        /**
         * Sets the score based on currently existing frequency, size, and QEP Score as formulate (Frequency * QEP Score)/Size
         */

        /**
         *
         * @param other Some other cache entry
         * @return the difference between the first and second cache entry's QEP QEPScore
         */
        @Override
        public int compareTo(LFUCacheEntry other)
        {
            return (int)(this.getFrequency()-other.getFrequency());
        }

        /**
         * @param o Object to compare
         * @return If the queries are equal
         */
        @Override
        public boolean equals(Object o)
        {
            if(o instanceof LFUCacheEntry)
            {
                if(((LFUCacheEntry)(o)).getQuery() == this.getQuery())
                    return true;
            }
            return false;
        }
    }

}
