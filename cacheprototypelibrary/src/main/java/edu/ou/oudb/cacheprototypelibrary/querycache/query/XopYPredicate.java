package edu.ou.oudb.cacheprototypelibrary.querycache.query;

import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ou.oudb.cacheprototypelibrary.metadata.Metadata;
import edu.ou.oudb.cacheprototypelibrary.metadata.ObjectSizer;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.InvalidPredicateException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.TrivialPredicateException;

/**
 * @author Mikael Perrin
 * @since 1.0
 * Definition of a predicate with Attribute-Operator-Attribute
 */
public class XopYPredicate extends Predicate {

    private String mLeftOperand;
    private String mRightOperand;
    /*Used for the Date and Time*/
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
    private Date curDate = null;
    private String month = null;
    private String percent = "%27";
    //TODO: Just like in SearchExam --> Save these arrays in a database or something
    // so it's initialized only once
    /*TODO: If we do that, replace mRightOperand to mRightOperand.replace("%27%,"").replace("20", " ") in switchAttributeUp and switchAttributeLow methods
    * and then put it back to what it was
    * mRightOperand comes here under the following format: %27Measurement%20of%20Body%27
    * Using both replace, you get "Measurement of Body"*/


    /*IMPORTANT
    * These arrays need to be sorted alphabetically
    * They are used to see if there is a Cache extended hit equivalent
    * (e.g. name <= Nicholas is equivalent to name < Pamela)
    * This thing is not possible using the alphabet because
    * name < Pamela would be equivalent to name <= O....*/
    private String[] patientsfirstnamesArray = new String[]{
            "%27Aaron%27", "%27Angela%27", "%27Anthony%27", "%27Arthur%27", "%27Betty%27", "%27Brian%27",
            "%27Carol%27", "%27Catherine%27", "%27Debra%27", "%27Denise%27", "%27Edward%27", "%27Eugene%27",
            "%27Evelyn%27", "%27Fred%27", "%27Gloria%27", "%27Jane%27", "%27Jason%27", "%27Joan%27",
            "%27Jonathan%27", "%27Juan%27", "%27Judith%27", "%27Julie%27", "%27Justin%27", "%27Kathleen%27",
            "%27Margaret%27", "%27Maria%27", "%27Melissa%27", "%27Michael%27", "%27Nicholas%27", "%27Pamela%27",
            "%27Patrick%27", "%27Paula%27", "%27Peter%27", "%27Robin%27", "%27Ronald%27", "%27Ruby%27",
            "%27Sara%27", "%27Sarah%27", "%27Steve%27", "%27Tammy%27", "%27Wade%27", "%27Wayne%27"
    };

    private String[] patientslastnamesArray = new String[]{
            "%27Adams%27", "%27Anderson%27", "%27Baker%27", "%27Barnes%27", "%27Berry%27", "%27Campbell%27",
            "%27Carr%27", "%27Carroll%27", "%27Chapman%27", "%27Coleman%27", "%27Collins%27", "%27Daniels%27",
            "%27Davis%27", "%27Day%27", "%27Diaz%27", "%27Fernandez%27", "%27Gardner%27", "%27Garrett%27",
            "%27George%27", "%27Gibson%27", "%27Hicks%27", "%27Hughes%27", "%27Jackson%27", "%27Jordan%27",
            "%27Lane%27", "%27Lopez%27", "%27Martin%27", "%27Miller%27", "%27Oliver%27", "%27Owens%27",
            "%27Perkins%27", "%27Powell%27", "%27Price%27", "%27Ramos%27", "%27Ray%27", "%27Richardson%27",
            "%27Russell%27", "%27Stanley%27", "%27Stewart%27", "%27Wagner%27", "%27Washington%27", "%27Weaver%27",
            "%27Welch%27", "%27Wells%27", "%27Wood%27"
    };

    private String[] doctorsfirstnamesArray = new String[]{
            "%27Alice%27", "%27Amy%27", "%27Anna%27", "%27Annie%27", "%27Anthony%27", "%27Betty%27",
            "%27Brandon%27", "%27Carl%27", "%27Cheryl%27", "%27Daniel%27", "%27Donald%27", "%27Earl%27",
            "%27Elizabeth%27", "%27George%27", "%27Gloria%27", "%27Harry%27", "%27Jason%27", "%27Jean%27",
            "%27Jessica%27", "%27John%27", "%27Johnny%27", "%27Joseph%27", "%27Julie%27", "%27Katherine%27",
            "%27Laura%27", "%27Lillian%27", "%27Lori%27", "%27Louis%27", "%27Margaret%27", "%27Marie%27",
            "%27Nancy%27", "%27Pamela%27", "%27Paul%27", "%27Raymond%27", "%27Ryan%27", "%27Shirley%27",
            "%27Tammy%27", "%27Teresa%27", "%27Theresa%27", "%27Wanda%27", "%27Wayne%27"
    };

