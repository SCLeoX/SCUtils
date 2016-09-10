package net.minegeck.plugins.scutils.minequery.ast;

import net.minegeck.plugins.utils.Annotations;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class ASTIdentifierExpression extends ASTExpression {

  private final String key;

  public ASTIdentifierExpression(String key) {
    this.key = key;
  }

  @Override
  public AnyType eval(IdentifierProvider ip) {
    return ip.get(key);
  }

  @Override
  public ASTNodeType getType() {
    return ASTNodeType.IDENTIFIER_EXPRESSION;
  }

  @Override
  public void getString(StringBuilder sb, int indent, int indentEachLevel) {
    super.getString(sb, indent, indentEachLevel);
    indent++;
    sb.append(new String(new char[indent * indentEachLevel]).replace("\0", " ")).append("名为 ").append(this.key).append(" 的变量\n");
  }

}
