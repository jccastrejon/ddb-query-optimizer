package mx.itesm.ddb.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mx.itesm.ddb.dao.DatabaseDictionaryDao;
import mx.itesm.ddb.model.dictionary.Relation;
import mx.itesm.ddb.util.ConditionOperator;

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
	// [expression = value]
	if (sqlData.indexOf(ConditionOperator.BinaryOperator.AND_OPERATOR.getDescription()) == -1) {
	    returnValue = sqlData;
	    if (returnValue.indexOf('=') > 0) {
		returnValue = sqlData.substring(0, sqlData.indexOf('='));
	    }
	}

	// [expression = value] and [expression = value]
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
	String[] expressions;
	List<String> returnValue;

	returnValue = new ArrayList<String>();
	// [expression = value]
	if (sqlData.indexOf(ConditionOperator.BinaryOperator.AND_OPERATOR.toString().trim()) == -1) {
	    expression = sqlData;
	    if (expression.indexOf('=') > 0) {
		expression = sqlData.substring(0, sqlData.indexOf('='));
	    }

	    returnValue = Arrays.asList(expression.trim().split(" "));
	}

	// [expression = value] and [expression = value]
	else {
	    expressions = sqlData.split(ConditionOperator.BinaryOperator.AND_OPERATOR
		    .getDescription());

	    returnValue = new ArrayList<String>(expressions.length);
	    for (String innerExpression : expressions) {
		if (innerExpression.indexOf('=') > 0) {
		    innerExpression = innerExpression.substring(0, innerExpression.indexOf('='));
		}

		returnValue.add(this.getValidExpression(innerExpression));
	    }
	}

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
