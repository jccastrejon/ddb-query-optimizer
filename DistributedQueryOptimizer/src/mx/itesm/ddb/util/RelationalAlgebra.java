/**
 * 
 */
package mx.itesm.ddb.util;

import java.util.List;

/**
 * @author jccastrejon
 * 
 */
public class RelationalAlgebra {

    /**
     * 
     */
    List<String> attributes;

    /**
     * 
     */
    List<String> relations;

    /**
     * 
     * @param attributes
     * @param relations
     */
    public RelationalAlgebra(List<String> attributes, List<String> relations) {
	this.attributes = attributes;
	this.relations = relations;
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
    public List<String> getRelations() {
	return relations;
    }

    /**
     * @param relations
     *            the relations to set
     */
    public void setRelations(List<String> relations) {
	this.relations = relations;
    }
}
