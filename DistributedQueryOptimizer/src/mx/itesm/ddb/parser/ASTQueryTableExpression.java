/* Generated By:JJTree: Do not edit this line. ASTQueryTableExpression.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=AST,NODE_EXTENDS=mx.itesm.ddb.util.SqlNode,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package mx.itesm.ddb.parser;

public
class ASTQueryTableExpression extends SimpleNode {
  public ASTQueryTableExpression(int id) {
    super(id);
  }

  public ASTQueryTableExpression(SqlParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SqlParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=f31df0ad39d8b00999d671c66b8cc126 (do not edit this line) */
