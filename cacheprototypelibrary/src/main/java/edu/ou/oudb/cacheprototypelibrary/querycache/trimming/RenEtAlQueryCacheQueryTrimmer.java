package edu.ou.oudb.cacheprototypelibrary.querycache.trimming;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.ou.oudb.cacheprototypelibrary.querycache.query.Predicate;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;

public class RenEtAlQueryCacheQueryTrimmer implements QueryCacheQueryTrimmer {

    @Override
    public QueryTrimmingResult evaluate(Query inputQuery, Query segmentQuery) {
        QueryTrimmingResult result = new QueryTrimmingResult();
        GuoEtAlPredicateAnalyzer geapanalyzer = new GuoEtAlPredicateAnalyzer();

        // if all the input query attributes are found in the segment query attributes
        if (segmentQuery.containsPredicateAttributes(inputQuery.getPredicateAttributes())) {

            Set<Predicate> allPredicates = new HashSet<Predicate>(inputQuery.getPredicates());
            allPredicates.addAll(segmentQuery.getPredicates());
            int verticalTrim = respectsAttributeVerticalSatisfiability(inputQuery.getAttributes(),segmentQuery.getAttributes());
            if (segmentQuery.equals(inputQuery)) // exact hit
            {
                result.type = QueryTrimmingType.CACHE_HIT;
                result.probeQuery = inputQuery;
            /*Goes to GuoEtAlPredicateAnalyzer
            * Use Ctrl+B on the method*/
            } else if (geapanalyzer.respectsImplicationIntegerDomain(inputQuery.getPredicates(), segmentQuery.getPredicates()))
            {
                if (verticalTrim==1) //extended hit
                {
                    GuoEtAlPredicateAnalyzer eqGeapanalyzer = new GuoEtAlPredicateAnalyzer();
                    if (eqGeapanalyzer.respectsImplicationIntegerDomain(segmentQuery.getPredicates(), inputQuery.getPredicates())) // equivalent
                    {
                        result.type = QueryTrimmingType.CACHE_EXTENDED_HIT_EQUIVALENT;
                    } else {
                        result.type = QueryTrimmingType.CACHE_EXTENDED_HIT_INCLUDED;
                    }
                    result.entryQuery = segmentQuery;
                    result.probeQuery = inputQuery;
                    return result;
                } else if(verticalTrim==2)  //vertical trim
                {
                    result.type = QueryTrimmingType.CACHE_VERTICAL;

                    HashSet<String> attributeSet1 = new HashSet<String>();
                    attributeSet1.addAll(inputQuery.getAttributes());
                    attributeSet1.retainAll(segmentQuery.getAttributes());
                    if(!attributeSet1.contains("noteid")) { attributeSet1.add("noteid"); } //vertical trimming requires union with key attribute set; key attribute should be noteid
                    HashSet<String> attributeSet2 = new HashSet<String>();
                    attributeSet2.addAll(inputQuery.getAttributes());
                    attributeSet2.removeAll(segmentQuery.getAttributes());
                    if(!attributeSet2.contains("noteid")) { attributeSet2.add("noteid"); } //vertical trimming requires union with key attribute set; key attribute should be noteid

                    result.entryQuery = segmentQuery;
                    result.inputQuery = new Query(inputQuery.getRelation());
                    result.inputQuery.addAttributes(inputQuery.getAttributes());
                    result.inputQuery.addPredicates(inputQuery.getPredicates());
                    result.probeQuery = new Query(inputQuery.getRelation());
                    result.probeQuery.addPredicates(inputQuery.getPredicates());
                    result.probeQuery.addAttributes(attributeSet1);
                    result.remainderQuery = new Query(inputQuery.getRelation());
                    result.remainderQuery.addPredicates(inputQuery.getPredicates());
                    result.remainderQuery.addAttributes(attributeSet2);
                }

            /*Goes to GuoEtAlPredicateAnalyzer*/
            } else if (geapanalyzer.respectsSatisfiabilityIntegerDomain(allPredicates))
            {
                if(verticalTrim==1) { //horizontal trimming
                    result.type = QueryTrimmingType.CACHE_HORIZONTAL;
                    result.entryQuery = segmentQuery;
                    result.probeQuery = new Query(inputQuery.getRelation());
                    result.probeQuery.addPredicates(inputQuery.getPredicates());
                    result.probeQuery.addAttributes(inputQuery.getAttributes());
                    result.remainderQuery = new Query(inputQuery.getRelation());
                    result.remainderQuery.addPredicates(inputQuery.getPredicates());
                    result.remainderQuery.addExcludedPredicates(segmentQuery.getPredicates());
                    result.remainderQuery.addAttributes(inputQuery.getAttributes());
                } else if(verticalTrim==2) {
                    result.type = QueryTrimmingType.CACHE_HYBRID;

                    HashSet<String> attributeSet1 = new HashSet<String>();
                    attributeSet1.addAll(inputQuery.getAttributes());
                    attributeSet1.retainAll(segmentQuery.getAttributes());
                    if(!attributeSet1.contains("noteid")) { attributeSet1.add("noteid"); } //vertical trimming requires union with key attribute set; key attribute should be noteid
                    HashSet<String> attributeSet2 = new HashSet<String>();
                    attributeSet2.addAll(inputQuery.getAttributes());
                    attributeSet2.removeAll(segmentQuery.getAttributes());
                    if(!attributeSet2.contains("noteid")) { attributeSet2.add("noteid"); } //vertical trimming requires union with key attribute set; key attribute should be noteid

                    HashSet<Predicate> qPsP = new HashSet<Predicate>();
                    qPsP.addAll(inputQuery.getPredicates());
                    qPsP.retainAll(segmentQuery.getPredicates()); //overlapping Predicates - required due to remainder query 1, not executed on segment query and so needs proper list of predicates

                    result.entryQuery = segmentQuery;
                    result.inputQuery = new Query(inputQuery.getRelation());
                    result.inputQuery.addAttributes(inputQuery.getAttributes());
                    result.inputQuery.addPredicates(inputQuery.getPredicates());
                    result.probeQuery = new Query(inputQuery.getRelation());
                    result.probeQuery.addPredicates(inputQuery.getPredicates());
                    result.probeQuery.addAttributes(attributeSet1);
                    result.remainderQuery = new Query(inputQuery.getRelation());
                    result.remainderQuery.addPredicates(qPsP);
                    result.remainderQuery.addAttributes(attributeSet2);
                    result.remainderQuery2 = new Query(inputQuery.getRelation());
                    result.remainderQuery2.addAttributes(inputQuery.getAttributes());
                    result.remainderQuery2.addPredicates(inputQuery.getPredicates());
                    result.remainderQuery2.addExcludedPredicates(segmentQuery.getPredicates());
                }
            }
        }
        //if no returns made above, then is a cache miss
        result.type = QueryTrimmingType.CACHE_MISS;
        result.remainderQuery = inputQuery;

        return result;
    }

