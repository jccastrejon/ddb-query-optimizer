/* Generated By:JJTree: Do not edit this line. ASTSQLRelationalExpression.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package mx.itesm.ddb.parser;

public
class ASTSQLRelationalExpression extends SimpleNode {
  public ASTSQLRelationalExpression(int id) {
    super(id);
  }

  public ASTSQLRelationalExpression(SqlParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SqlParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=118095e8fada5b8b095cd3250cc36307 (do not edit this line) */