/* Generated By:JJTree: Do not edit this line. ASTExistsClause.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=AST,NODE_EXTENDS=mx.itesm.ddb.util.SqlNode,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package mx.itesm.ddb.parser;



public
class ASTExistsClause extends SimpleNode {
  public ASTExistsClause(int id) {
    super(id);
  }

  public ASTExistsClause(SqlParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SqlParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=e43a26a639969c708f28506ffe7d752f (do not edit this line) */
