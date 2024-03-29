package mx.itesm.ddb.service.operator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mx.itesm.ddb.util.RelationalOperator;
import mx.itesm.ddb.util.SqlData;

/**
 * A node in the operator tree.
 * 
 * @author jccastrejon
 * 
 */
public class Node implements Cloneable {

    /**
     * Node Id.
     */
    private String id;

    /**
     * Node data.
     */
    private String sqlData;

    /**
     * Relational Operator.
     */
    private RelationalOperator relationalOperator;

    /**
     * Parent node.
     */
    private Node parent;

    /**
     * Children nodes.
     */
    private List<Node> children;

    /**
     * Initialize only with data.
     * 
     * @param sqlData
     *            Node data.
     */
    public Node(final String sqlData) {
	this.sqlData = sqlData;
	this.id = this.generateId();
    }

    /**
     * Initialize only with data.
     * 
     * @param sqlData
     *            Node data.
     */
    public Node(final SqlData sqlData) {
	this(sqlData.toString());
    }

    /**
     * Initialize only with relational operator.
     * 
     * @param relationalOperator
     *            Relational Operator.
     */
    public Node(final RelationalOperator relationalOperator) {
	this.relationalOperator = relationalOperator;
	this.id = this.generateId();
    }

    /**
     * Initialize with data and relational operator.
     * 
     * @param sqlData
     *            Node data.
     * @param relationalOperator
     *            Relational Operator.
     */
    public Node(final String sqlData, final RelationalOperator relationalOperator) {
	this(sqlData);
	this.relationalOperator = relationalOperator;
	this.parent = null;
	this.children = new ArrayList<Node>();
    }

    /**
     * Initialize with data and relational operator.
     * 
     * @param sqlData
     *            Node data.
     * @param relationalOperator
     *            Relational Operator.
     */
    public Node(final SqlData sqlData, final RelationalOperator relationalOperator) {
	this(sqlData.toString(), relationalOperator);
    }

    /**
     * Full constructor.
     * 
     * @param sqlData
     *            Node data.
     * @param relationalOperator
     *            Relational Operator.
     * @param parent
     *            Parent Node.
     */
    public Node(final SqlData sqlData, final RelationalOperator relationalOperator,
	    final Node parent) {
	this(sqlData, relationalOperator);
	this.parent = parent;
    }

    /**
     * Initialize with a collection of data and relational operator.
     * 
     * @param sqlData
     *            Node data.
     * @param relationalOperator
     *            Relational Operator.
     */
    public Node(final SqlData[] sqlData, final RelationalOperator relationalOperator) {
	this(relationalOperator);
	StringBuilder newSqlData;

	newSqlData = new StringBuilder();
	for (SqlData data : sqlData) {
	    newSqlData.append(data.toString()).append(" ");
	}

	this.parent = null;
	this.children = new ArrayList<Node>();
	this.sqlData = newSqlData.toString();
    }

    /**
     * Initialize with a collection of data and relational operator.
     * 
     * @param sqlData
     *            Node data.
     * @param relationalOperator
     *            Relational Operator.
     */
    public Node(final String[] sqlData, final RelationalOperator relationalOperator) {
	this(relationalOperator);
	this.parent = null;
	this.children = new ArrayList<Node>();
	this.setSqlData(sqlData);
    }

    /**
     * Full constructor with a collection of data.
     * 
     * @param sqlData
     *            Node data.
     * @param relationalOperator
     *            Relational Operator.
     * @param parent
     *            Parent Node.
     */
    public Node(final SqlData[] sqlData, final RelationalOperator relationalOperator,
	    final Node parent) {
	this(sqlData, relationalOperator);
	this.parent = parent;
    }

    /**
     * Add a node to the children nodes.
     * 
     * @param node
     *            Child Node.
     */
    public void addChild(final Node node) {
	if (this.children == null) {
	    this.children = new ArrayList<Node>();
	}

	if (node != null) {
	    node.setParent(this);
	    this.children.add(node);
	}
    }

    /**
     * Add a collection of nodes to the children nodes.
     * 
     * @param children
     *            Children Nodes.
     */
    public void addChildren(final List<Node> children) {
	if (this.children == null) {
	    this.children = new ArrayList<Node>();
	}

	if (children != null) {
	    for (Node node : children) {
		node.setParent(this);
	    }

	    this.children.addAll(children);
	}
    }

