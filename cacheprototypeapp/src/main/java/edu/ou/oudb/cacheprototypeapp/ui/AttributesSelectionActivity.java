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

import java.util.ArrayList;

import edu.ou.oudb.cacheprototypeapp.R;
import edu.ou.oudb.cacheprototypeapp.provider.ProcessedQueryDbHelper;

/**
 * Created by chenxiao on 5/19/17.
 */

/*Class used instead of NewQueryActivity*/
public class AttributesSelectionActivity extends FragmentActivity implements View.OnClickListener {
    private ProcessedQueryDbHelper mDBHelper = null;

    /*Radio Buttons*/
    RadioButton id_button,
    patientFirst_button,
    patientLast_button,
    doctorFirst_button,
    doctorLast_button,
    description_button,
    date_time_button,
    heartrate_button;

    RadioGroup attributeGroup;


    /*Buttons*/
    private Button confirm,
            cancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attributes_selection);

        initializeVariables();

        mDBHelper = new ProcessedQueryDbHelper(this);

        /*Buttons*/
        cancel.setOnClickListener(this);

        confirm.setOnClickListener(this);


    }

    /*Confirm, Cancel, and RadioButton actions*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmButton:
                confirmAttributes();
                break;
            case R.id.cancelButton:
                cancelAttributes();
                break;
        }
    }

    public void initializeVariables() {
        /*Radio Buttons*/
        id_button = (RadioButton) findViewById(R.id.id);
        patientFirst_button = (RadioButton) findViewById(R.id.patientfirstname);
        patientLast_button = (RadioButton) findViewById(R.id.patientlastname);
        doctorFirst_button = (RadioButton) findViewById(R.id.doctorfirstname);
        doctorLast_button = (RadioButton) findViewById(R.id.doctorlastname);
        description_button = (RadioButton) findViewById(R.id.description);
        date_time_button = (RadioButton) findViewById(R.id.p_date_time);
        heartrate_button = (RadioButton) findViewById(R.id.heartrate);


        /*Confirm and cancel buttons*/
        confirm = (Button) findViewById(R.id.confirm_button);
        cancel = (Button) findViewById(R.id.cancel_button);
    }


    public void onRadioButtonClicked(View view) {

        switch(view.getId()){
            case R.id.id:
                id_button.toggle();
                break;
        }

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

    /*When clicking on cancel, goes back to previous activity*/
    private void cancelAttributes() {

        NavUtils.navigateUpFromSameTask(AttributesSelectionActivity.this);
    }

    private void confirmAttributes() {
        //"noteid", "patientfirstname", "patientlastname", "doctorfirstname", "doctorlastname", "description", "p_date_time", "heartrate"};
        ArrayList<String> attributes = new ArrayList<String>();
        if(id_button.isSelected()) attributes.add("noteid");
        if(patientFirst_button.isSelected()) attributes.add("patientfirstname");
        if(patientLast_button.isSelected()) attributes.add("patientlastname");
        if(doctorFirst_button.isSelected()) attributes.add("doctorfirstname");
        if(doctorLast_button.isSelected()) attributes.add("doctorlastname");
        if(description_button.isSelected()) attributes.add("description");
        if(date_time_button.isSelected()) attributes.add("p_date_time");
        if(heartrate_button.isSelected()) attributes.add("heartrate");


        Intent intent = new Intent(this, SearchExamRecordActivity.class);
        intent.putExtra(SearchExamRecordActivity.ATTRIBUTELIST, attributes);

        startActivity(intent);
    }
}
