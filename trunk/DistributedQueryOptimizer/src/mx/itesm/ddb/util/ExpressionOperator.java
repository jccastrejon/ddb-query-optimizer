package mx.itesm.ddb.util;

/**
 * Operator used within a SQL expression.
 * 
 * @author jccastrejon
 * 
 */
public interface ExpressionOperator {

    /**
     * 
     * @author jccastrejon
     * 
     */
    public enum ArithmeticOperator implements ExpressionOperator {
	ADD_OPERATOR("+"), SUBSTRACT_OPERATOR("-"), MULTIPLY_OPERATOR("*"), DIVIDE_OPERATOR("/"), EQUALS_OPERATOR(
		"=");

	/**
	 * Operator description.
	 */
	private String description;

	/**
	 * Full constructor that specifies the operator description.
	 * 
	 * @param description
	 *            Operator description.
	 */
	private ArithmeticOperator(String description) {
	    this.description = description;
	}

	@Override
	public String toString() {
	    return " " + this.description + " ";
	}
    }
}