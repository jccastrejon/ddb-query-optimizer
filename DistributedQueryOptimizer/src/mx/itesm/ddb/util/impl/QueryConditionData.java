package mx.itesm.ddb.util.impl;

import mx.itesm.ddb.util.ConditionData;
import mx.itesm.ddb.util.QueryData;

/**
 * @author jccastrejon
 * 
 */
public class QueryConditionData implements ConditionData {

    /**
     * SubQuery data.
     */
    QueryData queryData;

    /**
     * Full constructor.
     * 
     * @param queryData
     *            SubQuery data.
     */
    public QueryConditionData(QueryData queryData) {
	this.queryData = queryData;
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
