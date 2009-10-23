package mx.itesm.ddb.util;

import java.util.List;

/**
 * Holder class for a SQL query data, that is, projection attributes and
 * relations.
 * 
 * @author jccastrejon
 * 
 */
public class QueryData {

    /**
     * Query projection attributes.
     */
    List<String> attributes;

    /**
     * Query relations.
     */
    List<RelationData> relations;

    /**
     * Full constructor.
     * 
     * @param attributes
     *            Query projection attributes.
     * @param relations
     *            Query relations.
     */
    public QueryData(List<String> attributes, List<RelationData> relations) {
	this.attributes = attributes;
	this.relations = relations;
    }

    @Override
    public String toString() {
	return "\u03A0<sub>" + this.attributes + "<sub>(" + this.relations + ")";
    }

    /**
     * @return the attributes
     */
    public List<String> getAttributes() {
	return attributes;
    }

    /**
     * @param attributes
     *            the attributes to set
     */
    public void setAttributes(List<String> attributes) {
	this.attributes = attributes;
    }

    /**
     * @return the relations
     */
    public List<RelationData> getRelations() {
	return relations;
    }

    /**
     * @param relations
     *            the relations to set
     */
    public void setRelations(List<RelationData> relations) {
	this.relations = relations;
    }
}
