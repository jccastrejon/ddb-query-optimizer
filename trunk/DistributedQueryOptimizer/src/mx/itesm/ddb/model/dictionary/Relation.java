package mx.itesm.ddb.model.dictionary;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Database relation.
 * 
 * @author jccastrejon
 * 
 */
public class Relation {

    /**
     * Relation name.
     */
    private String name;

    /**
     * Relation attributes.
     */
    private Collection<Attribute> attributes;

    /**
     * Relation key attributes.
     */
    private Collection<Attribute> keyAttributes;

    /**
     * Relation fragments that take this as a source relation.
     */
    private Collection<Relation> fragments;

    /**
     * Fragmentation type applied over this relation.
     */
    private FragmentationType fragmentationType;

    /**
     * Minimal constructor that specifies only the relation name.
     * 
     * @param name
     *            Relation name.
     */
    public Relation(final String name) {
	if (name == null) {
	    throw new IllegalArgumentException("Invalid relation name: " + name);
	}

	this.name = name;
    }

    /**
     * Full constructor that specifies the relation name and the attributes that
     * define it.
     * 
     * @param name
     *            Relation name.
     * @param attributes
     *            Relation attributes.
     */
    public Relation(final String name, final Collection<Attribute> attributes) {
	this(name);

	if (attributes == null) {
	    throw new IllegalArgumentException("Invalid attributes specified for relation: " + name
		    + ", specified: " + attributes);
	}

	this.attributes = attributes;
	this.assignKeyAttributes(attributes);
    }

    /**
     * Full constructor that specifies the relation name and the attributes that
     * define it.
     * 
     * @param name
     *            Relation name.
     * @param attributes
     *            Relation attributes.
     * @param fragmentationType
     *            Fragmentation Type.
     */
    public Relation(final String name, final Collection<Attribute> attributes,
	    final FragmentationType fragmentationType) {
	this(name, attributes);
	this.fragmentationType = fragmentationType;
    }

    /**
     * Full constructor that specifies the relation name and a base relation for
     * the other attributes.
     * 
     * @param name
     *            Relation name.
     * @param relation
     *            Base relation.
     */
    public Relation(final String name, final Relation relation) {
	this(name);

	if (relation == null) {
	    throw new IllegalArgumentException("Invalid base relation specified for relation: "
		    + name + ", specified: " + relation);
	}

	this.attributes = relation.getAttributes();
	this.assignKeyAttributes(relation.getAttributes());
    }

    /**
     * Full constructor that specifies the relation name and a base relation for
     * the other attributes.
     * 
     * @param name
     *            Relation name.
     * @param relation
     *            Base relation.
     * @param fragmentationType
     *            Fragmentation Type.
     */
    public Relation(final String name, final Relation relation,
	    final FragmentationType fragmentationType) {
	this(name, relation);
	this.fragmentationType = fragmentationType;
    }

    /**
     * Add a new fragment to this relation.
     * 
     * @param relation
     *            Fragment.
     */
    public void addFragment(final Relation relation) {
	if (relation != null) {
	    if (this.fragments == null) {
		this.fragments = new ArrayList<Relation>();
	    }

	    // A relation can only be fragmented in one way
	    if (this.fragmentationType != null) {
		if (this.fragmentationType != relation.fragmentationType) {
		    throw new IllegalArgumentException("Invalid fragment type: "
			    + relation.getFragmentationType() + " for relation of type: "
			    + this.getFragmentationType());
		}
	    } else {
		this.fragmentationType = relation.getFragmentationType();
	    }

	    this.fragments.add(relation);
	}
    }

    /**
     * Get the attribute identified with the given name.
     * 
     * @param name
     *            Attribute's name.
     * @return The Attribute if it's indeed associated with this relation,
     *         <em>null</em> otherwise.
     */
    public Attribute getAttribute(final String name) {
	Attribute returnValue;

	returnValue = null;
	if (this.attributes != null) {
	    for (Attribute attribute : this.attributes) {
		if (attribute.getName().equals(name)) {
		    returnValue = attribute;
		    break;
		}
	    }
	}

	return returnValue;
    }

    /**
     * Assign the key attributes to the <em>keyAttributes</em> property, from
     * the specified attributes collection.
     * 
     * @param attributes
     *            Attributes collection.
     */
    private void assignKeyAttributes(final Collection<Attribute> attributes) {
	this.keyAttributes = new ArrayList<Attribute>();
	if (attributes != null) {
	    for (Attribute attribute : attributes) {
		if ((attribute != null) && (attribute.isKeyAttribute())) {
		    this.keyAttributes.add(attribute);
		}
	    }
	}
    }

    /**
     * @return the name
     */
    public String getName() {
	return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
	this.name = name;
    }

    /**
     * @return the attributes
     */
    public Collection<Attribute> getAttributes() {
	return attributes;
    }

    /**
     * @param attributes
     *            the attributes to set
     */
    public void setAttributes(Collection<Attribute> attributes) {
	this.attributes = attributes;
    }

    /**
     * @return the keyAttributes
     */
    public Collection<Attribute> getKeyAttributes() {
	return keyAttributes;
    }

    /**
     * @param keyAttributes
     *            the keyAttributes to set
     */
    public void setKeyAttributes(Collection<Attribute> keyAttributes) {
	this.keyAttributes = keyAttributes;
    }

    /**
     * @return the fragments
     */
    public Collection<Relation> getFragments() {
	return fragments;
    }

    /**
     * @param fragments
     *            the fragments to set
     */
    public void setFragments(Collection<Relation> fragments) {
	this.fragments = fragments;
    }

    /**
     * @return the fragmentationType
     */
    public FragmentationType getFragmentationType() {
	return fragmentationType;
    }

    /**
     * @param fragmentationType
     *            the fragmentationType to set
     */
    public void setFragmentationType(FragmentationType fragmentationType) {
	this.fragmentationType = fragmentationType;
    }
}
