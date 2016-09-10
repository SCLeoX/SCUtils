package net.minegeck.plugins.scutils.minequery.ast;

import net.minegeck.plugins.scutils.minequery.PlayerCollection;
import net.minegeck.plugins.utils.Annotations;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class ASTBinarySelector extends ASTSelector {
  public enum ASTBinarySelectorType {

    UNION("并集", 1) {
      @Override
      public PlayerCollection select(PlayerCollection previous, ASTSelector selectorA, ASTSelector selectorB) {
        return selectorA.select(previous).unionWith(selectorB.select(previous));
      }
    },

    INTERSECTION("交集", 2) {
      @Override
      public PlayerCollection select(PlayerCollection previous, ASTSelector selectorA, ASTSelector selectorB) {
        return selectorA.select(previous).intersectionWith(selectorB.select(previous));
      }
    },

    DIFF("差集", 1) {
      @Override
      public PlayerCollection select(PlayerCollection previous, ASTSelector selectorA, ASTSelector selectorB) {
        return selectorA.select(previous).diffWith(selectorB.select(previous));
      }
    },

    SYMMETRIC_DIFF("对等差集", 2) {
      @Override
      public PlayerCollection select(PlayerCollection previous, ASTSelector selectorA, ASTSelector selectorB) {
        return selectorA.select(previous).symmetricDiffWith(selectorB.select(previous));
      }
    },
    ;

    private final int precedence;
    private final String name;
    private ASTBinarySelectorType(String name, int precedence) {
      this.precedence = precedence;
      this.name = name;
    }
    public int getPrecedence() {
      return this.precedence;
    }
    public String getName() {
      return this.name;
    }
    public abstract PlayerCollection select(PlayerCollection previous, ASTSelector selectorA, ASTSelector selectorB);
  }

  public static ASTBinarySelectorType getASTBinarySelectorType(String operator) {
    switch(operator) {
      case "+":
        return ASTBinarySelectorType.UNION;
      case "-":
        return ASTBinarySelectorType.DIFF;
      case "*":
        return ASTBinarySelectorType.INTERSECTION;
      case "/":
        return ASTBinarySelectorType.SYMMETRIC_DIFF;
      default:
        return null;
    }
  }

  private final ASTBinarySelectorType binaryType;
  private final ASTSelector selectorA;
  private final ASTSelector selectorB;
  public ASTBinarySelector(ASTBinarySelectorType binaryType, ASTSelector selectorA, ASTSelector selectorB) {
    this.binaryType = binaryType;
    this.selectorA = selectorA;
    this.selectorB = selectorB;
  }

  public ASTBinarySelectorType getBinaryType() {
    return this.binaryType;
  }

  @Override
  public ASTNodeType getType() {
    return ASTNodeType.BINARY_SELECTOR;
  }

  @Override
  public PlayerCollection select(PlayerCollection previous) {
    return binaryType.select(previous, selectorA, selectorB);
  }

  @Override
  public void getString(StringBuilder sb, int indent, int indentEachLevel) {
    super.getString(sb, indent, indentEachLevel);
    indent++;
    sb.append(new String(new char[indent * indentEachLevel]).replace("\0", " ")).append("获得以下两个选择器的 ").append(this.binaryType.getName()).append("\n");
    selectorA.getString(sb, indent, indentEachLevel);
    selectorB.getString(sb, indent, indentEachLevel);
  }

}
