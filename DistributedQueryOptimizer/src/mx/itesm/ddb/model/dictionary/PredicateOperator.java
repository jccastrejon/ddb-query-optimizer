package mx.itesm.ddb.model.dictionary;

/**
 * Operator used to define simple predicates.
 * 
 * @author jccastrejon
 * 
 */
public enum PredicateOperator {
    EQUALS_TO("="), DIFFERENT_THAN("!="), GREATER_THAN(">"), LESS_THAN("<"), GREATER_THAN_OR_EQUALS(
	    ">="), LESS_THAN_OR_EQUALS("<=");

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
    private PredicateOperator(String description) {
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
