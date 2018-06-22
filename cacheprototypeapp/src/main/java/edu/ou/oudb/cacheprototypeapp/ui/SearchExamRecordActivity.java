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
import edu.ou.oudb.cacheprototypelibrary.querycache.query.JoinQuery;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Predicate;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.PredicateFactory;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;

/**
 * Created by chenxiao on 5/19/17.
 */

/*Class used instead of NewQueryActivity*/
public class SearchExamRecordActivity extends FragmentActivity implements View.OnClickListener, DatePickerFragment.OnDateDataPass, TimePickerFragment.OnTimeDataPass {
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
            isHeartrateSelected = false,
            jQuery = false;

    /*Spinners*/
    private Spinner id_op,
            date_op,
            time_op,
            heartrate_op;

    /*TextViews
    * Put back to Spinners if using the cache with Strings*/
    private TextView patient_op,
            patient_second_op,
            doctor_op,
            doctor_second_op,
            description_op;

    /*EditTexts*/
    private EditText id_value,
            heartrate_value;

    /*AutoCompleteTextViews*/
    private AutoCompleteTextView patient_first_value,
            patient_second_value,
            doctor_first_value,
            doctor_second_value,
            description_value;

    /*Buttons*/
    private Button search,
            date_picker,
            time_picker;

    //TODO: Save all the arrays into a database / file / HashSet so it is only initialized once
    /*Array containing the values to complete with for the Patient First Names*/
    private static final String[] PATIENTFIRSTNAMES = new String[]{
            "Aaron", "Angela", "Anthony", "Arthur", "Betty", "Brian",
            "Carol", "Catherine", "Debra", "Denise", "Edward", "Eugene",
            "Evelyn", "Fred", "Gloria", "Jane", "Jason", "Joan",
            "Jonathan", "Juan", "Judith", "Julie", "Justin", "Kathleen",
            "Margaret", "Maria", "Melissa", "Michael", "Nicholas", "Pamela",
            "Patrick", "Paula", "Peter", "Robin", "Ronald", "Ruby",
            "Sara", "Sarah", "Steve", "Tammy", "Wade", "Wayne"
    };

    /*Array containing the values to complete with for the Patient Last Names*/
    private static final String[] PATIENTLASTNAMES = new String[]{
            "Adams", "Anderson", "Baker", "Barnes", "Berry", "Campbell",
            "Carr", "Carroll", "Chapman", "Coleman", "Collins", "Daniels",
            "Davis", "Day", "Diaz", "Fernandez", "Gardner", "Garrett",
            "George", "Gibson", "Hicks", "Hughes", "Jackson", "Jordan",
            "Lane", "Lopez", "Martin", "Miller", "Oliver", "Owens",
            "Perkins", "Powell", "Price", "Ramos", "Ray", "Richardson",
            "Russell", "Stanley", "Stewart", "Wagner", "Washington", "Weaver",
            "Welch", "Wells", "Wood"
    };

    /*Array containing the values to complete with for the Doctor First Names*/
    private static final String[] DOCTORFIRSTNAMES = new String[]{
            "Alice", "Amy", "Anna", "Annie", "Anthony", "Betty",
            "Brandon", "Carl", "Cheryl", "Daniel", "Donald", "Earl",
            "Elizabeth", "George", "Gloria", "Harry", "Jason", "Jean",
            "Jessica", "John", "Johnny", "Joseph", "Julie", "Katherine",
            "Laura", "Lillian", "Lori", "Louis", "Margaret", "Marie",
            "Nancy", "Pamela", "Paul", "Raymond", "Ryan", "Shirley",
            "Tammy", "Teresa", "Theresa", "Wanda", "Wayne"
    };

    /*Array containing the values to complete with for the Doctor First Names*/
    private static final String[] DOCTORLASTNAMES = new String[]{
            "Alvarez", "Campbell", "Crawford", "Davis", "Dixon", "Fisher",
            "Fox", "Franklin", "George", "Graham", "Greene", "Griffin",
            "Hamilton", "Hansen", "Harrison", "Hawkins", "Holmes", "Jordan",
            "Lawrence", "Marshall", "Matthews", "Mendoza", "Morgan", "Myers",
            "Payne", "Perkins", "Price", "Ramirez", "Ray", "Roberts",
            "Robertson", "Spencer", "Stanley", "Thompson", "Ward", "Washington",
            "Weaver", "West", "Willis"
    };

