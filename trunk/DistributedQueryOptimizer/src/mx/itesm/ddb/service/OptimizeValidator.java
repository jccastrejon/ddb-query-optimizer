package mx.itesm.ddb.service;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * @author jccastrejon
 * 
 */
@SuppressWarnings("unchecked")
public class OptimizeValidator implements Validator {

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
    }

}
