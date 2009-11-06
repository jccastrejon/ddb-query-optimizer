package mx.itesm.ddb.util;

/**
 * Relational Algebra Operator.
 * 
 * @author jccastrejon
 * 
 */
public enum RelationalOperator {
    SELECT("\u03C3", "&#x3C3;"), JOIN("\u22C8", "&#x22C8;"), UNION("\u22C3", "&#x22C3;"), PROJECTION(
	    "\uu03A0", "&#x3A0;"), PRODUCT("\u00D7", "&#xD7;");

    /**
     * Operator's Unicode code.
     */
    private String unicodeCode;

    /**
     * Operator's Html code.
     */
    private String htmlCode;

    /**
     * Full constructor that specifies both the unicode and html codes.
     * 
     * @param unicodeCode
     *            Operator's Unicode code.
     * @param htmlCode
     *            Operator's Html code.
     */
    private RelationalOperator(final String unicodeCode, final String htmlCode) {
	this.unicodeCode = unicodeCode;
	this.htmlCode = htmlCode;
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
}
