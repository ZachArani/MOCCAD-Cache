package edu.ou.oudb.cacheprototypelibrary.core.cachemanagers;

import android.content.Context;
import android.util.Log;

import java.net.ConnectException;
import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ou.oudb.cacheprototypelibrary.StandartEstimationCacheContentManager.EstimationTrimmingResult;
import edu.ou.oudb.cacheprototypelibrary.connection.DataAccessProvider;
import edu.ou.oudb.cacheprototypelibrary.core.cache.Cache;
import edu.ou.oudb.cacheprototypelibrary.core.process.Process;
import edu.ou.oudb.cacheprototypelibrary.estimationcache.CloudEstimationComputationManager;
import edu.ou.oudb.cacheprototypelibrary.estimationcache.Estimation;
import edu.ou.oudb.cacheprototypelibrary.estimationcache.MobileEstimationComputationManager;
import edu.ou.oudb.cacheprototypelibrary.estimationcache.process.CloudEstimationComputationProcess;
import edu.ou.oudb.cacheprototypelibrary.estimationcache.process.EstimationCacheRetrievalProcess;
import edu.ou.oudb.cacheprototypelibrary.estimationcache.process.MobileEstimationComputationProcess;
import edu.ou.oudb.cacheprototypelibrary.estimationcache.trimming.EstimationTrimmingType;
import edu.ou.oudb.cacheprototypelibrary.optimization.OptimizationParameters;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.ConstraintsNotRespectedException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.DownloadDataException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.JSONParserException;
import edu.ou.oudb.cacheprototypelibrary.querycache.process.CloudQueryProcess;
import edu.ou.oudb.cacheprototypelibrary.querycache.process.ExtendedHitInclusionMobileQueryProcess;
import edu.ou.oudb.cacheprototypelibrary.querycache.process.HitMobileQueryProcess;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.QuerySegment;
import edu.ou.oudb.cacheprototypelibrary.querycache.trimming.QueryCacheQueryTrimmer.QueryTrimmingResult;
import edu.ou.oudb.cacheprototypelibrary.querycache.trimming.QueryTrimmingType;
import edu.ou.oudb.cacheprototypelibrary.utils.StatisticsManager;

/**
 * @author Mikaël Perrin
 * @since 1.0
 * Class defining Mikael's algorithm for the management of a query with 2 estimation caches and
 * a query cache
 */
public class DecisionalSemanticCacheDataLoader extends DataLoader<Query,QuerySegment> {

	private Cache<Query,Estimation> mMobileEstimationCache = null;
	private Cache<Query,Estimation> mCloudEstimationCache= null;
	private Cache<Query,QuerySegment> mQueryCache = null;
	private OptimizationParameters mOptimizationParameters = null;
	private CloudEstimationComputationManager mCloudEstimationComputationManager = null;
	private MobileEstimationComputationManager mMobileEstimationComputationManager = null;
	private boolean mUsingReplacement = true;

	// Stats
	public static long totalTimeM;
	public static long totalTimeC;

	// SearchExamRecordActivity
	/*Costs that can be displayed*/
	//FIXME: There should be a more proper way to do that
	public static double resultTimeM;
	public static double resultMoneyM;
	public static double resultEnergyM;

	public static double resultTimeC;
	public static double resultMoneyC;
	public static double resultEnergyC;

	/*Mostly used to see if all is correct*/
	public static String cacheHitType;
	public static String executedOn;

	// Mobile costs
	private long costTimeM;
	private double costMoneyM;
	private double costEnergyM;

	// Cloud costs
	private long costTimeC;
	private double costMoneyC;
	private double costEnergyC;

	// Scores
	private double scoreM = 0;
	private double scoreC = 0;
	private double scoreF = 0; //Whatever we decide to execute on (Mobile or Cloud)

	public DecisionalSemanticCacheDataLoader(Context context, 
											DataAccessProvider dataAccessProvider, 
											Cache<Query,Estimation> mobileEstimationCache, 
											Cache<Query,Estimation> cloudEstimationCache,
											Cache<Query,QuerySegment> queryCache,
											OptimizationParameters optimizationParameters,
											boolean useReplacement)
	{
		super(context,dataAccessProvider);
		if(mobileEstimationCache != null 
				&& cloudEstimationCache != null 
				&& queryCache != null
				&& optimizationParameters != null)
		{
			this.mMobileEstimationCache = mobileEstimationCache;
			this.mCloudEstimationCache = cloudEstimationCache;
			this.mQueryCache = queryCache;
			this.setOptimizationParameters(optimizationParameters);
			this.mCloudEstimationComputationManager = new CloudEstimationComputationManager(context, mDataAccessProvider);
			this.mMobileEstimationComputationManager = new MobileEstimationComputationManager(mQueryCache.getCacheContentManager());
			mUsingReplacement = useReplacement;
		}
		else
		{
			throw new InvalidParameterException();
		}
	}

