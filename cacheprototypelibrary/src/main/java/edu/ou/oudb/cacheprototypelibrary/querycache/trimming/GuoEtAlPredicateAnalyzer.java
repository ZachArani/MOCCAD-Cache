package edu.ou.oudb.cacheprototypelibrary.querycache.trimming;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import edu.ou.oudb.cacheprototypelibrary.querycache.exception.CycleFoundException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.InvalidPredicateException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.NPHardProblemException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.TrivialPredicateException;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Predicate;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.PredicateFactory;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.XopCPredicate;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.XopYPredicate;

/**
 * @author Mikael Perrin
 * @since 1.0
 * <p>
 * implementation of a predicate analyzer
 * (Singleton)
 */
public class GuoEtAlPredicateAnalyzer implements PredicatesAnalyzer {
    /*Date and Time*/
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");

    /*Used to handle caching with Numbers*/
    private HashMap<String, AttributeNode> mNodeMap = null;
    private LabeledDirectedGraph mNodeGraph = null;
    private LabeledDirectedGraph mNodeGraphCollapsed = null;

    /*Used to handle caching with Strings*/
    private HashMap<String, AttributeNodeS> mNodeMapS = null;
    private LabeledDirectedStringsGraph mNodeStringGraph = null;
    private LabeledDirectedStringsGraph mNodeStringGraphCollapsed = null;

    /*Used to handle caching with Date and Time*/
    private HashMap<String, AttributeNodeDT> mNodeMapDT = null;
    private LabeledDirectedDateTimeGraph mNodeDateTimeGraph = null;
    private LabeledDirectedDateTimeGraph mNodeDateTimeGraphCollapsed = null;

    public GuoEtAlPredicateAnalyzer() {
        /*Maps*/
        mNodeMap = new LinkedHashMap<String, AttributeNode>();
        mNodeMapS = new LinkedHashMap<String, AttributeNodeS>();
        mNodeMapDT = new LinkedHashMap<String, AttributeNodeDT>();
        /*Graphs*/
        mNodeGraph = new LabeledDirectedGraph();
        mNodeStringGraph = new LabeledDirectedStringsGraph();
        mNodeDateTimeGraph = new LabeledDirectedDateTimeGraph();
    }


    /**
     * method used to respect all the rules of the real domain
     * for the solving of satisfiability and implications
     *
     * @param predicates the given predicate list
     * @return the cleaned predicate list
     * @throws TrivialPredicateException
     * @throws NPHardProblemException
     * @throws CycleFoundException
     */
    private Set<Predicate> transformToIntegerDomain(Set<Predicate> predicates)
            throws InvalidPredicateException, TrivialPredicateException, NPHardProblemException {
        Set<Predicate> cleanPredicateList = new HashSet<Predicate>();
        for (Predicate p : predicates) {
            // replace the X=Y or X=C
            // by X<=Y AND X>=Y or X<=C AND X>=C
            // and each X<C or X>C
            // by X<=C-1 and X>=C+1
            // if "<>" then NP-Hard
            if (p.getOperator().equals("=")) {
                Predicate pLowBound = PredicateFactory.copyPredicate(p);
                pLowBound.setOperator("<=");
                /*The transformToIntegerDomainPredicate method is used according to the type of right operand (i.e. Number, String, or Date and Time)
                * This method can be found in XopCPredicate and XopYPredicate respectively*/
                pLowBound.transformToIntegerDomainPredicate();
                Predicate pUpBound = PredicateFactory.copyPredicate(p);
                pUpBound.setOperator(">=");
                pUpBound.transformToIntegerDomainPredicate();
                cleanPredicateList.add(pLowBound);
                cleanPredicateList.add(pUpBound);
            } else if (!p.getOperator().equals("<>")) {
                Predicate copyP = PredicateFactory.copyPredicate(p);
                copyP.transformToIntegerDomainPredicate();
                cleanPredicateList.add(copyP);
            } else {
                throw new NPHardProblemException();
            }
        }
        return cleanPredicateList;
    }

