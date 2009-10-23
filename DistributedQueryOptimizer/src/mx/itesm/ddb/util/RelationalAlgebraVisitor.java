package mx.itesm.ddb.util;

import mx.itesm.ddb.parser.ASTExistsClause;
import mx.itesm.ddb.parser.ASTFromClause;
import mx.itesm.ddb.parser.ASTIsNullClause;
import mx.itesm.ddb.parser.ASTQuery;
import mx.itesm.ddb.parser.ASTQueryStatement;
import mx.itesm.ddb.parser.ASTQueryTableExpression;
import mx.itesm.ddb.parser.ASTSQLAndExpression;
import mx.itesm.ddb.parser.ASTSQLBetweenClause;
import mx.itesm.ddb.parser.ASTSQLExponentExpression;
import mx.itesm.ddb.parser.ASTSQLExpression;
import mx.itesm.ddb.parser.ASTSQLExpressionList;
import mx.itesm.ddb.parser.ASTSQLInClause;
import mx.itesm.ddb.parser.ASTSQLLikeClause;
import mx.itesm.ddb.parser.ASTSQLMultiplicativeExpression;
import mx.itesm.ddb.parser.ASTSQLPrimaryExpression;
import mx.itesm.ddb.parser.ASTSQLRelationalExpression;
import mx.itesm.ddb.parser.ASTSQLRelationalOperatorExpression;
import mx.itesm.ddb.parser.ASTSQLSimpleExpression;
import mx.itesm.ddb.parser.ASTSQLUnaryExpression;
import mx.itesm.ddb.parser.ASTSQLUnaryLogicalExpression;
import mx.itesm.ddb.parser.ASTSelectItem;
import mx.itesm.ddb.parser.ASTSelectList;
import mx.itesm.ddb.parser.ASTWhereClause;
import mx.itesm.ddb.parser.SimpleNode;
import mx.itesm.ddb.parser.SqlParserVisitor;

/**
 * @author jccastrejon
 * 
 */
public class RelationalAlgebraVisitor implements SqlParserVisitor {
    private int indent = 0;

    private String indentString() {
	StringBuffer sb = new StringBuffer();
	for (int i = 0; i < indent; ++i) {
	    sb.append(' ');
	}
	return sb.toString();
    }
    

    @Override
    public Object visit(SimpleNode node, Object data) {
	System.out.println(indentString() + node + ": acceptor not unimplemented in subclass?");
	++indent;
	data = node.childrenAccept(this, data);
	--indent;
	return data;
    }

    /*
     * (non-Javadoc)
     * 
     * @seemx.itesm.ddb.parser.SqlParserVisitor#visit(mx.itesm.ddb.parser.
     * ASTQueryStatement, java.lang.Object)
     */
    @Override
    public Object visit(ASTQueryStatement node, Object data) {
	System.out.println(indentString() + node);
	++indent;
	data = node.childrenAccept(this, data);
	--indent;
	return data;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * mx.itesm.ddb.parser.SqlParserVisitor#visit(mx.itesm.ddb.parser.ASTQuery,
     * java.lang.Object)
     */
    @Override
    public Object visit(ASTQuery node, Object data) {
	System.out.println(indentString() + node);
	++indent;
	data = node.childrenAccept(this, data);
	--indent;
	return data;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * mx.itesm.ddb.parser.SqlParserVisitor#visit(mx.itesm.ddb.parser.ASTSelectList
     * , java.lang.Object)
     */
    @Override
    public Object visit(ASTSelectList node, Object data) {
	System.out.println(indentString() + node);
	++indent;
	data = node.childrenAccept(this, data);
	--indent;
	return data;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * mx.itesm.ddb.parser.SqlParserVisitor#visit(mx.itesm.ddb.parser.ASTSelectItem
     * , java.lang.Object)
     */
    @Override
    public Object visit(ASTSelectItem node, Object data) {
	System.out.println(indentString() + node);
	++indent;
	data = node.childrenAccept(this, data);
	--indent;
	return data;
    }

