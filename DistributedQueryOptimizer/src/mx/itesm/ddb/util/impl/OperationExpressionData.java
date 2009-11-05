package mx.itesm.ddb.util.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mx.itesm.ddb.util.ExpressionData;
import mx.itesm.ddb.util.ExpressionOperator;

/**
 * @author jccastrejon
 * 
 */
public class OperationExpressionData implements ExpressionData {

    /**
     * 
     */
    private ExpressionOperator operator;

    /**
     * 
     */
    private List<ExpressionData> expressions;

    /**
     * 
     * @param operator
     * @param expressions
     */
    public OperationExpressionData(ExpressionOperator operator, List<ExpressionData> expressions) {
	this.operator = operator;
	this.expressions = expressions;
    }

    /**
     * 
     * @param operator
     * @param expression
     */
    public OperationExpressionData(ExpressionOperator operator, ExpressionData expression) {
	this.operator = operator;
	this.expressions = new ArrayList<ExpressionData>(1);
	this.expressions.add(expression);
    }

    /**
     * 
     * @param operator
     * @param expressions
     */
    public OperationExpressionData(ExpressionOperator operator, ExpressionData... expressions) {
	this.operator = operator;
	this.expressions = Arrays.asList(expressions);
    }

    @Override
    public OperationExpressionData clone() {
	return new OperationExpressionData(this.operator, this.expressions);
    }

    @Override
    public String toString() {
	StringBuilder returnValue = new StringBuilder();

	for (int i = 0; i < expressions.size(); i++) {
	    returnValue.append(expressions.get(i));

	    if (i != expressions.size() - 1) {
		returnValue.append(operator);
	    }
	}

	return returnValue.toString();
    }

    /**
     * @return the operator
     */
    public ExpressionOperator getOperator() {
	return operator;
    }

    /**
     * @param operator
     *            the operator to set
     */
    public void setOperator(ExpressionOperator operator) {
	this.operator = operator;
    }

    /**
     * @return the expressions
     */
    public List<ExpressionData> getExpressions() {
	return expressions;
    }

    /**
     * @param expressions
     *            the expressions to set
     */
    public void setExpressions(List<ExpressionData> expressions) {
	this.expressions = expressions;
    }
}
