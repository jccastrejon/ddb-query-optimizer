package mx.itesm.ddb.model.dictionary;

import java.util.Collection;

/**
 * Relation fragment obtained by applying both the <em>Selection</em> operation
 * using the minterm over the source relation and the <em>Projection</em>
 * operation using the fragment's attributes.
 * 
 * @author jccastrejon
 * 
 */
public class HybridFragment extends Relation {

    /**
     * Source relation of this fragment.
     */
    private Relation source;

    /**
     * Minterm that defines this fragment.
     */
    Collection<Predicate> minterm;

    /**
     * 
     * @param name
     * @param source
     * @param minterm
     * @param attributes
     * @param fragmentationType
     */
    public HybridFragment(final String name, final Relation source,
	    final Collection<Predicate> minterm, final Collection<Attribute> attributes,
	    final FragmentationType fragmentationType) {
	super(name, attributes, FragmentationType.Hybrid);
	this.source = source;
	this.minterm = minterm;
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

    /**
     * @return the minterm
     */
    public Collection<Predicate> getMinterm() {
	return minterm;
    }

    /**
     * @param minterm
     *            the minterm to set
     */
    public void setMinterm(Collection<Predicate> minterm) {
	this.minterm = minterm;
    }
}
