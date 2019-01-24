package edu.ou.oudb.cacheprototypeapp.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import edu.ou.oudb.cacheprototypeapp.AndroidCachePrototypeApplication;
import edu.ou.oudb.cacheprototypeapp.R;
import edu.ou.oudb.cacheprototypelibrary.core.cache.Cache;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.QuerySegment;

public class QueryCacheFragment extends ListFragment implements OnItemClickListener{
	
	private Cache<Query,QuerySegment> mQueryCache = null;
	
	private QueryAdapter mAdapter = null;
	
	private List<Query> mQueries = new ArrayList<Query>();
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        mQueryCache = ((AndroidCachePrototypeApplication) getActivity().getApplication()).getQueryCache();

        if (mQueryCache != null)
        {
        	mQueries.clear();
			mQueries.addAll(mQueryCache.getCacheContentManager().getEntrySet());
        }
       
        mAdapter = new QueryAdapter(getActivity(), R.layout.queries_item, mQueries);

		StatisticsActivity.setListCac(mQueries); // Sets the list in StatisticsActivity

        setListAdapter(mAdapter);
        
        getListView().setOnItemClickListener(this);
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	
		View rootView = inflater.inflate(R.layout.fragment_query_cache, container, false);
		
		return rootView;
    }
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
		Query q = (Query) getListAdapter().getItem(position);
		
		List<List<String>> tuples = null;
		
		if (mQueryCache != null)
        {
        	tuples = mQueryCache.getCacheContentManager().get(q).getTuples();

    		Intent intent = new Intent(parent.getContext(), ResultListActivity.class);
    		intent.putExtra(ResultListActivity.QUERY_RELATION, q.getRelation());
    		((AndroidCachePrototypeApplication) parent.getContext().getApplicationContext()).setCurrentQueryResult(tuples);
    		
    		parent.getContext().startActivity(intent);
        }
		
	}
	
	public void update()
	{
		if (mQueryCache != null)
        {
			mQueries.clear();
			mQueries.addAll(mQueryCache.getCacheContentManager().getEntrySet());
        }
		mAdapter.notifyDataSetChanged();
	}

	
}
