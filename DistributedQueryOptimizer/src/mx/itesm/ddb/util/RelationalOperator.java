package mx.itesm.ddb.util;

/**
 * Relational Algebra Operator.
 * 
 * @author jccastrejon
 * 
 */
public enum RelationalOperator {
    SELECT("σ", "&#x3C3;", false), JOIN("⋈", "&#x22C8;", false), UNION("⋃",
	    "&#x22C3;", false), PROJECTION("Π", "&#x3A0;", true), PRODUCT("×", "&#xD7;",
	    false);

    /**
     * Operator's Unicode code.
     */
    private String unicodeCode;

    /**
     * Operator's Html code.
     */
    private String htmlCode;

    /**
     * Whether or not this operator can have multiple expressions in its
     * definition.
     */
    private boolean compoundOperator;

    /**
     * Full constructor that specifies both the unicode and html codes.
     * 
     * @param unicodeCode
     *            Operator's Unicode code.
     * @param htmlCode
     *            Operator's Html code.
     * @param compoundOperator
     *            Whether or not this operator can have multiple expressions in
     *            its definition.
     */
    private RelationalOperator(final String unicodeCode, final String htmlCode,
	    final boolean compoundOperator) {
	this.unicodeCode = unicodeCode;
	this.htmlCode = htmlCode;
	this.compoundOperator = compoundOperator;
    }

    @Override
    public String toString() {
	return " " + this.unicodeCode + " ";
    }

    /**
     * @return the unicodeCode
     */
    public String getUnicodeCode() {
	return unicodeCode;
    }

    /**
     * @return the htmlCode
     */
    public String getHtmlCode() {
	return htmlCode;
    }

    /**
     * @return the compoundOperator
     */
    public boolean isCompoundOperator() {
	return compoundOperator;
    }
}
