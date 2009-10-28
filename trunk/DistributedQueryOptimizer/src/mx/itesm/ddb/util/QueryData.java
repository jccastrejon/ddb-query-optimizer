package mx.itesm.ddb.util;

import java.util.List;

/**
 * SQL query containing projection attributes, relations and conditions.
 * 
 * @author jccastrejon
 * 
 */
public class QueryData {

    /**
     * Query projection attributes.
     */
    List<ExpressionData> attributes;

    /**
     * Query relations.
     */
    List<RelationData> relations;

    /**
     * Query conditions.
     */
    ConditionData conditions;

    /**
     * Full constructor.
     * 
     * @param attributes
     *            Query projection attributes.
     * @param relations
     *            Query relations.
     * @param conditions
     *            Query conditions.
     */
    public QueryData(List<ExpressionData> attributes, List<RelationData> relations,
	    ConditionData conditions) {
	this.attributes = attributes;
	this.relations = relations;
	this.conditions = conditions;
    }

    @Override
    public String toString() {
	StringBuilder returnValue;

	// Projections
	returnValue = new StringBuilder("&#0928;<sub>" + this.attributes + "</sub>(");

	// Selections
	if (this.conditions != null) {
	    returnValue.append("&#0963;<sub>" + this.conditions + "</sub>(");
	}

	// Relations
	returnValue.append(this.relations + ")");

	// Close parenthesis in case of selections
	if (this.conditions != null) {
	    returnValue.append(")");
	}

	return returnValue.toString();
    }

    /**
     * @return the attributes
     */
    public List<ExpressionData> getAttributes() {
	return attributes;
    }

    /**
     * @param attributes
     *            the attributes to set
     */
    public void setAttributes(List<ExpressionData> attributes) {
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

    /**
     * @return the conditions
     */
    public ConditionData getConditions() {
	return conditions;
    }

    /**
     * @param conditions
     *            the conditions to set
     */
    public void setConditions(ConditionData conditions) {
	this.conditions = conditions;
    }
}
