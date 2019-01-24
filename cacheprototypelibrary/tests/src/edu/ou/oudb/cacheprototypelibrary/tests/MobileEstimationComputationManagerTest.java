package edu.ou.oudb.cacheprototypelibrary.tests;

import android.test.AndroidTestCase;
import android.util.Log;
import edu.ou.oudb.cacheprototypelibrary.estimationcache.MobileEstimationComputationManager;
import edu.ou.oudb.cacheprototypelibrary.power.HtcOneM7ulPowerReceiver;

public class MobileEstimationComputationManagerTest extends AndroidTestCase {

	@Override
	protected void setUp() throws Exception {
		HtcOneM7ulPowerReceiver.init(getContext());
		super.setUp();
	}
	
	public void testEstimateEnergy()
	{	
		long duration = 2000000000; // 2 seconds
		double estimatedEnergy = 0;
		
		estimatedEnergy = MobileEstimationComputationManager.estimateEnergy(duration);
		Log.d("ESTIMATION_ENERGY","estimate_energy: "+estimatedEnergy);
		// values tested for HTC ONE M7ul
		assertTrue(estimatedEnergy >= 0.0152 && estimatedEnergy <= 0.09);
	
	}
	
}
