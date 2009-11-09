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
     * Query Id.
     */
    private String id;

    /**
     * SQL query.
     */
    private String sql;

    /**
     * Parsed Query.
     */
    private QueryData queryData;

    /**
     * Optimal Operator Tree.
     */
    private OperatorTree operatorTree;

    /**
     * Default constructor.
     */
    public Query() {
	this.id = this.generateId();
    }

    /**
     * Initialize the query with the given SQL.
     * 
     * @param sql
     *            SQL Query.
     */
    public Query(final String sql) {
	this();
	this.sql = sql;
    }

    /**
     * Initialize the query with the given QueryData.
     * 
     * @param queryData
     *            Parsed Query.
     */
    public Query(final QueryData queryData) {
	this();
	this.queryData = queryData;
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
	this(sql);
	this.queryData = queryData;
    }

    /**
     * Generate an Id for this Query with the current time and a random double.
     * 
     * @return Id.
     */
    private String generateId() {
	return System.currentTimeMillis() + "-" + Math.random();
    }

    /**
     * @return the id
     */
    public String getId() {
	return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id) {
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
