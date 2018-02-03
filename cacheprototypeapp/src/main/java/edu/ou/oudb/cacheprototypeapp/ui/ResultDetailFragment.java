package edu.ou.oudb.cacheprototypeapp.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import edu.ou.oudb.cacheprototypeapp.R;
import edu.ou.oudb.cacheprototypelibrary.metadata.Metadata;

/**
 * A fragment representing a single Result detail screen. This fragment is
 * either contained in a {@link ResultListActivity} in two-pane mode (on
 * tablets) or a {@link ResultDetailActivity} on handsets.
 */
public class ResultDetailFragment extends ListFragment {
	
	
	public static final String ARG_TUPLE = "arg_tuple";
	
	public static final String ARG_RELATION = "arg_relation";

	private List<String> mTuple = null;
	
	private String mRelation;

	private List<String> mAttributes = null;
	
	private List<String> mDetails = null;

	public ResultDetailFragment() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_TUPLE)) {
			mTuple = (List<String>) getArguments().getSerializable(ARG_TUPLE);
		}
		
		if (getArguments().containsKey(ARG_RELATION)) {
			mRelation = getArguments().getString(ARG_RELATION);
			mAttributes = Metadata.getInstance().getRelationMetadata(mRelation).getAttributeNames();
		}
		
		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		if (mTuple != null && mAttributes != null) {
			mDetails = new ArrayList<String>();
			
			Iterator<String> itTuple = mTuple.iterator();
			Iterator<String> itAttributes = mAttributes.iterator();
			StringBuilder sb = new StringBuilder();
			while(itTuple.hasNext() && itAttributes.hasNext())
			{
				sb.append(itAttributes.next());
				sb.append(": ");
				sb.append(itTuple.next());
				
				mDetails.add(sb.toString());
				
				sb.setLength(0);
			}
			
			setListAdapter(new ArrayAdapter<String>(
					getActivity(),
					android.R.layout.simple_list_item_1,
					mDetails
				));
		}
		
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_result_detail,
				container, false);


		return rootView;
	}
}
