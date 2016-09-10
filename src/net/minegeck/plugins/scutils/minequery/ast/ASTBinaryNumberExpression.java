package net.minegeck.plugins.scutils.minequery.ast;

import net.minegeck.plugins.utils.Annotations;

import java.text.MessageFormat;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class ASTBinaryNumberExpression extends ASTBinaryExpression<NumberType> {

  @Override
  public BinaryExpressionType getBinaryExpressionType() {
    return this.binaryType;
  }

  public enum ASTBinaryNumberExpressionType implements BinaryExpressionType<NumberType> {

    PLUS {
      @Override
      public NumberType eval(AnyType left, AnyType right) {
        if (!(left instanceof NumberCastable)) {
          throw new MineQueryRuntimeException(MessageFormat.format("无法将 加 操作符左侧运算结果 [{0}] 转换为数字型。 ", left.toString()));
        }
        if (!(right instanceof NumberCastable)) {
          throw new MineQueryRuntimeException(MessageFormat.format("无法将 加 操作符右侧运算结果 [{0}] 转换为数字型。 ", right.toString()));
        }
        return new NumberType(((NumberCastable) left).castToNumber().get() + ((NumberCastable) right).castToNumber().get());
      }

      @Override
      public int getPrecedence() {
        return 10;
      }

      @Override
      public String getName() {
        return "加";
      }
    },

    MINUS {
      @Override
      public NumberType eval(AnyType left, AnyType right) {
        if (!(left instanceof NumberCastable)) {
          throw new MineQueryRuntimeException(MessageFormat.format("无法将 减 操作符左侧运算结果 [{0}] 转换为数字型。 ", left.toString()));
        }
        if (!(right instanceof NumberCastable)) {
          throw new MineQueryRuntimeException(MessageFormat.format("无法将 减 操作符右侧运算结果 [{0}] 转换为数字型。 ", right.toString()));
        }
        return new NumberType(((NumberCastable) left).castToNumber().get() - ((NumberCastable) right).castToNumber().get());
      }

      @Override
      public int getPrecedence() {
        return 10;
      }

      @Override
      public String getName() {
        return "减";
      }
    },

    MULTIPLY {
      @Override
      public NumberType eval(AnyType left, AnyType right) {
        if (!(left instanceof NumberCastable)) {
          throw new MineQueryRuntimeException(MessageFormat.format("无法将 乘 操作符左侧运算结果 [{0}] 转换为数字型。 ", left.toString()));
        }
        if (!(right instanceof NumberCastable)) {
          throw new MineQueryRuntimeException(MessageFormat.format("无法将 乘 操作符右侧运算结果 [{0}] 转换为数字型。 ", right.toString()));
        }
        return new NumberType(((NumberCastable) left).castToNumber().get() * ((NumberCastable) right).castToNumber().get());
      }

      @Override
      public int getPrecedence() {
        return 20;
      }

      @Override
      public String getName() {
        return "乘";
      }
    },

    DIVID {
      @Override
      public NumberType eval(AnyType left, AnyType right) {
        if (!(left instanceof NumberCastable)) {
          throw new MineQueryRuntimeException(MessageFormat.format("无法将 除以 操作符左侧运算结果 [{0}] 转换为数字型。 ", left.toString()));
        }
        if (!(right instanceof NumberCastable)) {
          throw new MineQueryRuntimeException(MessageFormat.format("无法将 除以 操作符右侧运算结果 [{0}] 转换为数字型。 ", right.toString()));
        }
        return new NumberType(((NumberCastable) left).castToNumber().get() / ((NumberCastable) right).castToNumber().get());
      }

      @Override
      public int getPrecedence() {
        return 20;
      }

      @Override
      public String getName() {
        return "除以";
      }
    },

    MOD {
      @Override
      public NumberType eval(AnyType left, AnyType right) {
        if (!(left instanceof NumberCastable)) {
          throw new MineQueryRuntimeException(MessageFormat.format("无法将 取余 操作符左侧运算结果 [{0}] 转换为数字型。 ", left.toString()));
        }
        if (!(right instanceof NumberCastable)) {
          throw new MineQueryRuntimeException(MessageFormat.format("无法将 取余 操作符右侧运算结果 [{0}] 转换为数字型。 ", right.toString()));
        }
        return new NumberType(((NumberCastable) left).castToNumber().get() % ((NumberCastable) right).castToNumber().get());
      }

      @Override
      public int getPrecedence() {
        return 20;
      }

      @Override
      public String getName() {
        return "取余";
      }
    },

    INT_DIVID {
      @Override
      public NumberType eval(AnyType left, AnyType right) {
        if (!(left instanceof NumberCastable)) {
          throw new MineQueryRuntimeException(MessageFormat.format("无法将 整除 操作符左侧运算结果 [{0}] 转换为数字型。 ", left.toString()));
        }
        if (!(right instanceof NumberCastable)) {
          throw new MineQueryRuntimeException(MessageFormat.format("无法将 整除 操作符右侧运算结果 [{0}] 转换为数字型。 ", right.toString()));
        }
        return new NumberType(Math.floor(((NumberCastable) left).castToNumber().get() / ((NumberCastable) right).castToNumber().get()));
      }

      @Override
      public int getPrecedence() {
        return 20;
      }

      @Override
      public String getName() {
        return "整除";
      }
    },
    ;

    @Override
    public ASTExpression createExpression(ASTExpression left, ASTExpression right) {
      return new ASTBinaryNumberExpression(this, left, right);
    }
  }

  private final ASTBinaryNumberExpressionType binaryType;
  private final ASTExpression left;
  private final ASTExpression right;

  public ASTBinaryNumberExpression(ASTBinaryNumberExpressionType binaryType, ASTExpression left, ASTExpression right) {
    this.binaryType = binaryType;
    this.left = left;
    this.right = right;
  }

  @Override
  public ASTNodeType getType() {
    return ASTNodeType.BINARY_NUMBER_EXPRESSION;
  }

  @Override
  public NumberType eval(IdentifierProvider ip) {
    return binaryType.eval(left.eval(ip), right.eval(ip));
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