    /*Array contatining the values to complete with the Descriptions*/
    private static final String[] DESCRIPTIONS = new String[]{
            "Analysis of Body Flu", "Biopsy", "Endoscopy", "Genetic Testing",
            "Imaging", "Measurement of Body"
    };

    /*Used when launching the query*/

    /*Attributes*/
    private String attributeId = null,
            attributePa = null, // First name
            attributePa2 = null, // Last name
            attributeDo = null,
            attributeDo2 = null,
            attributeDe = null,
            attributeDaTi = null, // Attribute used for both date and time
            attributeHe = null,
            /*Operators*/
            operatorId = null,
            operatorPa = null,
            operatorPa2 = null,
            operatorDo = null,
            operatorDo2 = null,
            operatorDe = null,
            operatorDa = null,
            operatorTi = null,
            operatorHe = null,
            /*Values*/
            valueId = null,
            valuePa = null,
            valuePa2 = null,
            valueDo = null,
            valueDo2 = null,
            valueDe = null,
            valueDa = null,
            valueTi = null,
            valueHe = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_exam_record);

        initializeVariables();

        mDBHelper = new ProcessedQueryDbHelper(this);

        /*ArrayAdapter using the string array*/
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.operators, android.R.layout.simple_spinner_item);

        /*Layout when list appears*/
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        /*Associating it to the spinners
        * We now have spinners with all operators*/
        id_op.setAdapter(adapter);
        patient_op.setText("=");
        patient_second_op.setText("=");
        doctor_op.setText("=");
        doctor_second_op.setText("=");
        description_op.setText("=");
        /*If using the cache with Strings, put this back, and switch the TextViews by Spinners*/