	@Override
	public List<List<String>> load(Query query) throws ConstraintsNotRespectedException, ConnectException, DownloadDataException, JSONParserException {

		Log.d("LOAD","A NEW QUERY HAS BEEN LAUNCHED !");

        costTimeM = 0;
        costMoneyM = 0;
        costEnergyM = 0;

		resultTimeM = 0;
		resultMoneyM = 0;
		resultEnergyM = 0;

        costTimeC = 0;
        costMoneyC = 0;
        costEnergyC =0;

		resultTimeC = 0;
		resultMoneyC = 0;
		resultEnergyC = 0;

        //<editor-fold desc="LOG newPosedQuery">
        StatisticsManager.newPosedQuery(query.toSQLString());
        //</editor-fold>
		
		QuerySegment queryResult = null;
		QueryTrimmingResult queryTrimmingResult = null;
		Estimation probeQueryMobileEstimation = new Estimation();
		probeQueryMobileEstimation.initToInfinity();
		Estimation probeQueryCloudEstimation = new Estimation();
		probeQueryCloudEstimation.initToInfinity();
		Estimation remainderQueryCloudEstimation = new Estimation();
		remainderQueryCloudEstimation.initToInfinity();

        //<editor-fold desc="LOG START QUERY PROCESS">
        long startQueryProcessTime = StatisticsManager.startQueryProcess();
        //</editor-fold>
        //<editor-fold desc="LOG START CACHE ANALYSIS">
        long startCacheAnalysisTime = StatisticsManager.startCacheAnalysis();
        //</editor-fold>

		// look up into cache to see what type of cache hit / miss we can do.
		queryTrimmingResult = (QueryTrimmingResult) mQueryCache.lookup(query);

        //<editor-fold desc="LOG START DECISION PROCESS">
        long startTimeForDecision = StatisticsManager.startDecisionProcess();
        //</editor-fold>

		// we compute the estimations
		estimationComputation(queryTrimmingResult,
								probeQueryMobileEstimation,
								probeQueryCloudEstimation,
								remainderQueryCloudEstimation);

		// TOTAL FOR THE STATS
		totalTimeM = totalTimeM + costTimeM;
		totalTimeC = totalTimeC + costTimeC;

		// Norm Values (User-defined maximum)
		double normMoney = mOptimizationParameters.getMoneyConstraint();
		double normTime = mOptimizationParameters.getTimeConstraint();
		double normEnergy = mOptimizationParameters.getEnergyConstraint();

		// Score for the mobile
		scoreM = getScore(costMoneyM, costTimeM, costEnergyM, normMoney, normTime, normEnergy);

		//Score for the cloud
		scoreC = getScore(costMoneyC, costTimeC, costEnergyC, normMoney, normTime, normEnergy);

		// Displaying the scores
		Log.d("mobileScore", Double.toString(scoreM));

		Log.d("cloudScore", Double.toString(scoreC));

        try {
            // decision making and build query plan
            buildQueryPlan(query,
                    queryTrimmingResult,
                    probeQueryMobileEstimation,
                    probeQueryCloudEstimation,
                    remainderQueryCloudEstimation);
        } catch (ConstraintsNotRespectedException e) {
            //<editor-fold desc="LOG STOP DECISION PROCESS">
            StatisticsManager.stopDecisionProcess(startTimeForDecision);
            //</editor-fold>
            //<editor-fold desc="LOG STOP CACHE ANALYSIS">
            StatisticsManager.stopCacheAnalysis(startCacheAnalysisTime);
            //</editor-fold>
            //<editor-fold desc="LOG STOP QUERY PROCESS">
            StatisticsManager.stopQueryProcess(startQueryProcessTime);
            //</editor-fold>
            throw e;
        }

        //<editor-fold desc="LOG STOP DECISION PROCESS">
        StatisticsManager.stopDecisionProcess(startTimeForDecision);
        //</editor-fold>
        //<editor-fold desc="LOG STOP CACHE ANALYSIS">
        StatisticsManager.stopCacheAnalysis(startCacheAnalysisTime);
        //</editor-fold>


        //<editor-fold desc="LOG START QUERY EXECUTION">
        long startQueryExecutionTime = StatisticsManager.startQueryExecution();
        //</editor-fold>
	
		//PROCESS THE QUERY PLAN
        //queryResult = new QuerySegment();
		queryResult = mQueryCache.process();

        //<editor-fold desc="LOG STOP QUERY EXECUTION">
        StatisticsManager.stopQueryExecution(startQueryExecutionTime);
        //</editor-fold>

        //<editor-fold desc="LOG START CACHE REPLACEMENT">
        long startCacheReplacementTime = StatisticsManager.startCacheReplacement();
        //</editor-fold>
		
		// REPLACEMENT IN QUERY CACHE
        if(mUsingReplacement && !mQueryCache.containsKey(query))
        {
            if (mQueryCache.canContainSegment(query, queryResult))
            {
                Set<Query> queriesToBeRemoved = new HashSet<Query>();
                //if an entry in cache contains the result of the probe query
                // we replace that entry with the most recent one
                if (queryTrimmingResult.entryQuery != null
                        &&
						(queryTrimmingResult.type == QueryTrimmingType.CACHE_HORIZONTAL
								|| queryTrimmingResult.type == QueryTrimmingType.CACHE_VERTICAL
								|| queryTrimmingResult.type == QueryTrimmingType.CACHE_HYBRID))
                {
                    queriesToBeRemoved.add(queryTrimmingResult.entryQuery);
                }

                if (queryTrimmingResult.entryQuery == null
                        ||	(queryTrimmingResult.entryQuery != null
                        && queryTrimmingResult.type != QueryTrimmingType.CACHE_EXTENDED_HIT_INCLUDED
                        && queryTrimmingResult.type != QueryTrimmingType.CACHE_EXTENDED_HIT_EQUIVALENT))
                {
                    while(mQueryCache.isCountFull())
                    {
                        queriesToBeRemoved.add(mQueryCache.replace());
                    }

                    mQueryCache.removeAll(queriesToBeRemoved);

                    while(mQueryCache.wouldBeOverflowedBy(query, queryResult))
                    {
                        mQueryCache.remove(mQueryCache.replace());
                    }

                    // INSERTION

                    mQueryCache.add(query, queryResult);
                    //<editor-fold desc="LOG NEW QUERY CACHE REPLACEMENT">
                    StatisticsManager.newQueryCacheReplacement();
                    //</editor-fold>
                }
            }
            //else we do not insert in cache.
        }
		
		/* at this point the query has been processed. */

        //<editor-fold desc="LOG STOP CACHE REPLACEMENT">
        StatisticsManager.stopCacheReplacement(startCacheReplacementTime);
        //</editor-fold>
        //<editor-fold desc="LOG STOP QUERY PROCESS">
        StatisticsManager.stopQueryProcess(startQueryProcessTime);
        //</editor-fold>

        //<editor-fold desc="LOG newProcessedQuery">
        StatisticsManager.newProcessedQuery();
        //</editor-fold>

		return queryResult.getTuples();
	}
	

