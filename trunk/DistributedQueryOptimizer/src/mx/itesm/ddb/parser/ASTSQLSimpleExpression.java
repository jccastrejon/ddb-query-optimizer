/* Generated By:JJTree: Do not edit this line. ASTSQLSimpleExpression.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package mx.itesm.ddb.parser;

public
class ASTSQLSimpleExpression extends SimpleNode {
  public ASTSQLSimpleExpression(int id) {
    super(id);
  }

  public ASTSQLSimpleExpression(SqlParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SqlParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=23006092df250732a255d4ba9337b397 (do not edit this line) */