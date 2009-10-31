package mx.itesm.ddb.service.operator;

import java.util.ArrayList;
import java.util.List;

import mx.itesm.ddb.util.SqlData;

/**
 * A node in the operator tree.
 * 
 * @author jccastrejon
 * 
 */
public class Node {

    /**
     * Node data.
     */
    SqlData[] sqlData;

    /**
     * Relational Operator.
     */
    RelationalOperator relationalOperator;

    /**
     * Parent node.
     */
    Node parent;

    /**
     * Children nodes.
     */
    List<Node> children;

    /**
     * Initialize only with data.
     * 
     * @param sqlData
     *            Node data.
     */
    public Node(final SqlData sqlData) {
	this.sqlData = new SqlData[] { sqlData };
    }

    /**
     * Initialize only with relational operator.
     * 
     * @param relationalOperator
     *            Relational Operator.
     */
    public Node(final RelationalOperator relationalOperator) {
	this.relationalOperator = relationalOperator;
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
	this.sqlData = sqlData;
	this.relationalOperator = relationalOperator;
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
     * Get the Node's description, containing it's operator and data.
     * 
     * @return Node's description.
     */
    public String getDescription() {
	StringBuilder returnValue;

	returnValue = new StringBuilder("\"");
	if (this.relationalOperator != null) {
	    returnValue.append(this.relationalOperator);
	}

	if (this.sqlData != null) {
	    for (SqlData sqlData : this.sqlData) {
		returnValue.append(" " + sqlData + " ");
	    }
	}

	returnValue.append("\"");

	return returnValue.toString();
    }

    @Override
    public String toString() {
	StringBuilder returnValue;

	returnValue = new StringBuilder("\n" + this.getDescription() + " -> ");

	if (this.children != null) {
	    // Links to children
	    returnValue.append("{ ");
	    for (Node child : this.children) {
		returnValue.append(child.getDescription() + ";");
	    }
	    returnValue.append(" }");

	    // Children definitions
	    for (Node child : this.children) {
		returnValue.append(child.toString());
	    }
	} else {
	    returnValue.append("{};\n");
	}

	return returnValue.toString();
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
}
