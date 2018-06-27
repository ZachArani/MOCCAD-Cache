package edu.ou.oudb.cacheprototypeapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import edu.ou.oudb.cacheprototypeapp.R;

/**
 * Created by chenxiao on 6/5/17.
 */

/*Class used to display the details of a record*/
public class SearchExamRecordResultsDetailsActivity extends Activity {
    public static final String IMAGE = "image";

    public static final String ID = "id";
    public static final String PATIENTSF = "patientsF";
    public static final String PATIENTSL = "patientsL";
    public static final String DOCTORSF = "doctorsF";
    public static final String DOCTORSL = "doctorsL";
    public static final String DESCRIPTIONS = "descriptions";
    public static final String DATES = "dates";
    public static final String TIMES = "times";
    public static final String HEARTRATES = "heartrates";

    public static final String IDJ = "idJ";
    public static final String PATIENTSFJ = "patientsFJ";
    public static final String PATIENTSLJ = "patientsLJ";
    public static final String DOCTORSFJ = "doctorsFJ";
    public static final String DOCTORSLJ = "doctorsLJ";
    public static final String DESCRIPTIONSJ = "descriptionsJ";
    public static final String DATESJ = "datesJ";
    public static final String TIMESJ = "timesJ";
    public static final String HEARTRATESJ = "heartratesJ";

    private ImageView image = null;

    private TextView idVal = null;
    private TextView patientVal = null;
    private TextView doctorVal = null;
    private TextView descriptionVal = null;
    private TextView dateVal = null;
    private TextView timeVal = null;
    private TextView heartrateVal = null;

    private TextView idValJ = null;
    private TextView patientValJ = null;
    private TextView doctorValJ = null;
    private TextView descriptionValJ = null;
    private TextView dateValJ = null;
    private TextView timeValJ = null;
    private TextView heartrateValJ = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Integer idj = getIntent().getIntExtra(IDJ, -1);
        if(idj != -1) {
            //join view
            setContentView(R.layout.activity_search_exam_record_results_details);
        }else{
            //regularview
            setContentView(R.layout.activity_search_exam_record_results_details1);
        }

        initializeVariables();

        /*Gets the image*/
        Bundle extras = getIntent().getExtras();
        Bitmap bmp = extras.getParcelable(IMAGE);

        image.setImageBitmap(bmp);
        /*Gets the arrays*/
        Integer id = getIntent().getIntExtra(ID, 1);
        String patientF = getIntent().getStringExtra(PATIENTSF);
        String patientL = getIntent().getStringExtra(PATIENTSL);
        String doctorF = getIntent().getStringExtra(DOCTORSF);
        String doctorL = getIntent().getStringExtra(DOCTORSL);
        String description = getIntent().getStringExtra(DESCRIPTIONS);
        String date = getIntent().getStringExtra(DATES);
        String time = getIntent().getStringExtra(TIMES);
        Integer heartrate = getIntent().getIntExtra(HEARTRATES, 1);

        String patientFj = getIntent().getStringExtra(PATIENTSFJ);
        String patientLj = getIntent().getStringExtra(PATIENTSLJ);
        String doctorFj = getIntent().getStringExtra(DOCTORSFJ);
        String doctorLj = getIntent().getStringExtra(DOCTORSLJ);
        String descriptionj = getIntent().getStringExtra(DESCRIPTIONSJ);
        String datej = getIntent().getStringExtra(DATESJ);
        String timej = getIntent().getStringExtra(TIMESJ);
        Integer heartratej = getIntent().getIntExtra(HEARTRATESJ, 1);


        /*Displaying the time under the 12h format*/
        String AM_PM = "AM";
        if (Integer.parseInt(time.split(":")[0]) > 12) {
            time = Integer.toString(Integer.parseInt(time.split(":")[0]) % 12) + ":" + time.split(":")[1];
            AM_PM = "PM";
            if (Integer.parseInt(time.split(":")[0]) < 10) {
                time = "0" + time;
            }
        }

        idVal.setText(String.valueOf(id));
        patientVal.setText(patientF + " " + patientL);
        doctorVal.setText(doctorF + " " + doctorL);
        descriptionVal.setText(description);
        dateVal.setText(date);
        timeVal.setText(time + " " + AM_PM);
        heartrateVal.setText(String.valueOf(heartrate));

        if(idj !=-1) {
            idValJ.setText(String.valueOf(idj));
            patientValJ.setText(patientFj + " " + patientLj);
            doctorValJ.setText(doctorFj + " " + doctorLj);
            descriptionValJ.setText(descriptionj);
            dateValJ.setText(datej);
            timeValJ.setText(timej + " " + AM_PM);
            heartrateValJ.setText(String.valueOf(heartratej));
        }
    }

    private void initializeVariables() {
        image = (ImageView) findViewById(R.id.image);

        idVal = (TextView) findViewById(R.id.idVal);
        patientVal = (TextView) findViewById(R.id.patientVal);
        doctorVal = (TextView) findViewById(R.id.doctorVal);
        descriptionVal = (TextView) findViewById(R.id.descriptionVal);
        dateVal = (TextView) findViewById(R.id.dateVal);
        timeVal = (TextView) findViewById(R.id.timeVal);
        heartrateVal = (TextView) findViewById(R.id.heartrateVal);

        idValJ = (TextView) findViewById(R.id.idValJ);
        patientValJ = (TextView) findViewById(R.id.patientValJ);
        doctorValJ = (TextView) findViewById(R.id.doctorValJ);
        descriptionValJ = (TextView) findViewById(R.id.descriptionValJ);
        dateValJ = (TextView) findViewById(R.id.dateValJ);
        timeValJ = (TextView) findViewById(R.id.timeValJ);
        heartrateValJ = (TextView) findViewById(R.id.heartrateValJ);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpTo(this, new Intent(this,
                    SearchExamRecordResultsAdapter.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
