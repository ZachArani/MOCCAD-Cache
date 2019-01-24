package edu.ou.oudb.cacheprototypeapp.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import edu.ou.oudb.cacheprototypeapp.R;


public class ErrorDialog extends DialogFragment{
	
	public static final String ERROR_DIALOG_TITLE = "error_dialog_title";
	public static final String ERROR_DIALOG_MESSAGE = "error_dialog_message";
	
	private AlertDialog.Builder alertDialogBuilder = null;
	
	public static ErrorDialog newInstance(String title, String message)
	{
		ErrorDialog errorDialog = new ErrorDialog();
		
		Bundle args = new Bundle();
	    args.putString(ErrorDialog.ERROR_DIALOG_TITLE, title);
	    args.putString(ErrorDialog.ERROR_DIALOG_MESSAGE, message);
	    errorDialog.setArguments(args);
	
	    return errorDialog;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		alertDialogBuilder = new AlertDialog.Builder(getActivity());
    	
		Bundle data = getArguments();
		String title = data.getString(ERROR_DIALOG_TITLE);
		String message = data.getString(ERROR_DIALOG_MESSAGE);
		
        alertDialogBuilder.setTitle(title);
    	alertDialogBuilder.setMessage(message);
    	
    	alertDialogBuilder.setCancelable(false);
		
    	/**
    	 * On propose de revenir au menu principal en annulant
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
	
