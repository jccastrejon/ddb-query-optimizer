package mx.itesm.ddb.model.dictionary;

/**
 * Relation attribute.
 * 
 * @author jccastrejon
 * 
 */
public class Attribute {

    /**
     * Attribute's name.
     */
    private String name;

    /**
     * Attribute's possible values.
     */
    private AttributeDomain attributeDomain;

    /**
     * Whether or not this is a key attribute.
     */
    private boolean keyAttribute;

    /**
     * Full constructor.
     * 
     * @param name
     *            Attribute's name.
     * @param attributeDomain
     *            Attribute's possible values.
     * @param keyAttribute
     *            Whether or not this is a key attribute.
     */
    public Attribute(final String name, final AttributeDomain attributeDomain,
	    final boolean keyAttribute) {
	if ((name == null) || (attributeDomain == null)) {
	    throw new IllegalArgumentException("Invalid arguments specified for Attribute: " + name
		    + ", " + attributeDomain);
	}

	this.name = name;
	this.attributeDomain = attributeDomain;
	this.keyAttribute = keyAttribute;
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
     * @return the attributeDomain
     */
    public AttributeDomain getAttributeDomain() {
	return attributeDomain;
    }

    /**
     * @param attributeDomain
     *            the attributeDomain to set
     */
    public void setAttributeDomain(AttributeDomain attributeDomain) {
	this.attributeDomain = attributeDomain;
    }

    /**
     * @return the keyAttribute
     */
    public boolean isKeyAttribute() {
	return keyAttribute;
    }

    /**
     * @param keyAttribute
     *            the keyAttribute to set
     */
    public void setKeyAttribute(boolean keyAttribute) {
	this.keyAttribute = keyAttribute;
    }
}
