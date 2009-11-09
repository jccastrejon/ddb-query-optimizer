package mx.itesm.ddb.service.operator;

/**
 * Operator Tree.
 * 
 * @author jccastrejon
 * 
 */
public class OperatorTree implements Cloneable {

    /**
     * Number of rewriting steps that were needed to generate this Operator
     * Tree.
     */
    private int rewritingSteps;

    /**
     * Operator Tree's Root Node.
     */
    private Node rootNode;

    /**
     * Full constructor.
     * 
     * @param root
     *            Root Node.
     */
    public OperatorTree(final Node rootNode) {
	this.rootNode = rootNode;
    }

    @Override
    public OperatorTree clone() {
	return new OperatorTree(this.rootNode.clone());
    }

    @Override
    public String toString() {
	return rootNode.toString();
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

    /**
     * @return the rewritingSteps
     */
    public int getRewritingSteps() {
	return rewritingSteps;
    }

    /**
     * @param rewritingSteps
     *            the rewritingSteps to set
     */
    public void setRewritingSteps(int rewritingSteps) {
	this.rewritingSteps = rewritingSteps;
    }
}
