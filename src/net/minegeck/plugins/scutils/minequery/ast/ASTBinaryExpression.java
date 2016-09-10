package net.minegeck.plugins.scutils.minequery.ast;

import net.minegeck.plugins.utils.Annotations;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public abstract class ASTBinaryExpression<T extends AnyType> extends ASTExpression<T> {
  public abstract BinaryExpressionType getBinaryExpressionType();
  public abstract ASTExpression getLeft();
  public abstract ASTExpression getRight();
  @Override
  public void getString(StringBuilder sb, int indent, int indentEachLevel) {
    super.getString(sb, indent, indentEachLevel);
    indent++;
    sb.append(new String(new char[indent * indentEachLevel]).replace("\0", " "))
      .append("使用运算符 ")
      .append(this.getBinaryExpressionType().getName())
      .append(" 对这两个输入进行运算\n");
    this.getLeft().getString(sb, indent, indentEachLevel);
    this.getRight().getString(sb, indent, indentEachLevel);
  }
}
