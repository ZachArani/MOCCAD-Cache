package edu.ou.oudb.cacheprototypeapp.ui;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

import edu.ou.oudb.cacheprototypeapp.R;
import edu.ou.oudb.cacheprototypeapp.provider.ProcessedQueryDbHelper;
import edu.ou.oudb.cacheprototypelibrary.connection.CloudDataAccessProvider;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.InvalidPredicateException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.TrivialPredicateException;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Predicate;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.PredicateFactory;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;

/**
 * Created by chenxiao on 5/19/17.
 */

/*Class used instead of NewQueryActivity*/
public class AttributesSelectionActivity extends FragmentActivity implements View.OnClickListener, DatePickerFragment.OnDateDataPass, TimePickerFragment.OnTimeDataPass {
    private ProcessedQueryDbHelper mDBHelper = null;

    /*Booleans to know if a field has been selected*/
    private boolean isIdSelected = false,
            isPatientSelected = false,
            isPatientSecondSelected = false,
            isDoctorSelected = false,
            isDoctorSecondSelected = false,
            isDescriptionSelected = false,
            isDateSelected = false,
            isTimeSelected = false,
            isHeartrateSelected = false;

    private RadioButton id,
            patientfirstname,
            patientlastname,
            doctorfirstname,
            doctorlastname,
            description,
            date,
            time,
            heartrate;

    /*Buttons*/
    private Button confirm,
            cancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attributes_selection);

        initializeVariables();

        mDBHelper = new ProcessedQueryDbHelper(this);

        /*Radio Buttons*/
        id.setOnClickListener(this);
        patientfirstname.setOnClickListener(this);
        patientlastname.setOnClickListener(this);
        doctorfirstname.setOnClickListener(this);
        doctorlastname.setOnClickListener(this);
        description.setOnClickListener(this);
        date.setOnClickListener(this);
        time.setOnClickListener(this);
        heartrate.setOnClickListener(this);


        /*Buttons*/
        cancel.setOnClickListener(this);

        confirm.setOnClickListener(this);


    }

    /*Confirm, Cancel, and RadioButton actions*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmButton:
                confirmWeightsDefinition();
                break;
            case R.id.cancelButton:
                cancelWeightsDefinition();
                break;
            /*Sets the SeekBars according to the predefined weight profiles*/
            case R.id.emergencyButton:
                getEmergency();
                break;
            case R.id.moneySaverButton:
                getMoneySaver();
                break;
            case R.id.lowPowerButton:
                getLowPower();
                break;
            case R.id.resetButton:
                getReset();
                break;
        }
    }

    public void initializeVariables() {
        /*Radio Buttons*/
        id = (RadioButton) findViewById(R.id.id);
        patientfirstname = (RadioButton)  findViewById(R.id.patientfirstname);
        patientlastname = (RadioButton) findViewById(R.id.patientlastname);
        doctorfirstname = (RadioButton) findViewById(R.id.doctorfirstname);
        doctorlastname = (RadioButton) findViewById(R.id.doctorlastname);
        description = (RadioButton) findViewById(R.id.description);
        date = (RadioButton) findViewById(R.id.p_date);
        time = (RadioButton) findViewById(R.id.p_time);
        heartrate = (RadioButton) findViewById(R.id.heartrate);
        /*Search button, Date and Time pickers*/
        confirm = (Button) findViewById(R.id.confirm_button);
        cancel = (Button) findViewById(R.id.cancel_button);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
