package net.minegeck.plugins.scutils.minequery.ast;

import net.minegeck.plugins.utils.Annotations;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public interface BinaryExpressionType<T extends AnyType> {
  public T eval(AnyType left, AnyType right);
  public int getPrecedence();
  public ASTExpression createExpression(ASTExpression left, ASTExpression right);
  public String getName();
}
