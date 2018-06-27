package edu.ou.oudb.cacheprototypeapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import edu.ou.oudb.cacheprototypeapp.R;

/**
 * Created by chenxiao on 6/5/17.
 */

/*Adapter used in SearchExamRecordResultsActivity, to display the list*/
public class SearchExamRecordResultsAdapter extends ArrayAdapter<String>{

    private final Activity context;
    private final Integer[] imgid;
    private final Integer[] id;
    private final String[] patientsFirstName;
    private final String[] patientsLastName;
    private final String[] doctorsFirstName;
    private final String[] doctorsLastName;
    private final String[] descriptions;
    private final String[] dates;
    private final String[] times;
    private final Integer[] heartrates;
    //Join attributes
    private final Integer[] idJ;
    private final String[] patientsFirstNameJ;
    private final String[] patientsLastNameJ;
    private final String[] doctorsFirstNameJ;
    private final String[] doctorsLastNameJ;
    private final String[] descriptionsJ;
    private final String[] datesJ;
    private final String[] timesJ;
    private final Integer[] heartratesJ;

    //To know the query is a join or not
    boolean isJoin = false;

    public SearchExamRecordResultsAdapter(Activity context, Integer[] imgid, Integer[] id, String[] patientsFirstName, String[] patientsLastName, String[] doctorsFirstName,
                                          String[] doctorsLastName, String[] descriptions, String[] dates, String[] times, Integer[] heartrates){
        super(context, R.layout.activity_search_exam_record_results_list_view, patientsFirstName);

        this.context = context;
        this.imgid = imgid;
        this.id = id;
        this.patientsFirstName = patientsFirstName;
        this.patientsLastName = patientsLastName;
        this.doctorsFirstName = doctorsFirstName;
        this.doctorsLastName = doctorsLastName;
        this.descriptions = descriptions;
        this.dates = dates;
        this.times = times;
        this.heartrates = heartrates;

        this.idJ = null;
        this.patientsFirstNameJ = null;
        this.patientsLastNameJ = null;
        this.doctorsFirstNameJ = null;
        this.doctorsLastNameJ = null;
        this.descriptionsJ = null;
        this.datesJ = null;
        this.timesJ = null;
        this.heartratesJ = null;
        this.isJoin = false;
    }

    public SearchExamRecordResultsAdapter(Activity context, Integer[] imgid, Integer[] id, String[] patientsFirstName, String[] patientsLastName, String[] doctorsFirstName,
                                          String[] doctorsLastName, String[] descriptions, String[] dates, String[] times, Integer[] heartrates, Integer[] idj, String[] patientsFirstNamej, String[] patientsLastNamej, String[] doctorsFirstNamej,
                                          String[] doctorsLastNamej, String[] descriptionsj, String[] datesj, String[] timesj, Integer[] heartratesj){
        super(context, R.layout.activity_search_exam_record_results_list_view, patientsFirstName);

        this.context = context;
        this.imgid = imgid;
        this.id = id;
        this.patientsFirstName = patientsFirstName;
        this.patientsLastName = patientsLastName;
        this.doctorsFirstName = doctorsFirstName;
        this.doctorsLastName = doctorsLastName;
        this.descriptions = descriptions;
        this.dates = dates;
        this.times = times;
        this.heartrates = heartrates;
        this.idJ = idj;
        this.patientsFirstNameJ = patientsFirstNamej;
        this.patientsLastNameJ = patientsLastNamej;
        this.doctorsFirstNameJ = doctorsFirstNamej;
        this.doctorsLastNameJ = doctorsLastNamej;
        this.descriptionsJ = descriptionsj;
        this.datesJ = datesj;
        this.timesJ = timesj;
        this.heartratesJ = heartratesj;
        this.isJoin = true;
    }


    @Override
    public View getView(final int position, View view, ViewGroup parent){

        final ViewHolder holder;
        /*If view is new*/
        if(view == null){
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.activity_search_exam_record_results_list_view, parent, false);
            holder = new ViewHolder();
            /*Lookups*/
            holder.patientname = (TextView) view.findViewById(R.id.name);
            holder.p_date = (TextView) view.findViewById(R.id.date);
            holder.img = (ImageView) view.findViewById(R.id.photo);
            view.setTag(holder);
        /*If view already exists, reuse it
        * This is a slight optimization to avoid too many lookups*/
        } else {
            holder = (ViewHolder) view.getTag();
        }

        /*Sets texts and image*/
        holder.patientname.setText(patientsFirstName[position] + " " + patientsLastName[position]);
        holder.p_date.setText(dates[position]);
        holder.img.setImageResource(imgid[id[position] - 1]);

