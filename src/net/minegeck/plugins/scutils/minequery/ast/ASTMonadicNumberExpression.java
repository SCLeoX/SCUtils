package net.minegeck.plugins.scutils.minequery.ast;

import net.minegeck.plugins.utils.Annotations;

import java.text.MessageFormat;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class ASTMonadicNumberExpression extends ASTMonadicExpression<NumberType> {

  @Override
  public MonadicExpressionType getMonadicExpressionType() {
    return this.monadicType;
  }

  @Override
  public ASTExpression getInput() {
    return this.input;
  }

  public enum ASTMonadicNumberExpressionType implements MonadicExpressionType<NumberType> {

    NEGATIVE {
      @Override
      public NumberType eval(AnyType input) {
        if (!(input instanceof NumberCastable)) {
          throw new MineQueryRuntimeException(MessageFormat.format("无法将 取负 操作符右侧运算结果 [{0}] 转换为数字型。 ", input.toString()));
        }
        return new NumberType(-((NumberCastable) input).castToNumber().get());
      }

      @Override
      public String getName() {
        return "取负";
      }
    },
    ;

  }

  private final ASTMonadicNumberExpressionType monadicType;
  private final ASTExpression input;

  public ASTMonadicNumberExpression(ASTMonadicNumberExpressionType monadicType, ASTExpression input) {
    this.monadicType = monadicType;
    this.input = input;
  }

  @Override
  public ASTNodeType getType() {
    return ASTNodeType.MONADIC_NUMBER_EXPRESSION;
  }

  @Override
  public NumberType eval(IdentifierProvider ip) {
    return monadicType.eval(input.eval(ip));
  }

}