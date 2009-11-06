package mx.itesm.ddb.util.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mx.itesm.ddb.util.ConditionData;
import mx.itesm.ddb.util.ConditionOperator;

/**
 * ConditionData of the form: [ConditionData] [Operator] [ConditionData] ...
 * 
 * @author jccastrejon
 * 
 */
public class OperationConditionData implements ConditionData {

    /**
     * Condition operator that links the conditions.
     */
    private ConditionOperator operator;

    /**
     * First condition.
     */
    private List<ConditionData> conditions;

    /**
     * Full constructor, receiing a list of conditions linked by a condition
     * operator.
     * 
     * @param operator
     *            Condition operator that links the conditions.
     * @param conditions
     *            Conditions logically linked by the operator.
     */
    public OperationConditionData(final ConditionOperator operator,
	    final List<ConditionData> conditions) {
	this.operator = operator;
	this.conditions = conditions;
    }

    /**
     * Full constructor, receiving a unique condition affected by the condition
     * operator.
     * 
     * @param operator
     *            Condition operator that links the conditions.
     * @param condition
     *            Conditions logically linked by the operator.
     */
    public OperationConditionData(final ConditionOperator operator, final ConditionData condition) {
	this.operator = operator;
	this.conditions = new ArrayList<ConditionData>(1);
	this.conditions.add(condition);
    }

    /**
     * Full constructor, receiving multiple conditions affected by the condition
     * operator.
     * 
     * @param operator
     *            Condition operator that links the conditions.
     * @param conditions
     *            Conditions logically linked by the operator.
     */
    public OperationConditionData(final ConditionOperator operator,
	    final ConditionData... conditions) {
	this.operator = operator;
	this.conditions = Arrays.asList(conditions);
    }

    @Override
    public OperationConditionData clone() {
	return new OperationConditionData(this.operator, this.conditions);
    }

    @Override
    public String toString() {
	StringBuilder returnValue = new StringBuilder();

	if (operator instanceof ConditionOperator.UnaryOperator) {
	    returnValue.append(operator);
	}

	for (int i = 0; i < conditions.size(); i++) {
	    returnValue.append("(" + conditions.get(i) + ")");

	    if ((!(operator instanceof ConditionOperator.UnaryOperator))
		    && (i != conditions.size() - 1)) {
		returnValue.append(operator);
	    }
	}

	return returnValue.toString();
    }

    /**
     * @return the operator
     */
    public ConditionOperator getOperator() {
	return operator;
    }

    /**
     * @param operator
     *            the operator to set
     */
    public void setOperator(ConditionOperator operator) {
	this.operator = operator;
    }

    /**
     * @return the conditions
     */
    public List<ConditionData> getConditions() {
	return conditions;
    }

    /**
     * @param conditions
     *            the conditions to set
     */
    public void setConditions(List<ConditionData> conditions) {
	this.conditions = conditions;
    }
}
