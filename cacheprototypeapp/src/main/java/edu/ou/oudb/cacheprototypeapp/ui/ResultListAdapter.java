package edu.ou.oudb.cacheprototypeapp.ui;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ResultListAdapter extends ArrayAdapter<List<String>>{

	private List<List<String>> mResults;
	
	public ResultListAdapter(Context context, int resource, List<List<String>> results) {
		super(context, resource, results);
		
		mResults = results;
	}

	/**
	 * Get the size of the list
	 * @return the size of the list
	 */
	@Override
	public int getCount() {
		return this.mResults.size();
	}
	
	@Override
	public void add(List<String> tuple) {
		mResults.add(tuple);
		super.add(tuple);
	}
	
	@Override
	public List<String> getItem(int index) {
		return this.mResults.get(index);
	}
	
	private String tupleToString(List<String> tuple)
	{
		StringBuilder sb = new StringBuilder();
		
		for(String attribute: tuple)
		{
			sb.append(attribute);
			sb.append(" ");
		}
		
		return sb.toString();
	}	
	

	@Override
	public View getView(int position, View row, ViewGroup parent) {

		ResultListFragment.ViewHolder holder;
		
		List<String> tuple = getItem(position);
		
		if (row == null) {
			// remplissage de la vue
			LayoutInflater inflater = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(android.R.layout.simple_list_item_activated_1, parent, false);	
			
			holder = new ResultListFragment.ViewHolder();
			
			holder.tuple = (TextView) row.findViewById(android.R.id.text1);
			
			row.setTag(holder);
		}
		else
		{
			holder = (ResultListFragment.ViewHolder) row.getTag();
		}
		
		holder.tuple.setText(tupleToString(tuple));
		
		return row;
	}
	
	
	
	

}
