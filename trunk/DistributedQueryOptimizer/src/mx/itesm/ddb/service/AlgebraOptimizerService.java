package mx.itesm.ddb.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mx.itesm.ddb.service.operator.Node;
import mx.itesm.ddb.service.operator.OperatorTree;
import mx.itesm.ddb.service.operator.RelationalOperator;
import mx.itesm.ddb.util.ConditionData;
import mx.itesm.ddb.util.ConditionOperator;
import mx.itesm.ddb.util.ExpressionData;
import mx.itesm.ddb.util.ExpressionOperator;
import mx.itesm.ddb.util.RelationData;
import mx.itesm.ddb.util.SqlData;
import mx.itesm.ddb.util.impl.ExpressionConditionData;
import mx.itesm.ddb.util.impl.OperationConditionData;
import mx.itesm.ddb.util.impl.OperationExpressionData;
import mx.itesm.ddb.util.impl.SimpleExpressionData;

import org.apache.log4j.Logger;

/**
 * Relational Algebra Optimizer.
 * 
 * @author jccastrejon
 * 
 */
public class AlgebraOptimizerService {

    /**
     * Class logger.
     */
    private final static Logger logger = Logger.getLogger(AlgebraOptimizerService.class);

    /**
     * Database Dictionary Service.
     */
    private DatabaseDictionaryService databaseDictionaryService;

    /**
     * Build the Optimal Operator Tree from the given SQL query.
     * 
     * @param query
     *            SQL Query.
     * @param imageDir
     *            Directory where to save the temporary operator trees.
     * @throws IOException
     *             In an I/O error occurs.
     */
    public void buildOperatorTree(final Query query, final File imageDir) throws IOException {
	OperatorTree operatorTree;
	Node rootNode;
	List<Node> leafNodes;
	File currentOperatorTreeImage;
	int intermediateOperatorTreeCount;

	// TODO: Repeat this till we find the optimal tree
	intermediateOperatorTreeCount = 0;
	rootNode = this.getRootNode(query.getQueryData().getAttributes());
	leafNodes = this.getLeafNodes(query.getQueryData().getRelations());
	this.getConditionNodes(query.getQueryData().getConditions(), leafNodes);

	operatorTree = this.orderNodes(rootNode, leafNodes);
	currentOperatorTreeImage = new File(imageDir.getAbsolutePath() + "/" + query.getId() + "-"
		+ (intermediateOperatorTreeCount++) + ".png");
	// currentOperatorTreeImage.deleteOnExit();
	this.exportOperatorTreeToPNG(operatorTree, currentOperatorTreeImage);

	query.setOperatorTree(operatorTree);
    }

