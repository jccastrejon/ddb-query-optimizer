package mx.itesm.ddb.model.dictionary;

import java.util.Collection;

/**
 * Identify a fragment that depends on a Minterm as a fragmentation criteria.
 * 
 * @author jccastrejon
 * 
 */
public interface MintermDependentFragment {

    /**
     * @return the minterm
     */
    public Collection<Predicate> getMinterm();

    /**
     * @param minterm
     *            the minterm to set
     */
    public void setMinterm(final Collection<Predicate> minterm);
}
