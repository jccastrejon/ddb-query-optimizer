package mx.itesm.ddb.util;

/**
 * Operator used within a SQL WHERE condition.
 * 
 * @author jccastrejon
 * 
 */
public interface ConditionOperator {

    /**
     * 
     * @author jccastrejon
     * 
     */
    public enum UnaryOperator implements ConditionOperator {
	NOT_OPERATOR("NOT"), EXISTS_OPERATOR("EXISTS"), IS_NULL_OPERATOR("IS NULL"), IS_NOT_NULL_OPERATOR(
		"IS NOT NULL"), ALL_OPERATOR("ALL"), ANY_OPERATOR("ANY");

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
	private UnaryOperator(String description) {
	    this.description = description;
	}

	@Override
	public String toString() {
	    return " " + this.description + " ";
	}
    };

    /**
     * 
     * @author jccastrejon
     * 
     */
    public enum BinaryOperator implements ConditionOperator {
	AND_OPERATOR("AND"), OR_OPERATOR("OR"), IN_OPERATOR("IN"), COMMA_OPERATOR(","), BETWEEN_OPERATOR(
		"BETWEEN"), LIKE_OPERATOR("LIKE");

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
	private BinaryOperator(String description) {
	    this.description = description;
	}

	@Override
	public String toString() {
	    return " " + this.description + " ";
	}
    }
}