	/**
	 * Method building the query plan and returning exception if processing does not respect the constraints
	 * @param query the inputQuery
	 * @param queryTrimmingResult the trimming result
	 * @param probeQueryMobileEstimation the estimation to process the probe query on the mobile device
	 * @param probeQueryCloudEstimation the estimation to process the probe query on the cloud
	 * @param remainderQueryCloudEstimation the estimation to process the remainder query on the cloud
	 * @throws java.net.ConnectException thrown if connection error
	 * @throws DownloadDataException thrown if error while downloading result
	 * @throws JSONParserException thrown if error while parsing result
	 * @throws ConstraintsNotRespectedException thrown if constraints are not respected
	 */
	private void buildQueryPlan(Query query,
									QueryTrimmingResult queryTrimmingResult, 
									Estimation probeQueryMobileEstimation,
									Estimation probeQueryCloudEstimation,
									Estimation remainderQueryCloudEstimation) 
									throws ConnectException, 
										DownloadDataException, 
										JSONParserException, 
										ConstraintsNotRespectedException
	{
		switch(queryTrimmingResult.type)
		{
		case CACHE_HIT:
			// we do not process any estimation because we consider the processing 
			// of the query on the mobile device much more advantageous.
			resultTimeM = costTimeM;
			resultMoneyM = costMoneyM;
			resultEnergyM = costEnergyM;
			cacheHitType = "Cache Hit";
			executedOn = "Mobile";
			scoreF = scoreM;
			mQueryCache.addProcess(query, new HitMobileQueryProcess(mQueryCache));
						
			break;
		case CACHE_EXTENDED_HIT_EQUIVALENT:
			if (queryTrimmingResult.entryQuery != null)
			{
				resultTimeM = costTimeM;
				resultMoneyM = costMoneyM;
				resultEnergyM = costEnergyM;
				cacheHitType = "Cache Extended Hit Equivalent";
				executedOn = "Mobile";
				scoreF = scoreM;
				mQueryCache.addProcess(queryTrimmingResult.entryQuery, new HitMobileQueryProcess(mQueryCache));
			}
			else
			{
				throw new IllegalArgumentException("CACHE_EXTENDED_HIT_EQUIVALENT: no entry query");
			}
			break;
		case CACHE_EXTENDED_HIT_INCLUDED:
			if(probeQueryMobileEstimation.isBetterThan(scoreM, scoreC))
			{
				resultTimeM = costTimeM;
				resultMoneyM = costMoneyM;
				resultEnergyM = costEnergyM;
				cacheHitType = "Cache Extended Hit Included";
				executedOn = "Mobile";
				scoreF = scoreM;
				//if it respects the constraints
				if(probeQueryMobileEstimation.respectsConstraints(mOptimizationParameters))
				{
					// we add the process to retrieve the result from the mobile device
					if (queryTrimmingResult.probeQuery != null && queryTrimmingResult.entryQuery != null)
					{
						mQueryCache.addProcess(queryTrimmingResult.probeQuery, new ExtendedHitInclusionMobileQueryProcess(queryTrimmingResult.entryQuery,mQueryCache));
					}
					else
					{
						throw new IllegalArgumentException("CACHE_EXTENDED_HIT_INCLUDED: no probe query or entry query");
					}
				}
				else
				{
                    //<editor-fold desc="LOG newFailedQuery">
                    StatisticsManager.newFailedQuery();
                    //</editor-fold>

					throw new ConstraintsNotRespectedException(probeQueryMobileEstimation);
				}
			}
			else // the cloud is better
			{
				resultTimeC = costTimeC;
				resultMoneyC = costMoneyC;
				resultEnergyC = costEnergyC;
				cacheHitType = "Cache Extended Hit Included";
				executedOn = "Cloud";
				scoreF = scoreC;
				//if it respects the constraints
				if(probeQueryCloudEstimation.respectsConstraints(mOptimizationParameters))
				{
					if (queryTrimmingResult.probeQuery != null)
					{
						mQueryCache.addProcess(queryTrimmingResult.probeQuery, new CloudQueryProcess(mDataAccessProvider));
					}
					else
					{
						throw new IllegalArgumentException("CACHE_EXTENDED_HIT_INCLUDED: no probe query");
					}
				}
				else
				{
                    //<editor-fold desc="LOG newFailedQuery">
                    StatisticsManager.newFailedQuery();
                    //</editor-fold>
					throw new ConstraintsNotRespectedException(probeQueryCloudEstimation);
				}
			}
			break;
		
			case CACHE_HORIZONTAL:
			case CACHE_VERTICAL:
			case CACHE_HYBRID:
			
			boolean probeQueryMobileEstimationIsBetter = probeQueryMobileEstimation.isBetterThan(scoreM, scoreC);
			Estimation totalEstimation;
			
			if (probeQueryMobileEstimationIsBetter)
			{
				resultTimeM = costTimeM;
				resultMoneyM = costMoneyM;
				resultEnergyM = costEnergyM;
				cacheHitType = "Cache Partial Hit";
				if(queryTrimmingResult.type==QueryTrimmingType.CACHE_HORIZONTAL) {
					cacheHitType = cacheHitType + ": Horizontal";
				} else if(queryTrimmingResult.type==QueryTrimmingType.CACHE_VERTICAL) {
					cacheHitType = cacheHitType + ": Vertical";
				} else {
					cacheHitType = cacheHitType + ": Hybrid";
				}

				executedOn = "Mobile";
				scoreF = scoreM;
				totalEstimation = Estimation.add(probeQueryMobileEstimation, remainderQueryCloudEstimation);
				//if it respect the constraints
				if (totalEstimation.respectsConstraints(mOptimizationParameters))
				{
					boolean resultExists = queryTrimmingResult.probeQuery != null && queryTrimmingResult.entryQuery != null && queryTrimmingResult.remainderQuery != null;
					if(queryTrimmingResult.type==QueryTrimmingType.CACHE_HYBRID) { resultExists = resultExists && queryTrimmingResult.remainderQuery2!=null; }
					// we add the process to retrieve the result from the mobile device
					if (resultExists)
					{
						mQueryCache.addProcess(queryTrimmingResult.probeQuery, new ExtendedHitInclusionMobileQueryProcess(queryTrimmingResult.entryQuery,mQueryCache));
						mQueryCache.addProcess(queryTrimmingResult.remainderQuery, new CloudQueryProcess(mDataAccessProvider));
						if(queryTrimmingResult.type==QueryTrimmingType.CACHE_HYBRID) { mQueryCache.addProcess(queryTrimmingResult.remainderQuery2, new CloudQueryProcess(mDataAccessProvider)); }

					}
					else
					{
						throw new IllegalArgumentException("CACHE_PARTIAL_HIT: no probe query, entry query or remainder query");
					}
				}
				else
				{
                    //<editor-fold desc="LOG newFailedQuery">
                    StatisticsManager.newFailedQuery();
                    //</editor-fold>
					throw new ConstraintsNotRespectedException(totalEstimation);
				}
			}
			else
			{
				resultTimeC = costTimeC;
				resultMoneyC = costMoneyC;
				resultEnergyC = costEnergyC;
				cacheHitType = "Cache Partial Hit";
				executedOn = "Cloud";
				scoreF = scoreC;
				//Corresponds the time to process the input query on the cloud
				//(probeQuery and InputQuery are equals in the case of a PARTIAL HIT
				if(probeQueryCloudEstimation.respectsConstraints(mOptimizationParameters))
				{
					if (queryTrimmingResult.probeQuery != null)
					{
						mQueryCache.addProcess(queryTrimmingResult.probeQuery, new CloudQueryProcess(mDataAccessProvider));
					}
					else
					{
						throw new IllegalArgumentException("CACHE_PARTIAL_HIT: no probe query");
					}
				}
				else
				{
                    //<editor-fold desc="LOG newFailedQuery">
                    StatisticsManager.newFailedQuery();
                    //</editor-fold>
					throw new ConstraintsNotRespectedException(probeQueryCloudEstimation);
				}
				
			}
			
			break;
		case CACHE_MISS:
			if(remainderQueryCloudEstimation.respectsConstraints(mOptimizationParameters))
			{
				resultTimeC = costTimeC;
				resultMoneyC = costMoneyC;
				resultEnergyC = costEnergyC;
				cacheHitType = "Cache Miss";
				executedOn = "Cloud";
				scoreF = scoreC;
				mQueryCache.addProcess(query, new CloudQueryProcess(mDataAccessProvider));
			}
			else
			{
				throw new ConstraintsNotRespectedException(remainderQueryCloudEstimation);
			}
			break;
		}
		
	}
	
	
	
	
	/**
	 * Method used to compute estimation
	 * @param queryTrimmingResult the result of the query trimming
	 * @param outProbeQueryMobileEstimation the estimation to process the probe query on the mobile device
	 * @param outProbeQueryCloudEstimation the estimation to process the probe query on the cloud
	 * @param outRemainderQueryCloudEstimation the estimation to process the remainder query on the cloud
	 * @throws java.net.ConnectException thrown if no connection
	 * @throws DownloadDataException thrown if error while downloading result
	 * @throws JSONParserException thrown if error while parsing result
	 */
	private void estimationComputation(QueryTrimmingResult queryTrimmingResult,
										Estimation outProbeQueryMobileEstimation,
										Estimation outProbeQueryCloudEstimation,
										Estimation outRemainderQueryCloudEstimation) 
												throws ConnectException, DownloadDataException, JSONParserException
	{
		
		switch(queryTrimmingResult.type)
		{
		/*We don't have anything here because it's done on mobile and assumed to be done fast (~ 0)*/
		case CACHE_HIT:
			System.out.println("CACHE_HIT");
            //<editor-fold desc="LOG newQueryCacheExactHit">
            StatisticsManager.newQueryCacheExactHit();
            //</editor-fold>
			break;
		case CACHE_EXTENDED_HIT_EQUIVALENT:
			System.out.println("CACHE_EXTENDED_HIT_EQUIVALENT");
            //<editor-fold desc="LOG newQueryCacheExtendedHit">
            StatisticsManager.newQueryCacheExtendedHit();
            //</editor-fold>
			break;
		case CACHE_EXTENDED_HIT_INCLUDED:
			System.out.println("CACHE_EXTENDED_HIT_INCLUDED");
            //<editor-fold desc="LOG newQueryCacheExtendedHit">
            StatisticsManager.newQueryCacheExtendedHit();
            //</editor-fold>
			// we get the estimation to process the query on the mobile device
			outProbeQueryMobileEstimation.init(getMobileEstimation(
                            mMobileEstimationCache,
                            queryTrimmingResult.probeQuery, //input query
                            queryTrimmingResult.entryQuery,//necessary for the number of tuples in the cache entry
                            new EstimationCacheRetrievalProcess(mMobileEstimationCache),
                            new MobileEstimationComputationProcess(mMobileEstimationComputationManager))
				);

			//MOBILE COSTS
			costTimeM = outProbeQueryMobileEstimation.getDuration();
			costEnergyM = outProbeQueryMobileEstimation.getEnergy();

			// we get the estimation to process the same query on the cloud
			outProbeQueryCloudEstimation.init(getCloudEstimation(
                            mCloudEstimationCache,
                            queryTrimmingResult.probeQuery,  // inputQuery
                            new EstimationCacheRetrievalProcess(mCloudEstimationCache),
                            new CloudEstimationComputationProcess(mCloudEstimationComputationManager))
				);

			//CLOUD COSTS
			costTimeC = outProbeQueryCloudEstimation.getDuration();
			costMoneyC = outProbeQueryCloudEstimation.getMonetaryCost();
			costEnergyC = outProbeQueryCloudEstimation.getEnergy();
			break;
		case CACHE_HORIZONTAL:
		case CACHE_VERTICAL:
		case CACHE_HYBRID:
			System.out.print("CACHE_PARTIAL_HIT ");
			if(queryTrimmingResult.type==QueryTrimmingType.CACHE_HORIZONTAL) {
				System.out.println("HORIZONTAL");
			} else if(queryTrimmingResult.type==QueryTrimmingType.CACHE_VERTICAL) {
				System.out.println("VERTICAL");
			} else {
				System.out.println("HYBRID");
			}
            //<editor-fold desc="LOG newQueryCachePartialHit">
            StatisticsManager.newQueryCachePartialHit();
            //</editor-fold>
			
			// getCloudEstimation to process remainder query on the cloud
			outRemainderQueryCloudEstimation.init(getCloudEstimation(
                            mCloudEstimationCache,
                            queryTrimmingResult.remainderQuery,
                            new EstimationCacheRetrievalProcess(mCloudEstimationCache),
                            new CloudEstimationComputationProcess(mCloudEstimationComputationManager))
            );
			if(queryTrimmingResult.type==QueryTrimmingType.CACHE_HYBRID) {
				Estimation temp = new Estimation();
				temp.init(getCloudEstimation(
						mCloudEstimationCache,
						queryTrimmingResult.remainderQuery2,
						new EstimationCacheRetrievalProcess(mCloudEstimationCache),
						new CloudEstimationComputationProcess(mCloudEstimationComputationManager))
				);
				outRemainderQueryCloudEstimation.add(outRemainderQueryCloudEstimation,temp);
			}
			
			// getMobileEstimation to process probe query on the mobile device
			outProbeQueryMobileEstimation.init(getMobileEstimation(
                            mMobileEstimationCache,
                            queryTrimmingResult.probeQuery,
                            queryTrimmingResult.entryQuery, //necessary for the number of tuples in the cache entry
                            new EstimationCacheRetrievalProcess(mMobileEstimationCache),
                            new MobileEstimationComputationProcess(mMobileEstimationComputationManager))
				);

			//MOBILE COSTS
			costTimeM = outRemainderQueryCloudEstimation.getDuration()+outProbeQueryMobileEstimation.getDuration();
			costMoneyM = outRemainderQueryCloudEstimation.getMonetaryCost();
			costEnergyM = outRemainderQueryCloudEstimation.getEnergy()+outProbeQueryMobileEstimation.getEnergy();
			
			// we get the estimation to process the same query on the cloud
			outProbeQueryCloudEstimation.init(getCloudEstimation(
                            mCloudEstimationCache,
                            queryTrimmingResult.probeQuery, // NOT always input query
                            new EstimationCacheRetrievalProcess(mCloudEstimationCache),
                            new CloudEstimationComputationProcess(mCloudEstimationComputationManager))
				);

			//CLOUD COSTS
			costTimeC = outRemainderQueryCloudEstimation.getDuration()+outProbeQueryCloudEstimation.getDuration();
			costMoneyC = outRemainderQueryCloudEstimation.getMonetaryCost()+outProbeQueryCloudEstimation.getMonetaryCost();
			costEnergyC = outRemainderQueryCloudEstimation.getEnergy()+outProbeQueryCloudEstimation.getEnergy();
			break;
		case CACHE_MISS:
			System.out.println("CACHE_MISS");
            //<editor-fold desc="LOG newQueryCacheMiss">
            StatisticsManager.newQueryCacheMiss();
            //</editor-fold>
			// getCloudEstimation to process remainder query on the cloud
			outRemainderQueryCloudEstimation.init(getCloudEstimation(
                            mCloudEstimationCache,
                            queryTrimmingResult.remainderQuery,
                            new EstimationCacheRetrievalProcess(mCloudEstimationCache),
                            new CloudEstimationComputationProcess(mCloudEstimationComputationManager))
				);

			//CLOUD COSTS
			costTimeC = outRemainderQueryCloudEstimation.getDuration();
			costMoneyC = outRemainderQueryCloudEstimation.getMonetaryCost();
			costEnergyC = outRemainderQueryCloudEstimation.getEnergy();
			break;
		}
	}


