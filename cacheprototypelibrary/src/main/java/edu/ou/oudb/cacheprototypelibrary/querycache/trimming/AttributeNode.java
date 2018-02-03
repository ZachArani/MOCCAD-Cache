package edu.ou.oudb.cacheprototypelibrary.querycache.trimming;


public class AttributeNode {

    private String mAttribute;
    private double mLowMinRange = Double.NEGATIVE_INFINITY;
    private boolean mLowMinRangeOpenBound = true;

    private double mUpMinRange = Double.POSITIVE_INFINITY;
    private boolean mUpMinRangeOpenBound = true;

    private double mLowRealMinRange = Double.NEGATIVE_INFINITY;
    private boolean mLowRealMinRangeOpenBound = true;

    private double mUpRealMinRange = Double.POSITIVE_INFINITY;
    private boolean mUpRealMinRangeOpenBound = true;

    public AttributeNode(String attribute) {
        mAttribute = attribute;
    }

    /**
     * @return the attribute
     */
    public String getAttribute() {
        return this.mAttribute;
    }

    /**
     * @param attribute the attribute to set
     */
    public void setAttribute(String attribute) {
        if (attribute != null) {
            this.mAttribute = attribute;
        }
    }

    /**
     * check is the node is valid in the real domain
     * that is:
     * If any Alow(X) < Aup(X)
     * or Alow(X) == Aup(X) with all bound "closed"
     *
     * @return true if above condition respected, false otherwise
     */
    public boolean isValidInRealDomain() {
        return (getLowRealMinRange() < getUpRealMinRange()
                || (getLowRealMinRange() == getUpRealMinRange() && !isLowRealMinRangeOpenBound() && !isUpRealMinRangeOpenBound()));
    }

    /**
     * check is the node is valid in the real domain
     * that is:
     * If any Alow(X) < Aup(X)
     *
     * @return true if above condition respected, false otherwise
     */
    public boolean isValidInIntegerDomain() {
        return (getLowRealMinRange() <= getUpRealMinRange());
    }

    /**
     * @return the lowClosedMinRange
     */
    public double getLowClosedMinRange() {
        return this.mLowMinRange;
    }

    /**
     * @param lowClosedMinRange the lowClosedMinRange to set
     * @param openBound         the openBound to set
     */
    public void setLowClosedMinRange(double lowClosedMinRange, boolean openBound) {
        this.mLowMinRange = lowClosedMinRange;
        this.mLowMinRangeOpenBound = openBound;
    }

    /**
     * @return the lowClosedMinRangeOpenBound
     */
    public boolean isLowClosedMinRangeOpenBound() {
        return this.mLowMinRangeOpenBound;
    }


    /**
     * @return the upClosedMinRange
     */
    public double getUpClosedMinRange() {
        return this.mUpMinRange;
    }

    /**
     * @param upClosedMinRange the upClosedMinRange to set
     * @param openBound        the openBound to set
     */
    public void setUpClosedMinRange(double upClosedMinRange, boolean openBound) {
        this.mUpMinRange = upClosedMinRange;
        this.mUpMinRangeOpenBound = openBound;
    }

    /**
     * @return the upClosedMinRangeOpenBound
     */
    public boolean isUpClosedMinRangeOpenBound() {
        return this.mUpMinRangeOpenBound;
    }

    /**
     * @return the lowRealMinRange
     */
    public double getLowRealMinRange() {
        return this.mLowRealMinRange;
    }

    /**
     * @param lowRealMinRange the lowRealMinRange to set
     * @param openBound       the openBound to set
     */
    public void setLowRealMinRange(double lowRealMinRange, boolean openBound) {
        this.mLowRealMinRange = lowRealMinRange;
        this.mLowRealMinRangeOpenBound = openBound;
    }

    /**
     * @return the lowRealMinRangeOpenBound
     */
    public boolean isLowRealMinRangeOpenBound() {
        return this.mLowRealMinRangeOpenBound;
    }

    /**
     * @return the upRealMinRange
     */
    public double getUpRealMinRange() {
        return this.mUpRealMinRange;
    }

    /**
     * @param upRealMinRange the upRealMinRange to set
     * @param openBound      the openBound to set
     */
    public void setUpRealMinRange(double upRealMinRange, boolean openBound) {
        this.mUpRealMinRange = upRealMinRange;
        this.mUpRealMinRangeOpenBound = openBound;
    }

    /**
     * @return the upRealMinRangeOpenBound
     */
    public boolean isUpRealMinRangeOpenBound() {
        return this.mUpRealMinRangeOpenBound;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((mAttribute == null) ? 0 : mAttribute.hashCode());
        long temp;
        temp = Double.doubleToLongBits(mLowMinRange);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + (mLowMinRangeOpenBound ? 1231 : 1237);
        temp = Double.doubleToLongBits(mUpMinRange);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + (mUpMinRangeOpenBound ? 1231 : 1237);
        return result;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AttributeNode other = (AttributeNode) obj;
        if (mAttribute == null) {
            if (other.mAttribute != null)
                return false;
        } else if (!mAttribute.equals(other.mAttribute))
            return false;
        if (Double.doubleToLongBits(mLowMinRange) != Double
                .doubleToLongBits(other.mLowMinRange))
            return false;
        if (mLowMinRangeOpenBound != other.mLowMinRangeOpenBound)
            return false;
        if (Double.doubleToLongBits(mLowRealMinRange) != Double
                .doubleToLongBits(other.mLowRealMinRange))
            return false;
        if (mLowRealMinRangeOpenBound != other.mLowRealMinRangeOpenBound)
            return false;
        if (Double.doubleToLongBits(mUpMinRange) != Double
                .doubleToLongBits(other.mUpMinRange))
            return false;
        if (mUpMinRangeOpenBound != other.mUpMinRangeOpenBound)
            return false;
        if (Double.doubleToLongBits(mUpRealMinRange) != Double
                .doubleToLongBits(other.mUpRealMinRange))
            return false;
        if (mUpRealMinRangeOpenBound != other.mUpRealMinRangeOpenBound)
            return false;
        return true;
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

        sb.append(getLowClosedMinRange());
        sb.append(",");
        sb.append(getUpClosedMinRange());

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
        sb.append(getLowRealMinRange());
        sb.append(",");
        sb.append(getUpRealMinRange());

        if (isUpRealMinRangeOpenBound()) {
            sb.append(")");
        } else {
            sb.append("]");
        }

        return sb.toString();
    }

}
