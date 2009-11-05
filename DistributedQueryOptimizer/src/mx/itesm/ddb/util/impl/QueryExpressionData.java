package mx.itesm.ddb.util.impl;

import mx.itesm.ddb.util.ExpressionData;
import mx.itesm.ddb.util.QueryData;

/**
 * @author jccastrejon
 * 
 */
public class QueryExpressionData implements ExpressionData {

    /**
     * 
     */
    private QueryData queryData;

    /**
     * 
     * @param query
     */
    public QueryExpressionData(QueryData queryData) {
	this.queryData = queryData;
    }

    @Override
    public QueryExpressionData clone() {
	return new QueryExpressionData(this.queryData);
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
