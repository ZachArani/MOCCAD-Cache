package edu.ou.oudb.cacheprototypelibrary.utils;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ou.oudb.cacheprototypelibrary.estimationcache.Estimation;
import edu.ou.oudb.cacheprototypelibrary.metadata.ObjectSizer;
import edu.ou.oudb.cacheprototypelibrary.metadata.RelationMetadata;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.JSONParserException;

public class JSONParser {

	private static final String TAG_ATTRIBUTE_NAMES = "attributeNames";
	private static final String TAG_ATTRIBUTE_TYPES = "attributeTypes";
	private static final String TAG_NAME = "name";
	private static final String TAG_NB_TUPLES = "nbTuples";
    private static final String TAG_AVG_TUPLE_SIZE = "avgTupleSize";
	private static final String TAG_MAX_TUPLE_SIZE = "maxTupleSize";
	private static final String TAG_MIN_VAL_FOR_ATTR = "minForAttributes";
	private static final String TAG_MAX_VAL_FOR_ATTR = "maxForAttributes";
	private static final String TAG_NB_DIFF_VAL_FOR_ATTR = "nbDifferentValuesForAttributes";
	private static final String TAG_ATTRIBUTES = "attributeValues";
	private static final String TAG_TIME = "time";
	private static final String TAG_MONEY = "money";
    private static final String TAG_TUPLES = "tuples";
    private static final String TAG_COST = "cost";
	
	public static Map<String, RelationMetadata> parseDBInfo(String stringJSON) throws JSONParserException {
		Map<String, RelationMetadata> dbInfo = new HashMap<String, RelationMetadata>();
		
		JSONArray relationArray = null;
		 // attempt to convert string to JSONArray
        try {
        	relationArray = new JSONArray(stringJSON);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
            if (stringJSON.contains("Not found"))
            {
            	Log.e("JSON Parser", "Error URL not found");
            	throw new JSONParserException("Url Not Found Exception");
            }
        }
		
		try {
			
			int relationArrayLength = relationArray.length();
			
			if (relationArrayLength == 0)
			{
				throw new JSONParserException();
			}
			
			// for each relation in the array
			for(int i = 0; i < relationArrayLength; ++i)
			{
				// get the current relation
				JSONObject curRelation = (JSONObject) relationArray.get(i);
				
				// get relation name
				String relationName = curRelation.getString(TAG_NAME);
				
				// get nb tuples
				long nbTuples = curRelation.getLong(TAG_NB_TUPLES);

                double avgTupleSize = curRelation.getDouble(TAG_AVG_TUPLE_SIZE);
				
				// get the maximum count for a tuple
				int maxTupleSize = curRelation.getInt(TAG_MAX_TUPLE_SIZE);
				
				// get attribute names JSONArray
				List<String> attributeNames = new ArrayList<String>();
				JSONArray attributeNamesJsonArray = curRelation.getJSONArray(TAG_ATTRIBUTE_NAMES); 
				if (attributeNamesJsonArray != null) { 
					int len = attributeNamesJsonArray.length();
					for (int j=0;j<len;j++){ 
						attributeNames.add(attributeNamesJsonArray.get(j).toString());
					}
				} 
				
				// get attribute types JSONArray
				List<String> attributeTypes = new ArrayList<String>();
				JSONArray attributeTypesJsonArray = curRelation.getJSONArray(TAG_ATTRIBUTE_TYPES); 
				if (attributeTypesJsonArray != null) { 
					int len = attributeTypesJsonArray.length();
					for (int j=0;j<len;j++){ 
						attributeTypes.add(attributeTypesJsonArray.get(j).toString());
					}
				} 
				
				// get minimum value for each attribute
				Map<String,Double> minForAttributes = new HashMap<String,Double>();
				JSONObject minForAttributesJSONObject = curRelation.getJSONObject(TAG_MIN_VAL_FOR_ATTR);
				if (minForAttributesJSONObject != null)
				{
					for(String attributeName :attributeNames)
					{
						if(!minForAttributesJSONObject.isNull(attributeName))
						{
							minForAttributes.put(attributeName, minForAttributesJSONObject.getDouble(attributeName));
						}
						else
						{
							minForAttributes.put(attributeName, null);
						}
					}
				}
				
				// get maximum value for each attribute
				Map<String,Double> maxForAttributes = new HashMap<String,Double>();
				JSONObject maxForAttributesJSONObject = curRelation.getJSONObject(TAG_MAX_VAL_FOR_ATTR);
				if (maxForAttributesJSONObject != null)
				{
					for(String attributeName :attributeNames)
					{
						if(!maxForAttributesJSONObject.isNull(attributeName))
						{
							maxForAttributes.put(attributeName, maxForAttributesJSONObject.getDouble(attributeName));
						}
						else
						{
							maxForAttributes.put(attributeName, null);
						}
					}
				}
				
				// get the number of different values for attribute
				Map<String,Long> nbDifferentValuesForAttributes = new HashMap<String,Long>();
				JSONObject nbDiffForAttributesJSONObject = curRelation.getJSONObject(TAG_NB_DIFF_VAL_FOR_ATTR);
				if (nbDiffForAttributesJSONObject != null)
				{
					for(String attributeName :attributeNames)
					{
						nbDifferentValuesForAttributes.put(attributeName, nbDiffForAttributesJSONObject.getLong(attributeName));
					}
				}
				
				
				
				// TEST 
				Log.d("DB_INFO","relation name: " + relationName);
				Log.d("DB_INFO","nbTuples: " + nbTuples);
                Log.d("DB_INFO", "avg tuple count: " + avgTupleSize);
				Log.d("DB_INFO","max tuple count: " + maxTupleSize);
				Log.d("DB_INFO","attribute names: " + attributeNames);
				Log.d("DB_INFO","attribute types: " + attributeTypes);
				Log.d("DB_INFO","minimum for attributes: " + minForAttributes);
				Log.d("DB_INFO","maximum for attributes: " + maxForAttributes);
				Log.d("DB_INFO","number of different values for attributes: " + nbDifferentValuesForAttributes);
				
				dbInfo.put(relationName, new RelationMetadata(
												relationName,
												attributeNames,
												attributeTypes,
												nbTuples,
                                                avgTupleSize,
												maxTupleSize,
												minForAttributes,
												maxForAttributes,
												nbDifferentValuesForAttributes
											));
				
			}
			
		} catch (JSONException e) {
			throw new JSONParserException();
		}
		
		return dbInfo;
	}
	
