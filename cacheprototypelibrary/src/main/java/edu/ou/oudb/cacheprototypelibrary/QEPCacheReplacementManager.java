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
public class QEPCacheReplacementManager implements CacheReplacementManager<Query>{

    private PriorityQueue<QEPCacheEntry> mEntriesPriorityQueue = null;

    private double scoreModifier = 0.00;

    public QEPCacheReplacementManager() {Log.i("LFUSQEP", "STARTED NEW MANAGER"); mEntriesPriorityQueue = new PriorityQueue<QEPCacheEntry>(); }


    /**
     * NO-OP
     */
    @Override
    public boolean update(Query q) //Update Frequency
    {
        return false;
    }

    /**
     *
     * @param q
     * @param scoreModifier update query with new score modifier
     * @return
     */
    @Override
    public boolean update(Query q, double scoreModifier){
        QEPCacheEntry qHolder = new QEPCacheEntry();
        qHolder.setQuery(q);
        boolean inQueue = mEntriesPriorityQueue.contains(qHolder);
        boolean ret = false;
        Log.i("QEPCache", "Updating Query");
        if(mEntriesPriorityQueue.contains(qHolder))
        {
            QEPCacheEntry curCacheEntry = getEntry(q);
            curCacheEntry.setScore(scoreModifier);
            mEntriesPriorityQueue.add(curCacheEntry); //Put back into queue
            ret = true;
        }

        return ret;
    }


    /**
     * No operation of QEPCacheReplacementManager
     */
    @Override
    public boolean add(Query q, double score){
        return false; //NO-OP
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(Query q, double QEPScore, double scoreModifier)
    {
        Log.i("LFUSQEP CACHE", "ADDING QUERY");
        QEPCacheEntry qHolder = new QEPCacheEntry();
        qHolder.setQuery(q);
        qHolder.setQEPScore(QEPScore);
        qHolder.normalizeScore();
        boolean ret;
        if (mEntriesPriorityQueue.contains(qHolder))
        {
            update(q, scoreModifier);
            ret = false;
            return ret;
        }
        mEntriesPriorityQueue.add(qHolder);

        ret = true;

        return ret;
    }
//TODO: ADD SET SCORE AND SET FREQUENCY THAT UPDATE THOSE FOR EVERYTHING IN THE QUEUE
    /**
     * NO-OP
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
        QEPCacheEntry qHolder = new QEPCacheEntry();
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
    private QEPCacheEntry getEntry(Query q){
        ArrayList<QEPCacheEntry> queryHolder = new ArrayList<QEPCacheEntry>(); //Holds queries while we iterate through queue
        QEPCacheEntry holder = null;
        while(mEntriesPriorityQueue.size() !=0)
        {
            holder = mEntriesPriorityQueue.poll();
            if(holder.getQuery().equals(q))
                break;
            queryHolder.add(holder);
            holder = null;
        }
        for(QEPCacheEntry tmp : queryHolder)
            mEntriesPriorityQueue.add(tmp); //Re-add all the things we took out of the queue back in
        return holder; //If holder actually found a value, it'll return it. Else, Null
    }



    class QEPCacheEntry implements Comparable<QEPCacheEntry>
    {
        private Query mQuery;
        private double QEPScore = 0; //QEP Score
        private double score = 0; //The actual score we use for cache Management.
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
         * @return the algorithim's score. Formulated as (Frequency * QEP)/Size
         */
        public final double getScore()
        {
            return score;
        }

        /**
         * Turns score into QEPScore * 10^14
         */
        public final void normalizeScore(){
            score = QEPScore * Math.pow(10,14);
        }

        /**
         * Sets the score based on currently existing frequency, size, and QEP Score as formulate (Frequency * QEP Score)/Size
         */
        public final void setScore(double amount)
        {

            double normanlizedQEP = QEPScore * Math.pow(10,14);
            score = normanlizedQEP + amount;
        }
        /**
         *
         * @param other Some other cache entry
         * @return the difference between the first and second cache entry's QEP QEPScore
         */
        @Override
        public int compareTo(QEPCacheEntry other)
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
            if(o instanceof QEPCacheEntry)
            {
                if(((QEPCacheEntry)(o)).getQuery() == this.getQuery())
                    return true;
            }
            return false;
        }
    }

}
