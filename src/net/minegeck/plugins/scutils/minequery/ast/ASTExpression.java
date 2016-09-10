package net.minegeck.plugins.scutils.minequery.ast;

import net.minegeck.plugins.utils.Annotations;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public abstract class ASTExpression<T extends AnyType> extends ASTNode {
  public abstract T eval(IdentifierProvider ip);
}
