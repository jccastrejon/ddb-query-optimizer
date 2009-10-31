package mx.itesm.ddb.service;

import java.io.IOException;

import mx.itesm.ddb.parser.ParseException;

import org.apache.log4j.Logger;

/**
 * SQL Parser tests.
 * 
 * @author jccastrejon
 * 
 */
public class ParserServiceTest extends SqlBaseTest {

    /**
     * Class logger;
     */
    Logger logger = Logger.getLogger(ParserServiceTest.class);

    /**
     * Execute the parser using each query in the '/sql/testQueries.sql' file
     * 
     * @throws IOException
     *             If the '/sql/testQueries.sql' cannot be properly read.
     */
    public void testQuery() throws IOException {
	Query query;
	int queryCount;
	boolean correctQueries;
	ParserService parserService;

	queryCount = 0;
	correctQueries = true;
	parserService = new ParserService();

	// Try to parse each query in the test file
	for (String testQuery : this.getTestQueries()) {
	    try {
		query = parserService.createQuery(testQuery);

		logger.info("Query #" + (++queryCount) + ": " + query.getSql()
			+ " correctly parsed");
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
