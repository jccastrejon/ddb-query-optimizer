package mx.itesm.ddb.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mx.itesm.ddb.service.operator.Node;
import mx.itesm.ddb.service.operator.OperatorTree;
import mx.itesm.ddb.util.ConditionData;
import mx.itesm.ddb.util.ConditionOperator;
import mx.itesm.ddb.util.ExpressionData;
import mx.itesm.ddb.util.ExpressionOperator;
import mx.itesm.ddb.util.QueryData;
import mx.itesm.ddb.util.RelationData;
import mx.itesm.ddb.util.RelationalOperator;
import mx.itesm.ddb.util.SqlData;
import mx.itesm.ddb.util.impl.ConditionExpressionData;
import mx.itesm.ddb.util.impl.ExpressionConditionData;
import mx.itesm.ddb.util.impl.OperationConditionData;
import mx.itesm.ddb.util.impl.OperationExpressionData;
import mx.itesm.ddb.util.impl.QueryExpressionData;
import mx.itesm.ddb.util.impl.QueryRelationData;
import mx.itesm.ddb.util.impl.SimpleExpressionData;
import mx.itesm.ddb.util.impl.SimpleRelationData;

/**
 * Relational Algebra Optimizer.
 * 
 * @author jccastrejon
 * 
 */
public class AlgebraOptimizerService {

    /**
     * Rewriting Service.
     */
    private RewritingService rewritingService;

    /**
     * Graphic Export Service.
     */
    GraphicExportService graphicExportService;

    /**
     * Localization Service.
     */
    private LocalizationService localizationService;

    /**
     * Database Dictionary Service.
     */
    private DatabaseDictionaryService databaseDictionaryService;

    /**
     * Build the Optimal Operator Tree from the given SQL query and save it in
     * the query's operatorTree property.
     * 
     * @param query
     *            SQL Query.
     * @throws IOException
     *             If an I/O error occurs.
     */
    public void buildOperatorTree(final Query query) throws IOException {
	OperatorTree operatorTree;

	operatorTree = this.buildOperatorTree(query.getQueryData(), query.getId(), null);
	query.setOperatorTree(operatorTree);
    }

    /**
     * Build the Optimal Operator Tree from the given SQL QueryData.
     * 
     * @param queryData
     *            SQL QueryData.
     * @return Optimal Operator Tree.
     * @throws IOException
     *             If an I/O error occurs.
     */
    public OperatorTree buildOperatorTree(final QueryData queryData) throws IOException {
	Query returnValue;

	returnValue = new Query(queryData);
	this.buildOperatorTree(returnValue, null);
	return returnValue.getOperatorTree();
    }

    /**
     * Build the Optimal Operator Tree from the given SQL query and save it in
     * the query's operatorTree property. The intermediate non-optimal operator
     * trees are saved in a directory with the Query Id as name, in the
     * specified Image Directory.
     * 
     * @param query
     *            SQL Query.
     * @param imageDir
     *            Directory where to save the temporary operator trees.
     * @throws IOException
     *             If an I/O error occurs.
     */
    public void buildOperatorTree(final Query query, final File imageDir) throws IOException {
	OperatorTree operatorTree;

	operatorTree = this.buildOperatorTree(query.getQueryData(), query.getId(), imageDir);
	query.setOperatorTree(operatorTree);
    }

