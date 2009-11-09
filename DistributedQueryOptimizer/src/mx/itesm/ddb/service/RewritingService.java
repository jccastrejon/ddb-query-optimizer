package mx.itesm.ddb.service;

import java.util.ArrayList;
import java.util.List;

import mx.itesm.ddb.service.operator.Node;
import mx.itesm.ddb.service.operator.OperatorTree;
import mx.itesm.ddb.util.ConditionData;
import mx.itesm.ddb.util.ConditionOperator;
import mx.itesm.ddb.util.RelationalOperator;
import mx.itesm.ddb.util.SqlData;
import mx.itesm.ddb.util.impl.ExpressionConditionData;
import mx.itesm.ddb.util.impl.OperationConditionData;
import mx.itesm.ddb.util.impl.SimpleExpressionData;

import org.apache.log4j.Logger;

/**
 * Operator Tree Rewriting Service.
 * 
 * @author jccastrejon
 * 
 */
public class RewritingService {

    /**
     * Class logger.
     */
    public final static Logger logger = Logger.getLogger(RewritingService.class);

    /**
     * Database Dictionary Service.
     */
    DatabaseDictionaryService databaseDictionaryService;

    /**
     * Check if any of these scenarios apply:
     * <ul>
     * <li>Several subsequent projections on the same relation may be grouped</li>
     * <li>Several subsequent selections on the same relation may be grouped</li>
     * </ul>
     * 
     * @param operatorTree
     *            Operator Tree to analyze.
     * @return true if any of the scenarios were found in the specified Node
     *         hierarchy, false otherwise.
     */
    public boolean idempotenceOfUnaryOperators(final Node currentNode) {
	boolean returnValue;
	String testRelation;
	String currentRelation;
	boolean childReturnValue;
	List<ConditionData> groupedConditions;
	OperationConditionData groupedConditionData;

	returnValue = false;
	if (currentNode.getRelationalOperator() != null) {
	    // Group projections
	    if (currentNode.getRelationalOperator().equals(RelationalOperator.PROJECTION)) {
		// TODO: Projection grouping:.
		logger.warn("Projection grouping should be checked in: "
			+ currentNode.getDescription());
	    }

	    // Group selections
	    if (currentNode.getRelationalOperator().equals(RelationalOperator.SELECT)) {
		currentRelation = databaseDictionaryService.getTableFromExpression(currentNode
			.getSqlData()[0].toString());

		// See if any of the chilidren is also a selection on the same
		// relation
		for (Node child : currentNode.getChildren()) {
		    if ((child.getRelationalOperator() != null)
			    && (child.getRelationalOperator().equals(RelationalOperator.SELECT))) {
			testRelation = databaseDictionaryService.getTableFromExpression(child
				.getSqlData()[0].toString());

			// Group the nodes
			if (currentRelation.equals(testRelation)) {
			    // Join conditions
			    groupedConditions = new ArrayList<ConditionData>(2);
			    groupedConditions.add(new ExpressionConditionData(
				    new SimpleExpressionData(currentNode.getSqlDataDescription())));
			    groupedConditions.add(new ExpressionConditionData(
				    new SimpleExpressionData(child.getSqlDataDescription())));
			    groupedConditionData = new OperationConditionData(
				    ConditionOperator.BinaryOperator.AND_OPERATOR,
				    groupedConditions);

			    // Update currentNode with grouped selections and
			    // new children. This includes deletion of the child
			    child.setParent(null);
			    currentNode.getChildren().remove(child);
			    currentNode.addChildren(child.getChildren());
			    currentNode.setSqlData(new SqlData[] { groupedConditionData });

			    returnValue = true;
			}
		    }
		}
	    }
	}

	// Test the same for every child
	if (currentNode.getChildren() != null) {
	    for (Node child : currentNode.getChildren()) {
		childReturnValue = this.idempotenceOfUnaryOperators(child);

		if (childReturnValue) {
		    returnValue = true;
		}
	    }
	}

	return returnValue;
    }

    /**
     * Check if any of these scenarios apply:
     * <ul>
     * <li>Selection and projection on the same relation can be commuted</li>
     * </ul>
     * 
     * @param operatorTree
     *            Operator Tree to analyze.
     * @return Rewritten Operator Tree.
     */
    public OperatorTree commuteSelectionWithProjection(final OperatorTree operatorTree) {
	return null;
    }

    /**
     * Check if any of these scenarios apply:
     * <ul>
     * <li>Selection and Cartesian product can be commuted</li>
     * <li>Selection and Join can be commuted</li>
     * <li>Selection and Union can be commuted if <em>R</em> and <em>T</em> are
     * union compatible (have the same schema)</li>
     * </ul>
     * 
     * @param operatorTree
     *            Operator Tree to analyze.
     * @return Rewritten Operator Tree.
     */
    public OperatorTree commuteSelectionWithBinaryOperators(final OperatorTree operatorTree) {
	return null;
    }

    /**
     * Check if any of these scenarios apply:
     * <ul>
     * <li>Projection and Cartesian product can be commuted</li>
     * <li>Projection and Join can also be commuted</li>
     * <li>Projection and Difference can be commuted</li>
     * <li></li>
     * </ul>
     * 
     * @param operatorTree
     *            Operator Tree to analyze.
     * @return Rewritten Operator Tree.
     */
    public OperatorTree commuteProjectionWithBinaryOperators(final OperatorTree operatorTree) {
	return null;
    }

    /**
     * @return the databaseDictionaryService
     */
    public DatabaseDictionaryService getDatabaseDictionaryService() {
	return databaseDictionaryService;
    }

    /**
     * @param databaseDictionaryService
     *            the databaseDictionaryService to set
     */
    public void setDatabaseDictionaryService(DatabaseDictionaryService databaseDictionaryService) {
	this.databaseDictionaryService = databaseDictionaryService;
    }
}
