package mx.itesm.ddb.service;

import mx.itesm.ddb.service.operator.OperatorTree;
import mx.itesm.ddb.util.QueryData;

/**
 * Holder class for a query SQL and Relational Agebra representation.
 * 
 * @author jccastrejon
 * 
 */
public class Query {

    /**
     * 
     */
    private long id;

    /**
     * SQL query.
     */
    private String sql;

    /**
     * Parsed Query.
     */
    private QueryData queryData;

    /**
     * 
     */
    private int intermediateOperatorTrees;

    /**
     * 
     */
    private OperatorTree operatorTree;

    /**
     * Default constructor.
     */
    public Query() {
    }

    /**
     * Initialize the query with the given SQL.
     * 
     * @param sql
     *            SQL Query.
     */
    public Query(final String sql) {
	this.sql = sql;
    }

    /**
     * Full constructor.
     * 
     * @param sql
     *            SQL Query.
     * @param queryData
     *            Parsed Query.
     */
    public Query(final String sql, final QueryData queryData) {
	this.sql = sql;
	this.queryData = queryData;
    }

    /**
     * @return the id
     */
    public long getId() {
	return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(long id) {
	this.id = id;
    }

    /**
     * @return the sql
     */
    public String getSql() {
	return sql;
    }

    /**
     * @param sql
     *            the sql to set
     */
    public void setSql(String sql) {
	this.sql = sql;
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

    /**
     * @return the intermediateOperatorTrees
     */
    public int getIntermediateOperatorTrees() {
	return intermediateOperatorTrees;
    }

    /**
     * @param intermediateOperatorTrees
     *            the intermediateOperatorTrees to set
     */
    public void setIntermediateOperatorTrees(int intermediateOperatorTrees) {
	this.intermediateOperatorTrees = intermediateOperatorTrees;
    }

    /**
     * @return the operatorTree
     */
    public OperatorTree getOperatorTree() {
	return operatorTree;
    }

    /**
     * @param operatorTree
     *            the operatorTree to set
     */
    public void setOperatorTree(OperatorTree operatorTree) {
	this.operatorTree = operatorTree;
    }
}
