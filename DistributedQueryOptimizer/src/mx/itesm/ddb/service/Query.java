package mx.itesm.ddb.service;

/**
 * @author jccastrejon
 * 
 */
public class Query {

    /**
     * 
     */
    private String sql;

    /**
     * 
     */
    private String relationalAlgebra;

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