    /**
     * method used to respect all the rules of the real domain
     * for the solving of satisfiability and implications
     *
     * @param predicates the given predicate list
     * @return the cleaned predicate list
     * @throws TrivialPredicateException
     * @throws NPHardProblemException
     * @throws CycleFoundException
     */
    private Set<Predicate> transformToRealDomain(Set<Predicate> predicates)
            throws InvalidPredicateException, TrivialPredicateException, NPHardProblemException {
        Set<Predicate> cleanPredicateList = new HashSet<Predicate>();
        for (Predicate p : predicates) {
            // replace the X=Y or X=C
            // by X<=Y AND X>=Y or X<=C AND X>=C
            // if "<>" then NP-Hard
            if (p.getOperator().equals("=")) {
                Predicate pLowBound = PredicateFactory.copyPredicate(p);
                pLowBound.setOperator("<=");
                pLowBound.transformToRealDomainPredicate();
                Predicate pUpBound = PredicateFactory.copyPredicate(p);
                pUpBound.setOperator(">=");
                pUpBound.transformToRealDomainPredicate();
                cleanPredicateList.add(pLowBound);
                cleanPredicateList.add(pUpBound);
            } else if (!p.getOperator().equals("<>")) {
                Predicate copyP = PredicateFactory.copyPredicate(p);
                copyP.transformToRealDomainPredicate();
                cleanPredicateList.add(copyP);
            } else {
                throw new NPHardProblemException();
            }
        }
        return cleanPredicateList;
    }

    /**
     * init the different collections to check satisfiability for cachePredicates + queryPredicates
     *
     * @param predicates
     * @return
     * @throws CycleFoundException
     * @throws TrivialPredicateException
     * @throws NPHardProblemException
     */
    private Set<Predicate> initPrerequisitesIntegerDomain(Set<Predicate> predicates)
            throws InvalidPredicateException, TrivialPredicateException, NPHardProblemException {
        Set<Predicate> newPredicates = new HashSet<Predicate>();
        // aggregation of the two lists
        newPredicates.addAll(transformToIntegerDomain(predicates)); // See transformToIntegerDomain method above

        return newPredicates;
    }

    /**
     * init the different collections to check satisfiability for cachePredicates + queryPredicates
     *
     * @param predicates
     * @return
     * @throws CycleFoundException
     * @throws TrivialPredicateException
     * @throws NPHardProblemException
     */
    private Set<Predicate> initPrerequisitesRealDomain(Set<Predicate> predicates)
            throws InvalidPredicateException, TrivialPredicateException, NPHardProblemException {
        Set<Predicate> newPredicates = new HashSet<Predicate>();
        // aggregation of the two lists
        newPredicates.addAll(transformToRealDomain(predicates));

        return newPredicates;
    }

