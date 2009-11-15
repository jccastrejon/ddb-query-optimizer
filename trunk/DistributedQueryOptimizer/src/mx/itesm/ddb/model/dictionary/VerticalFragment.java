package mx.itesm.ddb.model.dictionary;

import java.util.Collection;

/**
 * Relation fragment obtained by applying the Projection operation using the
 * fragment's attributes.
 * 
 * @author jccastrejon
 * 
 */
public class VerticalFragment extends Relation {

    /**
     * Source relation of this fragment.
     */
    private Relation source;

    /**
     * Full constructor.
     * 
     * @param name
     *            Fragment name.
     * @param source
     *            Source relation of this fragment.
     * @param attributes
     *            Attributes from the source relation that define this fragment.
     */
    public VerticalFragment(final String name, final Relation source,
	    final Collection<Attribute> attributes) {
	super(name, attributes);
	this.source = source;

	if (!this.getKeyAttributes().containsAll(source.getKeyAttributes())) {
	    throw new IllegalArgumentException(
		    "Vertical fragment doesn't contain required key attributes. Required: "
			    + source.getKeyAttributes() + ", provided: " + this.getKeyAttributes());
	}
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
}
