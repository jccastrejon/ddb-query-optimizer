package mx.itesm.ddb.service;

/**
 * Holder class for a query SQL and Relational Agebra representation.
 * 
 * @author jccastrejon
 * 
 */
public class Query {

    /**
     * SQL query.
     */
    private String sql;

    /**
     * Relational Alegbra query.
     */
    private String relationalAlgebra;

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
     * @param relationalAlgebra
     *            Relational Algebra Query.
     */
    public Query(final String sql, final String relationalAlgebra) {
	this.sql = sql;
	this.relationalAlgebra = relationalAlgebra;
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
     * @return the relationalAlgebra
     */
    public String getRelationalAlgebra() {
	return relationalAlgebra;
    }

    /**
     * @param relationalAlgebra
     *            the relationalAlgebra to set
     */
    public void setRelationalAlgebra(String relationalAlgebra) {
	this.relationalAlgebra = relationalAlgebra;
    }
}
