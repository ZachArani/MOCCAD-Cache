package edu.ou.oudb.cacheprototypeapp.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

import edu.ou.oudb.cacheprototypeapp.R;

public class EstimationDialog extends DialogFragment{

		
	public static final String ESTIMATION_DIALOG_QUERY = "estimation_dialog_query";
	public static final String ESTIMATION_DIALOG_TIME = "estimation_dialog_time";
	public static final String ESTIMATION_DIALOG_ENERGY = "estimation_dialog_energy";
	public static final String ESTIMATION_DIALOG_MONEY = "estimation_dialog_money";
	
	private AlertDialog.Builder alertDialogBuilder = null;
	
	
	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		alertDialogBuilder = new AlertDialog.Builder(getActivity());
    	
		Bundle data = getArguments();
		String query = data.getString(ESTIMATION_DIALOG_QUERY);
		Long time = data.getLong(ESTIMATION_DIALOG_TIME);
		Double energy = data.getDouble(ESTIMATION_DIALOG_ENERGY);
		Double money = data.getDouble(ESTIMATION_DIALOG_MONEY);
		
        alertDialogBuilder.setTitle(query);
    	View dialogLayout = getActivity().getLayoutInflater().inflate(R.layout.fragment_estimation_dialog, new LinearLayout(getActivity()));
    	
    	((TextView) dialogLayout.findViewById(R.id.time)).setText("time: " + String.format(Locale.US,"%,d",time) +" nanoseconds");
    	((TextView) dialogLayout.findViewById(R.id.energy)).setText("energy: " + String.format(Locale.US,"%,.5f",energy) + " mAh");
    	((TextView) dialogLayout.findViewById(R.id.money)).setText("monetary cost: $" + String.format(Locale.US,"%,.5f", money) + "");
    	
        alertDialogBuilder.setView(dialogLayout);
		
    	/**
    	 * Goes back to the fragment (Where the list is shown) when clicking on OK
    	 */
    	alertDialogBuilder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
    	
    	return alertDialogBuilder.create();
	}

	
}
