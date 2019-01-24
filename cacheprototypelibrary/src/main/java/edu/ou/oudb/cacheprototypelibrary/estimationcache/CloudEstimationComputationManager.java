package edu.ou.oudb.cacheprototypelibrary.estimationcache;

import android.content.Context;
import android.util.Log;

import java.net.ConnectException;

import edu.ou.oudb.cacheprototypelibrary.connection.DataAccessProvider;
import edu.ou.oudb.cacheprototypelibrary.connection.WifiReceiver;
import edu.ou.oudb.cacheprototypelibrary.metadata.Metadata;
import edu.ou.oudb.cacheprototypelibrary.metadata.RelationMetadata;
import edu.ou.oudb.cacheprototypelibrary.power.HtcOneM7ulPowerReceiver;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.DownloadDataException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.JSONParserException;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Predicate;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.XopCPredicate;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.XopYPredicate;

/**
 * @author Mikael Perrin
 * @since 1.0
 * Class used to compute the estimation to process the query on the cloud
 */
public class CloudEstimationComputationManager implements EstimationComputationManager{
	
	DataAccessProvider mDataAccessProvider = null;
    WifiReceiver mWifiReceiver = null;

	public CloudEstimationComputationManager(Context context, DataAccessProvider dataAccessProvider)
	{
		mDataAccessProvider = dataAccessProvider;
        mWifiReceiver = new WifiReceiver(context);
	}
	
	public void setDataAccessProvider(DataAccessProvider dataAccessProvider)
	{
		mDataAccessProvider = dataAccessProvider;
	}
	
	
	/**
	 * Used to estimate the energy on Low Network state
	 * @param duration the estimated duration in nanosecond
	 * @return the electric Charge in mAh
	 */
	public static double estimateEnergyLowNetwork(long duration)
	{	
		long nbNsInAnHour = 3600000000000L;
		double current = HtcOneM7ulPowerReceiver.getInstance().getWifiOnCurrent();
		
		double result = current * duration/nbNsInAnHour;
		
		Log.d("ESTIMATION","estimateEnergyLowNetwork: "+ result);
		
		return result;
	}
	
	public static double estimateEnergyHighNetwork(long duration)
	{
		long nbNsInAnHour = 3600000000000L;
		double current = HtcOneM7ulPowerReceiver.getInstance().getWifiActiveCurrent();
		long cpuFreq = HtcOneM7ulPowerReceiver.getInstance().getCPUFreq();
		
		if(cpuFreq > 0)
		{
			current += HtcOneM7ulPowerReceiver.getInstance().getCPUCurrent(cpuFreq/1000);
		}
		
		double result = current * duration/nbNsInAnHour;
		
		Log.d("ESTIMATION","estimateEnergyHighNetwork: "+ result);
		
		return result;
	}
	
	@Override
	public Estimation estimate(Query q) throws ConnectException, JSONParserException, DownloadDataException {
		
		Estimation result = null;
		
		// get the estimation for time and money
		Estimation estimationProcessCloud = mDataAccessProvider.estimate(q);
		
		// compute the estimation for energy
		estimationProcessCloud.setEnergy(estimateEnergyLowNetwork(estimationProcessCloud.getDuration()));
		
		long timeForDownload = estimateTimeDownload(q);
		
		Estimation estimationDownload = new Estimation();
		
		estimationDownload.setDuration(timeForDownload);
		
		estimationDownload.setEnergy(estimateEnergyHighNetwork(timeForDownload));
	
		result = Estimation.add(estimationDownload,estimationProcessCloud);
		
		Log.d("ESTIMATION", "CloudEstimationComputationManager:" + result);
		
		return result;
	}

	/**
	 * Return the estimated time to download the result in nanoseconds
	 * @param q the query to be processed
	 * @return the estimated time in nanoseconds
	 * @throws java.net.ConnectException
	 */
	private long estimateTimeDownload(Query q) throws ConnectException 
	{
		double speed =  mWifiReceiver.getSpeed(); // in BytePerSeconds
		Log.d("speed", speed +"");
		
		double estimatedSize = estimateQuerySize(q);
		long result = (long) (estimatedSize * 1000000000 / speed);
		
		Log.d("ESTIMATION","estimateTimeDownload: "+ result);
		
		return result;
	}

