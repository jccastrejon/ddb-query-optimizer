/* Generated By:JJTree: Do not edit this line. ASTSQLMultiplicativeExpression.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package mx.itesm.ddb.parser;

public
class ASTSQLMultiplicativeExpression extends SimpleNode {
  public ASTSQLMultiplicativeExpression(int id) {
    super(id);
  }

  public ASTSQLMultiplicativeExpression(SqlParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SqlParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=f42f34c6bfc991f1009aaa2e8ecd89a7 (do not edit this line) */
