package edu.ou.oudb.cacheprototypeapp.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by chenxiao on 6/1/17.
 */

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    /*Interface to pass the data to the activity*/
    public interface OnTimeDataPass {
        void onTimeDataPass(String data);
    }

    OnTimeDataPass dataPasser;

    @Override
    public void onAttach(Activity a) {
        super.onAttach(a);
        dataPasser = (OnTimeDataPass) a;
    }

    public void passData(String data) {
        dataPasser.onTimeDataPass(data);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        /*Use the current time as the default values for the picker*/
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        /*Creates a new instance of TimePickerDialog and returns it*/
        return new TimePickerDialog(getActivity(), this, hour, minute, false);
    }

    public void onTimeSet(TimePicker view, int hour, int minute) {
        /*Convert to String for when the query will be launched*/
        String minuteS = Integer.toString(minute);
        String hourS = Integer.toString(hour);
        /*Tests needed because displays 5:1 instead of 05:01 otherwise,
        * making the result of the query to be false*/
        if (hour < 10) {
            hourS = "0" + hour;
        }
        if (minute < 10) {
            minuteS = "0" + minute;
        }

        passData(hourS + ":" + minuteS);
    }

}
