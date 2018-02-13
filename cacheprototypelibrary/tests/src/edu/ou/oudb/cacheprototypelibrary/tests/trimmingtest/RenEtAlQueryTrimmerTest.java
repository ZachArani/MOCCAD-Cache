package edu.ou.oudb.cacheprototypelibrary.tests.trimmingtest;

import junit.framework.TestCase;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.InvalidPredicateException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.TrivialPredicateException;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.PredicateFactory;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;
import edu.ou.oudb.cacheprototypelibrary.querycache.trimming.QueryCacheQueryTrimmer.QueryTrimmingResult;
import edu.ou.oudb.cacheprototypelibrary.querycache.trimming.QueryTrimmingType;
import edu.ou.oudb.cacheprototypelibrary.querycache.trimming.RenEtAlQueryCacheQueryTrimmer;

public class RenEtAlQueryTrimmerTest extends TestCase {

	Query segmentQuery = null;
	RenEtAlQueryCacheQueryTrimmer renEtAlQueryCacheQueryTrimmer = null;
	
	@Override
	protected void setUp() throws Exception {
		//Build Segment
		segmentQuery = new Query("testRelation");
		try {
			segmentQuery.addPredicate(PredicateFactory.createPredicate("count", ">", "1"));
			segmentQuery.addPredicate(PredicateFactory.createPredicate("count", "<", "4"));
		} catch (TrivialPredicateException | InvalidPredicateException e) {
			fail();
		}
		
	
		renEtAlQueryCacheQueryTrimmer = new RenEtAlQueryCacheQueryTrimmer();
		
		super.setUp();
	}
	
	public void testAnalyzeResultCacheExactHit()
	{
		Query inputQuery = new Query("testRelation");
		try {
			inputQuery.addPredicate(PredicateFactory.createPredicate("count", "<", "4"));
			inputQuery.addPredicate(PredicateFactory.createPredicate("count", ">", "1"));
		} catch (TrivialPredicateException | InvalidPredicateException e) {
			fail();
		}
		
		QueryTrimmingResult expectedResult = new QueryTrimmingResult();
		expectedResult.type = QueryTrimmingType.CACHE_HIT;
		expectedResult.probeQuery = inputQuery;
		expectedResult.remainderQuery = null;
		
		QueryTrimmingResult actualResult = renEtAlQueryCacheQueryTrimmer.evaluate(inputQuery, segmentQuery);
		
		assertEquals(expectedResult, actualResult);
	}
	
	public void testAnalyzeResultCacheExtendedEquivalentHit()
	{
		Query inputQuery = new Query("testRelation");
		Query otherSegmentQuery = new Query("testRelation");
		try {
			otherSegmentQuery.addPredicate(PredicateFactory.createPredicate("count", ">", "1"));
			otherSegmentQuery.addPredicate(PredicateFactory.createPredicate("count", "<", "3"));
			inputQuery.addPredicate(PredicateFactory.createPredicate("count", "=", "2"));
		} catch (TrivialPredicateException | InvalidPredicateException e) {
			fail();
		}
		
		QueryTrimmingResult expectedResult = new QueryTrimmingResult();
		expectedResult.type = QueryTrimmingType.CACHE_EXTENDED_HIT_EQUIVALENT;
		expectedResult.entryQuery = otherSegmentQuery;
		expectedResult.probeQuery = inputQuery;
		expectedResult.remainderQuery = null;
		
		QueryTrimmingResult actualResult = renEtAlQueryCacheQueryTrimmer.evaluate(inputQuery, otherSegmentQuery);
		
		assertEquals(expectedResult, actualResult);
	}
	
	public void testAnalyzeResultCacheExtendedIncludedHit2()
	{
		
		Query inputQuery = new Query("NOTE");
		Query otherSegmentQuery = new Query("NOTE");
		try {
			otherSegmentQuery.addPredicate(PredicateFactory.createPredicate("HeartRate", "<=", "80.0"));
			inputQuery.addPredicate(PredicateFactory.createPredicate("HeartRate", "<", "68.0"));
		} catch (TrivialPredicateException | InvalidPredicateException e) {
			fail();
		}
		
		QueryTrimmingResult expectedResult = new QueryTrimmingResult();
		expectedResult.type = QueryTrimmingType.CACHE_EXTENDED_HIT_INCLUDED;
		expectedResult.entryQuery = otherSegmentQuery;
		expectedResult.probeQuery = inputQuery;
		expectedResult.remainderQuery = null;
		
		QueryTrimmingResult actualResult = renEtAlQueryCacheQueryTrimmer.evaluate(inputQuery, otherSegmentQuery);
		
		assertEquals(expectedResult, actualResult);
	}
	
