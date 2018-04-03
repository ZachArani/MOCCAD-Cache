package edu.ou.oudb.cacheprototypelibrary;

import android.support.annotation.Nullable;

import edu.ou.oudb.cacheprototypelibrary.core.cachemanagers.CacheReplacementManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;


/**
 * @author Zachary Arani
 * @since 2.0
 * CacheReplacementManager that used the created Least Frequently Used with respect to Size and QEP score (LFUSQEP)
 */
public class LFUSQEPCacheReplacementManager implements CacheReplacementManager<Query>{

    private Map<Query, LFUSQEPCacheEntry> mEntriesHashMap = null;

    private LFUSQEPCacheEntry begin;
    private LFUSQEPCacheEntry end;

    public LFUSQEPCacheReplacementManager() { mEntriesHashMap = new HashMap<Query, LFUSQEPCacheEntry>(); }


    @Override
    public boolean update(Query q) //Update Frequency
    {
        boolean ret = false;

        if (mEntriesHashMap.containsKey(q))
        {
            LFUSQEPCacheEntry curCacheEntry = mEntriesHashMap.get(q);

            if(end != curCacheEntry)
            {
                // if the entry is not the first one
                if (curCacheEntry.prev != null)
                {
                    curCacheEntry.prev.next = curCacheEntry.next;
                }
                else
                {
                    begin = curCacheEntry.next;
                }


                curCacheEntry.next.prev = curCacheEntry.prev;

                curCacheEntry.prev = end;
                end.next = curCacheEntry;
                end = curCacheEntry;
            }

            ret = true;
        }

        return ret;
    }

    @Override
    public boolean add(Query q)
    {
        boolean ret;

        if (mEntriesHashMap.containsKey(q))
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

            mEntriesHashMap.put(q, cacheEntry);

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

        if (!mEntriesHashMap.containsKey(q))
        {
            ret = false;
        }
        else // cannot be empty
        {
            LFUSQEPCacheEntry entryToRemove = mEntriesHashMap.get(q);

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


            mEntriesHashMap.remove(q);

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

    class LFUSQEPCacheEntry
    {
        private Query mQuery;
        public LFUSQEPCacheEntry prev;
        public LFUSQEPCacheEntry next;
        private double score = 0; //QEP Score
        private double size = 0; //In Xbs
        private double frequency = 0; //Frequency using LFUPP (Least Frequently used with respect to periodicity and priority)


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
         * @return the QEP score
         */
        public final double getScore()
        {
            return score;
        }

        /**
         *
         * @param the QEP score to set
         */
        public final void setScore(double qep)
        {
            score = qep;
        }

        /**
         *
         * @return the size of the query (in Xbytes)
         */
        public final double getSize()
        {
            return size;
        }

        /**
         *
         * @param the size of the query to set (in Xbytes)
         */
        public final void setSize(double s)
        {
            size = s;
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
    }

}
