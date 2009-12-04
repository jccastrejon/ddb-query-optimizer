package mx.itesm.ddb.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mx.itesm.ddb.model.dictionary.Attribute;
import mx.itesm.ddb.model.dictionary.FragmentationType;
import mx.itesm.ddb.model.dictionary.MintermDependentFragment;
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
     * Rewriting Service.
     */
    private RewritingService rewritingService;

    /**
     * Graphic Export Service.
     */
    private GraphicExportService graphicExportService;

    /**
     * Database Dictionary DAO.
     */
    private DatabaseDictionaryService databaseDictionaryService;

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
	List<Boolean> returnValues;

	returnValue = rewritingSteps;
	returnValues = new ArrayList<Boolean>();
	do {
	    returnValues.clear();

	    // GenericQuery
	    reductionFound = this.buildGenericQuery(operatorTree.getRootNode());
	    returnValues.add(reductionFound);
	    if (reductionFound) {
		this.graphicExportService.saveIntermediateOperatorTree(operatorTree, queryId,
			(++returnValue), "GenericQuery", imageDir);

		returnValue = rewritingService.rewriteOperatorTree(operatorTree, queryId, imageDir,
			returnValue);

		// Vertical Fragments
		do {
		    reductionFound = this.reduceVerticalFragmentation(operatorTree.getRootNode());
		    returnValues.add(reductionFound);
		    if (reductionFound) {
			this.graphicExportService.saveIntermediateOperatorTree(operatorTree,
				queryId, (++returnValue), "PrimaryVerticalFragments", imageDir);

			returnValue = rewritingService.rewriteOperatorTree(operatorTree, queryId,
				imageDir, returnValue);
		    }
		} while (reductionFound);

		// Horizontal Fragments
		do {
		    reductionFound = this.reducePrimaryHorizontalFragmentation(operatorTree
			    .getRootNode());
		    returnValues.add(reductionFound);
		    if (reductionFound) {
			this.graphicExportService.saveIntermediateOperatorTree(operatorTree,
				queryId, (++returnValue), "PrimaryHorizontalFragments", imageDir);

			returnValue = rewritingService.rewriteOperatorTree(operatorTree, queryId,
				imageDir, returnValue);
		    }
		} while (reductionFound);

		returnValue = rewritingService.rewriteOperatorTree(operatorTree, queryId, imageDir,
			returnValue);
	    }
	} while (returnValues.contains(Boolean.TRUE));

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
	Node hybridJoinFragments;
	Node hybridUnionFragments;
	Collection<String> joinAttributes;
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
		    case DerivedHorizontal:
			fragmentsParent = new Node(RelationalOperator.UNION);
			break;
		    case Vertical:
			joinAttributes = new ArrayList<String>();
			for (Attribute attribute : currentRelation.getKeyAttributes()) {
			    joinAttributes.add(attribute.getName());
			}

			fragmentsParent = new Node(RelationalOperator.JOIN);
			fragmentsParent.setSqlData(joinAttributes);
			break;
		    case Hybrid:
			hybridJoinFragments = new Node(RelationalOperator.JOIN);
			hybridUnionFragments = new Node(RelationalOperator.UNION);

			// Union with all the hybrid and horizontal fragments
			for (Relation fragment : currentRelationFragments) {
			    if (fragment.getFragmentationType() != FragmentationType.Vertical) {
				fragmentNode = new Node(fragment.getName());
				hybridUnionFragments.addChild(fragmentNode);
			    } else {
				fragmentNode = new Node(fragment.getName());
				hybridJoinFragments.addChild(fragmentNode);
			    }
			}

			// If there was any vertical fragment, group it with the
			// Union node. If not, the return value is the Union
			if ((hybridJoinFragments.getChildren() != null)
				&& (!hybridJoinFragments.getChildren().isEmpty())) {
			    // Complete the Join node with the join attributes
			    joinAttributes = new ArrayList<String>();
			    for (Attribute attribute : currentRelation.getKeyAttributes()) {
				joinAttributes.add(attribute.getName());
			    }
			    hybridJoinFragments.setSqlData(joinAttributes);

			    // Group with Union
			    hybridJoinFragments.addChild(hybridUnionFragments);
			    fragmentsParent = hybridJoinFragments;
			} else {
			    fragmentsParent = hybridUnionFragments;
			}

			break;
		    }

		    // Group fragments
		    if ((fragmentsParent.getChildren() == null)
			    || ((fragmentsParent.getChildren() != null) && (fragmentsParent
				    .getChildren().isEmpty()))) {
			for (Relation fragment : currentRelationFragments) {
			    fragmentNode = new Node(fragment.getName());
			    fragmentsParent.addChild(fragmentNode);
			}
		    }

		    // Replace leafNode with the new fragments parent
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
	Node unionNode;
	boolean returnValue;
	List<Node> leafNodes;
	Node upperOperatorNode;
	List<Node> ignoredNodes;
	boolean reductionApplied;

	// Reduction with Selection
	returnValue = false;
	ignoredNodes = new ArrayList<Node>();
	leafNodes = currentNode.getLeafNodes();

	// Custom reduction with selections with no intermediary Union node
	returnValue = this.customReductionWithSelection(leafNodes);

	// Check union cases
	if (!returnValue) {
	    for (Node leafNode : leafNodes) {
		if (ignoredNodes.contains(leafNode)) {
		    continue;
		}

		unionNode = leafNode.getClosestRelationalOperatorNode(RelationalOperator.UNION);
		if (unionNode != null) {
		    // Reduction with Selection
		    upperOperatorNode = unionNode
			    .getClosestRelationalOperatorNode(RelationalOperator.SELECT);

		    // If a selection is found, proceed with reduction
		    if (upperOperatorNode != null) {
			// All children of the Union node will be evaluated when
			// the first child is found so there's no need to repeat
			// this process with all of them
			ignoredNodes.addAll(unionNode.getLeafNodes());
			reductionApplied = this
				.reductionWithSelection(unionNode, upperOperatorNode);

			// If at least one reduction has been applied over the
			// leafs, the returnValue is true
			if ((!returnValue) && (reductionApplied)) {
			    returnValue = true;
			}
		    }

		    // Reduction with Join
		    if (!returnValue) {
			upperOperatorNode = unionNode
				.getClosestRelationalOperatorNode(RelationalOperator.JOIN);

			// If a join is found, proceed with reduction
			if (upperOperatorNode != null) {
			    // All children of the Union node will be evaluated
			    // when the first child is found so there's no need
			    // to repeat this process with all of them
			    ignoredNodes.addAll(upperOperatorNode.getLeafNodes());
			    reductionApplied = this.reductionWithJoin(unionNode, upperOperatorNode);

			    // If at least one reduction has been applied over
			    // the leafs, the returnValue is true
			    if ((!returnValue) && (reductionApplied)) {
				returnValue = true;
			    }
			}
		    }
		}
	    }

	    leafNodes = currentNode.getLeafNodes();
	    for (Node leafNode : leafNodes) {
		// If there's only one fragment left in the Union node,
		// there's no need for a Union after all
		unionNode = leafNode.getClosestRelationalOperatorNode(RelationalOperator.UNION);
		if ((unionNode != null) && (unionNode.getChildren() != null)
			&& (unionNode.getChildren().size() == 1)) {
		    unionNode.getParent().addChild(unionNode.getChildren().get(0));
		    unionNode.getParent().removeChild(unionNode);
		}
	    }
	}

	return returnValue;
    }

    /**
     * Custom reduction with Selection and Horizontal fragments, when there's no
     * intermediary Union node.
     * 
     * @param leafNodes
     *            Operator Tree's leaf nodes.
     * @return <em>true</em> if the custom reduction has been applied to the
     *         Operator Tree, <em>false</em> otherwise.
     */
    private boolean customReductionWithSelection(final List<Node> leafNodes) {
	boolean returnValue;
	List<Node> emptyLeafs;
	Node upperOperatorNode;

	// Reduction with Selection
	returnValue = false;
	emptyLeafs = new ArrayList<Node>();

	// Check if the nearest selection to a leaf causes empty relations,
	// without an intermediate Union. This can happen if
	// CommuteSelectionWithBinaryOperators is applied before reduction
	for (Node leafNode : leafNodes) {
	    upperOperatorNode = leafNode
		    .getClosestRelationalOperatorNode(RelationalOperator.SELECT);
	    if ((upperOperatorNode != null)
		    && (!upperOperatorNode.containsRelationalOperatorNode(RelationalOperator.UNION))) {
		if (this.generatesEmptyHorizontalFragment(leafNode, upperOperatorNode)) {
		    emptyLeafs.add(leafNode);
		}
	    }
	}

	// Remove empty relations
	if (!emptyLeafs.isEmpty()) {
	    returnValue = true;
	    for (Node emptyLeaf : emptyLeafs) {
		upperOperatorNode = emptyLeaf
			.getClosestRelationalOperatorNode(RelationalOperator.UNION);

		if (upperOperatorNode != null) {
		    upperOperatorNode.removeChild(upperOperatorNode
			    .getNodeContainingLeafNode(emptyLeaf.getSqlData()));
		}
	    }
	}

	// If there's only one fragment left in the Union node, there's no need
	// for a Union after all
	for (Node leafNode : leafNodes) {
	    upperOperatorNode = leafNode.getClosestRelationalOperatorNode(RelationalOperator.UNION);

	    if ((upperOperatorNode != null) && (upperOperatorNode.getChildren() != null)
		    && (upperOperatorNode.getChildren().size() == 1)) {
		upperOperatorNode.getParent().addChild(upperOperatorNode.getChildren().get(0));
		upperOperatorNode.getParent().removeChild(upperOperatorNode);
	    }
	}

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
	Node emptyBranch;
	boolean returnValue;
	List<Node> emptyLeafs;

	// Check for every fragment if their minterm contradicts the
	// selection condition
	returnValue = false;
	emptyLeafs = new ArrayList<Node>();
	for (Node leaf : unionNode.getLeafNodes()) {
	    if (this.generatesEmptyHorizontalFragment(leaf, selectionNode)) {
		emptyLeafs.add(leaf);
	    }
	}

	// Remove empty relations
	if (!emptyLeafs.isEmpty()) {
	    returnValue = true;
	    for (Node emptyLeaf : emptyLeafs) {
		emptyBranch = unionNode.getNodeContainingLeafNode(emptyLeaf.getSqlData());
		unionNode.removeChild(emptyBranch);
	    }
	}

	return returnValue;
    }

    /**
     * Check if the Selection node generates an empty relation when applied to
     * the given Relation fragment node.
     * 
     * @param fragmentNode
     *            Relation fragment node.
     * @param selectionNode
     *            Selection node.
     * @return <em>true</em> if the selection generates an empty relation,
     *         <em>false</em> otherwise.
     */
    private boolean generatesEmptyHorizontalFragment(final Node fragmentNode,
	    final Node selectionNode) {
	Relation fragment;
	boolean returnValue;
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

	returnValue = false;
	fragment = databaseDictionaryService.getRelation(fragmentNode.getSqlData());
	if (fragment instanceof MintermDependentFragment) {
	    fragmentPredicates = ((MintermDependentFragment) fragment).getMinterm();

	    invalidMinterm = false;
	    if (fragmentPredicates != null) {
		// Condition on the Selection Node
		selectionCondition = selectionNode.getSqlData().toLowerCase();
		for (Predicate predicate : fragmentPredicates) {

		    // Get the conditions that apply to this
		    // predicate
		    predicateAttribute = predicate.getAttribute().getName().toLowerCase();
		    currentSelectionConditionIndex = selectionCondition.indexOf(predicateAttribute);
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
				conditionValue = testCondition.substring(testCondition.indexOf(' ',
					testCondition.indexOf(operator.getDescription())));
				break;
			    }
			}

			comparissonResult = predicate.getAttribute().getAttributeDomain()
				.compareValues(conditionValue, predicate.getValue());

			invalidMinterm = conditionOperator.isInvalidComparisson(comparissonResult,
				predicate.getPredicateOperator());
		    }

		    if (invalidMinterm) {
			returnValue = true;
			logger.warn("Empty fragment: " + fragment + " conditions: "
				+ selectionCondition + " and " + predicate + " in fragment: "
				+ fragment + " comparisson result: " + comparissonResult);
			break;
		    }
		}
	    }
	}

	return returnValue;
    }

    /**
     * Joins on horizontally fragmented relations can be simplified when the
     * joined relations are fragmented according to the join attribute. The
     * simplification consists of distributing joins over unions and eliminating
     * useless joins.
     * 
     * @param unionNode
     *            Union Node of the relation fragments.
     * @param joinNode
     *            Join Node that contains the join attribute to test.
     * @return <em>true</em> if a reduction was applied over the unionNode,
     *         <em>false</em> otherwise.
     */
    private boolean reductionWithJoin(final Node unionNode, final Node joinNode) {
	Node newJoinNode;
	Node newUnionNode;
	Relation fragment;
	boolean returnValue;
	Node joinBranchNode;
	String attributeName;
	List<Node> leafNodes;
	int comparissonResult;
	Relation joinFragment;
	boolean invalidMinterm;
	Node currentBranchNode;
	Set<Node> ignoredNodes;
	Set<Node> ignoredLeafs;
	String joinAttributeName;
	Node firstJoinBranchNode;
	Node joinBranchUnionNode;
	Node secondJoinBranchNode;
	String joinCommonAttribute;
	Node ignoredLeafsBranchNode;
	Node currentBranchUnionNode;
	Node joinBranchUnionBranchNode;
	Node currentBranchUnionBranchNode;
	Collection<Predicate> fragmentPredicates;
	Collection<Predicate> joinFragmentPredicates;

	// Try to make the corresponding Joins
	returnValue = false;
	ignoredNodes = new HashSet<Node>();
	ignoredLeafs = new HashSet<Node>();

	// Make the Joins between all of the leaf Nodes of the joinNode and
	// group them with a new Union node
	leafNodes = joinNode.getLeafNodes();
	newUnionNode = new Node(RelationalOperator.UNION);

	// Get the common attribute name of this Join
	joinCommonAttribute = databaseDictionaryService.getAttributesFromSqlData(
		joinNode.getSqlData()).get(0);
	joinCommonAttribute = joinCommonAttribute.substring(joinCommonAttribute.indexOf('.') + 1);

	for (Node leafNode : leafNodes) {
	    // Avoid duplicating work with branches that are not grouped by a
	    // Union node
	    ignoredNodes.addAll(ignoredLeafs);
	    ignoredLeafs.clear();

	    // Branch of the Join node containing the leaf node
	    currentBranchNode = joinNode.getNodeContainingLeafNode(databaseDictionaryService
		    .getRelationNames(leafNode.getSqlData()));

	    // Union node inside the current branch that contains
	    // the leaf node
	    currentBranchUnionNode = leafNode
		    .getClosestRelationalOperatorNode(RelationalOperator.UNION);

	    if (currentBranchUnionNode != null) {
		// Branch of the Union node that contains the leaf node
		currentBranchUnionBranchNode = currentBranchUnionNode
			.getNodeContainingLeafNode(databaseDictionaryService
				.getRelationNames(leafNode.getSqlData()));
	    } else {
		// There's no Union node, there's only one fragment for this
		// relation
		currentBranchUnionNode = leafNode;
		currentBranchUnionBranchNode = leafNode;

		// Avoid evaluating other leaf nodes that are part of this
		// branch of the join, since there's no Union here to make the
		// separation
		ignoredNodes.addAll(currentBranchNode.getLeafNodes());
	    }

	    // Since Join is commutative, avoid duplicating work
	    if (ignoredNodes.contains(currentBranchUnionBranchNode)) {
		continue;
	    }

	    // Look for leaf nodes to make Join with
	    for (Node joinLeafNode : leafNodes) {
		// Avoid joining a leaf node with itself
		if (joinLeafNode == leafNode) {
		    continue;
		}

		// Avoid joining leaf nodes that belong to the same branch of
		// the Join node
		joinBranchNode = joinNode.getNodeContainingLeafNode(databaseDictionaryService
			.getRelationNames(joinLeafNode.getSqlData()));
		if (joinBranchNode == currentBranchNode) {
		    continue;
		}

		// The branch that contains this leaf node has already been
		// evaluated
		if (ignoredLeafs.contains(joinLeafNode)) {
		    continue;
		}

		// This is a leaf that belongs to the opposite branch of the
		// Join node
		joinBranchUnionNode = joinLeafNode
			.getClosestRelationalOperatorNode(RelationalOperator.UNION);

		if (joinBranchUnionNode != null) {
		    // Union node inside the opposite branch that contains the
		    // leaf node
		    joinBranchUnionBranchNode = joinBranchUnionNode
			    .getNodeContainingLeafNode(databaseDictionaryService
				    .getRelationNames(joinLeafNode.getSqlData()));

		    ignoredLeafsBranchNode = joinBranchUnionNode
			    .getNodeContainingLeafNode(joinLeafNode.getSqlData());
		} else {
		    // There's no Union node, there's only one fragment for this
		    // relation
		    joinBranchUnionNode = joinLeafNode;
		    joinBranchUnionBranchNode = joinLeafNode;
		    ignoredLeafsBranchNode = joinNode.getNodeContainingLeafNode(joinLeafNode
			    .getSqlData());
		}

		// Ignore all the leafs that are part of the branch that
		// contains this leaf. If there's a Union node, it's the
		// union branch, otherwise, it's the joinNode branch
		ignoredLeafs.addAll(ignoredLeafsBranchNode.getLeafNodes());

		// Since Join is commutative, avoid duplicating work
		ignoredNodes.add(currentBranchUnionBranchNode);
		ignoredNodes.add(joinBranchUnionBranchNode);

		// Check if the qualifications of the joined fragments are
		// contradicting
		fragment = databaseDictionaryService.getRelation(leafNode.getSqlData());
		joinFragment = databaseDictionaryService.getRelation(joinLeafNode.getSqlData());

		fragmentPredicates = null;
		joinFragmentPredicates = null;
		if ((fragment != null) && (joinFragment != null)) {
		    // For horizontal fragments we can look for contradicting
		    // minterms
		    if ((fragment instanceof MintermDependentFragment)
			    && (joinFragment instanceof MintermDependentFragment)) {
			fragmentPredicates = ((MintermDependentFragment) fragment).getMinterm();
			joinFragmentPredicates = ((MintermDependentFragment) joinFragment)
				.getMinterm();
		    }

		    // Look for predicates that refer to the same attribute and
		    // check if they contradict
		    invalidMinterm = false;
		    if ((fragmentPredicates != null) && (joinFragmentPredicates != null)) {
			for (Predicate predicate : fragmentPredicates) {
			    attributeName = predicate.getAttribute().getName();
			    attributeName = attributeName.substring(attributeName.indexOf('.'));
			    for (Predicate joinPredicate : joinFragmentPredicates) {
				joinAttributeName = joinPredicate.getAttribute().getName();
				joinAttributeName = joinAttributeName.substring(joinAttributeName
					.indexOf('.'));
				if (joinAttributeName.equalsIgnoreCase(attributeName)) {
				    comparissonResult = predicate.getAttribute()
					    .getAttributeDomain().compareValues(
						    predicate.getValue(), joinPredicate.getValue());
				    invalidMinterm = predicate.getPredicateOperator()
					    .isInvalidComparisson(comparissonResult,
						    joinPredicate.getPredicateOperator());
				    if (invalidMinterm) {
					break;
				    }
				}
			    }

			    if (invalidMinterm) {
				break;
			    }
			}
		    }

		    if (!invalidMinterm) {
			// Add the new Join node
			newJoinNode = new Node(joinNode.getSqlData(), RelationalOperator.JOIN);

			// The new branch nodes of the new Join node should have
			// what's between the original Join node and the union
			// node
			if (currentBranchNode != currentBranchUnionNode) {
			    firstJoinBranchNode = currentBranchNode
				    .limitedClone(currentBranchUnionNode);
			    firstJoinBranchNode.getLeafNodes().get(0).addChild(
				    currentBranchUnionBranchNode.clone());
			} else {
			    firstJoinBranchNode = currentBranchUnionBranchNode.clone();
			}

			if (joinBranchNode != joinBranchUnionNode) {
			    secondJoinBranchNode = joinBranchNode.limitedClone(joinBranchUnionNode);
			    secondJoinBranchNode.getLeafNodes().get(0).addChild(
				    joinBranchUnionBranchNode.clone());
			} else {
			    secondJoinBranchNode = joinBranchUnionBranchNode.clone();
			}

			newJoinNode.addChild(firstJoinBranchNode);
			newJoinNode.addChild(secondJoinBranchNode);
			newUnionNode.addChild(newJoinNode);

			logger.warn("Reduction with selection, new Join: " + newJoinNode);
		    }
		}
	    }
	}

	// New Join nodes have been added
	if (newUnionNode.getChildren() != null) {
	    returnValue = true;
	    joinNode.getParent().addChild(newUnionNode);
	    joinNode.getParent().removeChild(joinNode);
	}

	return returnValue;
    }

    /**
     * Queries on vertical fragments can be reduced by determining the useless
     * intermediate relations and removing the subtrees that produce them.
     * Projections on a vertical fragment that has no attributes in common with
     * the projection attributes (except the key of the relation) produces
     * useless, though not empty relations.
     * 
     * @param currentNode
     *            Starting node for the analysis.
     */
    private boolean reduceVerticalFragmentation(final Node currentNode) {
	Node joinNode;
	Relation relation;
	boolean returnValue;
	List<Node> leafNodes;
	Node lastProjectionNode;
	boolean validProjection;
	String leafGlobalRelation;
	List<String> projectionAttributes;
	List<String> newProjectionAttributes;
	List<String> lastProjectionAttributes;
	List<String> pendingProjectionAttributes;

	// For each leaf node, check if its parent is a projection node. If
	// that's the case, check if the relation identified by the leaf node is
	// vertically fragmented and contains the projection attributes, if it
	// doesn't, remove the branch that contains this leaf node
	returnValue = false;
	leafNodes = currentNode.getLeafNodes();
	for (Node leafNode : leafNodes) {
	    relation = databaseDictionaryService.getRelation(leafNode.getSqlData());

	    if ((relation.getFragmentationType() == FragmentationType.Vertical)
		    && (leafNode.getParent() != null)
		    && (leafNode.getParent().getRelationalOperator() == RelationalOperator.PROJECTION)) {
		projectionAttributes = databaseDictionaryService.getAttributesFromSqlData(leafNode
			.getParent().getSqlData());

		// Add to the new projection attributes list only those
		// attributes that are valid for this relation
		if (relation != null) {
		    leafGlobalRelation = null;
		    newProjectionAttributes = new ArrayList<String>();
		    for (String attribute : projectionAttributes) {
			if (relation.containsAttribute(attribute)) {
			    newProjectionAttributes.add(attribute);
			    leafGlobalRelation = attribute.substring(0, attribute.indexOf('.'));
			}
		    }

		    // Check if the newProjectionAttributes are used by all the
		    // projection nodes that refer to this relation
		    joinNode = leafNode.getClosestRelationalOperatorNode(RelationalOperator.JOIN);
		    if (joinNode != null) {
			pendingProjectionAttributes = new ArrayList<String>(newProjectionAttributes);
			lastProjectionNode = joinNode
				.getClosestRelationalOperatorNode(RelationalOperator.PROJECTION);

			// If this projection uses attributes from the leaf
			// relation, the relation should contain all the
			// referenced attributes
			validProjection = true;
			while ((lastProjectionNode != null)
				&& (!pendingProjectionAttributes.isEmpty())) {
			    lastProjectionAttributes = databaseDictionaryService
				    .getAttributesFromSqlData(lastProjectionNode.getSqlData());

			    for (String projectionAttribute : lastProjectionAttributes) {
				if (projectionAttribute.startsWith(leafGlobalRelation + ".")) {
				    if (!pendingProjectionAttributes.contains(projectionAttribute)) {
					validProjection = false;
					break;
				    }
				}
			    }

			    // Check if we haven't make it to the root node,
			    // to avoid an infinite loop
			    if ((validProjection) && (lastProjectionNode.getParent() != null)) {
				lastProjectionNode = lastProjectionNode
					.getClosestRelationalOperatorNode(RelationalOperator.PROJECTION);
			    } else {
				break;
			    }
			}

			// If the projection generates an empty relation
			if (!validProjection) {
			    returnValue = true;
			    joinNode.removeChild(joinNode.getNodeContainingLeafNode(leafNode
				    .getSqlData()));

			    // If there's only one child left in the Join node,
			    // there's no need to keep this Join node
			    if (joinNode.getChildren().size() == 1) {
				if (joinNode.getParent() != null) {
				    joinNode.getParent().addChild(joinNode.getChildren().get(0));
				    joinNode.getParent().removeChild(joinNode);
				}
			    }
			}

			// If all the projection attributes are used, check if
			// there has been any change to the attributes list
			else if (newProjectionAttributes.size() != projectionAttributes.size()) {
			    returnValue = true;
			    leafNode.getParent().setSqlData(newProjectionAttributes);
			}
		    }
		}
	    }
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
}