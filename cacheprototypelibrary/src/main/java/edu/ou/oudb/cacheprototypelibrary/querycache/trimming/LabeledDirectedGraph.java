package edu.ou.oudb.cacheprototypelibrary.querycache.trimming;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import android.util.Pair;

import edu.ou.oudb.cacheprototypelibrary.querycache.exception.CycleFoundException;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.XopCPredicate;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.XopYPredicate;

public class LabeledDirectedGraph {

    private LinkedHashMap<AttributeNode, LinkedHashMap<AttributeNode, String>> mSuccessors;
    private LinkedHashMap<AttributeNode, LinkedHashMap<AttributeNode, String>> mPredecessors;
    private int mNbNode = 0;

    private LinkedHashMap<String, Integer> transitiveClosureIndex;

    private HashMap<String, AttributeNode> mNodesIndex;

    private boolean[][] transitiveClosure = null;

    //used to remember if at least one "<" has been found between X and Y
    private boolean[][] transitiveClosureLt = null;

    public LabeledDirectedGraph() {
        mSuccessors = new LinkedHashMap<AttributeNode, LinkedHashMap<AttributeNode, String>>();
        mPredecessors = new LinkedHashMap<AttributeNode, LinkedHashMap<AttributeNode, String>>();
        transitiveClosureIndex = new LinkedHashMap<String, Integer>();
        mNodesIndex = new LinkedHashMap<String, AttributeNode>();
    }

    public void clear() {
        mSuccessors.clear();
        mPredecessors.clear();
        transitiveClosureIndex.clear();
        mNodesIndex.clear();
    }


    public void addNode(AttributeNode node) {
        if (!mSuccessors.containsKey(node)) {
            mSuccessors.put(node, new LinkedHashMap<AttributeNode, String>());
        }

        if (!mPredecessors.containsKey(node)) {
            mPredecessors.put(node, new LinkedHashMap<AttributeNode, String>());
            System.out.println("");
        }

        if (!transitiveClosureIndex.containsKey(node.getAttribute())) {
            transitiveClosureIndex.put(node.getAttribute(), mNbNode++);
        }

        if (!mNodesIndex.containsKey(node.getAttribute())) {
            /*Puts the bounds in the index*/
            mNodesIndex.put(node.getAttribute(), node);
            System.out.println("Bounds: " + mNodesIndex.values());
        }
    }

    public void addAllNodes(Collection<AttributeNode> nodes) {
        for (AttributeNode node : nodes) {
            addNode(node);
        }
    }

    /**
     * Method used to add an edge to the graph
     *
     * @param src      the source node of the edge
     * @param dest     the destination node of the edge
     * @param operator the operator of the graph
     */
    public void addEdge(AttributeNode src, AttributeNode dest, String operator) {
        String oldOperator = null;
        if (src != dest) //this graph does not allow edges with one node
        {
            //we create the nodes if they do not exist
            if ((!mSuccessors.containsKey(src)) || (!mPredecessors.containsKey(src))) {
                addNode(src);
            }
            if ((!mSuccessors.containsKey(dest)) || (!mPredecessors.containsKey(dest))) {
                addNode(dest);
            }

            // if the edge already exist
            if (mSuccessors.get(src).containsKey(dest)) {
                // override the operator if open bound
                oldOperator = mSuccessors.get(src).get(dest);
                if (oldOperator == "<=" && operator == "<") {
                    mSuccessors.get(src).put(dest, "<");
                    mPredecessors.get(dest).put(src, "<");
                } else if (oldOperator == ">=" && operator == ">") {
                    mSuccessors.get(src).put(dest, ">");
                    mPredecessors.get(dest).put(src, ">");
                }
                // else do nothing since edge is already there
            } else {
                mSuccessors.get(src).put(dest, operator);
                mPredecessors.get(dest).put(src, operator);
            }
        }
    }