    /**
     * Remove a node from the children nodes.
     * 
     * @param node
     *            Child node.
     */
    public void removeChild(final Node node) {
	if ((this.children != null) && (node != null)) {
	    this.children.remove(node);

	    if (node.getParent() == this) {
		node.setParent(null);
	    }
	}
    }

    /**
     * Remove a collection of nodes from the children nodes.
     * 
     * @param children
     *            Children Nodes.
     */
    public void removeChildren(final List<Node> children) {
	if ((this.children != null) && (children != null)) {
	    for (Node child : children) {
		this.removeChild(child);
	    }
	}
    }

    /**
     * Remove all the children nodes from this node.
     */
    public void removeAllChildren() {
	if (this.children != null) {
	    for (Node child : this.children) {
		child.setParent(null);
	    }

	    this.children = null;
	}
    }

    /**
     * Check if in down in this Node's hierarchy there's a Node with the
     * specified Relational Operator.
     * 
     * @param relationalOperator
     *            Relational Operator to look for.
     * @return <em>true</em> if a Node exists down in this Node's hierarchy with
     *         the specified Relational Operator.
     */
    public boolean containsRelationalOperatorNode(final RelationalOperator relationalOperator) {
	boolean returnValue;

	returnValue = false;
	if (this.getRelationalOperatorNode(relationalOperator) != null) {
	    returnValue = true;
	}

	return returnValue;
    }

    /**
     * Get the closest Node down in this Node's hierarchy identified by the
     * specified relational operator.
     * 
     * @param relationalOperator
     *            Relational Operator to look for.
     * @return The closest Node down in the hierarchy identified by the
     *         specified relational operator.
     */
    public Node getRelationalOperatorNode(final RelationalOperator relationalOperator) {
	Node returnValue;

	returnValue = null;
	if (this.relationalOperator == relationalOperator) {
	    returnValue = this;
	}

	else if (this.children != null) {
	    for (Node child : this.children) {
		returnValue = child.getRelationalOperatorNode(relationalOperator);
		if (returnValue != null) {
		    break;
		}
	    }
	}

	return returnValue;
    }

    /**
     * Checks if this a leaf node identified by the specified SQL Data, or if
     * it's contained in one of its children.
     * 
     * @param sqlData
     *            Leaf Node's SQL Data.
     * @return <em>true</em> if the leaf node is defined within this node
     *         hierarchy, <em>false</em> otherwise.
     */
    public boolean containsLeafNode(final String... sqlData) {
	boolean returnValue;

	returnValue = false;
	if (!this.getLeafNodes(sqlData).isEmpty()) {
	    returnValue = true;
	}

	return returnValue;
    }

    /**
     * Look for the leaf nodes identified by the specified SQL Data in this
     * Node's hierarchy.
     * 
     * @param sqlData
     *            Leaf Node's SQL Data.
     * @return List containing the leaf Nodes if they're indeed in this Node's
     *         hierarchy. If no matching Node is found, an empty List is
     *         returned.
     */
    public List<Node> getLeafNodes(final String... sqlData) {
	List<Node> returnValue;

	// Check if the data is in this node
	returnValue = new ArrayList<Node>();
	if (this.children == null) {
	    if (sqlData != null) {
		for (String relationName : sqlData) {
		    if (this.sqlData.toLowerCase().equalsIgnoreCase(relationName)) {
			returnValue.add(this);
		    }
		}
	    }
	}

	// Check if the data is in one of my children
	else {
	    for (Node child : this.children) {
		returnValue.addAll(child.getLeafNodes(sqlData));
	    }
	}

	return returnValue;
    }

    /**
     * Get all the leaf nodes associated to this node.
     * 
     * @return List containing the leaf nodes associated to this node.
     */
    public List<Node> getLeafNodes() {
	List<Node> returnValue;

	// Check if this is a leafNode
	returnValue = new ArrayList<Node>();
	if (this.children == null) {
	    returnValue.add(this);
	}

	// If not, get the node's leafNodes
	else {
	    for (Node child : this.children) {
		returnValue.addAll(child.getLeafNodes());
	    }
	}

	return returnValue;
    }

