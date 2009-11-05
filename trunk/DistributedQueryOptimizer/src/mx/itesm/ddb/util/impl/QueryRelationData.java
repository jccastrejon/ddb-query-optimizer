package mx.itesm.ddb.util.impl;

import mx.itesm.ddb.util.QueryData;
import mx.itesm.ddb.util.RelationData;

/**
 * Holder class for a relation represented by a SubQuery.
 * 
 * @author jccastrejon
 * 
 */
public class QueryRelationData implements RelationData {

    /**
     * SubQuery data.
     */
    public QueryData queryData;

    /**
     * Full constructor.
     * 
     * @param queryData
     *            SubQuery data.
     */
    public QueryRelationData(QueryData queryData) {
	this.queryData = queryData;
    }

    @Override
    public QueryRelationData clone() {
	return new QueryRelationData(this.queryData);
    }

    @Override
    public String toString() {
	return "(" + queryData + ")";
    }

    /**
     * @return the queryData
     */
    public QueryData getQueryData() {
	return queryData;
    }

    /**
     * @param queryData
     *            the queryData to set
     */
    public void setQueryData(QueryData queryData) {
	this.queryData = queryData;
    }
}
