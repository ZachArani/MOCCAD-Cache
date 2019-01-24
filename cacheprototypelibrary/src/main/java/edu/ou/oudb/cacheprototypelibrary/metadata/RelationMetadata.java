package edu.ou.oudb.cacheprototypelibrary.metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.support.annotation.Nullable;

public class RelationMetadata {

	private String mRelationName = "";
	private Map<String,Integer> mAttributeNameIndex = new HashMap<String,Integer>();
	private List<String> mAttributeNames = new ArrayList<String>();
	private List<String> mAttributeTypes = new ArrayList<String>();
	private Map<String,Double> mMinValueForAttributeMap = new HashMap<String,Double>();
	private Map<String,Double> mMaxValueForAttributeMap = new HashMap<String,Double>();
	private Map<String,Long> mNbDifferentValuesForAttributeMap = new HashMap<String,Long>();
	private long mNbTuples = 0;
    private double mAvgTupleSize = 0;
	private int mMaxTupleSize = 0;
	private int mNbAttributes = 0;
	
	public RelationMetadata(String relationName, List<String> attributeNames, List<String> attributeTypes, long nbTuples, double avgTupleSize, int tupleMaxSize,
								Map<String,Double> minValueForAttributeMap,
								Map<String,Double> maxValueForAttributeMap,
								Map<String,Long> nbDifferentValuesForAttributeMap) {
		int i = 0;
		
		setRelationName(relationName);
		setAttributeNames(attributeNames);
		
		for(String attributeName: attributeNames)
		{
			mAttributeNameIndex.put(attributeName, i);
			++i;
		}
		
		setAttributeTypes(attributeTypes);
		setNbTuples(nbTuples);
		setNbAttributes(attributeNames.size());
        setAvgTupleSize(avgTupleSize);
		setMaxTupleSize(tupleMaxSize);
		mMinValueForAttributeMap = minValueForAttributeMap;
		mMaxValueForAttributeMap = maxValueForAttributeMap;
		mNbDifferentValuesForAttributeMap = nbDifferentValuesForAttributeMap;
	}

	public int getAttributeIndex(String attributeName)
	{
		return mAttributeNameIndex.get(attributeName);
	}
	
	@Nullable
	public Double getAttributeMinValue(String attributeName)
	{
		return mMinValueForAttributeMap.get(attributeName);
	}
	
	@Nullable
	public Double getAttributeMaxValue(String attributeName)
	{
		return mMaxValueForAttributeMap.get(attributeName);
	}
	
	@Nullable
	public Long getAttributeNbValue(String attributeName)
	{
		return mNbDifferentValuesForAttributeMap.get(attributeName);
	}
	
	
	/**
	 * @return the minValueForAttributeMap
	 */
	public final Map<String, Double> getMinValueForAttributeMap() {
		return this.mMinValueForAttributeMap;
	}

	/**
	 * @param minValueForAttributeMap the minValueForAttributeMap to set
	 */
	public final void setMinValueForAttributeMap(
			Map<String, Double> minValueForAttributeMap) {
		if (minValueForAttributeMap != null) {
			this.mMinValueForAttributeMap = minValueForAttributeMap;
		}
	}

	/**
	 * @return the maxValueForAttributeMap
	 */
	public final Map<String, Double> getMaxValueForAttributeMap() {
		return this.mMaxValueForAttributeMap;
	}

	/**
	 * @param maxValueForAttributeMap the maxValueForAttributeMap to set
	 */
	public final void setMaxValueForAttributeMap(
			Map<String, Double> maxValueForAttributeMap) {
		if (maxValueForAttributeMap != null) {
			this.mMaxValueForAttributeMap = maxValueForAttributeMap;
		}
	}

	/**
	 * @return the nbDifferentValuesForAttributeMap
	 */
	public final Map<String, Long> getNbDifferentValuesForAttributeMap() {
		return this.mNbDifferentValuesForAttributeMap;
	}

	/**
	 * @param nbDifferentValuesForAttributeMap the nbDifferentValuesForAttributeMap to set
	 */
	public final void setNbDifferentValuesForAttributeMap(
			Map<String, Long> nbDifferentValuesForAttributeMap) {
		if (nbDifferentValuesForAttributeMap != null) {
			this.mNbDifferentValuesForAttributeMap = nbDifferentValuesForAttributeMap;
		}
	}

	/**
	 * @return the relationName
	 */
	public final String getRelationName() {
		return this.mRelationName;
	}
	/**
	 * @param relationName the relationName to set
	 */
	private final void setRelationName(String relationName) {
		if (relationName != null) {
			this.mRelationName = relationName;
		}
	}
	/**
	 * @return the attributeNames
	 */
	public final List<String> getAttributeNames() {
		return this.mAttributeNames;
	}
	/**
	 * @param attributeNames the attributeNames to set
	 */
	private final void setAttributeNames(List<String> attributeNames) {
		if (attributeNames != null) {
			mAttributeNames = attributeNames;
		}
	}
	
	/**
	 * @return the attributeTypes
	 */
	public final List<String> getAttributeTypes() {
		return this.mAttributeTypes;
	}
	
	/**
	 * @param attributeTypes the attributeTypes to set
	 */
	private final void setAttributeTypes(List<String> attributeTypes) {
		if (attributeTypes != null) {
			this.mAttributeTypes = attributeTypes;
		}
	}
	
	/**
	 * @return the nbTuples
	 */
	public long getNbTuples() {
		return this.mNbTuples;
	}
	
	/**
	 * @param nbTuples the nbTuples to set
	 */
	private final void setNbTuples(long nbTuples) {
		if (nbTuples >= 0) {
			this.mNbTuples = nbTuples;
		}
	}
	
	public int getNbAttributes()
	{
		return mNbAttributes;
	}
	
	private final void setNbAttributes(int nbAttributes) {
		if (nbAttributes >= 0) {
			this.mNbAttributes = nbAttributes;
		}
	}

	/**
	 * @return the maxTupleSize
	 */
	public int getMaxTupleSize() {
		return this.mMaxTupleSize;
	}

	/**
	 * @param maxTupleSize the maxTupleSize to set
	 */
	public void setMaxTupleSize(int maxTupleSize) {
		if (maxTupleSize > 0)
		{
			this.mMaxTupleSize = maxTupleSize;
		}
	}

    /**
     * @return the avgTupleSize
     */
    public double getAvgTupleSize() {
        return this.mAvgTupleSize;
    }

    /**
     * @param avgTupleSize the maxTupleSize to set
     */
    public void setAvgTupleSize(double avgTupleSize) {
        if (avgTupleSize > 0)
        {
            this.mAvgTupleSize = avgTupleSize;
        }
    }
	
}