    private Estimation getMobileEstimation(Cache<Query, Estimation> cache, Query probeQuery, Query entryQuery, Process<Query, Estimation> cacheRetrievalProcess, Process<Query, Estimation> computationProcess) throws ConnectException, DownloadDataException, JSONParserException
    {
        EstimationTrimmingResult estimationTypeResult = null;
        Estimation resultEstimation = null;


        estimationTypeResult = (EstimationTrimmingResult) cache.lookup(probeQuery);
        if (estimationTypeResult.type == EstimationTrimmingType.CACHE_HIT)
        {
            cache.addProcess(probeQuery, cacheRetrievalProcess);
            resultEstimation = cache.process();

            //<editor-fold desc="LOG newMobileEstimationCacheHit">
            StatisticsManager.newMobileEstimationCacheHit();
            //</editor-fold>
        }
        else // cache miss
        {
            // the entry Query is needed by the computation process to know how many tuples it has to analyze
            cache.addProcess(entryQuery, computationProcess);
            // estimation is computed
            resultEstimation = cache.process();
            // the estimation is replaced for the probe query
            replace(cache, probeQuery, resultEstimation);

            //<editor-fold desc="LOG newMobileEstimationCacheMiss">
            StatisticsManager.newMobileEstimationCacheMiss();
            //</editor-fold>
        }



        return resultEstimation;
    }
	
