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
     * Verify if the comparissonResult between two values is valid according to
     * this and the specified operator.
     * 
     * @param comparissonResult
     *            Comparisson Result.
     * @param operator
     *            Predicate operator to compare with.
     * @return <em>true</em> if the comparissonValue means that the operators
     *         won't apply for the values compared, <em>false</em> otherwise.
     */
    public boolean isInvalidComparisson(final int comparissonResult,
	    final PredicateOperator operator) {
	boolean returnValue;

	returnValue = false;
	switch (operator) {
	case LESS_THAN:
	    if (comparissonResult >= 0) {
		returnValue = true;
	    }
	    break;
	case LESS_THAN_OR_EQUALS:
	    if (comparissonResult > 0) {
		returnValue = true;
	    }
	    break;
	case GREATER_THAN:
	    if (comparissonResult <= 0) {
		returnValue = true;
	    }
	    break;
	case GREATER_THAN_OR_EQUALS:
	    if (comparissonResult < 0) {
		returnValue = true;
	    }
	    break;
	case EQUALS_TO:
	    if (comparissonResult != 0) {
		returnValue = true;
	    }
	    break;
	case DIFFERENT_THAN:
	    if (comparissonResult == 0) {
		returnValue = true;
	    }
	    break;
	}

	return returnValue;
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