	/**
	 * Estimate predicate count with formulas from Database System Concepts group
	 * ONLY WORKS FOR XopCPredicates with C being a real number of an integer.
	 * We do not make any difference here between <= or <, or if the value is an integer or
	 * a double. Ref Database system concepts
	 * @param p the given predicate
	 * @param relationSize the number of tuples in the relation on which the query predicate will be applied
	 * @param minValueForAttribute the minimum value for the given attribute in the predicate
	 * @param maxValueForAttribute the maximum value for the given attribute in the predicate
	 * @param nbDifferentValueForAttribute the number of different values for the given attribute in the given relation
	 * @return the estimated result count as a number of tuples.
	 */
	private double estimatePredicateSize(XopCPredicate p, long relationSize, double minValueForAttribute, double maxValueForAttribute, long nbDifferentValueForAttribute)
	{
		double size = 0;
		
		double rightOperand = p.getRightOperand();
		switch(p.getOperator())
		{
		case "<=": case "<":
			if(rightOperand < minValueForAttribute)
			{
				size = 0;
			}
			else if(rightOperand > maxValueForAttribute)
			{
				size = relationSize;
			}
			else
			{
				size = relationSize * (rightOperand - minValueForAttribute)/(maxValueForAttribute - minValueForAttribute);
			}
			break;
		case ">": case ">=":
			if(rightOperand < minValueForAttribute)
			{
				size = relationSize;
			}
			else if(rightOperand > maxValueForAttribute)
			{
				size = 0;
			}
			else
			{
				size = relationSize * (maxValueForAttribute - rightOperand)/(maxValueForAttribute - minValueForAttribute);
			}
			break;
		case "=":
			size = relationSize / nbDifferentValueForAttribute;
			break;
		}
		
		return size;
	}
	
	/**
	 * Estimate predicate count with formulas from Database System Concepts group
	 * ONLY WORKS FOR XopYPredicates
	 * @param p the given XopY predicate
	 * @param relationSize the count of the relation in terms of number of tuples
	 * @return the estimated count as a number of tuple
	 */
	private double estimatePredicateSize(XopYPredicate p, long relationSize)
	{
		return relationSize/2; /* the best we can do without additional information */
	}
	
	private long estimateQuerySize(Query q)
	{
		long relationSize = Metadata.getInstance().getRelationMetadata(q.getRelation()).getNbTuples();
		RelationMetadata relationMetadata = Metadata.getInstance().getRelationMetadata(q.getRelation());
		double querySize = relationSize;
		double excludedQuerySize = relationSize;
		double minValueForAttribute = 0;
		double maxValueForAttribute = 0;
		double tupleSize = relationMetadata.getMaxTupleSize();
		long nbDifferentValueForAttribute = 0;
		
		// for each predicate we estimate the number of tuple they may return
		// when there are n predicates, we have S = nb_tuple(r) * (S1...Sn)/(nb_tuple(r)^n)
		for(Predicate p: q.getPredicates())
		{
			if(p instanceof XopCPredicate)
			{
				minValueForAttribute = relationMetadata.getAttributeMinValue(((XopCPredicate) p).getLeftOperand());
				maxValueForAttribute = relationMetadata.getAttributeMaxValue(((XopCPredicate) p).getLeftOperand());
				nbDifferentValueForAttribute = relationMetadata.getAttributeNbValue(((XopCPredicate) p).getLeftOperand());
				querySize *= estimatePredicateSize((XopCPredicate) p, relationSize, minValueForAttribute, maxValueForAttribute, nbDifferentValueForAttribute) / relationSize;
			}
			else if (p instanceof XopYPredicate)
			{
				querySize *= estimatePredicateSize((XopYPredicate) p, relationSize) / relationSize;
			}
		}
		
		if (!q.getExcludedPredicates().isEmpty())
		{
			for(Predicate p: q.getExcludedPredicates())
			{
				if(p instanceof XopCPredicate)
				{
					minValueForAttribute = relationMetadata.getAttributeMinValue(((XopCPredicate) p).getLeftOperand());
					maxValueForAttribute = relationMetadata.getAttributeMaxValue(((XopCPredicate) p).getLeftOperand());
					nbDifferentValueForAttribute = relationMetadata.getAttributeNbValue(((XopCPredicate) p).getLeftOperand());
					excludedQuerySize *= estimatePredicateSize((XopCPredicate) p, relationSize, minValueForAttribute, maxValueForAttribute, nbDifferentValueForAttribute) / relationSize;
				}
				else if (p instanceof XopYPredicate)
				{
					excludedQuerySize *= estimatePredicateSize((XopYPredicate) p, relationSize) / relationSize;
				}
			}
			
			// we substract the excludedQuerySize from the querySize
			querySize -= excludedQuerySize;
		}
		
		// Then, with number of tuples, we multiply by the count of a tuple (we do not have projections)
		querySize *= tupleSize;
		
		Log.d("ESTIMATION","estimateQuerySize: "+ querySize);
		
		return (long) querySize; // count in octet troncated
	}

}
