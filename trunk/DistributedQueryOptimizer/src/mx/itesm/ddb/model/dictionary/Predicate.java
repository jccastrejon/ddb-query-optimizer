package mx.itesm.ddb.model.dictionary;

/**
 * Simple Predicate that links an attribute with some value of its domain using
 * a predicator operator.
 * 
 * @author jccastrejon
 * 
 */
public class Predicate {

    /**
     * Attribute refered by this predicate.
     */
    private Attribute attribute;

    /**
     * Operator used by this predicate to link attribute - value.
     */
    private PredicateOperator predicateOperator;

    /**
     * Value of the attribute's domain used by this predicate.
     */
    private Object value;

    /**
     * Full constructor.
     * 
     * @param attribute
     *            Attribute refered by this predicate.
     * @param predicateOperator
     *            Operator used by this predicate to link attribute - value.
     * @param value
     *            Value of the attribute's domain used by this predicate.
     */
    public Predicate(final Attribute attribute, final PredicateOperator predicateOperator,
	    final Object value) {
	if (!attribute.getAttributeDomain().isValidValue(value)) {
	    throw new IllegalArgumentException("Invalid value: " + value + " for domain: "
		    + attribute.getAttributeDomain());
	}

	this.attribute = attribute;
	this.predicateOperator = predicateOperator;
	this.value = value;
    }

    /**
     * @return the attribute
     */
    public Attribute getAttribute() {
	return attribute;
    }

    /**
     * @param attribute
     *            the attribute to set
     */
    public void setAttribute(Attribute attribute) {
	this.attribute = attribute;
    }

    /**
     * @return the predicateOperator
     */
    public PredicateOperator getPredicateOperator() {
	return predicateOperator;
    }

    /**
     * @param predicateOperator
     *            the predicateOperator to set
     */
    public void setPredicateOperator(PredicateOperator predicateOperator) {
	this.predicateOperator = predicateOperator;
    }

    /**
     * @return the value
     */
    public Object getValue() {
	return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(Object value) {
	this.value = value;
    }
}
