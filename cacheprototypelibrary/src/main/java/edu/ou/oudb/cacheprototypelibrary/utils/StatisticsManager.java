package edu.ou.oudb.cacheprototypelibrary.utils;

import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.ou.oudb.cacheprototypelibrary.estimationcache.CloudEstimationComputationManager;
import edu.ou.oudb.cacheprototypelibrary.estimationcache.MobileEstimationComputationManager;

/**
 * Created by Mikaël on 09/02/2015.
 */
public class StatisticsManager {

    private static final String FOLDER_NAME = "/MOCCADCacheStatistics";
    private static final String DATE_FORMAT = "yyyyMMddHHmmss";
    private static final String PREFIX_FILE_NAME = "stats";
    private static final String EXTENSION= ".txt";
    private static final String SPLIT_CHAR = "_";

    private static final String QUERY_CACHE_EXACT_HIT_TAG = "QUERY_CACHE_EXACT_HIT";
    private static final String QUERY_CACHE_EXTENDED_HIT_TAG = "QUERY_CACHE_EXTENDED_HIT";
    private static final String QUERY_CACHE_PARTIAL_HIT_TAG = "QUERY_CACHE_PARTIAL_HIT";
    private static final String QUERY_CACHE_MISS_TAG = "QUERY_CACHE_MISS";
    private static final String MOBILE_ESTIMATION_CACHE_HIT_TAG = "MOBILE_ESTIMATION_CACHE_HIT";
    private static final String CLOUD_ESTIMATION_CACHE_HIT_TAG = "CLOUD_ESTIMATION_CACHE_HIT";
    private static final String MOBILE_ESTIMATION_CACHE_MISS_TAG = "MOBILE_ESTIMATION_CACHE_MISS";
    private static final String CLOUD_ESTIMATION_CACHE_MISS_TAG = "CLOUD_ESTIMATION_CACHE_MISS";
    private static final String QUERY_PROCESS_ON_CLOUD_TAG = "QUERY_PROCESS_ON_CLOUD";
    private static final String QUERY_PROCESS_ON_MOBILE_TAG = "QUERY_PROCESS_ON_MOBILE";
    private static final String POSED_QUERY_TAG = "POSED_QUERY";
    private static final String PROCESSED_QUERY_TAG = "PROCESSED_QUERY";
    private static final String FAILED_QUERY_TAG = "FAILED_QUERY";
    private static final String CLOUD_PROCESS_RESULT_SIZE_TAG = "CLOUD_PROCESS_RESULT_SIZE";
    private static final String QUERY_CACHE_REPLACEMENT_TAG = "QUERY_CACHE_REPLACEMENT";
    private static final String CLOUD_COEFFICIENT_TAG = "CLOUD_COEFFICIENT";

    private static final String START_COMPUTE_CLOUD_ESTIMATION_TAG = "START_COMPUTE_CLOUD_ESTIMATION";
    private static final String STOP_COMPUTE_CLOUD_ESTIMATION_TAG = "STOP_COMPUTE_CLOUD_ESTIMATION";
    private static final String START_MOBILE_PROCESS_TAG = "START_MOBILE_PROCESS";
    private static final String STOP_MOBILE_PROCESS_TAG = "STOP_MOBILE_PROCESS";
    private static final String START_CLOUD_PROCESS_TAG = "START_CLOUD_PROCESS";
    private static final String STOP_CLOUD_PROCESS_TAG = "STOP_CLOUD_PROCESS";
    private static final String START_DOWNLOAD_DATA_TAG = "START_DOWNLOAD_DATA";
    private static final String STOP_DOWNLOAD_DATA_TAG = "STOP_DOWNLOAD_DATA";
    private static final String START_DOWNLOAD_ESTIMATION_RESULT_TAG = "START_DOWNLOAD_ESTIMATION_RESULT";
    private static final String STOP_DOWNLOAD_ESTIMATION_RESULT_TAG = "STOP_DOWNLOAD_ESTIMATION_RESULT";
    private static final String START_QUERY_PROCESS_TAG = "START_QUERY_PROCESS";
    private static final String STOP_QUERY_PROCESS_TAG = "STOP_QUERY_PROCESS";
    private static final String START_CACHE_ANALYSIS_TAG = "START_CACHE_ANALYSIS";
    private static final String STOP_CACHE_ANALYSIS_TAG = "STOP_CACHE_ANALYSIS";
    private static final String START_QUERY_EXECUTION_TAG = "START_QUERY_EXECUTION";
    private static final String STOP_QUERY_EXECUTION_TAG = "STOP_QUERY_EXECUTION";
    private static final String START_DECISION_PROCESS_TAG = "START_DECISION_PROCESS";
    private static final String STOP_DECISION_PROCESS_TAG = "STOP_DECISION_PROCESS";
    private static final String START_CACHE_REPLACEMENT_TAG = "START_CACHE_REPLACEMENT";
    private static final String STOP_CACHE_REPLACEMENT_TAG = "STOP_CACHE_REPLACEMENT";

    private static final String ENERGY_COST_TAG = "COST_ENERGY";
    private static final String MONEY_COST_TAG = "COST_MONEY";