    /**
     * Get the node whose branch contains the leaf node identified by the
     * specified SQL Data.
     * 
     * @param invalidNodes
     *            List of nodes that though probably containa node identified by
     *            the sqlData, are considered invalid for return value. If one
     *            of these is found, we should keep looking for another one.
     * @param sqlData
     *            Leaf Node's SQL Data.
     * @return If this node has children, we return the child whose branch
     *         contains the specified leaf node. If this node has no children,
     *         we return this node if the leaf node exists in its hierarchy. If
     *         no leaf node is identified the given SQL Data, we return
     *         <em>null</em>.
     */
    public Node getNodeContainingLeafNode(final Collection<Node> invalidNodes,
	    final String... sqlData) {
	Node returnValue;
	boolean validValue;

	returnValue = null;
	if (this.getChildren() != null) {
	    for (Node child : children) {
		if (child.containsLeafNode(sqlData)) {
		    validValue = true;
		    if ((invalidNodes != null) && (invalidNodes.contains(child))) {
			validValue = false;
		    }

		    if (validValue) {
			returnValue = child;
			break;
		    }
		}
	    }
	} else {
	    if (this.containsLeafNode(sqlData)) {
		validValue = true;
		if ((invalidNodes != null) && (invalidNodes.contains(this))) {
		    validValue = false;
		}

		if (validValue) {
		    return this;
		}
	    }
	}

	return returnValue;
    }

    /**
     * Get the attributes of the given relation that are used starting from this
     * Node up to the specified Root Node.
     * 
     * @param sqlData
     *            Relation's SqlData.
     * @return List of attributes used in this Node's hierarchy up to the
     *         specified Root Node.
     */
    public Set<String> getRelationAttributes(final String sqlData, final Node rootNode) {
	String[] dataElements;
	Set<String> returnValue;

	returnValue = new HashSet<String>();
	dataElements = this.getSqlDataElements();
	if (dataElements != null) {
	    for (String attribute : dataElements) {
		attribute = attribute.toLowerCase().replace('(', ' ').replace(')', ' ').trim();
		if (attribute.startsWith(sqlData.toLowerCase() + ".")) {
		    returnValue.add(attribute);
		}
	    }
	}

	if ((this != rootNode) && (this.parent != null)) {
	    returnValue.addAll(this.parent.getRelationAttributes(sqlData, rootNode));
	}

	return returnValue;
    }

    /**
     * Get the closes Node up in this Node's hierarchy that contains the
     * specified Relational Operator.
     * 
     * @param relationalOperator
     *            Relational Operator.
     * @param limitNode
     *            Limit node up in the hierarchy.
     * @return The closest Node containing the specified Relational Operator or
     *         <em>null</em> if no Node is found.
     */
    public Node getClosestRelationalOperatorNode(final RelationalOperator relationalOperator,
	    final Node limitNode) {
	Node returnValue;
	boolean checkLimit;
	boolean continueSearch;

	// Whether or not we have a limit node for the search
	checkLimit = false;
	continueSearch = true;
	if (limitNode != null) {
	    checkLimit = true;
	}

	returnValue = null;
	if (this.getParent() != null) {
	    returnValue = this.getParent();
	    // We've reached the limit without finding the node
	    if (checkLimit) {
		if (returnValue == limitNode) {
		    continueSearch = false;
		}
	    }

	    if (continueSearch) {
		while ((returnValue != null)
			&& (returnValue.getRelationalOperator() != relationalOperator)) {
		    returnValue = returnValue.getParent();
		    // We've reached the limit without finding the node
		    if (checkLimit) {
			if (returnValue == limitNode) {
			    returnValue = null;
			    break;
			}
		    }
		}
	    }
	}

	// Make sure that the return value does contain the Relational Operator,
	// avoiding the case where we reached the root node without finding a
	// matching Node
	if ((returnValue != null) && (returnValue.getRelationalOperator() != relationalOperator)) {
	    returnValue = null;
	}

	return returnValue;
    }

    /**
     * Get the Node's description, containing it's operator and data.
     * 
     * @return Node's description.
     */
    public String getDescription() {
	String sqlData;
	StringBuilder returnValue;

	returnValue = new StringBuilder();
	if (this.relationalOperator != null) {
	    returnValue.append(this.relationalOperator);
	}

	if (this.sqlData != null) {
	    sqlData = this.sqlData;
	    if ((this.relationalOperator != null) && (this.relationalOperator.isCompoundOperator())) {
		sqlData = sqlData.trim().replace(" ", ", ");
	    }

	    returnValue.append(sqlData);
	}

	return returnValue.toString();
    }

