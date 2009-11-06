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
    private List<ExpressionData> attributes;

    /**
     * Query relations.
     */
    private List<RelationData> relations;

    /**
     * Query conditions.
     */
    private ConditionData conditions;

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
    public QueryData(final List<ExpressionData> attributes, final List<RelationData> relations,
	    ConditionData conditions) {
	this.attributes = attributes;
	this.relations = relations;
	this.conditions = conditions;
    }

    @Override
    public String toString() {
	int counter;
	StringBuilder returnValue;

	// Projections
	returnValue = new StringBuilder(RelationalOperator.PROJECTION.getHtmlCode());
	returnValue.append("<sub>");
	counter = 0;
	for (ExpressionData attribute : this.attributes) {
	    returnValue.append(attribute);

	    if ((++counter) < this.attributes.size()) {
		returnValue.append(", ");
	    }
	}
	returnValue.append("</sub>(");

	// Selections
	if (this.conditions != null) {
	    returnValue.append(RelationalOperator.SELECT.getHtmlCode());
	    returnValue.append("<sub>");
	    returnValue.append(this.conditions);
	    returnValue.append("</sub>(");
	}

	// Relations
	counter = 0;
	for (RelationData relation : this.relations) {
	    returnValue.append(relation);

	    if ((++counter) < this.relations.size()) {
		returnValue.append(RelationalOperator.PRODUCT.getHtmlCode());
	    }
	}
	returnValue.append(")");

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
