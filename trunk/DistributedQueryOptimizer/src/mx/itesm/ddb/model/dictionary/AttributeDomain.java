package mx.itesm.ddb.model.dictionary;

/**
 * Specifies the valid values for a relation attribute.
 * 
 * @author jccastrejon
 * 
 */
public interface AttributeDomain {

    /**
     * Check if the specified value is valid for this domain.
     * 
     * @param value
     *            Value to check.
     * @return <em>true</em> if this is a valid value for this domain,
     *         <em>false</em> otherwise.
     */
    public boolean isValidValue(final Object value);
}