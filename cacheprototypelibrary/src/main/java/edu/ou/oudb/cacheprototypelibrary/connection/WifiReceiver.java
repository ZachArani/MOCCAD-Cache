package edu.ou.oudb.cacheprototypelibrary.connection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.ConnectException;

/**
 * @author Mikaël Perrin
 * @since 1.0
 * Class defining the subscription to the broadcast of wifi information from the device.
 */
public class WifiReceiver extends BroadcastReceiver{
	
	private WifiInfo mWifiInfo = null;
	
	private double mSpeedBps = 0;
	
	boolean isConnected = false;
	
	// to be modefied when experimenting at an other place than DEH220 
	// value retreived with speed test
	//private static final double MAX_SPEED_BPS = 1750000;
	
	//in Carson room 34 (conference room)
	//private static final double MAX_SPEED_BPS = 625000;
	
	//at campus lodge
	//private static final double MAX_SPEED_BPS = 1100000;
	//private static final double MAX_SPEED_BPS = 1010000;
	private static final double MAX_SPEED_BPS = 3000000;
	
	public WifiReceiver() {} /* public constructor for receiver */
	
	public WifiReceiver(Context context)
	{
		updateConnectionInfo(context);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		updateConnectionInfo(context);
	
	}
	
	private void updateConnectionInfo(Context context)
	{
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (info != null)
		{
			if(info.isConnected())
			{
				WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				mWifiInfo = wifiManager.getConnectionInfo();
				isConnected = true;

			}
			else
			{
				isConnected = false;
			}
		}
	}
	
	public double getSpeed() throws ConnectException
	{
		if (isConnected)
		{
			//wifiInfo returns speed in Mbps
			mSpeedBps = mWifiInfo.getLinkSpeed()*1000000/8.0;
			return (mSpeedBps > MAX_SPEED_BPS)? MAX_SPEED_BPS: mSpeedBps;
		}
		else
		{
			throw new ConnectException();
		}
	}
	
}
