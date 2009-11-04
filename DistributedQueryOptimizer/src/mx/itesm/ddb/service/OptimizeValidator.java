package mx.itesm.ddb.service;

import mx.itesm.ddb.parser.ParseException;

import org.apache.log4j.Logger;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validator for Query instances.
 * 
 * @author jccastrejon
 * 
 */
@SuppressWarnings("unchecked")
public class OptimizeValidator implements Validator {

    /**
     * 
     */
    private final static Logger logger = Logger.getLogger(OptimizeValidator.class);

    /**
     * Query Optimizer Manager.
     */
    private ParserService parserService;

    @Override
    public boolean supports(Class clazz) {
	return Query.class.equals(clazz);
    }

    @Override
    public void validate(Object object, Errors errors) {
	Query query = (Query) object;

	if (query == null) {
	    errors.rejectValue("sql", "error.queryNotSpecified");
	}

	// Try to get the Relation Algebra representation
	try {
	    parserService.parseQuery(query);
	} catch (ParseException e) {
	    errors.rejectValue("sql", "error.parseError", new Object[] { e.getMessage() }, null);
	    logger.error("Invalid query", e);
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
}