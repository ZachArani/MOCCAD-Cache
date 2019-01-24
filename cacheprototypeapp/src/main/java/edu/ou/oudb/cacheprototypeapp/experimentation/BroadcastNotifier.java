package edu.ou.oudb.cacheprototypeapp.experimentation;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class BroadcastNotifier {

	public static final String BROADCAST_ACTION = "edu.ou.oudb.cacheprototypeapp.BROADCAST";
	public static final String EXTENDED_DATA_STATUS = "edu.ou.oudb.cacheprototypeapp.STATUS";
	public static final String EXTENDED_STATUS_LOG = "edu.ou.oudb.cacheprototypeapp.LOG";
	
	
	public static final int STATE_ACTION_STARTED = 0;
	public static final int STATE_ACTION_PROGRESSED = 1;
	public static final int STATE_ACTION_COMPLETED = 2;
	public static final int STATE_ACTION_WARMUP_STARTED = 3;
	public static final int STATE_ACTION_WARMUP_PROGRESSED = 4;
	public static final int STATE_ACTION_WARMUP_COMPLETED = 5;
	public static final int ERROR_CONNECTION = -1;
	public static final int ERROR_DOWNLOAD_DATA = -2;
	public static final int ERROR_JSON_PARSER = -3;
	public static final int ERROR = -4;
	
	private LocalBroadcastManager mBroadcaster;
	
	public BroadcastNotifier(Context context) {

        mBroadcaster = LocalBroadcastManager.getInstance(context);

    }
	
	public void broadcastIntentWithError(int status) {

	    Intent localIntent = new Intent();
	
	    // The Intent contains the custom broadcast action for this app
	    localIntent.setAction(BROADCAST_ACTION);
	
	    // Puts the status into the Intent
	    localIntent.putExtra(EXTENDED_DATA_STATUS, status);
	    localIntent.addCategory(Intent.CATEGORY_DEFAULT);
	
	    // Broadcasts the Intent
	    mBroadcaster.sendBroadcast(localIntent);
	}
	
	public void notifyProgress(int status, String logData) {

        Intent localIntent = new Intent();

        // The Intent contains the custom broadcast action for this app
        localIntent.setAction(BROADCAST_ACTION);

        localIntent.putExtra(EXTENDED_DATA_STATUS, status);

        // Puts log data into the Intent
        localIntent.putExtra(EXTENDED_STATUS_LOG, logData);
        localIntent.addCategory(Intent.CATEGORY_DEFAULT);

        // Broadcasts the Intent
        mBroadcaster.sendBroadcast(localIntent);

    }
	
}
