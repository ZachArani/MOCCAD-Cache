package edu.ou.oudb.cacheprototypelibrary.querycache.trimming;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import edu.ou.oudb.cacheprototypelibrary.querycache.exception.CycleFoundException;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.XopYPredicate;

/**
 * Created by chenxiao on 6/16/17.
 */

/*Graph used for the Strings
* Refer to LabeledDirectedGraph for information on the methods*/
public class LabeledDirectedStringsGraph extends LabeledDirectedGraph{

    private HashMap<String, AttributeNodeS> mNodesIndex;
    private LinkedHashMap<AttributeNodeS, LinkedHashMap<AttributeNodeS, String>> mSuccessors;
    private LinkedHashMap<AttributeNodeS, LinkedHashMap<AttributeNodeS, String>> mPredecessors;
    private int mNbNode = 0;

    private LinkedHashMap<String, Integer> transitiveClosureIndex;
    private boolean[][] transitiveClosure = null;
    private boolean[][] transitiveClosureLt = null;

    public LabeledDirectedStringsGraph(){
        super();
        mNodesIndex = new LinkedHashMap<String, AttributeNodeS>();
        mSuccessors = new LinkedHashMap<AttributeNodeS, LinkedHashMap<AttributeNodeS, String>>();
        mPredecessors = new LinkedHashMap<AttributeNodeS, LinkedHashMap<AttributeNodeS, String>>();
        transitiveClosureIndex = new LinkedHashMap<String, Integer>();
    }

    public void clear(){
        mNodesIndex.clear();
        mSuccessors.clear();
        mPredecessors.clear();
        transitiveClosureIndex.clear();
    }

    public void addNode(AttributeNodeS node) {
        if (!mSuccessors.containsKey(node)) {
            mSuccessors.put(node, new LinkedHashMap<AttributeNodeS, String>());
        }
        if (!mPredecessors.containsKey(node)) {
            mPredecessors.put(node, new LinkedHashMap<AttributeNodeS, String>());
        }
        if (!transitiveClosureIndex.containsKey(node.getAttribute())) {
            transitiveClosureIndex.put(node.getAttribute(), mNbNode++);
        }
        if (!mNodesIndex.containsKey(node.getAttribute())) {
            mNodesIndex.put(node.getAttribute(), node);
            System.out.println("Bounds: " + mNodesIndex.values());
        }
    }

    public void addAllNodesS(Collection<AttributeNodeS> nodes) {
        for (AttributeNodeS node : nodes) {
            addNode(node);
        }
    }

    public Set<AttributeNodeS> getNodesS() {
        return mSuccessors.keySet();
    }

    public Set<Map.Entry<AttributeNodeS, String>> getEdges(AttributeNodeS node) {
        return mSuccessors.get(node).entrySet();
    }

    public final void setSuccessorsS(
            LinkedHashMap<AttributeNodeS, LinkedHashMap<AttributeNodeS, String>> successors) {
        if (successors != null) {
            this.mSuccessors = successors;
        }
    }

    public final void setPredecessorsS(
            LinkedHashMap<AttributeNodeS, LinkedHashMap<AttributeNodeS, String>> predecessors) {
        if (predecessors != null) {
            this.mPredecessors = predecessors;
        }
    }

    private boolean[][] buildTransitiveClosure() {
        if (transitiveClosure == null) {
            transitiveClosure = new boolean[mNbNode][mNbNode];
            transitiveClosureLt = new boolean[mNbNode][mNbNode];
            int curIndex;
            for (AttributeNodeS n : getNodesS()) {
                curIndex = transitiveClosureIndex.get(n.getAttribute());
                transitiveClosure[curIndex][curIndex] = true;
                depthFirstSearchClosure(n);
            }
        }

        return getTransitiveClosure();
    }

    private void depthFirstSearchClosure(AttributeNodeS root) {
        Stack<Pair<AttributeNodeS, Boolean>> stack = new Stack<Pair<AttributeNodeS, Boolean>>();
        HashSet<AttributeNodeS> visited = new HashSet<AttributeNodeS>();
        Pair<AttributeNodeS, Boolean> curNode;
        int curRootIndex = transitiveClosureIndex.get(root.getAttribute());
        int curChildIndex;

        stack.push(new Pair<AttributeNodeS, Boolean>(root, false));
        while (!stack.isEmpty()) {
            curNode = stack.pop();
            if (!visited.contains(curNode.first)) {
                visited.add(curNode.first);
                stack.push(curNode);

                for (Map.Entry<AttributeNodeS, String> e : getEdges(curNode.first)) {
                    curChildIndex = transitiveClosureIndex.get(e.getKey().getAttribute());
                    transitiveClosure[curRootIndex][curChildIndex] = true;
                    transitiveClosureLt[curRootIndex][curChildIndex] = (curNode.second || e.getValue() == "<");
                    stack.push(new Pair<AttributeNodeS, Boolean>(e.getKey(), transitiveClosureLt[curRootIndex][curChildIndex]));
                }
            }
        }
    }

