package mx.itesm.ddb.service;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import mx.itesm.ddb.model.dictionary.FragmentationType;
import mx.itesm.ddb.model.dictionary.HorizontalFragment;
import mx.itesm.ddb.model.dictionary.Predicate;
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
	boolean fragmentsFound;

	returnValue = rewritingSteps;

	// GenericQuery
	fragmentsFound = this.buildGenericQuery(operatorTree.getRootNode());
	if (fragmentsFound) {
	    graphicExportService.saveIntermediateOperatorTree(operatorTree, queryId,
		    (++returnValue), "GenericQuery", imageDir);

	    // Primary Horizontal Fragments
	    this.reducePrimaryHorizontalFragmentation(operatorTree.getRootNode());

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
     * Selections on fragments that have a qualification contradicting the
     * qualification of the fragmentation rule generate empty relations.
     * 
     * @param currentNode
     *            Starting node for the analysis.
     */
    private void reducePrimaryHorizontalFragmentation(final Node currentNode) {
	Relation fragment;
	Node operatorNode;
	List<Node> leafNodes;
	StringBuilder selectionAttributeCondition;
	String predicateAttribute;
	String[] selectionConditionElements;
	HorizontalFragment horizontalFragment;
	Collection<Predicate> fragmentPredicates;

	// Reduction with Selection
	leafNodes = currentNode.getLeafNodes();
	for (Node leafNode : leafNodes) {
	    operatorNode = leafNode.getParent();
	    if ((operatorNode != null)
		    && (operatorNode.getRelationalOperator() == RelationalOperator.UNION)) {
		if ((operatorNode.getParent() != null)
			&& (operatorNode.getParent().getRelationalOperator() == RelationalOperator.SELECT)) {
		    selectionConditionElements = operatorNode.getParent().getSqlDataElements();

		    // Check for every fragment if their minterm contradicts the
		    // selection condition
		    for (Node child : operatorNode.getChildren()) {
			fragment = databaseDictionaryService.getRelation(child.getSqlData());

			if ((fragment != null)
				&& (fragment.getFragmentationType() == FragmentationType.Horizontal)) {
			    horizontalFragment = (HorizontalFragment) fragment;
			    fragmentPredicates = horizontalFragment.getMinterm();

			    if (fragmentPredicates != null) {
				for (Predicate predicate : fragmentPredicates) {
				    predicateAttribute = predicate.getAttribute().getName()
					    .toLowerCase();

				    selectionAttributeCondition = new StringBuilder();
				    for (int i = 0; i < selectionConditionElements.length; i++) {
					if (selectionConditionElements[i].toLowerCase()
						.equalsIgnoreCase(predicateAttribute)
						&& ((i + 4) < selectionConditionElements.length)) {
					    selectionAttributeCondition.append(
						    selectionConditionElements[i]).append(
						    selectionConditionElements[i + 1]).append(
						    selectionConditionElements[i + 2]).append(
						    selectionConditionElements[i + 3]).append(
						    selectionConditionElements[i + 4]);
					    break;
					}
				    }

				    logger.warn("Check reduction with selection between: "
					    + selectionAttributeCondition + " and "
					    + fragmentPredicates);
				}
			    }
			}
		    }
		}
	    }
	}

	// Reduction with Join
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
