package edu.ou.oudb.cacheprototypelibrary.querycache.query;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import edu.ou.oudb.cacheprototypelibrary.metadata.Metadata;
import edu.ou.oudb.cacheprototypelibrary.metadata.ObjectSizer;
import edu.ou.oudb.cacheprototypelibrary.metadata.RelationMetadata;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.InvalidPredicateException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.TrivialPredicateException;

/**
 * @author Mikael Perrin
 * @since 1.0
 * Definition of a predicate with Attribute-Operator-Constant
 */
public class XopCPredicate extends Predicate {

	/** The left operand for the predicate */
	private String mLeftOperand;
	
	/** The right operand for the predicate */
	private double mRightOperand;
	
	/**
	 * Specialization of a predicate representing the XopC precicates
	 * @param leftOperand the left operand as an attribute
	 * @param operator the predicate operator
	 * @param rightOperand the right operand as a constant
	 * @throws InvalidPredicateException thrown when predicate is invalid
	 * @throws TrivialPredicateException thrown when predicate is useless
	 */
	public XopCPredicate(String leftOperand, String operator, double rightOperand) 
			throws InvalidPredicateException, TrivialPredicateException {
		super(operator);
		mLeftOperand = leftOperand;
		mRightOperand = rightOperand;
		if (!isValidPredicate())
		{
			throw new InvalidPredicateException();
		}
		else if (isTrivialPredicate())
		{
			throw new TrivialPredicateException();
		}
	}

	/**
	 * Determine is the predicate has valid operator
	 */
	@Override
	public boolean isValidPredicate()
	{
		return hasValidOperator();
	}
	
	/**
	 * For this kind of predicate, it must not be trivial
	 */
	@Override
	public boolean isTrivialPredicate()
	{
		return false;
	}
	
	/**
	 * For this kind of predicate, no transformation is needed for XopC in the real domain
	 */
	@Override
	public boolean transformToRealDomainPredicate() {
		return false;
	}

	@Override
	public boolean transformToIntegerDomainPredicate() {
		boolean transformed = true;

		switch (mOperator) {
			case "<":
				mOperator = "<=";
				mRightOperand--;
				break;
			case ">":
				mOperator = ">=";
				mRightOperand++;
				break;
			default:
				transformed = false;
				break;
		}

		return transformed;
	}
	
	
	@Override
	public boolean apply(String relation, List<String> tuple) throws NumberFormatException{
		RelationMetadata relationMetadata = Metadata.getInstance().getRelationMetadata(relation);
		int index = relationMetadata.getAttributeIndex(mLeftOperand);
		boolean isValidTuple = false;
		
		Double curValue = Double.parseDouble(tuple.get(index));
		
		
		switch(mOperator)
		{
			case "<":
				isValidTuple = curValue < mRightOperand;
				break;
			case ">":
				isValidTuple = curValue > mRightOperand;
				break;
			case "<=":
				isValidTuple = curValue <= mRightOperand;
				break;
			case ">=":
				isValidTuple = curValue >= mRightOperand;
				break;
			case "=":
				isValidTuple = curValue == mRightOperand;
				break;
			case "<>":
				isValidTuple = curValue != mRightOperand;
				break;
		}
			
		return isValidTuple;
	}
	
	@Override
	public Set<String> getAttributes() {
		Set<String> attributeSet = new HashSet<String>();
		attributeSet.add(mLeftOperand);
		return attributeSet;
	}

	/**
	 * @return the leftOperand
	 */
	public String getLeftOperand() {
		return this.mLeftOperand;
	}

	/**
	 * @param leftOperand the leftOperand to set
	 */
	public void setLeftOperand(String leftOperand) {
		if (leftOperand != null) {
			this.mLeftOperand = leftOperand;
		}
	}

	/**
	 * @return the rightOperand
	 */
	public double getRightOperand() {
		return this.mRightOperand;
	}

	/**
	 * @param rightOperand the rightOperand to set
	 */
	public void setRightOperand(double rightOperand) {
		this.mRightOperand = rightOperand;
	}
	
	@Override
	public String getSerializedLeftOperand() {
		return this.mLeftOperand;
	}
	
	@Override
	public String getSerializedRightOperand()
	{
		StringBuilder sb = new StringBuilder();
		if(mRightOperand == (long) mRightOperand)
	        sb.append(String.format(Locale.US, "%d",(long)mRightOperand));
	    else
	        sb.append(String.format(Locale.US, "%s",mRightOperand));
		
		return sb.toString();
	}
	
	@Override
	public long size() {
		return ObjectSizer.getStringSize32bits(mLeftOperand.length())
				+ ObjectSizer.getStringSize32bits(mOperator.length())
				+ Double.SIZE;
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((mLeftOperand == null) ? 0 : mLeftOperand.hashCode());
		long temp;
		temp = Double.doubleToLongBits(mRightOperand);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		XopCPredicate other = (XopCPredicate) obj;
		if (mLeftOperand == null) {
			if (other.mLeftOperand != null) {
				return false;
			}
		} else if (!mLeftOperand.equals(other.mLeftOperand)) {
			return false;
		}
		if (Double.doubleToLongBits(mRightOperand) != Double
				.doubleToLongBits(other.mRightOperand)) {
			return false;
		}
		return true;
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(mLeftOperand);
		sb.append(" ");
		sb.append(mOperator);
		sb.append(" ");
		if(mRightOperand == (long) mRightOperand)
	        sb.append(String.format(Locale.US, "%d",(long)mRightOperand));
	    else
	        sb.append(String.format(Locale.US, "%s",mRightOperand));
		
		return sb.toString();
	}

	
	
	
}
