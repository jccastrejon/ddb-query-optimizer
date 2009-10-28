package mx.itesm.ddb.service;

import java.io.StringReader;

import mx.itesm.ddb.parser.SqlParser;
import mx.itesm.ddb.util.QueryData;

import org.apache.log4j.Logger;

/**
 * Manager that allows execution of service methods over the Query object.
 * 
 * @author jccastrejon
 * 
 */
public class OptimizerManager {

    /**
     * Class Logger.
     */
    private static Logger logger = Logger.getLogger(OptimizerManager.class.getName());

    /**
     * Get the Query Object containing the Relational Algebra representation of
     * the SQL query.
     * 
     * @param query
     *            SQL query.
     * @return Query object containg the SQL and Relational Algebra
     *         representation of the original SQL query.
     */
    public Query createQuery(final String query) {
	Query returnValue;

	returnValue = new Query(query);
	this.updateRelationalAlgebra(returnValue);

	return returnValue;
    }

    /**
     * Get the Relational Algebra representation of the SQL query.
     * 
     * @param query
     *            Query object containing the SQL query.
     */
    public void updateRelationalAlgebra(final Query query) {
	QueryData queryData;
	String returnValue;
	SqlParser parser;

	try {
	    parser = new SqlParser(new StringReader(query.getSql()));
	    queryData = parser.QueryStatement();
	    returnValue = queryData.toString();
	} catch (Exception e) {
	    returnValue = "Problems while parsing query [" + query + "]";
	    logger.error("Problems while parsing query [" + query + "]", e);
	}

	query.setRelationalAlgebra(returnValue);
    }
}
