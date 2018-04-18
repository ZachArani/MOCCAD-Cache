package edu.ou.oudb.cacheprototypeapp.ui;

import java.util.List;
import java.util.ListResourceBundle;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import edu.ou.oudb.cacheprototypeapp.R;
import edu.ou.oudb.cacheprototypeapp.provider.ProcessedQueryDbHelper;
import edu.ou.oudb.cacheprototypelibrary.core.cachemanagers.DecisionalSemanticCacheDataLoader;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;

public class ProcessedQueriesFragment extends ListFragment implements OnItemClickListener{
	
	
	public static class ViewHolder
	{
		public TextView queryRelation;
		public TextView predicates;
		public TextView time;
	}

	public ProcessedQueriesFragment(){}
	
    private QueryAdapter mProcessedQueryAdapter = null;
	private List<Query> mQueries = null;
    private TextView mEmptyTextView = null;
    private ProcessedQueryDbHelper mDBHelper = null;

	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        getListView().setOnItemClickListener(this);
        
        mDBHelper = new ProcessedQueryDbHelper(getActivity());
        
        mQueries = mDBHelper.getAllProcessedQueries(); // returns an array containing queries

    	mProcessedQueryAdapter = new QueryAdapter(getActivity(),R.layout.queries_item, mQueries);

		StatisticsActivity.setList(mQueries); // Sets the list in StatisticsActivity

    	setListAdapter(mProcessedQueryAdapter);

    }
	
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	
		View rootView = inflater.inflate(R.layout.fragment_processed_queries, container, false);
		
		return rootView;
    }

	public TextView getEmptyTextView()
	{
		if(mEmptyTextView == null)
		{
			mEmptyTextView = (TextView) getActivity().findViewById(android.R.id.empty);
		}
		
		return mEmptyTextView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// Create and show the dialog.
	    RelaunchQueryDialogFragment newFragment = new RelaunchQueryDialogFragment(mProcessedQueryAdapter.getItem(position));
	    newFragment.show(getFragmentManager(), "dialog");
	}
	
	
	/**
	 * Class defining a dialog fragment
	 * @author mika�l
	 */
	public static class RelaunchQueryDialogFragment extends DialogFragment {
		
		private Query mQuery = null;

		public RelaunchQueryDialogFragment(){}

		public RelaunchQueryDialogFragment(Query query)
		{
			mQuery = query;
		}
		
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	    	
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setMessage(R.string.dialog_relaunch_query)
	               .setPositiveButton(R.string.launch_query, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       (new QueryProcessTask(getActivity())).execute(mQuery);
	                   }
	               })
	               .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       RelaunchQueryDialogFragment.this.dismiss();
	                   }
	               });
	        // Create the AlertDialog object and return it
	        return builder.create();
	    }
	}
}