//        patient_op.setAdapter(adapter);
//        patient_second_op.setAdapter(adapter);
//        doctor_op.setAdapter(adapter);
//        doctor_second_op.setAdapter(adapter);
//        description_op.setAdapter(adapter);
        date_op.setAdapter(adapter);
        time_op.setAdapter(adapter);
        heartrate_op.setAdapter(adapter);

        /*Patient Name and Doctor Name operators never change so we put it*/
        operatorPa = "=";
        operatorPa2 = "=";
        operatorDo = "=";
        operatorDo2 = "=";
        operatorDe = "=";

        /*Filling the adapter with the array*/
        ArrayAdapter<String> adapterPF = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, PATIENTFIRSTNAMES);
        ArrayAdapter<String> adapterPL = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, PATIENTLASTNAMES);
        ArrayAdapter<String> adapterDF = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, DOCTORFIRSTNAMES);
        ArrayAdapter<String> adapterDL = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, DOCTORLASTNAMES);
        ArrayAdapter<String> adapterDesc = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, DESCRIPTIONS);

        /*Sets adapters
        * Now the AutoCompleteTextViews have the values to retrieve from when the user enters smthg (At least 2 letters)*/
        patient_first_value.setAdapter(adapterPF);
        patient_second_value.setAdapter(adapterPL);
        doctor_first_value.setAdapter(adapterDF);
        doctor_second_value.setAdapter(adapterDL);
        description_value.setAdapter(adapterDesc);

        onSpinnerItemSelected();
        onDateTimeClicked();
        onFieldClicked();

        search.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_button:
                launchQuery();
            case R.id.join_button:
                launchJoinQuery();
        }
    }

    public void launchJoinQuery(){
        jQuery = true;
        String query = "SELECT * FROM patients INNER JOIN one ON one.noteid = patients.noteid;";
        mDBHelper.addQuery(getQuery(query)); // Add the query to the Processed Queries
        (new QueryProcessTask(this)).execute(getQuery(query));
//        }
    }

    public void launchQuery() {
        /*Base for the query*/
//        String query = "SELECT * FROM patients WHERE ";
        //String query = "SELECT noteid FROM patients WHERE ";
        String query = "SELECT * FROM patients WHERE ";
        int cpt = 0;
        /*Checks if the value is null (i.e. the user didn't enter anything)
        * Also checks if the value is empty (i.e. if the user entered something and erased it)
        * The null test needs to be the first, or an exception is thrown*/
        if (valueId == null || valueId.isEmpty()) {
            isIdSelected = false;
        }
        if (valuePa == null || valuePa.isEmpty()) {
            isPatientSelected = false;
        }
        if (valuePa2 == null || valuePa2.isEmpty()) {
            isPatientSecondSelected = false;
        }
        if (valueDo == null || valueDo.isEmpty()) {
            isDoctorSelected = false;
        }
        if (valueDo2 == null || valueDo2.isEmpty()) {
            isDoctorSecondSelected = false;
        }
        if (valueDe == null || valueDe.isEmpty()) {
            isDescriptionSelected = false;
        }
        if (valueHe == null || valueHe.isEmpty()) {
            isHeartrateSelected = false;
        }
        /*In case the user cancels the DatePicker / TimePicker selection
        * This needs to be done because otherwise the boolean are considered true*/
        if (valueDa == null) {
            isDateSelected = false;
        }
        if (valueTi == null) {
            isTimeSelected = false;
        }
        /*Looks if fields have been filled out or not
        * If not, selects all, else we look how much have been filled,
        * and construct the request based on that*/
        /*The replacements and %27 are used in the URL when the request is launched*/
        if (isIdSelected) {
            query = query.concat(attributeId + " " + operatorId + " " + valueId);
            cpt++;
        }
        if (isPatientSelected) {
            if (cpt > 0) {
                query = query.concat(" AND ");
            }
            query = query.concat(attributePa + " " + operatorPa + " %27" + valuePa + "%27");
            cpt++;
        }
        if (isPatientSecondSelected) {
            if (cpt > 0) {
                query = query.concat(" AND ");
            }
            query = query.concat(attributePa2 + " " + operatorPa2 + " %27" + valuePa2 + "%27");
            cpt++;
        }
        if (isDoctorSelected) {
            if (cpt > 0) {
                query = query.concat(" AND ");
            }
            query = query.concat(attributeDo + " " + operatorDo + " %27" + valueDo + "%27");
            cpt++;
        }
        if (isDoctorSecondSelected) {
            if (cpt > 0) {
                query = query.concat(" AND ");
            }
            query = query.concat(attributeDo2 + " " + operatorDo2 + " %27" + valueDo2 + "%27");
            cpt++;
        }
        if (isDescriptionSelected) {
            if (cpt > 0) {
                query = query.concat(" AND ");
            }
            query = query.concat(attributeDe + " " + operatorDe + " %27" + valueDe.replace(" ", "%20") + "%27");
            cpt++;
        }
        if (isDateSelected) {
            if (cpt > 0) {
                query = query.concat(" AND ");
            }
            /*For both Date and Time, we need to specify that we are taking a substring of the attribute
            * We do this to isolate the part we want, in order to run the query*/
            query = query.concat("substr(" + attributeDaTi + ",0,10) " + operatorDa + " %27" + valueDa + "%27");
            cpt++;
        }
        if (isTimeSelected) {
            if (cpt > 0) {
                query = query.concat(" AND ");
            }
            query = query.concat("substr(" + attributeDaTi + ",12) " + operatorTi + " %27" + valueTi + "%27");
            cpt++;
        }
        if (isHeartrateSelected) {
            if (cpt > 0) {
                query = query.concat(" AND ");
            }
            query = query.concat(attributeHe + " " + operatorHe + " " + valueHe);
            cpt++;
        }

        //FIXME: We do this because otherwise we can't do "SELECT * FROM patients;" after having done one query
        //FIXME: This is a quick fix
        if (cpt == 0) {
            query = "SELECT * FROM patients WHERE noteid >= 1";
        }

        query = query.concat(";");
        System.out.println("QUERY: " + query);

//        if (cpt == 0) {
//            query = "SELECT * FROM patients";
//        }

        mDBHelper.addQuery(getQuery(query)); // Add the query to the Processed Queries

        /*Processes the query
        * Gets the result of the query and launches SearchExamRecordResultsActivity*/
//        if (cpt == 0) {
//            (new QueryProcessTask(this)).execute(getQuery("SELECT * FROM patients"));
//        } else {
            (new QueryProcessTask(this)).execute(getQuery(query));
//        }
    }

    /*Convert a String to a Query*/
    private Query getQuery(String line) {

        /*if(jQuery == true){
            JoinQuery jq = null;

            String leftFrom = line.split("FROM")[0].trim();

            String att = leftFrom.substring(7); //should be first non-space after SELECT

            String rightFrom = line.split("FROM")[1].trim();

            String table = rightFrom.split(" ")[0];

            jq = new Query(table);

            String[] attributeList;
            if (!att.equals("*"))
            {
                attributeList = att.split(", ");
            } else {
                attributeList = new String[]
                        {"noteid", "patientfirstname", "patientlastname", "doctorfirstname", "doctorlastname", "description", "p_date_time", "heartrate"};
            }
            for(String a: attributeList)
            {
                attributes.add(a);
            }
            query.addAttributes(attributes);
        }*/

        Query query = null;
        Set<Predicate> predicates = new HashSet<Predicate>();
        Set<String> attributes = new HashSet<String>();

        String leftFrom = line.split("FROM")[0].trim();

        String att = leftFrom.substring(7); //should be first non-space after SELECT

        String rightFrom = line.split("FROM")[1].trim();

        String table = rightFrom.split(" ")[0];

        query = new Query(table);

        String[] attributeList;
        if (!att.equals("*"))
        {
            attributeList = att.split(", ");
        } else {
            attributeList = new String[]
                    {"noteid", "patientfirstname", "patientlastname", "doctorfirstname", "doctorlastname", "description", "p_date_time", "heartrate"};
        }
        for(String a: attributeList)
        {
            attributes.add(a);
        }
        query.addAttributes(attributes);

        /*Only takes requests having a WHERE statement*/
        if (line.contains("WHERE")) {

            String predicateStr = rightFrom.split("WHERE")[1].trim();

            predicateStr = predicateStr.substring(0, predicateStr.length() - 1);

            String[] predicateList = predicateStr.split("AND");

            int size = predicateList.length;

            String predicateItems[] = null;
            for (int i = 0; i < size; ++i) {
                predicateItems = predicateList[i].trim().split(" ");
                try {
                    Predicate p = PredicateFactory.createPredicate(predicateItems[0], predicateItems[1], predicateItems[2]);
                    predicates.add(p);
                } catch (TrivialPredicateException | InvalidPredicateException e) {
                    Log.e("PARSE_QUERY_LINE", "invalid predicate");
                    return null;
                }
            }

            query.addPredicates(predicates);
        }

        return query;
    }

    /*When Date or Time button is clicked, we display a picker*/
    private void onDateTimeClicked() {
        date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newDateFragment = new DatePickerFragment();
                newDateFragment.show(getFragmentManager(), "datePicker");
                isDateSelected = true;
            }
        });
        time_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newTimeFragment = new TimePickerFragment();
                newTimeFragment.show(getFragmentManager(), "timePicker");
                isTimeSelected = true;
            }
        });
    }

    /*Tells us when the user has finished writing
    * This way, we put values that will be used later on for the query*/
    private void onFieldClicked() {
        id_value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                attributeId = "noteid";
                valueId = s.toString();
                isIdSelected = true;
            }
        });
        patient_first_value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                attributePa = "patientfirstname";
                valuePa = s.toString();
                isPatientSelected = true;
            }
        });
        patient_second_value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                attributePa2 = "patientlastname";
                valuePa2 = s.toString();
                isPatientSecondSelected = true;
            }
        });
        doctor_first_value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                attributeDo = "doctorfirstname";
                valueDo = s.toString();
                isDoctorSelected = true;
            }
        });
        doctor_second_value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                attributeDo2 = "doctorlastname";
                valueDo2 = s.toString();
                isDoctorSecondSelected = true;
            }
        });
        description_value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                attributeDe = "description";
                valueDe = s.toString();
                isDescriptionSelected = true;
            }
        });
        heartrate_value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                attributeHe = "heartrate";
                valueHe = s.toString();
                isHeartrateSelected = true;
            }
        });
    }

    /*Tells us which operator of the list is selected*/
    private void onSpinnerItemSelected() {
        id_op.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                operatorId = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // Used if wanting to do queries like "patientfirstname <= 'Juan';" for example
        // Atm we only allow "="
        /*patient_op.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                operatorPa = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        patient_second_op.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                operatorPa2 = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        doctor_op.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                operatorDo = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        doctor_second_op.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                operatorDo2 = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        description_op.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                operatorDe = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
        date_op.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                operatorDa = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        time_op.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                operatorTi = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        heartrate_op.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                operatorHe = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /*Gets the data from the fragment / picker*/
    public void onDateDataPass(String data) {
        if (isDateSelected) {
            /*Sets attribute*/
            attributeDaTi = "p_date_time";
            /*Sets text and value*/
            date_picker.setText(data);
            valueDa = data;
        }
    }

    public void onTimeDataPass(String data) {
        if (isTimeSelected) {
            attributeDaTi = "p_date_time";
            /*Variables used for the display to the user*/
            String AM_PM = "AM";
            int hours = Integer.parseInt(data.split(":")[0]);
            String hourS = Integer.toString(hours);
            int minutes = Integer.parseInt(data.split(":+")[1]);
            String minuteS = Integer.toString(minutes);
            /*Default format is 24h
            * Displaying AM or PM according to the value of hours*/
            if (hours > 12) {
                hours = hours % 12;
                AM_PM = "PM";
                hourS = Integer.toString(hours);
            }
            /*Display purposes*/
            if (hours == 12) {
                AM_PM = "PM";
            }
            if (hours == 0) {
                hours = 12;
                hourS = Integer.toString(hours);
            }
            /*Look TimePickerFragment for this*/
            if (hours < 10) {
                hourS = "0" + hours;
            }
            if (minutes < 10) {
                minuteS = "0" + minutes;
            }
            time_picker.setText(hourS + ":" + minuteS + " " + AM_PM);
            valueTi = data + ":00";
        }
    }

    public void initializeVariables() {
        /*Spinners*/
        id_op = (Spinner) findViewById(R.id.id_op);
        date_op = (Spinner) findViewById(R.id.date_op);
        time_op = (Spinner) findViewById(R.id.time_op);
        heartrate_op = (Spinner) findViewById(R.id.heartrate_op);
        patient_op = (TextView) findViewById(R.id.patient_op);
        patient_second_op = (TextView) findViewById(R.id.patient_second_op);
        doctor_op = (TextView) findViewById(R.id.doctor_op);
        doctor_second_op = (TextView) findViewById(R.id.doctor_second_op);
        description_op = (TextView) findViewById(R.id.description_op);
        /*EditTexts*/
        id_value = (EditText) findViewById(R.id.id_value);
        heartrate_value = (EditText) findViewById(R.id.heartrate_value);
        /*AutoCompleteTextViews*/
        patient_first_value = (AutoCompleteTextView) findViewById(R.id.patient_first_value);
        patient_second_value = (AutoCompleteTextView) findViewById(R.id.patient_second_value);
        doctor_first_value = (AutoCompleteTextView) findViewById(R.id.doctor_first_value);
        doctor_second_value = (AutoCompleteTextView) findViewById(R.id.doctor_second_value);
        description_value = (AutoCompleteTextView) findViewById(R.id.description_value);
        /*Search button, Date and Time pickers*/
        search = (Button) findViewById(R.id.search_button);
        date_picker = (Button) findViewById(R.id.date_picker);
        time_picker = (Button) findViewById(R.id.time_picker);
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
