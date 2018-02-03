package edu.ou.oudb.cacheprototypeapp.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import edu.ou.oudb.cacheprototypeapp.AndroidCachePrototypeApplication;
import edu.ou.oudb.cacheprototypeapp.R;
import edu.ou.oudb.cacheprototypelibrary.core.cache.Cache;
import edu.ou.oudb.cacheprototypelibrary.estimationcache.Estimation;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;

public class CloudEstimationCacheFragment extends ListFragment implements OnItemClickListener {

	private Cache<Query,Estimation> mCloudEstimationCache;
	private QueryAdapter mAdapter;
	
	private List<Query> mQueries = new ArrayList<Query>();
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        mCloudEstimationCache = ((AndroidCachePrototypeApplication) getActivity().getApplication()).getCloudEstimationCache();
        
        if (mCloudEstimationCache != null)
        {
        	mQueries.clear();
			mQueries.addAll(mCloudEstimationCache.getCacheContentManager().getEntrySet());
        }
        
        mAdapter = new QueryAdapter(getActivity(), R.layout.queries_item, mQueries);

		StatisticsActivity.setListCloud(mQueries); // Sets the list in StatisticsActivity

        setListAdapter(mAdapter);
        
        getListView().setOnItemClickListener(this);
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	
		View rootView = inflater.inflate(R.layout.fragment_cloud_estimation_cache, container, false);
		
		return rootView;
    }
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		 
		Query query = (Query) getListAdapter().getItem(position);
		
		// use cache content manager to avoid affecting the replacement management
		Estimation estimation = mCloudEstimationCache.getCacheContentManager().get(query);
		
		EstimationDialog dialog = new EstimationDialog();
		Bundle args = new Bundle();
		args.putString(EstimationDialog.ESTIMATION_DIALOG_QUERY, query.toSQLString());
		args.putLong(EstimationDialog.ESTIMATION_DIALOG_TIME, estimation.getDuration());
		args.putDouble(EstimationDialog.ESTIMATION_DIALOG_ENERGY, estimation.getEnergy() );
		args.putDouble(EstimationDialog.ESTIMATION_DIALOG_MONEY, estimation.getMonetaryCost());
		dialog.setArguments(args);
		
		dialog.show(getActivity().getFragmentManager(),"estimation_dialog");
		
	}
	
	public void update()
	{
		if (mCloudEstimationCache != null)
        {
			mQueries.clear();
			mQueries.addAll(mCloudEstimationCache.getCacheContentManager().getEntrySet());
        }
		mAdapter.notifyDataSetChanged();
	}
}
