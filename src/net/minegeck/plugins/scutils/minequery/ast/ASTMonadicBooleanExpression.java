package net.minegeck.plugins.scutils.minequery.ast;

import net.minegeck.plugins.utils.Annotations;

import java.text.MessageFormat;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class ASTMonadicBooleanExpression extends ASTMonadicExpression<BooleanType> {

  @Override
  public MonadicExpressionType getMonadicExpressionType() {
    return this.monadicType;
  }

  @Override
  public ASTExpression getInput() {
    return this.input;
  }
  
  public enum ASTMonadicBooleanExpressionType implements MonadicExpressionType<BooleanType> {

    NOT {
      @Override
      public BooleanType eval(AnyType input) {
        if (!(input instanceof BooleanCastable)) {
          throw new MineQueryRuntimeException(MessageFormat.format("无法将 非 操作符右侧运算结果 [{0}] 转换为布尔型。 ", input.toString()));
        }
        return new BooleanType(!((BooleanCastable) input).castToBoolean().get());
      }

      @Override
      public String getName() {
        return "非";
      }
    },
    ;

  }

  private final ASTMonadicBooleanExpressionType monadicType;
  private final ASTExpression input;

  public ASTMonadicBooleanExpression(ASTMonadicBooleanExpressionType monadicType, ASTExpression input) {
    this.monadicType = monadicType;
    this.input = input;
  }

  @Override
  public ASTNodeType getType() {
    return ASTNodeType.MONADIC_BOOLEAN_EXPRESSION;
  }

  @Override
  public BooleanType eval(IdentifierProvider ip) {
    return monadicType.eval(input.eval(ip));
  }

}