    private String[] doctorslastnamesArray = new String[]{
            "%27Alvarez%27", "%27Campbell%27", "%27Crawford%27", "%27Davis%27", "%27Dixon%27", "%27Fisher%27",
            "%27Fox%27", "%27Franklin%27", "%27George%27", "%27Graham%27", "%27Greene%27", "%27Griffin%27",
            "%27Hamilton%27", "%27Hansen%27", "%27Harrison%27", "%27Hawkins%27", "%27Holmes%27", "%27Jordan%27",
            "%27Lawrence%27", "%27Marshall%27", "%27Matthews%27", "%27Mendoza%27", "%27Morgan%27", "%27Myers%27",
            "%27Payne%27", "%27Perkins%27", "%27Price%27", "%27Ramirez%27", "%27Ray%27", "%27Roberts%27",
            "%27Robertson%27", "%27Spencer%27", "%27Stanley%27", "%27Thompson%27", "%27Ward%27", "%27Washington%27",
            "%27Weaver%27", "%27West%27", "%27Willis%27"
    };

    private String[] descriptionsArray = new String[]{"%27Analysis%20of%20Body%20Flu%27", "%27Biopsy%27", "%27Endoscopy%27",
            "%27Genetic%20Testing%27", "%27Imaging%27", "%27Measurement%20of%20Body%27"};

    /**
     * The XopYPredicate constructor
     *
     * @param leftOperand  the left operand of the predicate
     * @param operator     the predicate operator
     * @param rightOperand the right operand of the predicate
     * @throws InvalidPredicateException thrown when predicate is invalid
     * @throws TrivialPredicateException thrown when predicate is useless
     */
    public XopYPredicate(String leftOperand, String operator, String rightOperand)
            throws InvalidPredicateException, TrivialPredicateException {
        super(operator);
        mLeftOperand = leftOperand;
        mRightOperand = rightOperand;

        if (!isValidPredicate()) {
            throw new InvalidPredicateException();
        } else if (isTrivialPredicate()) {
            throw new TrivialPredicateException();
        }
    }

    @Override
    public boolean isValidPredicate() {
        boolean isValidPredicate = hasValidOperator();

        if ((mLeftOperand.equals(mRightOperand)) &&
                (mOperator == "<" || mOperator == ">" || mOperator == "<>")) {
            isValidPredicate = false;
        }
        return isValidPredicate;
    }


    @Override
    public boolean isTrivialPredicate() {
        boolean isTrivialPredicate = false;
        if (mLeftOperand == mRightOperand) {
            isTrivialPredicate = true;
        }

        return isTrivialPredicate;
    }

    /*Gets the month as a number, used for the comparison of the Date*/
    private void getMonth(String mon) {
        switch (mon) {
            case "Jan":
                month = "1";
                break;
            case "Feb":
                month = "2";
                break;
            case "Mar":
                month = "3";
                break;
            case "Apr":
                month = "4";
                break;
            case "May":
                month = "5";
                break;
            case "Jun":
                month = "6";
                break;
            case "Jul":
                month = "7";
                break;
            case "Aug":
                month = "8";
                break;
            case "Sep":
                month = "9";
                break;
            case "Oct":
                month = "10";
                break;
            case "Nov":
                month = "11";
                break;
            case "Dec":
                month = "12";
                break;
        }
    }

