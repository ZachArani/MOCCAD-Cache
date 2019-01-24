package edu.ou.oudb.cacheprototypeapp.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by chenxiao on 6/1/17.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    /*Interface to pass the data to the activity*/
    public interface OnDateDataPass {
        void onDateDataPass(String data);
    }

    OnDateDataPass dataPasser;

    @Override
    public void onAttach(Activity a) {
        super.onAttach(a);
        dataPasser = (OnDateDataPass) a;
    }

    public void passData(String data) {
        dataPasser.onDateDataPass(data);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        /*Use the current date as the default date in the picker*/
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        /*Creates a new instance of DatePickerDialog and returns it*/
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        /*Months starts at 0 so we need to increment*/
        month = month + 1;
        /*We convert into String to add a "0" in case < 10
        * Needed for when the query will be launched*/
        String yearS = Integer.toString(year);
        String monthS;
        String dayS;
        if (month < 10) {
            monthS = "0" + Integer.toString(month);
        } else {
            monthS = Integer.toString(month);
        }
        if (day < 10) {
            dayS = "0" + Integer.toString(day);
        } else {
            dayS = Integer.toString(day);
        }
        passData(yearS + "-" + monthS + "-" + dayS);
    }

}
