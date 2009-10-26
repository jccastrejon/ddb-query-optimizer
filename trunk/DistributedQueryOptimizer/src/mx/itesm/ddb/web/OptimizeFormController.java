package mx.itesm.ddb.web;

import javax.servlet.ServletException;

import mx.itesm.ddb.service.OptimizerManager;
import mx.itesm.ddb.service.Query;

import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 * @author jccastrejon
 * 
 */
public class OptimizeFormController extends SimpleFormController {

    /**
     * 
     */
    OptimizerManager optimizerManager;

    @Override
    public void doSubmitAction(Object command) throws ServletException {
	Query query;
	String relationalAlgebra;

	query = (Query) command;
	relationalAlgebra = optimizerManager.executeQuery(query);
	query.setRelationalAlgebra(relationalAlgebra);
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
