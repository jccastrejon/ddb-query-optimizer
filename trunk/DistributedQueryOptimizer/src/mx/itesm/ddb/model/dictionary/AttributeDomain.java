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

	@Override
	public int compareValues(final String first, final String second) {
	    return first.compareToIgnoreCase(second.trim());
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

	@Override
	public int compareValues(final String first, final String second) {
	    return new Integer(first.trim()).compareTo(new Integer(second.trim()));
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

    /**
     * Compare to values according to the domain semantics.
     * 
     * @param first
     *            First value.
     * @param second
     *            Second value.
     * @return <em>0</em> if the values are equal,
     *         <em>a value lower than cero</em> if the first value is lower than
     *         the second one and <em>a value greater than cero</em> otherwise.
     */
    public abstract int compareValues(final String first, final String second);
}