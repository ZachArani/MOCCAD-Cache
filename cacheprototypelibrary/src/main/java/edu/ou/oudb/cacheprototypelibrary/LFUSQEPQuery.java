package edu.ou.oudb.cacheprototypelibrary;

import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;

public class LFUSQEPQuery extends Query {

    protected double score = 0.0; //The QEP score. Scaled from 0-1

    public LFUSQEPQuery(String relation, double qep){
        super(relation);
        score = qep;
    }

    /**
     *
     * @return the QEP score for query
     */
    public double getScore()
    {
        return score;
    }

    /**
     *
     * @param query1 The first query to be compared
     * @param query2 The second query to be compared
     * @return The difference between query 1 and query 2's QEP scores
     */
    public int compare(LFUSQEPQuery query1, LFUSQEPQuery query2)
    {
        return (int)(query1.getScore() - query2.getScore());
    }


}
