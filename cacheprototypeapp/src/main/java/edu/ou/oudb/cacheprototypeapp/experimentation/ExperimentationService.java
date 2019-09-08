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
import edu.ou.oudb.cacheprototypelibrary.core.cachemanagers.DataLoader;
import edu.ou.oudb.cacheprototypelibrary.core.cachemanagers.DecisionalSemanticCacheDataLoader;

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
		List<Query> warmupQueries = getQueries(this, R.raw.tpch_warmup);
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
				R.raw.tpch_test
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
        if (!handleErrors())
		{


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
                for (int i = 0; i < myExperiment.length; ++i) {
                    //Warm-up cache
                 //   mBroadcaster.notifyProgress(BroadcastNotifier.STATE_ACTION_WARMUP_STARTED, String.valueOf(warmupQueries.size()));
               //     warmupCache(warmupQueries);
                  //  mBroadcaster.notifyProgress(BroadcastNotifier.STATE_ACTION_WARMUP_COMPLETED, "");
                    //Gather Queries
                    queriesToProcess = getQueries(this, myExperiment[i]);
                    sizeOfQuerySet = queriesToProcess.size();
                    if (nbQueriesToExecute != 0) {
                        mBroadcaster.notifyProgress(BroadcastNotifier.STATE_ACTION_STARTED,
                                String.valueOf((sizeOfQuerySet < nbQueriesToExecute) ? sizeOfQuerySet : nbQueriesToExecute));
                    } else {
                        mBroadcaster.notifyProgress(BroadcastNotifier.STATE_ACTION_STARTED,
                                String.valueOf(sizeOfQuerySet));
                    }

                    //Begin experiment
                    //<editor-fold desc="LOG START EXPERIMENTATION">
                    StatisticsManager.createFileWriter("test_experiment_" + i + "_LRU");
                    //</editor-fold>
					//First run, LRU
					((AndroidCachePrototypeApplication) getApplicationContext()).updateQueryCache("LRU");
                    StatisticsManager.finishedExperiment(); //Clear experiment log writer before starting
					//TODO: Edit logger to have CSV format
                    runExperimentation(queriesToProcess, nbQueriesToExecute);
                    StatisticsManager.finishedExperiment();
                    //<editor-fold desc="LOG STOP EXPERIMENTATION">
                    clearCache(); //Remove all entries from cache before we start again
                    StatisticsManager.close();
                    //</editor-fold>


					//Do it all again
					//Warm-up cache
				//	mBroadcaster.notifyProgress(BroadcastNotifier.STATE_ACTION_WARMUP_STARTED, String.valueOf(warmupQueries.size()));
					//(warmupQueries);
				//	mBroadcaster.notifyProgress(BroadcastNotifier.STATE_ACTION_WARMUP_COMPLETED, "");
					//Gather Queries
					queriesToProcess = getQueries(this, myExperiment[i]);
					sizeOfQuerySet = queriesToProcess.size();
					if (nbQueriesToExecute != 0) {
						mBroadcaster.notifyProgress(BroadcastNotifier.STATE_ACTION_STARTED,
								String.valueOf((sizeOfQuerySet < nbQueriesToExecute) ? sizeOfQuerySet : nbQueriesToExecute));
					} else {
						mBroadcaster.notifyProgress(BroadcastNotifier.STATE_ACTION_STARTED,
								String.valueOf(sizeOfQuerySet));
					}

					//Begin experiment
					//<editor-fold desc="LOG START EXPERIMENTATION">
					StatisticsManager.createFileWriter("test_experiment_" + i + "_LFUSQEP");
					//</editor-fold>
					//Second run, LFUSQEP
					((AndroidCachePrototypeApplication) getApplicationContext()).updateQueryCache("LFU");
					StatisticsManager.finishedExperiment(); //Clear experiment log writer before starting
					//TODO: Edit logger to have CSV format
					runExperimentation(queriesToProcess, nbQueriesToExecute);
					StatisticsManager.finishedExperiment();
					//<editor-fold desc="LOG STOP EXPERIMENTATION">
					clearCache(); //Remove all entries from cache before we start again
					StatisticsManager.close();


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

	/**
	 * Clear cache (usually between experiments)
	 * @return if cache was cleared
	 */
	private boolean clearCache(){
		DataLoader loader = ((AndroidCachePrototypeApplication)getApplicationContext()).getDataLoader();
		if(loader instanceof DecisionalSemanticCacheDataLoader){
			((DecisionalSemanticCacheDataLoader) loader).getQueryCache().clear();
			return true;
		}
		return false;
	}

	private Query getQuery(String line) {
		
		Query query = null;
		String predicateStr = " ";
		//Set<String> attributes = new HashSet<>();
		Set<Predicate> predicates = new HashSet<Predicate>();
        HashSet<String> queryAttributes = new HashSet<String>(); //Grab attributes (SELECT * )
		for(String attr : line.split("SELECT")[1].split("FROM")[0].split(",")) //For each attribute
		{
			queryAttributes.add(attr);
		}

		String rightFrom = line.split("FROM")[1].trim();

		String table = rightFrom.split(" ")[0];

		query = new Query(table);
		query.addAttributes(queryAttributes);

		if(rightFrom.contains("WHERE") || rightFrom.contains("where"))
		    predicateStr = rightFrom.split("WHERE")[1].trim();

		if(predicateStr.contains(";"))
		    predicateStr = predicateStr.substring(0,predicateStr.length()-1); //fixed bug where the last char was being cut off
		if(predicateStr.contains("ORDER BY") && predicateStr.contains("LIMIT")) //If we have both specifications
		{
			query.addLimit("LIMIT " + predicateStr.split("LIMIT")[1].split(" ")[0]); //Split the query by the word LIMIT and grab the first number following the word
			query.addLimit("ORDER BY " + predicateStr.split("ORDER BY")[1].split(" ")[0]); //The same for ORDER BY

			if(predicateStr.indexOf("LIMIT") < predicateStr.indexOf("ORDER BY")) //If limit comes before order by
				predicateStr = predicateStr.substring(0, predicateStr.indexOf("LIMIT")); //Remove the LIMIT and ORDER BY segments from the query
			else
				predicateStr = predicateStr.substring(0, predicateStr.indexOf("ORDER BY"));
		}


		if(predicateStr.contains("LIMIT"))
		{
			query.addLimit("LIMIT " + predicateStr.split("LIMIT")[1].split(" ")[0]); //Split the query by the word LIMIT and grab the first number following the word
			predicateStr = predicateStr.substring(0, predicateStr.indexOf("LIMIT")); //Remove limit from predicate statement
		}

		else if(predicateStr.contains("ORDER BY"))
		{
			query.addLimit("ORDER BY " + predicateStr.split("ORDER BY")[1].split(" ")[0]); //The same for ORDER BY
			predicateStr = predicateStr.substring(0, predicateStr.indexOf("ORDER BY")); //remove orderby from predicate statement
		}

		if(predicateStr.contains("BETWEEN")) //If we're dealing with a query with one or more 'between' statements
		{
			String[] betweenList = predicateStr.split("BETWEEN");
			for(int i = 0; i<betweenList.length-1; i++) //For each series of statements in the between split
			{
				String[] andList = betweenList[i].split("AND"); //Split each between split by and
				String[] predicateItems;
				for(int j = 0; j<andList.length-1; j++) //For each and split besides the last (our between statement
				{
					predicateItems = andList[j].trim().split(" "); //Split simple predicates into parts
					try { //Ship them onward
						Predicate p = PredicateFactory.createPredicate(predicateItems[0], predicateItems[1], predicateItems[2]);
						predicates.add(p);
					} catch (TrivialPredicateException | InvalidPredicateException e) {
						Log.e("PARSE_QUERY_LINE", "invalid predicate");
						return null;
					}
				}
				//Now for our between statement we have to look ahead to the next split and grab the first two statements
				//So if we had something like C between X and Y then our variables are
				String column = andList[andList.length-1]; //Grab the final column before the between split ("column between x and y") aka C
				String[] nextAndList = betweenList[i+1].split("AND"); //Split the next series of statements after between
				String op1 = nextAndList[0]; //X
				String op2 = nextAndList[1]; //Y
				double result1 = 0;
				double result2 = 0;
				if(op1!=null && !op1.matches("[-+]?\\d*\\.?\\d+")) //If our first op exists and isn't purely a number (i.e., it is an arithmetic statement
				{
					result1 = eval(op1.trim()); //Evaluate number
				}
				else if(op1==null) //Log parsing issues
				{
					Log.e("OPERATION PARSE ERROR", "Operation 1 not parsed when analyzing between prediate");
				}
				if(op2!=null && !op2.matches("[-+]?\\d*\\.?\\d+"))
				{
					result2 = eval(op2.trim()); //Evaluate number
				}
				if(result1>result2) //Figure out which one is larger
				{
					try {
						//Result2 is smaller so it goes first
						predicates.add(PredicateFactory.createPredicate(column, ">=", String.valueOf(result2))); //C >= Y
						predicates.add(PredicateFactory.createPredicate(column, "<=", String.valueOf(result1))); //AND C <= X
					} catch (TrivialPredicateException | InvalidPredicateException e) {
						Log.e("PARSE_QUERY_LINE", "invalid predicate");
						return null;
					}

					}
				else if(result2>result1) //If result 2 is larger
				{
					try {
					predicates.add(PredicateFactory.createPredicate(column, ">=", String.valueOf(result1))); //C >= Y
					predicates.add(PredicateFactory.createPredicate(column, "<=", String.valueOf(result2))); //AND C <= X
					} catch (TrivialPredicateException | InvalidPredicateException e) {
						Log.e("PARSE_QUERY_LINE", "invalid predicate");
						return null;
					}
				}
				else //If they're equal
				{
					try {
						predicates.add(PredicateFactory.createPredicate(column, "==", String.valueOf(result1)));
					} catch (TrivialPredicateException | InvalidPredicateException e) {
						Log.e("PARSE_QUERY_LINE", "invalid predicate");
						return null;
					}
				}

			}
			String[] andSplit = betweenList[betweenList.length-1].split("AND"); //After the last between, catch the rest of the ends
			for(int k = 2; k<andSplit.length; k++) //Start from 2 to ignore the two predicates from the last between
			{
				String[] pred = andSplit[k].split(" "); //Split predicate into X OP Y
				try{
					predicates.add(PredicateFactory.createPredicate(pred[1], pred[2], pred[3])); //Ship predicate
				} catch (TrivialPredicateException | InvalidPredicateException e) {
					Log.e("PARSE_QUERY_LINE", "invalid predicate");
					return null;
				}
			}
		}
		else { //If we're dealing with a simple case (no between)
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
		}

		query.addPredicates(predicates);

		return query;
	}

	public static double eval(final String str) { //For evaluating string arithmetic expressions. From https://stackoverflow.com/questions/3422673/how-to-evaluate-a-math-expression-given-in-string-form
		return new Object() {
			int pos = -1, ch;

			void nextChar() {
				ch = (++pos < str.length()) ? str.charAt(pos) : -1;
			}

			boolean eat(int charToEat) {
				while (ch == ' ') nextChar();
				if (ch == charToEat) {
					nextChar();
					return true;
				}
				return false;
			}

			double parse() {
				nextChar();
				double x = parseExpression();
				if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
				return x;
			}

			// Grammar:
			// expression = term | expression `+` term | expression `-` term
			// term = factor | term `*` factor | term `/` factor
			// factor = `+` factor | `-` factor | `(` expression `)`
			//        | number | functionName factor | factor `^` factor

			double parseExpression() {
				double x = parseTerm();
				for (;;) {
					if      (eat('+')) x += parseTerm(); // addition
					else if (eat('-')) x -= parseTerm(); // subtraction
					else return x;
				}
			}

			double parseTerm() {
				double x = parseFactor();
				for (;;) {
					if      (eat('*')) x *= parseFactor(); // multiplication
					else if (eat('/')) x /= parseFactor(); // division
					else return x;
				}
			}

			double parseFactor() {
				if (eat('+')) return parseFactor(); // unary plus
				if (eat('-')) return -parseFactor(); // unary minus

				double x;
				int startPos = this.pos;
				if (eat('(')) { // parentheses
					x = parseExpression();
					eat(')');
				} else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
					while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
					x = Double.parseDouble(str.substring(startPos, this.pos));
				} else if (ch >= 'a' && ch <= 'z') { // functions
					while (ch >= 'a' && ch <= 'z') nextChar();
					String func = str.substring(startPos, this.pos);
					x = parseFactor();
					if (func.equals("sqrt")) x = Math.sqrt(x);
					else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
					else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
					else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
					else throw new RuntimeException("Unknown function: " + func);
				} else {
					throw new RuntimeException("Unexpected: " + (char)ch);
				}

				if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

				return x;
			}
		}.parse();
	}
}
