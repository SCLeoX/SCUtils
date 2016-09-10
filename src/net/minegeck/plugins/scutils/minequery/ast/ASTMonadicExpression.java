package net.minegeck.plugins.scutils.minequery.ast;

import net.minegeck.plugins.utils.Annotations;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public abstract class ASTMonadicExpression<T extends AnyType> extends ASTExpression<T> {
  public abstract MonadicExpressionType getMonadicExpressionType();
  public abstract ASTExpression getInput();

  @Override
  public void getString(StringBuilder sb, int indent, int indentEachLevel) {
    super.getString(sb, indent, indentEachLevel);
    indent++;
    sb.append(new String(new char[indent * indentEachLevel]).replace("\0", " "))
      .append("使用运算符 ")
      .append(this.getMonadicExpressionType().getName())
      .append(" 对这个输入进行运算\n");
    this.getInput().getString(sb, indent, indentEachLevel);
  }
}
