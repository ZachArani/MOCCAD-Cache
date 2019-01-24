package edu.ou.oudb.cacheprototypeapp.provider;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenxiao on 6/15/17.
 */

/*Class used to store the data that will be queried*/
public class MedicalInfoDbHelper extends SQLiteOpenHelper {

    //Database version
    private static final int DATABASE_VERSION = 1;

    // Database name
    private static final String DATABASE_NAME = "MedicalInfo.db";

    // Table Data
    private static final String TABLE_DATA = "DATA";

    // Columns names
    private static final String KEY_QUERY_ID = "query_id";
    private static final String KEY_AGE = "age";
    private static final String KEY_NB_SEX_PART = "nb_sex_part";
    private static final String KEY_FIRST_SEX_INTER = "first_sex_inter";
    private static final String KEY_NB_PREGNANCIES = "nb_pregnancies";
    private static final String KEY_SMOKES = "smokes";
    private static final String KEY_HORMONAL_CONTRACEPTIVES = "hormonal_contraceptives";
    private static final String KEY_IUD = "iud";
    private static final String KEY_STDS = "stds";
    private static final String KEY_NB_DIAGNOSIS = "nb_diagnosis";

    public MedicalInfoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DATA_TABLE = "CREATE TABLE " + TABLE_DATA + "("
                + KEY_QUERY_ID + " INTEGER PRIMARY KEY,"
                + KEY_AGE + " INTEGER,"
                + KEY_NB_SEX_PART + " INTEGER,"
                + KEY_FIRST_SEX_INTER + " INTEGER,"
                + KEY_NB_PREGNANCIES + " INTEGER,"
                + KEY_SMOKES + " BOOLEAN,"
                + KEY_HORMONAL_CONTRACEPTIVES + " BOOLEAN,"
                + KEY_IUD + " BOOLEAN,"
                + KEY_STDS + " INTEGER,"
                + KEY_NB_DIAGNOSIS + " INTEGER"
                + ");";

        db.execSQL(CREATE_DATA_TABLE);
        Log.i("data_base path", db.getPath());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Deletes previous db
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA);

        // Recreates the table
        onCreate(db);
    }

    public void putDataInDb(Context context) {
        /*Get the CSV file we want to read from*/
        String myCSVFile = "risk_factors_cervical_cancer.csv";
        AssetManager manager = context.getAssets();
        InputStream inputStream = null;
        try {
            inputStream = manager.open(myCSVFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        /*Gets db and insert values in it*/
        SQLiteDatabase db = this.getWritableDatabase();
        onUpgrade(db,DATABASE_VERSION,DATABASE_VERSION+1);
        db.beginTransaction();
        try {
            while ((line = bufferedReader.readLine()) != null) {
                String[] colums = line.split(",");
                if(!line.contains("Age")){
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(KEY_AGE, colums[0].trim());
                    contentValues.put(KEY_NB_SEX_PART, colums[1].trim());
                    contentValues.put(KEY_FIRST_SEX_INTER, colums[2].trim());
                    contentValues.put(KEY_NB_PREGNANCIES, colums[3].trim());
                    contentValues.put(KEY_SMOKES, colums[4].trim());
                    contentValues.put(KEY_HORMONAL_CONTRACEPTIVES, colums[7].trim());
                    contentValues.put(KEY_IUD, colums[9].trim());
                    contentValues.put(KEY_STDS, colums[12].trim());
                    contentValues.put(KEY_NB_DIAGNOSIS, colums[25].trim());
                    db.insert(TABLE_DATA, null, contentValues);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public void readDb() {
        SQLiteDatabase db = this.getWritableDatabase();

        /*The attributes we want to get*/
        String[] projection = {
                KEY_QUERY_ID,
                KEY_AGE,
                KEY_NB_SEX_PART,
                KEY_FIRST_SEX_INTER,
                KEY_NB_PREGNANCIES,
                KEY_SMOKES,
                KEY_HORMONAL_CONTRACEPTIVES,
                KEY_IUD,
                KEY_STDS,
                KEY_NB_DIAGNOSIS
        };

        String sortOrder = KEY_QUERY_ID + " ASC";

        Cursor cursor = db.query(TABLE_DATA, projection, null, null, null, null, sortOrder);

        List items = new ArrayList<>();
        while (cursor.moveToNext()) {
            /*Gets values from db*/
            int itemId = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_QUERY_ID));
            int age = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_AGE));
            int nbsexpart = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_NB_SEX_PART));
            int firstsexinter = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_FIRST_SEX_INTER));
            int nbpregn = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_NB_PREGNANCIES));
            int smokes = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SMOKES));
            int hormcontr = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_HORMONAL_CONTRACEPTIVES));
            int iud = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IUD));
            int stds = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_STDS));
            int nbdiag = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_NB_DIAGNOSIS));
            /*Storing the values into arrays*/
            items.add(itemId);
            items.add(age);
            items.add(nbsexpart);
            items.add(firstsexinter);
            items.add(nbpregn);
            items.add(smokes);
            items.add(hormcontr);
            items.add(iud);
            items.add(stds);
            items.add(nbdiag);
        }
        /*String you can use to get all the info
        * (e.g. values[i].split(",")[0] gives all the ids)*/
        String[] values = new String[items.size()];
        for (int i = 0; i < items.size(); i++) {
            values[i] = items.get(i).toString();
//            System.out.println(values[i]);
        }
        cursor.close();
        db.close();
    }

}