	private static List<String> readAttributeList(JsonReader reader) throws IOException
	{
		List<String> attributes = new ArrayList<String>();
		reader.beginArray();
		while(reader.hasNext())
		{
			attributes.add(reader.nextString());
		}
		reader.endArray();
		return attributes;
	}
	
	private static List<String> readTuple(JsonReader reader) throws IOException
	{
		List<String> tuple = null;
		String curTag;
		
		reader.beginObject();
		while(reader.hasNext())
		{
			curTag = reader.nextName();
			if (curTag.equals(TAG_ATTRIBUTES) && reader.peek() != JsonToken.NULL)
			{
				tuple = readAttributeList(reader);
			}
			else
			{
				reader.skipValue();
			}
		}
		reader.endObject();
		
		return tuple;
	}

	private static List<List<String>> readTupleList(JsonReader reader) throws IOException
	{
		List<List<String>> result = new ArrayList<List<String>>();
		int i = 0;
		reader.beginArray();

		while(reader.hasNext())
		{
			result.add(readTuple(reader));
            ++i;
            if (i % 10000 == 0)
            {
                System.out.println();
            }
		}

		reader.endArray();
		
		return result;
	}

    private static Object[] readProcessCost(JsonReader reader) throws IOException
    {
        Object[] cost = new Object[2];
        String curTag;

        reader.beginObject();
        while(reader.hasNext())
        {
            curTag = reader.nextName();
            if (curTag.equals(TAG_MONEY) && reader.peek() != JsonToken.NULL)
            {
                cost[0] = reader.nextDouble();
                Log.d("Money To Process:","" + (Double) cost[0]);
            }
            else if (curTag.equals(TAG_TIME) && reader.peek() != JsonToken.NULL)
            {
                cost[1] = reader.nextLong();
                Log.d("Time To Process:","" + (Long) cost[1]);
            }
            else
            {
                reader.skipValue();
            }
        }
        reader.endObject();

        return cost;
    }
	
	public static QueryResult parseQueryResult(InputStream jsonStream) throws IOException {

        QueryResult result = new QueryResult();
		String curTag;
		JsonReader reader = null;

		try {
			reader = new JsonReader(new InputStreamReader(jsonStream, "UTF-8"));
            reader.beginObject();

            while(reader.hasNext()) {
                curTag = reader.nextName();
                if (curTag.equals(TAG_TUPLES) && reader.peek() != JsonToken.NULL) {
                    result.tuples = readTupleList(reader);
                    for(List<String> tuple : result.tuples)
                    {
                        for(String att: tuple)
                        {
                            result.size += ObjectSizer.getStringSize32bits(att.length());
                        }
                    }
                } else if (curTag.equals(TAG_COST) && reader.peek() != JsonToken.NULL) {
                    result.cost  = readProcessCost(reader);
                } else {
                    reader.skipValue();
                }
            }
		}
        catch (OutOfMemoryError e)
        {

        }
		finally
		{
			reader.close();
		}
		
		return result;
	}

	public static Estimation parseEstimation(String stringJSON) throws JSONParserException {
		
		Estimation estimation = new Estimation();
		
		JSONObject estimationJSONObject = null;
		try {
			estimationJSONObject = new JSONObject(stringJSON);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
            if (stringJSON.contains("Not found"))
            {
            	Log.e("JSON Parser", "Error URL not found");
            	throw new JSONParserException("Url Not Found Exception");
            }   		
        }
		
		try
		{
			estimation.setDuration(estimationJSONObject.getLong(TAG_TIME));
			estimation.setMonetaryCost(estimationJSONObject.getDouble(TAG_MONEY));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		Log.d("ESTIMATION","estimation: " + estimation);
		
		return estimation;
	}

	
	public static final class QueryResult
    {
        public List<List<String>> tuples = null;
        public long size = 0;
        public Object[] cost = null;
    }
	
}
