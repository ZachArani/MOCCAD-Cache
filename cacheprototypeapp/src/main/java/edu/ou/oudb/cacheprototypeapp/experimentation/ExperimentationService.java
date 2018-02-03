package edu.ou.oudb.cacheprototypeapp.experimentation;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ou.oudb.cacheprototypeapp.AndroidCachePrototypeApplication;
import edu.ou.oudb.cacheprototypeapp.R;
import edu.ou.oudb.cacheprototypeapp.ui.SettingsActivity;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.ConstraintsNotRespectedException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.DownloadDataException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.InvalidPredicateException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.JSONParserException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.TrivialPredicateException;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Predicate;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.PredicateFactory;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;
import edu.ou.oudb.cacheprototypelibrary.utils.StatisticsManager;

public class ExperimentationService extends IntentService
{
	private BroadcastNotifier mBroadcaster = new BroadcastNotifier(this);
	
	Exception exception = null;
	public ExperimentationService()
	{
		super("Experimentation");
	}
	
	@Override
	protected void onHandleIntent(Intent workIntent) {

		//FIXME: WAS THE MIKAEL'S CACHE QUERIES VERSION
		List<Query> warmupQueries = getQueries(this, R.raw.cache_queries);
		List<Query> queriesToProcess = null;
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		int nbQueriesToExecute = Integer.parseInt(sharedPref.getString(SettingsActivity.KEY_PREF_NB_QUERIES_TO_PROCESS,"0"));
		int sizeOfQuerySet = 0;
		
		// used to update estimations on the cloud
		int[] experiments = {R.raw.queries};
		
		int[] warmupexp = {
			   			//R.raw.queries_exp_extended_hit_0,
                        //R.raw.queries_exp_extended_hit_10
						};

		int[] experimentsPartialHit = {
						/*R.raw.queries_exp_partial_hit_0,
						R.raw.queries_exp_partial_hit_10,
						R.raw.queries_exp_partial_hit_20,
						R.raw.queries_exp_partial_hit_30,
						R.raw.queries_exp_partial_hit_40,
						R.raw.queries_exp_partial_hit_50,
						R.raw.queries_exp_partial_hit_60,
						R.raw.queries_exp_partial_hit_70,
						R.raw.queries_exp_partial_hit_80,
						R.raw.queries_exp_partial_hit_90,
						R.raw.queries_exp_partial_hit_100*/
						};

		//FIXME: WASN'T THERE
		int[] myExperiment = {
				R.raw.queries_maxime
		};

		//FIXME: WASN'T COMMENTED
        int[] experimentsExtendedHit = {
                        /*R.raw.queries_exp_extended_hit_0,
                        R.raw.queries_exp_extended_hit_10,
                        R.raw.queries_exp_extended_hit_20,
                        R.raw.queries_exp_extended_hit_30,
                        R.raw.queries_exp_extended_hit_40,
                        R.raw.queries_exp_extended_hit_50,
                        R.raw.queries_exp_extended_hit_60,
                        R.raw.queries_exp_extended_hit_70,
                        R.raw.queries_exp_extended_hit_80,
                        R.raw.queries_exp_extended_hit_90,
                        R.raw.queries_exp_extended_hit_100*/
                        };
		
		// warming cache up
		mBroadcaster.notifyProgress(BroadcastNotifier.STATE_ACTION_WARMUP_STARTED, String.valueOf(warmupQueries.size()));

		warmupCache(warmupQueries);
		
		if (!handleErrors())
		{
			mBroadcaster.notifyProgress(BroadcastNotifier.STATE_ACTION_WARMUP_COMPLETED, "");


            /*for (int i = 0; i < warmupexp.length; ++i) {
                queriesToProcess = getQueries(this, warmupexp[i]);
                sizeOfQuerySet = queriesToProcess.size();
                mBroadcaster.notifyProgress(BroadcastNotifier.STATE_ACTION_STARTED,
                        String.valueOf((sizeOfQuerySet < nbQueriesToExecute) ? sizeOfQuerySet : nbQueriesToExecute));

                //<editor-fold desc="LOG START EXPERIMENTATION">
                StatisticsManager.createFileWriter("warmup_" + i);
                //</editor-fold>
                runExperimentation(queriesToProcess, nbQueriesToExecute);
                //<editor-fold desc="LOG STOP EXPERIMENTATION">
                StatisticsManager.close();
                //</editor-fold>

                if (!handleErrors()) {
                    mBroadcaster.notifyProgress(BroadcastNotifier.STATE_ACTION_COMPLETED, "");
                }
            }*/

            //System.gc(); // clean memory

			for (int k=0; k < 1; ++k) {
                // experimentation for exact hit
				//FIXME: PREVIOUSLY experimentsExtendedHit instead of myExperiment
                for (int i = 0; i < myExperiment.length; ++i) {
                    queriesToProcess = getQueries(this, myExperiment[i]);
                    sizeOfQuerySet = queriesToProcess.size();
                    if (nbQueriesToExecute != 0) {
                        mBroadcaster.notifyProgress(BroadcastNotifier.STATE_ACTION_STARTED,
                                String.valueOf((sizeOfQuerySet < nbQueriesToExecute) ? sizeOfQuerySet : nbQueriesToExecute));
                    } else {
                        mBroadcaster.notifyProgress(BroadcastNotifier.STATE_ACTION_STARTED,
                                String.valueOf(sizeOfQuerySet));
                    }


                    //<editor-fold desc="LOG START EXPERIMENTATION">
                    StatisticsManager.createFileWriter("exp_extended_" + i);
                    //</editor-fold>
                    runExperimentation(queriesToProcess, nbQueriesToExecute);
                    //<editor-fold desc="LOG STOP EXPERIMENTATION">
                    StatisticsManager.close();
                    //</editor-fold>


                    if (!handleErrors()) {
                        mBroadcaster.notifyProgress(BroadcastNotifier.STATE_ACTION_COMPLETED, "");
                    }

                }
            }
			
			/*System.gc(); // clean memory

            for (int k=0; k < 3; ++k) {
                // experimentation for partial hit
                for (int i = 0; i < experimentsPartialHit.length; ++i) {
                    queriesToProcess = getQueries(this, experimentsPartialHit[i]);
                    sizeOfQuerySet = queriesToProcess.size();
                    if (nbQueriesToExecute != 0) {
                        mBroadcaster.notifyProgress(BroadcastNotifier.STATE_ACTION_STARTED,
                                String.valueOf((sizeOfQuerySet < nbQueriesToExecute) ? sizeOfQuerySet : nbQueriesToExecute));
                    } else {
                        mBroadcaster.notifyProgress(BroadcastNotifier.STATE_ACTION_STARTED,
                                String.valueOf(sizeOfQuerySet));
                    }

                    //<editor-fold desc="LOG START EXPERIMENTATION">
                    StatisticsManager.createFileWriter("exp_partial_" + k + "_" + i);
                    //</editor-fold>
                    runExperimentation(queriesToProcess, nbQueriesToExecute);
                    //<editor-fold desc="LOG STOP EXPERIMENTATION">
                    StatisticsManager.close();
                    //</editor-fold>

                    if (!handleErrors()) {
                        mBroadcaster.notifyProgress(BroadcastNotifier.STATE_ACTION_COMPLETED, "");
                    }

                }
            }*/
		}
		
	}
	