    /**
     * Given a node, this method returns all the edges starting from that node
     *
     * @param node the root node
     * @return the edges (destination, operator)
     */
    public Set<Entry<AttributeNode, String>> getEdges(AttributeNode node) {
        return mSuccessors.get(node).entrySet();
    }

    public Set<AttributeNode> getSuccessors(AttributeNode node) {
        return mSuccessors.get(node).keySet();
    }

    public Set<AttributeNode> getPredecessors(AttributeNode node) {
        return mPredecessors.get(node).keySet();
    }

    /**
     * method getting the set of graph nodes
     *
     * @return the graph nodes
     */
    public Set<AttributeNode> getNodes() {
        return mSuccessors.keySet();
    }

    /**
     * Method used to know if the node is within the graph
     *
     * @param node the looked up node
     * @return true if found, false otherwise
     */
    public boolean containsNode(AttributeNode node) {
        return mSuccessors.containsKey(node);
    }


    /**
     * Method used to return the graph with inversed edges
     *
     * @return the transposed graph
     */
    public LabeledDirectedGraph getTransposedGraph() {
        LabeledDirectedGraph transposedGraph = new LabeledDirectedGraph();

        transposedGraph.setSuccessors(mPredecessors);
        transposedGraph.setPredecessors(mSuccessors);

        return transposedGraph;
    }


    private boolean[][] buildTransitiveClosure() {
        if (transitiveClosure == null) {
            transitiveClosure = new boolean[mNbNode][mNbNode];
            transitiveClosureLt = new boolean[mNbNode][mNbNode];
            int curIndex;
            for (AttributeNode n : getNodes()) {
                curIndex = transitiveClosureIndex.get(n.getAttribute());
                transitiveClosure[curIndex][curIndex] = true;
                depthFirstSearchClosure(n);
            }
        }

        return getTransitiveClosure();
    }

    private void depthFirstSearchClosure(AttributeNode root) {
        Stack<Pair<AttributeNode, Boolean>> stack = new Stack<Pair<AttributeNode, Boolean>>();
        HashSet<AttributeNode> visited = new HashSet<AttributeNode>();
        Pair<AttributeNode, Boolean> curNode;
        int curRootIndex = transitiveClosureIndex.get(root.getAttribute());
        int curChildIndex;

        stack.push(new Pair<AttributeNode, Boolean>(root, false));
        while (!stack.isEmpty()) {
            curNode = stack.pop();
            if (!visited.contains(curNode.first)) {
                visited.add(curNode.first);
                stack.push(curNode);

                for (Entry<AttributeNode, String> e : getEdges(curNode.first)) {
                    curChildIndex = transitiveClosureIndex.get(e.getKey().getAttribute());
                    transitiveClosure[curRootIndex][curChildIndex] = true;
                    transitiveClosureLt[curRootIndex][curChildIndex] = (curNode.second || e.getValue() == "<");
                    stack.push(new Pair<AttributeNode, Boolean>(e.getKey(), transitiveClosureLt[curRootIndex][curChildIndex]));
                }
            }
        }
    }

