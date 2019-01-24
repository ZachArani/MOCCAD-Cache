package edu.ou.oudb.cacheprototypelibrary.querycache.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.ou.oudb.cacheprototypelibrary.core.cache.Sizeable;
import edu.ou.oudb.cacheprototypelibrary.metadata.Metadata;
import edu.ou.oudb.cacheprototypelibrary.metadata.ObjectSizer;
import edu.ou.oudb.cacheprototypelibrary.metadata.RelationMetadata;

/**
 * @author Mikael Perrin
 * @since 1.0
 * Definition of the query entity
 */
public class Query implements Sizeable {
	/*
	 * for final versions, an expression tree will be necessary to represent the query.
	 * The algorithms of query trimming will need to be adapted consequently
	 */
	
	/** The queryID */
	private long mId = -1;
	
	/** The relation on which the query is posed */
	private String mRelation;

	/** The hash set of attributes of the query */
	private HashSet<String> mAttributes;
	
	/** The hash set of attribute allowing contained in the predicates */
	private HashSet<String> mPredicateAttributes;
	
	/** Collection of predicates (conjunction)*/
	private Set<Predicate> mPredicates;
	
	/** Excluded Predicates: !(ExcludedPredicates)*/
	private Set<Predicate> mExcludedPredicates;

	/** Memory Size of a query */
	private long mSize = 0;
	
	/**
	 * The query constructor
	 * @param relation the query relation
	 */
	public Query(String relation)
	{
		setRelation(relation);
		mAttributes = new HashSet<String>();
		mPredicates = new HashSet<Predicate>();
		mPredicateAttributes = new HashSet<String>();
		mExcludedPredicates = new HashSet<Predicate>();
		mSize += ObjectSizer.getStringSize32bits(relation.length());
	}
	
	/**
	 * Method used to add another predicate
	 * @param predicate the predicate to be added
	 * @return true if added, false otherwise
	 */
	public boolean addPredicate(Predicate predicate)
	{
		boolean inserted = true;
		inserted = mPredicates.add(predicate);
		
		if (inserted)
			inserted = addPredicateAttributes(predicate.getAttributes());
		
		mSize += predicate.size();
		
		return inserted;
	}
	
	/**
	 * Method used to add several other predicates
	 * @param predicates the predicates to be added
	 * @return true if added, false otherwise
	 */
	public boolean addPredicates(Collection<Predicate> predicates)
	{
		boolean inserted = true;
		inserted = mPredicates.addAll(predicates);
		
		Iterator<Predicate> it = predicates.iterator();
		Predicate p = null;
		
		while(it.hasNext() && inserted)
		{
			p = it.next();
			inserted = addPredicateAttributes(p.getAttributes());
			mSize += p.size();
		}
		
		return inserted;
	}
	
	/**
	 * Method used to add a Predicate in front of a NOT
	 * e.g: NOT(list of predicates)
	 * @param predicate the predicate to be excluded
	 * @return true if added, false otherwise
	 */
	public boolean addExcludedPredicate(Predicate predicate)
	{
		boolean inserted = true;
		inserted = mExcludedPredicates.add(predicate);
		
		if (inserted)
			inserted = addPredicateAttributes(predicate.getAttributes());
		
		mSize += predicate.size();
		
		return inserted;
	}
	
	/**
	 * Method used to add several Predicates in front of a NOT
	 * e.g: NOT(list of predicates)
	 * @param predicates the predicates to be added
	 * @return true if added, false otherwise
	 */
	public boolean addExcludedPredicates(Collection<Predicate> predicates)
	{
		boolean inserted = true;
		inserted = mExcludedPredicates.addAll(predicates);
		
		Iterator<Predicate> it = predicates.iterator();
		Predicate p = null;
		
		while(it.hasNext() && inserted)
		{
			p = it.next();
			inserted = addPredicateAttributes(p.getAttributes());
			mSize += p.size();
		}
		
		return inserted;
	}

	/**
	 * Method to add attributes to query
	 * @param attribute the attribute to be added
	 * @return true if added, false otherwise
	 */
	public boolean addAttribute(String attribute)
	{
		if (attribute != "*") {
			mSize += ObjectSizer.getStringSize32bits(attribute.length());
			return mAttributes.add(attribute);
		} else { //if attribute is *, adds all attributes
			String[] attributes = new String[]{"noteid", "patientfirstname", "patientlastname", "doctorfirstname", "doctorlastname", "description", "p_date_time", "heartrate"};
			for(String a: attributes) {
				addAttribute(a);
			}
		}
		return false; //if *, do not add - not sure about implementation
	}

	/**
	 * Method to add attributes to query
	 * @param attributes the attributes to be added
	 * @return true if added, false otherwise
	 */
	public boolean addAttributes(Collection<String> attributes)
	{
		for(String attribute: attributes)
		{
			if (!addAttribute(attribute))
				return false;
		}

		return true;
	}
	
	/**
	 * Attribute to be added in order to make the analysis easier
	 * @param attribute the attribute to be added
	 * @return true if added, false otherwise
	 */
	public boolean addPredicateAttribute(String attribute)
	{
		mSize += ObjectSizer.getStringSize32bits(attribute.length());
		return mPredicateAttributes.add(attribute);
	}
	