    /**
     * Get the Node's SqlData elements.
     * 
     * @return Array containing SqlData elements.
     */
    public String[] getSqlDataElements() {
	String[] returnValue;

	returnValue = null;
	if (this.sqlData != null) {
	    returnValue = this.sqlData.trim().split(" ");
	}

	return returnValue;
    }

    @Override
    public String toString() {
	StringBuilder returnValue;

	// Node Id and label
	returnValue = new StringBuilder("\n\"" + this.getId() + "\" [label=\""
		+ this.getDescription() + "\"]");

	// Children Nodes
	returnValue.append("\n\"" + this.getId() + "\" -> ");
	if (this.children != null) {
	    // Links to children nodes
	    returnValue.append("{ ");
	    for (Node child : this.children) {
		returnValue.append("\"" + child.getId() + "\";");
	    }
	    returnValue.append(" }");

	    // Children nodes definitions
	    for (Node child : this.children) {
		returnValue.append(child.toString());
	    }
	} else {
	    // Leaf Node
	    returnValue.append("{};\n");
	}

	return returnValue.toString();
    }

    @Override
    public Node clone() {
	Node newNode;
	String newSqlData;
	Node returnValue;

	newSqlData = null;
	returnValue = new Node(this.relationalOperator);

	// Clone children
	if (this.children != null) {
	    for (Node node : this.children) {
		newNode = node.clone();
		returnValue.addChild(newNode);
	    }
	}

	// Clone node data
	if (this.sqlData != null) {
	    newSqlData = new String(this.sqlData);
	    returnValue.setSqlData(newSqlData);
	}

	return returnValue;
    }

    /**
     * Clone this node and its hierarchy until the limitNode is found in the
     * hierarchy.
     * 
     * @param limitNode
     *            Limit Node.
     * @return Node that contains this node's hierarchy until the limitNode. If
     *         no limitNode is found, the complete node's hierarchy is returned.
     */
    public Node limitedClone(final Node limitNode) {
	Node newNode;
	String newSqlData;
	Node returnValue;

	newSqlData = null;
	returnValue = new Node(this.relationalOperator);

	// Clone children
	if (this.children != null) {
	    if (!this.children.contains(limitNode)) {
		for (Node node : this.children) {
		    newNode = node.limitedClone(limitNode);
		    returnValue.addChild(newNode);
		}
	    }
	}

	// Clone node data
	if (this.sqlData != null) {
	    newSqlData = new String(this.sqlData);
	    returnValue.setSqlData(newSqlData);
	}

	return returnValue;
    }

    /**
     * Generate an Id for this node with the current time and a random double.
     * 
     * @return Id.
     */
    private String generateId() {
	return System.currentTimeMillis() + "-" + Math.random();
    }

    /**
     * @return the sqlData
     */
    public String getSqlData() {
	return sqlData;
    }

    /**
     * @param sqlData
     *            the sqlData to set
     */
    public void setSqlData(String sqlData) {
	this.sqlData = sqlData;
    }

    /**
     * @param sqlData
     *            the sqlData to set
     */
    public void setSqlData(final String[] sqlData) {
	StringBuilder newSqlData;

	newSqlData = new StringBuilder();
	for (String data : sqlData) {
	    newSqlData.append(data).append(" ");
	}

	this.sqlData = newSqlData.toString();
    }

    /**
     * @param sqlData
     *            the sqlData to set
     */
    public void setSqlData(final Collection<String> sqlData) {
	StringBuilder newSqlData;

	newSqlData = new StringBuilder();
	for (String data : sqlData) {
	    newSqlData.append(data).append(" ");
	}

	this.sqlData = newSqlData.toString();
    }

    /**
     * @return the relationalOperator
     */
    public RelationalOperator getRelationalOperator() {
	return relationalOperator;
    }

    /**
     * @param relationalOperator
     *            the relationalOperator to set
     */
    public void setRelationalOperator(RelationalOperator relationalOperator) {
	this.relationalOperator = relationalOperator;
    }

    /**
     * @return the parent
     */
    public Node getParent() {
	return parent;
    }

    /**
     * @param parent
     *            the parent to set
     */
    public void setParent(Node parent) {
	this.parent = parent;
    }

    /**
     * @return the children
     */
    public List<Node> getChildren() {
	return children;
    }

    /**
     * @param children
     *            the children to set
     */
    public void setChildren(List<Node> children) {
	this.children = children;
    }

    /**
     * @return the id
     */
    public String getId() {
	return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id) {
	this.id = id;
    }
}
