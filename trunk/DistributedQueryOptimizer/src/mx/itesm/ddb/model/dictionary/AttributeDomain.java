package mx.itesm.ddb.model.dictionary;

/**
 * Specifies the valid values for a relation attribute.
 * 
 * @author jccastrejon
 * 
 */
public enum AttributeDomain {
    StringAttributeDomain {
	@Override
	public boolean isValidValue(final String value) {
	    boolean returnValue;

	    returnValue = false;
	    if (value != null) {
		returnValue = true;
	    }

	    return returnValue;
	}
    },
    IntegerAttributeDomain {
	@Override
	public boolean isValidValue(final String value) {
	    boolean returnValue;

	    returnValue = false;
	    if (value != null) {
		try {
		    Integer.parseInt(value);
		    returnValue = true;
		} catch (NumberFormatException e) {
		    returnValue = false;
		}
	    }

	    return returnValue;
	}
    };
    /**
     * Check if the specified value is valid for this domain.
     * 
     * @param value
     *            Value to check.
     * @return <em>true</em> if this is a valid value for this domain,
     *         <em>false</em> otherwise.
     */
    public abstract boolean isValidValue(final String value);
}