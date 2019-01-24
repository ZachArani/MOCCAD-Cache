package edu.ou.oudb.cacheprototypelibrary.querycache.query;

import java.util.List;
import java.util.Set;

import edu.ou.oudb.cacheprototypelibrary.core.cache.Sizeable;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.CycleFoundException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.InvalidPredicateException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.TrivialPredicateException;


/**
 * @author Mikael Perrin
 * @since 1.0
 *
 * @brief class allowing to represent a predicate 
 * It can either be X op Y where X and Y are attributes
 * or X op C where X is an attribute and C is a constant
 */
public abstract class Predicate implements Sizeable{
	
	/** the predicate operator */
	protected String mOperator;
	
	/**
	 * @brief creates a predicate
	 * @param operator the predicate operator 
	 * @throws CycleFoundException thrown when predicate is not valid
	 * @throws TrivialPredicateException thrown when the predicate is useless
	 */
	public Predicate(String operator) throws InvalidPredicateException, TrivialPredicateException 
	{
		this.mOperator = operator;
	}
	
	/**
	 * @return false if invalid predicate, true if valid
	 */
	public abstract boolean isValidPredicate();
	
	/**
	 * @return true if trivial equality or false if not a trivial equality
	 */
	public abstract boolean isTrivialPredicate();
	
	
	/**
	 * This method transform the predicate such that it can only be:
	 * X{<,<=}Y and X{<,<=,>=,>}C
	 * @return true if transformed, false otherwise
	 */
	public abstract boolean transformToRealDomainPredicate();
	
	
	/**
	 * This method transform the predicate such that it can only be:
	 * X{<,<=}Y and X{<=,>=}C
	 * @return true if transformed, false otherwise
	 */
	public abstract boolean transformToIntegerDomainPredicate();
	
	/**
	 * tells if a tuples respect a predicate
	 * @param relation the tuple's relation
	 * @param tuple the tuple to analyze
	 * @return true if the predicated is applied by the tuple, false otherwise
	 */
	public abstract boolean apply(String relation, List<String> tuple);
	
	
	/**
	 * Method used to return the attributes of the given predicates
	 * @return teh set of attributes
	 */
	public abstract Set<String> getAttributes();
	
	/**
	 * Method checking is the operator is valid or not
	 * @return true is operator is valid, false otherwise
	 */
	public boolean hasValidOperator()
	{		
		return ((mOperator != null) && 
			(mOperator.equals("<") || mOperator.equals(">") || mOperator.equals("<=") ||
			mOperator.equals(">=") || mOperator.equals("<>") || mOperator.equals("=")));
	}
	
	
	/**
	 * @return the operator
	 */
	public String getOperator() {
		return this.mOperator;
	}

	/**
	 * @param operator the operator to set
	 */
	public void setOperator(String operator) {
		if (operator != null) {
			this.mOperator = operator;
		}
	}
	
	public abstract long size();
	
	public abstract String getSerializedLeftOperand();
	
	public abstract String getSerializedRightOperand();

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((mOperator == null) ? 0 : mOperator.hashCode());
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
		Predicate other = (Predicate) obj;
		if (mOperator == null) {
			if (other.mOperator != null) {
				return false;
			}
		} else if (!mOperator.equals(other.mOperator)) {
			return false;
		}
		return true;
	}

	
	
	
}
