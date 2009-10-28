package mx.itesm.ddb.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;
import mx.itesm.ddb.util.QueryData;

import org.apache.log4j.Logger;

/**
 * SQL Parser tests.
 * 
 * @author jccastrejon
 * 
 */
public class SqlParserTest extends TestCase {

    /**
     * Class logger;
     */
    Logger logger = Logger.getLogger(SqlParserTest.class.getName());

    /**
     * Execute the parser using each query in the '/sql/testQueries.sql' file
     * 
     * @throws IOException
     *             If the '/sql/testQueries.sql' cannot be properly read.
     */
    public void testQuery() throws IOException {
	int queryCount;
	String query;
	SqlParser parser;
	boolean correctQueries;
	BufferedReader testReader;
	QueryData queryStatement;

	testReader = new BufferedReader(new FileReader("./sql/testQueries.sql"));
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
		logger.info("\nQuery #" + queryCount + " correctly parsed:\n" + queryStatement);
	    } catch (Exception e) {
		logger.error("Problems in query #" + queryCount, e);
		correctQueries = false;
	    }
	}

	assertEquals(correctQueries, true);
	logger.info("Correctly parsed queries: " + queryCount);
    }
}
