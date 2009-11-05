package mx.itesm.ddb.util.impl;

import mx.itesm.ddb.util.ExpressionData;

/**
 * @author jccastrejon
 * 
 */
public class SimpleExpressionData implements ExpressionData {

    /**
     * 
     */
    private String expression;

    /**
     * 
     * @param expression
     */
    public SimpleExpressionData(String expression) {
	this.expression = expression;
    }

    @Override
    public SimpleExpressionData clone() {
	return new SimpleExpressionData(this.expression);
    }

    @Override
    public String toString() {
	return this.expression.replace('"', '\'');
    }

    /**
     * @return the expression
     */
    public String getExpression() {
	return expression;
    }

    /**
     * @param expression
     *            the expression to set
     */
    public void setExpression(String expression) {
	this.expression = expression;
    }
}
