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
	ADD_OPERATOR("+"), SUBSTRACT_OPERATOR("-"), MULTIPLY_OPERATOR("*"), DIVIDE_OPERATOR("/"), EQUALS_TO_OPERATOR(
		"="), DIFFERENT_THAN_OPERATOR("!="), GREATER_THAN_OPERATOR(">"), LESS_THAN_OPERATOR(
		"<"), GREATER_THAN_OR_EQUALS_OPERATOR(">="), LESS_THAN_OR_EQUALS_OPERATOR("<=");

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

	/**
	 * Get the Operator's description.
	 * 
	 * @return Operator's description.
	 */
	public String getDescription() {
	    return this.description;
	}

	@Override
	public String toString() {
	    return " " + this.description + " ";
	}
    }
}