    public boolean impliesPredicateRealDomain(XopYPredicate p) {
        boolean impliesPredicate = false;
        int leftOperandIndex;
        AttributeNode curLeftNode;
        int rightOperandIndex;
        AttributeNode curRightNode;

        if (mNodesIndex.containsKey(p.getLeftOperand()) && mNodesIndex.containsKey(p.getRightOperand())) {

            if (transitiveClosure == null || transitiveClosureLt == null) {
                buildTransitiveClosure();
            }

            curLeftNode = mNodesIndex.get(p.getLeftOperand());
            curRightNode = mNodesIndex.get(p.getRightOperand());

            leftOperandIndex = transitiveClosureIndex.get(curLeftNode.getAttribute());
            rightOperandIndex = transitiveClosureIndex.get(curRightNode.getAttribute());

            switch (p.getOperator()) {
                case "<=":
                    impliesPredicate = transitiveClosure[leftOperandIndex][rightOperandIndex]
                            || curLeftNode.getUpRealMinRange() <= curRightNode.getLowRealMinRange();
                    break;
                case "<":
                    impliesPredicate = (transitiveClosure[leftOperandIndex][rightOperandIndex])
                            && (transitiveClosureLt[leftOperandIndex][rightOperandIndex])
                            || (curLeftNode.getUpRealMinRange() < curRightNode.getLowRealMinRange())
                            || ((curLeftNode.getUpRealMinRange() < curRightNode.getLowRealMinRange())
                            && ((curLeftNode.isUpRealMinRangeOpenBound())
                            || (curRightNode.isLowRealMinRangeOpenBound()))
                    );
                    break;
                default:
                    impliesPredicate = false;
                    break;
            }
        }

        return impliesPredicate;
    }

    public boolean impliesPredicateIntegerDomain(XopCPredicate p) {
        boolean impliesPredicate = false;

        AttributeNode curLeftNode;
        double curRightOperand;

        if (mNodesIndex.containsKey(p.getLeftOperand())) {
            if (transitiveClosure == null || transitiveClosureLt == null) {
                buildTransitiveClosure();
            }

            curLeftNode = mNodesIndex.get(p.getLeftOperand());
            curRightOperand = p.getRightOperand();

            switch (p.getOperator()) {
                case "<=":
                    /*Checks if upBound <= rightOperand (input value)
                    * Display those two to see what's inside*/
                    impliesPredicate = (curLeftNode.getUpRealMinRange() <= curRightOperand);
                    break;
                case ">=":
                    impliesPredicate = (curLeftNode.getLowRealMinRange() >= curRightOperand);
                    break;
                default:
                    impliesPredicate = false;
                    break;
            }
        }

        return impliesPredicate;
    }

    public boolean impliesPredicateRealDomain(XopCPredicate p) {
        boolean impliesPredicate = false;

        AttributeNode curLeftNode;
        double curRightOperand;

        if (mNodesIndex.containsKey(p.getLeftOperand())) {
            if (transitiveClosure == null || transitiveClosureLt == null) {
                buildTransitiveClosure();
            }

            curLeftNode = mNodesIndex.get(p.getLeftOperand());
            curRightOperand = p.getRightOperand();

            switch (p.getOperator()) {
                case "<=":
                    impliesPredicate = (curLeftNode.getUpRealMinRange() <= curRightOperand);
                    break;
                case "<":
                    impliesPredicate = (curLeftNode.getUpRealMinRange() < curRightOperand)
                            || (curLeftNode.getUpRealMinRange() == curRightOperand)
                            && (curLeftNode.isUpRealMinRangeOpenBound());
                    break;
                case ">=":
                    impliesPredicate = (curLeftNode.getLowRealMinRange() >= curRightOperand);
                    break;
                case ">":
                    impliesPredicate = (curLeftNode.getLowRealMinRange() > curRightOperand)
                            || (curLeftNode.getLowRealMinRange() == curRightOperand)
                            && (curLeftNode.isLowRealMinRangeOpenBound());
                    break;
                default:
                    impliesPredicate = false;
                    break;
            }
        }

        return impliesPredicate;
    }

    /**
     * Browse the graph and add the visited nodes to the visited set
     * to find the order of browsing
     *
     * @param rootNode the node where the DFS algorithm starts
     * @param visited  the set of visited nodes
     * @return the stack of nodes in the order that the DFS finished exploring them
     */
    private Stack<AttributeNode> findFinishedExplorationOrder(AttributeNode rootNode, HashSet<AttributeNode> visited) {
        Stack<AttributeNode> stack = new Stack<AttributeNode>();
        Stack<AttributeNode> visitOrder = new Stack<AttributeNode>();
        AttributeNode curNode = null;

        stack.push(rootNode);
        while (!stack.isEmpty()) {
            curNode = stack.pop();
            if (!visited.contains(curNode)) {
                visited.add(curNode);
                stack.push(curNode);
                for (Entry<AttributeNode, String> e : getEdges(curNode)) {
                    if (!stack.contains(e.getKey()) && !visitOrder.contains(e.getKey()))
                        stack.push(e.getKey());
                }
            } else //visited.contains(curNode)
            {
                visitOrder.push(curNode);
            }
        }

        return visitOrder;
    }