        if(this.isJoin) {
            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), SearchExamRecordResultsDetailsActivity.class);
                    /*Builds the image cache*/
                    holder.img.buildDrawingCache();
                    /*Gets the images according to the image cache*/
                    Bitmap image = holder.img.getDrawingCache();
                    Bundle extras = new Bundle();
                    extras.putParcelable(SearchExamRecordResultsDetailsActivity.IMAGE, image);
                    intent.putExtras(extras);
                    /*Passes all the arrays*/
                    intent.putExtra(SearchExamRecordResultsDetailsActivity.ID, id[position]);
                    intent.putExtra(SearchExamRecordResultsDetailsActivity.PATIENTSF, patientsFirstName[position]);
                    intent.putExtra(SearchExamRecordResultsDetailsActivity.PATIENTSL, patientsLastName[position]);
                    intent.putExtra(SearchExamRecordResultsDetailsActivity.DOCTORSF, doctorsFirstName[position]);
                    intent.putExtra(SearchExamRecordResultsDetailsActivity.DOCTORSL, doctorsLastName[position]);
                    intent.putExtra(SearchExamRecordResultsDetailsActivity.DESCRIPTIONS, descriptions[position]);
                    intent.putExtra(SearchExamRecordResultsDetailsActivity.DATES, dates[position]);
                    intent.putExtra(SearchExamRecordResultsDetailsActivity.TIMES, times[position]);
                    intent.putExtra(SearchExamRecordResultsDetailsActivity.HEARTRATES, heartrates[position]);
                    intent.putExtra(SearchExamRecordResultsDetailsActivity.IDJ, idJ[position]);
                    intent.putExtra(SearchExamRecordResultsDetailsActivity.PATIENTSFJ, patientsFirstNameJ[position]);
                    intent.putExtra(SearchExamRecordResultsDetailsActivity.PATIENTSLJ, patientsLastNameJ[position]);
                    intent.putExtra(SearchExamRecordResultsDetailsActivity.DOCTORSFJ, doctorsFirstNameJ[position]);
                    intent.putExtra(SearchExamRecordResultsDetailsActivity.DOCTORSLJ, doctorsLastNameJ[position]);
                    intent.putExtra(SearchExamRecordResultsDetailsActivity.DESCRIPTIONSJ, descriptionsJ[position]);
                    intent.putExtra(SearchExamRecordResultsDetailsActivity.DATESJ, datesJ[position]);
                    intent.putExtra(SearchExamRecordResultsDetailsActivity.TIMESJ, timesJ[position]);
                    intent.putExtra(SearchExamRecordResultsDetailsActivity.HEARTRATESJ, heartratesJ[position]);


                    /*Starts SearchExamRecordResultsDetailsActivity with all the data*/
                    context.startActivity(intent);
                    /*Frees the resources used by the image cache
                     * This will cleanup the image cache after the buildDrawingCache has been used
                     * We put it here so the image is passed to the ResultsDetailsActivity*/
                    holder.img.destroyDrawingCache();
                }
            });
        }else{
            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), SearchExamRecordResultsDetailsActivity.class);
                    /*Builds the image cache*/
                    holder.img.buildDrawingCache();
                    /*Gets the images according to the image cache*/
                    Bitmap image = holder.img.getDrawingCache();
                    Bundle extras = new Bundle();
                    extras.putParcelable(SearchExamRecordResultsDetailsActivity.IMAGE, image);
                    intent.putExtras(extras);
                    /*Passes all the arrays*/
                    intent.putExtra(SearchExamRecordResultsDetailsActivity.ID, id[position]);
                    intent.putExtra(SearchExamRecordResultsDetailsActivity.PATIENTSF, patientsFirstName[position]);
                    intent.putExtra(SearchExamRecordResultsDetailsActivity.PATIENTSL, patientsLastName[position]);
                    intent.putExtra(SearchExamRecordResultsDetailsActivity.DOCTORSF, doctorsFirstName[position]);
                    intent.putExtra(SearchExamRecordResultsDetailsActivity.DOCTORSL, doctorsLastName[position]);
                    intent.putExtra(SearchExamRecordResultsDetailsActivity.DESCRIPTIONS, descriptions[position]);
                    intent.putExtra(SearchExamRecordResultsDetailsActivity.DATES, dates[position]);
                    intent.putExtra(SearchExamRecordResultsDetailsActivity.TIMES, times[position]);
                    intent.putExtra(SearchExamRecordResultsDetailsActivity.HEARTRATES, heartrates[position]);

                    /*Starts SearchExamRecordResultsDetailsActivity with all the data*/
                    context.startActivity(intent);
                    /*Frees the resources used by the image cache
                     * This will cleanup the image cache after the buildDrawingCache has been used
                     * We put it here so the image is passed to the ResultsDetailsActivity*/
                    holder.img.destroyDrawingCache();
                }
            });
        }

        return view;
    }

    /*ViewHolder, used to make the scroll smoother*/
    static class ViewHolder{
        private TextView patientname;
        private TextView p_date;
        private ImageView img;
    }

}
