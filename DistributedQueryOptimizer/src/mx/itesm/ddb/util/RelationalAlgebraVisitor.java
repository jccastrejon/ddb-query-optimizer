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
import mx.itesm.ddb.parser.Token;

/**
 * @author jccastrejon
 * 
 */
public class RelationalAlgebraVisitor implements SqlParserVisitor {

    @Override
    public Object visit(SimpleNode node, Object data) {
	return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTQueryStatement node, Object data) {
	return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTQuery node, Object data) {
	return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTSelectList node, Object data) {
	Token currentToken;

	// Recover result attributes
	currentToken = node.jjtGetFirstToken();

	System.out.print("\u03A0<sub>");

	while (currentToken != node.jjtGetLastToken()) {
	    System.out.print(currentToken.image);
	    currentToken = currentToken.next;
	}

	System.out.print(currentToken.image);
	System.out.print("</sub>");

	return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTSelectItem node, Object data) {
	return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTSQLSimpleExpression node, Object data) {
	return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTSQLMultiplicativeExpression node, Object data) {
	return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTSQLExponentExpression node, Object data) {
	return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTSQLUnaryExpression node, Object data) {
	return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTSQLPrimaryExpression node, Object data) {
	return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTSQLExpression node, Object data) {
	return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTSQLAndExpression node, Object data) {
	return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTSQLUnaryLogicalExpression node, Object data) {
	return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTExistsClause node, Object data) {
	return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTSQLRelationalExpression node, Object data) {
	return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTSQLExpressionList node, Object data) {
	return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTSQLRelationalOperatorExpression node, Object data) {
	return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTSQLInClause node, Object data) {
	return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTSQLBetweenClause node, Object data) {
	return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTSQLLikeClause node, Object data) {
	return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTIsNullClause node, Object data) {
	return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTFromClause node, Object data) {
	Token currentToken;
	ASTQueryTableExpression currentRelation;

	// Recover relations
	System.out.print("(");
	for (int i = 0; i < node.jjtGetNumChildren(); i++) {
	    currentRelation = (ASTQueryTableExpression) node.jjtGetChild(i);
	    currentToken = currentRelation.jjtGetFirstToken();

	    while (currentToken != currentRelation.jjtGetLastToken()) {
		System.out.print(currentToken.image);
		currentToken = currentToken.next;
	    }

	    System.out.print(currentToken.image);
	    if (i != (node.jjtGetNumChildren() - 1)) {
		System.out.print("\u22C8");
	    }
	}
	System.out.print(")");

	return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTQueryTableExpression node, Object data) {
	return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTWhereClause node, Object data) {
	return node.childrenAccept(this, data);
    }
}