	public void testAnalyzeResultCacheExtendedIncludedHit()
	{
		Query inputQuery = new Query("testRelation");
		try {
			inputQuery.addPredicate(PredicateFactory.createPredicate("count", "=", "2"));
		} catch (TrivialPredicateException | InvalidPredicateException e) {
			fail();
		}
		
		QueryTrimmingResult expectedResult = new QueryTrimmingResult();
		expectedResult.type = QueryTrimmingType.CACHE_EXTENDED_HIT_INCLUDED;
		expectedResult.entryQuery = segmentQuery;
		expectedResult.probeQuery = inputQuery;
		expectedResult.remainderQuery = null;
		
		QueryTrimmingResult actualResult = renEtAlQueryCacheQueryTrimmer.evaluate(inputQuery, segmentQuery);
		
		assertEquals(expectedResult, actualResult);
	}
	
	public void testAnalyzeResultCacheParitalHit()
	{
		Query inputQuery = new Query("testRelation");
		try {
			inputQuery.addPredicate(PredicateFactory.createPredicate("count", ">=", "1"));
		} catch (TrivialPredicateException | InvalidPredicateException e) {
			fail();
		}
		
		QueryTrimmingResult expectedResult = new QueryTrimmingResult();
		expectedResult.type = QueryTrimmingType.CACHE_HORIZONTAL;
		expectedResult.entryQuery = segmentQuery;
		expectedResult.probeQuery = inputQuery;
		expectedResult.remainderQuery = new Query("testRelation");
		try {
			expectedResult.remainderQuery.addPredicate(PredicateFactory.createPredicate("count", ">=", "1"));
			expectedResult.remainderQuery.addExcludedPredicate(PredicateFactory.createPredicate("count", "<", "4"));
			expectedResult.remainderQuery.addExcludedPredicate(PredicateFactory.createPredicate("count", ">", "1"));
		} catch (TrivialPredicateException | InvalidPredicateException e) {
			fail();
		}
				
		QueryTrimmingResult actualResult = renEtAlQueryCacheQueryTrimmer.evaluate(inputQuery, segmentQuery);
		
		
		assertEquals(expectedResult, actualResult);
	}
	
	public void testAnalyzeResultCacheParitalHit2()
	{
		Query inputQuery = new Query("testRelation");
		try {
			inputQuery.addPredicate(PredicateFactory.createPredicate("count", ">=", "2"));
		} catch (TrivialPredicateException | InvalidPredicateException e) {
			fail();
		}
		
		QueryTrimmingResult expectedResult = new QueryTrimmingResult();
		expectedResult.type = QueryTrimmingType.CACHE_HORIZONTAL;
		expectedResult.entryQuery = segmentQuery;
		expectedResult.probeQuery = inputQuery;
		expectedResult.remainderQuery = new Query("testRelation");
		try {
			expectedResult.remainderQuery.addPredicate(PredicateFactory.createPredicate("count", ">=", "2"));
			expectedResult.remainderQuery.addExcludedPredicate(PredicateFactory.createPredicate("count", "<", "4"));
			expectedResult.remainderQuery.addExcludedPredicate(PredicateFactory.createPredicate("count", ">", "1"));
		} catch (TrivialPredicateException | InvalidPredicateException e) {
			fail();
		}
				
		QueryTrimmingResult actualResult = renEtAlQueryCacheQueryTrimmer.evaluate(inputQuery, segmentQuery);
		
		
		assertEquals(expectedResult, actualResult);
	}
	
	public void testAnalyzeResultCacheMiss()
	{
		Query inputQuery = new Query("testRelation");
		try {
			inputQuery.addPredicate(PredicateFactory.createPredicate("count", "=", "5"));
		} catch (TrivialPredicateException | InvalidPredicateException e) {
			fail();
		}
		
		QueryTrimmingResult expectedResult = new QueryTrimmingResult();
		expectedResult.type = QueryTrimmingType.CACHE_MISS;
		expectedResult.probeQuery = null;
		expectedResult.remainderQuery = inputQuery;
		
		QueryTrimmingResult actualResult = renEtAlQueryCacheQueryTrimmer.evaluate(inputQuery, segmentQuery);
		
		assertEquals(expectedResult, actualResult);
	}
	
	public void testAnalyzeResultCacheMissForMissingAttributes()
	{
		Query inputQuery = new Query("testRelation");
		try {
			inputQuery.addPredicate(PredicateFactory.createPredicate("height", "=", "2"));
		} catch (TrivialPredicateException | InvalidPredicateException e) {
			fail();
		}
		
		QueryTrimmingResult expectedResult = new QueryTrimmingResult();
		expectedResult.type = QueryTrimmingType.CACHE_MISS;
		expectedResult.probeQuery = null;
		expectedResult.remainderQuery = inputQuery;
		
		QueryTrimmingResult actualResult = renEtAlQueryCacheQueryTrimmer.evaluate(inputQuery, segmentQuery);
		
		assertEquals(expectedResult, actualResult);
	}
}
