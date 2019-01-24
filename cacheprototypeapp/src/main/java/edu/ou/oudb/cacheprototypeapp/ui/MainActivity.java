package edu.ou.oudb.cacheprototypeapp.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import edu.ou.oudb.cacheprototypeapp.AndroidCachePrototypeApplication;
import edu.ou.oudb.cacheprototypeapp.R;
import edu.ou.oudb.cacheprototypeapp.experimentation.BroadcastNotifier;
import edu.ou.oudb.cacheprototypeapp.experimentation.ExperimentationService;
import edu.ou.oudb.cacheprototypeapp.provider.MedicalInfoDbHelper;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.DownloadDataException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.JSONParserException;
import edu.ou.oudb.cacheprototypelibrary.utils.JSONLoader;
import edu.ou.oudb.cacheprototypelibrary.utils.StatisticsManager;

public class MainActivity extends Activity implements ActionBar.TabListener {

	private SectionsPagerAdapter mSectionsPagerAdapter;

	private ViewPager mViewPager;
	
	private AndroidCachePrototypeApplication mApplication = null;
	
	private SharedPreferences prefs = null;
	
	private Intent mExpService = null;
	
	private NotificationManager mNotifyManager = null;
	private NotificationCompat.Builder mBuilder = null;
	
	private ProgressDialog mProgressExpDialog = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mApplication = (AndroidCachePrototypeApplication) getApplication();
		
