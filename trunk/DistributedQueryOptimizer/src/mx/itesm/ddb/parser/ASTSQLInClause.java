/* Generated By:JJTree: Do not edit this line. ASTSQLInClause.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=AST,NODE_EXTENDS=mx.itesm.ddb.util.SqlNode,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package mx.itesm.ddb.parser;



public
class ASTSQLInClause extends SimpleNode {
  public ASTSQLInClause(int id) {
    super(id);
  }

  public ASTSQLInClause(SqlParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SqlParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=73581c089d52ee2b919332a231e53dfd (do not edit this line) */