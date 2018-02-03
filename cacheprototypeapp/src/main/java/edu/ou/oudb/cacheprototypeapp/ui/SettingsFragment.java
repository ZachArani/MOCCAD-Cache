package edu.ou.oudb.cacheprototypeapp.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.util.Log;
import edu.ou.oudb.cacheprototypeapp.AndroidCachePrototypeApplication;
import edu.ou.oudb.cacheprototypeapp.R;
import edu.ou.oudb.cacheprototypelibrary.connection.DataAccessProvider;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.DownloadDataException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.JSONParserException;
import edu.ou.oudb.cacheprototypelibrary.utils.JSONLoader;
import edu.ou.oudb.cacheprototypelibrary.utils.StatisticsManager;

public class SettingsFragment extends PreferenceFragment 
	implements OnSharedPreferenceChangeListener {
	
	
	private AndroidCachePrototypeApplication mApplication = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		mApplication = (AndroidCachePrototypeApplication) getActivity().getApplication();
	}

	@Override
	public void onResume() {
	    super.onResume();
	    getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

	    initSummary(getPreferenceScreen());
	    
	}

	@Override
	public void onPause() {
	    getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	    super.onPause();
	}
	
	private void initSummary(Preference p) {
        if (p instanceof PreferenceGroup) {
            PreferenceGroup pGrp = (PreferenceGroup) p;
            for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
                initSummary(pGrp.getPreference(i));
            }
        } else {
        	updatePreferenceSummary(p);
        }
    }
	
	public void updatePreferenceSummary(Preference p)
	{	
		if (p instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
            if (p.getKey().equals(SettingsActivity.KEY_PREF_TIME_CONSTRAINT)){
            	p.setSummary(editTextPref.getText()+" ns");
            } else if (p.getKey().equals(SettingsActivity.KEY_PREF_MONEY_CONSTRAINT)){
                p.setSummary("$"+editTextPref.getText());
            } else if (p.getKey().equals(SettingsActivity.KEY_PREF_ENERGY_CONSTRAINT)){
                p.setSummary(editTextPref.getText()+" mAh");
            } else if (p.getKey().equals(SettingsActivity.KEY_PREF_IP_ADDRESS)) {
            	p.setSummary(editTextPref.getText());
            } else if (p.getKey().equals(SettingsActivity.KEY_PREF_PORT)) {
            	p.setSummary(editTextPref.getText());
            }
        }
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		
		Preference pref = findPreference(key);

	    
	    if (pref instanceof ListPreference) {
	        ListPreference listPref = (ListPreference) pref;
	        pref.setSummary(listPref.getEntry());
	        
	        if (key.equals(SettingsActivity.KEY_PREF_DATA_ACCESS_PROVIDER))
		    {
	        	int currentValue = Integer.parseInt(sharedPreferences.getString(key, "1"));
	        	if (currentValue == 0)
	        	{
	        		StatisticsManager.createFileWriter();
			    	//update DataAccessProvider
                    (new SetDataAccessProviderTask()).execute();
	        	}
		    }
		    else if (key.equals(SettingsActivity.KEY_PREF_CACHE_TYPE))
		    {
                StatisticsManager.createFileWriter();
		    	//build a new cache_manager
		    	(new SetCacheTypeTask()).execute();
		    }
		    else if (key.equals(SettingsActivity.KEY_PREF_MAX_CLOUD_ESTIMATION_CACHE_NUMBER_SEGMENT))
		    {
		    	if (mApplication.getCloudEstimationCache() != null)
		    	{
		    		int currentValue = Integer.parseInt(sharedPreferences.getString(key, ""));
			    	if (currentValue == 0)
			    	{
			    		mApplication.getCloudEstimationCache().setMaxCount(Integer.MAX_VALUE);
			    	}
			    	else
			    	{
			    		mApplication.getCloudEstimationCache().setMaxCount(currentValue);
			    	}
		    	}
		    }
		    else if (key.equals(SettingsActivity.KEY_PREF_MAX_CLOUD_ESTIMATION_CACHE_SIZE))
		    {
		    	if (mApplication.getCloudEstimationCache() != null)
		    	{
			    	int currentValue = Integer.parseInt(sharedPreferences.getString(key, "100000000"));
			    	mApplication.getCloudEstimationCache().setMaxSize(currentValue);
		    	}
		    }
		    else if (key.equals(SettingsActivity.KEY_PREF_MAX_MOBILE_ESTIMATION_CACHE_NUMBER_SEGMENT))
		    {
		    	if (mApplication.getMobileEstimationCache() != null)
		    	{
			    	int currentValue = Integer.parseInt(sharedPreferences.getString(key, "0"));
			    	if (currentValue == 0)
			    	{
			    		mApplication.getMobileEstimationCache().setMaxCount(Integer.MAX_VALUE);
			    	}
			    	else
			    	{
			    		mApplication.getMobileEstimationCache().setMaxCount(currentValue);
			    	}
		    	}
		    }
		    else if (key.equals(SettingsActivity.KEY_PREF_MAX_MOBILE_ESTIMATION_CACHE_SIZE))
		    {
		    	if (mApplication.getMobileEstimationCache() != null)
		    	{
			    	int currentValue = Integer.parseInt(sharedPreferences.getString(key, "100000000"));
			    	mApplication.getMobileEstimationCache().setMaxSize(currentValue);
		    	}
		    }
		    else if (key.equals(SettingsActivity.KEY_PREF_MAX_QUERY_CACHE_NUMBER_SEGMENT))
		    {
		    	if (mApplication.getQueryCache() != null)
		    	{
			    	int currentValue = Integer.parseInt(sharedPreferences.getString(key, "0"));
			    	if (currentValue == 0)
			    	{
			    		mApplication.getQueryCache().setMaxCount(Integer.MAX_VALUE);
			    	}
			    	else
			    	{
			    		mApplication.getQueryCache().setMaxCount(currentValue);
			    	}
		    	}
		    }
		    else if (key.equals(SettingsActivity.KEY_PREF_MAX_QUERY_CACHE_SIZE))
		    {
		    	if (mApplication.getQueryCache() != null)
		    	{
			    	int currentValue = Integer.parseInt(sharedPreferences.getString(key, "100000000"));
			    	mApplication.getQueryCache().setMaxSize(currentValue);
		    	}
		    }
	    }
	    
	    if (pref instanceof EditTextPreference)
	    {
	    	if (key.equals(SettingsActivity.KEY_PREF_TIME_CONSTRAINT)
		    		|| key.equals(SettingsActivity.KEY_PREF_MONEY_CONSTRAINT)
		    		|| key.equals(SettingsActivity.KEY_PREF_ENERGY_CONSTRAINT))
		    {
	    		
	    		if (sharedPreferences.getString(key,"0").isEmpty())
	    		{
	    			SharedPreferences.Editor prefEdit = sharedPreferences.edit();
	    			prefEdit.putString(key, "0");
	    			prefEdit.commit();
	    		}
	    	
	    		
		    	if (mApplication.getDataLoader() != null)
		    	{
		    		mApplication.setOptimizationParameters();
		    		updatePreferenceSummary(findPreference(key));
		    	}
		    }
	    	else if (key.equals(SettingsActivity.KEY_PREF_IP_ADDRESS)
	    			|| key.equals(SettingsActivity.KEY_PREF_PORT))
	    	{
	    		SharedPreferences settings = getActivity().getSharedPreferences(DataAccessProvider.PREF_METADATA, 0);
	            if(settings.contains(DataAccessProvider.PREF_METADATA)) {
	                SharedPreferences.Editor editor = settings.edit();
	                editor.remove(DataAccessProvider.PREF_METADATA);
	                editor.commit();
	            }

	            updatePreferenceSummary(findPreference(key));

		    	//update DataAccessProvider
		    	(new SetDataAccessProviderTask()).execute();
	    	}
	    }
	    
	    if(pref instanceof CheckBoxPreference)
	    {
	    	if(key.equals(SettingsActivity.KEY_PREF_USE_REPLACEMENT))
	    	{
	    		boolean useReplacement = sharedPreferences.getBoolean(key, true);
	    		mApplication.setUseReplacement(useReplacement);
	    	}
	    }
	}
	
	private class SetDataAccessProviderTask extends AsyncTask<Void, Void, Void>
	{
		ProgressDialog mProgressDialog = null;
		
		Exception exception = null;
		
		WakeLock wakeLock = null;
		
		@Override
		protected void onPreExecute() {
			mProgressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.set_data_access_provider_message));
			
			mProgressDialog.setCancelable(true);
			mProgressDialog.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					
					cancel(true);
					handleCancelled();
					
				}
			});
			
			PowerManager powerManager = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
			wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag");
			wakeLock.acquire();
			
			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			try {
				((AndroidCachePrototypeApplication) getActivity().getApplication()).setDataAccessProvider();
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
					launchErrorDialog(getString(R.string.connection_error),getString(R.string.connection_error_message));
				} 
				else if (exception instanceof JSONParserException)
				{
					Log.e("ERR_PARSER",getString(R.string.parsing_error_message));
					launchErrorDialog(getString(R.string.connection_error),getString(R.string.parsing_error_message));
				}
			}
			
			super.onPostExecute(result);
		}
		
		private void handleCancelled() {

			wakeLock.release();
			
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
			SharedPreferences.Editor edit = sharedPref.edit();
			edit.remove(SettingsActivity.KEY_PREF_DATA_ACCESS_PROVIDER);
			edit.apply();
			
			JSONLoader.abort();
	
		}
		
		private void launchErrorDialog(String title, String message)
		{
			ErrorDialog errorDialog = ErrorDialog.newInstance(title, message);
		    errorDialog.show(getFragmentManager(),"error_dialog");
		}
		
	}

	private class SetCacheTypeTask extends AsyncTask<Void, Void, Void>
	{
		ProgressDialog mProgressDialog = null;
		
		@Override
		protected void onPreExecute() {
			mProgressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.set_cache_type_message),false);
			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			((AndroidCachePrototypeApplication) getActivity().getApplication()).setCacheManager();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mProgressDialog.dismiss();
			super.onPostExecute(result);
		}
	}
}
