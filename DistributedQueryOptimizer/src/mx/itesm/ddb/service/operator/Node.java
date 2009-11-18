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
     * Checks if this a leaf node identified by the specified SQL Data, or if
     * it's contained in one of its children.
     * 
     * @param sqlData
     *            Leaf Node's SQL Data.
     * @return <em>true</em> if the leaf node is defined within this node
     *         hierarchy, <em>false</em> otherwise.
     */
    public boolean containsLeafNode(final String sqlData) {
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
    public List<Node> getLeafNodes(final String sqlData) {
	List<Node> returnValue;

	// Check if the data is in this node
	returnValue = new ArrayList<Node>();
	if (this.children == null) {
	    if (this.sqlData.toLowerCase().equals(sqlData.toLowerCase())) {
		returnValue.add(this);
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

	newSqlData = new String(this.sqlData);

	if (this.children != null) {
	    for (Node node : this.children) {
		newNode = node.clone();
		returnValue.addChild(newNode);
	    }
	}

	returnValue.setSqlData(newSqlData);
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
