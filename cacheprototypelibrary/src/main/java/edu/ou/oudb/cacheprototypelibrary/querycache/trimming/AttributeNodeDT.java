package edu.ou.oudb.cacheprototypelibrary.querycache.trimming;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by chenxiao on 6/19/17.
 */

/*Same class as AttributeNode but used to handle the cache for the Date and the Time
* Bounds are Dates and comparison is done using before() and after()*/
public class AttributeNodeDT extends AttributeNode {
    private Calendar low = new GregorianCalendar(1970, Calendar.JANUARY, 1);
    private Calendar up = Calendar.getInstance(); // curDate

    private String mAttribute;

    private Date mLowMinRangeDT = low.getTime();
    private boolean mLowMinRangeOpenBound = true;

    private Date mUpMinRangeDT = up.getTime();
    private boolean mUpMinRangeOpenBound = true;

    private Date mLowRealMinRange = low.getTime();
    private boolean mLowRealMinRangeOpenBound = true;

    private Date mUpRealMinRange = up.getTime();
    private boolean mUpRealMinRangeOpenBound = true;

    public AttributeNodeDT(String attribute) {
        super(attribute);
        mAttribute = attribute;
    }

    public String getAttribute() {
        return this.mAttribute;
    }

    public boolean isValidInIntegerDomain() {
        return getLowRealMinRangeDT().before(getUpRealMinRangeDT())
                || (getLowRealMinRangeDT().equals(getUpRealMinRangeDT()) && !isLowRealMinRangeOpenBound() && !isUpRealMinRangeOpenBound());
    }

    /*CLOSED*/
    public Date getLowClosedMinRangeDT() {
        return this.mLowMinRangeDT;
    }

    public void setLowClosedMinRangeDT(Date lowClosedMinRange, boolean openBound) {
        this.mLowMinRangeDT = lowClosedMinRange;
        this.mLowMinRangeOpenBound = openBound;
    }

    public Date getUpClosedMinRangeDT() {
        return this.mUpMinRangeDT;
    }

    public void setUpClosedMinRangeDT(Date upClosedMinRange, boolean openBound) {
        this.mUpMinRangeDT = upClosedMinRange;
        this.mUpMinRangeOpenBound = openBound;
    }

    public boolean isLowClosedMinRangeOpenBound() {
        return this.mLowMinRangeOpenBound;
    }

    public boolean isUpClosedMinRangeOpenBound() {
        return this.mUpMinRangeOpenBound;
    }

    /*REAL*/
    public Date getLowRealMinRangeDT() {
        return this.mLowRealMinRange;
    }

    public void setLowRealMinRangeDT(Date lowRealMinRange, boolean openBound) {
        this.mLowRealMinRange = lowRealMinRange;
        this.mLowRealMinRangeOpenBound = openBound;
    }

    public boolean isLowRealMinRangeOpenBound() {
        return this.mLowRealMinRangeOpenBound;
    }


    public Date getUpRealMinRangeDT() {
        return this.mUpRealMinRange;
    }

    public void setUpRealMinRangeDT(Date upRealMinRange, boolean openBound) {
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

        sb.append(getLowClosedMinRangeDT());
        sb.append(",");
        sb.append(getUpClosedMinRangeDT());

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
        sb.append(getLowRealMinRangeDT());
        sb.append(",");
        sb.append(getUpRealMinRangeDT());

        if (isUpRealMinRangeOpenBound()) {
            sb.append(")");
        } else {
            sb.append("]");
        }

        return sb.toString();
    }

}
