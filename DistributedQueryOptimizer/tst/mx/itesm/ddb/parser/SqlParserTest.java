package mx.itesm.ddb.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import junit.framework.TestCase;
import mx.itesm.ddb.service.OptimizerManager;
import mx.itesm.ddb.service.Query;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.Loader;

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

    @Override
    public void setUp() {
	PropertyConfigurator.configure(Loader.getResource("test-log4j.properties"));
    }

    /**
     * Execute the parser using each query in the '/sql/testQueries.sql' file
     * 
     * @throws IOException
     *             If the '/sql/testQueries.sql' cannot be properly read.
     */
    public void testQuery() throws IOException {
	Query query;
	int queryCount;
	String testQuery;
	boolean correctQueries;
	BufferedReader testReader;
	OptimizerManager optimizerManager = new OptimizerManager();

	// Load test file
	testReader = new BufferedReader(new FileReader("./sql/testQueries.sql"));
	queryCount = 0;
	correctQueries = true;

	// Try to parse each query in the file
	while ((testQuery = testReader.readLine()) != null) {
	    query = optimizerManager.createQuery(testQuery);
	    queryCount++;

	    // It should start with the Projection symbol
	    if (!query.getRelationalAlgebra().startsWith("&#0928;")) {
		correctQueries = false;
		logger.error("Problems transforming query #" + queryCount + " '" + testQuery
			+ " into relational algebra: " + query.getRelationalAlgebra());
	    }
	}

	logger.info("Correctly parsed queries: " + queryCount);
	assertEquals(correctQueries, true);
    }
}