	/**
	 * Attributes to be added in order to make the analysis easier
	 * @param attributes the attributes to be added
	 * @return true if added, false otherwise
	 */
	public boolean addPredicateAttributes(Collection<String> attributes)
	{
		for(String attribute: attributes)
		{
			if (!addPredicateAttribute(attribute))
				return false;
		}

		return true;
	}
	

	/**
	 * @return the id
	 */
	public long getId() {
		return this.mId;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id)
	{
		if(id > 0)
		{
			mId = id;
		}
	}
	
	/**
	 * @return the relation
	 */
	public final String getRelation() {
		return this.mRelation;
	}

	/**
	 * @param relation the relation to set
	 */
	public void setRelation(final String relation) {
		
		if (relation != null)
		{
			this.mRelation = relation;
			mSize -= ObjectSizer.getStringSize32bits(mRelation.length());
			mSize += ObjectSizer.getStringSize32bits(relation.length());
		}
	}

	/**
	 * @return the attributes
	 */
	public final HashSet<String> getAttributes() { return this.mAttributes; }

	/**
	 * @return the predicateAttributes
	 */
	public final HashSet<String> getPredicateAttributes() {
		return this.mPredicateAttributes;
	}

	/**
	 * @return the predicates
	 */
	public final Set<Predicate> getPredicates() {
		return this.mPredicates;
	}
	
	/**
	 * @return the excludedPredicates
	 */
	public final Set<Predicate> getExcludedPredicates() {
		return this.mExcludedPredicates;
	}

	public boolean containsPredicateAttribute(String attribute)
	{
		return mPredicateAttributes.contains(attribute);
	}
	
	public boolean containsPredicateAttributes(Collection<String> attributes)
	{
		return mPredicateAttributes.containsAll(attributes);
	}

	public boolean containsAttribute(String attribute)
	{
		return mAttributes.contains(attribute);
	}

	public boolean containsAttributes(Collection<String> attributes)
	{
		return mAttributes.containsAll(attributes);
	}

	public boolean respectsAttributes(String relation, List<String> tuple) throws NumberFormatException
	{
		RelationMetadata relationMetadata = Metadata.getInstance().getRelationMetadata(relation);
		boolean isValidTuple = false;
		Iterator<String> it = mAttributes.iterator();
		while(it.hasNext() && isValidTuple)
		{
			Integer attIndex = relationMetadata.getAttributeIndex(it.next());
			if (attIndex == null)
			{
				isValidTuple = false;
			}
		}
		return isValidTuple;
	}

	@Override
	public long size() {
		return mSize;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((mExcludedPredicates == null) ? 0 : mExcludedPredicates
						.hashCode());
		result = prime
				* result
				+ ((mPredicateAttributes == null) ? 0 : mPredicateAttributes
						.hashCode());
		result = prime * result
				+ ((mPredicates == null) ? 0 : mPredicates.hashCode());
		result = prime * result
				+ ((mRelation == null) ? 0 : mRelation.hashCode());
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
		Query other = (Query) obj;
		if (mExcludedPredicates == null) {
			if (other.mExcludedPredicates != null) {
				return false;
			}
		} else if (!mExcludedPredicates.equals(other.mExcludedPredicates)) {
			return false;
		}
		if (mPredicateAttributes == null) {
			if (other.mPredicateAttributes != null) {
				return false;
			}
		} else if (!mPredicateAttributes.equals(other.mPredicateAttributes)) {
			return false;
		}
		if (mPredicates == null) {
			if (other.mPredicates != null) {
				return false;
			}
		} else if (!mPredicates.equals(other.mPredicates)) {
			return false;
		}
		if (mRelation == null) {
			if (other.mRelation != null) {
				return false;
			}
		} else if (!mRelation.equals(other.mRelation)) {
			return false;
		}
		return true;
	}

	public String toSQLString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT ");

		int sizeAttributes = mAttributes.size();

		if (sizeAttributes == 0)
		{
			builder.append("*");
		}

		int k = 0;
		for(String a: mAttributes) {
			builder.append(a);
			if (k < sizeAttributes-1) {
				builder.append(", ");
			}
			++k;
		}

		builder.append(" FROM ");
		builder.append(mRelation);
		
		
		int sizePredicates = mPredicates.size();
		int sizeExcludedPredicates = mExcludedPredicates.size();
		
		if (sizePredicates + sizeExcludedPredicates > 0)
		{
			builder.append(" WHERE ");
		}
		
		int i = 0;
		for(Predicate p: mPredicates)
		{
			builder.append(p);
			if (i < sizePredicates-1)
			{
				builder.append(" AND ");
			}
			++i;
		}
		
		
		
		if (sizeExcludedPredicates != 0)
		{
			builder.append(" AND NOT ( ");
			i = 0;
			for(Predicate p: mExcludedPredicates)
			{
				builder.append(p);
				if (i < sizeExcludedPredicates-1)
				{
					builder.append(" AND ");
				}
				++i;
			}
			
			builder.append(" )");
		}
		
		builder.append(";");
		
		return builder.toString();
	}
}
