package edu.ou.oudb.cacheprototypelibrary.tests.querytest;

import junit.framework.TestCase;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.InvalidPredicateException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.TrivialPredicateException;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Predicate;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.PredicateFactory;


public class PredicateTest extends TestCase
{
	
	@SuppressWarnings("unused")
	public void testThrowInvalidPredicateExceptionIfTwoIdenticalOperands()
	{
		try {
			Predicate mPredicate = PredicateFactory.createPredicate("X", "<", "X");
			fail();
		} catch (InvalidPredicateException e) {
			assertTrue(true);
		} catch (TrivialPredicateException e) {
			fail();
		}
	}
	
	@SuppressWarnings("unused")
	public void testThrowInvalidPredicateExceptionIfOperatorInvalid()
	{
		try {
			Predicate mPredicate = PredicateFactory.createPredicate("X", "42", "X");
			fail();
		} catch (InvalidPredicateException e) {
			assertTrue(true);
		} catch (TrivialPredicateException e) {
			fail();
		}
	}
	
	@SuppressWarnings("unused")
	public void testThrowTrivialPredicateExceptionIfOperatorInvalid()
	{
		try {
			Predicate mPredicate = PredicateFactory.createPredicate("2", "<", "3");
		} catch (InvalidPredicateException e) {
			fail();
		} catch (TrivialPredicateException e) {
			assertTrue(true);
		}
	}
	
}
