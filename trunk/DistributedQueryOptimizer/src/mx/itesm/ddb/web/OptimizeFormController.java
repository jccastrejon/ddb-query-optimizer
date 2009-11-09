package mx.itesm.ddb.web;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;

import mx.itesm.ddb.service.ParserService;
import mx.itesm.ddb.service.Query;
import mx.itesm.ddb.service.AlgebraOptimizerService;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 * Form controller for the Optimize Query process.
 * 
 * @author jccastrejon
 * 
 */
public class OptimizeFormController extends SimpleFormController {

    /**
     * Class logger.
     */
    Logger logger = Logger.getLogger(OptimizeFormController.class);

    /**
     * Parser service.
     */
    private ParserService parserService;

    /**
     * Relational Algebra Optimizer.
     */
    AlgebraOptimizerService algebraOptimizerService;

    @Override
    public void doSubmitAction(Object command) throws ServletException {
	Query query;
	File queryDir;

	query = (Query) command;
	try {
	    queryDir = new File(this.getServletContext().getRealPath("/img/" + query.getId()));
	    queryDir.mkdirs();
	    queryDir.deleteOnExit();

	    algebraOptimizerService.buildOperatorTree(query, queryDir);
	} catch (IOException e) {
	    logger.error("Error building operatorTree for: " + query.getSql(), e);
	}
    }

    /**
     * @return the parserService
     */
    public ParserService getParserService() {
	return parserService;
    }

    /**
     * @param parserService
     *            the parserService to set
     */
    public void setParserService(ParserService parserService) {
	this.parserService = parserService;
    }

    /**
     * @return the algebraOptimizerService
     */
    public AlgebraOptimizerService getAlgebraOptimizerService() {
	return algebraOptimizerService;
    }

    /**
     * @param algebraOptimizerService
     *            the algebraOptimizerService to set
     */
    public void setAlgebraOptimizerService(AlgebraOptimizerService algebraOptimizerService) {
	this.algebraOptimizerService = algebraOptimizerService;
    }
}
