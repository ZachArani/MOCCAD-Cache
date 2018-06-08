package edu.ou.oudb.cacheprototypelibrary.connection;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ou.oudb.cacheprototypelibrary.estimationcache.Estimation;
import edu.ou.oudb.cacheprototypelibrary.metadata.RelationMetadata;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.DownloadDataException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.JSONParserException;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.QuerySegment;
import edu.ou.oudb.cacheprototypelibrary.utils.JSONLoader;
import edu.ou.oudb.cacheprototypelibrary.utils.JSONParser;
import edu.ou.oudb.cacheprototypelibrary.utils.StatisticsManager;

/**
 * @author Mikaël Perrin
 * @since 1.0
 * Class implementing the different methods to be able to connect to a cloud (or rather
 * the web service connected to the cloud and other databases, aka the data owner)
 */
public class CloudDataAccessProvider implements DataAccessProvider{


	private static final String URL_DB_INFO = "dbinfo/relations";
	private static final String URL_EXTRA_QUERY = "result?query=";
	private static final String URL_EXTRA_ESTIMATION = "estimation?query=";

	private static final int CLOUD_COEFF = 5;

	private String mURLBase = "http://10.204.69.210:8080/CloudWebService/rest/";

	private String mURLGetRelationMetadata = mURLBase + URL_DB_INFO;

	private String mURLProcessQueryBase = mURLBase + URL_EXTRA_QUERY;

	private String mURLEstimateBase = mURLBase + URL_EXTRA_ESTIMATION;

	private Map<String, RelationMetadata> dbInfoMap = null;

	public CloudDataAccessProvider(Context context) throws DownloadDataException, JSONParserException
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

		String ipAddress = pref.getString(PREF_IP_ADDRESS,"10.204.69.210"); //school is 10.204.69.210, Jason home is 192.168.0.132 - change in two places here, and in preferences.xml in cacheprototypeapp

		String port = pref.getString(PREF_PORT,"8080");

		StringBuilder urlBaseBuilder = new StringBuilder("http://");
		urlBaseBuilder.append(ipAddress);
		urlBaseBuilder.append(":");
		urlBaseBuilder.append(port);
		urlBaseBuilder.append("/CloudWebService/rest/");
		mURLBase = urlBaseBuilder.toString();

		mURLGetRelationMetadata = mURLBase + URL_DB_INFO;
		mURLProcessQueryBase = mURLBase + URL_EXTRA_QUERY;
		mURLEstimateBase = mURLBase + URL_EXTRA_ESTIMATION;

		dbInfoMap = new HashMap<String, RelationMetadata>();

		String dbInfo = null;
		InputStream jsonStream;
		// save the string result into preferences

		String cloudMetadata = pref.getString(PREF_METADATA, "");
		//Below used because empty metadata seems to give a string with brackets in it
		String cloudMetadata2 =cloudMetadata.replaceAll("\\[","");
		cloudMetadata2 =cloudMetadata2.replaceAll("\\]","");
		if(cloudMetadata2.isEmpty())
		{
			jsonStream = JSONLoader.getJSONInputStreamFromUrl(mURLGetRelationMetadata);
			BufferedReader r = new BufferedReader(new InputStreamReader(jsonStream));
			StringBuilder json = new StringBuilder();
			String line;
			try {
				while ((line = r.readLine()) != null) {
					json.append(line);
				}
			} catch (IOException e) {
				throw new JSONParserException("io exception");
			}
			dbInfo = json.toString();
			SharedPreferences.Editor edit = pref.edit();
			edit.putString(PREF_METADATA, json.toString());
			edit.commit();
		}
		else
		{
			dbInfo = cloudMetadata;
		}

		try{
			dbInfoMap = JSONParser.parseDBInfo(dbInfo);
		} catch (JSONParserException e){
			throw e;
		}