	private Estimation getCloudEstimation(Cache<Query, Estimation> cache, Query remainderQuery, Process<Query, Estimation> cacheRetrievalProcess, Process<Query, Estimation> computationProcess) throws ConnectException, DownloadDataException, JSONParserException
	{
		EstimationTrimmingResult estimationTypeResult = null;
		Estimation resultEstimation = null;
		
		
		estimationTypeResult = (EstimationTrimmingResult) cache.lookup(remainderQuery);
		if (estimationTypeResult.type == EstimationTrimmingType.CACHE_HIT)
		{
			cache.addProcess(remainderQuery, cacheRetrievalProcess);
			resultEstimation = cache.process();

            //<editor-fold desc="LOG newCloudEstimationCacheHit">
            StatisticsManager.newCloudEstimationCacheHit();
            //</editor-fold>
		}
		else // cache miss
		{
			cache.addProcess(remainderQuery, computationProcess);
			resultEstimation = cache.process();
			replace(cache, remainderQuery, resultEstimation);

            //<editor-fold desc="LOG newCloudEstimationCacheMiss">
            StatisticsManager.newCloudEstimationCacheMiss();
            //</editor-fold>
		}


		
		return resultEstimation;
	}

    /**
     * Method used to add results inside the cache
     * @param cache the estimation cache in which an estimation entry needs to be added
     * @param queryToBeAdded the query as the key
     * @param estimationToBeAdded the estimation as the value
     */
	private void replace(Cache<Query,Estimation> cache, Query queryToBeAdded, Estimation estimationToBeAdded)
	{
		if(!cache.containsKey(queryToBeAdded))
		{
			while(cache.isCountFull())
			{
				cache.remove(cache.replace());
			}
			
			if (cache.canContainSegment(queryToBeAdded, estimationToBeAdded))
			{
				while(cache.wouldBeOverflowedBy(queryToBeAdded, estimationToBeAdded))
				{
					cache.remove(cache.replace());
				}
				
				// INSERTION
				cache.add(queryToBeAdded, estimationToBeAdded);
			}
			//else we do not insert since the cache is not big enough.
		}
	}
	
