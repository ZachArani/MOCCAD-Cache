package edu.ou.oudb.cacheprototypeapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class SettingsActivity extends Activity {

	
	public static final String PREFERENCES_FILE_NAME="preferences.xml";
	public static final String KEY_PREF_MAX_QUERY_CACHE_SIZE = "pref_max_query_cache_size";
	public static final String KEY_PREF_TIME_CONSTRAINT = "pref_time_contraint";
	public static final String KEY_PREF_MONEY_CONSTRAINT = "pref_money_contraint";
	public static final String KEY_PREF_ENERGY_CONSTRAINT = "pref_energy_contraint";
	public static final String KEY_PREF_MAX_MOBILE_ESTIMATION_CACHE_SIZE = "pref_max_mobile_estimation_cache_size";
	public static final String KEY_PREF_MAX_CLOUD_ESTIMATION_CACHE_SIZE = "pref_max_cloud_estimation_cache_size";
	public static final String KEY_PREF_MAX_QUERY_CACHE_NUMBER_SEGMENT = "pref_max_query_cache_number_segment";
	public static final String KEY_PREF_MAX_MOBILE_ESTIMATION_CACHE_NUMBER_SEGMENT = "pref_max_mobile_estimation_cache_number_segment";
	public static final String KEY_PREF_MAX_CLOUD_ESTIMATION_CACHE_NUMBER_SEGMENT = "pref_max_cloud_estimation_cache_number_segment";
	public static final String KEY_PREF_DATA_ACCESS_PROVIDER = "pref_data_access_provider";
	public static final String KEY_PREF_CACHE_TYPE = "pref_cache_type";
	public static final String KEY_PREF_IP_ADDRESS = "pref_ip_address";
	public static final String KEY_PREF_PORT = "pref_port";
	public static final String KEY_PREF_NB_QUERIES_TO_PROCESS = "pref_nb_queries_to_process";
	public static final String KEY_PREF_REPLACEMENT_TYPE = "pref_replacement_type";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		// Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			NavUtils.navigateUpTo(this, new Intent(this,
					MainActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	

}
