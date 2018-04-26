package edu.ou.oudb.cacheprototypeapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

import java.util.ArrayList;

import edu.ou.oudb.cacheprototypeapp.R;
import edu.ou.oudb.cacheprototypeapp.provider.ProcessedQueryDbHelper;

/**
 * Created by chenxiao on 5/19/17.
 */

/*Class used instead of NewQueryActivity*/
public class AttributesSelectionActivity extends FragmentActivity implements View.OnClickListener {
    private ProcessedQueryDbHelper mDBHelper = null;

    /*Toggle Buttons*/
    ToggleButton id_button,
    patientFirst_button,
    patientLast_button,
    doctorFirst_button,
    doctorLast_button,
    description_button,
    date_time_button,
    heartrate_button;


    /*Buttons*/
    private Button confirm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attributes_selection);

        initializeVariables();

        mDBHelper = new ProcessedQueryDbHelper(this);

        /*Buttons*/

        confirm.setOnClickListener(this);


    }

    /*Confirm, Cancel, and RadioButton actions*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmButton:
                System.out.println("button pressed");
                confirmAttributes();
                break;
        }
    }

    public void initializeVariables() {
        /*Radio Buttons*/
        id_button = (ToggleButton) findViewById(R.id.id);
        patientFirst_button = (ToggleButton) findViewById(R.id.patientfirstname);
        patientLast_button = (ToggleButton) findViewById(R.id.patientlastname);
        doctorFirst_button = (ToggleButton) findViewById(R.id.doctorfirstname);
        doctorLast_button = (ToggleButton) findViewById(R.id.doctorlastname);
        description_button = (ToggleButton) findViewById(R.id.description);
        date_time_button = (ToggleButton) findViewById(R.id.p_date_time);
        heartrate_button = (ToggleButton) findViewById(R.id.heartrate);


        /*Confirm and cancel buttons*/
        confirm = (Button) findViewById(R.id.confirmButton);

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

    private void confirmAttributes() {
        //"noteid", "patientfirstname", "patientlastname", "doctorfirstname", "doctorlastname", "description", "p_date_time", "heartrate"};
        ArrayList<String> attributes = new ArrayList<String>();
        if(id_button.isChecked()) attributes.add("noteid");
        if(patientFirst_button.isChecked()) attributes.add("patientfirstname");
        if(patientLast_button.isChecked()) attributes.add("patientlastname");
        if(doctorFirst_button.isChecked()) attributes.add("doctorfirstname");
        if(doctorLast_button.isChecked()) attributes.add("doctorlastname");
        if(description_button.isChecked()) attributes.add("description");
        if(date_time_button.isChecked()) attributes.add("p_date_time");
        if(heartrate_button.isChecked()) attributes.add("heartrate");


        Intent intent = new Intent(this, SearchExamRecordActivity.class);
        intent.putExtra(SearchExamRecordActivity.ATTRIBUTELIST, attributes);

        startActivity(intent);
        System.out.println("button pressed");
    }
}
