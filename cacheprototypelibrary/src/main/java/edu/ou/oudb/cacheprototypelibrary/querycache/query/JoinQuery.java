package edu.ou.oudb.cacheprototypelibrary.querycache.query;/* Stuff related to join and join tables are stored in this class*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import edu.ou.oudb.cacheprototypelibrary.metadata.ObjectSizer;

public class JoinQuery extends Query{

    /** Relations to join with the first table */
    private ArrayList<String> jRelation;

    /** Joins attributes for each table **/
    private HashMap<String, String> joinAttributes;

    public JoinQuery(String relation, int nbrel)
    {
        super(relation);
        jRelation = new ArrayList<>(1+nbrel);
        //jAttributes = new HashMap<String, String>();
        joinAttributes = new HashMap<String, String>();
    }

    public void fillRelations(ArrayList<String> relation){
        //Fill jRelation with the relation array value except 1st
        for (int i = 0; i < (relation.size()); i++){
            jRelation.add(relation.get(i));
        }
    }


    public void fillJAttributes(HashMap<String, String> jattr){
        joinAttributes.putAll(jattr);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        JoinQuery other = (JoinQuery) obj;
        /*if (jAttributes == null){
            if(other.jAttributes != null) {
                return false;
            }
        }else if(!jAttributes.equals(other.jAttributes)){
            return false;
        }*/


        if (jRelation == null){
            if(other.jRelation != null) {
                return false;
            }
        }else if(!jRelation.equals(other.jRelation)){
            return false;
        }

        //Query part equals

        String rel = getRelation();
        Set<Predicate> mExPredic = getExcludedPredicates();
        Set<Predicate> predic = getPredicates();
        HashSet<String> attr = getAttributes();
        HashSet<String> predAttr = getPredicateAttributes();

        if (mExPredic == null) {
            if (other.getExcludedPredicates() != null) {
                return false;
            }
        } else if (!mExPredic.equals(other.getExcludedPredicates())) {
            return false;
        }
        if (predAttr == null) {
            if (other.getPredicateAttributes() != null) {
                return false;
            }
        } else if (!predAttr.equals(other.getPredicateAttributes())) {
            return false;
        }
        if (attr == null) {
            if (other.getAttributes() != null) {
                return false;
            }
        } else if (!attr.equals(other.getAttributes())) {
            return false;
        }
        if (predic == null) {
            if (other.getPredicates() != null) {
                return false;
            }
        } else if (!predic.equals(other.getPredicates())) {
            return false;
        }
        if (rel == null) {
            if (other.getRelation() != null) {
                return false;
            }
        } else if (!rel.equals(other.getRelation())) {
            return false;
        }
        return true;
    }

    @Override
    public String toSQLString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ");

        int sizeAttributes = getAttributes().size();

        if (sizeAttributes == 0)
        {
            builder.append("*");
        }

        int k = 0;


        LinkedHashSet<String> orderAttributes = new LinkedHashSet<>(getAttributes());
        //q loop
        for(String a: getAttributes()) {
            builder.append(a);
            if (k < sizeAttributes-1) {
                builder.append(", ");
            }
            k++;
        }
        /*join loop
        for(int i = 0; i < jAttributes.size(); i++) {
            builder.append(", ");
            builder.append(jAttributes.get(i));
        }*/

        builder.append(" FROM ");
        builder.append(getRelation());
        builder.append(" INNER JOIN ");
        builder.append(jRelation.get(0));
        /*
        for(int i = 0; i < jRelation.size(); i++) {
            builder.append(", ");
            builder.append(jRelation.get(i));
        }*/

            builder.append(" ON ");
            // table2.joinattribute = table1.joinattribute
            builder.append(joinAttributes.get(jRelation.get(0))+" = "+ joinAttributes.get(getRelation()));

            if(getPredicates().size() > 0){
                builder.append(" WHERE ");
                int i = 0;
                Set<Predicate> qPredicates = getPredicates();
                for(Predicate p: qPredicates)
                {
                    builder.append(p);
                    if (i < qPredicates.size()-1)
                    {
                        builder.append(" AND ");
                    }
                    ++i;
                }
            }

        builder.append(";");

        return builder.toString();
    }

}