package mx.itesm.ddb.service;

/**
 * @author jccastrejon
 * 
 */
public class DatabaseDictionaryService {

    /**
     * Verify the referenced table used by this expression.
     * 
     * @param expression
     *            SQL expression.
     * @return Table name or null if the expression doesn't make any reference
     *         to a table.
     */
    public String getTableFromExpression(final String expression) {
	String returnValue;

	// TODO: Connect to the real Dictionary:.
	returnValue = null;
	if (expression.indexOf('.') > 0) {
	    returnValue = expression.substring(0, expression.indexOf('.'));
	}

	return returnValue;
    }
}