    public boolean impliesPredicateIntegerDomain(XopYPredicate p) {
        boolean impliesPredicate = false;

        AttributeNodeS curLeftNode;
        String curRightOperand;

        if (mNodesIndex.containsKey(p.getLeftOperand())) {
            if (transitiveClosure == null || transitiveClosureLt == null) {
                buildTransitiveClosure();
            }

            curLeftNode = mNodesIndex.get(p.getLeftOperand());
            curRightOperand = p.getRightOperand().replace("%27", "").replace("%20", " ");

            switch (p.getOperator()) {
                case "<=":
                    impliesPredicate = curLeftNode.getUpRealMinRangeS().compareTo(curRightOperand) <= 0;
                    break;
                case ">=":
                    impliesPredicate = curLeftNode.getLowRealMinRangeS().compareTo(curRightOperand) >= 0;
                    break;
                default:
                    impliesPredicate = false;
                    break;
            }
        }

        return impliesPredicate;
    }

    public LabeledDirectedStringsGraph getCollapsedGraph() {
        Stack<AttributeNodeS> stack = new Stack<AttributeNodeS>();
        HashSet<AttributeNodeS> visited = new HashSet<AttributeNodeS>();
        HashSet<AttributeNodeS> curSCC = null;
        HashMap<AttributeNodeS, AttributeNodeS> mapSCC = new HashMap<AttributeNodeS, AttributeNodeS>();
        Set<AttributeNodeS> nodeSet = getNodesS();
        LabeledDirectedStringsGraph transposedGraph = null;
        LabeledDirectedStringsGraph collapsedGraph = null;

        for (AttributeNodeS n : nodeSet) {
            if (!visited.contains(n)) {
                stack.addAll(findFinishedExplorationOrder(n, visited));
            }
        }

        visited.clear();
        transposedGraph = getTransposedGraph();

        AttributeNodeS curNode;
        curSCC = new HashSet<AttributeNodeS>();
        while (!stack.isEmpty() && curSCC != null) {
            curNode = stack.pop();
            curSCC = transposedGraph.findSCC(curNode, visited);

            //merge scc
            if (curSCC != null) {
                AttributeNodeS mergedNode = mergeNodesS(curSCC);
                for (AttributeNodeS n : curSCC) {
                    mapSCC.put(n, mergedNode);
                }
            }
        }

        // if no errors
        if (curSCC != null) {
            collapsedGraph = buildCollapsedGraph(mapSCC);
        }

        return collapsedGraph;
    }

