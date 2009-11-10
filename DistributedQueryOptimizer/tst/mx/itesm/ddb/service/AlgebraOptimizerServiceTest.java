package mx.itesm.ddb.service;

import java.io.File;

import mx.itesm.ddb.parser.ParseException;

import org.apache.log4j.Logger;

/**
 * @author jccastrejon
 * 
 */
public class AlgebraOptimizerServiceTest extends SqlBaseTest {

    /**
     * Class logger;
     */
    Logger logger = Logger.getLogger(AlgebraOptimizerServiceTest.class);

    /**
     * @throws ParseException
     * 
     */
    public void testBuildOperatorTree() throws ParseException {
	Query query;
	File imageDir;
	int queryCount;
	boolean correctQueries;
	ParserService parserService;
	RewritingService rewritingService;
	GraphicExportService graphicExportService;
	AlgebraOptimizerService algebraOptimizerService;
	DatabaseDictionaryService databaseDictionaryService;

	queryCount = 0;
	correctQueries = true;
	imageDir = new File("img");
	imageDir.mkdir();
	// imageDir.deleteOnExit();
	parserService = new ParserService();
	rewritingService = new RewritingService();
	graphicExportService = new GraphicExportService();
	algebraOptimizerService = new AlgebraOptimizerService();
	databaseDictionaryService = new DatabaseDictionaryService();
	algebraOptimizerService.setRewritingService(rewritingService);
	rewritingService.setGraphicExportService(graphicExportService);
	rewritingService.setDatabaseDictionaryService(databaseDictionaryService);
	algebraOptimizerService.setDatabaseDictionaryService(databaseDictionaryService);

	// Try to parse each query in the test file
	for (String testQuery : this.getTestQueries()) {
	    try {
		query = parserService.createQuery(testQuery);
		algebraOptimizerService.buildOperatorTree(query, imageDir);

		logger.info("Operator tree for Query #" + (++queryCount) + ": " + query.getSql()
			+ " correctly generated");
	    } catch (Exception e) {
		correctQueries = false;
		logger.error("Problems transforming query #" + queryCount + " '" + testQuery
			+ " into an operator tree: ", e);
	    }
	}

	logger.info("Correctly operator trees generated: " + queryCount);
	assertEquals(correctQueries, true);
    }
}
