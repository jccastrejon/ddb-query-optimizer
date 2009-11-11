package mx.itesm.ddb.util;

/**
 * Relational Algebra Operator.
 * 
 * @author jccastrejon
 * 
 */
public enum RelationalOperator {
    SELECT("\u03C3", "&#x3C3;", false), JOIN("\u22C8", "&#x22C8;", false), UNION("\u22C3",
	    "&#x22C3;", false), PROJECTION("\uu03A0", "&#x3A0;", true), PRODUCT("\u00D7", "&#xD7;",
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
