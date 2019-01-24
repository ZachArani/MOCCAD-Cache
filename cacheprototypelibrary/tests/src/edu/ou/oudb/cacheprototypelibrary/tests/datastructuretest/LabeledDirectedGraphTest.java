package edu.ou.oudb.cacheprototypelibrary.tests.datastructuretest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Stack;

import junit.framework.TestCase;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.CycleFoundException;
import edu.ou.oudb.cacheprototypelibrary.querycache.trimming.AttributeNode;
import edu.ou.oudb.cacheprototypelibrary.querycache.trimming.LabeledDirectedGraph;

public class LabeledDirectedGraphTest extends TestCase{

	public void testMergeNodesMethod()
	{
		LabeledDirectedGraph ldg = new LabeledDirectedGraph();
		
		AttributeNode node1 = new AttributeNode("x1");
		node1.setLowClosedMinRange(1, true);
		node1.setUpClosedMinRange(5, false);
		
		AttributeNode node2 = new AttributeNode("x2");
		node2.setLowClosedMinRange(1, false);
		node2.setUpClosedMinRange(7, false);
		
		AttributeNode expectedResult = new AttributeNode("x1x2");
		expectedResult.setLowClosedMinRange(1, true);
		expectedResult.setUpClosedMinRange(5, false);
		
		LinkedHashSet<AttributeNode> sccs = new LinkedHashSet<AttributeNode>();
		sccs.add(node1);
		sccs.add(node2);
		
		AttributeNode result = ldg.mergeNodes(sccs);
		
		assertEquals(expectedResult,result);
	}
	
