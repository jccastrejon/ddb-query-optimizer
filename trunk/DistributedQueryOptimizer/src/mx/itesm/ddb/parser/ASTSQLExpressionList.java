/* Generated By:JJTree: Do not edit this line. ASTSQLExpressionList.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package mx.itesm.ddb.parser;

public
class ASTSQLExpressionList extends SimpleNode {
  public ASTSQLExpressionList(int id) {
    super(id);
  }

  public ASTSQLExpressionList(SqlParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SqlParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=fdda0e6de59b6f31ff4d66a82e38668a (do not edit this line) */