	/**
	 * @return the mobileEstimationCache
	 */
	public final Cache<Query, Estimation> getMobileEstimationCache() {
		return this.mMobileEstimationCache;
	}


	/**
	 * @param mobileEstimationCache the mobileEstimationCache to set
	 */
	public final void setMobileEstimationCache(
			Cache<Query, Estimation> mobileEstimationCache) {
		if (mobileEstimationCache != null) {
			this.mMobileEstimationCache = mobileEstimationCache;
		}
	}


	/**
	 * @return the cloudEstimationCache
	 */
	public final Cache<Query, Estimation> getCloudEstimationCache() {
		return this.mCloudEstimationCache;
	}


	/**
	 * @param cloudEstimationCache the cloudEstimationCache to set
	 */
	public final void setCloudEstimationCache(
			Cache<Query, Estimation> cloudEstimationCache) {
		if (cloudEstimationCache != null) {
			this.mCloudEstimationCache = cloudEstimationCache;
		}
	}


	/**
	 * @return the queryCache
	 */
	public final Cache<Query, QuerySegment> getQueryCache() {
		return this.mQueryCache;
	}


	/**
	 * @param queryCache the queryCache to set
	 */
	public final void setQueryCache(Cache<Query, QuerySegment> queryCache) {
		if (queryCache != null) {
			this.mQueryCache = queryCache;
		}
	}