	public void testNodesIndexAfterMergeNodes()
	{
		LabeledDirectedGraph ldg = new LabeledDirectedGraph();
		LabeledDirectedGraph collapsedLdg = new LabeledDirectedGraph();
		
		AttributeNode x1 = new AttributeNode("x1");
		x1.setLowClosedMinRange(1, true);
		x1.setUpClosedMinRange(5, false);
		
		AttributeNode x2 = new AttributeNode("x2");
		x2.setLowClosedMinRange(1, false);
		x2.setUpClosedMinRange(7, false);
		
		ldg.addEdge(x1, x2, "<=");
		ldg.addEdge(x2, x1, "<=");
		
		AttributeNode expectedMergeNode = new AttributeNode("x1x2");
		expectedMergeNode.setLowClosedMinRange(1, true);
		expectedMergeNode.setUpClosedMinRange(5, false);
		
		HashMap<AttributeNode,AttributeNode> sccs = new HashMap<AttributeNode,AttributeNode>();
		
		sccs.put(x1, expectedMergeNode);
		sccs.put(x2, expectedMergeNode);

		
		try {
			Method buildCollapsedGraph = LabeledDirectedGraph.class.getDeclaredMethod("buildCollapsedGraph", HashMap.class);
			buildCollapsedGraph.setAccessible(true);
			
			collapsedLdg = (LabeledDirectedGraph) buildCollapsedGraph.invoke(ldg, sccs);
			
			//assert that index of x1 points to the same node than index of x2
			assertEquals(collapsedLdg.getNodesIndex().get("x1"),collapsedLdg.getNodesIndex().get("x2"));
		
		} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			fail("Exception launched");
		}
		
		
	}
	
	public void testAddEdgeIfItAlreadyExists()
	{
		LabeledDirectedGraph ldg = new LabeledDirectedGraph();
		LabeledDirectedGraph expectedLdg = new LabeledDirectedGraph();
		
		AttributeNode x1 = new AttributeNode("x1");
		x1.setLowClosedMinRange(Double.NEGATIVE_INFINITY, true);
		x1.setUpClosedMinRange(5, true);
		
		AttributeNode x2 = new AttributeNode("x2");
		x2.setLowClosedMinRange(1, true);
		x2.setUpClosedMinRange(5, false);
		
		ldg.addEdge(x1, x2, "<=");
		ldg.addEdge(x1, x2, "<");
		
		expectedLdg.addEdge(x1, x2, "<");
		
		assertEquals(expectedLdg,ldg);
	}
	
	public void testBuildCollapsedGraphMethod()
	{
		LabeledDirectedGraph ldg = new LabeledDirectedGraph();
		LabeledDirectedGraph collapsedLdg = new LabeledDirectedGraph();
		LabeledDirectedGraph expectedLdg = new LabeledDirectedGraph();
		
		AttributeNode x1 = new AttributeNode("x1");
		x1.setLowClosedMinRange(Double.NEGATIVE_INFINITY, true);
		x1.setUpClosedMinRange(5, true);
		
		AttributeNode x2 = new AttributeNode("x2");
		x2.setLowClosedMinRange(1, true);
		x2.setUpClosedMinRange(5, false);
		
		AttributeNode x3 = new AttributeNode("x3");
		x3.setLowClosedMinRange(1, false);
		x3.setUpClosedMinRange(7, false);
		
		AttributeNode x4 = new AttributeNode("x4");
		x4.setLowClosedMinRange(4, false);
		x4.setUpClosedMinRange(6, false);
		
		AttributeNode expectedMergeNode = new AttributeNode("x2x3");
		expectedMergeNode.setLowClosedMinRange(1, true);
		expectedMergeNode.setUpClosedMinRange(5, false);
		
		ldg.addEdge(x1, x2, "<");
		ldg.addEdge(x2, x3, "<=");
		ldg.addEdge(x3, x2, "<=");
		ldg.addEdge(x3, x4, "<=");
		
		HashMap<AttributeNode,AttributeNode> sccs = new HashMap<AttributeNode,AttributeNode>();
		
		sccs.put(x1, x1);
		sccs.put(x2, expectedMergeNode);
		sccs.put(x3, expectedMergeNode);
		sccs.put(x4, x4);
		
		expectedLdg.addEdge(x1, expectedMergeNode, "<");
		expectedLdg.addEdge(expectedMergeNode, x4 , "<=");
		
		try {
			Method buildCollapsedGraph = LabeledDirectedGraph.class.getDeclaredMethod("buildCollapsedGraph", HashMap.class);
			buildCollapsedGraph.setAccessible(true);
			
			collapsedLdg = (LabeledDirectedGraph) buildCollapsedGraph.invoke(ldg, sccs);
		
			assertEquals(expectedLdg,collapsedLdg);
		} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			fail("Exception launched");
		}
	}
	
	public void testTransposedGraphMethod()
	{
		LabeledDirectedGraph ldg = new LabeledDirectedGraph();
		LabeledDirectedGraph transposedGraph = new LabeledDirectedGraph();
		LabeledDirectedGraph expectedLdg = new LabeledDirectedGraph();
		
		AttributeNode x1 = new AttributeNode("x1");
		x1.setLowClosedMinRange(Double.NEGATIVE_INFINITY, true);
		x1.setUpClosedMinRange(5, true);
		
		AttributeNode x2 = new AttributeNode("x2");
		x2.setLowClosedMinRange(1, true);
		x2.setUpClosedMinRange(5, false);
		
		AttributeNode x3 = new AttributeNode("x3");
		x3.setLowClosedMinRange(1, false);
		x3.setUpClosedMinRange(7, false);
		
		AttributeNode x4 = new AttributeNode("x4");
		x4.setLowClosedMinRange(4, false);
		x4.setUpClosedMinRange(6, false);
		
		ldg.addEdge(x1, x2, "<");
		ldg.addEdge(x2, x3, "<=");
		ldg.addEdge(x3, x2, "<=");
		ldg.addEdge(x3, x4, "<=");
		
		expectedLdg.addEdge(x4, x3, "<=");
		expectedLdg.addEdge(x2, x3, "<=");
		expectedLdg.addEdge(x3, x2, "<=");
		expectedLdg.addEdge(x2, x1, "<");
		
		transposedGraph = ldg.getTransposedGraph();
		
		assertEquals(expectedLdg,transposedGraph);
	}
	
	public void testBuildTransitiveClosure()
	{
		LabeledDirectedGraph ldg = new LabeledDirectedGraph();
		boolean[][] transitiveClosure;
		boolean[][] expectedTransitiveClosure = { 	
													{true, true, true, true, true, true, true},
													{false, true, true, false, false, true, false},
													{false, false, true, false, false, true, false},
													{false, false, true, true, true, true, false},
													{false, false, false, false, true, true, false},
													{false, false, false, false, false, true, false},
													{false, false, false, false, true, true, true}	
												};
		
		AttributeNode x1 = new AttributeNode("x1");
		x1.setLowClosedMinRange(Double.NEGATIVE_INFINITY, true);
		x1.setUpClosedMinRange(5, true);
		
		AttributeNode x2x3 = new AttributeNode("x2x3");
		x2x3.setLowClosedMinRange(1, true);
		x2x3.setUpClosedMinRange(5, false);
		
		AttributeNode x4 = new AttributeNode("x4");
		x4.setLowClosedMinRange(4, false);
		x4.setUpClosedMinRange(6, false);
		
		AttributeNode x5 = new AttributeNode("x5");
		x5.setLowClosedMinRange(2, false);
		x5.setUpClosedMinRange(6, false);
		
		AttributeNode x6 = new AttributeNode("x6");
		x6.setLowClosedMinRange(5, false);
		x6.setUpClosedMinRange(12, false);
		
		AttributeNode x7 = new AttributeNode("x7");
		x7.setLowClosedMinRange(1, false);
		x7.setUpClosedMinRange(8, true);
		
		AttributeNode x8 = new AttributeNode("x8");
		x8.setLowClosedMinRange(3, true);
		x8.setUpClosedMinRange(Double.POSITIVE_INFINITY, true);
		
		//insert the node first to be sure that the linked hash map respect the order
		ldg.addNode(x1);
		ldg.addNode(x2x3);
		ldg.addNode(x4);
		ldg.addNode(x5);
		ldg.addNode(x6);
		ldg.addNode(x7);
		ldg.addNode(x8);
		
		ldg.addEdge(x1, x2x3, "<");
		ldg.addEdge(x1, x5, "<");
		ldg.addEdge(x1, x8, "<=");
		ldg.addEdge(x2x3, x4, "<=");
		ldg.addEdge(x4, x7, "<");
		ldg.addEdge(x5, x4, "<");
		ldg.addEdge(x5, x6, "<");
		ldg.addEdge(x6, x7, "<=");
		ldg.addEdge(x8, x6, "<=");
		
		try {
			Method buildTransitiveClosure = LabeledDirectedGraph.class.getDeclaredMethod("buildTransitiveClosure");
			buildTransitiveClosure.setAccessible(true);
			
			buildTransitiveClosure.invoke(ldg);
			transitiveClosure = ldg.getTransitiveClosure();
		
			for (int i=0; i < transitiveClosure.length; ++i)
			{
				for(int j=0; j < transitiveClosure[i].length; ++j)
				{
					assertEquals(expectedTransitiveClosure[i][j], transitiveClosure[i][j]);
				}
			}
		} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			fail("Exception launched");
		}
		
	}
	
	public void testBuildTransitiveClosureLt()
	{
		LabeledDirectedGraph ldg = new LabeledDirectedGraph();
		boolean[][] transitiveClosureLt;
		boolean[][] expectedTransitiveClosureLt = { 	
													{false, true, true, true, true, true, false},
													{false, false, false, false, false, true, false},
													{false, false, false, false, false, true, false},
													{false, false, true, false, true, true, false},
													{false, false, false, false, false, false, false},
													{false, false, false, false, false, false, false},
													{false, false, false, false, false, false, false}	
												};
		
		AttributeNode x1 = new AttributeNode("x1");
		x1.setLowClosedMinRange(Double.NEGATIVE_INFINITY, true);
		x1.setUpClosedMinRange(5, true);
		
		AttributeNode x2x3 = new AttributeNode("x2x3");
		x2x3.setLowClosedMinRange(1, true);
		x2x3.setUpClosedMinRange(5, false);
		
		AttributeNode x4 = new AttributeNode("x4");
		x4.setLowClosedMinRange(4, false);
		x4.setUpClosedMinRange(6, false);
		
		AttributeNode x5 = new AttributeNode("x5");
		x5.setLowClosedMinRange(2, false);
		x5.setUpClosedMinRange(6, false);
		
		AttributeNode x6 = new AttributeNode("x6");
		x6.setLowClosedMinRange(5, false);
		x6.setUpClosedMinRange(12, false);
		
		AttributeNode x7 = new AttributeNode("x7");
		x7.setLowClosedMinRange(1, false);
		x7.setUpClosedMinRange(8, true);
		
		AttributeNode x8 = new AttributeNode("x8");
		x8.setLowClosedMinRange(3, true);
		x8.setUpClosedMinRange(Double.POSITIVE_INFINITY, true);
		
		//insert the node first to be sure that the linked hash map respect the order
		ldg.addNode(x1);
		ldg.addNode(x2x3);
		ldg.addNode(x4);
		ldg.addNode(x5);
		ldg.addNode(x6);
		ldg.addNode(x7);
		ldg.addNode(x8);
		
		ldg.addEdge(x1, x2x3, "<");
		ldg.addEdge(x1, x5, "<");
		ldg.addEdge(x1, x8, "<=");
		ldg.addEdge(x2x3, x4, "<=");
		ldg.addEdge(x4, x7, "<");
		ldg.addEdge(x5, x4, "<");
		ldg.addEdge(x5, x6, "<");
		ldg.addEdge(x6, x7, "<=");
		ldg.addEdge(x8, x6, "<=");
		
		try {
			Method buildTransitiveClosure = LabeledDirectedGraph.class.getDeclaredMethod("buildTransitiveClosure");
			buildTransitiveClosure.setAccessible(true);
			
			buildTransitiveClosure.invoke(ldg);
			transitiveClosureLt = ldg.getTransitiveClosureLt();
		
			for (int i=0; i < transitiveClosureLt.length; ++i)
			{
				for(int j=0; j < transitiveClosureLt[i].length; ++j)
				{
					assertEquals(expectedTransitiveClosureLt[i][j], transitiveClosureLt[i][j]);
				}
			}
		} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			fail("Exception launched");
		}
	}
	
	@SuppressWarnings("unchecked")
	public void testFindFinishedExplorationOrderMethod()
	{
		LabeledDirectedGraph ldg = new LabeledDirectedGraph();
		Stack<AttributeNode> finishedExplorationOrder = null;
		Stack<AttributeNode> expectedFinishedExplorationOrder = new Stack<AttributeNode>();
		HashSet<AttributeNode> visited = new HashSet<AttributeNode>();
		
		AttributeNode x1 = new AttributeNode("x1");
		x1.setLowClosedMinRange(Double.NEGATIVE_INFINITY, true);
		x1.setUpClosedMinRange(5, true);
		
		AttributeNode x2 = new AttributeNode("x2");
		x2.setLowClosedMinRange(1, true);
		x2.setUpClosedMinRange(5, false);
		
		AttributeNode x3 = new AttributeNode("x3");
		x3.setLowClosedMinRange(1, false);
		x3.setUpClosedMinRange(7, false);
		
		AttributeNode x4 = new AttributeNode("x4");
		x4.setLowClosedMinRange(4, false);
		x4.setUpClosedMinRange(6, false);
		
		AttributeNode x5 = new AttributeNode("x5");
		x5.setLowClosedMinRange(2, false);
		x5.setUpClosedMinRange(6, false);
		
		AttributeNode x6 = new AttributeNode("x6");
		x6.setLowClosedMinRange(5, false);
		x6.setUpClosedMinRange(12, false);
		
		AttributeNode x7 = new AttributeNode("x7");
		x7.setLowClosedMinRange(1, false);
		x7.setUpClosedMinRange(8, true);
		
		AttributeNode x8 = new AttributeNode("x8");
		x8.setLowClosedMinRange(3, true);
		x8.setUpClosedMinRange(Double.POSITIVE_INFINITY, true);
		
		ldg.addEdge(x1, x2, "<");
		ldg.addEdge(x1, x5, "<");
		ldg.addEdge(x1, x8, "<=");
		ldg.addEdge(x2, x3, "<=");
		ldg.addEdge(x3, x2, "<=");
		ldg.addEdge(x3, x4, "<=");
		ldg.addEdge(x4, x7, "<");
		ldg.addEdge(x5, x4, "<");
		ldg.addEdge(x5, x6, "<");
		ldg.addEdge(x6, x7, "<=");
		ldg.addEdge(x8, x6, "<=");
		
		expectedFinishedExplorationOrder.push(x7);
		expectedFinishedExplorationOrder.push(x6);
		expectedFinishedExplorationOrder.push(x8);
		expectedFinishedExplorationOrder.push(x4);
		expectedFinishedExplorationOrder.push(x5);
		expectedFinishedExplorationOrder.push(x3);
		expectedFinishedExplorationOrder.push(x2);
		expectedFinishedExplorationOrder.push(x1);
		
		
		try {
			Class<?>[] paramTypes = new Class[2];
			paramTypes[0] = AttributeNode.class;
			paramTypes[1] =	HashSet.class;
			
			Method findFinishedExplorationOrder = LabeledDirectedGraph.class.getDeclaredMethod("findFinishedExplorationOrder", paramTypes);
			findFinishedExplorationOrder.setAccessible(true);
			
			finishedExplorationOrder = (Stack<AttributeNode>) findFinishedExplorationOrder.invoke( ldg, x1, visited);
			
			assertEquals(expectedFinishedExplorationOrder,finishedExplorationOrder);
		} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			fail("Exception launched");
		}
	}
	
	public void testGetCollapsedGraphMethod()
	{
		LabeledDirectedGraph ldg = new LabeledDirectedGraph();
		LabeledDirectedGraph collapsedLdg = new LabeledDirectedGraph();
		LabeledDirectedGraph expectedLdg = new LabeledDirectedGraph();
		
		AttributeNode x1 = new AttributeNode("x1");
		x1.setLowClosedMinRange(Double.NEGATIVE_INFINITY, true);
		x1.setUpClosedMinRange(5, true);
		
		AttributeNode x2 = new AttributeNode("x2");
		x2.setLowClosedMinRange(1, true);
		x2.setUpClosedMinRange(5, false);
		
		AttributeNode x3 = new AttributeNode("x3");
		x3.setLowClosedMinRange(1, false);
		x3.setUpClosedMinRange(7, false);
		
		AttributeNode x4 = new AttributeNode("x4");
		x4.setLowClosedMinRange(4, false);
		x4.setUpClosedMinRange(6, false);
		
		AttributeNode x5 = new AttributeNode("x5");
		x5.setLowClosedMinRange(2, false);
		x5.setUpClosedMinRange(6, false);
		
		ldg.addEdge(x1, x2, "<");
		ldg.addEdge(x1, x5, "<");
		ldg.addEdge(x2, x3, "<=");
		ldg.addEdge(x3, x2, "<=");
		ldg.addEdge(x3, x4, "<=");
		ldg.addEdge(x5, x4, "<");
		
		AttributeNode expectedMergeNode = new AttributeNode("x2x3");
		expectedMergeNode.setLowClosedMinRange(1, true);
		expectedMergeNode.setUpClosedMinRange(5, false);
		
		expectedLdg.addEdge(x1, expectedMergeNode, "<");
		expectedLdg.addEdge(x1, x5, "<");
		expectedLdg.addEdge(expectedMergeNode, x4, "<=");
		expectedLdg.addEdge(x5, x4, "<");
		
		
		collapsedLdg = ldg.getCollapsedGraph();
		
		assertEquals(expectedLdg,collapsedLdg);
	}
	
	public void testGetCollapsedGraphMethodNullWithInvalidSCC()
	{
		LabeledDirectedGraph ldg = new LabeledDirectedGraph();
		LabeledDirectedGraph collapsedLdg = new LabeledDirectedGraph();
		
		AttributeNode x1 = new AttributeNode("x1");
		x1.setLowClosedMinRange(Double.NEGATIVE_INFINITY, true);
		x1.setUpClosedMinRange(5, true);
		
		AttributeNode x2 = new AttributeNode("x2");
		x2.setLowClosedMinRange(1, true);
		x2.setUpClosedMinRange(5, false);
		
		AttributeNode x3 = new AttributeNode("x3");
		x3.setLowClosedMinRange(1, false);
		x3.setUpClosedMinRange(7, false);
		
		AttributeNode x4 = new AttributeNode("x4");
		x4.setLowClosedMinRange(4, false);
		x4.setUpClosedMinRange(6, false);
		
		AttributeNode x5 = new AttributeNode("x5");
		x5.setLowClosedMinRange(2, false);
		x5.setUpClosedMinRange(6, false);
		
		ldg.addEdge(x1, x2, "<");
		ldg.addEdge(x1, x5, "<");
		
		//<invalid SCC>
		ldg.addEdge(x2, x3, "<");
		ldg.addEdge(x3, x2, "<=");
		//</invalidSCC>
		
		ldg.addEdge(x3, x4, "<=");
		ldg.addEdge(x5, x4, "<");
		
		
		collapsedLdg = ldg.getCollapsedGraph();
		
		assertNull(collapsedLdg);
	}
	
	public void testComputeMinRealRangesMethod()
	{
		LabeledDirectedGraph ldg = new LabeledDirectedGraph();
		LabeledDirectedGraph expectedLdg = new LabeledDirectedGraph();
		
		// computed nodes
		AttributeNode x1 = new AttributeNode("x1");
		x1.setLowClosedMinRange(Double.NEGATIVE_INFINITY, true);
		x1.setUpClosedMinRange(5, true);
		
		AttributeNode x2x3 = new AttributeNode("x2x3");
		x2x3.setLowClosedMinRange(1, true);
		x2x3.setUpClosedMinRange(5, false);
		
		AttributeNode x4 = new AttributeNode("x4");
		x4.setLowClosedMinRange(4, false);
		x4.setUpClosedMinRange(6, false);
		
		AttributeNode x5 = new AttributeNode("x5");
		x5.setLowClosedMinRange(2, false);
		x5.setUpClosedMinRange(6, false);
		
		AttributeNode x6 = new AttributeNode("x6");
		x6.setLowClosedMinRange(5, false);
		x6.setUpClosedMinRange(12, false);
		
		AttributeNode x7 = new AttributeNode("x7");
		x7.setLowClosedMinRange(1, false);
		x7.setUpClosedMinRange(8, true);
		
		AttributeNode x8 = new AttributeNode("x8");
		x8.setLowClosedMinRange(3, true);
		x8.setUpClosedMinRange(Double.POSITIVE_INFINITY, true);
		
		// expected nodes
		AttributeNode expectedx1 = new AttributeNode("x1");
		expectedx1.setLowClosedMinRange(Double.NEGATIVE_INFINITY, true);
		expectedx1.setUpClosedMinRange(5, true);
		expectedx1.setLowRealMinRange(Double.NEGATIVE_INFINITY, true);
		expectedx1.setUpRealMinRange(5, true);
		
		AttributeNode expectedx2x3 = new AttributeNode("x2x3");
		expectedx2x3.setLowClosedMinRange(1, true);
		expectedx2x3.setUpClosedMinRange(5, false);
		expectedx2x3.setLowRealMinRange(1, true);
		expectedx2x3.setUpRealMinRange(5, false);
		
		AttributeNode expectedx4 = new AttributeNode("x4");
		expectedx4.setLowClosedMinRange(4, false);
		expectedx4.setUpClosedMinRange(6, false);
		expectedx4.setLowRealMinRange(4, false);
		expectedx4.setUpRealMinRange(6, false);
		
		AttributeNode expectedx5 = new AttributeNode("x5");
		expectedx5.setLowClosedMinRange(2, false);
		expectedx5.setUpClosedMinRange(6, false);
		expectedx5.setLowRealMinRange(2, false);
		expectedx5.setUpRealMinRange(6, true);
		
		AttributeNode expectedx6 = new AttributeNode("x6");
		expectedx6.setLowClosedMinRange(5, false);
		expectedx6.setUpClosedMinRange(12, false);
		expectedx6.setLowRealMinRange(5, false);
		expectedx6.setUpRealMinRange(8, true);
		
		AttributeNode expectedx7 = new AttributeNode("x7");
		expectedx7.setLowClosedMinRange(1, false);
		expectedx7.setUpClosedMinRange(8, true);
		expectedx7.setLowRealMinRange(5, false);
		expectedx7.setUpRealMinRange(8, true);
				
		AttributeNode expectedx8 = new AttributeNode("x8");
		expectedx8.setLowClosedMinRange(3, true);
		expectedx8.setUpClosedMinRange(Double.POSITIVE_INFINITY, true);
		expectedx8.setLowRealMinRange(3, true);
		expectedx8.setUpRealMinRange(8, true);
		
		// add edges to computed graph
		ldg.addEdge(x1, x2x3, "<");
		ldg.addEdge(x1, x5, "<");
		ldg.addEdge(x1, x8, "<=");
		ldg.addEdge(x2x3, x4, "<=");
		ldg.addEdge(x4, x7, "<");
		ldg.addEdge(x5, x4, "<");
		ldg.addEdge(x5, x6, "<");
		ldg.addEdge(x6, x7, "<=");
		ldg.addEdge(x8, x6, "<=");
		
		// add edges to expected graph
		expectedLdg.addEdge(expectedx1, expectedx2x3, "<");
		expectedLdg.addEdge(expectedx1, expectedx5, "<");
		expectedLdg.addEdge(expectedx1, expectedx8, "<=");
		expectedLdg.addEdge(expectedx2x3, expectedx4, "<=");
		expectedLdg.addEdge(expectedx4, expectedx7, "<");
		expectedLdg.addEdge(expectedx5, expectedx4, "<");
		expectedLdg.addEdge(expectedx5, expectedx6, "<");
		expectedLdg.addEdge(expectedx6, expectedx7, "<=");
		expectedLdg.addEdge(expectedx8, expectedx6, "<=");
		

		try {
			ldg.computeRealMinRanges();
		} catch (CycleFoundException e) {
			fail();
		}
		
		assertEquals(expectedLdg, ldg);
	}
	
	public void testThrowCycleExceptionIfComputeMinRealRangesWithCycle()
	{
		LabeledDirectedGraph ldg = new LabeledDirectedGraph();
		
		AttributeNode x1 = new AttributeNode("x1");
		x1.setLowClosedMinRange(Double.NEGATIVE_INFINITY, true);
		x1.setUpClosedMinRange(5, true);
		
		AttributeNode x2 = new AttributeNode("x2");
		x2.setLowClosedMinRange(1, true);
		x2.setUpClosedMinRange(5, false);
		
		AttributeNode x3 = new AttributeNode("x3");
		x3.setLowClosedMinRange(1, false);
		x3.setUpClosedMinRange(7, false);
		
		AttributeNode x4 = new AttributeNode("x4");
		x4.setLowClosedMinRange(4, false);
		x4.setUpClosedMinRange(6, false);
		
		// add edges to computed graph
		ldg.addEdge(x1, x2, "<");
		ldg.addEdge(x2, x3, "<=");
		ldg.addEdge(x3, x2, "<=");
		ldg.addEdge(x3, x4, "<=");
		
		try {
			ldg.computeRealMinRanges();
			fail();
		} catch (CycleFoundException e) {
			assertTrue(true);
		}
	}
	
	public void testCheckRealMinRangesMethod()
	{
		LabeledDirectedGraph ldg = new LabeledDirectedGraph();
		
		// two correct nodes
		AttributeNode x1 = new AttributeNode("x1");
		x1.setLowClosedMinRange(Double.NEGATIVE_INFINITY, true);
		x1.setUpClosedMinRange(5, true);
		x1.setLowRealMinRange(Double.NEGATIVE_INFINITY, true);
		x1.setUpRealMinRange(5, true);
		
		AttributeNode x2 = new AttributeNode("x2");
		x2.setLowClosedMinRange(1, true);
		x2.setUpClosedMinRange(5, false);
		x2.setLowRealMinRange(1, true);
		x2.setUpRealMinRange(5, false);
		
		ldg.addEdge(x1, x2, "<");
		
		assertTrue(ldg.areValidRealMinRangesInRealDomain());
		
	}

	public void testCheckRealMinRangesMethodReturnsFalseIfInvalidNodeCase1()
	{
		LabeledDirectedGraph ldg = new LabeledDirectedGraph();
		
		AttributeNode x1 = new AttributeNode("x1");
		x1.setLowClosedMinRange(Double.NEGATIVE_INFINITY, true);
		x1.setUpClosedMinRange(5, true);
		x1.setLowRealMinRange(Double.NEGATIVE_INFINITY, true);
		x1.setUpRealMinRange(5, true);
		
		//invalid node:
		AttributeNode x2 = new AttributeNode("x2x3");
		x2.setLowClosedMinRange(1, true);
		x2.setUpClosedMinRange(5, false);
		x2.setLowRealMinRange(1, true);// open bound for equal values
		x2.setUpRealMinRange(1, false);
		
		ldg.addEdge(x1, x2, "<");
		
		assertFalse(ldg.areValidRealMinRangesInRealDomain());
		
	}
	
	public void testCheckRealMinRangesMethodReturnsFalseIfInvalidNodeCase2()
	{
		LabeledDirectedGraph ldg = new LabeledDirectedGraph();
		
		AttributeNode x1 = new AttributeNode("x1");
		x1.setLowClosedMinRange(Double.NEGATIVE_INFINITY, true);
		x1.setUpClosedMinRange(5, true);
		x1.setLowRealMinRange(Double.NEGATIVE_INFINITY, true);
		x1.setUpRealMinRange(5, true);
		
		//invalid node:
		AttributeNode x2 = new AttributeNode("x2");
		x2.setLowClosedMinRange(1, true);
		x2.setUpClosedMinRange(5, false);
		x2.setLowRealMinRange(5, true); // flower bound > upperbound
		x2.setUpRealMinRange(1, false);
		
		ldg.addEdge(x1, x2, "<");
		
		assertFalse(ldg.areValidRealMinRangesInRealDomain());
		
	}
	
}
