package net.minegeck.plugins.scutils.minequery.ast;

import net.minegeck.plugins.utils.Annotations;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class ASTConstNumberExpression extends ASTConstExpression<NumberType> {

  private final NumberType value;

  public ASTConstNumberExpression(double value) {
    this.value = new NumberType(value);
  }

  @Override
  public NumberType eval(IdentifierProvider ip) {
    return value;
  }

  @Override
  public ASTNodeType getType() {
    return ASTNodeType.CONST_NUMBER_EXPRESSION;
  }

  @Override
  public NumberType getValue() {
    return value;
  }

}
