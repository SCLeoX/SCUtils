package net.minegeck.plugins.scutils.minequery.ast;

import net.minegeck.plugins.utils.Annotations;

import java.text.MessageFormat;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class ASTBinaryBooleanExpression extends ASTBinaryExpression<BooleanType> {

  @Override
  public BinaryExpressionType getBinaryExpressionType() {
    return this.binaryType;
  }

  public enum ASTBinaryBooleanExpressionType implements BinaryExpressionType<BooleanType> {

    AND {
      @Override
      public BooleanType eval(AnyType left, AnyType right) {
        if (!(left instanceof BooleanCastable)) {
          throw new MineQueryRuntimeException(MessageFormat.format("无法将 与 操作符左侧运算结果 [{0}] 转换为布尔型。 ", left.toString()));
        }
        if (!((BooleanCastable) left).castToBoolean().get()) {
          return new BooleanType(false);
        }
        if (!(right instanceof BooleanCastable)) {
          throw new MineQueryRuntimeException(MessageFormat.format("无法将 与 操作符右侧运算结果 [{0}] 转换为布尔型。 ", right.toString()));
        }
        return new BooleanType(((BooleanCastable) right).castToBoolean().get());
      }

      @Override
      public int getPrecedence() {
        return 3;
      }

      @Override
      public String getName() {
        return "与";
      }
    },

    OR {
      @Override
      public BooleanType eval(AnyType left, AnyType right) {
        if (!(left instanceof BooleanCastable)) {
          throw new MineQueryRuntimeException(MessageFormat.format("无法将 或 操作符左侧运算结果 [{0}] 转换为布尔型。 ", left.toString()));
        }
        if (((BooleanCastable) left).castToBoolean().get()) {
          return new BooleanType(true);
        }
        if (!(right instanceof BooleanCastable)) {
          throw new MineQueryRuntimeException(MessageFormat.format("无法将 或 操作符右侧运算结果 [{0}] 转换为布尔型。 ", right.toString()));
        }
        return new BooleanType(((BooleanCastable) right).castToBoolean().get());
      }

      @Override
      public int getPrecedence() {
        return 2;
      }

      @Override
      public String getName() {
        return "或";
      }
    },

    LARGER {
      @Override
      public BooleanType eval(AnyType left, AnyType right) {
        if (!(left instanceof NumberCastable)) {
          throw new MineQueryRuntimeException(MessageFormat.format("无法将 大于 操作符左侧运算结果 [{0}] 转换为数字型。 ", left.toString()));
        }
        if (!(right instanceof NumberCastable)) {
          throw new MineQueryRuntimeException(MessageFormat.format("无法将 大于 操作符右侧运算结果 [{0}] 转换为数字型。 ", right.toString()));
        }
        return new BooleanType(((NumberCastable) left).castToNumber().get() > ((NumberCastable) right).castToNumber().get());
      }

      @Override
      public int getPrecedence() {
        return 7;
      }

      @Override
      public String getName() {
        return "大于";
      }
    },

    LARGER_OR_EQUAL {
      @Override
      public BooleanType eval(AnyType left, AnyType right) {
        if (!(left instanceof NumberCastable)) {
          throw new MineQueryRuntimeException(MessageFormat.format("无法将 大于或等于 操作符左侧运算结果 [{0}] 转换为数字型。 ", left.toString()));
        }
        if (!(right instanceof NumberCastable)) {
          throw new MineQueryRuntimeException(MessageFormat.format("无法将 大于或等于 操作符右侧运算结果 [{0}] 转换为数字型。 ", right.toString()));
        }
        return new BooleanType(((NumberCastable) left).castToNumber().get() >= ((NumberCastable) right).castToNumber().get());
      }

      @Override
      public int getPrecedence() {
        return 7;
      }

      @Override
      public String getName() {
        return "大于或等于";
      }
    },

    EQUAL {
      @Override
      public BooleanType eval(AnyType left, AnyType right) {
        if (!(left instanceof StringCastable)) {
          throw new MineQueryRuntimeException(MessageFormat.format("无法将 等于 操作符左侧运算结果 [{0}] 转换为字符串。 ", left.toString()));
        }
        if (!(right instanceof StringCastable)) {
          throw new MineQueryRuntimeException(MessageFormat.format("无法将 等于 操作符右侧运算结果 [{0}] 转换为字符串。 ", right.toString()));
        }
        return new BooleanType(((StringCastable) left).castToString().get().equals(((StringCastable) right).castToString().get()));
      }

      @Override
      public int getPrecedence() {
        return 7;
      }

      @Override
      public String getName() {
        return "等于";
      }
    },

    UNEQUAL {
      @Override
      public BooleanType eval(AnyType left, AnyType right) {
        if (!(left instanceof StringCastable)) {
          throw new MineQueryRuntimeException(MessageFormat.format("无法将 不等于 操作符左侧运算结果 [{0}] 转换为字符串。 ", left.toString()));
        }
        if (!(right instanceof StringCastable)) {
          throw new MineQueryRuntimeException(MessageFormat.format("无法将 不等于 操作符右侧运算结果 [{0}] 转换为字符串。 ", right.toString()));
        }
        return new BooleanType(!((StringCastable) left).castToString().get().equals(((StringCastable) right).castToString().get()));
      }

      @Override
      public int getPrecedence() {
        return 7;
      }

      @Override
      public String getName() {
        return "不等于";
      }
    },

    LESS_OR_EQUAL {
      @Override
      public BooleanType eval(AnyType left, AnyType right) {
        if (!(left instanceof NumberCastable)) {
          throw new MineQueryRuntimeException(MessageFormat.format("无法将 小于或等于 操作符左侧运算结果 [{0}] 转换为数字型。 ", left.toString()));
        }
        if (!(right instanceof NumberCastable)) {
          throw new MineQueryRuntimeException(MessageFormat.format("无法将 小于或等于 操作符右侧运算结果 [{0}] 转换为数字型。 ", right.toString()));
        }
        return new BooleanType(((NumberCastable) left).castToNumber().get() <= ((NumberCastable) right).castToNumber().get());
      }

      @Override
      public int getPrecedence() {
        return 7;
      }

      @Override
      public String getName() {
        return "小于或等于";
      }
    },

    LESS {
      @Override
      public BooleanType eval(AnyType left, AnyType right) {
        if (!(left instanceof NumberCastable)) {
          throw new MineQueryRuntimeException(MessageFormat.format("无法将 小于 操作符左侧运算结果 [{0}] 转换为数字型。 ", left.toString()));
        }
        if (!(right instanceof NumberCastable)) {
          throw new MineQueryRuntimeException(MessageFormat.format("无法将 小于 操作符右侧运算结果 [{0}] 转换为数字型。 ", right.toString()));
        }
        return new BooleanType(((NumberCastable) left).castToNumber().get() < ((NumberCastable) right).castToNumber().get());
      }

      @Override
      public int getPrecedence() {
        return 7;
      }

      @Override
      public String getName() {
        return "小于";
      }
    },
    ;

    @Override
    public ASTExpression createExpression(ASTExpression left, ASTExpression right) {
      return new ASTBinaryBooleanExpression(this, left, right);
    }
  }

  private final ASTBinaryBooleanExpressionType binaryType;
  private final ASTExpression left;
  private final ASTExpression right;

  public ASTBinaryBooleanExpression(ASTBinaryBooleanExpressionType binaryType, ASTExpression left, ASTExpression right) {
    this.binaryType = binaryType;
    this.left = left;
    this.right = right;
  }

  @Override
  public ASTNodeType getType() {
    return ASTNodeType.BINARY_BOOLEAN_EXPRESSION;
  }

  @Override
  public BooleanType eval(IdentifierProvider ip) {
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
