package mx.itesm.ddb.util.impl;

import mx.itesm.ddb.util.ExpressionData;

/**
 * SQL Expression of the form: [Expression] as [alias]
 * 
 * @author jccastrejon
 * 
 */
public class AliasExpressionData implements ExpressionData {

    /**
     * SQL Expression.
     */
    ExpressionData expression;

    /**
     * Expression alias.
     */
    String alias;

    /**
     * Full constructor.
     * 
     * @param expression
     *            SQL Expression.
     * @param alias
     *            Expression alias.
     */
    public AliasExpressionData(final ExpressionData expression, final String alias) {
	this.expression = expression;
	this.alias = alias;
    }

    @Override
    public AliasExpressionData clone() {
	return new AliasExpressionData(this.expression, this.alias);
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