    /*
     * (non-Javadoc)
     * 
     * @seemx.itesm.ddb.parser.SqlParserVisitor#visit(mx.itesm.ddb.parser.
     * ASTSQLSimpleExpression, java.lang.Object)
     */
    @Override
    public Object visit(ASTSQLSimpleExpression node, Object data) {
	System.out.println(indentString() + node);
	++indent;
	data = node.childrenAccept(this, data);
	--indent;
	return data;
    }

    /*
     * (non-Javadoc)
     * 
     * @seemx.itesm.ddb.parser.SqlParserVisitor#visit(mx.itesm.ddb.parser.
     * ASTSQLMultiplicativeExpression, java.lang.Object)
     */
    @Override
    public Object visit(ASTSQLMultiplicativeExpression node, Object data) {
	System.out.println(indentString() + node);
	++indent;
	data = node.childrenAccept(this, data);
	--indent;
	return data;
    }

    /*
     * (non-Javadoc)
     * 
     * @seemx.itesm.ddb.parser.SqlParserVisitor#visit(mx.itesm.ddb.parser.
     * ASTSQLExponentExpression, java.lang.Object)
     */
    @Override
    public Object visit(ASTSQLExponentExpression node, Object data) {
	System.out.println(indentString() + node);
	++indent;
	data = node.childrenAccept(this, data);
	--indent;
	return data;
    }

    /*
     * (non-Javadoc)
     * 
     * @seemx.itesm.ddb.parser.SqlParserVisitor#visit(mx.itesm.ddb.parser.
     * ASTSQLUnaryExpression, java.lang.Object)
     */
    @Override
    public Object visit(ASTSQLUnaryExpression node, Object data) {
	System.out.println(indentString() + node);
	++indent;
	data = node.childrenAccept(this, data);
	--indent;
	return data;
    }

    /*
     * (non-Javadoc)
     * 
     * @seemx.itesm.ddb.parser.SqlParserVisitor#visit(mx.itesm.ddb.parser.
     * ASTSQLPrimaryExpression, java.lang.Object)
     */
    @Override
    public Object visit(ASTSQLPrimaryExpression node, Object data) {
	System.out.println(indentString() + node);
	++indent;
	data = node.childrenAccept(this, data);
	--indent;
	return data;
    }

    /*
     * (non-Javadoc)
     * 
     * @seemx.itesm.ddb.parser.SqlParserVisitor#visit(mx.itesm.ddb.parser.
     * ASTSQLExpression, java.lang.Object)
     */
    @Override
    public Object visit(ASTSQLExpression node, Object data) {
	System.out.println(indentString() + node);
	++indent;
	data = node.childrenAccept(this, data);
	--indent;
	return data;
    }

    /*
     * (non-Javadoc)
     * 
     * @seemx.itesm.ddb.parser.SqlParserVisitor#visit(mx.itesm.ddb.parser.
     * ASTSQLAndExpression, java.lang.Object)
     */
    @Override
    public Object visit(ASTSQLAndExpression node, Object data) {
	System.out.println(indentString() + node);
	++indent;
	data = node.childrenAccept(this, data);
	--indent;
	return data;
    }

    /*
     * (non-Javadoc)
     * 
     * @seemx.itesm.ddb.parser.SqlParserVisitor#visit(mx.itesm.ddb.parser.
     * ASTSQLUnaryLogicalExpression, java.lang.Object)
     */
    @Override
    public Object visit(ASTSQLUnaryLogicalExpression node, Object data) {
	System.out.println(indentString() + node);
	++indent;
	data = node.childrenAccept(this, data);
	--indent;
	return data;
    }

    /*
     * (non-Javadoc)
     * 
     * @seemx.itesm.ddb.parser.SqlParserVisitor#visit(mx.itesm.ddb.parser.
     * ASTExistsClause, java.lang.Object)
     */
    @Override
    public Object visit(ASTExistsClause node, Object data) {
	System.out.println(indentString() + node);
	++indent;
	data = node.childrenAccept(this, data);
	--indent;
	return data;
    }

