package net.minegeck.plugins.scutils.minequery.ast;

import net.minegeck.plugins.utils.Annotations;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class ASTConstStringExpression extends ASTConstExpression<StringType> {

  private final StringType value;

  public ASTConstStringExpression(String value) {
    this.value = new StringType(value);
  }

  @Override
  public StringType eval(IdentifierProvider ip) {
    return value;
  }

  @Override
  public ASTNodeType getType() {
    return ASTNodeType.CONST_STRING_EXPRESSION;
  }

  @Override
  public StringType getValue() {
    return this.value;
  }

}
