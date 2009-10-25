package mx.itesm.ddb.util.impl;

import mx.itesm.ddb.util.ExpressionData;

/**
 * @author jccastrejon
 * 
 */
public class AliasExpressionData implements ExpressionData {

    /**
     * 
     */
    ExpressionData expression;

    /**
     * 
     */
    String alias;

    /**
     * 
     * @param expression
     * @param alias
     */
    public AliasExpressionData(ExpressionData expression, String alias) {
	this.expression = expression;
	this.alias = alias;
    }

    @Override
    public String toString() {
	return "(" + this.expression + ") as " + alias;
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

    /**
     * @return the alias
     */
    public String getAlias() {
	return alias;
    }

    /**
     * @param alias
     *            the alias to set
     */
    public void setAlias(String alias) {
	this.alias = alias;
    }
}
