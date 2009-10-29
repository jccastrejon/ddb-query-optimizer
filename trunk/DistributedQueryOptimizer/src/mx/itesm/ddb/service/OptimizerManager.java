package mx.itesm.ddb.service;

import java.io.StringReader;

import mx.itesm.ddb.parser.ParseException;
import mx.itesm.ddb.parser.SqlParser;
import mx.itesm.ddb.util.QueryData;

/**
 * Manager that allows execution of service methods over the Query object.
 * 
 * @author jccastrejon
 * 
 */
public class OptimizerManager {

    /**
     * Get the Query Object containing the Relational Algebra representation of
     * the SQL query.
     * 
     * @param query
     *            SQL query.
     * @return Query object containg the SQL and Relational Algebra
     *         representation of the original SQL query.
     * @throws ParseException
     *             If the SQL query cannot be converted to Relational Algebra.
     */
    public Query createQuery(final String query) throws ParseException {
	Query returnValue;

	returnValue = new Query(query);
	this.parseQuery(returnValue);

	return returnValue;
    }

    /**
     * Get the Relational Algebra representation of the SQL query.
     * 
     * @param query
     *            Query object containing the SQL query.
     * @throws ParseException
     *             If the SQL query cannot be converted to Relational Algebra.
     */
    public void parseQuery(final Query query) throws ParseException {
	QueryData queryData;
	SqlParser parser;

	parser = new SqlParser(new StringReader(query.getSql()));
	queryData = parser.QueryStatement();
	query.setQueryData(queryData);
    }
}
