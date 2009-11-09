package mx.itesm.ddb.service.operator;

import java.util.ArrayList;
import java.util.List;

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
    private SqlData[] sqlData;

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
    public Node(final SqlData sqlData) {
	this.sqlData = new SqlData[] { sqlData };
	this.id = this.generateId();
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
    public Node(final SqlData sqlData, final RelationalOperator relationalOperator) {
	this(sqlData);
	this.relationalOperator = relationalOperator;
	this.parent = null;
	this.children = new ArrayList<Node>();
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
    public Node(SqlData sqlData, RelationalOperator relationalOperator, Node parent) {
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
    public Node(SqlData[] sqlData, RelationalOperator relationalOperator) {
	this(relationalOperator);
	this.sqlData = sqlData;
	this.parent = null;
	this.children = new ArrayList<Node>();
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
    public Node(SqlData[] sqlData, RelationalOperator relationalOperator, Node parent) {
	this(sqlData, relationalOperator);
	this.parent = parent;
    }

    /**
     * Add a node to the children nodes.
     * 
     * @param node
     *            Child Node.
     */
    public void addChild(Node node) {
	if (this.children == null) {
	    this.children = new ArrayList<Node>();
	}

	node.setParent(this);
	this.children.add(node);
    }

    /**
     * Add a collection of nodes to the children nodes.
     * 
     * @param children
     *            Children Nodes.
     */
    public void addChildren(List<Node> children) {
	if (this.children == null) {
	    this.children = new ArrayList<Node>();
	}

	for (Node node : children) {
	    node.setParent(this);
	}

	this.children.addAll(children);
    }

    /**
     * Remove a node from the children nodes.
     * 
     * @param node
     *            Child Node.
     */
    public void removeChild(Node node) {
	this.children.remove(node);
    }

    /**
     * Remove a collection of nodes from the children nodes.
     * 
     * @param children
     *            Children Nodes.
     */
    public void removeChildren(List<Node> children) {
	this.children.removeAll(children);
    }

    /**
     * Checks if this a leaf node identified by the specified SQL Data, or if
     * it's contained in one of its children.
     * 
     * @param id
     *            Leaf Node's SQL Data.
     * @return <em>true</em> if the leaf node is defined within this node
     *         hierarchy.
     */
    public boolean containsLeafNode(final String sqlData) {
	boolean returnValue;

	// Check if the data is in this node
	returnValue = false;
	if (this.children == null) {
	    for (SqlData data : this.sqlData) {
		if (data.toString().toLowerCase().equals(sqlData.toLowerCase())) {
		    returnValue = true;
		    break;
		}
	    }
	}

	// Check if the data is in one of my children
	else {
	    for (Node child : this.children) {
		if (child.containsLeafNode(sqlData)) {
		    returnValue = true;
		    break;
		}
	    }
	}

	return returnValue;
    }

    /**
     * Get the Node's description, containing it's operator and data.
     * 
     * @return Node's description.
     */
    public String getDescription() {
	StringBuilder returnValue;

	returnValue = new StringBuilder();
	if (this.relationalOperator != null) {
	    returnValue.append(this.relationalOperator);
	}

	returnValue.append(this.getSqlDataDescription());
	return returnValue.toString();
    }

    /**
     * Get the Node's SqlData description.
     * 
     * @return SqlData description.
     */
    public String getSqlDataDescription() {
	StringBuilder returnValue;

	returnValue = new StringBuilder();
	if (this.sqlData != null) {
	    for (SqlData sqlData : this.sqlData) {
		returnValue.append(" " + sqlData + " ");
	    }
	}

	return returnValue.toString();
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
	List<Node> newChildren;
	SqlData[] newSqlData;
	Node returnValue;

	newSqlData = null;
	newChildren = null;
	returnValue = new Node(this.relationalOperator);

	if (this.sqlData != null) {
	    newSqlData = new SqlData[this.sqlData.length];
	    for (int i = 0; i < this.sqlData.length; i++) {
		newSqlData[i] = this.sqlData[i].clone();
	    }
	}

	if (this.children != null) {
	    newChildren = new ArrayList<Node>(this.children.size());
	    for (Node node : this.children) {
		newNode = node.clone();
		newNode.setParent(returnValue);
		newChildren.add(node.clone());
	    }
	}

	returnValue.setChildren(newChildren);
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
    public SqlData[] getSqlData() {
	return sqlData;
    }

    /**
     * @param sqlData
     *            the sqlData to set
     */
    public void setSqlData(SqlData[] sqlData) {
	this.sqlData = sqlData;
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
