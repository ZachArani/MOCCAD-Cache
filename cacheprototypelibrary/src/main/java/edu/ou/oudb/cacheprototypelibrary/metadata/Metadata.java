package edu.ou.oudb.cacheprototypelibrary.metadata;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.ou.oudb.cacheprototypelibrary.connection.DataAccessProvider;

public class Metadata {

	private static Metadata instance;
	private DataAccessProvider mDataAccessProvider;
	
	private Map<String,RelationMetadata> mRelationMetadataMap = new HashMap<String,RelationMetadata>();
	
	public Metadata(DataAccessProvider dataAccessProvider)
	{
		setDataAccessProvider(dataAccessProvider);
	}
	
	public RelationMetadata getRelationMetadata(String relation)
	{
		return mRelationMetadataMap.get(relation);
	}
	
	public static void init(DataAccessProvider dataAccessProvider)
	{
		if (instance == null)
		{
			instance = new Metadata(dataAccessProvider);
		}
	}
	
	public static Metadata getInstance()
	{
		if (instance == null)
			throw new IllegalStateException("have you called init(connectionWrapper)?");
		
		return instance;
	}
	
	public Set<String> getRelationNames()
	{
		return mRelationMetadataMap.keySet();
	}
	
	public void setDataAccessProvider( DataAccessProvider dataAccessProvider)
	{
		if (dataAccessProvider != null && mDataAccessProvider != dataAccessProvider)
		{
			mDataAccessProvider = dataAccessProvider;
			update();
		}
	}
	
	public void update()
	{
		mRelationMetadataMap.clear();
		Collection<String> relationNames = mDataAccessProvider.getRelationNames();
		
		for(String relationName: relationNames)
		{
			mRelationMetadataMap.put(relationName, new RelationMetadata(relationName,
																		mDataAccessProvider.getAttributeNames(relationName),
																		mDataAccessProvider.getAttributeTypes(relationName),
																		mDataAccessProvider.getNbTuples(relationName),
                                                                        mDataAccessProvider.getAvgTupleSize(relationName),
																		mDataAccessProvider.getMaxTupleSize(relationName),
																		mDataAccessProvider.getMinValueForAttributes(relationName),
																		mDataAccessProvider.getMaxValueForAttributes(relationName),
																		mDataAccessProvider.getNbDifferentValuesForAttributes(relationName)
																	));
		}
	}
	
}
