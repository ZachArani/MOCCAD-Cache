package edu.ou.oudb.cacheprototypelibrary.connection;

import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ou.oudb.cacheprototypelibrary.estimationcache.Estimation;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.DownloadDataException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.JSONParserException;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.QuerySegment;

/**
 * @author Mikael Perrin
 * @since <version>
 * Interface wrapping the connexion with the server/cloud services
 */
public interface DataAccessProvider {

	public static final String PREF_METADATA = "pref_metadata";
	public static String PREF_IP_ADDRESS = "pref_ip_address";
	public static String PREF_PORT = "pref_port";
	
	/**
	 * CAUTION: return attributeTypes as LowerCase
	 * Get all the attribute types of a relation
	 * @param relation the corresponding relation
	 * @return the attribute types as LOWERCASE!!!
	 */
	public List<String> getAttributeTypes(String relation);
	
	/**
	 * CAUTION: return attributeNames as LowerCase
	 * Get all the attribute names of a relation
	 * @param relation the corresponding relation
	 * @return the attribute name as LOWERCASE!!!
	 */
	public List<String> getAttributeNames(String relation);
	
	/**
	 * get the number of tuples inside a relation
	 * @param relation the corresponding relation
	 * @return the number of tuples
	 */
	public long getNbTuples(String relation);

    /**
     * Get the average count (in byte) of a tuple inside the relation
     * @param relationName the considered relation
     * @return the count of a tuple
     */
    public double getAvgTupleSize(String relationName);

	/**
	 * Get the maximum count (in byte) of a tuple inside the relation
	 * @param relation the corresponding relation
	 * @return the count of a tuple
	 */
	public int getMaxTupleSize(String relation);
	
	/**
	 * get the number of attributes for the given relation
	 * @param relation the corresponding relation
	 * @return teh number of attributes for this relation
	 */
	public int getNbAttributes(String relation);
	
	/**
	 * Process the given query on the service
	 * @param query the query to be processed
	 * @return the result of the query as a QuerySegment
	 * @throws DownloadDataException 
	 * @throws JSONParserException 
	 */
	public QuerySegment process(Query query) throws JSONParserException, DownloadDataException;
	
	/**
	 * Get the names of the relations inside the database
	 * @return the list of all relation names
	 */
	public Set<String> getRelationNames();
	
	/**
	 * Method used to ask how much time energy and money
	 * the query will spend when running on the this provider
	 * @param query the query to be estimated
	 * @return the corresponding estimation
	 * @throws JSONParserException 
	 * @throws DownloadDataException 
	 */
	public Estimation estimate(Query query) throws JSONParserException, DownloadDataException;

	/**
	 * For each attribute it associate the minimum value in the table
	 * If the attribute is not quantifiable, put null
	 * @param relationName the considered relation
	 * @return the mapping attribute -> min Value
	 */
	public Map<String, Double> getMinValueForAttributes(String relationName);
	
	/**
	 * For each attribute it associate the maximum value in the table
	 * If the attribute is not quantifiable, put null
	 * @param relationName the considered relation
	 * @return the mapping attribute -> max Value
	 */
	public Map<String, Double> getMaxValueForAttributes(String relationName);

	/**
	 * For each attribute it associate the number of different values in the table
	 * @param relationName the considered relation
	 * @return the mapping attribute -> number of different values
	 */
	public Map<String, Long> getNbDifferentValuesForAttributes(String relationName);



}
