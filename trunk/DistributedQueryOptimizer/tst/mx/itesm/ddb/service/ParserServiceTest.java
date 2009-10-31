package mx.itesm.ddb.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import junit.framework.TestCase;
import mx.itesm.ddb.parser.ParseException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.Loader;

/**
 * SQL Parser tests.
 * 
 * @author jccastrejon
 * 
 */
public class ParserServiceTest extends TestCase {

    /**
     * Class logger;
     */
    Logger logger = Logger.getLogger(ParserServiceTest.class.getName());

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
	ParserService parserService = new ParserService();

	// Load test file
	testReader = new BufferedReader(new FileReader("./sql/testQueries.sql"));
	queryCount = 0;
	correctQueries = true;

	// Try to parse each query in the file
	while ((testQuery = testReader.readLine()) != null) {
	    try {
		query = parserService.createQuery(testQuery);
		queryCount++;
		logger.info("Query #" + queryCount + ": " + query.getQueryData());
	    } catch (ParseException e) {
		correctQueries = false;
		logger.error("Problems transforming query #" + queryCount + " '" + testQuery
			+ " into relational algebra: ", e);
	    }
	}

	logger.info("Correctly parsed queries: " + queryCount);
	assertEquals(correctQueries, true);
    }
}
