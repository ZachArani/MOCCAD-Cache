package edu.ou.oudb.cacheprototypelibrary.power;

import java.io.IOException;
import java.io.RandomAccessFile;

import android.content.Context;

public class HtcOneM7ulPowerReceiver extends PowerReceiver{

	private static HtcOneM7ulPowerReceiver instance = null;
	
	private HtcOneM7ulPowerReceiver(Context context) {
		super(context);
	}

    public HtcOneM7ulPowerReceiver()
    {
        super();
    }

	
	public static void init(Context context)
	{
		if (instance == null && context != null)
			instance = new HtcOneM7ulPowerReceiver(context);
	}
	
	public static HtcOneM7ulPowerReceiver getInstance()
	{
		if (instance == null)
			throw new IllegalStateException("have you called init(context)?");
		
		return instance;
	}


	public long getCPUFreq()
    {
		String cpuFreq;
		RandomAccessFile reader;
		try {
			reader = new RandomAccessFile("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq", "r");
			cpuFreq = reader.readLine();
		    reader.close();
		} catch (IOException e) {
			cpuFreq = "-1";
		}
		
    	return Long.parseLong(cpuFreq) * 1000;
    }

	@Override
	public double getCPUCurrent(long freq) {
		return mPowerProfile.getCPUPower(freq);
	}

	@Override
	public double getWifiActiveCurrent() {
		return mPowerProfile.getAveragePower(PowerProfile.POWER_WIFI_ACTIVE);
	}

	@Override
	public double getWifiOnCurrent() {
		return mPowerProfile.getAveragePower(PowerProfile.POWER_WIFI_ON);
	}
}
