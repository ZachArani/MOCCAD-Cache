package edu.ou.oudb.cacheprototypelibrary.querycache.trimming;

import android.util.Pair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
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
 * Created by chenxiao on 6/19/17.
 */

/*Graph used for the Date and the Time
* Refer to LabeledDirectedGraph for more information*/
public class LabeledDirectedDateTimeGraph extends LabeledDirectedGraph {
    /*Format of Date, and Time*/
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");

    private HashMap<String, AttributeNodeDT> mNodesIndex;
    private LinkedHashMap<AttributeNodeDT, LinkedHashMap<AttributeNodeDT, String>> mSuccessors;
    private LinkedHashMap<AttributeNodeDT, LinkedHashMap<AttributeNodeDT, String>> mPredecessors;
    private int mNbNode = 0;

    private LinkedHashMap<String, Integer> transitiveClosureIndex;
    private boolean[][] transitiveClosure = null;
    private boolean[][] transitiveClosureLt = null;

    public LabeledDirectedDateTimeGraph(){
        super();
        mNodesIndex = new LinkedHashMap<String, AttributeNodeDT>();
        mSuccessors = new LinkedHashMap<AttributeNodeDT, LinkedHashMap<AttributeNodeDT, String>>();
        mPredecessors = new LinkedHashMap<AttributeNodeDT, LinkedHashMap<AttributeNodeDT, String>>();
        transitiveClosureIndex = new LinkedHashMap<String, Integer>();
    }

    public void clear(){
        mNodesIndex.clear();
        mSuccessors.clear();
        mPredecessors.clear();
        transitiveClosureIndex.clear();
    }

    public void addNode(AttributeNodeDT node) {
        if (!mSuccessors.containsKey(node)) {
            mSuccessors.put(node, new LinkedHashMap<AttributeNodeDT, String>());
        }
        if (!mPredecessors.containsKey(node)) {
            mPredecessors.put(node, new LinkedHashMap<AttributeNodeDT, String>());
        }
        if (!transitiveClosureIndex.containsKey(node.getAttribute())) {
            transitiveClosureIndex.put(node.getAttribute(), mNbNode++);
        }
        if (!mNodesIndex.containsKey(node.getAttribute())) {
            mNodesIndex.put(node.getAttribute(), node);
            System.out.println("Bounds: " + mNodesIndex.values());
        }
    }

    public void addAllNodesDT(Collection<AttributeNodeDT> nodes) {
        for (AttributeNodeDT node : nodes) {
            addNode(node);
        }
    }

    public Set<AttributeNodeDT> getNodesDT() {
        return mSuccessors.keySet();
    }

    public Set<Map.Entry<AttributeNodeDT, String>> getEdges(AttributeNodeDT node) {
        return mSuccessors.get(node).entrySet();
    }

    public final void setSuccessorsDT(
            LinkedHashMap<AttributeNodeDT, LinkedHashMap<AttributeNodeDT, String>> successors) {
        if (successors != null) {
            this.mSuccessors = successors;
        }
    }

    public final void setPredecessorsDT(
            LinkedHashMap<AttributeNodeDT, LinkedHashMap<AttributeNodeDT, String>> predecessors) {
        if (predecessors != null) {
            this.mPredecessors = predecessors;
        }
    }

    private boolean[][] buildTransitiveClosure() {
        if (transitiveClosure == null) {
            transitiveClosure = new boolean[mNbNode][mNbNode];
            transitiveClosureLt = new boolean[mNbNode][mNbNode];
            int curIndex;
            for (AttributeNodeDT n : getNodesDT()) {
                curIndex = transitiveClosureIndex.get(n.getAttribute());
                transitiveClosure[curIndex][curIndex] = true;
                depthFirstSearchClosure(n);
            }
        }

        return getTransitiveClosure();
    }

