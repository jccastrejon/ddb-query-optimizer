package mx.itesm.ddb.service.operator;

/**
 * Operator Tree.
 * 
 * @author jccastrejon
 * 
 */
public class OperatorTree {

    /**
     * Operator Tree's Root Node.
     */
    Node rootNode;

    /**
     * Full constructor.
     * 
     * @param root
     *            Root Node.
     */
    public OperatorTree(final Node rootNode) {
	this.rootNode = rootNode;
    }

    /**
     * @return the rootNode
     */
    public Node getRootNode() {
	return rootNode;
    }

    /**
     * @param rootNode
     *            the rootNode to set
     */
    public void setRootNode(Node rootNode) {
	this.rootNode = rootNode;
    }

    @Override
    public String toString() {
	return rootNode.toString();
    }
}
