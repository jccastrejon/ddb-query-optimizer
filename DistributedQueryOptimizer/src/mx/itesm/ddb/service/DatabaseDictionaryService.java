package mx.itesm.ddb.service;

import java.util.ArrayList;
import java.util.List;

import mx.itesm.ddb.util.ConditionData;
import mx.itesm.ddb.util.SqlData;
import mx.itesm.ddb.util.impl.ExpressionConditionData;
import mx.itesm.ddb.util.impl.OperationConditionData;
import mx.itesm.ddb.util.impl.SimpleExpressionData;

/**
 * @author jccastrejon
 * 
 */
public class DatabaseDictionaryService {

    /**
     * Get the referenced table used by this expression.
     * 
     * @param expression
     *            SQL expression.
     * @return Table name or null if the expression doesn't make any reference
     *         to a table.
     */
    public String getTableFromExpression(final String expression) {
	String returnValue;

	// TODO: Connect to the real Dictionary:.
	returnValue = null;
	if (expression.indexOf('.') > 0) {
	    returnValue = expression.substring(0, expression.indexOf('.'));
	}

	return returnValue;
    }

    /**
     * Get the referenced table used by this SqlData.
     * 
     * @param sqlData
     *            SqlData.
     * @return Table name.
     */
    public String getTableFromSqlData(final SqlData[] sqlData) {
	StringBuilder returnValue;
	List<ConditionData> conditions;
	ExpressionConditionData expression;

	returnValue = null;
	// [expression = value]
	if (sqlData[0] instanceof SimpleExpressionData) {
	    returnValue = new StringBuilder();
	    for (SqlData data : sqlData) {
		if (data.toString().trim().equals("=")) {
		    break;
		}

		returnValue.append(data);
	    }
	}

	// [expression = value] and [expression = value]
	else if (sqlData[0] instanceof OperationConditionData) {
	    conditions = ((OperationConditionData) sqlData[0]).getConditions();

	    // All the conditions refer to the same relation, so we take the
	    // first one
	    if ((conditions != null) && (!conditions.isEmpty())) {
		expression = (ExpressionConditionData) conditions.get(0);
		returnValue = new StringBuilder(expression.getExpression().toString().replace('(',
			' ').replace(')', ' '));
	    }
	}

	return this.getTableFromExpression(returnValue.toString().trim());
    }

    /**
     * Get the referenced attributes used by this SqlData elements.
     * 
     * @param sqlData
     *            Array of SqlData elements to be analyzed.
     * @return Array of referenced attributes.
     */
    public List<String> getAttributesFromSqlData(final SqlData[] sqlData) {
	String expression;
	List<String> returnValue;
	SimpleExpressionData simpleExpression;
	OperationConditionData operationConditionData;
	ExpressionConditionData expressionConditionData;

	returnValue = new ArrayList<String>();
	// [expression = value]
	if (sqlData[0] instanceof SimpleExpressionData) {
	    for (SqlData data : sqlData) {
		if (data.toString().trim().equals("=")) {
		    break;
		}

		returnValue.add(data.toString().trim());
	    }
	}

	// [expression = value] and [expression = value]
	else if (sqlData[0] instanceof OperationConditionData) {
	    operationConditionData = ((OperationConditionData) sqlData[0]);
	    for (ConditionData condition : operationConditionData.getConditions()) {
		expressionConditionData = (ExpressionConditionData) condition;
		simpleExpression = (SimpleExpressionData) expressionConditionData.getExpression();

		// table.attribute = value
		expression = simpleExpression.toString().substring(0,
			simpleExpression.getExpression().toString().indexOf('=')).trim();
		returnValue.add(expression);
	    }
	}

	return returnValue;
    }
}
