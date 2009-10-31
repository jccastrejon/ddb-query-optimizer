package mx.itesm.ddb.service;

/**
 * @author jccastrejon
 * 
 */
public class DatabaseDictionaryService {

    /**
     * 
     * @return
     */
    public String getTableFromExpression(String expression) {
	String returnValue;

	// TODO: Connect to the real Dictionary:.
	if (expression.indexOf('.') > 0) {
	    returnValue = expression.substring(0, expression.indexOf('.'));
	} else {
	    returnValue = expression;
	}

	return returnValue;
    }
}