    private Stack<AttributeNodeS> findFinishedExplorationOrder(AttributeNodeS rootNode, HashSet<AttributeNodeS> visited) {
        Stack<AttributeNodeS> stack = new Stack<AttributeNodeS>();
        Stack<AttributeNodeS> visitOrder = new Stack<AttributeNodeS>();
        AttributeNodeS curNode = null;

        stack.push(rootNode);
        while (!stack.isEmpty()) {
            curNode = stack.pop();
            if (!visited.contains(curNode)) {
                visited.add(curNode);
                stack.push(curNode);
                for (Map.Entry<AttributeNodeS, String> e : getEdges(curNode)) {
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

    public LabeledDirectedStringsGraph getTransposedGraph() {
        LabeledDirectedStringsGraph transposedGraph = new LabeledDirectedStringsGraph();

        transposedGraph.setSuccessorsS(mPredecessors);
        transposedGraph.setPredecessorsS(mSuccessors);

        return transposedGraph;
    }

    private LinkedHashSet<AttributeNodeS> findSCC(AttributeNodeS rootNode, HashSet<AttributeNodeS> visited) {
        Stack<AttributeNodeS> stack = new Stack<AttributeNodeS>();
        LinkedHashSet<AttributeNodeS> scc = new LinkedHashSet<AttributeNodeS>();
        AttributeNodeS curNode = null;
        boolean isValidSCC = true;

        stack.push(rootNode);
        while (!stack.isEmpty() && isValidSCC) {
            curNode = stack.pop();
            if (!visited.contains(curNode) && !scc.contains(curNode)) {
                scc.add(curNode);
                visited.add(curNode);
                Iterator<Map.Entry<AttributeNodeS, String>> it = getEdges(curNode).iterator();
                Map.Entry<AttributeNodeS, String> e = null;
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

    public AttributeNodeS mergeNodesS(HashSet<AttributeNodeS> sccSet) {
        AttributeNodeS node = null;
        StringBuilder sb = new StringBuilder();
        String minLowRange = "A";
        String minUpRange = "Z";
        boolean minLowOpenBound = false;
        boolean minUpOpenBound = false;

        for (AttributeNodeS n : sccSet) {
            sb.append(n.getAttribute());
            if (n.getLowClosedMinRangeS().compareTo(minLowRange) >= 0) {
                // if same string, get the open bound if there is one in the
                // list of ssc's open bounds
                if (n.getLowClosedMinRangeS().equals(minLowRange)) {
                    minLowOpenBound = n.isLowClosedMinRangeOpenBound() || minLowOpenBound;
                } else {
                    minLowRange = n.getLowClosedMinRangeS();
                    minLowOpenBound = n.isLowClosedMinRangeOpenBound();
                }
            }

            if (n.getUpClosedMinRangeS().compareTo(minUpRange) <= 0) {
                // if same string, get the open bound if there is one in the
                // list of ssc's open bounds
                if (n.getUpClosedMinRangeS().equals(minUpRange)) {
                    minUpOpenBound = n.isUpClosedMinRangeOpenBound() || minUpOpenBound;
                } else {
                    minUpRange = n.getUpClosedMinRangeS();
                    minUpOpenBound = n.isUpClosedMinRangeOpenBound();
                }
            }
        }

        node = new AttributeNodeS(sb.toString());
        node.setLowClosedMinRangeS(minLowRange, minLowOpenBound);
        node.setUpClosedMinRangeS(minUpRange, minUpOpenBound);

        return node;
    }

    private LabeledDirectedStringsGraph buildCollapsedGraph(HashMap<AttributeNodeS, AttributeNodeS> mapSCC) {
        LabeledDirectedStringsGraph collapsedGraph = new LabeledDirectedStringsGraph();
        HashMap<String, AttributeNodeS> nodesIndex = new HashMap<String, AttributeNodeS>();

        for (AttributeNodeS n : getNodesS()) {
            AttributeNodeS newNode = mapSCC.get(n);

            collapsedGraph.addNode(newNode);

            nodesIndex.put(n.getAttribute(), newNode);

            for (Map.Entry<AttributeNodeS, String> e : getEdges(n)) {
                collapsedGraph.addEdge(newNode, mapSCC.get(e.getKey()), e.getValue());
            }
        }

        collapsedGraph.setNodesIndexS(nodesIndex);

        return collapsedGraph;
    }

    public final void setNodesIndexS(HashMap<String, AttributeNodeS> nodesIndex) {
        if (nodesIndex != null) {
            this.mNodesIndex = nodesIndex;
        }
    }

    public void addEdge(AttributeNodeS src, AttributeNodeS dest, String operator) {
        String oldOperator = null;
        if (src != dest)
        {
            if ((!mSuccessors.containsKey(src)) || (!mPredecessors.containsKey(src))) {
                addNode(src);
            }
            if ((!mSuccessors.containsKey(dest)) || (!mPredecessors.containsKey(dest))) {
                addNode(dest);
            }

            if (mSuccessors.get(src).containsKey(dest)) {
                oldOperator = mSuccessors.get(src).get(dest);
                if (oldOperator == "<=" && operator == "<") {
                    mSuccessors.get(src).put(dest, "<");
                    mPredecessors.get(dest).put(src, "<");
                } else if (oldOperator == ">=" && operator == ">") {
                    mSuccessors.get(src).put(dest, ">");
                    mPredecessors.get(dest).put(src, ">");
                }
            } else {
                mSuccessors.get(src).put(dest, operator);
                mPredecessors.get(dest).put(src, operator);
            }
        }
    }

    public void computeRealMinRanges() throws CycleFoundException {
        HashSet<AttributeNodeS> visited = new HashSet<AttributeNodeS>();
        List<AttributeNodeS> order = new ArrayList<AttributeNodeS>();
        Stack<AttributeNodeS> curOrder = null;
        Set<AttributeNodeS> nodeSet = getNodesS();
        AttributeNodeS curNode = null;
        Map.Entry<AttributeNodeS, String> curParent = null;
        Map.Entry<AttributeNodeS, String> curChild = null;

        Iterator<AttributeNodeS> it = nodeSet.iterator();
        while (it.hasNext()) {
            curNode = it.next();
            if (!visited.contains(curNode)) {
                curOrder = topologicalSort(curNode, visited);
                order.addAll(0, curOrder);
            }
        }

        Iterator<Map.Entry<AttributeNodeS, String>> itParent = null;
        Iterator<Map.Entry<AttributeNodeS, String>> itChild = null;
        String max, min;
        boolean openBound;
        int nbNodes = order.size();

        for (int i = nbNodes - 1; i >= 0; --i) {

            curNode = order.get(i);

            curNode.setLowRealMinRangeS(curNode.getLowClosedMinRangeS(), curNode.isLowClosedMinRangeOpenBound());

            itParent = mPredecessors.get(curNode).entrySet().iterator();
            while (itParent.hasNext()) {
                curParent = itParent.next();
                if (curParent.getKey().getLowRealMinRangeS().compareTo(curNode.getLowRealMinRangeS()) > 0) {
                    max = curParent.getKey().getLowRealMinRangeS();
                    openBound = curParent.getValue() != "<=" || curParent.getKey().isLowRealMinRangeOpenBound();
                    curNode.setLowRealMinRangeS(max, openBound);
                } else if (curParent.getKey().getLowRealMinRangeS().equals(curNode.getLowRealMinRangeS())) {
                    max = curNode.getLowRealMinRangeS();
                    openBound = curParent.getValue() != "<=" || curParent.getKey().isLowRealMinRangeOpenBound() || curNode.isLowRealMinRangeOpenBound();
                    curNode.setLowRealMinRangeS(max, openBound);
                }
            }
        }

        for (int i = 0; i < nbNodes; ++i) {
            curNode = order.get(i);
            curNode.setUpRealMinRangeS(curNode.getUpClosedMinRangeS(), curNode.isUpClosedMinRangeOpenBound());

            itChild = mSuccessors.get(curNode).entrySet().iterator();
            while (itChild.hasNext()) {
                curChild = itChild.next();
                if (curChild.getKey().getUpRealMinRangeS().compareTo(curNode.getUpRealMinRangeS()) < 0) {
                    min = curChild.getKey().getUpRealMinRangeS();
                    openBound = curChild.getValue() != "<=" || curChild.getKey().isUpRealMinRangeOpenBound();
                    curNode.setUpRealMinRangeS(min, openBound);
                } else if (curChild.getKey().getUpRealMinRangeS().equals(curNode.getUpRealMinRangeS())) {
                    min = curNode.getUpRealMinRangeS();
                    openBound = curChild.getValue() != "<=" || curChild.getKey().isUpRealMinRangeOpenBound() || curNode.isUpRealMinRangeOpenBound();
                    curNode.setUpRealMinRangeS(min, openBound);
                }
            }
        }
    }

    private Stack<AttributeNodeS> topologicalSort(AttributeNodeS rootNode, HashSet<AttributeNodeS> visited) throws CycleFoundException {
        Stack<AttributeNodeS> stack = new Stack<AttributeNodeS>();
        Stack<AttributeNodeS> visitOrder = new Stack<AttributeNodeS>();
        AttributeNodeS curNode = null;

        stack.push(rootNode);
        while (!stack.isEmpty()) {
            curNode = stack.pop();
            if (!visited.contains(curNode)) {
                visited.add(curNode);
                stack.push(curNode);
                Iterator<Map.Entry<AttributeNodeS, String>> it = getEdges(curNode).iterator();
                Map.Entry<AttributeNodeS, String> e = null;
                while (it.hasNext()) {
                    e = it.next();

                    if (stack.contains(e.getKey())) {
                        throw new CycleFoundException();
                    } else if (!visitOrder.contains(e.getKey())) {
                        stack.push(e.getKey());
                    }
                }
            } else
            {
                visitOrder.push(curNode);
            }
        }

        return visitOrder;
    }

    public boolean areValidRealMinRangesInIntegerDomain() {
        boolean satisfiable = true;

        Iterator<AttributeNodeS> it = getNodesS().iterator();
        AttributeNodeS curNode = null;

        while (it.hasNext() && satisfiable) {
            curNode = it.next();
            if (!curNode.isValidInIntegerDomain()) {
                satisfiable = false;
            }
        }

        return satisfiable;
    }

}
