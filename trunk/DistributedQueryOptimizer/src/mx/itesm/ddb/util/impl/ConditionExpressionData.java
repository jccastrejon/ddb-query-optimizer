package mx.itesm.ddb.util.impl;

import mx.itesm.ddb.util.ConditionData;
import mx.itesm.ddb.util.ExpressionData;

/**
 * @author jccastrejon
 * 
 */
public class ConditionExpressionData implements ExpressionData {

    /**
     * 
     */
    ConditionData condition;

    /**
     * 
     * @param condition
     */
    public ConditionExpressionData(ConditionData condition) {
	this.condition = condition;
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