	/**
	 * @return the optimizationParameters
	 */
	public OptimizationParameters getOptimizationParameters() {
		return this.mOptimizationParameters;
	}

	/**
	 * @param optimizationParameters the optimizationParameters to set
	 */
	public void setOptimizationParameters(OptimizationParameters optimizationParameters) {
		if (optimizationParameters != null)
		{
			this.mOptimizationParameters = optimizationParameters;
		}
	}
	
	@Override
	public void setDataAccessProvider(DataAccessProvider dataAccessProvider) {
		mCloudEstimationComputationManager.setDataAccessProvider(dataAccessProvider);
		super.setDataAccessProvider(dataAccessProvider);
	}
	
	/**
	 * @return the useReplacement
	 */
	public boolean isUsingReplacement() {
		return this.mUsingReplacement;
	}

	/**
	 * @param useReplacement the useReplacement to set
	 */
	public void setUsingReplacement(boolean useReplacement) {
		this.mUsingReplacement = useReplacement;
	}

	//SCORE FUNCTION
	private double getScore(double cM, double cT, double cE, double nM, double nT, double nE){ // c = cost, n = norm
		double res;

        /*We want the weights from 0 to 1 (double) but we currently have them from 0 to 100 (int) due to the SeekBar system
        * Score = (Weight * Cost / Norm Value)*/
		double moneyScore = (mOptimizationParameters.getMoney()*0.01) * cM / nM;
		double timeScore = (mOptimizationParameters.getTime()*0.01) * cT / nT;
		double energyScore = (mOptimizationParameters.getEnergy()*0.01) * cE / nE;

		// Needed because division by 0 is not good
		if(nM==0){
			moneyScore = 0;
		}
		if(nT==0){
			timeScore = 0;
		}
		if(nE==0){
			energyScore = 0;
		}

		res = moneyScore + timeScore + energyScore;

		return res;
	}

    /**
     * @return QEP score for query
     */
    public double getScore()
    {
        return scoreF;
    }
}