    /**
     * Browse the graph and add the visited nodes to the visited set
     * to find the SCC
     *
     * @param rootNode the node where the DFS algorithm starts
     * @param visited  the set of visited nodes in previous DFS
     * @return the SCC
     */
    private LinkedHashSet<AttributeNode> findSCC(AttributeNode rootNode, HashSet<AttributeNode> visited) {
        Stack<AttributeNode> stack = new Stack<AttributeNode>();
        LinkedHashSet<AttributeNode> scc = new LinkedHashSet<AttributeNode>();
        AttributeNode curNode = null;
        boolean isValidSCC = true;

        stack.push(rootNode);
        while (!stack.isEmpty() && isValidSCC) {
            curNode = stack.pop();
            if (!visited.contains(curNode) && !scc.contains(curNode)) {
                scc.add(curNode);
                visited.add(curNode);
                Iterator<Entry<AttributeNode, String>> it = getEdges(curNode).iterator();
                Entry<AttributeNode, String> e = null;
                while (it.hasNext() && isValidSCC) {
                    e = it.next();
                    if ((scc.contains(e.getKey()) || !visited.contains(e.getKey()))
                            && e.getValue() == "<") {
                        isValidSCC = false;
                    } else {
                        stack.push(e.getKey());
                    }
                }
            }
        }

        if (!isValidSCC) {
            scc = null;
        }

        return scc;
    }

    /**
     * Given one set of Strong Connected Components, this method merges
     * them into one node, respecting lowerBound and upperBound rules
     *
     * @param sccSet Strong Connected Components set
     * @return the aggregation node of all the SCCs in the set.
     */
    public AttributeNode mergeNodes(HashSet<AttributeNode> sccSet) {
        AttributeNode node = null;
        StringBuilder sb = new StringBuilder();
        double minLowRange = Double.NEGATIVE_INFINITY;
        double minUpRange = Double.POSITIVE_INFINITY;
        boolean minLowOpenBound = false;
        boolean minUpOpenBound = false;

        for (AttributeNode n : sccSet) {
            sb.append(n.getAttribute());
            if (n.getLowClosedMinRange() >= minLowRange) {
                // if same number, get the open bound if there is one in the
                // list of ssc's open bounds
                if (n.getLowClosedMinRange() == minLowRange) {
                    minLowOpenBound = n.isLowClosedMinRangeOpenBound() || minLowOpenBound;
                } else {
                    minLowRange = n.getLowClosedMinRange();
                    minLowOpenBound = n.isLowClosedMinRangeOpenBound();
                }
            }

            if (n.getUpClosedMinRange() <= minUpRange) {
                // if same number, get the open bound if there is one in the
                // list of ssc's open bounds
                if (n.getUpClosedMinRange() == minUpRange) {
                    minUpOpenBound = n.isUpClosedMinRangeOpenBound() || minUpOpenBound;
                } else {
                    minUpRange = n.getUpClosedMinRange();
                    minUpOpenBound = n.isUpClosedMinRangeOpenBound();
                }
            }
        }

        node = new AttributeNode(sb.toString());
        node.setLowClosedMinRange(minLowRange, minLowOpenBound);
        node.setUpClosedMinRange(minUpRange, minUpOpenBound);

        return node;
    }