    private void depthFirstSearchClosure(AttributeNodeDT root) {
        Stack<Pair<AttributeNodeDT, Boolean>> stack = new Stack<Pair<AttributeNodeDT, Boolean>>();
        HashSet<AttributeNodeDT> visited = new HashSet<AttributeNodeDT>();
        Pair<AttributeNodeDT, Boolean> curNode;
        int curRootIndex = transitiveClosureIndex.get(root.getAttribute());
        int curChildIndex;

        stack.push(new Pair<AttributeNodeDT, Boolean>(root, false));
        while (!stack.isEmpty()) {
            curNode = stack.pop();
            if (!visited.contains(curNode.first)) {
                visited.add(curNode.first);
                stack.push(curNode);

                for (Map.Entry<AttributeNodeDT, String> e : getEdges(curNode.first)) {
                    curChildIndex = transitiveClosureIndex.get(e.getKey().getAttribute());
                    transitiveClosure[curRootIndex][curChildIndex] = true;
                    transitiveClosureLt[curRootIndex][curChildIndex] = (curNode.second || e.getValue() == "<");
                    stack.push(new Pair<AttributeNodeDT, Boolean>(e.getKey(), transitiveClosureLt[curRootIndex][curChildIndex]));
                }
            }
        }
    }

    public boolean impliesPredicateIntegerDomain(XopYPredicate p) {
        boolean impliesPredicate = false;

        AttributeNodeDT curLeftNode;
        Date curRightOperand = null;

        if (mNodesIndex.containsKey(p.getLeftOperand())) {
            if (transitiveClosure == null || transitiveClosureLt == null) {
                buildTransitiveClosure();
            }

            curLeftNode = mNodesIndex.get(p.getLeftOperand());
            /*If Date*/
            if (p.getLeftOperand().equals("substr(p_date_time,0,10)")) {
                try {
                    curRightOperand = sdf.parse(p.getRightOperand().replace("%27", ""));
                } catch (ParseException e){
                    e.printStackTrace();
                }
            /*If Time*/
            } else {
                try {
                    curRightOperand = sdfTime.parse(p.getRightOperand().replace("%27", ""));
                } catch (ParseException e){
                    e.printStackTrace();
                }
            }

            switch (p.getOperator()) {
                case "<=":
                    impliesPredicate = curLeftNode.getUpRealMinRangeDT().before(curRightOperand) ||
                            curLeftNode.getUpRealMinRangeDT().equals(curRightOperand);
                    break;
                case ">=":
                    impliesPredicate = curLeftNode.getLowRealMinRangeDT().after(curRightOperand) ||
                            curLeftNode.getLowRealMinRangeDT().equals(curRightOperand);
                    break;
                default:
                    impliesPredicate = false;
                    break;
            }
        }

        return impliesPredicate;
    }