	private boolean handleErrors() {
		
		boolean hasErrors = false;
		if(exception != null)
		{
			if (exception instanceof ConnectException)
			{
				mBroadcaster.broadcastIntentWithError(BroadcastNotifier.ERROR_CONNECTION);
			}
			else if (exception instanceof DownloadDataException)
			{
				mBroadcaster.broadcastIntentWithError(BroadcastNotifier.ERROR_DOWNLOAD_DATA);
			} 
			else if (exception instanceof JSONParserException)
			{
				mBroadcaster.broadcastIntentWithError(BroadcastNotifier.ERROR_JSON_PARSER);
			}
			else
			{
				mBroadcaster.broadcastIntentWithError(BroadcastNotifier.ERROR);
			}
			hasErrors = true;
		}
		return hasErrors;
	}
	
	private void warmupCache(List<Query> queries)
	{
		
		int i = 0;
		
		for(Query q: queries)
		{	
			mBroadcaster.notifyProgress(BroadcastNotifier.STATE_ACTION_WARMUP_PROGRESSED, String.valueOf(++i));
			try {
				((AndroidCachePrototypeApplication) getApplicationContext()).getDataLoader().load(q);
			} catch (ConnectException | DownloadDataException | JSONParserException e) {
				exception = e;
				break;
			} catch (ConstraintsNotRespectedException e) {

			}
		}
		
	}


	private void runExperimentation(List<Query> queries, int nbQueriesToProcess)
	{
		int length = queries.size();
		
		int i;

        if (nbQueriesToProcess == 0)
            nbQueriesToProcess = queries.size();

		for(i=0; i < length && i < nbQueriesToProcess ;++i)
		{	
			mBroadcaster.notifyProgress(BroadcastNotifier.STATE_ACTION_PROGRESSED, String.valueOf(i+1));
				
			try {
				((AndroidCachePrototypeApplication) getApplicationContext()).getDataLoader().load(queries.get(i));
			} catch (ConnectException | DownloadDataException | JSONParserException e) {
				exception = e;
				break;
			} catch (ConstraintsNotRespectedException e) {
				
			}
		}
	}

	private List<Query> getQueries(Context context, int resourceID)
	{
		List<Query> queries = new ArrayList<Query>();
		
		Query query = null;
		
		InputStream inputStream = context.getResources().openRawResource(resourceID);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		try {
			String line;
			while((line= reader.readLine()) != null)
			{
				query = getQuery(line);
				queries.add(query);
			}
		} catch (IOException e) {
			Log.e("EXPERIMENTATION", "trouble while reading query files");
			e.printStackTrace();
		}
		
		return queries;
	}

	private Query getQuery(String line) {
		
		Query query = null;
		Set<Predicate> predicates = new HashSet<Predicate>();

		String rightFrom = line.split("FROM")[1].trim();

		String table = rightFrom.split(" ")[0];

		query = new Query(table);

		String predicateStr = rightFrom.split("WHERE")[1].trim();

		predicateStr = predicateStr.substring(0,predicateStr.length()-1);

		String[] predicateList = predicateStr.split("AND");

		int size = predicateList.length;

		String predicateItems[] = null;
		for (int i=0; i < size; ++i)
		{
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

		return query;
	}
}
