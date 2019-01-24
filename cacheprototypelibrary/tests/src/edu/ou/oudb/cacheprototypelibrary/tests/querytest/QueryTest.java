package edu.ou.oudb.cacheprototypelibrary.tests.querytest;

import junit.framework.TestCase;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.InvalidPredicateException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.TrivialPredicateException;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.PredicateFactory;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;

public class QueryTest extends TestCase{
	
	public void testToSQLString()
	{
		Query query = new Query("testRelation");
		String actualSQLQuery = null;
		String expectedSQLQuery = null;
		
		try {
			query.addPredicate(PredicateFactory.createPredicate("count", ">=", "1.5"));
			query.addExcludedPredicate(PredicateFactory.createPredicate("count", "<", "3.0"));
			query.addExcludedPredicate(PredicateFactory.createPredicate("count", ">", "1.0"));
		} catch (TrivialPredicateException | InvalidPredicateException e) {
			fail();
		}
		
		expectedSQLQuery = "SELECT * FROM testRelation WHERE count >= 1.5 "
								+ "AND NOT ( count < 3 AND count > 1 );";

		actualSQLQuery = query.toSQLString();
		
		
		assertEquals(expectedSQLQuery,actualSQLQuery);
	}

}