    /**
     * Method used to build the acyclic graph given the list of SCC
     *
     * @param mapSCC the map OldNode -> NewNode
     * @return the acyclic labeled directed graph
     */
    private LabeledDirectedGraph buildCollapsedGraph(HashMap<AttributeNode, AttributeNode> mapSCC) {
        LabeledDirectedGraph collapsedGraph = new LabeledDirectedGraph();
        HashMap<String, AttributeNode> nodesIndex = new HashMap<String, AttributeNode>();


        // for each node in the graph
        for (AttributeNode n : getNodes()) {
            // get the correponding new node
            AttributeNode newNode = mapSCC.get(n);

            //add the new node to the collapsed graph.
            collapsedGraph.addNode(newNode);

            //update the index to have the non-merged nodes pointing to the merged nodes
            nodesIndex.put(n.getAttribute(), newNode);

            // for each edges starting form this node.
            for (Entry<AttributeNode, String> e : getEdges(n)) {
                //add an edge from the new node to the corresponding node in the SCC list
                collapsedGraph.addEdge(newNode, mapSCC.get(e.getKey()), e.getValue());
            }
        }

        collapsedGraph.setNodesIndex(nodesIndex);

        return collapsedGraph;
    }

    /**
     * Look for the SCCs and build the acyclic graph from the current graph
     *
     * @return the acyclic labeled directed graph
     */
    public LabeledDirectedGraph getCollapsedGraph() {
        Stack<AttributeNode> stack = new Stack<AttributeNode>();
        HashSet<AttributeNode> visited = new HashSet<AttributeNode>();
        HashSet<AttributeNode> curSCC = null;
        HashMap<AttributeNode, AttributeNode> mapSCC = new HashMap<AttributeNode, AttributeNode>();
        Set<AttributeNode> nodeSet = getNodes();
        LabeledDirectedGraph transposedGraph = null;
        LabeledDirectedGraph collapsedGraph = null;

        //search for SCCs
        for (AttributeNode n : nodeSet) {
            if (!visited.contains(n)) {
                //to be tested: supposed to merge the two stacks
                stack.addAll(findFinishedExplorationOrder(n, visited));
            }
        }

        visited.clear();
        transposedGraph = getTransposedGraph();

        AttributeNode curNode;
        curSCC = new HashSet<AttributeNode>();
        while (!stack.isEmpty() && curSCC != null) {
            curNode = stack.pop();
            curSCC = transposedGraph.findSCC(curNode, visited);

            //merge scc
            if (curSCC != null) {
                AttributeNode mergedNode = mergeNodes(curSCC);
                for (AttributeNode n : curSCC) {
                    mapSCC.put(n, mergedNode);
                }
            }
        }

        // if no errors
        if (curSCC != null) {
            collapsedGraph = buildCollapsedGraph(mapSCC);
        }
        //else collapsedGraph = null

        return collapsedGraph;
    }

