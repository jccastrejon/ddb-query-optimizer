/* Generated By:JJTree: Do not edit this line. ASTSQLBetweenClause.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=AST,NODE_EXTENDS=mx.itesm.ddb.util.SqlNode,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package mx.itesm.ddb.parser;



public
class ASTSQLBetweenClause extends SimpleNode {
  public ASTSQLBetweenClause(int id) {
    super(id);
  }

  public ASTSQLBetweenClause(SqlParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SqlParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=f7587b9faec98d4db7ac9672be9bcb4d (do not edit this line) */
