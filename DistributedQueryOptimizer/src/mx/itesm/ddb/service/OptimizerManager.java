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
     * Parser for the SQL query.
     */
    private SqlParser parser;

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

	// TODO: Change to non-static
	if (parser == null) {
	    parser = new SqlParser(new StringReader(query.getSql()));
	} else {
	    SqlParser.ReInit(new StringReader(query.getSql()));
	}

	try {
	    queryData = SqlParser.QueryStatement();
	    returnValue = queryData.toString();
	} catch (Exception e) {
	    returnValue = "<font color='red'>Problems while parsing query [" + query + "]</font>";
	    logger.error("Problems while parsing query [" + query + "]", e);
	}

	query.setRelationalAlgebra(returnValue);
    }
}