    public boolean respectsSatisfiabilityIntegerDomain(Set<Predicate> predicates) {
        XopCPredicate curXopCPredicate = null;
        /*Current Node with Numbers*/
        AttributeNode curNode = null;
        /*Current Node with Strings*/
        AttributeNodeS curNodeS = null;
        /*Current Node with Date and Time*/
        AttributeNodeDT curNodeDT = null;
        boolean respectsSatisfiability = true;
        Set<Predicate> cleanPredicates = null;

        try {
            cleanPredicates = initPrerequisitesIntegerDomain(predicates); // See method above
        } catch (InvalidPredicateException | TrivialPredicateException e1) {
            System.err.println("Bad Predicate within provided lists of predicate");
            respectsSatisfiability = false;
        } catch (NPHardProblemException e) {
            respectsSatisfiability = false;
        }

        Iterator<Predicate> it = cleanPredicates.iterator();
        Predicate p = null;
        while (it.hasNext() && respectsSatisfiability) {
            p = it.next();
            /*If the rightOperand is a Constant
            * In our case Constant = Number*/
            if (p instanceof XopCPredicate) {
                curXopCPredicate = (XopCPredicate) p;

                // if the node already exists
                if (mNodeMap.containsKey(curXopCPredicate.getLeftOperand())) {
                    curNode = mNodeMap.get(curXopCPredicate.getLeftOperand());
                } else // if the node does not exist
                {
                    curNode = new AttributeNode(curXopCPredicate.getLeftOperand());
                }

                // Algorithm 1: stp 2
                switch (curXopCPredicate.getOperator()) {
                    case "<=":
                        /*Bounds are (-INFINITY;INFINITY) at first, those bounds are defined in AttributeNode
                        * If rightOperand (input value) <= curBound (i.e. INFINITY the first time), bounds are changed to (-INFINITY;rightOperand]
                        * Note that bound is also closed in the meantime*/
                        if (curXopCPredicate.getRightOperand() <= curNode.getUpClosedMinRange()) {
                            /*We change the bounds here, see the method*/
                            curNode.setUpClosedMinRange(curXopCPredicate.getRightOperand(), false);
                        }
                        break;
                    case ">=":
                        /*Here it will be changed to [rightOperand;INFINITY) for example*/
                        if (curXopCPredicate.getRightOperand() >= curNode.getLowClosedMinRange()) {
                            curNode.setLowClosedMinRange(curXopCPredicate.getRightOperand(), false);
                        }
                        break;
                }
                /*curNode: (attribute;[lowBound;upBound];[lowBound;upBound])
                * With above cases, only the first bounds are changed
                * The second bounds are changed when computeRealMinRanges() is called, see below*/
                mNodeMap.put(curNode.getAttribute(), curNode);
            /*If the rightOperand is a String*/
            } else {
                XopYPredicate pXopY = ((XopYPredicate) p);

                switch (pXopY.getLeftOperand()) {
                    /*If it's the Date*/
                    case "l_shipdate":
                        if (mNodeMapDT.containsKey(pXopY.getLeftOperand())) {
                            curNodeDT = mNodeMapDT.get(pXopY.getLeftOperand());
                        } else {
                            curNodeDT = new AttributeNodeDT(pXopY.getLeftOperand());
                        }

                        /*We convert the String into a Date*/
                        Date rightOperand = null;
                        try {
                            rightOperand = sdf.parse(pXopY.getRightOperand().replace("%27", "").replace("'", ""));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        switch (pXopY.getOperator()) {
                            case "<=":
                                /*Same principle than with the Numbers, bounds are defined in AttributeNodeDT
                                * First Bounds are (1970-01-01 00:00:00;curDate) (e.g. curDate = 2017-06-22 16:12:XX (4:12PM) at the time I'm writing this)*/
                                if (rightOperand.before(curNodeDT.getUpClosedMinRangeDT())) {
                                    curNodeDT.setUpClosedMinRangeDT(rightOperand, false);
                                }
                                break;
                            case ">=":
                                if (rightOperand.after(curNodeDT.getLowClosedMinRangeDT())) {
                                    curNodeDT.setLowClosedMinRangeDT(rightOperand, false);
                                }
                                break;
                        }
                        mNodeMapDT.put(curNodeDT.getAttribute(), curNodeDT);
                        break;
                    /*If it's the Time*/
                    case "substr(p_date_time,12)":
                        if (mNodeMapDT.containsKey(pXopY.getLeftOperand())) {
                            curNodeDT = mNodeMapDT.get(pXopY.getLeftOperand());
                        } else {
                            curNodeDT = new AttributeNodeDT(pXopY.getLeftOperand());
                        }

                        Date rightOperandTime = null;
                        /*We convert the String into a Time*/
                        try {
                            rightOperandTime = sdfTime.parse(pXopY.getRightOperand().replace("%27", ""));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        /*Same as the Date but comparing the Time instead*/
                        switch (pXopY.getOperator()) {
                            case "<=":
                                if (rightOperandTime.before(curNodeDT.getUpClosedMinRangeDT())) {
                                    curNodeDT.setUpClosedMinRangeDT(rightOperandTime, false);
                                }
                                break;
                            case ">=":
                                if (rightOperandTime.after(curNodeDT.getLowClosedMinRangeDT())) {
                                    curNodeDT.setLowClosedMinRangeDT(rightOperandTime, false);
                                }
                                break;
                        }
                        mNodeMapDT.put(curNodeDT.getAttribute(), curNodeDT);
                        break;
                    /*Else*/
                    default:
                        if (mNodeMapS.containsKey(pXopY.getLeftOperand())) {
                            curNodeS = mNodeMapS.get(pXopY.getLeftOperand());
                        } else {
                            curNodeS = new AttributeNodeS(pXopY.getLeftOperand());
                        }

                        /*Same principle, bounds are defined in AttributeNodeS
                        * This type of cache has some problems, we do not use it atm*/
                        switch (pXopY.getOperator()) {
                            case "<=":
                                if (pXopY.getRightOperand().replace("%27", "").replace("%20", " ").compareTo(curNodeS.getUpClosedMinRangeS()) <= 0) {
                                    curNodeS.setUpClosedMinRangeS(pXopY.getRightOperand().replace("%27", "").replace("%20", " "), false);
                                }
                                break;
                            case ">=":
                                if (pXopY.getRightOperand().replace("%27", "").replace("%20", " ").compareTo(curNodeS.getLowClosedMinRangeS()) >= 0) {
                                    curNodeS.setLowClosedMinRangeS(pXopY.getRightOperand().replace("%27", "").replace("%20", " "), false);
                                }
                                break;
                        }
                        mNodeMapS.put(curNodeS.getAttribute(), curNodeS);
                        break;
                }
            }
        }


        if (respectsSatisfiability) {
            /*The bounds are added to the nodes here*/
            mNodeGraph.addAllNodes(mNodeMap.values());
            mNodeStringGraph.addAllNodesS(mNodeMapS.values());
            mNodeDateTimeGraph.addAllNodesDT(mNodeMapDT.values());

            /*Building the graph with Numbers*/
            if (mNodeMap.size() > 0) {
                mNodeGraphCollapsed = mNodeGraph.getCollapsedGraph();
                if (mNodeGraphCollapsed != null) try {
                    //compute Alow, Aup, change the second bounds
                    mNodeGraphCollapsed.computeRealMinRanges();

                    // check real minimum ranges (i.e. if lowBound <= upBound), see in the corresponding LabeledDirectedGraph
                    respectsSatisfiability = mNodeGraphCollapsed.areValidRealMinRangesInIntegerDomain();
                } catch (CycleFoundException e) {
                    respectsSatisfiability = false;
                }
                else {
                    respectsSatisfiability = false;
                }
            /*Building the graph with Strings*/
            } if (mNodeMapS.size() > 0) {
                mNodeStringGraphCollapsed = mNodeStringGraph.getCollapsedGraph();
                if (mNodeStringGraphCollapsed != null) {
                    try {
                        mNodeStringGraphCollapsed.computeRealMinRanges();
                        respectsSatisfiability = mNodeStringGraphCollapsed.areValidRealMinRangesInIntegerDomain();
                    } catch (CycleFoundException e) {
                        respectsSatisfiability = false;
                    }
                } else {
                    respectsSatisfiability = false;
                }
            /*Building the graph with Date and Time*/
            } if (mNodeMapDT.size() > 0) {
                mNodeDateTimeGraphCollapsed = mNodeDateTimeGraph.getCollapsedGraph();
                if (mNodeDateTimeGraphCollapsed != null) {
                    try {
                        mNodeDateTimeGraphCollapsed.computeRealMinRanges();
                        respectsSatisfiability = mNodeDateTimeGraphCollapsed.areValidRealMinRangesInIntegerDomain();
                    } catch (CycleFoundException e) {
                        respectsSatisfiability = false;
                    }
                } else {
                    respectsSatisfiability = false;
                }
            }
        }

        return respectsSatisfiability;
    }

    public boolean respectsSatifiabilityRealDomain(Set<Predicate> predicates) {
        XopCPredicate curXopCPredicate = null;
        AttributeNode curNode = null;
        boolean respectsSatisfiability = true;
        Set<Predicate> cleanPredicates = null;
        Set<XopYPredicate> listXopYPredicates = new HashSet<XopYPredicate>();


        try {
            cleanPredicates = initPrerequisitesRealDomain(predicates);
        } catch (InvalidPredicateException | TrivialPredicateException e1) {
            System.err.println("Bad Predicate within provided lists of predicate");
            respectsSatisfiability = false;
        } catch (NPHardProblemException e) {
            respectsSatisfiability = false;
        }

        Iterator<Predicate> it = cleanPredicates.iterator();
        Predicate p = null;
        while (it.hasNext() && respectsSatisfiability) {
            p = it.next();
            if (p instanceof XopCPredicate) {
                curXopCPredicate = (XopCPredicate) p;

                // if the node already exists
                if (mNodeMap.containsKey(curXopCPredicate.getLeftOperand())) {
                    curNode = mNodeMap.get(curXopCPredicate.getLeftOperand());
                } else // if the node does not exist
                {
                    curNode = new AttributeNode(curXopCPredicate.getLeftOperand());
                }

                // Algorithm 2: stp 2
                switch (curXopCPredicate.getOperator()) {
                    case "<=":
                        if (curXopCPredicate.getRightOperand() <= curNode.getUpClosedMinRange()) {
                            curNode.setUpClosedMinRange(curXopCPredicate.getRightOperand(), false);
                        }
                        break;
                    case "<":
                        if (curXopCPredicate.getRightOperand() < curNode.getUpClosedMinRange()) {
                            curNode.setUpClosedMinRange(curXopCPredicate.getRightOperand(), true);
                        }
                        break;
                    case ">=":
                        if (curXopCPredicate.getRightOperand() >= curNode.getLowClosedMinRange()) {
                            curNode.setLowClosedMinRange(curXopCPredicate.getRightOperand(), false);
                        }
                        break;
                    case ">":
                        if (curXopCPredicate.getRightOperand() > curNode.getLowClosedMinRange()) {
                            curNode.setLowClosedMinRange(curXopCPredicate.getRightOperand(), true);
                        }
                        break;
                }
                mNodeMap.put(curNode.getAttribute(), curNode);
            } else // p instanceof XopYPredicate
            {
                XopYPredicate pXopY = ((XopYPredicate) p);
                listXopYPredicates.add(pXopY);
                if (!mNodeMap.containsKey(pXopY.getLeftOperand()))
                    mNodeMap.put(pXopY.getLeftOperand(), new AttributeNode(pXopY.getLeftOperand()));
                if (!mNodeMap.containsKey(pXopY.getRightOperand()))
                    mNodeMap.put(pXopY.getRightOperand(), new AttributeNode(pXopY.getRightOperand()));

            }
        }

        if (respectsSatisfiability) {
//            mNodeGraph.addAllNodes(mNodeMap.values());

            for (XopYPredicate pXopY : listXopYPredicates) {
                //Build the edges
                mNodeGraph.addEdge(mNodeMap.get(pXopY.getLeftOperand()), mNodeMap.get(pXopY.getRightOperand()), pXopY.getOperator());
            }

            // get the sccs
            mNodeGraphCollapsed = mNodeGraph.getCollapsedGraph();
            if (mNodeGraphCollapsed != null) {
                try {
                    //compute Alow, Aup
                    mNodeGraphCollapsed.computeRealMinRanges();

                    // check real minimum ranges
                    respectsSatisfiability = mNodeGraphCollapsed.areValidRealMinRangesInRealDomain();
                } catch (CycleFoundException e) {
                    respectsSatisfiability = false;
                }
            } else {
                respectsSatisfiability = false;
            }
        }


        return respectsSatisfiability;
    }


    public boolean respectsImplicationIntegerDomain(Set<Predicate> queryPredicates, Set<Predicate> cachePredicates) {
        boolean respectsImplication = true;
        Set<Predicate> cleanCachePredicates = null;

        mNodeMap.clear();
        mNodeGraph.clear();

        if (respectsSatisfiabilityIntegerDomain(queryPredicates)) {
            try {
                cleanCachePredicates = initPrerequisitesIntegerDomain(cachePredicates);
            } catch (InvalidPredicateException | TrivialPredicateException e1) {
                System.err.println("Bad Predicate within provided lists of predicate");
                respectsImplication = false;
            } catch (NPHardProblemException e) {
                respectsImplication = false;
            }

            Iterator<Predicate> iterator = cleanCachePredicates.iterator();
            int stringNum = 0;
            int regNum = 0;
            int dateNum = 0;
            Predicate curPredicate;
            while (iterator.hasNext() && respectsImplication) {
                curPredicate = iterator.next();
                /*Goes to the corresponding LabeledDirectedGraph
                * (i.e. Strings, Numbers, or Date and Time)*/
                if (mNodeStringGraphCollapsed != null && stringNum < mNodeStringGraphCollapsed.getmNbNode()  && curPredicate instanceof XopCPredicate) { //If not null and we haven't already visited the node already
                    respectsImplication = mNodeStringGraphCollapsed.impliesPredicateIntegerDomain((XopYPredicate) curPredicate);
                    stringNum++;
                } else if (mNodeGraphCollapsed != null && regNum < mNodeGraphCollapsed.getmNbNode() && curPredicate instanceof XopCPredicate) {
                    respectsImplication = mNodeGraphCollapsed.impliesPredicateIntegerDomain((XopCPredicate) curPredicate);
                    regNum++;
                } else if (mNodeDateTimeGraphCollapsed != null && dateNum < mNodeDateTimeGraphCollapsed.getNodesDT().size() *2 && curPredicate instanceof XopYPredicate) {
                    respectsImplication = mNodeDateTimeGraphCollapsed.impliesPredicateIntegerDomain((XopYPredicate) curPredicate);
                    dateNum++;
                } else {
                    respectsImplication = false;
                }
            }
        }

        return respectsImplication;
    }


    public boolean respectsImplicationRealDomain(Set<Predicate> queryPredicates, Set<Predicate> cachePredicates) {
        boolean respectsImplication = true;
        Set<Predicate> cleanCachePredicates = null;

        mNodeMap.clear();
        mNodeGraph.clear();

        if (respectsSatifiabilityRealDomain(queryPredicates)) {
            try {
                cleanCachePredicates = initPrerequisitesRealDomain(cachePredicates);
            } catch (InvalidPredicateException | TrivialPredicateException e1) {
                System.err.println("Bad Predicate within provided lists of predicate");
                respectsImplication = false;
            } catch (NPHardProblemException e) {
                respectsImplication = false;
            }

            Iterator<Predicate> iterator = cleanCachePredicates.iterator();
            Predicate curPredicate;
            while (iterator.hasNext() && respectsImplication) {
                curPredicate = iterator.next();
                if (mNodeGraphCollapsed != null) {
                    if (curPredicate instanceof XopYPredicate)
                        respectsImplication = mNodeGraphCollapsed.impliesPredicateRealDomain((XopYPredicate) curPredicate);
                    else if (curPredicate instanceof XopCPredicate)
                        respectsImplication = mNodeGraphCollapsed.impliesPredicateRealDomain((XopCPredicate) curPredicate);
                    else
                        throw new UnsupportedOperationException();
                } else {
                    respectsImplication = false;
                }
            }
        }

        return respectsImplication;
    }


}


