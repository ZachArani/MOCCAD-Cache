package edu.ou.oudb.cacheprototypeapp.ui;

import java.io.Serializable;
import java.util.List;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import edu.ou.oudb.cacheprototypeapp.R;

/**
 * An activity representing a list of Results. This activity has different
 * presentations for handset and tablet-count devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link ResultDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ResultListFragment} and the item details (if present) is a
 * {@link ResultDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link ResultListFragment.Callbacks} interface to listen for item selections.
 */
public class ResultListActivity extends Activity implements
		ResultListFragment.Callbacks {

	public static final String LIST_TAG = "List";
	public static final String DETAIL_TAG ="detail";
	
	public static final String QUERY_RESULT = "query_result";
	public static final String QUERY_RELATION = "query_relation";
	
	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
		
	private String mRelation = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mRelation = getIntent().getStringExtra(QUERY_RELATION);
		
		
		FragmentManager fm = getFragmentManager();

		setContentView(R.layout.activity_result_list);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		if (findViewById(R.id.result_detail_container) != null) {
			
			mTwoPane = true;
			
		}
		
		ResultListFragment rlf = (ResultListFragment) fm.findFragmentByTag(LIST_TAG);
		if ( rlf == null) {
    		rlf = new ResultListFragment();     
        	
        	fm.beginTransaction().replace(R.id.result_list, rlf, LIST_TAG).commit();
    	}
	}

	

	/**
	 * Callback method from {@link ResultListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(List<String> tuple) {
		if (mTwoPane) {
			Bundle arguments = new Bundle();
			arguments.putString(ResultDetailFragment.ARG_RELATION, mRelation);
			arguments.putSerializable(ResultDetailFragment.ARG_TUPLE, (Serializable) tuple);
			ResultDetailFragment fragment = new ResultDetailFragment();
			fragment.setArguments(arguments);
			getFragmentManager().beginTransaction()
					.replace(R.id.result_detail_container, fragment,DETAIL_TAG).commit();

		} else {
			Intent detailIntent = new Intent(this, ResultDetailActivity.class);
			detailIntent.putExtra(ResultDetailFragment.ARG_RELATION, mRelation);
			detailIntent.putExtra(ResultDetailFragment.ARG_TUPLE, (Serializable) tuple);
			startActivity(detailIntent);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
