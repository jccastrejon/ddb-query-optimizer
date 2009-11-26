package mx.itesm.ddb.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.Loader;

import junit.framework.TestCase;

/**
 * Base class for the SQL tests.
 * 
 * @author jccastrejon
 * 
 */
public abstract class SqlBaseTest extends TestCase {

    /**
     * File containing the test SQL queries.
     */
    private final static String TEST_FILE = "./sql/testQueries.sql";

    /**
     * Test queries.
     */
    private List<String> testQueries;

    /**
     * Load the SQL queries in the <em>SqlTest.TEST_FILE</em> file.
     * 
     * @return List of queries.
     * @throws IOException
     *             If an I/O error occurs.
     */
    public void setUp() throws IOException {
	String testQuery;
	BufferedReader testReader;

	// Initialize test queries
	this.testQueries = new ArrayList<String>();
	testReader = new BufferedReader(new FileReader(SqlBaseTest.TEST_FILE));
	while ((testQuery = testReader.readLine()) != null) {
	    if (!testQuery.startsWith("#")) {
		this.testQueries.add(testQuery);
	    }
	}

	// Initialize logger
	PropertyConfigurator.configure(Loader.getResource("test-log4j.properties"));
    }

    /**
     * @return the testQueries
     */
    public List<String> getTestQueries() {
	return testQueries;
    }

    /**
     * @param testQueries
     *            the testQueries to set
     */
    public void setTestQueries(List<String> testQueries) {
	this.testQueries = testQueries;
    }
}