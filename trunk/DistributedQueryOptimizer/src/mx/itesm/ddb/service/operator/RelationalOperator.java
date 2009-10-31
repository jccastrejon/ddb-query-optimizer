package mx.itesm.ddb.service.operator;

/**
 * Relational Algebra Operator.
 * 
 * @author jccastrejon
 * 
 */
public enum RelationalOperator {
    SELECT("\u03C3"), JOIN("\u22C8"), UNION("\u22C3"), PROJECTION("\uu03A0"), PRODUCT("\u00D7");

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
    private RelationalOperator(String description) {
	this.description = description;
    }

    @Override
    public String toString() {
	return " " + this.description + " ";
    }
}