    /**
     * Export the given Operator Tree to the specified PNG file.
     * 
     * @param operatorTree
     *            Operator Tree.
     * @param imageFile
     *            Image File where the image will be saved.
     * @throws IOException
     *             In an I/O error occurs.
     */
    public void exportOperatorTreeToPNG(final OperatorTree operatorTree, final File imageFile)
	    throws IOException {
	File dotFile;
	int processCode;
	Process process;
	String fileName;
	String dotCommand;
	FileWriter fileWriter;
	StringBuilder dotDescription;

	if ((imageFile == null) || (!imageFile.getAbsolutePath().endsWith(".png"))) {
	    throw new IllegalArgumentException("Not an png file: " + imageFile.getAbsolutePath());
	}

	fileName = imageFile.getName().substring(0, imageFile.getName().indexOf('.'));
	dotFile = new File(imageFile.getParent() + "/" + fileName + ".dot");

	// Build dot file
	dotDescription = new StringBuilder("digraph \"" + fileName
		+ "\" {\n\tnode[shape=box, fontsize=8];\n");
	dotDescription.append(operatorTree.getRootNode().toString());
	dotDescription.append("}");

	// Save dot file
	fileWriter = new FileWriter(dotFile, false);
	fileWriter.write(dotDescription.toString());
	fileWriter.close();

	// Execute dot command
	try {
	    dotCommand = "dot -Tpng " + dotFile.getAbsolutePath() + " -o "
		    + imageFile.getAbsolutePath();
	    process = Runtime.getRuntime().exec(dotCommand);
	    processCode = process.waitFor();
	    dotFile.delete();

	    if (processCode != 0) {
		throw new RuntimeException("An error ocurred while executing: " + dotCommand);
	    }

	} catch (Exception e) {
	    logger.error("Error creating image file: " + imageFile.getAbsolutePath(), e);
	    throw new RuntimeException("Error creating image file: " + imageFile.getAbsolutePath(),
		    e);
	}
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
     */
    private List<Node> getLeafNodes(final List<RelationData> relations) {
	List<Node> returnValue;

	returnValue = new ArrayList<Node>();
	for (RelationData relationData : relations) {
	    returnValue.add(new Node(relationData));
	}

	return returnValue;
    }

    /**
     * The qualification (SQL WHERE clause) is translated into the appropiate.
     * sequence of relational operators, going from the leaves to the root
     * 
     * @param conditionData
     *            ConditionData.
     */
    private void getConditionNodes(final ConditionData conditionData, final List<Node> leafNodes) {
	OperationConditionData operationConditionData;

	if (conditionData != null) {
	    // [table.attribute = table2.attribute]
	    if (conditionData instanceof ExpressionConditionData) {
		this.addConditionNodeFromExpressionConditionData(
			(ExpressionConditionData) conditionData, leafNodes);
	    }

	    // [table.attribute = table2.attribute] and
	    // [table.attribute2 = table3.attribute2]
	    else if (conditionData instanceof OperationConditionData) {
		operationConditionData = (OperationConditionData) conditionData;

		// TODO: Support other operators
		if (operationConditionData.getOperator() == ConditionOperator.BinaryOperator.AND_OPERATOR) {
		    for (ConditionData innerConditionData : operationConditionData.getConditions()) {
			if (innerConditionData instanceof ExpressionConditionData) {
			    this.addConditionNodeFromExpressionConditionData(
				    (ExpressionConditionData) innerConditionData, leafNodes);
			}
		    }
		} else {
		    throw new RuntimeException("Unsupported query");
		}
	    } else {
		throw new RuntimeException("Unsupported query");
	    }
	}
    }

    /**
     * Add a leaf node corresponding to a condition in the expression received
     * as parameter.
     * 
     * @param expressionConditionData
     *            Expression containing the condition data.
     * @param leafNodes
     *            Leaf Nodes.
     */
    private void addConditionNodeFromExpressionConditionData(
	    final ExpressionConditionData expressionConditionData, final List<Node> leafNodes) {
	Set<String> tables;
	ExpressionData expressionData;
	List<ExpressionData> expressions;
	ExpressionOperator expressionOperator;
	RelationalOperator relationalOperator;
	List<Node> newNodeChildren;
	Node newNode;

	expressionData = expressionConditionData.getExpression();

	if (expressionData instanceof OperationExpressionData) {
	    expressionOperator = ((OperationExpressionData) expressionData).getOperator();

	    // TODO: Support other operators
	    if (expressionOperator == ExpressionOperator.ArithmeticOperator.EQUALS_OPERATOR) {
		expressions = ((OperationExpressionData) expressionData).getExpressions();

		tables = this.getRequiredTables(expressions, leafNodes);

		if (tables.size() > 1) {
		    relationalOperator = RelationalOperator.JOIN;
		} else {
		    relationalOperator = RelationalOperator.SELECT;
		}

		newNodeChildren = new ArrayList<Node>();
		for (String table : tables) {
		    nodeSearch: for (Node leafNode : leafNodes) {
			for (SqlData sqlData : leafNode.getSqlData()) {
			    if (sqlData.toString().contains(table)) {
				newNodeChildren.add(leafNode);
				break nodeSearch;
			    }
			}
		    }
		}

		newNode = new Node(expressions.toArray(new SqlData[expressions.size()]),
			relationalOperator);
		newNode.addChildren(newNodeChildren);
		leafNodes.removeAll(newNodeChildren);
		leafNodes.add(newNode);
	    } else {
		throw new RuntimeException("Unsupported query");
	    }
	} else {
	    throw new RuntimeException("Unsupported query");
	}
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
	Set<String> returnValue;
	SimpleExpressionData simpleExpressionData;

	returnValue = new HashSet<String>();

	for (ExpressionData expressionData : expressions) {
	    if (expressionData instanceof SimpleExpressionData) {
		simpleExpressionData = (SimpleExpressionData) expressionData;
		returnValue.add(databaseDictionaryService
			.getTableFromExpression(simpleExpressionData.getExpression()));
	    } else {
		throw new RuntimeException("Unsupported query");
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
}
