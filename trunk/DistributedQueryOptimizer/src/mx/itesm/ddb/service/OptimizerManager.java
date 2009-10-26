package mx.itesm.ddb.service;

import java.io.StringReader;

import mx.itesm.ddb.parser.SqlParser;
import mx.itesm.ddb.util.QueryData;

/**
 * @author jccastrejon
 * 
 */
public class OptimizerManager {

    /**
     * Reference to the actual SQL parser.
     */
    private SqlParser parser;

    /**
     * Execute a SQL query.
     * 
     * @param query
     *            SQL query.
     * @return Relational Calculus representation of the query data.
     */
    public String executeQuery(Query query) {
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
	    returnValue = "Problems while parsing query [" + query + "]";
	}

	return returnValue;
    }
}
