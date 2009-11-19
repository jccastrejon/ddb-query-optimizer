package mx.itesm.ddb.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mx.itesm.ddb.model.dictionary.FragmentationType;
import mx.itesm.ddb.model.dictionary.HorizontalFragment;
import mx.itesm.ddb.model.dictionary.Predicate;
import mx.itesm.ddb.model.dictionary.PredicateOperator;
import mx.itesm.ddb.model.dictionary.Relation;
import mx.itesm.ddb.service.operator.Node;
import mx.itesm.ddb.service.operator.OperatorTree;
import mx.itesm.ddb.util.RelationalOperator;

import org.apache.log4j.Logger;

/**
 * Translates an algebraic query on global relations into an algebraic query
 * expressed on physical fragments.
 * 
 * @author jccastrejon
 * 
 */
public class LocalizationService {

    /**
     * Class logger
     */
    private final Logger logger = Logger.getLogger(LocalizationService.class);

    /**
     * Graphic Export Service.
     */
    GraphicExportService graphicExportService;

    /**
     * Database Dictionary DAO.
     */
    DatabaseDictionaryService databaseDictionaryService;

    /**
     * Use reduction techniques over the relation fragments to generate simpler
     * and optimized queries.
     * 
     * @param operatorTree
     *            Operator Tree.
     * @param rewritingSteps
     *            Rewriting steps so far.
     * @param queryId
     *            Query Id.
     * @param imageDir
     *            Directory where to save the temporary Operator Tree.
     * @return Number of steps needed to reduce the operator tree.
     * @throws IOException
     *             If an I/O error occurs.
     */
    public int reduceRelationFragments(final OperatorTree operatorTree, final int rewritingSteps,
	    final String queryId, final File imageDir) throws IOException {
	int returnValue;
	boolean reductionFound;

	returnValue = rewritingSteps;

	// GenericQuery
	reductionFound = this.buildGenericQuery(operatorTree.getRootNode());
	if (reductionFound) {
	    this.graphicExportService.saveIntermediateOperatorTree(operatorTree, queryId,
		    (++returnValue), "GenericQuery", imageDir);

	    // Primary Horizontal Fragments
	    reductionFound = this.reducePrimaryHorizontalFragmentation(operatorTree.getRootNode());
	    if (reductionFound) {
		this.graphicExportService.saveIntermediateOperatorTree(operatorTree, queryId,
			(++returnValue), "PrimaryHorizontalFragments", imageDir);
	    }

	    // Reduction for Primary Horizontal Fragmentation
	}

	return returnValue;
    }

    /**
     * Generates a query where each global relation is substituted by its
     * localization program.
     * 
     * @param currentNode
     *            Starting node for the analysis.
     */
    private boolean buildGenericQuery(final Node currentNode) {
	Node fragmentNode;
	boolean returnValue;
	Node fragmentsParent;
	List<Node> leafNodes;
	Relation currentRelation;
	Collection<Relation> currentRelationFragments;

	returnValue = false;
	leafNodes = currentNode.getLeafNodes();
	for (Node leafNode : leafNodes) {
	    currentRelation = this.databaseDictionaryService.getRelation(leafNode.getSqlData());

	    if (currentRelation != null) {
		currentRelationFragments = currentRelation.getFragments();

		if ((currentRelationFragments != null) && (!currentRelationFragments.isEmpty())) {
		    returnValue = true;
		    fragmentsParent = null;

		    // Decide the grouping operator
		    switch (currentRelation.getFragmentationType()) {
		    case Horizontal:
			fragmentsParent = new Node(RelationalOperator.UNION);
			break;
		    case Vertical:
			fragmentsParent = new Node(RelationalOperator.JOIN);
			break;
		    }

		    // Group fragments
		    for (Relation fragment : currentRelationFragments) {
			fragmentNode = new Node(fragment.getName());
			fragmentsParent.addChild(fragmentNode);
		    }

		    // Replace leafNode with the new Union
		    leafNode.getParent().addChild(fragmentsParent);
		    leafNode.getParent().removeChild(leafNode);
		}
	    }
	}

	return returnValue;
    }

    /**
     * The reduction of queries on horizontally fragmented relations consists
     * primarily of determining, after restructuring the subtrees, those that
     * will produce empty relations, and removing them. Horizontal fragmentation
     * can be exploited to simplify both selection and join operations.
     * 
     * @param currentNode
     *            Starting node for the analysis.
     */
    private boolean reducePrimaryHorizontalFragmentation(final Node currentNode) {
	Node operatorNode;
	boolean returnValue;
	List<Node> leafNodes;
	List<Node> ignoredNodes;
	Node selectionNode;

	// Reduction with Selection
	returnValue = false;
	ignoredNodes = new ArrayList<Node>();
	leafNodes = currentNode.getLeafNodes();
	for (Node leafNode : leafNodes) {
	    if (ignoredNodes.contains(leafNode)) {
		continue;
	    }

	    operatorNode = leafNode.getParent();
	    if ((operatorNode != null)
		    && (operatorNode.getRelationalOperator() == RelationalOperator.UNION)) {
		// Look for the closest Selection
		selectionNode = operatorNode.getParent();
		while ((selectionNode != null)
			&& (selectionNode.getRelationalOperator() != RelationalOperator.SELECT)) {
		    selectionNode = selectionNode.getParent();
		}

		// If a selection is found, proceed with reduction
		if (selectionNode != null) {
		    // All children of the Union node will be evaluated when the
		    // first chidl is found so there's no need to repeat this
		    // process with all of them
		    ignoredNodes.addAll(operatorNode.getChildren());
		    returnValue = this.reductionWithSelection(operatorNode, selectionNode);
		}
	    }
	}

	// Reduction with Join

	return returnValue;
    }

