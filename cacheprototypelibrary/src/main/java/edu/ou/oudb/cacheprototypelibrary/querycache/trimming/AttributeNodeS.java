package edu.ou.oudb.cacheprototypelibrary.querycache.trimming;

/**
 * Created by chenxiao on 6/16/17.
 */

/*Same class as AttributeNode but used to handle the cache for the Strings
* Bounds are replaced to Strings and comparison is done using compareTo*/
public class AttributeNodeS extends AttributeNode {

    private String mAttribute;

    private String mLowMinRangeS = "A";
    private boolean mLowMinRangeOpenBound = true;

    private String mUpMinRangeS = "Z";
    private boolean mUpMinRangeOpenBound = true;

    private String mLowRealMinRange = "A";
    private boolean mLowRealMinRangeOpenBound = true;

    private String mUpRealMinRange = "Z";
    private boolean mUpRealMinRangeOpenBound = true;

    public AttributeNodeS(String attribute) {
        super(attribute);
        mAttribute = attribute;
    }

    public String getAttribute() {
        return this.mAttribute;
    }

    public boolean isValidInIntegerDomain() {
        return (getLowRealMinRangeS().compareTo(getUpRealMinRangeS()) <= 0)
                || (getLowRealMinRangeS().equals(getUpRealMinRangeS()) && !isLowRealMinRangeOpenBound() && !isUpRealMinRangeOpenBound());
    }

    /*CLOSED*/
    public String getLowClosedMinRangeS() {
        return this.mLowMinRangeS;
    }

    public void setLowClosedMinRangeS(String lowClosedMinRange, boolean openBound) {
        this.mLowMinRangeS = lowClosedMinRange;
        this.mLowMinRangeOpenBound = openBound;
    }

    public String getUpClosedMinRangeS() {
        return this.mUpMinRangeS;
    }

    public void setUpClosedMinRangeS(String upClosedMinRange, boolean openBound) {
        this.mUpMinRangeS = upClosedMinRange;
        this.mUpMinRangeOpenBound = openBound;
    }

    public boolean isLowClosedMinRangeOpenBound() {
        return this.mLowMinRangeOpenBound;
    }

    public boolean isUpClosedMinRangeOpenBound() {
        return this.mUpMinRangeOpenBound;
    }

    /*REAL*/
    public String getLowRealMinRangeS() {
        return this.mLowRealMinRange;
    }

    public void setLowRealMinRangeS(String lowRealMinRange, boolean openBound) {
        this.mLowRealMinRange = lowRealMinRange;
        this.mLowRealMinRangeOpenBound = openBound;
    }

    public boolean isLowRealMinRangeOpenBound() {
        return this.mLowRealMinRangeOpenBound;
    }


    public String getUpRealMinRangeS() {
        return this.mUpRealMinRange;
    }

    public void setUpRealMinRangeS(String upRealMinRange, boolean openBound) {
        this.mUpRealMinRange = upRealMinRange;
        this.mUpRealMinRangeOpenBound = openBound;
    }

    public boolean isUpRealMinRangeOpenBound() {
        return this.mUpRealMinRangeOpenBound;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(getAttribute());

        sb.append(";");

        if (isLowClosedMinRangeOpenBound()) {
            sb.append("(");
        } else {
            sb.append("[");
        }

        sb.append(getLowClosedMinRangeS());
        sb.append(",");
        sb.append(getUpClosedMinRangeS());

        if (isUpClosedMinRangeOpenBound()) {
            sb.append(")");
        } else {
            sb.append("]");
        }

        sb.append(";");

        if (isLowRealMinRangeOpenBound()) {
            sb.append("(");
        } else {
            sb.append("[");
        }
        sb.append(getLowRealMinRangeS());
        sb.append(",");
        sb.append(getUpRealMinRangeS());

        if (isUpRealMinRangeOpenBound()) {
            sb.append(")");
        } else {
            sb.append("]");
        }

        return sb.toString();
    }

}
