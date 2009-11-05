package mx.itesm.ddb.util.impl;

import mx.itesm.ddb.util.ConditionData;
import mx.itesm.ddb.util.ExpressionData;

/**
 * ConditionData of the form: [Expression]
 * 
 * @author jccastrejon
 * 
 */
public class ExpressionConditionData implements ConditionData {

    /**
     * Condition ExpressionData.
     */
    private ExpressionData expression;

    /**
     * Full constructor.
     * 
     * @param expression
     *            ExpressionData.
     */
    public ExpressionConditionData(final ExpressionData expression) {
	this.expression = expression;
    }

    @Override
    public ExpressionConditionData clone() {
	return new ExpressionConditionData(expression);
    }

    @Override
    public String toString() {
	return expression.toString();
    }

    /**
     * @return the expression
     */
    public ExpressionData getExpression() {
	return expression;
    }

    /**
     * @param expression
     *            the expression to set
     */
    public void setExpression(ExpressionData expression) {
	this.expression = expression;
    }
}