    private static final String DEFAULT_NAME = "APP";

    private static File mRootPath = null;
    private static File mDir = null;
    private static File mLogFile = null;
    private static FileOutputStream mLogFOS = null;
    private static PrintWriter mLogPW = null;

    public static void createFileWriter()
    {
        createFileWriter(DEFAULT_NAME);
    }

    public static void createFileWriter(String name)
    {
        close();
        if(isExternalStorageWritable() && mLogPW == null)
        {
            mRootPath = Environment.getExternalStorageDirectory();
            mDir = new File (mRootPath.getAbsolutePath() + FOLDER_NAME);
            if (!mDir.exists()) {
                mDir.mkdirs();
            }

            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());
            mLogFile = new File(mDir, PREFIX_FILE_NAME + SPLIT_CHAR + name + SPLIT_CHAR + currentDateandTime + EXTENSION);

            try {
                mLogFOS = new FileOutputStream(mLogFile);
                mLogPW = new PrintWriter(mLogFOS);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                close();
                Log.i("createFile", "******* File not found. Did you" +
                        " add a WRITE_EXTERNAL_STORAGE permission to the manifest?");
            } //catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }

    private static void printTimeStamp()
    {
        if(mLogPW != null)
        {
            mLogPW.print(":");
            mLogPW.print(SystemClock.elapsedRealtimeNanos());
        }
    }

    private static void printTag(String tag)
    {
        if(mLogPW != null)
        {
            mLogPW.print(tag);
            printTimeStamp();
            mLogPW.println();
        }
    }

    private static long printStartTask(String tag)
    {
        long time = 0;
        if(mLogPW != null)
        {
            mLogPW.print(tag);
            mLogPW.print(":");
            time = SystemClock.elapsedRealtimeNanos();
            mLogPW.print(time);
            mLogPW.println();
        }
        return time;
    }

    private static void printStopTask(long startingTime, String tag)
    {
        if(mLogPW != null)
        {
            mLogPW.print(tag);
            mLogPW.print(":");
            long time = SystemClock.elapsedRealtimeNanos();
            mLogPW.print(time);
            mLogPW.println();
            mLogPW.print(ENERGY_COST_TAG);
            mLogPW.print(":");
            mLogPW.print(MobileEstimationComputationManager.estimateEnergy(time-startingTime));
            mLogPW.println();
        }
    }

    public static long startComputeCloudEstimation()
    {
        return printStartTask(START_COMPUTE_CLOUD_ESTIMATION_TAG);
    }

    public static void stopComputeCloudEstimation(long startTime, long duration, double returnedMoneyCost)
    {
        if(mLogPW != null)
        {
            mLogPW.print(STOP_COMPUTE_CLOUD_ESTIMATION_TAG);
            mLogPW.print(":");
            mLogPW.print(startTime+duration);
            mLogPW.println();
            mLogPW.print(ENERGY_COST_TAG);
            mLogPW.print(":");
            mLogPW.print(CloudEstimationComputationManager.estimateEnergyLowNetwork(duration));
            mLogPW.println();
            mLogPW.print(MONEY_COST_TAG);
            mLogPW.print(":");
            mLogPW.print(returnedMoneyCost);
            mLogPW.println();
        }
    }

    public static long startCloudProcess()
    {
        return printStartTask(START_CLOUD_PROCESS_TAG);
    }

    public static void stopCloudProcess(long startTime, long duration, double returnedMoneyCost)
    {
        if(mLogPW != null)
        {
            mLogPW.print(STOP_CLOUD_PROCESS_TAG);
            mLogPW.print(":");
            mLogPW.print(startTime+(duration));
            mLogPW.println();
            mLogPW.print(ENERGY_COST_TAG);
            mLogPW.print(":");
            mLogPW.print(CloudEstimationComputationManager.estimateEnergyLowNetwork(duration));
            mLogPW.println();
            mLogPW.print(MONEY_COST_TAG);
            mLogPW.print(":");
            mLogPW.print(returnedMoneyCost);
            mLogPW.println();
        }
    }


    public static long startMobileProcess()
    {
        return printStartTask(START_MOBILE_PROCESS_TAG);
    }

    public static void stopMobileProcess(long startTime)
    {
        if(mLogPW != null)
        {
            mLogPW.print(STOP_MOBILE_PROCESS_TAG);
            mLogPW.print(":");
            long time = SystemClock.elapsedRealtimeNanos();
            mLogPW.print(time);
            mLogPW.println();
            mLogPW.print(ENERGY_COST_TAG);
            mLogPW.print(":");
            mLogPW.print(MobileEstimationComputationManager.estimateEnergy(time-startTime));
            mLogPW.println();
        }
    }

    public static long startDownloadData()
    {
        return printStartTask(START_DOWNLOAD_DATA_TAG);
    }

    public static void stopDownloadData(long startTime)
    {
        if(mLogPW != null)
        {
            mLogPW.print(STOP_DOWNLOAD_DATA_TAG);
            mLogPW.print(":");
            long time = SystemClock.elapsedRealtimeNanos();
            mLogPW.print(time);
            mLogPW.println();
            mLogPW.print(ENERGY_COST_TAG);
            mLogPW.print(":");
            mLogPW.print(CloudEstimationComputationManager.estimateEnergyHighNetwork(time-startTime));
            mLogPW.println();
        }
    }

    public static long startDownloadEstimationResult()
    {
        return printStartTask(START_DOWNLOAD_ESTIMATION_RESULT_TAG);
    }

    public static void stopDownloadEstimationResult(long startTime)
    {
        if(mLogPW != null)
        {
            mLogPW.print(STOP_DOWNLOAD_ESTIMATION_RESULT_TAG);
            mLogPW.print(":");
            long time = SystemClock.elapsedRealtimeNanos();
            mLogPW.print(time);
            mLogPW.println();
            mLogPW.print(ENERGY_COST_TAG);
            mLogPW.print(":");
            mLogPW.print(CloudEstimationComputationManager.estimateEnergyHighNetwork(time-startTime));
            mLogPW.println();
        }
    }

    public static void newQueryCacheExactHit()
    {
        printTag(QUERY_CACHE_EXACT_HIT_TAG);
    }

    public static void newQueryCacheExtendedHit()
    {
        printTag(QUERY_CACHE_EXTENDED_HIT_TAG);
    }

    public static void newQueryCachePartialHit()
    {
        printTag(QUERY_CACHE_PARTIAL_HIT_TAG);
    }

    public static void newQueryCacheMiss()
    {
        printTag(QUERY_CACHE_MISS_TAG);
    }

    public static void newQueryProcessedOnCloud() { printTag(QUERY_PROCESS_ON_CLOUD_TAG); }

    public static void newQueryProcessedOnMobile() { printTag(QUERY_PROCESS_ON_MOBILE_TAG); }

    public static void newQueryCacheReplacement() { printTag(QUERY_CACHE_REPLACEMENT_TAG); }

    public static void newPosedQuery(String query)
    {
        if(mLogPW != null)
        {
            mLogPW.print(POSED_QUERY_TAG);
            printTimeStamp();
            mLogPW.print(':');
            mLogPW.print(query);
            mLogPW.println();
        }
    }

    public static void newProcessedQuery()
    {
        printTag(PROCESSED_QUERY_TAG);
    }

    public static void newFailedQuery()
    {
        printTag(FAILED_QUERY_TAG);
    }

    public static long startCacheAnalysis() {
        return printStartTask(START_CACHE_ANALYSIS_TAG);
    }

    public static void stopCacheAnalysis(long startingTime) {
        printStopTask(startingTime, STOP_CACHE_ANALYSIS_TAG);
    }

    public static long startQueryProcess() {
        return printStartTask(START_QUERY_PROCESS_TAG);
    }

    public static void stopQueryProcess(long startingTime) {
        printStopTask(startingTime,STOP_QUERY_PROCESS_TAG);
    }

    public static long startQueryExecution() {
        return printStartTask(START_QUERY_EXECUTION_TAG);
    }

    public static void stopQueryExecution(long startingTime) {
        printStopTask(startingTime,STOP_QUERY_EXECUTION_TAG);
    }

    public static long startCacheReplacement() {
        return printStartTask(START_CACHE_REPLACEMENT_TAG);
    }

    public static void stopCacheReplacement(long startingTime) {
        printStopTask(startingTime, STOP_CACHE_REPLACEMENT_TAG);
    }


    public static long startDecisionProcess() {
        return printStartTask(START_DECISION_PROCESS_TAG);
    }

    public static void stopDecisionProcess(long startingTime) {
        printStopTask(startingTime,STOP_DECISION_PROCESS_TAG);
    }

    public static void newMobileEstimationCacheHit()
    {
        printTag(MOBILE_ESTIMATION_CACHE_HIT_TAG);
    }

    public static void newCloudEstimationCacheHit()
    {
        printTag(CLOUD_ESTIMATION_CACHE_HIT_TAG);
    }

    public static void newCloudEstimationCacheMiss()
    {
        printTag(CLOUD_ESTIMATION_CACHE_MISS_TAG);
    }

    public static void newResultSize(long resultSize) {
        if(mLogPW != null) {
            mLogPW.print(CLOUD_PROCESS_RESULT_SIZE_TAG);
            mLogPW.print(":");
            mLogPW.print(resultSize);
            mLogPW.println();
        }
    }

    public static void cloudCoefficient(int coeff)
    {
        if(mLogPW != null) {
            mLogPW.print(CLOUD_COEFFICIENT_TAG);
            mLogPW.print(":");
            mLogPW.print(coeff);
            mLogPW.println();
        }
    }


    public static void newMobileEstimationCacheMiss()
    {
        printTag(MOBILE_ESTIMATION_CACHE_MISS_TAG);
    }

    public static void delete()
    {
        StatisticsManager.close();
        mLogFile.delete();
        mLogFile = null;
    }



    public static void close()
    {
        if(mLogPW != null)
        {
            mLogPW.flush();
            mLogPW.close();
            mLogPW = null;
        }

        if(mLogFOS != null) {
            try {
                mLogFOS.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mLogFOS = null;
            }
        }
    }

    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    /*private static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
            Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }*/


}
