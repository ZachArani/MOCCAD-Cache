package edu.ou.oudb.cacheprototypelibrary.tests.trimmingtest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.InvalidPredicateException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.TrivialPredicateException;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Predicate;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.PredicateFactory;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.XopCPredicate;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.XopYPredicate;
import edu.ou.oudb.cacheprototypelibrary.querycache.trimming.GuoEtAlPredicateAnalyzer;

public class GuoEtAlPredicateAnalyzerTest extends TestCase {

	@SuppressWarnings("unchecked")
	public void testInitSatisfiabilityRealDomain()
	{
		Set<Predicate> inputPredicates = new HashSet<Predicate>();
		Set<Predicate> predicates = null;
		GuoEtAlPredicateAnalyzer analyzer = new GuoEtAlPredicateAnalyzer();
		
		Set<Predicate> expectedPredicates = new HashSet<Predicate>();
		
		try {
			inputPredicates.add(PredicateFactory.createPredicate("x2", ">", "x1"));
			inputPredicates.add(PredicateFactory.createPredicate("x1", "=", "5.0"));
			inputPredicates.add(PredicateFactory.createPredicate("x2", "=", "x3"));
			
			
			expectedPredicates.add(new XopYPredicate("x1", "<", "x2"));
			expectedPredicates.add(new XopCPredicate("x1", "<=", 5.0));
			expectedPredicates.add(new XopCPredicate("x1", ">=", 5.0));
			expectedPredicates.add(new XopYPredicate("x2", "<=", "x3"));
			expectedPredicates.add(new XopYPredicate("x3", "<=", "x2"));
			

		} catch (TrivialPredicateException te) {
			fail("Exception launched on predicate creation:trivial");
			te.printStackTrace();
		} catch (InvalidPredicateException ie){
			fail("Exception launched on predicate creation:invalid");
			ie.printStackTrace();
		}
		
		
		try {
			Class<?>[] paramTypes = new Class[1];
			paramTypes[0] = Set.class;
			
			Method initPrerequisites = GuoEtAlPredicateAnalyzer.class.getDeclaredMethod("initPrerequisitesRealDomain", paramTypes);
			initPrerequisites.setAccessible(true);
			predicates = (Set<Predicate>) initPrerequisites.invoke(analyzer, inputPredicates);
			assertEquals(expectedPredicates,predicates);
		} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			fail("Exception launched on method invocation");
			e.printStackTrace();
		}
		
		
	}
	
	
	@SuppressWarnings("unchecked")
	public void testInitSatisfiabilityIntegerDomain()
	{
		Set<Predicate> inputPredicates = new HashSet<Predicate>();
		Set<Predicate> predicates = null;
		GuoEtAlPredicateAnalyzer analyzer = new GuoEtAlPredicateAnalyzer();
		
		Set<Predicate> expectedPredicates = new HashSet<Predicate>();
		
		try {
			inputPredicates.add(PredicateFactory.createPredicate("x3", "<", "10"));
			inputPredicates.add(PredicateFactory.createPredicate("x2", "=", "x3"));
			
			expectedPredicates.add(new XopCPredicate("x3", "<=", 9));
			expectedPredicates.add(new XopYPredicate("x2", "<=", "x3"));
			expectedPredicates.add(new XopYPredicate("x3", "<=", "x2"));
			
			

		} catch (TrivialPredicateException te) {
			fail("Exception launched on predicate creation:trivial");
			te.printStackTrace();
		} catch (InvalidPredicateException ie){
			fail("Exception launched on predicate creation:invalid");
			ie.printStackTrace();
		}
		
		
		try {
			Class<?>[] paramTypes = new Class[1];
			paramTypes[0] = Set.class;
			
			Method initPrerequisites = GuoEtAlPredicateAnalyzer.class.getDeclaredMethod("initPrerequisitesIntegerDomain", paramTypes);
			initPrerequisites.setAccessible(true);
			predicates = (Set<Predicate>) initPrerequisites.invoke(analyzer, inputPredicates);
			assertEquals(expectedPredicates,predicates);
		} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			fail("Exception launched on method invocation");
			e.printStackTrace();
		}
		
		
	}
	
	public void testRespectsSatisfiabilityRealDomain()
	{
		Set<Predicate> predicates = new HashSet<Predicate>();
		GuoEtAlPredicateAnalyzer analyzer = new GuoEtAlPredicateAnalyzer();
		
		try {
			predicates.add(PredicateFactory.createPredicate("x1", "<", "x2"));
			predicates.add(PredicateFactory.createPredicate("x1", "<", "x5"));
			predicates.add(PredicateFactory.createPredicate("x1", "<=", "x8"));
			predicates.add(PredicateFactory.createPredicate("x1", "<", "5.0"));
			
			predicates.add(PredicateFactory.createPredicate("x2", "=", "x3"));
			predicates.add(PredicateFactory.createPredicate("x2", "<=", "5.0"));
			predicates.add(PredicateFactory.createPredicate("x2", ">", "1.0"));
			
			predicates.add(PredicateFactory.createPredicate("x3", "<=", "x4"));
			predicates.add(PredicateFactory.createPredicate("x3", "<=", "7.0"));
			predicates.add(PredicateFactory.createPredicate("x3", ">=", "1.0"));
			
			predicates.add(PredicateFactory.createPredicate("x4", "<", "x7"));
			predicates.add(PredicateFactory.createPredicate("x4", "<=", "6.0"));
			predicates.add(PredicateFactory.createPredicate("x4", ">=", "4.0"));
			
			predicates.add(PredicateFactory.createPredicate("x5", "<", "x4"));
			predicates.add(PredicateFactory.createPredicate("x5", "<", "x6"));
			predicates.add(PredicateFactory.createPredicate("x5", "<=", "6.0"));
			predicates.add(PredicateFactory.createPredicate("x5", ">=", "2.0"));
			
			predicates.add(PredicateFactory.createPredicate("x6", "<=", "x7"));
			predicates.add(PredicateFactory.createPredicate("x6", "<=", "12.0"));
			predicates.add(PredicateFactory.createPredicate("x6", ">=", "5.0"));
			
			predicates.add(PredicateFactory.createPredicate("x7", "<", "8.0"));
			predicates.add(PredicateFactory.createPredicate("x7", ">=", "1.0"));
			
			predicates.add(PredicateFactory.createPredicate("x8", "<=", "x6"));
			predicates.add(PredicateFactory.createPredicate("x8", ">", "3.0"));
			

		} catch (TrivialPredicateException | InvalidPredicateException te) {
			fail("Exception launched on predicate creation");
		}
	
		
		assertTrue(analyzer.respectsSatifiabilityRealDomain(predicates));
	}
	
	public void testRespectsSatisfiabilityIntegerDomain()
	{
		Set<Predicate> predicates = new HashSet<Predicate>();
		GuoEtAlPredicateAnalyzer analyzer = new GuoEtAlPredicateAnalyzer();
		
		try {
			predicates.add(PredicateFactory.createPredicate("x1", "<", "x2"));
			predicates.add(PredicateFactory.createPredicate("x1", "<", "x5"));
			predicates.add(PredicateFactory.createPredicate("x1", "<=", "x8"));
			predicates.add(PredicateFactory.createPredicate("x1", "<", "5"));
			
			predicates.add(PredicateFactory.createPredicate("x2", "=", "x3"));
			predicates.add(PredicateFactory.createPredicate("x2", "<=", "5"));
			predicates.add(PredicateFactory.createPredicate("x2", ">", "1"));
			
			predicates.add(PredicateFactory.createPredicate("x3", "<=", "x4"));
			predicates.add(PredicateFactory.createPredicate("x3", "<=", "7"));
			predicates.add(PredicateFactory.createPredicate("x3", ">=", "1"));
			
			predicates.add(PredicateFactory.createPredicate("x4", "<", "x7"));
			predicates.add(PredicateFactory.createPredicate("x4", "<=", "6"));
			predicates.add(PredicateFactory.createPredicate("x4", ">=", "4"));
			
			predicates.add(PredicateFactory.createPredicate("x5", "<", "x4"));
			predicates.add(PredicateFactory.createPredicate("x5", "<", "x6"));
			predicates.add(PredicateFactory.createPredicate("x5", "<=", "6"));
			predicates.add(PredicateFactory.createPredicate("x5", ">=", "2"));
			
			predicates.add(PredicateFactory.createPredicate("x6", "<=", "x7"));
			predicates.add(PredicateFactory.createPredicate("x6", "<=", "12"));
			predicates.add(PredicateFactory.createPredicate("x6", ">=", "5"));
			
			predicates.add(PredicateFactory.createPredicate("x7", "<", "8"));
			predicates.add(PredicateFactory.createPredicate("x7", ">=", "1"));
			
			predicates.add(PredicateFactory.createPredicate("x8", "<=", "x6"));
			predicates.add(PredicateFactory.createPredicate("x8", ">", "3"));
			

		} catch (TrivialPredicateException | InvalidPredicateException te) {
			fail("Exception launched on predicate creation");
		}
	
		
		assertTrue(analyzer.respectsSatisfiabilityIntegerDomain(predicates));
	}
	
	public void testNotRespectSatisfiabilityIntegerDomain()
	{
		Set<Predicate> predicates = new HashSet<Predicate>();
		GuoEtAlPredicateAnalyzer analyzer = new GuoEtAlPredicateAnalyzer();
		
		try {
			predicates.add(PredicateFactory.createPredicate("HeartRate", "<=", "68"));
			predicates.add(PredicateFactory.createPredicate("HeartRate", ">=", "80"));
		} catch (TrivialPredicateException | InvalidPredicateException te) {
			fail("Exception launched on predicate creation");
		}
	
		
		assertFalse(analyzer.respectsSatisfiabilityIntegerDomain(predicates));
	}
	
	
	public void testRespectsImplicationOnXopYPredicatesRealDomain()
	{
		Set<Predicate> queryPredicates = new HashSet<Predicate>();
		Set<Predicate> cachePredicates = new HashSet<Predicate>();
		GuoEtAlPredicateAnalyzer analyzer = new GuoEtAlPredicateAnalyzer();
		
		try {
			queryPredicates.add(PredicateFactory.createPredicate("x1", "<", "x2"));
			cachePredicates.add(PredicateFactory.createPredicate("x1", "<=", "x2"));
		} catch (TrivialPredicateException | InvalidPredicateException e) {
			fail("Exception launched on predicate creation");
		}
		
		
		assertTrue(analyzer.respectsImplicationRealDomain(queryPredicates,cachePredicates));
	}
	
	public void testRespectsImplicationOnXopYPredicatesIntegerDomain()
	{
		Set<Predicate> queryPredicates = new HashSet<Predicate>();
		Set<Predicate> cachePredicates = new HashSet<Predicate>();
		GuoEtAlPredicateAnalyzer analyzer = new GuoEtAlPredicateAnalyzer();
		
		try {
			queryPredicates.add(PredicateFactory.createPredicate("x1", "<", "x2"));
			cachePredicates.add(PredicateFactory.createPredicate("x1", "<=", "x2"));
		} catch (TrivialPredicateException | InvalidPredicateException e) {
			fail("Exception launched on predicate creation");
		}
		
		
		assertTrue(analyzer.respectsImplicationIntegerDomain(queryPredicates,cachePredicates));
	}
	
	public void testNotRespectImplicationOnXopYPredicatesRealDomain()
	{
		Set<Predicate> queryPredicates = new HashSet<Predicate>();
		Set<Predicate> cachePredicates = new HashSet<Predicate>();
		GuoEtAlPredicateAnalyzer analyzer = new GuoEtAlPredicateAnalyzer();
		
		try {
			queryPredicates.add(PredicateFactory.createPredicate("x1", "<=", "x2"));
			cachePredicates.add(PredicateFactory.createPredicate("x1", "<", "x2"));
		} catch (TrivialPredicateException | InvalidPredicateException e) {
			fail("Exception launched on predicate creation");
		}
		
		assertFalse(analyzer.respectsImplicationRealDomain(queryPredicates,cachePredicates));
	}
	
	
	public void testNotRespectImplicationOnXopYPredicatesIntegerDomain()
	{
		Set<Predicate> queryPredicates = new HashSet<Predicate>();
		Set<Predicate> cachePredicates = new HashSet<Predicate>();
		GuoEtAlPredicateAnalyzer analyzer = new GuoEtAlPredicateAnalyzer();
		
		try {
			queryPredicates.add(PredicateFactory.createPredicate("x1", "<=", "x2"));
			cachePredicates.add(PredicateFactory.createPredicate("x1", "<", "x2"));
		} catch (TrivialPredicateException | InvalidPredicateException e) {
			fail("Exception launched on predicate creation");
		}
		
		assertFalse(analyzer.respectsImplicationIntegerDomain(queryPredicates,cachePredicates));
	}
	
	public void testRespectImplicationOnXopCPredicatesRealDomain()
	{
		Set<Predicate> queryPredicates = new HashSet<Predicate>();
		Set<Predicate> cachePredicates = new HashSet<Predicate>();
		GuoEtAlPredicateAnalyzer analyzer = new GuoEtAlPredicateAnalyzer();
		
		try {
			queryPredicates.add(PredicateFactory.createPredicate("x1", "=", "25.0"));
			cachePredicates.add(PredicateFactory.createPredicate("x1", ">", "20.0"));
			cachePredicates.add(PredicateFactory.createPredicate("x1", "<", "30.0"));
		} catch (TrivialPredicateException | InvalidPredicateException e) {
			fail("Exception launched on predicate creation");
		}
		
		assertTrue(analyzer.respectsImplicationRealDomain(queryPredicates,cachePredicates));
	}
	
	public void testRespectImplicationOnXopCPredicatesIntegerDomain()
	{
		Set<Predicate> queryPredicates = new HashSet<Predicate>();
		Set<Predicate> cachePredicates = new HashSet<Predicate>();
		GuoEtAlPredicateAnalyzer analyzer = new GuoEtAlPredicateAnalyzer();
		
		try {
			queryPredicates.add(PredicateFactory.createPredicate("x1", "=", "25"));
			cachePredicates.add(PredicateFactory.createPredicate("x1", ">", "24"));
			cachePredicates.add(PredicateFactory.createPredicate("x1", "<", "26"));
		} catch (TrivialPredicateException | InvalidPredicateException e) {
			fail("Exception launched on predicate creation");
		}
		
		assertTrue(analyzer.respectsImplicationIntegerDomain(queryPredicates,cachePredicates));
	}
	
	public void testNotRespectImplicationOnXopCPredicatesRealDomain()
	{
		Set<Predicate> queryPredicates = new HashSet<Predicate>();
		Set<Predicate> cachePredicates = new HashSet<Predicate>();
		GuoEtAlPredicateAnalyzer analyzer = new GuoEtAlPredicateAnalyzer();
		
		try {
			queryPredicates.add(PredicateFactory.createPredicate("x1", ">", "20.0"));
			queryPredicates.add(PredicateFactory.createPredicate("x1", "<", "30.0"));
			cachePredicates.add(PredicateFactory.createPredicate("x1", "=", "23.0"));
		} catch (TrivialPredicateException | InvalidPredicateException e) {
			fail("Exception launched on predicate creation");
		}
		
		assertFalse(analyzer.respectsImplicationRealDomain(queryPredicates,cachePredicates));
	}
	
	public void testNotRespectImplicationOnXopCPredicatesIntegerDomain()
	{
		Set<Predicate> queryPredicates = new HashSet<Predicate>();
		Set<Predicate> cachePredicates = new HashSet<Predicate>();
		GuoEtAlPredicateAnalyzer analyzer = new GuoEtAlPredicateAnalyzer();
		
		try {
			queryPredicates.add(PredicateFactory.createPredicate("x1", ">", "16"));
			queryPredicates.add(PredicateFactory.createPredicate("x1", "<", "19"));
			cachePredicates.add(PredicateFactory.createPredicate("x1", "=", "17"));
		} catch (TrivialPredicateException | InvalidPredicateException e) {
			fail("Exception launched on predicate creation");
		}
		
		assertFalse(analyzer.respectsImplicationIntegerDomain(queryPredicates,cachePredicates));
	}
	
	public void testRespectImplicationOnAnyPredicatesRealDomain()
	{
		Set<Predicate> queryPredicates = new HashSet<Predicate>();
		Set<Predicate> cachePredicates = new HashSet<Predicate>();
		GuoEtAlPredicateAnalyzer analyzer = new GuoEtAlPredicateAnalyzer();
		
		try {
			queryPredicates.add(PredicateFactory.createPredicate("x1", "=", "1.0"));
			queryPredicates.add(PredicateFactory.createPredicate("x2", ">", "30.0"));
			
			cachePredicates.add(PredicateFactory.createPredicate("x1", "<=", "x2"));
			cachePredicates.add(PredicateFactory.createPredicate("x1", "<", "2.0"));
			cachePredicates.add(PredicateFactory.createPredicate("x1", ">", "0"));
		} catch (TrivialPredicateException | InvalidPredicateException e) {
			fail("Exception launched on predicate creation");
		}
		
		assertTrue(analyzer.respectsImplicationRealDomain(queryPredicates,cachePredicates));
	}
	
	public void testRespectImplicationOnAnyPredicatesIntegerDomain()
	{
		Set<Predicate> queryPredicates = new HashSet<Predicate>();
		Set<Predicate> cachePredicates = new HashSet<Predicate>();
		GuoEtAlPredicateAnalyzer analyzer = new GuoEtAlPredicateAnalyzer();
		
		try {
			queryPredicates.add(PredicateFactory.createPredicate("x1", "=", "1"));
			queryPredicates.add(PredicateFactory.createPredicate("x2", ">", "30"));
			
			cachePredicates.add(PredicateFactory.createPredicate("x1", "<=", "x2"));
			cachePredicates.add(PredicateFactory.createPredicate("x1", "<", "2"));
			cachePredicates.add(PredicateFactory.createPredicate("x1", ">", "0"));
		} catch (TrivialPredicateException | InvalidPredicateException e) {
			fail("Exception launched on predicate creation");
		}
		
		assertTrue(analyzer.respectsImplicationIntegerDomain(queryPredicates,cachePredicates));
	}
	
	public void testNotRespectImplicationOnAnyPredicatesRealDomain()
	{
		Set<Predicate> queryPredicates = new HashSet<Predicate>();
		Set<Predicate> cachePredicates = new HashSet<Predicate>();
		GuoEtAlPredicateAnalyzer analyzer = new GuoEtAlPredicateAnalyzer();
		
		try {
			queryPredicates.add(PredicateFactory.createPredicate("x1", ">", "0"));
			queryPredicates.add(PredicateFactory.createPredicate("x2", ">", "30.0"));
			
			cachePredicates.add(PredicateFactory.createPredicate("x1", "<=", "x2"));
			cachePredicates.add(PredicateFactory.createPredicate("x1", "<", "2.0"));
			cachePredicates.add(PredicateFactory.createPredicate("x1", ">", "0"));
		} catch (TrivialPredicateException | InvalidPredicateException e) {
			fail("Exception launched on predicate creation");
		}
		
		assertFalse(analyzer.respectsImplicationRealDomain(queryPredicates,cachePredicates));
	}
	
	public void testNotRespectImplicationOnAnyPredicatesIntegerDomain()
	{
		Set<Predicate> queryPredicates = new HashSet<Predicate>();
		Set<Predicate> cachePredicates = new HashSet<Predicate>();
		GuoEtAlPredicateAnalyzer analyzer = new GuoEtAlPredicateAnalyzer();
		
		try {
			queryPredicates.add(PredicateFactory.createPredicate("x1", ">", "0"));
			queryPredicates.add(PredicateFactory.createPredicate("x2", ">", "30"));
			
			cachePredicates.add(PredicateFactory.createPredicate("x1", "<=", "x2"));
			cachePredicates.add(PredicateFactory.createPredicate("x1", "<", "2"));
			cachePredicates.add(PredicateFactory.createPredicate("x1", ">", "0"));
		} catch (TrivialPredicateException | InvalidPredicateException e) {
			fail("Exception launched on predicate creation");
		}
		
		assertFalse(analyzer.respectsImplicationIntegerDomain(queryPredicates,cachePredicates));
	}
	
	
	public void testNotRespectImplicationIfAttributeNotPresentInBothPredicateListsRealDomain()
	{
		Set<Predicate> queryPredicates = new HashSet<Predicate>();
		Set<Predicate> cachePredicates = new HashSet<Predicate>();
		GuoEtAlPredicateAnalyzer analyzer = new GuoEtAlPredicateAnalyzer();
		
		try {
			queryPredicates.add(PredicateFactory.createPredicate("purchase", "<", "100000.0"));
			cachePredicates.add(PredicateFactory.createPredicate("purchase", "<=", "cpt_amount"));
			cachePredicates.add(PredicateFactory.createPredicate("cpt_amount", ">", "300000.0"));
		} catch (TrivialPredicateException | InvalidPredicateException e) {
			fail("Exception launched on predicate creation");
		}
		
		assertFalse(analyzer.respectsImplicationRealDomain(queryPredicates,cachePredicates));
	}
	
	public void testNotRespectImplicationIfAttributeNotPresentInBothPredicateListsIntegerDomain()
	{
		Set<Predicate> queryPredicates = new HashSet<Predicate>();
		Set<Predicate> cachePredicates = new HashSet<Predicate>();
		GuoEtAlPredicateAnalyzer analyzer = new GuoEtAlPredicateAnalyzer();
		
		try {
			queryPredicates.add(PredicateFactory.createPredicate("purchase", "<", "100000.0"));
			cachePredicates.add(PredicateFactory.createPredicate("purchase", "<=", "cpt_amount"));
			cachePredicates.add(PredicateFactory.createPredicate("cpt_amount", ">", "300000.0"));
		} catch (TrivialPredicateException | InvalidPredicateException e) {
			fail("Exception launched on predicate creation");
		}
		
		assertFalse(analyzer.respectsImplicationIntegerDomain(queryPredicates,cachePredicates));
	}
	
	
}
