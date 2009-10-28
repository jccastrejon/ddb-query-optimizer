package mx.itesm.ddb.service;

import mx.itesm.ddb.parser.ParseException;

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
     * Query Optimizer Manager.
     */
    private OptimizerManager optimizerManager;

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
	    optimizerManager.updateRelationalAlgebra(query);
	} catch (ParseException e) {
	    errors.rejectValue("sql", "error.parseError", new Object[] { e.getMessage() }, null);
	}
    }

    /**
     * @return the optimizerManager
     */
    public OptimizerManager getOptimizerManager() {
	return optimizerManager;
    }

    /**
     * @param optimizerManager
     *            the optimizerManager to set
     */
    public void setOptimizerManager(OptimizerManager optimizerManager) {
	this.optimizerManager = optimizerManager;
    }
}