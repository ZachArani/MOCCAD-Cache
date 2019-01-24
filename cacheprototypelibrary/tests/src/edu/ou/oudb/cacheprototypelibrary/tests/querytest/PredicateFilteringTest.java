package edu.ou.oudb.cacheprototypelibrary.tests.querytest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;
import edu.ou.oudb.cacheprototypelibrary.connection.DataAccessProvider;
import edu.ou.oudb.cacheprototypelibrary.estimationcache.Estimation;
import edu.ou.oudb.cacheprototypelibrary.metadata.Metadata;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.InvalidPredicateException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.TrivialPredicateException;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Predicate;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.PredicateFactory;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.QuerySegment;

public class PredicateFilteringTest extends TestCase {

	QuerySegment segment = null;
	ArrayList<String> testTuple = null;
	
	
	@Override
	protected void setUp() throws Exception {
        Metadata.init(new StubConnectionWrapper());
		testTuple = new ArrayList<String>(Arrays.asList(new String[]{"1","bernard","22","180"}));
		List<List<String>> tuples = new ArrayList<List<String>>();
		tuples.add(testTuple);
		tuples.add(new ArrayList<String>(Arrays.asList(new String[]{"2","gerard","35","190"})));
		tuples.add(new ArrayList<String>(Arrays.asList(new String[]{"3","bob","42","200"})));
		tuples.add(new ArrayList<String>(Arrays.asList(new String[]{"4","alice","52","185"})));
		segment = new QuerySegment(tuples);
		super.setUp();
	}
	
	public void testXopCPredicateApply()
	{
		try {
			Predicate p = PredicateFactory.createPredicate("age", "<", "30");
			assertTrue(p.apply("TESTPREDICATE", testTuple));
		} catch (TrivialPredicateException | InvalidPredicateException e) {
			fail("invalid preddicate");
			e.printStackTrace();
		}
	}
	
	public void testXopYPredicateApply()
	{
		try {
			Predicate p = PredicateFactory.createPredicate("age", "<", "count");
			assertTrue(p.apply("TESTPREDICATE", testTuple));
		} catch (TrivialPredicateException | InvalidPredicateException e) {
			fail("invalid predicate");
			e.printStackTrace();
		}
	}
	
	public void testQuerySegmentFilter()
	{
		Query query = new Query("TESTPREDICATE");
		
		try {
			query.addPredicate(PredicateFactory.createPredicate("count", ">=", "185"));
			query.addExcludedPredicate(PredicateFactory.createPredicate("age", ">", "40"));
			
			List<List<String>> expectedTuples = new ArrayList<List<String>>();
			expectedTuples.add(new ArrayList<String>(Arrays.asList(new String[]{"2","gerard","35","190"})));
			QuerySegment expectedSegment = new QuerySegment(expectedTuples);
			QuerySegment actualSegment = segment.filter(query);
			assertEquals(expectedSegment, actualSegment);
		} catch (TrivialPredicateException | InvalidPredicateException e) {
			fail();
		}
	}
	
	class StubConnectionWrapper implements DataAccessProvider
	{

		@Override
		public ArrayList<String> getAttributeTypes(String relation) {
			ArrayList<String> types = new ArrayList<String>();
			Collections.addAll(types, "INTEGER","VARCHAR2(16)","INT","INT");
			return types;
		}

		@Override
		public ArrayList<String> getAttributeNames(String relation) {
			ArrayList<String> names = new ArrayList<String>();
			Collections.addAll(names, "id","name","age","count");
			return names;
		}

		@Override
		public long getNbTuples(String relation) {
			return 18;
		}

        @Override
        public double getAvgTupleSize(String relationName) { return 25; }

        @Override
		public int getMaxTupleSize(String relation) {
			return 28; //4+16+4+4
		}

		@Override
		public QuerySegment process(Query q) {
			// do nothing
			return new QuerySegment();
		}

		@Override
		public Set<String> getRelationNames() {
			Set<String> relations = new HashSet<String>();
			relations.add("TESTPREDICATE");
			return relations;
		}

		@Override
		public int getNbAttributes(String relation) {
			return 4; 
		}

		@Override
		public Estimation estimate(Query query) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public HashMap<String, Double> getMinValueForAttributes(
				String relationName) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public HashMap<String, Double> getMaxValueForAttributes(
				String relationName) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public HashMap<String, Long> getNbDifferentValuesForAttributes(
				String relationName) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
}
