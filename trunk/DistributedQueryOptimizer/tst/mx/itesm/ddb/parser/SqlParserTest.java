package mx.itesm.ddb.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.TestCase;
import mx.itesm.ddb.util.RelationalAlgebraVisitor;

/**
 * @author jccastrejon
 * 
 */
public class SqlParserTest extends TestCase {

    /**
     * Class logger;
     */
    Logger logger = Logger.getLogger(SqlParserTest.class.getName());

    /**
     * Execute the parser using each query in the /sql/testQueries.sql
     * 
     * @throws Exception
     */
    public void testQuery() throws Exception {
	int queryCount;
	String query;
	SqlParser parser;
	boolean correctQueries;
	BufferedReader testReader;
	ASTQueryStatement queryStatement;
	RelationalAlgebraVisitor visitor;

	testReader = new BufferedReader(new FileReader("./sql/testQueries.sql"));
	visitor = new RelationalAlgebraVisitor();
	parser = null;
	queryCount = 0;
	correctQueries = true;

	// Try to parse each query on the file
	while ((query = testReader.readLine()) != null) {
	    queryCount++;

	    // Considering a static parser
	    if (parser == null) {
		parser = new SqlParser(new StringReader(query));
	    } else {
		SqlParser.ReInit(new StringReader(query));
	    }

	    try {
		queryStatement = SqlParser.QueryStatement();
		queryStatement.jjtAccept(visitor, null);
	    } catch (Exception e) {
		logger.log(Level.INFO, "Problems in query #" + queryCount, e);
		correctQueries = false;
	    }
	}

	assertEquals(correctQueries, true);
	logger.info("Correctly parsed queries: " + queryCount);
    }
}
