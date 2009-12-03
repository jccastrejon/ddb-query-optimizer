package mx.itesm.ddb.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mx.itesm.ddb.dao.DatabaseDictionaryDao;
import mx.itesm.ddb.model.dictionary.Relation;
import mx.itesm.ddb.util.ConditionOperator;
import mx.itesm.ddb.util.ExpressionOperator;
import mx.itesm.ddb.util.ExpressionOperator.ArithmeticOperator;

/**
 * Database Dictionary Service.
 * 
 * @author jccastrejon
 * 
 */
public class DatabaseDictionaryService {

    /**
     * Database Dictionary DAO.
     */
    DatabaseDictionaryDao databaseDictionaryDao;

    /**
     * Get the relation identified by the given name.
     * 
     * @param name
     *            Relation name.
     * @return A Relation instance if there's a Relation identified by the given
     *         name in the database dictionary, <em>null</em> otherwise.
     */
    public Relation getRelation(final String name) {
	return databaseDictionaryDao.getRelation(name);
    }

    /**
     * Get all the names that are used to reference a relation. That is, the
     * global relation and fragments names.
     * 
     * @param name
     *            Relation name.
     * @return Names used to reference a relation.
     */
    public String[] getRelationNames(final String name) {
	Relation relation;
	List<String> returnValue;

	returnValue = new ArrayList<String>();
	relation = this.getRelation(name);
	if (relation != null) {
	    returnValue.add(relation.getName());
	    if (relation.getFragments() != null) {
		for (Relation fragment : relation.getFragments()) {
		    returnValue.add(fragment.getName());
		}
	    }
	}

	return returnValue.toArray(new String[returnValue.size()]);
    }

    /**
     * Verifies if a given attribute is valid for a given relation. The
     * attribute name is of the form: <em>relation.attribute</em>.
     * 
     * @param attributeName
     *            Full attribute name.
     * @return <em>true</em> if the attribute is valid for the given relation,
     *         <em>false</em> otherwise.
     */
    public boolean isValidAttribute(final String attributeName) {
	int dotIndex;
	Relation relation;
	boolean returnValue;
	String relationPart;
	String trimmedAttributeName;

	returnValue = false;
	trimmedAttributeName = attributeName.trim();
	dotIndex = trimmedAttributeName.indexOf('.');
	if (dotIndex > 0) {
	    relationPart = trimmedAttributeName.substring(0, dotIndex);

	    relation = this.databaseDictionaryDao.getRelation(relationPart);
	    if (relation != null) {
		if (relation.getAttribute(trimmedAttributeName) != null) {
		    returnValue = true;
		}
	    }
	}

	return returnValue;
    }

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
    public String getTableFromSqlData(final String sqlData) {
	String returnValue;

	returnValue = null;
	// [expression {operator} value]
	if (sqlData.indexOf(ConditionOperator.BinaryOperator.AND_OPERATOR.getDescription()) == -1) {
	    returnValue = sqlData;
	    for (ArithmeticOperator operator : ExpressionOperator.ArithmeticOperator.values()) {
		if (returnValue.indexOf(operator.getDescription()) > 0) {
		    returnValue = sqlData.substring(0, sqlData.indexOf(operator.getDescription()));
		    break;
		}
	    }
	}

	// [expression {operator} value] and [expression {operator} value]
	else {
	    // All the conditions refer to the same relation, so we take the
	    // first one
	    returnValue = sqlData.substring(0, sqlData
		    .indexOf(ConditionOperator.BinaryOperator.AND_OPERATOR.getDescription()));
	}

	return this.getTableFromExpression(this.getValidExpression(returnValue));
    }