    /**
     * Build the Optimal Operator Tree from the given SQL query and save it in
     * the query's operatorTree property. The intermediate non-optimal operator
     * trees are saved in a directory with the Query Id as name, in the
     * specified Image Directory.
     * 
     * @param query
     *            SQL Query.
     * @param queryId
     *            Query Id.
     * @param imageDir
     *            Directory where to save the temporary operator trees.
     * @throws IOException
     *             If an I/O error occurs.
     */
    protected OperatorTree buildOperatorTree(final QueryData queryData, final String queryId,
	    final File imageDir) throws IOException {
	Node rootNode;
	int rewritingSteps;
	List<Node> leafNodes;
	OperatorTree returnValue;
	List<String> finalProjectionAttributes;
	List<String> originalProjectionAttributes;

	rootNode = this.getRootNode(queryData.getAttributes());
	leafNodes = this.getLeafNodes(queryData.getRelations());
	leafNodes = this.getConditionNodes(queryData.getConditions(), leafNodes);

	rewritingSteps = 0;
	returnValue = this.orderNodes(rootNode, leafNodes);
	originalProjectionAttributes = this.databaseDictionaryService
		.getAttributesFromSqlData(rootNode.getSqlData());
	this.graphicExportService.saveIntermediateOperatorTree(returnValue, queryId,
		rewritingSteps, "Initial", imageDir);

	// Rewrite Operator Tree according to the Global Relations
	rewritingSteps = rewritingService.rewriteOperatorTree(returnValue, queryId, imageDir,
		rewritingSteps);

	// Custom OR conditions reduction
	rewritingSteps = rewritingService.customOrConditionsReduction(returnValue, queryId,
		imageDir, rewritingSteps);

	// Reduce Global Relations to Fragments and apply reductions
	rewritingSteps = localizationService.reduceRelationFragments(returnValue, rewritingSteps,
		queryId, imageDir);

	// Make sure the projection attributes list of the initial tree is
	// maintained
	if (rootNode.getRelationalOperator() == RelationalOperator.PROJECTION) {
	    finalProjectionAttributes = this.databaseDictionaryService
		    .getAttributesFromSqlData(rootNode.getSqlData());
	    if (finalProjectionAttributes.size() != originalProjectionAttributes.size()) {
		rootNode.setSqlData(originalProjectionAttributes);
		this.graphicExportService.saveIntermediateOperatorTree(returnValue, queryId,
			(++rewritingSteps), "OriginalProjectionAttributes", imageDir);
	    }
	}

	// Update the rewriting steps needed to generate the Operator Tree
	returnValue.setRewritingSteps(rewritingSteps);
	return returnValue;
    }

    /**
     * The root node is created as a project operation involving the result
     * attributes.
     * 
     * @param attributes
     *            ExpressionData attributes.
     * @return Root Node.
     */
    private Node getRootNode(final List<ExpressionData> attributes) {
	Node returnValue;

	returnValue = new Node(attributes.toArray(new SqlData[attributes.size()]),
		RelationalOperator.PROJECTION);
	return returnValue;
    }

    /**
     * A different leave is created for each different tuple variable.
     * 
     * @param relations
     *            RelationData relations.
     * @return Leaf Nodes.
     * @throws IOException
     */
    private List<Node> getLeafNodes(final List<RelationData> relations) throws IOException {
	List<Node> returnValue;
	OperatorTree operatorTree;

	returnValue = new ArrayList<Node>();
	for (RelationData relationData : relations) {
	    // Table
	    if (relationData instanceof SimpleRelationData) {
		returnValue.add(new Node(relationData));
	    }

	    // SubQuery
	    else if (relationData instanceof QueryRelationData) {
		operatorTree = this.buildOperatorTree(((QueryRelationData) relationData)
			.getQueryData());
		returnValue.add(operatorTree.getRootNode());
	    }
	}

	return returnValue;
    }

