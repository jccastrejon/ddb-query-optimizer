package mx.itesm.ddb.service;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;
import mx.itesm.ddb.parser.ParseException;

/**
 * @author jccastrejon
 * 
 */
public class AlgebraOptimizerServiceTest extends TestCase {

    /**
     * @throws ParseException
     * 
     */
    public void testBuildOperatorTree() throws ParseException, IOException {
	Query query;
	ParserService parserService;
	AlgebraOptimizerService algebraOptimizerService;

	parserService = new ParserService();
	algebraOptimizerService = new AlgebraOptimizerService();
	algebraOptimizerService.setDatabaseDictionaryService(new DatabaseDictionaryService());
	query = parserService
		.createQuery("Select * from tabla, tabla2 where tabla.attr = tabla2.attr;");
	algebraOptimizerService.buildOperatorTree(query, new File("img"));

	assertNotNull(query.getOperatorTree());
    }
}
