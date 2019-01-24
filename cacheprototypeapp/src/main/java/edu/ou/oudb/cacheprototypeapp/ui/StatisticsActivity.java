package edu.ou.oudb.cacheprototypeapp.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import edu.ou.oudb.cacheprototypeapp.R;
import edu.ou.oudb.cacheprototypelibrary.core.cachemanagers.DecisionalSemanticCacheDataLoader;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;
import edu.ou.oudb.cacheprototypelibrary.utils.StatisticsManager;

public class StatisticsActivity extends Activity {

	public static List<Query> list,
							listCac,
							listMob,
							listCloud;

	private long timeEstiMob = 0,
				timeEstiCloud = 0;

	TextView result,
			resultCac,
			resultMob,
			resultCloud,
			resultTimeMob,
			resultTimeCloud;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistics);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Number of Processed Queries
		result = (TextView) findViewById(R.id.result_nbProcQue);
		result.setText(String.format(Locale.US,"%d",list.size()));

		// Number of Queries in Cache
		resultCac = (TextView) findViewById(R.id.result_nbQueCac);
		resultCac.setText(String.format(Locale.US,"%d",listCac.size()));

		// Percentage of Processed Queries on Mobile device
		resultMob = (TextView) findViewById(R.id.result_nbProcQueMob);
		if(listMob.size()==0){
			resultMob.setText("" + String.format(Locale.US,"%d",0) + "%");
		}else {
			resultMob.setText("" + String.format(Locale.US,"%d",listMob.size()*100/(listMob.size()+listCloud.size())) + "%");
		}

		// Percentage of Processed Queries on Cloud
		resultCloud = (TextView) findViewById(R.id.result_nbProcQueCloud);
		if(listCloud.size()==0){
			resultCloud.setText("" + String.format(Locale.US,"%d",0) + "%");
		}else {
			resultCloud.setText("" + String.format(Locale.US,"%d",listCloud.size()*100/(listMob.size()+listCloud.size())) + "%");
		}

 		//Total time needed on Mobile
		resultTimeMob = (TextView) findViewById(R.id.result_timeMob);
		resultTimeMob.setText("" + String.format(Locale.US,"%d",timeEstiMob) + " ns");

		// Total time needed on Cloud
		resultTimeCloud = (TextView) findViewById(R.id.result_timeCloud);
		resultTimeCloud.setText("" + String.format(Locale.US,"%d",timeEstiCloud) + " ns");
	}

	@Override
	protected void onResume(){
		super.onResume();
		setTotalTimeCloud();
		resultTimeCloud.setText("" + String.format(Locale.US,"%d",timeEstiCloud) + " ns");
		setTotalTimeMobile();
		resultTimeMob.setText("" + String.format(Locale.US,"%d",timeEstiMob) + " ns");
	}

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.statistics, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		switch(id)
		{
		case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
		case R.id.action_save:
			StatisticsManager.close();
			return true;
		case R.id.action_delete:
			StatisticsManager.delete();
			finish();
			return true;
	    }
		return super.onOptionsItemSelected(item);
	}

	// STATS

	// Sets the list in this class to the list contained in the ProcessedQueriesFragment
	public static void setList(List<Query> listPQF) {
		list = listPQF;
	}

	// Sets the list in this class to the list contained in the ProcessedQueriesFragment
	public static void setListCac(List<Query> listQCF) {
		listCac = listQCF;
	}

	// Sets the list in this class to the list contained in the MobileEstimationCacheFragment
	public static void setListMob(List<Query> listQoM) { // QoM --> Queries on Mobile
		listMob = listQoM;
	}

	// Sets the list in this class to the list contained in the CloudEstimationCacheFragment
	public static void setListCloud(List<Query> listQoC) { // QoC --> Queries on Cloud
		listCloud = listQoC;
	}

	// Total Time Cloud
	private void setTotalTimeCloud(){
		timeEstiCloud = timeEstiCloud + DecisionalSemanticCacheDataLoader.totalTimeC;
	}

	// Total Time Mobile
	private void setTotalTimeMobile() {
		timeEstiMob = timeEstiMob + DecisionalSemanticCacheDataLoader.totalTimeM;
	}
}
