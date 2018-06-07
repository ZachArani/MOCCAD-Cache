/* Stuff related to join and join tables are stored in this class*/

public class JoinQuery extends Query{

    /** Relations to join with the first table */
    private ArrayList jRelation;

    /** Attributes to look for in the others relations */
    private HashMap<String, String> jAttributes;

    /** Predicate attributes in others tables */
    private HashMap<String, String> jPredicateAttributes;

    /** Collection of predicates (conjunction)*/
    private HashMap<String, Predicate> jPredicates;

    /** Excluded Predicates: !(ExcludedPredicates)*/
    private HashMap<String, Predicate> jExcludedPredicates;

    public JoinQuery(ArrayList relation)
    {
        setRelation(relation[0]);
        for (int i = 0; i < (relation.size-1); i++){
            setjRelation(i, relation.get(i+1));
        }
        jAttributesPredicates = new HashMap<String, Predicate>();
        jAttributes = new HashMap<String, Predicate>();
        jPredicateAttributes = new HashMap<String, Predicate>();
        jExcludedPredicates = new HashMap<String, Predicate>();
        mSize += ObjectSizer.getStringSize32bits(relation.length());
    }

    private setjRelation(int nb, String rel){
        if (rel != null)
        {
            this.jRelation.set(i, rel);
            mSize -= ObjectSizer.getStringSize32bits(mRelation.length());
            mSize += ObjectSizer.getStringSize32bits(relation.length());
        }
    }

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
        if (jAttributes == null){
            if(other.jAttributes != null) {
                return false;
            }
        }else if(!jAttributes.equals(other.jAttributes)){
            return false;
        }

        if (jPredicateAttributes == null){
            if(other.jPredicateAttributes != null) {
                return false;
            }
        }else if(!jPredicateAttributes.equals(other.jPredicateAttributes)){
            return false;
        }

        if (jRelation == null){
            if(other.jRelation != null) {
                return false;
            }
        }else if(!jRelation.equals(other.jRelation)){
            return false;
        }

        if (jExcludedPredicates == null){
            if(other.jExcludedPredicates != null) {
                return false;
            }
        }else if(!jExcludedPredicates.equals(other.jExcludedPredicates)){
            return false;
        }

        if (jPredicates == null){
            if(other.jPredicates != null) {
                return false;
            }
        }else if(!jPredicates.equals(other.jPredicates)){
            return false;
        }

        if (mExcludedPredicates == null) {
            if (other.mExcludedPredicates != null) {
                return false;
            }
        } else if (!mExcludedPredicates.equals(other.mExcludedPredicates)) {
            return false;
        }
        if (mPredicateAttributes == null) {
            if (other.mPredicateAttributes != null) {
                return false;
            }
        } else if (!mPredicateAttributes.equals(other.mPredicateAttributes)) {
            return false;
        }
        if (mAttributes == null) {
            if (other.mAttributes != null) {
                return false;
            }
        } else if (!mAttributes.equals(other.mAttributes)) {
            return false;
        }
        if (mPredicates == null) {
            if (other.mPredicates != null) {
                return false;
            }
        } else if (!mPredicates.equals(other.mPredicates)) {
            return false;
        }
        if (mRelation == null) {
            if (other.mRelation != null) {
                return false;
            }
        } else if (!mRelation.equals(other.mRelation)) {
            return false;
        }
        return true;
    }

}