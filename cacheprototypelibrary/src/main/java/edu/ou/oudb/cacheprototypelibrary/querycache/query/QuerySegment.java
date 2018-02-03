package edu.ou.oudb.cacheprototypelibrary.querycache.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.ou.oudb.cacheprototypelibrary.core.cache.Sizeable;
import edu.ou.oudb.cacheprototypelibrary.metadata.ObjectSizer;


public class QuerySegment implements Sizeable,Serializable{

	private static final long serialVersionUID = 1L;
	private List<List<String>> mTuples = null;
	
	private long mSize = 0;
	
	public QuerySegment()
	{
		mTuples = new LinkedList<List<String>>();
	}
	
	public QuerySegment(List<List<String>> tuples)
	{
		setTuples(tuples);
	}
	
	private long computeTuplesSize(List<List<String>> tuples) {
		int size=0;
		
		for(List<String> tuple: tuples)
		{
			for(String attribute: tuple)
			{
				// memory count of the string value
				size += ObjectSizer.getStringSize32bits(attribute.length());
			}
		}
		
		return size;
	}
	
	public QuerySegment filter(Query query)
	{
		QuerySegment filteredSegment = new QuerySegment();
		String relation = query.getRelation();
		boolean isValidTuple;
		boolean respectsAllExcludedPredicate = false;

		for(List<String> tuple: mTuples)
		{
			isValidTuple = true;
			
			// check included predicates
			Iterator<Predicate> it = query.getPredicates().iterator();
			while(it.hasNext() && isValidTuple)
			{
				if(!it.next().apply(relation,tuple))
				{
					isValidTuple = false;
				}
			}
			
			if(!query.getExcludedPredicates().isEmpty())
			{
				respectsAllExcludedPredicate = true;
				// check excluded predicates !(excludedPredicate1 AND excludedPredicate2 AND...)
				it = query.getExcludedPredicates().iterator();
				while(it.hasNext() && isValidTuple && respectsAllExcludedPredicate)
				{
					if(!it.next().apply(relation,tuple))
					{
						respectsAllExcludedPredicate = false;
					}
				}
			}
			
			if(isValidTuple && respectsAllExcludedPredicate)
			{
				isValidTuple = false;
			}
			
			if (isValidTuple)
			{
				filteredSegment.addTuple(tuple);
			}
		}
		return filteredSegment;
	}
	
	
	public final List<List<String>> getTuples()
	{
		return this.mTuples;
	}
	
	public final void setTuples (final List<List<String>> tuples)
	{
		if (tuples != null)
		{
			mSize += computeTuplesSize(tuples);
			this.mTuples = tuples;
		}
		else
		{
			this.mTuples = new ArrayList<List<String>>();
		}
	}
	
	public int getNbTuples()
	{
		return mTuples.size();
	}
	
	/**
	 * get the number of attributes for the tuples
	 * @return the horizontal count of the segment
	 */
	public int getNbAttributes()
	{
		int size = 0;
		if (!mTuples.isEmpty())
		{
			size = mTuples.get(0).size();
		}
		return size;
	}
	
	public boolean addTuple (List<String> tuple)
	{	
		for(String attribute: tuple)
		{
			mSize += ObjectSizer.getStringSize32bits(attribute.length());
		}
		
		return mTuples.add(tuple);
	}
	
	public boolean addAllTuples(List<List<String>> tuples)
	{	
		mSize += computeTuplesSize(tuples);
		return mTuples.addAll(tuples);
	}
	
	public boolean addAllTuples(QuerySegment querySegment)
	{
		return addAllTuples(querySegment.getTuples());
	}
	
	@Override
	public long size() {
		return mSize;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QuerySegment that = (QuerySegment) o;

        if (mSize != that.mSize) return false;
        if (mTuples != null ? !mTuples.equals(that.mTuples) : that.mTuples != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mTuples != null ? mTuples.hashCode() : 0;
        result = 31 * result + (int) (mSize ^ (mSize >>> 32));
        return result;
    }
}