    /**
     * Selections on fragments that have a qualification contradicting the
     * qualification of the fragmentation rule generate empty relations.
     * 
     * @param unionNode
     *            Union Node of the relation fragments.
     * @param selectionNode
     *            Selection Node that contains the conditions to test.
     * @return <em>true</em> if a reduction was applied over the unionNode,
     *         <em>false</em> otherwise.
     */
    private boolean reductionWithSelection(final Node unionNode, final Node selectionNode) {
	Relation fragment;
	boolean returnValue;
	List<Node> emptyLeafs;
	int comparissonResult;
	String conditionValue;
	boolean invalidMinterm;
	String currentCondition;
	String predicateAttribute;
	String selectionCondition;
	List<String> selectionConditions;
	int currentSelectionConditionIndex;
	PredicateOperator conditionOperator;
	Collection<Predicate> fragmentPredicates;

	// Check for every fragment if their minterm contradicts the
	// selection condition
	returnValue = false;
	emptyLeafs = new ArrayList<Node>();
	for (Node child : unionNode.getChildren()) {
	    fragment = databaseDictionaryService.getRelation(child.getSqlData());

	    if ((fragment != null)
		    && (fragment.getFragmentationType() == FragmentationType.Horizontal)) {
		fragmentPredicates = ((HorizontalFragment) fragment).getMinterm();

		invalidMinterm = false;
		if (fragmentPredicates != null) {
		    // Condition on the Selection Node
		    selectionCondition = selectionNode.getSqlData();
		    for (Predicate predicate : fragmentPredicates) {

			// Get the conditions that apply to this
			// predicate
			predicateAttribute = predicate.getAttribute().getName().toLowerCase();
			currentSelectionConditionIndex = selectionCondition
				.indexOf(predicateAttribute);
			selectionConditions = new ArrayList<String>();
			while (currentSelectionConditionIndex >= 0) {
			    // Unique condition
			    currentCondition = selectionCondition
				    .substring(currentSelectionConditionIndex);

			    // Part of a complex selection condition
			    if (currentCondition.indexOf(')') > 0) {
				currentCondition = currentCondition.substring(0, currentCondition
					.indexOf(')') - 1);
			    }

			    // Add to the selection conditions for
			    // this predicate
			    selectionConditions.add(currentCondition.trim());

			    // Look for the next condition
			    currentSelectionConditionIndex = selectionCondition.indexOf(
				    predicateAttribute, currentSelectionConditionIndex + 1);
			}

			// Check if there's any violation
			comparissonResult = 0;
			for (String testCondition : selectionConditions) {
			    conditionValue = null;
			    conditionOperator = null;
			    for (PredicateOperator operator : PredicateOperator.values()) {
				if (testCondition.indexOf(operator.getDescription()) > 0) {
				    conditionOperator = operator;
				    conditionValue = testCondition.substring(testCondition.indexOf(
					    ' ', testCondition.indexOf(operator.getDescription())));
				    break;
				}
			    }

			    comparissonResult = predicate.getAttribute().getAttributeDomain()
				    .compareValues(conditionValue, predicate.getValue());

			    invalidMinterm = conditionOperator.isInvalidComparisson(
				    comparissonResult, predicate.getPredicateOperator());
			}

			if (invalidMinterm) {
			    emptyLeafs.add(child);
			    logger.warn("Empty fragment: " + fragment + " conditions: "
				    + selectionCondition + " and " + predicate + " in fragment: "
				    + fragment + " comparisson result: " + comparissonResult);
			    break;
			}
		    }
		}
	    }
	}

	// Remove empty relations
	if (!emptyLeafs.isEmpty()) {
	    returnValue = true;
	    for (Node emptyLeaf : emptyLeafs) {
		emptyLeaf.getParent().removeChild(emptyLeaf);
	    }
	}

	// If there's only one fragment left in the Union node, there's no need
	// for a Union after all
	if ((unionNode.getChildren() != null) && (unionNode.getChildren().size() == 1)) {
	    unionNode.getParent().addChild(unionNode.getChildren().get(0));
	    unionNode.getParent().removeChild(unionNode);
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
