package mx.itesm.ddb.web;

import javax.servlet.ServletException;

import mx.itesm.ddb.service.OptimizerManager;

import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 * Form controller for the Optimize Query process.
 * 
 * @author jccastrejon
 * 
 */
public class OptimizeFormController extends SimpleFormController {

    /**
     * Query Optimizer Manager.
     */
    private OptimizerManager optimizerManager;

    @Override
    public void doSubmitAction(Object command) throws ServletException {

	// TODO: Algebra manipulation
	//Query query = (Query) command;
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
