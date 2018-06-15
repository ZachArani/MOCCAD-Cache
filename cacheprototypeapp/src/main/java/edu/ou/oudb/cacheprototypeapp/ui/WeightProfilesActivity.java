package edu.ou.oudb.cacheprototypeapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

import edu.ou.oudb.cacheprototypeapp.AndroidCachePrototypeApplication;
import edu.ou.oudb.cacheprototypeapp.R;

/**
 * Created by chenxiao on 5/4/17.
 */

/*Class used to allow the user to set / choose a Weight Profile*/
public class WeightProfilesActivity extends Activity implements View.OnClickListener {
    /*Sum of the SeekBars / Weights*/
    public static final int MAX = 100;

    /*Strings to be used by the SharedPreferences
    * These values will be used in the DecisionalSemanticCacheDataLoader*/
    public static final String WEIGHT_TIME = "weight_time";
    public static final String WEIGHT_MONEY = "weight_money";
    public static final String WEIGHT_ENERGY = "weight_energy";

    /*Buttons*/
    private Button mCancelButton,
            mConfirmButton,
            emergency,
            moneySaver,
            lowPower,
            reset;

    /*SeekBars*/
    private SeekBar timeBar = null,
            moneyBar = null,
            energyBar = null;

    /*TextViews*/
    private TextView timeNumber = null,
            moneyNumber = null,
            energyNumber = null;

    /*Weights to be changed with the SeekBars*/
    private int weightTime = MAX / 3,
            weightMoney = MAX / 3,
            weightEnergy = MAX / 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_weight_profiles);
        initializeVariables();

        /*Buttons*/
        mCancelButton.setOnClickListener(this);

        mConfirmButton.setOnClickListener(this);

        emergency.setOnClickListener(this);
        moneySaver.setOnClickListener(this);
        lowPower.setOnClickListener(this);
        reset.setOnClickListener(this);

        /*SeekBars and TextViews set with default weight value*/
        getReset();

        /*Progress of the SeekBars and changes of the TextViews when the user interacts*/
        getTimeBar();
        getMoneyBar();
        getEnergyBar();
    }

    /*Getters for the weights*/
    public int getWeightTime() {
        return weightTime;
    }

    public int getWeightMoney() {
        return weightMoney;
    }

    public int getWeightEnergy() {
        return weightEnergy;
    }

    /*Setters for the weights*/
    public void setWeightTime(int time) {
        this.weightTime = time;
    }

    public void setWeightMoney(int money) {
        this.weightMoney = money;
    }

    public void setWeightEnergy(int energy) {
        this.weightEnergy = energy;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*Handle action bar item clicks here. The action bar will
        automatically handle clicks on the Home/Up button, so long
        as you specify a parent activity in AndroidManifest.xml.*/
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*Confirm, Cancel, Reset, and Preset profiles buttons actions*/
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

    /*When clicking on cancel, goes back to the Main Activity*/
    private void cancelWeightsDefinition() {
        NavUtils.navigateUpFromSameTask(WeightProfilesActivity.this);
    }

    /*Updates the SharedPreferences to what the user chose*/
    private void confirmWeightsDefinition() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(WEIGHT_TIME, getWeightTime());
        editor.putInt(WEIGHT_MONEY, getWeightMoney());
        editor.putInt(WEIGHT_ENERGY, getWeightEnergy());
        editor.apply();
        /*Updates the SAME mOptimizationParameters
        * Method is in AndroidCachePrototypeApplication*/
        ((AndroidCachePrototypeApplication) getApplication()).updateWeights(); // This is the most important thing
        Log.d("WEIGHT TIME", "" + sharedPref.getInt(WEIGHT_TIME, MAX / 3));
        Log.d("WEIGHT MONEY", "" + sharedPref.getInt(WEIGHT_MONEY, MAX / 3));
        Log.d("WEIGHT ENERGY", "" + sharedPref.getInt(WEIGHT_ENERGY, MAX / 3));
        Log.d("Data Access Provider", "" + sharedPref.getString("pref_ip_address", "DEFAULT"));
        /*Goes to the search exam page*/
//        Intent intent = new Intent(this, SearchExamRecordActivity.class);
        Intent intent = new Intent(this, AttributesSelectionActivity.class);
        startActivity(intent);
    }

    /*Method so the numbers change when the SeekBars do as well*/
    public void getTimeBar() {
        timeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setWeightTime(progress);
                moneyBar.setProgress(MAX - timeBar.getProgress() - energyBar.getProgress());
                energyBar.setProgress(MAX - timeBar.getProgress() - moneyBar.getProgress());
                if (timeNumber != null) {
                    String prog = "" + progress * 0.01;
                    timeNumber.setText(prog);
                }
            }
        });
    }

    public void getMoneyBar() {
        moneyBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setWeightMoney(progress);
                energyBar.setProgress(MAX - timeBar.getProgress() - moneyBar.getProgress());
                timeBar.setProgress(100 - moneyBar.getProgress() - energyBar.getProgress());
                if (moneyNumber != null) {
                    String prog = "" + progress * 0.01;
                    moneyNumber.setText(prog);
                }
            }
        });
    }

    public void getEnergyBar() {
        energyBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setWeightEnergy(progress);
                timeBar.setProgress(MAX - moneyBar.getProgress() - energyBar.getProgress());
                moneyBar.setProgress(MAX - timeBar.getProgress() - energyBar.getProgress());
                if (energyNumber != null) {
                    String prog = "" + progress * 0.01;
                    energyNumber.setText(prog);
                }
            }
        });
    }

    /*Weight Profiles already defined according to what they say
    (e.g. emergency = high weight on time)*/
    public void getEmergency() {
        timeBar.setProgress(80);
        moneyBar.setProgress(5);
        energyBar.setProgress(15);
    }

    public void getMoneySaver() {
        timeBar.setProgress(5);
        moneyBar.setProgress(80);
        energyBar.setProgress(15);
    }

    public void getLowPower() {
        timeBar.setProgress(5);
        moneyBar.setProgress(15);
        energyBar.setProgress(80);
        mConfirmButton.setEnabled(true);
    }

    /*Resets the SeekBars to default (i.e. MAX / 3 for each weight)*/
    public void getReset() {
        timeBar.setProgress(MAX / 3);
        moneyBar.setProgress(MAX / 3);
        energyBar.setProgress(MAX / 3);
        timeNumber.setText(String.format(Locale.US, "%d", MAX / 3));
        moneyNumber.setText(String.format(Locale.US, "%d", MAX / 3));
        energyNumber.setText(String.format(Locale.US, "%d", MAX / 3));
    }

    private void initializeVariables() {
        /*SeekBars*/
        timeBar = (SeekBar) findViewById(R.id.seekBar_time);
        moneyBar = (SeekBar) findViewById(R.id.seekBar_money);
        energyBar = (SeekBar) findViewById(R.id.seekBar_energy);
        /*TextViews*/
        timeNumber = (TextView) findViewById(R.id.timeNumber);
        moneyNumber = (TextView) findViewById(R.id.moneyNumber);
        energyNumber = (TextView) findViewById(R.id.energyNumber);
        /*Predefined weight profiles buttons*/
        emergency = (Button) findViewById(R.id.emergencyButton);
        moneySaver = (Button) findViewById(R.id.moneySaverButton);
        lowPower = (Button) findViewById(R.id.lowPowerButton);
        reset = (Button) findViewById(R.id.resetButton);
        /*Confirm and Cancel buttons*/
        mConfirmButton = (Button) findViewById(R.id.confirmButton);
        mCancelButton = (Button) findViewById(R.id.cancelButton);
    }
}