    /**
     * Get the referenced attributes used by this SqlData.
     * 
     * @param sqlData
     *            SqlData to be analyzed.
     * @return Array of referenced attributes.
     */
    public List<String> getAttributesFromSqlData(final String sqlData) {
	String expression;
	String firstOperand;
	String secondOperand;
	String[] expressions;
	boolean operatorFound;
	List<String> returnValue;
	List<String> invalidAttributes;
	List<String> modifiedAttributes;

	returnValue = new ArrayList<String>();
	if (sqlData != null) {
	    // [expression {operator} value]
	    if (sqlData.indexOf(ConditionOperator.BinaryOperator.AND_OPERATOR.toString().trim()) == -1) {
		expression = sqlData;
		operatorFound = false;
		for (ArithmeticOperator operator : ExpressionOperator.ArithmeticOperator.values()) {
		    if (expression.indexOf(operator.getDescription()) > 0) {
			// expression
			firstOperand = expression.substring(0, expression.indexOf(operator
				.getDescription()));
			if (this.isValidAttribute(firstOperand)) {
			    returnValue.add(firstOperand.toLowerCase().trim());
			}

			// value
			secondOperand = expression.substring(expression.indexOf(operator
				.getDescription())
				+ operator.getDescription().length());
			if (this.isValidAttribute(secondOperand)) {
			    returnValue.add(secondOperand.toLowerCase().trim());
			}

			operatorFound = true;
			break;
		    }
		}

		// If no operator was found
		if (!operatorFound) {
		    expressions = expression.toLowerCase().trim().split(" ");
		    returnValue = new ArrayList<String>(expressions.length);
		    for (String innerExpression : expressions) {
			returnValue.add(innerExpression);
		    }
		}
	    }

	    // [expression {operator} value] and [expression {operator} value]
	    else {
		expressions = sqlData.split(ConditionOperator.BinaryOperator.AND_OPERATOR
			.getDescription());

		returnValue = new ArrayList<String>(expressions.length);
		for (String innerExpression : expressions) {
		    for (ArithmeticOperator operator : ExpressionOperator.ArithmeticOperator
			    .values()) {
			if (innerExpression.indexOf(operator.getDescription()) > 0) {
			    innerExpression = innerExpression.substring(0, innerExpression
				    .indexOf(operator.getDescription()));
			    break;
			}
		    }

		    returnValue.add(this.getValidExpression(innerExpression).toLowerCase().trim());
		}
	    }
	}

	// Remove empty values and operators that could have been incorrectly
	// added ('<, >' when evaluating '<=, >=')
	invalidAttributes = new ArrayList<String>();
	modifiedAttributes = new ArrayList<String>();
	for (String attribute : returnValue) {
	    operatorFound = false;
	    if (attribute.trim().length() == 0) {
		operatorFound = true;
	    } else {
		for (ArithmeticOperator operator : ExpressionOperator.ArithmeticOperator.values()) {
		    if (attribute.contains(operator.getDescription())) {
			operatorFound = true;
			modifiedAttributes.add(attribute.substring(0,
				attribute.indexOf(operator.getDescription())).trim());
			break;
		    }
		}
	    }

	    if (operatorFound) {
		invalidAttributes.add(attribute);
	    }
	}

	// Update returnValue with the changes found
	returnValue.removeAll(invalidAttributes);
	returnValue.addAll(modifiedAttributes);

	return returnValue;
    }

    /**
     * Get the referenced attributes used by this SqlData, grouped by relations.
     * 
     * @param sqlData
     *            SqlData to be analyzed.
     * @return Referenced attributes, grouped by relations.
     */
    public Map<String, String> getGroupedAttributesFromSqlData(final String sqlData) {
	String currentRelation;
	List<String> attributes;
	String currentGroup;
	Map<String, String> returnValue;

	attributes = this.getAttributesFromSqlData(sqlData);
	returnValue = new HashMap<String, String>();
	for (String attribute : attributes) {
	    if (attribute.indexOf('.') > 0) {
		currentRelation = attribute.substring(0, attribute.indexOf('.'));
		currentGroup = returnValue.get(currentRelation);
		if (currentGroup == null) {
		    currentGroup = "";
		}

		currentGroup += attribute + " ";
		returnValue.put(currentRelation, currentGroup);
	    }
	}

	return returnValue;
    }

    /**
     * Get a valid expression value.
     * 
     * @param expression
     *            Expression to analyze.
     * @return Valid expression.
     */
    private String getValidExpression(final String expression) {
	return expression.replace('(', ' ').replace(')', ' ').trim();
    }

    /**
     * @return the databaseDictionaryDao
     */
    public DatabaseDictionaryDao getDatabaseDictionaryDao() {
	return databaseDictionaryDao;
    }

    /**
     * @param databaseDictionaryDao
     *            the databaseDictionaryDao to set
     */
    public void setDatabaseDictionaryDao(DatabaseDictionaryDao databaseDictionaryDao) {
	this.databaseDictionaryDao = databaseDictionaryDao;
    }
}
