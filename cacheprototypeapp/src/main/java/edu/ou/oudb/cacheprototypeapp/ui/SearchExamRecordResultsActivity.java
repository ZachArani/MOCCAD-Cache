package edu.ou.oudb.cacheprototypeapp.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import edu.ou.oudb.cacheprototypeapp.R;
import edu.ou.oudb.cacheprototypelibrary.core.cachemanagers.DecisionalSemanticCacheDataLoader;

/**
 * Created by chenxiao on 5/22/17.
 */

/*Class showing the results of the user's query, in a list view
* containing the image of the patient, his name, and the date of the consultation
* Class used instead of ResultListActivity*/
public class SearchExamRecordResultsActivity extends Activity {
    public static final String RESULT = "result";

    /*ListView*/
    private ListView listView = null;
    /*Array containing all the result*/
    String[] sArray = null;

    /*Array holding the attributes of the query*/
    String[] attributes = null;

    /*Arrays containing each attribute*/
    Integer[] ID = null;
    String[] PFN = null; // Patient First Name
    String[] PLN = null; // Patient Last Name
    String[] DFN = null; // Same with Doctor
    String[] DLN = null;
    String[] Desc = null;
    String[] Dates = null;
    String[] Times = null;
    Integer[] HR = null;

    /*Button to show / hide the costs*/
    private Button show_costs = null;

    /*HorizontalScrollView, here to set its visibility, thus the costs*/
    private HorizontalScrollView hScroll = null;

    /*TextViews to display the costs*/
    private TextView costsText = null;
    private TextView costMoney = null;
    private TextView costTime = null;
    private TextView costEnergy = null;