    /**
     * Method used to compute the Alow, Aup for each node of the graph
     *
     * @return true if success, false otherwise
     * @throws CycleFoundException
     */
    public void computeRealMinRanges() throws CycleFoundException {
        HashSet<AttributeNode> visited = new HashSet<AttributeNode>();
        List<AttributeNode> order = new ArrayList<AttributeNode>();
        Stack<AttributeNode> curOrder = null;
        Set<AttributeNode> nodeSet = getNodes();
        AttributeNode curNode = null;
        Entry<AttributeNode, String> curParent = null;
        Entry<AttributeNode, String> curChild = null;

        Iterator<AttributeNode> it = nodeSet.iterator();
        while (it.hasNext()) {
            curNode = it.next();
            if (!visited.contains(curNode)) {
                curOrder = topologicalSort(curNode, visited);
                order.addAll(0, curOrder);
            }
        }

        Iterator<Entry<AttributeNode, String>> itParent = null;
        Iterator<Entry<AttributeNode, String>> itChild = null;
        double max, min;
        boolean openBound;
        // set the lower bounds
        int nbNodes = order.size();

        for (int i = nbNodes - 1; i >= 0; --i) {

            curNode = order.get(i);

            // set the first node with the close lower minimum range
            curNode.setLowRealMinRange(curNode.getLowClosedMinRange(), curNode.isLowClosedMinRangeOpenBound());

            //browse the edges to get Alow(X) = max(Alow(Xi),Clow(X))
            // Xi are all the parents for X
            itParent = mPredecessors.get(curNode).entrySet().iterator();
            while (itParent.hasNext()) {
                curParent = itParent.next();
                if (curParent.getKey().getLowRealMinRange() > curNode.getLowRealMinRange()) {
                    max = curParent.getKey().getLowRealMinRange();
                    openBound = curParent.getValue() != "<=" || curParent.getKey().isLowRealMinRangeOpenBound();
                    curNode.setLowRealMinRange(max, openBound);
                } else if (curParent.getKey().getLowRealMinRange() == curNode.getLowRealMinRange()) {
                    max = curNode.getLowRealMinRange();
                    openBound = curParent.getValue() != "<=" || curParent.getKey().isLowRealMinRangeOpenBound() || curNode.isLowRealMinRangeOpenBound();
                    curNode.setLowRealMinRange(max, openBound);
                }
            }
        }

        for (int i = 0; i < nbNodes; ++i) {
            curNode = order.get(i);
            // set the first node with the close upper minimum range
            curNode.setUpRealMinRange(curNode.getUpClosedMinRange(), curNode.isUpClosedMinRangeOpenBound());

            //browse the edges to get Aup(X) = min(Aup(Xi),Cup(X))
            // Xi are all the children for X
            itChild = mSuccessors.get(curNode).entrySet().iterator();
            while (itChild.hasNext()) {
                curChild = itChild.next();
                if (curChild.getKey().getUpRealMinRange() < curNode.getUpRealMinRange()) {
                    min = curChild.getKey().getUpRealMinRange();
                    openBound = curChild.getValue() != "<=" || curChild.getKey().isUpRealMinRangeOpenBound();
                    curNode.setUpRealMinRange(min, openBound);
                } else if (curChild.getKey().getUpRealMinRange() == curNode.getUpRealMinRange()) {
                    min = curNode.getUpRealMinRange();
                    openBound = curChild.getValue() != "<=" || curChild.getKey().isUpRealMinRangeOpenBound() || curNode.isUpRealMinRangeOpenBound();
                    curNode.setUpRealMinRange(min, openBound);
                }
            }
        }

    }

    /**
     * Method returning the topological sort for a DAG
     *
     * @param rootNode the starting node
     * @param visited  the set of visited nodes
     * @return the topological sort, or null if cycle.
     * @throws CycleFoundException thrown if cycle
     */
    private Stack<AttributeNode> topologicalSort(AttributeNode rootNode, HashSet<AttributeNode> visited) throws CycleFoundException {
        Stack<AttributeNode> stack = new Stack<AttributeNode>();
        Stack<AttributeNode> visitOrder = new Stack<AttributeNode>();
        AttributeNode curNode = null;

        stack.push(rootNode);
        while (!stack.isEmpty()) {
            curNode = stack.pop();
            if (!visited.contains(curNode)) {
                visited.add(curNode);
                stack.push(curNode);
                Iterator<Entry<AttributeNode, String>> it = getEdges(curNode).iterator();
                Entry<AttributeNode, String> e = null;
                while (it.hasNext()) {
                    e = it.next();

                    // if there is a cycle
                    if (stack.contains(e.getKey())) {
                        throw new CycleFoundException();
                    } else if (!visitOrder.contains(e.getKey())) {
                        stack.push(e.getKey());
                    }
                }
            } else //visited.contains(curNode)
            {
                visitOrder.push(curNode);
            }
        }

        return visitOrder;
    }


