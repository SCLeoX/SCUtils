package net.minegeck.plugins.scutils.minequery.ast;

import net.minegeck.plugins.utils.Annotations;

import java.text.MessageFormat;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class ASTBinaryStringExpression extends ASTBinaryExpression<StringType> {

  @Override
  public BinaryExpressionType getBinaryExpressionType() {
    return this.binaryType;
  }

  public enum ASTBinaryStringExpressionType implements BinaryExpressionType<StringType> {

    CONCAT {
      @Override
      public StringType eval(AnyType left, AnyType right) {
        if (!(left instanceof StringCastable)) {
          throw new MineQueryRuntimeException(MessageFormat.format("无法将 连接 操作符左侧运算结果 [{0}] 转换为字符串型。 ", left.toString()));
        }
        if (!(right instanceof StringCastable)) {
          throw new MineQueryRuntimeException(MessageFormat.format("无法将 连接 操作符右侧运算结果 [{0}] 转换为字符串型。 ", right.toString()));
        }
        return new StringType(((StringCastable) left).castToString().get() + ((StringCastable) right).castToString().get());
      }

      @Override
      public int getPrecedence() {
        return 10;
      }

      @Override
      public String getName() {
        return "连接";
      }
    },
    ;

    @Override
    public ASTExpression createExpression(ASTExpression left, ASTExpression right) {
      return new ASTBinaryStringExpression(this, left, right);
    }
  }

  private final ASTBinaryStringExpressionType binaryType;
  private final ASTExpression left;
  private final ASTExpression right;

  public ASTBinaryStringExpression(ASTBinaryStringExpressionType binaryType, ASTExpression left, ASTExpression right) {
    this.binaryType = binaryType;
    this.left = left;
    this.right = right;
  }

  @Override
  public StringType eval(IdentifierProvider ip) {
    return binaryType.eval(left.eval(ip), right.eval(ip));
  }

  @Override
  public ASTNodeType getType() {
    return ASTNodeType.BINARY_STRING_EXPRESSION;
  }

  @Override
  public ASTExpression getLeft() {
    return this.left;
  }

  @Override
  public ASTExpression getRight() {
    return this.right;
  }


}
