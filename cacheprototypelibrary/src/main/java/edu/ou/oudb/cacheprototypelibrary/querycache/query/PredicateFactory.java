package edu.ou.oudb.cacheprototypelibrary.querycache.query;

import edu.ou.oudb.cacheprototypelibrary.querycache.exception.InvalidPredicateException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.TrivialPredicateException;

/**
 * @author Mikael Perrin
 * @since 1.0
 * Class used to create predicate based on the String operands and operator
 */
public class PredicateFactory {

    /**
     * Method used to create a prediate
     *
     * @param leftOperand  the left operand of the predicate
     * @param operator     the operator
     * @param rightOperand the right operand of the predicate
     * @return the instance of the corresponding predicate
     * @throws TrivialPredicateException thrown on trivial predicate: e.g. X=X, 2=2
     * @throws InvalidPredicateException thrown on invalid predicate: e.g. X?X, 2=3
     */
    public static Predicate createPredicate(String leftOperand, String operator, String rightOperand)
            throws TrivialPredicateException, InvalidPredicateException {
        Predicate newPredicate = null;

        if (leftOperand == null || operator == null || rightOperand == null) {
            throw new InvalidPredicateException();
        }

        boolean leftOpIsDigit = Character.isDigit(leftOperand.charAt(0));
        boolean rightOpIsDigit = Character.isDigit(rightOperand.charAt(0));

        if (!leftOpIsDigit && rightOpIsDigit) {
            //if XopC
            newPredicate = new XopCPredicate(leftOperand, operator, Double.parseDouble(rightOperand));
        } else {
            //if XopY
            newPredicate = new XopYPredicate(leftOperand, operator, rightOperand);
        }

        return newPredicate;
    }

    /**
     * Used to copy a predicate to another instance
     *
     * @param other the predicate to copy
     * @return the new instance of predicate
     * @throws InvalidPredicateException thrown on invalid predicate: e.g. X?X, 2=3
     * @throws TrivialPredicateException thrown on trivial predicate: e.g. X=X, 2=2
     */
    public static Predicate copyPredicate(Predicate other)
            throws InvalidPredicateException, TrivialPredicateException {
        Predicate retPredicate = null;
        if (other instanceof XopCPredicate) {
            XopCPredicate p = (XopCPredicate) other;
            retPredicate = new XopCPredicate(p.getLeftOperand(), p.getOperator(), p.getRightOperand());
        } else //other instanceof XopYPredicate
        {
            XopYPredicate p = (XopYPredicate) other;
            retPredicate = new XopYPredicate(p.getLeftOperand(), p.getOperator(), p.getRightOperand());
        }

        return retPredicate;
    }
}