    /*Methods used to convert < to <= and > to >=
    * It is used for the case of the CACHE_EXTENDED_HIT_EQUIVALENT*/
    private void switchAttributeUp() {
        /*String used to increase or decrease the bounds
        * (e.g. with numbers --> > 19 equals >= 20)
        * Here we do the same, for instance > Imaging equals >= Measurement of Body*/
        String swap = null;
        boolean found = false;

        /*There are some problems when trying to do < minValue or > maxValue
        * That is why we only use "=" atm*/
        switch (mLeftOperand) {
            //FIXME: Pb is probably here but I don't even know
            case "description":
                for (int i = 0; i < descriptionsArray.length; i++) {
                    if (i == descriptionsArray.length - 1 && !found) {
                        swap = "["; //[ is greater than Z, use compareTo to see
                    } else {
                        if (mRightOperand.equals(descriptionsArray[i])) {
                            swap = descriptionsArray[i + 1];
                            found = true;
                        }
                    }
                }
                mRightOperand = swap;
                break;
            case "patientfirstname":
                for (int i = 0; i < patientsfirstnamesArray.length; i++) {
                    if (i == patientsfirstnamesArray.length - 1 && !found) {
                        swap = "";
                    } else {
                        if (mRightOperand.equals(patientsfirstnamesArray[i])) {
                            swap = patientsfirstnamesArray[i + 1];
                            found = true;
                        }
                    }
                }
                mRightOperand = swap;
                break;
            case "patientlastname":
                for (int i = 0; i < patientslastnamesArray.length; i++) {
                    if (i == patientslastnamesArray.length - 1 && !found) {
                        swap = "";
                    } else {
                        if (mRightOperand.equals(patientslastnamesArray[i])) {
                            swap = patientslastnamesArray[i + 1];
                            found = true;
                        }
                    }
                }
                mRightOperand = swap;
                break;
            case "doctorfirstname":
                for (int i = 0; i < doctorsfirstnamesArray.length; i++) {
                    if (i == doctorsfirstnamesArray.length - 1 && !found) {
                        swap = "";
                    } else {
                        if (mRightOperand.equals(doctorsfirstnamesArray[i])) {
                            swap = doctorsfirstnamesArray[i + 1];
                            found = true;
                        }
                    }
                }
                mRightOperand = swap;
                break;
            case "doctorlastname":
                for (int i = 0; i < doctorslastnamesArray.length; i++) {
                    if (i == doctorslastnamesArray.length - 1 && !found) {
                        swap = "";
                    } else {
                        if (mRightOperand.equals(doctorslastnamesArray[i])) {
                            swap = doctorslastnamesArray[i + 1];
                            found = true;
                        }
                    }
                }
                mRightOperand = swap;
                break;
            /*Below works for Date and Time*/
            /*Date*/
            case "substr(p_date_time,0,10)":
                try {
                    /*Gets the Date under the following format :
                    * yyyy-MM-dd*/
                    curDate = sdf.parse(mRightOperand.replace("%27", ""));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                /*We use a calendar to get the next day*/
                Calendar calendar = Calendar.getInstance();
                /*First, set it up at the curDate*/
                calendar.setTime(curDate);
                /*Then gets the next day*/
                calendar.add(Calendar.DATE, +1);
                /*Convert into a Date*/
                Date tomorrow = calendar.getTime();
                /*We build the String so it can be passed correctly afterwards*/
                String day = tomorrow.toString().split(" ")[2];
                month = tomorrow.toString().split(" ")[1];
                /*Convert the month from e.g. "Jan" to "1" to respect the format yyyy-MM-dd*/
                getMonth(month);
                String year = tomorrow.toString().split(" ")[5].replace("%27", "");
                /*Save the value into mRightOperand
                * Will be used in GuoEtAlPredicateAnalyzer
                * The %27 are needed at the beginning and at the end,
                * otherwise it will create an Exception in GuoEtAlPredicateAnalyzer*/
                mRightOperand = percent.concat(year + "-" + month + "-" + day).concat(percent);
                break;
            /*Time*/
            case "substr(p_date_time,12)":
                try {
                    /*Gets the Time under the following format :
                    * HH:mm:ss*/
                    curDate = sdfTime.parse(mRightOperand.replace("%27", ""));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                /*We use a calendar to get the previous day*/
                Calendar calendarTime = Calendar.getInstance();
                /*First, set it up at the curDate*/
                calendarTime.setTime(curDate);
                /*Then gets the previous day*/
                calendarTime.add(Calendar.MINUTE, +1);
                /*Convert into a Date*/
                Date timeMinus1Minute = calendarTime.getTime();
                /*We build the String so it can be passed correctly afterwards*/
                String hours = timeMinus1Minute.toString().split(" ")[3].split(":")[0];
                String minutes = timeMinus1Minute.toString().split(" ")[3].split(":")[1];
                String seconds = timeMinus1Minute.toString().split(" ")[3].split(":")[2];
                /*Save the value into mRightOperand
                * Will be used in GuoEtAlPredicateAnalyzer*/
                mRightOperand = percent.concat(hours + ":" + minutes + ":" + seconds).concat(percent);
                break;
        }
    }

    private void switchAttributeLow() {
        String swap = null;
        boolean found = false;

        switch (mLeftOperand) {
            case "description":
                for (int i = 0; i < descriptionsArray.length; i++) {
                    if (i == 0 && !found) {
                        swap = "."; // "." is lower than "A"
                    } else {
                        if (mRightOperand.equals(descriptionsArray[i])) {
                            swap = descriptionsArray[i - 1];
                            found = true;
                        }
                    }
                }
                mRightOperand = swap;
                break;
            case "patientfirstname":
                for (int i = 0; i < patientsfirstnamesArray.length; i++) {
                    if (i == 0 && !found) {
                        swap = "";
                    } else {
                        if (mRightOperand.equals(patientsfirstnamesArray[i])) {
                            swap = patientsfirstnamesArray[i - 1];
                            found = true;
                        }
                    }
                }
                mRightOperand = swap;
                break;
            case "patientlastname":
                for (int i = 0; i < patientslastnamesArray.length; i++) {
                    if (i == 0 && !found) {
                        swap = "";
                    } else {
                        if (mRightOperand.equals(patientslastnamesArray[i])) {
                            swap = patientslastnamesArray[i - 1];
                            found = true;
                        }
                    }
                }
                mRightOperand = swap;
                break;
            case "doctorfirstname":
                for (int i = 0; i < doctorsfirstnamesArray.length; i++) {
                    if (i == 0 && !found) {
                        swap = "";
                    } else {
                        if (mRightOperand.equals(doctorsfirstnamesArray[i])) {
                            swap = doctorsfirstnamesArray[i - 1];
                            found = true;
                        }
                    }
                }
                mRightOperand = swap;
                break;
            case "doctorlastname":
                for (int i = 0; i < doctorslastnamesArray.length; i++) {
                    if (i == 0 && !found) {
                        swap = "";
                    } else {
                        if (mRightOperand.equals(doctorslastnamesArray[i])) {
                            swap = doctorslastnamesArray[i - 1];
                            found = true;
                        }
                    }
                }
                mRightOperand = swap;
                break;
            /*Date*/
            case "substr(p_date_time,0,10)":
                try {
                    /*Gets the Date under the following format :
                    * yyyy-MM-dd*/
                    curDate = sdf.parse(mRightOperand.replace("%27", ""));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                /*We use a calendar to get the previous day*/
                Calendar calendar = Calendar.getInstance();
                /*First, set it up at the curDate*/
                calendar.setTime(curDate);
                /*Then gets the previous day*/
                calendar.add(Calendar.DATE, -1);
                /*Convert into a Date*/
                Date yesterday = calendar.getTime();
                /*We build the String so it can be passed correctly afterwards*/
                String day = yesterday.toString().split(" ")[2];
                month = yesterday.toString().split(" ")[1];
                /*Convert the month from e.g. "Jan" to "01"*/
                getMonth(month);
                String year = yesterday.toString().split(" ")[5].replace("%27", "");
                /*Save the value into mRightOperand
                * Will be used in GuoEtAlPredicateAnalyzer*/
                mRightOperand = percent.concat(year + "-" + month + "-" + day).concat(percent);
                break;
            /*Time*/
            case "substr(p_date_time,12)":
                try {
                    /*Gets the Time under the following format :
                    * HH:mm:ss*/
                    curDate = sdfTime.parse(mRightOperand.replace("%27", ""));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                /*We use a calendar to get the previous day*/
                Calendar calendarTime = Calendar.getInstance();
                /*First, set it up at the curDate*/
                calendarTime.setTime(curDate);
                /*Then gets the previous day*/
                calendarTime.add(Calendar.MINUTE, -1);
                /*Convert into a Date*/
                Date timeMinus1Minute = calendarTime.getTime();
                /*We build the String so it can be passed correctly afterwards*/
                String hours = timeMinus1Minute.toString().split(" ")[3].split(":")[0];
                String minutes = timeMinus1Minute.toString().split(" ")[3].split(":")[1];
                String seconds = timeMinus1Minute.toString().split(" ")[3].split(":")[2];
                /*Save the value into mRightOperand
                * Will be used in GuoEtAlPredicateAnalyzer*/
                mRightOperand = percent.concat(hours + ":" + minutes + ":" + seconds).concat(percent);
                break;
        }
    }

    /*Method used in GuoEtAlPredicateAnalyzer*/
    @Override
    public boolean transformToRealDomainPredicate() {

        boolean transformed = true;

        switch (mOperator) {
            case ">":
                /*Changes operator and take next value
                * (e.g. value > "M" is equivalent to value >= "N")*/
                mOperator = ">=";
                switchAttributeUp();
                break;
            case "<":
                mOperator = "<=";
                switchAttributeLow();
                break;
            default:
                transformed = false;
                break;
        }

        return transformed;
    }

    @Override
    public boolean transformToIntegerDomainPredicate() {
        return transformToRealDomainPredicate();
    }

    @Override
    public boolean apply(String relation, List<String> tuple) throws NumberFormatException {
        int indexLeft;
        String valueLeft;

        /*In case it is the date or the time:
        * We set the attribute back to be p_date_time, so we can get the right attribute index
        * By doing that we get the whole tuple, so we split to get date or time
        * Time needs the substring to have the correct format (i.e. HH:MI:SS)
        * Then we put attribute back to the substr...
        * This way, we go through the switch case once again if we have multiple results*/
        switch (mLeftOperand) {
            case "substr(p_date_time,0,10)":
                mLeftOperand = "p_date_time";
                indexLeft = Metadata.getInstance().getRelationMetadata(relation).getAttributeIndex(mLeftOperand);
                valueLeft = tuple.get(indexLeft).split(" ")[0];
                mLeftOperand = "substr(p_date_time,0,10)";
                break;
            case "substr(p_date_time,12)":
                mLeftOperand = "p_date_time";
                indexLeft = Metadata.getInstance().getRelationMetadata(relation).getAttributeIndex(mLeftOperand);
                valueLeft = tuple.get(indexLeft).split(" ")[1].substring(0, 8);
                mLeftOperand = "substr(p_date_time,12)";
                break;
            default:
                indexLeft = Metadata.getInstance().getRelationMetadata(relation).getAttributeIndex(mLeftOperand);
                valueLeft = tuple.get(indexLeft);
                break;
        }

        String valueRight = mRightOperand.replace("%27", "");

        boolean isValidTuple = false;

        switch (mOperator) {
            case "<":
                isValidTuple = valueLeft.compareTo(valueRight) < 0;
                break;
            case ">":
                isValidTuple = valueLeft.compareTo(valueRight) > 0;
                break;
            case "<=":
                isValidTuple = valueLeft.compareTo(valueRight) <= 0;
                break;
            case ">=":
                isValidTuple = valueLeft.compareTo(valueRight) >= 0;
                break;
            case "=":
                isValidTuple = valueLeft.compareTo(valueRight) == 0;
                break;
            case "<>":
                isValidTuple = valueLeft.compareTo(valueRight) != 0;
                break;
        }

        return isValidTuple;
    }

    @Override
    public Set<String> getAttributes() {
        Set<String> attributeSet = new HashSet<String>();
        attributeSet.add(mLeftOperand);
        return attributeSet;
    }

    /**
     * @return the leftOperand
     */
    public String getLeftOperand() {
        return this.mLeftOperand;
    }

    /**
     * @param leftOperand the leftOperand to set
     */
    public void setLeftOperand(String leftOperand) {
        if (leftOperand != null) {
            this.mLeftOperand = leftOperand;
        }
    }

    /**
     * @return the rightOperand
     */
    public String getRightOperand() {
        return this.mRightOperand;
    }

    /**
     * @param rightOperand the rightOperand to set
     */
    public void setRightOperand(String rightOperand) {
        if (rightOperand != null) {
            this.mRightOperand = rightOperand;
        }
    }

    @Override
    public String getSerializedLeftOperand() {
        return this.mLeftOperand;
    }

    @Override
    public String getSerializedRightOperand() {
        return this.mRightOperand;
    }

    @Override
    public long size() {
        return ObjectSizer.getStringSize32bits(mLeftOperand.length())
                + ObjectSizer.getStringSize32bits(mOperator.length())
                + ObjectSizer.getStringSize32bits(mRightOperand.length());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                + ((mLeftOperand == null) ? 0 : mLeftOperand.hashCode());
        result = prime * result
                + ((mRightOperand == null) ? 0 : mRightOperand.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        XopYPredicate other = (XopYPredicate) obj;
        if (mLeftOperand == null) {
            if (other.mLeftOperand != null) {
                return false;
            }
        } else if (!mLeftOperand.equals(other.mLeftOperand)) {
            return false;
        }
        if (mRightOperand == null) {
            if (other.mRightOperand != null) {
                return false;
            }
        } else if (!mRightOperand.equals(other.mRightOperand)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(mLeftOperand);
        sb.append(" ");
        sb.append(mOperator);
        sb.append(" ");
        sb.append(mRightOperand);
        return sb.toString();
    }

}