		prefs = getSharedPreferences("edu.ou.oudb.cacheprototypeapp", MODE_PRIVATE);
		
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager(),this);

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});


		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		
		mViewPager.setCurrentItem(0);
		mViewPager.setOffscreenPageLimit(3);
		
		(new InitializingTask()).execute();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		prefs.edit().putBoolean("firstrun", false).apply();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		switch(id)
		{
		case R.id.action_settings:
			startSettings();
			return true;
		case R.id.action_add:
			startWeightsActivity();
			return true;
		case R.id.action_stats:
			//startStatsActivity();
			startExperimentation();
			return true;
		case R.id.action_experimentation:
			startExperimentation();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public QueryCacheFragment getQueryCacheFragment()
	{
		return (QueryCacheFragment) getFragmentManager().findFragmentByTag(makeFragmentName(mViewPager.getId(), 1));
	}
	
	public MobileEstimationCacheFragment getMobileEstimationCacheFragment()
	{
		return (MobileEstimationCacheFragment) getFragmentManager().findFragmentByTag(makeFragmentName(mViewPager.getId(), 2));
	}
	
	public CloudEstimationCacheFragment getCloudEstimationCacheFragment()
	{
		return (CloudEstimationCacheFragment) getFragmentManager().findFragmentByTag(makeFragmentName(mViewPager.getId(), 3));
	}
	
	private static String makeFragmentName(int viewId, int index) {
	     return "android:switcher:" + viewId + ":" + index;
	}
	
	private void startExperimentation() {
		mExpService = new Intent(this, ExperimentationService.class);
		IntentFilter statusIntentFilter = new IntentFilter(BroadcastNotifier.BROADCAST_ACTION);
		statusIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);
		ExperimentationStateReceiver experimentationStateReceiver = new ExperimentationStateReceiver();
		LocalBroadcastManager.getInstance(this).registerReceiver(experimentationStateReceiver, statusIntentFilter);
		startService(mExpService);
	}

	private void startNewQueryActivity() {
		Intent intent = new Intent(this, NewQueryActivity.class);
		startActivity(intent);
	}
	
	private void startStatsActivity() {
		Intent intent = new Intent(this, StatisticsActivity.class);
		startActivity(intent);
	}

	private void startSettings() {
		Intent intent = new Intent(this,SettingsActivity.class);
		startActivity(intent);
	}

	private void startWeightsActivity(){
		Intent intent = new Intent(this, WeightProfilesActivity.class);
		startActivity(intent);
	}

	private void startErrorDialog(String title, String message)
	{
		ErrorDialog errorDialog = ErrorDialog.newInstance(title, message);
		errorDialog.show(getFragmentManager(),"error_dialog");
	}
	
	
	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	protected void onDestroy() {
		if (mNotifyManager != null)
		{
			mNotifyManager.cancelAll();
			stopService(mExpService);
		}
		if (mProgressExpDialog != null)
		{
			mProgressExpDialog.dismiss();
		}
        StatisticsManager.close();
		super.onDestroy();
	}
	
	private class InitializingTask extends AsyncTask<Void, Void, Void>
	{
		ProgressDialog mProgressDialog = null;
		Exception exception = null;
		WakeLock wakeLock = null;
		
		@Override
		protected void onPreExecute() {
			mProgressDialog = ProgressDialog.show(MainActivity.this, getString(R.string.initialization), getString(R.string.long_initialization_message));
			
			mProgressDialog.setCancelable(true);
			mProgressDialog.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					
					cancel(true);
					handleCancelled();
					
				}
			});
			
			PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
			wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag");
			wakeLock.acquire();
			
			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			
			try {
				mApplication.setDataAccessProvider();
			} catch (DownloadDataException | JSONParserException e) {
				exception = e;
			}

			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mProgressDialog.dismiss();
			wakeLock.release();
			
			if (exception != null)
			{
				if (exception instanceof DownloadDataException)
				{
					Log.e("ERR_CONNECTION",getString(R.string.connection_error_message));
					startErrorDialog(getString(R.string.connection_error),getString(R.string.connection_error_message));
				} 
				else if (exception instanceof JSONParserException)
				{
					Log.e("ERR_PARSER",getString(R.string.parsing_error_message));
					startErrorDialog(getString(R.string.connection_error),getString(R.string.parsing_error_message));
				}
			}
			
			super.onPostExecute(result);
		}
		
		private void handleCancelled()
		{
			wakeLock.release();
			
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
			SharedPreferences.Editor edit = sharedPref.edit();
			edit.putString(SettingsActivity.KEY_PREF_DATA_ACCESS_PROVIDER, "0");
			edit.apply();
			
			// interrupt any JSON loading if there is any;
			JSONLoader.abort();
			
			mApplication.setDataAccessProviderToDefault();
		}
		
		
	}
	
	private class ExperimentationStateReceiver extends BroadcastReceiver {

		private int mNbQueriesToProcess = 0;
		private int notifID = 0;
		private ExperimentationStateReceiver() {}
		
		@Override
		public void onReceive(Context context, Intent intent) {
			int progress = 0;
			switch (intent.getIntExtra(BroadcastNotifier.EXTENDED_DATA_STATUS,
                    BroadcastNotifier.STATE_ACTION_COMPLETED)) {
                
                
                case BroadcastNotifier.STATE_ACTION_WARMUP_STARTED:
                	
                	((AndroidCachePrototypeApplication) getApplication()).setUseReplacement(true);
                	
                	mNbQueriesToProcess = Integer.parseInt(intent.getStringExtra(BroadcastNotifier.EXTENDED_STATUS_LOG));
                	
                	setupProgressDialog();
                	
                	setupNotfication();
                	
                    break;
                    
                case BroadcastNotifier.STATE_ACTION_WARMUP_PROGRESSED:
                    
                	progress = Integer.parseInt(intent.getStringExtra(BroadcastNotifier.EXTENDED_STATUS_LOG));
                	
                	updateProgressWarmUp(progress);
                	
                	break;
                    
                case BroadcastNotifier.STATE_ACTION_WARMUP_COMPLETED:
                	
                	//((AndroidCachePrototypeApplication) getApplication()).setUseReplacement(false);
                	
                	mBuilder.setContentText("WarmUp complete").setProgress(0,0,false);
                	
                	mNotifyManager.notify(notifID, mBuilder.build());
                	
                	mProgressExpDialog.dismiss();
                	break;
                
                case BroadcastNotifier.STATE_ACTION_STARTED: 
                	notifID++;
                	mNbQueriesToProcess = Integer.parseInt(intent.getStringExtra(BroadcastNotifier.EXTENDED_STATUS_LOG));
                	
                	setupProgressDialog();
                	
                	setupNotfication();
                	
                    break;
                    
                case BroadcastNotifier.STATE_ACTION_PROGRESSED:
                
                	progress = Integer.parseInt(intent.getStringExtra(BroadcastNotifier.EXTENDED_STATUS_LOG));
                	
                	updateProgress(progress);
                	
                	break;
                case BroadcastNotifier.STATE_ACTION_COMPLETED:

                	//query cache ui update
                	getQueryCacheFragment().update();
                	
                	// mobile estimation cache ui update
                	getMobileEstimationCacheFragment().update();
                	
                	// cloud estimation cache ui update
                	getCloudEstimationCacheFragment().update();
                	
                	mBuilder.setContentText("Experimentation " + notifID + " complete").setProgress(0,0,false);
                	
                	mNotifyManager.notify(notifID, mBuilder.build());
                	
                	if (mProgressExpDialog != null)
                	{
                		mProgressExpDialog.dismiss();
                	}
                
                	break;
                case BroadcastNotifier.ERROR_CONNECTION:
                	mBuilder.setContentText("Connection error").setProgress(0,0,false);
                	mNotifyManager.notify(notifID, mBuilder.build());
                	mProgressExpDialog.dismiss();
                	startErrorDialog(getString(R.string.no_connection_error), getString(R.string.no_connection_error_message));
                	break;
                case BroadcastNotifier.ERROR_DOWNLOAD_DATA:
                	mBuilder.setContentText("Download error").setProgress(0,0,false);
                	mNotifyManager.notify(notifID, mBuilder.build());
                	mProgressExpDialog.dismiss();
                	startErrorDialog(getString(R.string.connection_error),getString(R.string.connection_error_message));
                	break;
                case BroadcastNotifier.ERROR_JSON_PARSER:
                	mBuilder.setContentText("Parser error").setProgress(0,0,false);
                	mNotifyManager.notify(notifID, mBuilder.build());
                	mProgressExpDialog.dismiss();
                	startErrorDialog(getString(R.string.connection_error),getString(R.string.parsing_error_message));
                	break;
                case BroadcastNotifier.ERROR:
                	mBuilder.setContentText("Query processing error").setProgress(0,0,false);
                	mNotifyManager.notify(notifID, mBuilder.build());
                	mProgressExpDialog.dismiss();
                	startErrorDialog(getString(R.string.query_processing_error), getString(R.string.query_processing_error_message));
                	break;
                default:
                    break;
            }
        }
			
		private void setupProgressDialog() {
			
			if (!isFinishing())
			{
				mProgressExpDialog = new ProgressDialog(MainActivity.this);
				mProgressExpDialog.setTitle(getString(R.string.experimentation));
				mProgressExpDialog.setMessage(getString(R.string.experimentation_message));
				mProgressExpDialog.setProgress(0);
				mProgressExpDialog.setCancelable(false);
				mProgressExpDialog.setMax(mNbQueriesToProcess);
				mProgressExpDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				mProgressExpDialog.show();
			}
		}
		
		private void setupNotfication()
		{
			mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			mBuilder = new NotificationCompat.Builder(MainActivity.this);
			mBuilder.setContentTitle(getString(R.string.experimentation) + " " + notifID)
			    .setContentText("Experimentation in progress")
			    .setSmallIcon(R.drawable.ic_launcher);
			mBuilder.setProgress(mNbQueriesToProcess, 0, true);
			mNotifyManager.notify(notifID, mBuilder.build());
		}
		
		private void updateProgress(int progress) {
			mProgressExpDialog.setProgress(progress);
			StringBuilder sbDialog = new StringBuilder(getString(R.string.experimentation_message));
			sbDialog.append(" (");
			sbDialog.append(progress);
			sbDialog.append("/");
			sbDialog.append(mNbQueriesToProcess);
			sbDialog.append(")");
			mProgressExpDialog.setMessage(sbDialog.toString());
			
			StringBuilder sbNotif = new StringBuilder(getString(R.string.experimentation));
			sbNotif.append(" (");
			sbNotif.append(progress);
			sbNotif.append("/");
			sbNotif.append(mNbQueriesToProcess);
			sbNotif.append(")");
			
			mBuilder.setProgress(mNbQueriesToProcess, progress, false)
				.setContentText(sbNotif.toString());
			mNotifyManager.notify(notifID, mBuilder.build());
		}
		
		private void updateProgressWarmUp(int progress) {
			mProgressExpDialog.setProgress(progress);
			StringBuilder sbDialog = new StringBuilder(getString(R.string.warmup_message));
			sbDialog.append(" (");
			sbDialog.append(progress);
			sbDialog.append("/");
			sbDialog.append(mNbQueriesToProcess);
			sbDialog.append(")");
			mProgressExpDialog.setMessage(sbDialog.toString());
			
			StringBuilder sbNotif = new StringBuilder(getString(R.string.warmup));
			sbNotif.append(" (");
			sbNotif.append(progress);
			sbNotif.append("/");
			sbNotif.append(mNbQueriesToProcess);
			sbNotif.append(")");
			
			mBuilder.setProgress(mNbQueriesToProcess, progress, false)
				.setContentText(sbNotif.toString());
			mNotifyManager.notify(notifID, mBuilder.build());
		}
	}
	
}