    /***
     * Tests to see whether all, some, or none of the input query's attributes are contained in the segment query
     * @param inputQueryAttributes The set of attributes of the input query
     * @param segmentQueryAttributes The set of attributes of the query segment from the cache
     * @return int representing whether the sets of attribute overlap. 1 = segment query contains all input query attributes, 2 = some but not all of input query attributes in segment query, 3 = none of input query attributes in segment query
     */
    public int respectsAttributeVerticalSatisfiability (Set<String> inputQueryAttributes, Set<String> segmentQueryAttributes) {
        Iterator<String> it = inputQueryAttributes.iterator();
        String a = null;
        if (segmentQueryAttributes.containsAll(inputQueryAttributes)) {
            return 1;
        } //returns 1 if segment query attributes contain all input query attributes; no vertical trimming required
        while (it.hasNext()) {
            a = it.next();
            Iterator<String> itSeg = segmentQueryAttributes.iterator();
            String s = null;
            while (itSeg.hasNext()) {
                s = itSeg.next();
                if (a.equalsIgnoreCase(s)) {
                    return 2; //once a single attribute of overlap is found, no need to continue; vertical trimming will be necessary
                }
            }
        }
        return 3; //if no overlap is found, 3 is returned and no vertical trimming will be necessary (Cache Miss)
    }

}
