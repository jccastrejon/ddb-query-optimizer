package mx.itesm.ddb.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mx.itesm.ddb.service.operator.Node;
import mx.itesm.ddb.service.operator.OperatorTree;
import mx.itesm.ddb.util.ConditionData;
import mx.itesm.ddb.util.ConditionOperator;
import mx.itesm.ddb.util.RelationalOperator;
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
     * Graphic Export Service.
     */
    GraphicExportService graphicExportService;

    /**
     * Rewrite the Operator Tree by applying Transformation Rules.
     * 
     * @param operatorTree
     *            Operator Tree.
     * @param queryId
     *            Query Id.
     * @param imageDir
     *            Directory where to save the temporary Operator Tree.
     * @return Number of steps needed to build the final Operator Tree.
     * @throws IOException
     *             If an I/O error occurs.
     */
    public int rewriteOperatorTree(final OperatorTree operatorTree, final String queryId,
	    final File imageDir) throws IOException {
	int returnValue;
	boolean rewriteTree;
	List<Boolean> returnValues;

	// Export original tree before rewriting
	returnValue = 0;
	graphicExportService.saveIntermediateOperatorTree(operatorTree, queryId, returnValue,
		"Initial", imageDir);

	// Refine the operator tree till no more changes are found
	returnValues = new ArrayList<Boolean>();
	do {
	    returnValues.clear();

	    do {
		rewriteTree = this.idempotenceOfUnaryOperators(operatorTree.getRootNode());
		returnValues.add(rewriteTree);
		returnValue = this.exportTemporaryTree(operatorTree, queryId, rewriteTree,
			returnValue, "IdempotenceOfUnaryOperators", imageDir);
	    } while (rewriteTree);

	    do {
		rewriteTree = this.commuteSelectionWithProjection(operatorTree.getRootNode());
		returnValues.add(rewriteTree);
		returnValue = this.exportTemporaryTree(operatorTree, queryId, rewriteTree,
			returnValue, "CommuteSelectionWithProjection", imageDir);
	    } while (rewriteTree);
	} while (returnValues.contains(Boolean.TRUE));

	return returnValue;
    }

    /**
     * Check if any of these scenarios apply:
     * <ul>
     * <li>Several subsequent projections on the same relation may be grouped</li>
     * <li>Several subsequent selections on the same relation may be grouped</li>
     * </ul>
     * 
     * @param currentNode
     *            Starting node for the analysis.
     * @return true if any of the scenarios were found in the specified Node
     *         hierarchy, false otherwise.
     */
    public boolean idempotenceOfUnaryOperators(final Node currentNode) {
	boolean returnValue;
	String childRelation;
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
		currentRelation = databaseDictionaryService.getTableFromSqlData(currentNode
			.getSqlData());

		// See if any of the chilidren is also a selection on the same
		// relation
		if ((currentRelation != null) && (currentNode.getChildren() != null)) {
		    for (Node child : currentNode.getChildren()) {
			if ((child.getRelationalOperator() != null)
				&& (child.getRelationalOperator().equals(RelationalOperator.SELECT))) {
			    childRelation = databaseDictionaryService.getTableFromSqlData(child
				    .getSqlData());

			    // Group nodes
			    if (currentRelation.equals(childRelation)) {
				logger.debug("idempotenceOfUnaryOperators: Grouping <"
					+ child.getDescription() + "> with <"
					+ currentNode.getDescription() + ">");

				// Join conditions
				groupedConditions = new ArrayList<ConditionData>(2);
				groupedConditions.add(new ExpressionConditionData(
					new SimpleExpressionData(currentNode.getSqlData())));
				groupedConditions.add(new ExpressionConditionData(
					new SimpleExpressionData(child.getSqlData())));
				groupedConditionData = new OperationConditionData(
					ConditionOperator.BinaryOperator.AND_OPERATOR,
					groupedConditions);

				// Update currentNode with grouped selections
				// and new children. This includes deletion of
				// the child
				child.setParent(null);
				currentNode.getChildren().remove(child);
				currentNode.addChildren(child.getChildren());
				currentNode.setSqlData(groupedConditionData.toString());

				returnValue = true;
			    }
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
     * @param currentNode
     *            Starting node for the analysis.
     * @return true if any of the scenarios were found in the specified Node
     *         hierarchy, false otherwise.
     */
    public boolean commuteSelectionWithProjection(final Node currentNode) {
	boolean returnValue;
	Node projectionNode;
	String childRelation;
	boolean previousMatch;
	String currentRelation;
	boolean childReturnValue;
	List<String> selectionAttributes;
	List<String> projectionAttributes;

	// Look for a projection followed by a selection on the same relation
	returnValue = false;
	previousMatch = false;
	if (currentNode.getRelationalOperator() != null) {
	    if (currentNode.getRelationalOperator().equals(RelationalOperator.PROJECTION)) {
		currentRelation = databaseDictionaryService.getTableFromSqlData(currentNode
			.getSqlData());

		if ((currentRelation != null) && (currentNode.getChildren() != null)) {
		    for (Node child : currentNode.getChildren()) {
			if ((child.getRelationalOperator() != null)
				&& (child.getRelationalOperator().equals(RelationalOperator.SELECT))) {
			    childRelation = databaseDictionaryService.getTableFromSqlData(child
				    .getSqlData());

			    // Group nodes
			    if (currentRelation.equals(childRelation)) {

				// Before grouping nodes, verify if this is a
				// previous found case, that is, a projection
				// already exists after the selection with the
				// required attributes
				for (Node innerChild : child.getChildren()) {
				    if ((innerChild.getRelationalOperator() != null)
					    && (innerChild.getRelationalOperator()
						    .equals(RelationalOperator.PROJECTION))) {
					selectionAttributes = databaseDictionaryService
						.getAttributesFromSqlData(innerChild.getSqlData());

					// If the inner child contains all the
					// projection attributes, this case was
					// previously evaluated
					previousMatch = true;
					for (String data : currentNode.getSqlDataElements()) {
					    if (!selectionAttributes.contains(data)) {
						previousMatch = false;
					    }
					}

					// Don't evaluate the same case twice
					if (previousMatch) {
					    break;
					}
				    }
				}

				// Don't evaluate the same case twice
				if (previousMatch) {
				    break;
				}

				selectionAttributes = databaseDictionaryService
					.getAttributesFromSqlData(child.getSqlData());

				logger.warn("commuteSelectionWithProjection: Grouping <"
					+ child.getDescription() + "> with <"
					+ currentNode.getDescription()
					+ ">. Selection attributes: " + selectionAttributes);

				// The projection attributes include the
				// original projection attributes and the
				// attributes needed by the selection
				previousMatch = false;
				projectionAttributes = new ArrayList<String>(Arrays
					.asList(currentNode.getSqlDataElements()));

				for (String expression : selectionAttributes) {
				    for (String sqlData : projectionAttributes) {
					if (sqlData.toString().trim().equals(expression.trim())) {
					    previousMatch = true;
					    break;
					}
				    }

				    if (!previousMatch) {
					projectionAttributes.add(expression);
				    }
				}

				// Add the new projection node as a child of the
				// current child node
				projectionNode = new Node(projectionAttributes
					.toArray(new String[projectionAttributes.size()]),
					RelationalOperator.PROJECTION);
				projectionNode.setChildren(child.getChildren());
				child.setChildren(null);
				child.addChild(projectionNode);

				returnValue = true;
			    }
			}
		    }
		}
	    }
	}

	// Test the same for every child
	if (currentNode.getChildren() != null) {
	    for (Node child : currentNode.getChildren()) {
		childReturnValue = this.commuteSelectionWithProjection(child);

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
     * Export a temporary Operator Tree to an image in the given Image
     * Directory.
     * 
     * @param operatorTree
     *            Operator Tree to be exported.
     * @param queryId
     *            Query Id.
     * @param rewriteTree
     *            Flag that indicates wheter this tree should be exported or
     *            not.
     * @param currentStep
     *            Current rewriting step that generated this intermediate
     *            Operator Tree.
     * @param label
     *            Image Label.
     * @param imageDir
     *            Directory where to save the temporary Operator Tree.
     * @return Current number of rewriting step.
     * @throws IOException
     *             If an I/O error occurs.
     */
    private int exportTemporaryTree(final OperatorTree operatorTree, final String queryId,
	    final boolean rewriteTree, final int currentStep, final String label,
	    final File imageDir) throws IOException {
	int returnValue;

	returnValue = currentStep;
	if (rewriteTree) {
	    graphicExportService.saveIntermediateOperatorTree(operatorTree, queryId,
		    (++returnValue), label, imageDir);
	}

	return returnValue;
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

    /**
     * @return the graphicExportService
     */
    public GraphicExportService getGraphicExportService() {
	return graphicExportService;
    }

    /**
     * @param graphicExportService
     *            the graphicExportService to set
     */
    public void setGraphicExportService(GraphicExportService graphicExportService) {
	this.graphicExportService = graphicExportService;
    }
}
