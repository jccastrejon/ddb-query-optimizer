package mx.itesm.ddb.util.impl;

import mx.itesm.ddb.util.RelationData;

/**
 * Holder class for a relation represented by a relation name.
 * 
 * @author jccastrejon
 * 
 */
public class SimpleRelationData implements RelationData {

    /**
     * Relation name.
     */
    public String relationName;

    /**
     * Full constructor.
     * 
     * @param relationName
     *            Relation name.
     */
    public SimpleRelationData(String relationName) {
	this.relationName = relationName;
    }

    @Override
    public SimpleRelationData clone() {
	return new SimpleRelationData(this.relationName);
    }

    @Override
    public String toString() {
	return this.relationName;
    }

    /**
     * @return the relationName
     */
    public String getRelationName() {
	return relationName;
    }

    /**
     * @param relationName
     *            the relationName to set
     */
    public void setRelationName(String relationName) {
	this.relationName = relationName;
    }
}
