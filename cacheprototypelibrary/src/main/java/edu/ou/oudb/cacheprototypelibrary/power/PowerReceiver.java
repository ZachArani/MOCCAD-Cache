package edu.ou.oudb.cacheprototypelibrary.power;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;


public abstract class PowerReceiver extends BroadcastReceiver {

	private int mBatteryLevel = 0;
	private int mBatteryScale = 100;
	private double mBatteryCapacity = 0;
	protected PowerProfile mPowerProfile;

    public PowerReceiver()
    {

    }

	public PowerReceiver(Context context)
	{
		mPowerProfile = new PowerProfile(context);
		
		double batteryCapacity = mPowerProfile.getBatteryCapacity();
		
		if (batteryCapacity > 0)
		{
			this.mBatteryCapacity = batteryCapacity;
		}
		
		
		
		Intent batteryIntent = context.registerReceiver(this, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		mBatteryLevel = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		Log.d("BATTERY_STATUS","battery_level: " + mBatteryLevel);
		mBatteryScale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		Log.d("BATTERY_STATUS","battery_scale: " + mBatteryScale);
		double voltage = batteryIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
		Log.d("BATTERY_STATUS","battery_voltage: " + voltage);
		
		if (mBatteryLevel == -1 || mBatteryScale == -1)
		{
			throw new IllegalStateException();
		}
				
	}
	
	/**
	 * get the CPU Frequency in hertz
	 * @return the cpu frequency
	 */
	public abstract long getCPUFreq();
	
	/**
	 * Get the current to be consumed by the cpu in mA 
	 * @param freq in kHz
	 * @return the current to be consumed by the cpu in mA
	 */
	public abstract double getCPUCurrent(long freq);
	
	/**
	 * Get the current to be consumed while in wifi
	 * @return the current consumed in wifi when active
	 */
	public abstract double getWifiActiveCurrent();
	
	/**
	 * Get the current to be consumed while in wifi is just on
	 * @return the current consumed in wifi when on
	 */
	public abstract double getWifiOnCurrent();
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		mBatteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
		mBatteryScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
	}
	
	public double getBatteryLevel()
	{
		return mBatteryLevel*mBatteryCapacity/mBatteryScale;
	}
	
}
