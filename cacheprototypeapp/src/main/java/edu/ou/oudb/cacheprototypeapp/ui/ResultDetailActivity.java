package edu.ou.oudb.cacheprototypeapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import edu.ou.oudb.cacheprototypeapp.R;

/**
 * An activity representing a single Result detail screen. This activity is only
 * used on handset devices. On tablet-count devices, item details are presented
 * side-by-side with a list of items in a {@link ResultListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link ResultDetailFragment}.
 */
public class ResultDetailActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result_detail);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState == null) {
			Bundle arguments = new Bundle();
			arguments.putString(ResultDetailFragment.ARG_RELATION, getIntent()
					.getStringExtra(ResultDetailFragment.ARG_RELATION));
			arguments.putSerializable(ResultDetailFragment.ARG_TUPLE, getIntent()
					.getSerializableExtra(ResultDetailFragment.ARG_TUPLE));
			ResultDetailFragment fragment = new ResultDetailFragment();
			fragment.setArguments(arguments);
			getFragmentManager().beginTransaction()
					.add(R.id.result_detail_container, fragment).commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			NavUtils.navigateUpTo(this, new Intent(this,
					ResultListActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