    //TODO: Store that elsewhere --> Use File or Database --> Do getWritable and getReadable in AsyncTask
    /*Images used
    * Need at least as much images as the number of tuples (e.g. here we need at least 50 images)*/
    private Integer[] imgid = {R.drawable.abo, R.drawable.aeromite, R.drawable.alakazam, R.drawable.aspicot,
            R.drawable.bulbizarre, R.drawable.canartichaud, R.drawable.caninos, R.drawable.carapuce,
            R.drawable.chenipan, R.drawable.chetiflor, R.drawable.craby, R.drawable.doduo,
            R.drawable.excelangue, R.drawable.fantominus, R.drawable.ferosinge, R.drawable.feunard,
            R.drawable.machoc, R.drawable.magneton, R.drawable.marill, R.drawable.melofee,
            R.drawable.mentali, R.drawable.miaouss, R.drawable.mimitoss, R.drawable.mystherbe,
            R.drawable.nidorina, R.drawable.nidorino, R.drawable.noeunoeuf, R.drawable.nostenfer,
            R.drawable.onyx, R.drawable.otaria, R.drawable.paras, R.drawable.piaffabec,
            R.drawable.pikachu, R.drawable.ponyta, R.drawable.psykokwak, R.drawable.ptitard,
            R.drawable.racaillou, R.drawable.ramoloss, R.drawable.ratattac, R.drawable.rondoudou,
            R.drawable.roucoul, R.drawable.sablette, R.drawable.salameche, R.drawable.soporifik,
            R.drawable.substitute, R.drawable.sulfura, R.drawable.tasdmorve, R.drawable.taupiqueur,
            R.drawable.tentacoul, R.drawable.voltorbe};

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_exam_record_results);


        Toast toast = Toast.makeText(this, DecisionalSemanticCacheDataLoader.cacheHitType + ", Query executed on " + DecisionalSemanticCacheDataLoader.executedOn, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        initializeVariables();

        //TODO: We get the data here, put this in SQLite ?
        /*Replacements to only get the useful data*/
        String res = getIntent().getStringExtra(RESULT).replace("], ", "\n")
                .replace("[[", "")
                .replace("[", "")
                .replace("]]", "");

        /*Splitting to get each row
        * (e.g. 1, Juan Coleman, Joseph Payne, etc... then 2, Pamela Daniels, etc...)*/
        sArray = res.split("\n+");

        /*Defining the Arrays*/
        initializeArrays();

        /*Filling the Arrays*/
        arraysValues();

        /*Shows or hides the costs*/
        onShowCostsButtonClicked();

        /*Displays costs according to whether Mobile device or Cloud is the best*/
        costMoney.setText("" + String.format(Locale.US, "%.4f", (DecisionalSemanticCacheDataLoader.resultMoneyM + DecisionalSemanticCacheDataLoader.resultMoneyC) * 100) + " cents");
        costTime.setText("" + String.format(Locale.US, "%.3f", (DecisionalSemanticCacheDataLoader.resultTimeM + DecisionalSemanticCacheDataLoader.resultTimeC) * 0.001) + " ms");
        costEnergy.setText("" + String.format(Locale.US, "%.4f", (DecisionalSemanticCacheDataLoader.resultEnergyM + DecisionalSemanticCacheDataLoader.resultEnergyC)) + " mAh");

        /*Creation of the list using our custom adapter
        * Arrays will be used in SearchExamRecordResultsAdapter*/
        SearchExamRecordResultsAdapter adapter = new SearchExamRecordResultsAdapter(this, imgid, ID, PFN, PLN, DFN, DLN, Desc, Dates, Times, HR);
        listView.setAdapter(adapter);
    }

    /*Sorting methods*/
    public void sortById() {
        for (int i = 0; i < sArray.length; i++) {
            String string = sArray[i];
            int val = Integer.parseInt(sArray[i].split(", +")[0]);
            int j = i;
            while (j > 0 && Integer.parseInt(sArray[j - 1].split(", +")[0]) > val) {
                sArray[j] = sArray[j - 1];
                j--;
            }
            sArray[j] = string;
        }
        /*Filling the Arrays with the updated order to update the list*/
        arraysValues();
        SearchExamRecordResultsAdapter adapter = new SearchExamRecordResultsAdapter(this, imgid, ID, PFN, PLN, DFN, DLN, Desc, Dates, Times, HR);
        listView.setAdapter(adapter);
    }

    public void sortByPatientFirstName() {
        /*This line ensures the results are sorted by ID on top of being sorted by patient names
        * This can be useful if two patients have the same first name for example
        * It can be seen more easily with the descriptions, so you may want to take a look at it*/
        sortById();
        for (int i = 0; i < sArray.length; i++) {
            String string = sArray[i];
            String val = sArray[i].split(", +")[1];
            int j = i;
            while (j > 0 && sArray[j - 1].split(", +")[1].compareTo(val) > 0) {
                sArray[j] = sArray[j - 1];
                j--;
            }
            sArray[j] = string;
        }
        arraysValues();
        SearchExamRecordResultsAdapter adapter = new SearchExamRecordResultsAdapter(this, imgid, ID, PFN, PLN, DFN, DLN, Desc, Dates, Times, HR);
        listView.setAdapter(adapter);
    }

    public void sortByPatientLastName() {
        sortById();
        for (int i = 0; i < sArray.length; i++) {
            String string = sArray[i];
            String val = sArray[i].split(", +")[2];
            int j = i;
            while (j > 0 && sArray[j - 1].split(", +")[2].compareTo(val) > 0) {
                sArray[j] = sArray[j - 1];
                j--;
            }
            sArray[j] = string;
        }
        arraysValues();
        SearchExamRecordResultsAdapter adapter = new SearchExamRecordResultsAdapter(this, imgid, ID, PFN, PLN, DFN, DLN, Desc, Dates, Times, HR);
        listView.setAdapter(adapter);
    }

    public void sortByDoctorFirstName() {
        sortById();
        for (int i = 0; i < sArray.length; i++) {
            String string = sArray[i];
            String val = sArray[i].split(", +")[3];
            int j = i;
            while (j > 0 && sArray[j - 1].split(", +")[3].compareTo(val) > 0) {
                sArray[j] = sArray[j - 1];
                j--;
            }
            sArray[j] = string;
        }
        arraysValues();
        SearchExamRecordResultsAdapter adapter = new SearchExamRecordResultsAdapter(this, imgid, ID, PFN, PLN, DFN, DLN, Desc, Dates, Times, HR);
        listView.setAdapter(adapter);
    }

    public void sortByDoctorLastName() {
        sortById();
        for (int i = 0; i < sArray.length; i++) {
            String string = sArray[i];
            String val = sArray[i].split(", +")[4];
            int j = i;
            while (j > 0 && sArray[j - 1].split(", +")[4].compareTo(val) > 0) {
                sArray[j] = sArray[j - 1];
                j--;
            }
            sArray[j] = string;
        }
        arraysValues();
        SearchExamRecordResultsAdapter adapter = new SearchExamRecordResultsAdapter(this, imgid, ID, PFN, PLN, DFN, DLN, Desc, Dates, Times, HR);
        listView.setAdapter(adapter);
    }

    public void sortByDescription() {
        sortById();
        for (int i = 0; i < sArray.length; i++) {
            String string = sArray[i];
            String val = sArray[i].split(", +")[5];
            int j = i;
            while (j > 0 && sArray[j - 1].split(", +")[5].compareTo(val) > 0) {
                sArray[j] = sArray[j - 1];
                j--;
            }
            sArray[j] = string;
        }
        arraysValues();
        SearchExamRecordResultsAdapter adapter = new SearchExamRecordResultsAdapter(this, imgid, ID, PFN, PLN, DFN, DLN, Desc, Dates, Times, HR);
        listView.setAdapter(adapter);
    }

    public void sortByDate() {
        sortById();
        for (int i = 0; i < sArray.length; i++) {
            String string = sArray[i];
            String val = sArray[i].split(", +")[6].substring(0, 10);
            int j = i;
            while (j > 0 && sArray[j - 1].split(", +")[6].substring(0, 10).compareTo(val) > 0) {
                sArray[j] = sArray[j - 1];
                j--;
            }
            sArray[j] = string;
        }
        arraysValues();
        SearchExamRecordResultsAdapter adapter = new SearchExamRecordResultsAdapter(this, imgid, ID, PFN, PLN, DFN, DLN, Desc, Dates, Times, HR);
        listView.setAdapter(adapter);
    }

    public void sortByTime() {
        sortById();
        for (int i = 0; i < sArray.length; i++) {
            String string = sArray[i];
            String val = sArray[i].split(", +")[6].substring(11, 16);
            int j = i;
            while (j > 0 && sArray[j - 1].split(", +")[6].substring(11, 16).compareTo(val) > 0) {
                sArray[j] = sArray[j - 1];
                j--;
            }
            sArray[j] = string;
        }
        arraysValues();
        SearchExamRecordResultsAdapter adapter = new SearchExamRecordResultsAdapter(this, imgid, ID, PFN, PLN, DFN, DLN, Desc, Dates, Times, HR);
        listView.setAdapter(adapter);
    }

    public void sortByHeartRate() {
        sortById();
        for (int i = 0; i < sArray.length; i++) {
            String string = sArray[i];
            int val = Integer.parseInt(sArray[i].split(", +")[7]);
            int j = i;
            while (j > 0 && Integer.parseInt(sArray[j - 1].split(", +")[7]) > val) {
                sArray[j] = sArray[j - 1];
                j--;
            }
            sArray[j] = string;
        }
        arraysValues();
        SearchExamRecordResultsAdapter adapter = new SearchExamRecordResultsAdapter(this, imgid, ID, PFN, PLN, DFN, DLN, Desc, Dates, Times, HR);
        listView.setAdapter(adapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu, this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_exam_record_resutls, menu);

        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item); // Displays the spinner in the ActionBar, allowing the user to sort the result

        /*Values to put into the spinner*/
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.attributes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (parent.getSelectedItemPosition()) {
                    case 0:
                        sortById();
                        break;
                    case 1:
                        sortByPatientFirstName();
                        break;
                    case 2:
                        sortByPatientLastName();
                        break;
                    case 3:
                        sortByDoctorFirstName();
                        break;
                    case 4:
                        sortByDoctorLastName();
                        break;
                    case 5:
                        sortByDescription();
                        break;
                    case 6:
                        sortByDate();
                        break;
                    case 7:
                        sortByTime();
                        break;
                    case 8:
                        sortByHeartRate();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeVariables() {
        listView = (ListView) findViewById(R.id.listview);

        show_costs = (Button) findViewById(R.id.show_costs);

        hScroll = (HorizontalScrollView) findViewById(R.id.scroll_view);
        costsText = (TextView) findViewById(R.id.costsText);
        costMoney = (TextView) findViewById(R.id.costMoney);
        costTime = (TextView) findViewById(R.id.costTime);
        costEnergy = (TextView) findViewById(R.id.costEnergy);
    }

    public void initializeArrays() {
        ID = new Integer[sArray.length];
        PFN = new String[sArray.length];
        PLN = new String[sArray.length];
        DFN = new String[sArray.length];
        DLN = new String[sArray.length];
        Desc = new String[sArray.length];
        Dates = new String[sArray.length];
        Times = new String[sArray.length];
        HR = new Integer[sArray.length];
    }

    /*Puts all IDs into an Array, same for Patients First Names, and so on*/
    public void arraysValues() {
        attributes = sArray[0].split(", +");
        for (int i = 1; i < sArray.length; i++) {
            String[] splitLine = sArray[i].split(", +");
            int current = 0;
            for(String a : attributes) {
                /*noteid, patientfirstname, patientlastname, doctorfirstname, doctorlastname, description, p_date_time, heartrate*/
                switch (a) {
                    case "noteid":
                        ID[i] = Integer.parseInt(splitLine[current]);
                        break;

                    case "patientfirstname":
                        PFN[i] = splitLine[current];
                        break;

                    case "patientlastname":
                        PLN[i] = splitLine[current];
                        break;

                    case "doctorfirstname":
                        DFN[i] = splitLine[current];
                        break;

                    case "doctorlastname":
                        DLN[i] = splitLine[current];
                        break;

                    case "description":
                        Desc[i] = splitLine[current];
                        break;

                    case "p_date_time":
                        Dates[i] = splitLine[current].substring(0, 10);
                        Times[i] = splitLine[current].substring(11, 16);
                        break;

                    case "heartrate":
                        HR[i] = Integer.parseInt(splitLine[current]);
                        break;
                }
                current++;
            }
        }
    }

    public void onShowCostsButtonClicked(){
        show_costs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*If costs are hidden and button is clicked*/
                if(show_costs.getText().equals("Show costs")) {
                    /*Displays costs*/
                    costsText.setVisibility(View.VISIBLE);
                    hScroll.setVisibility(View.VISIBLE);
                    /*Change the text*/
                    show_costs.setText(String.format(Locale.US, "%s", "Hide costs"));
                /*If costs are visible*/
                } else {
                    /*Hides costs*/
                    costsText.setVisibility(View.INVISIBLE);
                    hScroll.setVisibility(View.INVISIBLE);
                    /*Change the text*/
                    show_costs.setText(String.format(Locale.US, "%s", "Show costs"));
                }
            }
        });
    }
}