    /**
     * The qualification (SQL WHERE clause) is translated into the appropiate.
     * sequence of relational operators, going from the leaves to the root
     * 
     * @param conditionData
     *            ConditionData.
     * @throws IOException
     */
    private List<Node> getConditionNodes(final ConditionData conditionData,
	    final List<Node> leafNodes) throws IOException {
	List<Node> returnValue;
	List<Node> newLeafNodes;
	Node unionConditionNode;
	Node productConditionNode;
	List<List<Node>> unionLeafNodes;
	OperationConditionData operationConditionData;

	returnValue = leafNodes;
	if (conditionData != null) {
	    // [table.attribute = table2.attribute]
	    if (conditionData instanceof ExpressionConditionData) {
		returnValue = this.getConditionNodeFromExpressionConditionData(
			(ExpressionConditionData) conditionData, leafNodes);
	    }

	    // [table.attribute = table2.attribute] [operator]
	    // [table.attribute2 = table3.attribute2]
	    else if (conditionData instanceof OperationConditionData) {
		operationConditionData = (OperationConditionData) conditionData;

		// operator = AND
		if (operationConditionData.getOperator() == ConditionOperator.BinaryOperator.AND_OPERATOR) {
		    for (ConditionData innerConditionData : operationConditionData.getConditions()) {
			// AND [expression]
			if (innerConditionData instanceof ExpressionConditionData) {
			    returnValue = this.getConditionNodeFromExpressionConditionData(
				    (ExpressionConditionData) innerConditionData, leafNodes);
			}

			// AND [expression] AND [expression] AND ...
			else if (innerConditionData instanceof OperationConditionData) {
			    returnValue = this.getConditionNodes(
				    (OperationConditionData) innerConditionData, leafNodes);
			}
		    }
		}

		// operator = OR
		else if (operationConditionData.getOperator() == ConditionOperator.BinaryOperator.OR_OPERATOR) {
		    unionLeafNodes = new ArrayList<List<Node>>(operationConditionData
			    .getConditions().size());
		    for (ConditionData innerConditionData : operationConditionData.getConditions()) {
			newLeafNodes = new ArrayList<Node>(leafNodes.size());
			for (Node leafNode : leafNodes) {
			    newLeafNodes.add(leafNode.clone());
			}

			newLeafNodes = this.getConditionNodeFromExpressionConditionData(
				(ExpressionConditionData) innerConditionData, newLeafNodes);
			unionLeafNodes.add(newLeafNodes);
		    }

		    // Unite the leafNodes set into only one leafNode
		    unionConditionNode = new Node(RelationalOperator.UNION);
		    returnValue = new ArrayList<Node>(1);
		    returnValue.add(unionConditionNode);

		    for (List<Node> condition : unionLeafNodes) {
			if (condition.size() == 1) {
			    unionConditionNode.addChild(condition.get(0));
			} else {
			    // If there are more than one nodes, group them by
			    // making the cartesian product before uniting them
			    productConditionNode = new Node(RelationalOperator.PRODUCT);
			    unionConditionNode.addChild(productConditionNode);
			    for (Node innerCondition : condition) {
				productConditionNode.addChild(innerCondition);
			    }
			}
		    }
		}

		// operator = NOT
		else if (operationConditionData.getOperator() == ConditionOperator.UnaryOperator.NOT_OPERATOR) {
		    // TODO: Implementation!!:.
		} else {
		    throw new RuntimeException("Unsupported query");
		}
	    } else {
		throw new RuntimeException("Unsupported query");
	    }
	}

	return returnValue;
    }

    /**
     * Add a leaf node corresponding to a condition in the expression received
     * as parameter.
     * 
     * @param expressionConditionData
     *            Expression containing the condition data.
     * @param leafNodes
     *            Leaf Nodes.
     * @throws IOException
     */
    private List<Node> getConditionNodeFromExpressionConditionData(
	    final ExpressionConditionData expressionConditionData, final List<Node> leafNodes)
	    throws IOException {
	Node newNode;
	Set<String> tables;
	List<Node> returnValue;
	List<SqlData> nodeData;
	OperatorTree operatorTree;
	List<Node> newNodeChildren;
	ExpressionData expressionData;
	List<ExpressionData> expressions;
	ExpressionOperator expressionOperator;
	RelationalOperator relationalOperator;

	// Expression to analyze
	expressionData = expressionConditionData.getExpression();

	// [expression] [operator] [expression]
	if (expressionData instanceof OperationExpressionData) {
	    expressionOperator = ((OperationExpressionData) expressionData).getOperator();
	    expressions = ((OperationExpressionData) expressionData).getExpressions();

	    // Decide how to join the leafNodes
	    tables = this.getRequiredTables(expressions, leafNodes);
	    if (tables.size() > 1) {
		relationalOperator = RelationalOperator.JOIN;
	    } else {
		relationalOperator = RelationalOperator.SELECT;
	    }

	    // Find the nodes that contains the related tables
	    newNodeChildren = new ArrayList<Node>();
	    for (String table : tables) {
		for (Node leafNode : leafNodes) {
		    if (leafNode
			    .containsLeafNode(databaseDictionaryService.getRelationNames(table))) {
			newNodeChildren.add(leafNode);
			break;
		    }
		}
	    }

	    // Differentiate between simple and subquery condition data
	    nodeData = new ArrayList<SqlData>(expressions.size());
	    for (ExpressionData expression : expressions) {
		if (expression instanceof SimpleExpressionData) {
		    nodeData.add(expression);
		    nodeData.add(new SimpleExpressionData(expressionOperator.toString()));
		} else if (expression instanceof QueryExpressionData) {
		    operatorTree = this.buildOperatorTree(((QueryExpressionData) expression)
			    .getQueryData());
		    newNodeChildren.add(operatorTree.getRootNode());
		    relationalOperator = RelationalOperator.JOIN;
		}
	    }

	    // Remove last operator
	    if (nodeData.size() > 1) {
		nodeData.remove(nodeData.size() - 1);
	    }

	    // Add a new node that represents the link between the previous
	    // leafNodes involved in the ExpressionConditionData. The previous
	    // leafNodes are no longer leafNodes
	    newNode = new Node(nodeData.toArray(new SqlData[nodeData.size()]), relationalOperator);
	    newNode.addChildren(newNodeChildren);
	    leafNodes.removeAll(newNodeChildren);
	    leafNodes.add(newNode);
	    returnValue = leafNodes;
	}

	// ( [condition] [operator] [condition] [operator] ... )
	else if (expressionData instanceof ConditionExpressionData) {
	    returnValue = new ArrayList<Node>(1);
	    newNodeChildren = this.getConditionNodes(((ConditionExpressionData) expressionData)
		    .getCondition(), leafNodes);

	    // If the operator was an OR, there should be only one parent node
	    // with the UNION operator
	    if (newNodeChildren.size() == 1) {
		returnValue.add(newNodeChildren.get(0));
	    } else {
		// If there are more than one nodes, group them by
		// making the cartesian product before uniting them
		newNode = new Node(RelationalOperator.PRODUCT);
		returnValue.add(newNode);
		for (Node innerChild : newNodeChildren) {
		    newNode.addChild(innerChild);
		}
	    }
	} else {
	    throw new RuntimeException("Unsupported query");
	}

	return returnValue;
    }

