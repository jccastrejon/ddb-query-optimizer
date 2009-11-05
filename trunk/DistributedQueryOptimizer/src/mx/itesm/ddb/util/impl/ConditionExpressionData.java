package mx.itesm.ddb.util.impl;

import mx.itesm.ddb.util.ConditionData;
import mx.itesm.ddb.util.ExpressionData;

/**
 * SQL Expression of the form: [ConditionData]
 * 
 * @author jccastrejon
 * 
 */
public class ConditionExpressionData implements ExpressionData {

    /**
     * ConditionData.
     */
    ConditionData condition;

    /**
     * Full constructor.
     * 
     * @param condition
     *            ConditionData.
     */
    public ConditionExpressionData(final ConditionData condition) {
	this.condition = condition;
    }

    @Override
    public ConditionExpressionData clone() {
	return new ConditionExpressionData(this.condition);
    }

    @Override
    public String toString() {
	return this.condition.toString();
    }

    /**
     * @return the condition
     */
    public ConditionData getCondition() {
	return condition;
    }

    /**
     * @param condition
     *            the condition to set
     */
    public void setCondition(ConditionData condition) {
	this.condition = condition;
    }
}
