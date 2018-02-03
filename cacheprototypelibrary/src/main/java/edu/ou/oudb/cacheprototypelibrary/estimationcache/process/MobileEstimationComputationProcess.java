package edu.ou.oudb.cacheprototypelibrary.estimationcache.process;

import edu.ou.oudb.cacheprototypelibrary.core.process.Process;
import edu.ou.oudb.cacheprototypelibrary.estimationcache.Estimation;
import edu.ou.oudb.cacheprototypelibrary.estimationcache.MobileEstimationComputationManager;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;


public class MobileEstimationComputationProcess implements Process<Query, Estimation> {

	MobileEstimationComputationManager mMobileEstimationComputationManager = null;
	
	public MobileEstimationComputationProcess(MobileEstimationComputationManager mobileEstiCompManager)
	{
		mMobileEstimationComputationManager = mobileEstiCompManager;
	}
	
	@Override
	public Estimation run(Query query) {
		return mMobileEstimationComputationManager.estimate(query);
	}

}