    /**
     * Get the tables involved in the expressions received as parameter.
     * 
     * @param expressions
     *            List of Expressions.
     * @param leafNodes
     *            Leaf Nodes.
     * @return Set of required tables.
     */
    private Set<String> getRequiredTables(final List<ExpressionData> expressions,
	    final List<Node> leafNodes) {
	String currentTable;
	Set<String> returnValue;
	SimpleExpressionData simpleExpressionData;

	returnValue = new HashSet<String>();
	for (ExpressionData expressionData : expressions) {
	    if (expressionData instanceof SimpleExpressionData) {
		simpleExpressionData = (SimpleExpressionData) expressionData;
		currentTable = databaseDictionaryService
			.getTableFromExpression(simpleExpressionData.getExpression());

		// Add the referenced table from the expression
		if (currentTable != null) {
		    returnValue.add(currentTable);
		}
	    }
	}

	return returnValue;
    }

    /**
     * Order root and leaf nodes in the correct order for the Operator Tree.
     * 
     * @param rootNode
     *            Root Node.
     * @param leafNodes
     *            Leaf Nodes.
     * @return Operator Tree.
     */
    private OperatorTree orderNodes(final Node rootNode, final List<Node> leafNodes) {
	OperatorTree returnValue;
	Node unionNode = null;

	returnValue = new OperatorTree(rootNode);
	if ((leafNodes != null) && (!leafNodes.isEmpty())) {
	    if (leafNodes.size() > 1) {
		unionNode = new Node(RelationalOperator.PRODUCT);
		unionNode.addChildren(leafNodes);
		rootNode.addChild(unionNode);
	    } else {
		rootNode.addChild(leafNodes.get(0));
	    }
	} else {
	    throw new RuntimeException("Unsupported query");
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
    public void setDatabaseDictionaryService(DatabaseDictionaryService databaseDictionaryManager) {
	this.databaseDictionaryService = databaseDictionaryManager;
    }

    /**
     * @return the rewritingService
     */
    public RewritingService getRewritingService() {
	return rewritingService;
    }

    /**
     * @param rewritingService
     *            the rewritingService to set
     */
    public void setRewritingService(RewritingService rewritingService) {
	this.rewritingService = rewritingService;
    }

    /**
     * @return the localizationService
     */
    public LocalizationService getLocalizationService() {
	return localizationService;
    }

    /**
     * @param localizationService
     *            the localizationService to set
     */
    public void setLocalizationService(LocalizationService localizationService) {
	this.localizationService = localizationService;
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
