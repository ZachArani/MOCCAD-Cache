package edu.ou.oudb.cacheprototypeapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import edu.ou.oudb.cacheprototypeapp.R;
import edu.ou.oudb.cacheprototypeapp.provider.MedicalInfoDbHelper;

/**
 * Created by chenxiao on 6/9/17.
 */

/*Welcome page, this is where all the initializations should be done
* (This is done in MainActivity atm)*/
public class WelcomePageActivity extends Activity {

//    MedicalInfoDbHelper mDbH;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        /*Data to be used in the future*/
//        mDbH = new MedicalInfoDbHelper(getApplicationContext());
//        mDbH.putDataInDb(this);
//        mDbH.readDb();

        Button start = (Button) findViewById(R.id.get_started);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy(){
//        mDbH.close();
        super.onDestroy();
    }

}