    /*
     * (non-Javadoc)
     * 
     * @seemx.itesm.ddb.parser.SqlParserVisitor#visit(mx.itesm.ddb.parser.
     * ASTSQLRelationalExpression, java.lang.Object)
     */
    @Override
    public Object visit(ASTSQLRelationalExpression node, Object data) {
	System.out.println(indentString() + node);
	++indent;
	data = node.childrenAccept(this, data);
	--indent;
	return data;
    }

    /*
     * (non-Javadoc)
     * 
     * @seemx.itesm.ddb.parser.SqlParserVisitor#visit(mx.itesm.ddb.parser.
     * ASTSQLExpressionList, java.lang.Object)
     */
    @Override
    public Object visit(ASTSQLExpressionList node, Object data) {
	System.out.println(indentString() + node);
	++indent;
	data = node.childrenAccept(this, data);
	--indent;
	return data;
    }

    /*
     * (non-Javadoc)
     * 
     * @seemx.itesm.ddb.parser.SqlParserVisitor#visit(mx.itesm.ddb.parser.
     * ASTSQLRelationalOperatorExpression, java.lang.Object)
     */
    @Override
    public Object visit(ASTSQLRelationalOperatorExpression node, Object data) {
	System.out.println(indentString() + node);
	++indent;
	data = node.childrenAccept(this, data);
	--indent;
	return data;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * mx.itesm.ddb.parser.SqlParserVisitor#visit(mx.itesm.ddb.parser.ASTSQLInClause
     * , java.lang.Object)
     */
    @Override
    public Object visit(ASTSQLInClause node, Object data) {
	System.out.println(indentString() + node);
	++indent;
	data = node.childrenAccept(this, data);
	--indent;
	return data;
    }

    /*
     * (non-Javadoc)
     * 
     * @seemx.itesm.ddb.parser.SqlParserVisitor#visit(mx.itesm.ddb.parser.
     * ASTSQLBetweenClause, java.lang.Object)
     */
    @Override
    public Object visit(ASTSQLBetweenClause node, Object data) {
	System.out.println(indentString() + node);
	++indent;
	data = node.childrenAccept(this, data);
	--indent;
	return data;
    }

    /*
     * (non-Javadoc)
     * 
     * @seemx.itesm.ddb.parser.SqlParserVisitor#visit(mx.itesm.ddb.parser.
     * ASTSQLLikeClause, java.lang.Object)
     */
    @Override
    public Object visit(ASTSQLLikeClause node, Object data) {
	System.out.println(indentString() + node);
	++indent;
	data = node.childrenAccept(this, data);
	--indent;
	return data;
    }

    /*
     * (non-Javadoc)
     * 
     * @seemx.itesm.ddb.parser.SqlParserVisitor#visit(mx.itesm.ddb.parser.
     * ASTIsNullClause, java.lang.Object)
     */
    @Override
    public Object visit(ASTIsNullClause node, Object data) {
	System.out.println(indentString() + node);
	++indent;
	data = node.childrenAccept(this, data);
	--indent;
	return data;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * mx.itesm.ddb.parser.SqlParserVisitor#visit(mx.itesm.ddb.parser.ASTFromClause
     * , java.lang.Object)
     */
    @Override
    public Object visit(ASTFromClause node, Object data) {
	System.out.println(indentString() + node);
	++indent;
	data = node.childrenAccept(this, data);
	--indent;
	return data;
    }

    /*
     * (non-Javadoc)
     * 
     * @seemx.itesm.ddb.parser.SqlParserVisitor#visit(mx.itesm.ddb.parser.
     * ASTQueryTableExpression, java.lang.Object)
     */
    @Override
    public Object visit(ASTQueryTableExpression node, Object data) {
	System.out.println(indentString() + node);
	++indent;
	data = node.childrenAccept(this, data);
	--indent;
	return data;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * mx.itesm.ddb.parser.SqlParserVisitor#visit(mx.itesm.ddb.parser.ASTWhereClause
     * , java.lang.Object)
     */
    @Override
    public Object visit(ASTWhereClause node, Object data) {
	System.out.println(indentString() + node);
	++indent;
	data = node.childrenAccept(this, data);
	--indent;
	return data;
    }
}
