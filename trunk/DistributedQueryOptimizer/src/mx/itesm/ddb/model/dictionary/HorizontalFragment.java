package mx.itesm.ddb.model.dictionary;

import java.util.Collection;

/**
 * Relation fragment obtained by applying the <em>Selection</em> operation using
 * the minterm over the source relation.
 * 
 * @author jccastrejon
 * 
 */
public class HorizontalFragment extends Relation implements MintermDependentFragment {

    /**
     * Source relation of this fragment.
     */
    private Relation source;

    /**
     * Minterm that defines this fragment.
     */
    Collection<Predicate> minterm;

    /**
     * Full constructor.
     * 
     * @param name
     *            Fragment name.
     * @param source
     *            Source relation of this fragment.
     * @param minterm
     *            Minterm that defines this fragment.
     * @param fragmentationType
     *            Fragmentation Type (Primary - Derived).
     */
    public HorizontalFragment(final String name, final Relation source,
	    final Collection<Predicate> minterm, final FragmentationType fragmentationType) {
	super(name, source, fragmentationType);
	this.minterm = minterm;
	this.source = source;
	this.source.addFragment(this);
    }

    /**
     * @return the source
     */
    public Relation getSource() {
	return source;
    }

    /**
     * @param source
     *            the source to set
     */
    public void setSource(Relation source) {
	this.source = source;
    }

    @Override
    public Collection<Predicate> getMinterm() {
	return minterm;
    }

    @Override
    public void setMinterm(Collection<Predicate> minterm) {
	this.minterm = minterm;
    }
}
