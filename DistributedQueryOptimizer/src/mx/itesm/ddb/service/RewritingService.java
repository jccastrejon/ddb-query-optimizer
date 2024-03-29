package mx.itesm.ddb.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
     * Binary operators that can be commuted either with Selection or with
     * Projection.
     */
    private final static List<RelationalOperator> commutableOperators = Arrays.asList(
	    RelationalOperator.PRODUCT, RelationalOperator.JOIN, RelationalOperator.UNION);;

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
     * @param currentRewritingSteps
     *            Current Rewriting Steps applied over the Operator Tree.
     * @return Number of steps needed to build the final Operator Tree.
     * @throws IOException
     *             If an I/O error occurs.
     */
    public int rewriteOperatorTree(final OperatorTree operatorTree, final String queryId,
	    final File imageDir, final int currentRewritingSteps) throws IOException {
	int returnValue;
	boolean rewriteTree;
	List<Boolean> returnValues;

	// Refine the operator tree till no more changes are found
	returnValue = currentRewritingSteps;
	returnValues = new ArrayList<Boolean>();
	do {
	    returnValues.clear();

	    // idempotenceOfUnaryOperators
	    do {
		rewriteTree = this.idempotenceOfUnaryOperators(operatorTree.getRootNode());
		returnValues.add(rewriteTree);
		returnValue = this.exportTemporaryTree(operatorTree, queryId, rewriteTree,
			returnValue, "IdempotenceOfUnaryOperators", imageDir);
	    } while (rewriteTree);

	    // commuteSelectionWithProjection
	    do {
		rewriteTree = this.commuteSelectionWithProjection(operatorTree.getRootNode());
		returnValues.add(rewriteTree);
		returnValue = this.exportTemporaryTree(operatorTree, queryId, rewriteTree,
			returnValue, "CommuteSelectionWithProjection", imageDir);
	    } while (rewriteTree);

	    // commuteSelectionWithBinaryOperators
	    do {
		rewriteTree = this.commuteSelectionWithBinaryOperators(operatorTree.getRootNode());
		returnValues.add(rewriteTree);
		returnValue = this.exportTemporaryTree(operatorTree, queryId, rewriteTree,
			returnValue, "CommuteSelectionWithBinaryOperators", imageDir);
	    } while (rewriteTree);

	    // commuteProjectionWithBinaryOperators
	    do {
		rewriteTree = this.commuteProjectionWithBinaryOperators(operatorTree.getRootNode());
		returnValues.add(rewriteTree);
		returnValue = this.exportTemporaryTree(operatorTree, queryId, rewriteTree,
			returnValue, "CommuteProjectionWithBinaryOperators", imageDir);
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
	Set<String> groupedAttributes;
	List<ConditionData> groupedConditions;
	OperationConditionData groupedConditionData;

	returnValue = false;
	if (currentNode.getRelationalOperator() != null) {

	    // Group projections
	    if (currentNode.getRelationalOperator().equals(RelationalOperator.PROJECTION)) {
		currentRelation = databaseDictionaryService.getTableFromSqlData(currentNode
			.getSqlData());

		// See if any of the chilidren is also a projection on the same
		// relation
		if ((currentRelation != null) && (currentNode.getChildren() != null)) {
		    for (Node child : currentNode.getChildren()) {
			if ((child.getRelationalOperator() != null)
				&& (child.getRelationalOperator()
					.equals(RelationalOperator.PROJECTION))) {
			    childRelation = databaseDictionaryService.getTableFromSqlData(child
				    .getSqlData());

			    // Group nodes
			    if (currentRelation.equals(childRelation)) {
				logger.debug("idempotenceOfUnaryOperators: Grouping <"
					+ child.getDescription() + "> with <"
					+ currentNode.getDescription() + ">");

				// Update currentNode with grouped selections
				// and new children. This includes deletion of
				// the child
				child.getParent().removeChild(child);
				currentNode.removeChild(child);
				currentNode.addChildren(child.getChildren());

				// Avoid grouping the same attribute twice
				groupedAttributes = new HashSet<String>(databaseDictionaryService
					.getAttributesFromSqlData(currentNode.getSqlData()));
				groupedAttributes.addAll(databaseDictionaryService
					.getAttributesFromSqlData(child.getSqlData()));
				currentNode.setSqlData(groupedAttributes);

				returnValue = true;
			    }
			}
		    }
		}
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
				child.getParent().removeChild(child);
				currentNode.removeChild(child);
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
				    previousMatch = false;
				    for (String sqlData : projectionAttributes) {
					if (sqlData.toString().trim().equalsIgnoreCase(
						expression.trim())) {
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
				projectionNode.addChildren(child.getChildren());
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
     * @param currentNode
     *            Starting node for the analysis.
     * @return true if any of the scenarios were found in the specified Node
     *         hierarchy, false otherwise.
     */
    public boolean commuteSelectionWithBinaryOperators(final Node currentNode) {
	Node testNode;
	boolean returnValue;
	String currentRelation;
	boolean childReturnValue;
	List<Node> currentChildren;

	returnValue = false;
	currentChildren = currentNode.getChildren();
	if (currentNode.getRelationalOperator() != null) {

	    // Selection
	    if (currentNode.getRelationalOperator().equals(RelationalOperator.SELECT)) {
		currentRelation = databaseDictionaryService.getTableFromSqlData(currentNode
			.getSqlData());

		// Cartesian product, Join or Union operators
		if ((currentRelation != null) && (currentNode.getChildren() != null)) {
		    for (Node child : currentNode.getChildren()) {
			// Consider the case when commuteSelectionWithProjection
			// has occurred in the global relation. If this relation
			// has been fragmented we need to consider applying the
			// commute operation again
			testNode = child;
			if (child.getRelationalOperator() == RelationalOperator.PROJECTION) {
			    if (child.getChildren() != null) {
				testNode = child.getChildren().get(0);
			    }
			}

			// Check if one of the commutable operators is found
			if ((testNode.getRelationalOperator() != null)
				&& (RewritingService.commutableOperators.contains(testNode
					.getRelationalOperator()))) {

			    // Try to commute operators
			    returnValue = this.addOperationNodeBeforeLeafNode(currentRelation,
				    currentNode, testNode, currentChildren);

			    // Commuting done
			    if (returnValue) {
				break;
			    }
			}
		    }
		}
	    }
	}

	// Test the same for every child only if we haven't find a matching
	// case, because if we did, the operator tree has already changed for
	// the children nodes
	if (currentChildren != null) {
	    for (Node child : currentChildren) {
		childReturnValue = this.commuteSelectionWithBinaryOperators(child);

		if (childReturnValue) {
		    returnValue = true;
		    break;
		}
	    }
	}

	return returnValue;
    }

    /**
     * Check if any of these scenarios apply:
     * <ul>
     * <li>Projection and Cartesian product can be commuted</li>
     * <li>Projection and Join can be commuted</li>
     * <li>Projection and Union can be commuted</li>
     * <li></li>
     * </ul>
     * 
     * @param currentNode
     *            Starting node for the analysis.
     * @return true if any of the scenarios were found in the specified Node
     *         hierarchy, false otherwise.
     */
    public boolean commuteProjectionWithBinaryOperators(final Node currentNode) {
	Node projectionNode;
	boolean returnValue;
	List<Node> leafNodes;
	boolean previousMatch;
	boolean projectionAdded;
	boolean childReturnValue;
	List<Node> currentChildren;
	List<Node> newInnerChildren;
	List<Node> originalInnerChildren;
	StringBuilder projectionAttributes;
	Map<String, String> groupedAttributes;
	Map<String, String> childGroupedAttributes;

	returnValue = false;
	currentChildren = currentNode.getChildren();
	if (currentNode.getRelationalOperator() != null) {
	    // Projection
	    if (currentNode.getRelationalOperator().equals(RelationalOperator.PROJECTION)) {
		groupedAttributes = databaseDictionaryService
			.getGroupedAttributesFromSqlData(currentNode.getSqlData());

		// Any of the commutable operators
		if ((!groupedAttributes.isEmpty()) && (currentNode.getChildren() != null)) {
		    for (Node child : currentNode.getChildren()) {
			if ((child.getRelationalOperator() != null)
				&& (RewritingService.commutableOperators.contains(child
					.getRelationalOperator()))) {

			    // Add the neccesary projections for the appropiate
			    // leaf nodes
			    for (String currentRelation : groupedAttributes.keySet()) {
				projectionNode = new Node(groupedAttributes.get(currentRelation),
					RelationalOperator.PROJECTION);

				// Before commuting operators, verify if this is
				// a previous found case, that is, a projection
				// already exists right before the relation leaf
				// node, with the required attributes
				leafNodes = child.getLeafNodes(databaseDictionaryService
					.getRelationNames(currentRelation));
				previousMatch = false;
				for (Node relationNode : leafNodes) {
				    if ((relationNode != null)
					    && (relationNode.getParent() != null)
					    && (relationNode.getParent().getRelationalOperator() != null)
					    && (relationNode.getParent().getRelationalOperator()
						    .equals(RelationalOperator.PROJECTION))) {
					previousMatch = true;
					break;
				    }
				}

				if (previousMatch) {
				    continue;
				}

				// Try to commute operators
				projectionAdded = this.addOperationNodeBeforeLeafNode(
					currentRelation, projectionNode, child, currentChildren);

				if (projectionAdded) {
				    returnValue = true;
				}
			    }

			    // Besides the projection added just before the leaf
			    // nodes, there should be a projection after the
			    // commutable operator, to reduce the attributes
			    // before performing the operation. The projection
			    // just added could be that required projection,
			    // depending on the position of the currentNode in
			    // the Operator Tree
			    if ((returnValue) && (child.getChildren() != null)) {
				newInnerChildren = new ArrayList<Node>();
				originalInnerChildren = new ArrayList<Node>();

				// Attributes contained in the commutable
				// operator node
				childGroupedAttributes = null;
				if (child.getSqlData() != null) {
				    childGroupedAttributes = databaseDictionaryService
					    .getGroupedAttributesFromSqlData(child.getSqlData());
				}
				for (Node innerChild : child.getChildren()) {
				    // Add the new projection node only if
				    // it hasn't been added yet
				    if ((innerChild.getRelationalOperator() != null)
					    && (!innerChild.getRelationalOperator().equals(
						    RelationalOperator.PROJECTION))) {

					projectionAttributes = new StringBuilder();
					for (String currentRelation : groupedAttributes.keySet()) {
					    if (innerChild
						    .containsLeafNode(databaseDictionaryService
							    .getRelationNames(currentRelation))) {
						projectionAttributes.append(groupedAttributes
							.get(currentRelation));
					    }
					}

					// Add the attributes required to
					// perform the commutable operation
					if (childGroupedAttributes != null) {
					    for (String relation : childGroupedAttributes.keySet()) {
						if (innerChild
							.containsLeafNode(databaseDictionaryService
								.getRelationNames(relation))) {
						    projectionAttributes
							    .append(childGroupedAttributes
								    .get(relation));
						}
					    }
					}

					// Add the new projection node only if
					// it contains attributes that will
					// later be used up in the operator tree
					if (projectionAttributes.length() != 0) {
					    projectionNode = new Node(projectionAttributes
						    .toString(), RelationalOperator.PROJECTION);

					    // Add the new projection node just
					    // after the commutable operator
					    projectionNode.addChild(innerChild);
					    newInnerChildren.add(projectionNode);
					    originalInnerChildren.add(innerChild);
					}
				    }
				}

				// Replace original inner children for new
				// projection children. We didn't do it in the
				// previous cycle because of
				// ConcurrentModificationException
				for (Node replacedNode : originalInnerChildren) {
				    child.removeChild(replacedNode);
				}

				for (Node newInnerChild : newInnerChildren) {
				    child.addChild(newInnerChild);
				}
			    }
			}
		    }
		}
	    }
	}

	// Test the same for every child
	if (currentChildren != null) {
	    for (Node child : currentChildren) {
		childReturnValue = this.commuteProjectionWithBinaryOperators(child);

		if (childReturnValue) {
		    returnValue = true;
		}
	    }
	}

	return returnValue;
    }

    /**
     * Check if branches of the operator node can be joined after being
     * separated by an <em>OR</em> condition. This means, if the branches are
     * equal except for a condition over one leaf node, group the conditions
     * over this leaf node from the branches, and create a new Union node in the
     * first branch to hold them.
     * 
     * @param operatorTree
     *            Operator Tree to be exported.
     * @param queryId
     *            Query Id.
     * @param imageDir
     *            Directory where to save the temporary Operator Tree.
     * @param currentRewritingSteps
     *            Current rewriting step that generated this intermediate
     *            Operator Tree.
     * @return Current number of rewriting step.
     * @throws IOException
     *             If an I/O error occurs.
     */
    public int customOrConditionsReduction(final OperatorTree operatorTree, final String queryId,
	    final File imageDir, final int currentRewritingSteps) throws IOException {
	Node unionNode;
	int returnValue;
	Node selectNode;
	Node firstBranch;
	Node newUnionNode;
	Node selectParent;
	boolean leafFound;
	boolean orCondition;
	List<Node> leafNodes;
	List<Node> selectionNodes;
	List<Node> branchLeafNodes;
	List<Node> orConditionLeafs;
	List<Node> orConditionSelectNodes;

	firstBranch = null;
	returnValue = currentRewritingSteps;
	unionNode = operatorTree.getRootNode().getRelationalOperatorNode(RelationalOperator.UNION);
	if ((unionNode != null) && (unionNode.getChildren() != null)
		&& (!unionNode.getChildren().isEmpty())) {
	    // Check if the branches refer to the same relations
	    leafFound = false;
	    firstBranch = unionNode.getChildren().get(0);
	    leafNodes = firstBranch.getLeafNodes();
	    for (Node branch : unionNode.getChildren()) {
		leafFound = false;

		// The leaf references will not be the same, but the
		// description might be
		for (Node innerLeafNode : branch.getLeafNodes()) {
		    leafFound = false;
		    for (Node leafNode : leafNodes) {
			if (leafNode.getDescription().equalsIgnoreCase(
				innerLeafNode.getDescription())) {
			    leafFound = true;
			    break;
			}
		    }

		    // A leaf node not found before
		    if (!leafFound) {
			break;
		    }
		}

		// This branch doesn't have the same leafs as the others
		if (!leafFound) {
		    break;
		}
	    }

	    // All the branches refer to the same relations, try to apply the
	    // reduction to keep only one of the branches
	    if (leafFound) {
		// Case: <expression> AND (<expression> OR <expression)
		// For each leaf node get the closest selection node, compare
		// all of them and see if they're the same, except for a
		// comparisson, that is, what original cause the Union
		orCondition = false;
		orConditionSelectNodes = null;
		orConditionLeafs = new ArrayList<Node>(leafNodes.size());

		for (Node leafNode : leafNodes) {
		    orCondition = false;
		    selectionNodes = new ArrayList<Node>(unionNode.getChildren().size());
		    for (Node branch : unionNode.getChildren()) {
			// Assume a relation appears only once
			// TODO: For nested queries this may not be true:.
			branchLeafNodes = branch.getLeafNodes(leafNode.getSqlData());
			selectNode = branchLeafNodes.get(0).getClosestRelationalOperatorNode(
				RelationalOperator.SELECT, null);

			if (selectNode != null) {
			    selectionNodes.add(selectNode);
			}
		    }

		    // Check if the select conditions are the same, if they
		    // don't this is the operator that generated the separation
		    // of the operator tree
		    for (Node selectionNode : selectionNodes) {
			for (Node otherSelectionNode : selectionNodes) {
			    if (!selectionNode.getSqlData().equalsIgnoreCase(
				    otherSelectionNode.getSqlData())) {
				orCondition = true;
				orConditionSelectNodes = selectionNodes;
				break;
			    }
			}

			// Or condition detected
			if (orCondition) {
			    break;
			}
		    }

		    // The selection nodes that affect this leaf node in the
		    // branches were the ones that generated the separation of
		    // the Operator Tree
		    if (orCondition) {
			orConditionLeafs.add(leafNode);
		    }
		}

		// Group the orConditionLeafs identified before into just one
		// branch, with the Union node
		if (!orConditionLeafs.isEmpty()) {
		    for (Node orConditionLeaf : orConditionLeafs) {
			// Assume a relation appears only once
			// TODO: For nested queries this may not be true:.
			selectNode = firstBranch.getLeafNodes(orConditionLeaf.getSqlData()).get(0)
				.getClosestRelationalOperatorNode(RelationalOperator.SELECT, null);
			selectParent = selectNode.getParent();
			newUnionNode = new Node(RelationalOperator.UNION);
			for (Node orConditionSelectNode : orConditionSelectNodes) {
			    orConditionSelectNode.getParent().removeChild(orConditionSelectNode);
			    newUnionNode.addChild(orConditionSelectNode);
			}

			// Group the selection nodes by one Union node
			selectParent.addChild(newUnionNode);
		    }

		    // Remove all but the fist branch of the original Union node
		    unionNode.getParent().addChild(firstBranch);
		    unionNode.getParent().removeChild(unionNode);
		    this.exportTemporaryTree(operatorTree, queryId, true, returnValue,
			    "CustomOrConditionsReduction", imageDir);
		    returnValue++;
		}
	    }
	}

	return returnValue;
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
     *            Flag that indicates whether this tree should be exported or
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

    /**
     * Try to add the Operation Node just before the Leaf Nodes that contain the
     * operation relation.
     * 
     * @param relation
     *            Relation that identifies the Leaf Nodes.
     * @param operationNode
     *            Operation Node.
     * @param operationChildNode
     *            Child of the Operation Node whose hierarchy contains the Leaf
     *            Node.
     * @param currentChildren
     *            Current children of the Operation Node.
     * @return <em>true</em> if the Operation Node is correctly added just
     *         before the Leaf Nodes, <em>false</em> otherwise.
     */
    private boolean addOperationNodeBeforeLeafNode(final String relation, final Node operationNode,
	    final Node operationChildNode, final List<Node> currentChildren) {
	boolean returnValue;
	List<Node> leafNodes;
	Node newOperationNode;
	Set<String> operationSqlDataElements;

	// Look for the child of the operationChildNode that
	// contains the relation (leafNode) in order to apply
	// the operation there
	returnValue = false;
	leafNodes = operationChildNode.getLeafNodes(databaseDictionaryService
		.getRelationNames(relation));

	if (!leafNodes.isEmpty()) {
	    // Disassociate operationNode with its
	    // children
	    operationNode.removeAllChildren();

	    // Intermediary node
	    if (operationNode.getParent() != null) {
		// The former children of the operationNode are now
		// children
		// of the former parent of the operationNode
		operationNode.getParent().addChildren(currentChildren);
		operationNode.getParent().removeChild(operationNode);
	    }

	    // The operationNode is added between the Leaf Node and the
	    // leaf's parent
	    for (Node leafNode : leafNodes) {
		newOperationNode = operationNode.clone();
		if (leafNode.getParent() != null) {
		    // In case of compund operators, all the intermediary
		    // attributes used in the Node's hierarchy, before reaching
		    // the leaf node (for joins, selections, etc.), need to be
		    // added to the operationNode SqlData
		    if ((operationNode.getRelationalOperator() != null)
			    && (operationNode.getRelationalOperator().isCompoundOperator())) {
			operationSqlDataElements = leafNode.getRelationAttributes(relation,
				operationChildNode);
			operationSqlDataElements.addAll(Arrays.asList(newOperationNode
				.getSqlDataElements()));
			newOperationNode.setSqlData(operationSqlDataElements);
		    }

		    leafNode.getParent().addChild(newOperationNode);
		    leafNode.getParent().removeChild(leafNode);
		}

		// Add the leaf node just after the operation node
		newOperationNode.addChild(leafNode);
	    }

	    returnValue = true;
	}

	return returnValue;
    }
}
