package edu.ou.oudb.cacheprototypeapp.ui;

import java.util.HashSet;

public class JoinQuery {

    private HashSet<String> mRelations;
    private HashSet<String> mPredicatesToJoin;

    public JoinQuery(String[] relations){
        mRelations = new HashSet<>();
        for(int i = 0; i < relations.length; i++){
            mRelations.add(relations[i]);
        }
        mPredicatesToJoin = new HashSet<>();
        mPredicatesToJoin.add("patients.id");
        mPredicatesToJoin.add("doctors.id");
    }

    public void addRelation(String rel){
        mRelations.add(rel);
    }

    public void addPredJoin(String pred){
        mPredicatesToJoin.add(pred);
    }
}
