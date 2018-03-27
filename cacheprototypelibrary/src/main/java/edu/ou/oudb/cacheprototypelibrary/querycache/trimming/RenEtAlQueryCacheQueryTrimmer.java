package edu.ou.oudb.cacheprototypelibrary.querycache.trimming;

import java.util.HashSet;
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
            if (segmentQuery.equals(inputQuery)) // exact hit
            {
                result.type = QueryTrimmingType.CACHE_HIT;
                result.probeQuery = inputQuery;
            /*Goes to GuoEtAlPredicateAnalyzer
            * Use Ctrl+B on the method*/
            } else if (geapanalyzer.respectsImplicationIntegerDomain(inputQuery.getPredicates(), segmentQuery.getPredicates())) // extended hit
            {
                GuoEtAlPredicateAnalyzer eqGeapanalyzer = new GuoEtAlPredicateAnalyzer();
                if (eqGeapanalyzer.respectsImplicationIntegerDomain(segmentQuery.getPredicates(), inputQuery.getPredicates()))// equivalent
                {
                    result.type = QueryTrimmingType.CACHE_EXTENDED_HIT_EQUIVALENT;
                } else {
                    result.type = QueryTrimmingType.CACHE_EXTENDED_HIT_INCLUDED;
                }
                result.entryQuery = segmentQuery;
                result.probeQuery = inputQuery;
            /*Goes to GuoEtAlPredicateAnalyzer*/
            } else if (geapanalyzer.respectsSatifiabilityIntegerDomain(allPredicates)) //partial hit
            {
                result.type = QueryTrimmingType.CACHE_PARTIAL_HIT;
                result.entryQuery = segmentQuery;
                result.probeQuery = new Query(inputQuery.getRelation());
                result.probeQuery.addAttribute("noteid");
                result.probeQuery.addAttribute("patientfirstname");
                result.probeQuery.addAttribute("patientlastname");
                result.probeQuery.addAttribute("doctorfirstname");
                result.probeQuery.addAttribute("doctorlastname");
                result.probeQuery.addAttribute("description");
                result.probeQuery.addAttribute("p_date_time");
                result.probeQuery.addAttribute("heartrate");
                result.probeQuery.addPredicates(inputQuery.getPredicates());
                result.remainderQuery = new Query(inputQuery.getRelation());
                result.remainderQuery.addAttribute("noteid");
                result.remainderQuery.addAttribute("patientfirstname");
                result.remainderQuery.addAttribute("patientlastname");
                result.remainderQuery.addAttribute("doctorfirstname");
                result.remainderQuery.addAttribute("doctorlastname");
                result.remainderQuery.addAttribute("description");
                result.remainderQuery.addAttribute("p_date_time");
                result.remainderQuery.addAttribute("heartrate");
                result.remainderQuery.addPredicates(inputQuery.getPredicates());
                result.remainderQuery.addExcludedPredicates(segmentQuery.getPredicates());
            } else {
                result.type = QueryTrimmingType.CACHE_MISS;
                result.remainderQuery = inputQuery;
            }
        } else {
            result.type = QueryTrimmingType.CACHE_MISS;
            result.remainderQuery = inputQuery;
        }

        return result;
    }

}