    public LabeledDirectedDateTimeGraph getCollapsedGraph() {
        Stack<AttributeNodeDT> stack = new Stack<AttributeNodeDT>();
        HashSet<AttributeNodeDT> visited = new HashSet<AttributeNodeDT>();
        HashSet<AttributeNodeDT> curSCC = null;
        HashMap<AttributeNodeDT, AttributeNodeDT> mapSCC = new HashMap<AttributeNodeDT, AttributeNodeDT>();
        Set<AttributeNodeDT> nodeSet = getNodesDT();
        LabeledDirectedDateTimeGraph transposedGraph = null;
        LabeledDirectedDateTimeGraph collapsedGraph = null;

        for (AttributeNodeDT n : nodeSet) {
            if (!visited.contains(n)) {
                stack.addAll(findFinishedExplorationOrder(n, visited));
            }
        }

        visited.clear();
        transposedGraph = getTransposedGraph();

        AttributeNodeDT curNode;
        curSCC = new HashSet<AttributeNodeDT>();
        while (!stack.isEmpty() && curSCC != null) {
            curNode = stack.pop();
            curSCC = transposedGraph.findSCC(curNode, visited);

            //merge scc
            if (curSCC != null) {
                AttributeNodeDT mergedNode = mergeNodesS(curSCC);
                for (AttributeNodeDT n : curSCC) {
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

    private Stack<AttributeNodeDT> findFinishedExplorationOrder(AttributeNodeDT rootNode, HashSet<AttributeNodeDT> visited) {
        Stack<AttributeNodeDT> stack = new Stack<AttributeNodeDT>();
        Stack<AttributeNodeDT> visitOrder = new Stack<AttributeNodeDT>();
        AttributeNodeDT curNode = null;

        stack.push(rootNode);
        while (!stack.isEmpty()) {
            curNode = stack.pop();
            if (!visited.contains(curNode)) {
                visited.add(curNode);
                stack.push(curNode);
                for (Map.Entry<AttributeNodeDT, String> e : getEdges(curNode)) {
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

    public LabeledDirectedDateTimeGraph getTransposedGraph() {
        LabeledDirectedDateTimeGraph transposedGraph = new LabeledDirectedDateTimeGraph();

        transposedGraph.setSuccessorsDT(mPredecessors);
        transposedGraph.setPredecessorsDT(mSuccessors);

        return transposedGraph;
    }

    private LinkedHashSet<AttributeNodeDT> findSCC(AttributeNodeDT rootNode, HashSet<AttributeNodeDT> visited) {
        Stack<AttributeNodeDT> stack = new Stack<AttributeNodeDT>();
        LinkedHashSet<AttributeNodeDT> scc = new LinkedHashSet<AttributeNodeDT>();
        AttributeNodeDT curNode = null;
        boolean isValidSCC = true;

        stack.push(rootNode);
        while (!stack.isEmpty() && isValidSCC) {
            curNode = stack.pop();
            if (!visited.contains(curNode) && !scc.contains(curNode)) {
                scc.add(curNode);
                visited.add(curNode);
                Iterator<Map.Entry<AttributeNodeDT, String>> it = getEdges(curNode).iterator();
                Map.Entry<AttributeNodeDT, String> e = null;
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

    public AttributeNodeDT mergeNodesS(HashSet<AttributeNodeDT> sccSet) {
        AttributeNodeDT node = null;
        StringBuilder sb = new StringBuilder();
        /*Lowest Date*/
        Calendar low = new GregorianCalendar(1970,Calendar.JANUARY,1);
        /*Current Date*/
        Calendar up = Calendar.getInstance();
        Date minLowRange = low.getTime();
        Date minUpRange = up.getTime();
        boolean minLowOpenBound = false;
        boolean minUpOpenBound = false;

        for (AttributeNodeDT n : sccSet) {
            sb.append(n.getAttribute());
            if (n.getLowClosedMinRangeDT().after(minLowRange) || n.getLowClosedMinRangeDT() == minLowRange) {
                // if same Date / Time, get the open bound if there is one in the
                // list of ssc's open bounds
                if (n.getLowClosedMinRangeDT() == minLowRange) {
                    minLowOpenBound = n.isLowClosedMinRangeOpenBound() || minLowOpenBound;
                } else {
                    minLowRange = n.getLowClosedMinRangeDT();
                    minLowOpenBound = n.isLowClosedMinRangeOpenBound();
                }
            }

            if (n.getUpClosedMinRangeDT().before(minUpRange) || n.getUpClosedMinRangeDT() == minUpRange) {
                // if same Date / Time, get the open bound if there is one in the
                // list of ssc's open bounds
                if (n.getUpClosedMinRangeDT() == minUpRange) {
                    minUpOpenBound = n.isUpClosedMinRangeOpenBound() || minUpOpenBound;
                } else {
                    minUpRange = n.getUpClosedMinRangeDT();
                    minUpOpenBound = n.isUpClosedMinRangeOpenBound();
                }
            }
        }

        node = new AttributeNodeDT(sb.toString());
        node.setLowClosedMinRangeDT(minLowRange, minLowOpenBound);
        node.setUpClosedMinRangeDT(minUpRange, minUpOpenBound);

        return node;
    }

    private LabeledDirectedDateTimeGraph buildCollapsedGraph(HashMap<AttributeNodeDT, AttributeNodeDT> mapSCC) {
        LabeledDirectedDateTimeGraph collapsedGraph = new LabeledDirectedDateTimeGraph();
        HashMap<String, AttributeNodeDT> nodesIndex = new HashMap<String, AttributeNodeDT>();

        for (AttributeNodeDT n : getNodesDT()) {
            AttributeNodeDT newNode = mapSCC.get(n);

            collapsedGraph.addNode(newNode);

            nodesIndex.put(n.getAttribute(), newNode);

            for (Map.Entry<AttributeNodeDT, String> e : getEdges(n)) {
                collapsedGraph.addEdge(newNode, mapSCC.get(e.getKey()), e.getValue());
            }
        }

        collapsedGraph.setNodesIndexDT(nodesIndex);

        return collapsedGraph;
    }

    public final void setNodesIndexDT(HashMap<String, AttributeNodeDT> nodesIndex) {
        if (nodesIndex != null) {
            this.mNodesIndex = nodesIndex;
        }
    }

    public void addEdge(AttributeNodeDT src, AttributeNodeDT dest, String operator) {
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

        HashSet<AttributeNodeDT> visited = new HashSet<AttributeNodeDT>();
        List<AttributeNodeDT> order = new ArrayList<AttributeNodeDT>();
        Stack<AttributeNodeDT> curOrder = null;
        Set<AttributeNodeDT> nodeSet = getNodesDT();
        AttributeNodeDT curNode = null;
        Map.Entry<AttributeNodeDT, String> curParent = null;
        Map.Entry<AttributeNodeDT, String> curChild = null;

        Iterator<AttributeNodeDT> it = nodeSet.iterator();
        while (it.hasNext()) {
            curNode = it.next();
            if (!visited.contains(curNode)) {
                curOrder = topologicalSort(curNode, visited);
                order.addAll(0, curOrder);
            }
        }

        Iterator<Map.Entry<AttributeNodeDT, String>> itParent = null;
        Iterator<Map.Entry<AttributeNodeDT, String>> itChild = null;
        Date max, min;
        boolean openBound;
        int nbNodes = order.size();

        for (int i = nbNodes - 1; i >= 0; --i) {

            curNode = order.get(i);

            curNode.setLowRealMinRangeDT(curNode.getLowClosedMinRangeDT(), curNode.isLowClosedMinRangeOpenBound());

            itParent = mPredecessors.get(curNode).entrySet().iterator();
            while (itParent.hasNext()) {
                curParent = itParent.next();
                if (curParent.getKey().getLowRealMinRangeDT().after(curNode.getLowRealMinRangeDT())) {
                    max = curParent.getKey().getLowRealMinRangeDT();
                    openBound = curParent.getValue() != "<=" || curParent.getKey().isLowRealMinRangeOpenBound();
                    curNode.setLowRealMinRangeDT(max, openBound);
                } else if (curParent.getKey().getLowRealMinRangeDT() == curNode.getLowRealMinRangeDT()) {
                    max = curNode.getLowRealMinRangeDT();
                    openBound = curParent.getValue() != "<=" || curParent.getKey().isLowRealMinRangeOpenBound() || curNode.isLowRealMinRangeOpenBound();
                    curNode.setLowRealMinRangeDT(max, openBound);
                }
            }
        }

        for (int i = 0; i < nbNodes; ++i) {
            curNode = order.get(i);
            curNode.setUpRealMinRangeDT(curNode.getUpClosedMinRangeDT(), curNode.isUpClosedMinRangeOpenBound());

            itChild = mSuccessors.get(curNode).entrySet().iterator();
            while (itChild.hasNext()) {
                curChild = itChild.next();
                if (curChild.getKey().getUpRealMinRangeDT().before(curNode.getUpRealMinRangeDT())) {
                    min = curChild.getKey().getUpRealMinRangeDT();
                    openBound = curChild.getValue() != "<=" || curChild.getKey().isUpRealMinRangeOpenBound();
                    curNode.setUpRealMinRangeDT(min, openBound);
                } else if (curChild.getKey().getUpRealMinRangeDT() == curNode.getUpRealMinRangeDT()) {
                    min = curNode.getUpRealMinRangeDT();
                    openBound = curChild.getValue() != "<=" || curChild.getKey().isUpRealMinRangeOpenBound() || curNode.isUpRealMinRangeOpenBound();
                    curNode.setUpRealMinRangeDT(min, openBound);
                }
            }
        }
    }

    private Stack<AttributeNodeDT> topologicalSort(AttributeNodeDT rootNode, HashSet<AttributeNodeDT> visited) throws CycleFoundException {
        Stack<AttributeNodeDT> stack = new Stack<AttributeNodeDT>();
        Stack<AttributeNodeDT> visitOrder = new Stack<AttributeNodeDT>();
        AttributeNodeDT curNode = null;

        stack.push(rootNode);
        while (!stack.isEmpty()) {
            curNode = stack.pop();
            if (!visited.contains(curNode)) {
                visited.add(curNode);
                stack.push(curNode);
                Iterator<Map.Entry<AttributeNodeDT, String>> it = getEdges(curNode).iterator();
                Map.Entry<AttributeNodeDT, String> e = null;
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

        Iterator<AttributeNodeDT> it = getNodesDT().iterator();
        AttributeNodeDT curNode = null;

        while (it.hasNext() && satisfiable) {
            curNode = it.next();
            if (!curNode.isValidInIntegerDomain()) {
                satisfiable = false;
            }
        }

        return satisfiable;
    }

}