    public boolean areValidRealMinRangesInRealDomain() {
        boolean satisfiable = true;

        Iterator<AttributeNode> it = getNodes().iterator();
        AttributeNode curNode = null;

        while (it.hasNext() && satisfiable) {
            curNode = it.next();
            if (!curNode.isValidInRealDomain()) {
                satisfiable = false;
            }
        }

        return satisfiable;
    }


    public boolean areValidRealMinRangesInIntegerDomain() {
        boolean satisfiable = true;

        Iterator<AttributeNode> it = getNodes().iterator();
        AttributeNode curNode = null;

        while (it.hasNext() && satisfiable) {
            curNode = it.next();
            if (!curNode.isValidInIntegerDomain()) {
                satisfiable = false;
            }
        }

        return satisfiable;
    }


    /**
     * @return the successors
     */
    public final LinkedHashMap<AttributeNode, LinkedHashMap<AttributeNode, String>> getSuccessors() {
        return this.mSuccessors;
    }


    /**
     * @param successors the successors to set
     */
    public final void setSuccessors(
            LinkedHashMap<AttributeNode, LinkedHashMap<AttributeNode, String>> successors) {
        if (successors != null) {
            this.mSuccessors = successors;
        }
    }


    /**
     * @return the predecessors
     */
    public final LinkedHashMap<AttributeNode, LinkedHashMap<AttributeNode, String>> getPredecessors() {
        return this.mPredecessors;
    }


    /**
     * @param predecessors the predecessors to set
     */
    public final void setPredecessors(
            LinkedHashMap<AttributeNode, LinkedHashMap<AttributeNode, String>> predecessors) {
        if (predecessors != null) {
            this.mPredecessors = predecessors;
        }
    }


    /**
     * @return the transitiveClosureIndex
     */
    public final LinkedHashMap<String, Integer> getTransitiveClosureIndex() {
        return this.transitiveClosureIndex;
    }


    /**
     * @param transitiveClosureIndex the transitiveClosureIndex to set
     */
    public final void setTransitiveClosureIndex(LinkedHashMap<String, Integer> transitiveClosureIndex) {
        if (transitiveClosureIndex != null) {
            this.transitiveClosureIndex = transitiveClosureIndex;
        }
    }


    /**
     * @return the transitiveClosure
     */
    public final boolean[][] getTransitiveClosure() {
        return this.transitiveClosure;
    }


    /**
     * @param transitiveClosure the transitiveClosure to set
     */
    public final void setTransitiveClosure(boolean[][] transitiveClosure) {
        if (transitiveClosure != null) {
            this.transitiveClosure = transitiveClosure;
        }
    }


    /**
     * @return the transitiveClosureLt
     */
    public final boolean[][] getTransitiveClosureLt() {
        return this.transitiveClosureLt;
    }


    /**
     * @param transitiveClosureLt the transitiveClosureLt to set
     */
    public final void setTransitiveClosureLt(boolean[][] transitiveClosureLt) {
        if (transitiveClosureLt != null) {
            this.transitiveClosureLt = transitiveClosureLt;
        }
    }


    /**
     * @return the nodesIndex
     */
    public final HashMap<String, AttributeNode> getNodesIndex() {
        return this.mNodesIndex;
    }


    /**
     * @param nodesIndex the nodesIndex to set
     */
    public final void setNodesIndex(HashMap<String, AttributeNode> nodesIndex) {
        if (nodesIndex != null) {
            this.mNodesIndex = nodesIndex;
        }
    }


    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mSuccessors == null) ? 0 : mSuccessors.hashCode());
        return result;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LabeledDirectedGraph other = (LabeledDirectedGraph) obj;
        if (mSuccessors == null) {
            if (other.mSuccessors != null)
                return false;
        } else if (!mSuccessors.equals(other.mSuccessors))
            return false;
        return true;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "LabeledDirectedGraph [mNodes = " + mSuccessors + "]";
    }

}
