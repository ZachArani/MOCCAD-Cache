package edu.ou.oudb.cacheprototypelibrary.estimationcache;

import android.util.Log;
import edu.ou.oudb.cacheprototypelibrary.core.cachemanagers.CacheContentManager;
import edu.ou.oudb.cacheprototypelibrary.power.HtcOneM7ulPowerReceiver;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.QuerySegment;



/**
 * @author Mikael Perrin
 * @since 1.0
 * Class used to compute the estimation to process the query on the mobile device
 */
public class MobileEstimationComputationManager implements EstimationComputationManager{

	CacheContentManager<Query,QuerySegment> mCacheContentManager = null;
	
	public MobileEstimationComputationManager(CacheContentManager<Query,QuerySegment> contentManager)
	{
		mCacheContentManager = contentManager;
	}
	
	/**
	 * @param entryQuery the query for which we estimate the processing time
	 * @return the estimated time in nanosecond
	 */
	private long estimateTime(Query entryQuery)
	{
		//long estimatedNbOperations = 0;
		long nbTuples =  mCacheContentManager.get(entryQuery).getNbTuples();
		//long nbPredicates = q.getPredicates().count() + q.getExcludedPredicates().count();
		//long cpuFreq = mPowerReceiver.getCPUFreq(); // frequency in Hz
		long estimatedTime = 0;
		
		//T(nbTuples) = nbTuples * T(analyzeTuple)
		//T(analyzeTuple) = nbPredicates * 1 (1 = One Operation to compare)
		//estimatedNbOperations = nbTuples * nbPredicates;
		
		// so the number of nano seconds to do those operations is:
		//estimatedTime = estimatedNbOperations * 1000000000 / cpuFreq;
		
		// with statistic model
		estimatedTime = 48332 * nbTuples;
		
		
		Log.d("ESTIMATION", "estimateTime: "+estimatedTime);
		
		return estimatedTime;
	}
	
	/**
	 * Used to estimate the energy
	 * @param duration the estimated duration in nanosecond
	 * @return the electric Charge in mAh
	 */
	public static double estimateEnergy(long duration)
	{	
		long cpuFreq = 0; /* in Hz */
		double current = 0; /* in mA */
		double electricCharge = 0; /* in mAh */
		long nbNsInAnHour = 3600000000000L;
		
		cpuFreq = HtcOneM7ulPowerReceiver.getInstance().getCPUFreq();
		
		if(cpuFreq > 0)
		{
			current = HtcOneM7ulPowerReceiver.getInstance().getCPUCurrent(cpuFreq/1000);
			if(current > 0)
		    {
				electricCharge = (current*duration/nbNsInAnHour);
		    }
		}	    
		
		Log.d("ESTIMATION", "estimateEnergy: "+electricCharge);
	    
	    return electricCharge;
	}
	
	@Override
	public Estimation estimate(Query q) {
		long estimatedTime = estimateTime(q);
		double estimatedEnergy = estimateEnergy(estimatedTime);
		Estimation estimation = null;
		
		if (estimatedTime >= 0 && estimatedEnergy >= 0)
		{
			estimation = new Estimation(estimatedTime, estimatedEnergy, 0);
		}

		Log.d("ESTIMATION", "MobileEstimationComputationManager:" + estimation);
		
		return estimation;
	}
	
}