		//<editor-fold desc="LOG CLOUD COEFF">
		StatisticsManager.cloudCoefficient(CLOUD_COEFF);
		//</editor-fold>
	}

	@Override
	public List<String> getAttributeTypes(String relation) {
		return dbInfoMap.get(relation).getAttributeTypes();
	}

	@Override
	public List<String> getAttributeNames(String relation) {
		return dbInfoMap.get(relation).getAttributeNames();
	}

	@Override
	public long getNbTuples(String relation) {
		return dbInfoMap.get(relation).getNbTuples();
	}

	@Override
	public QuerySegment process(Query query) throws JSONParserException, DownloadDataException {

		QuerySegment querySegment = null;
		StringBuilder sb = new StringBuilder();
		sb.append(mURLProcessQueryBase);
		sb.append(query.toSQLString()
				.replace(" ", "%20")
				.replace("<", "%3C")
				.replace(">","%3E")
				.replace(";", ""));
		String urlString = sb.toString();

		//<editor-fold desc="LOG START CLOUD PROCESS">
		long startCloudProcess = StatisticsManager.startCloudProcess();
		//</editor-fold>
		InputStream jsonStream = JSONLoader.getJSONInputStreamFromUrl(urlString);

		//<editor-fold desc="LOG START DOWNLOAD RESULT">
		long startDownloadData = StatisticsManager.startDownloadData();
		//</editor-fold>

		JSONParser.QueryResult result;
		try {
			result = JSONParser.parseQueryResult(jsonStream);
			if (result == null)
			{
				throw new JSONParserException();
			}
		} catch (IOException e) {
			throw new JSONParserException(e.getMessage());
		}
		querySegment = new QuerySegment(result.tuples);

		//<editor-fold desc="LOG STOP DOWNLOAD DATA">
		StatisticsManager.stopDownloadData(startDownloadData);
		//</editor-fold>

		//<editor-fold desc="LOG STOP CLOUD PROCESS">
		StatisticsManager.stopCloudProcess(startCloudProcess,((Long) result.cost[1])/CLOUD_COEFF,((Double) result.cost[0])*CLOUD_COEFF);
		//</editor-fold>

		//<editor-fold desc="LOG NEW DOWNLOADED RESULT SIZE">
		StatisticsManager.newResultSize(result.size);
		//</editor-fold>


		return querySegment;

	}

	@Override
	public Set<String> getRelationNames() {
		return dbInfoMap.keySet();
	}

	@Override
	public int getNbAttributes(String relation) {
		return dbInfoMap.get(relation).getAttributeNames().size();
	}

	@Override
	public Estimation estimate(Query query) throws JSONParserException, DownloadDataException {

		Estimation estimation = null;
		StringBuilder sb = new StringBuilder();
		sb.append(mURLEstimateBase);
		sb.append(query.toSQLString()
				.replace(" ", "%20")
				.replace("<", "%3C")
				.replace(">","%3E")
				.replace(";", ""));
		String urlString = sb.toString();

		//<editor-fold desc="LOG START COMPUTE CLOUD ESTIMATION">
		long startComputeCloudEstimation = StatisticsManager.startComputeCloudEstimation();
		//</editor-fold
		InputStream jsonStream = JSONLoader.getJSONInputStreamFromUrl(urlString);

		//<editor-fold desc="LOG STOP COMPUTE CLOUD ESTIMATION">
		/* to be replaced with cost returned by the cloud */
		long tmpTimeElapsedComputeCloudEstimation = SystemClock.elapsedRealtimeNanos() - startComputeCloudEstimation;
		StatisticsManager.stopComputeCloudEstimation(startComputeCloudEstimation,tmpTimeElapsedComputeCloudEstimation,0.053 * tmpTimeElapsedComputeCloudEstimation/3600000000000L);
		//</editor-fold>

		//<editor-fold desc="LOG START DOWNLOAD ESTIMATION RESULT">
		long startDownloadCloudEstimation = StatisticsManager.startDownloadEstimationResult();
		//</editor-fold>
		BufferedReader r = new BufferedReader(new InputStreamReader(jsonStream));
		StringBuilder json = new StringBuilder();
		String line;
		try {
			while ((line = r.readLine()) != null) {
				json.append(line);
			}
		} catch (IOException e) {
			throw new JSONParserException("io exception");
		}

		//<editor-fold desc="LOG STOP DOWNLOAD ESTIMATION RESULT">
		StatisticsManager.stopDownloadEstimationResult(startDownloadCloudEstimation);
		//</editor-fold>

		estimation = JSONParser.parseEstimation(json.toString());

		estimation.setDuration(estimation.getDuration()/CLOUD_COEFF);
		estimation.setMonetaryCost(estimation.getMonetaryCost()*CLOUD_COEFF);

		return estimation;
	}

	@Override
	public double getAvgTupleSize(String relation) { return dbInfoMap.get(relation).getAvgTupleSize(); }

	@Override
	public int getMaxTupleSize(String relation) {
		return dbInfoMap.get(relation).getMaxTupleSize();
	}

	@Override
	public Map<String, Double> getMinValueForAttributes(
			String relationName) {
		return dbInfoMap.get(relationName).getMinValueForAttributeMap();
	}

	@Override
	public Map<String, Double> getMaxValueForAttributes(
			String relationName) {
		return dbInfoMap.get(relationName).getMaxValueForAttributeMap();
	}

	@Override
	public Map<String,Long> getNbDifferentValuesForAttributes(
			String relationName) {
		return dbInfoMap.get(relationName).getNbDifferentValuesForAttributeMap();
	}


}
