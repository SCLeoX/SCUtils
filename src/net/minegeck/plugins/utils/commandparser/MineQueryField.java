package net.minegeck.plugins.utils.commandparser;


import net.minegeck.plugins.scutils.minequery.MineQuerySyntaxException;
import net.minegeck.plugins.scutils.minequery.ast.ASTNode;
import net.minegeck.plugins.scutils.minequery.ast.ASTParser;
import net.minegeck.plugins.utils.Annotations;

import java.text.MessageFormat;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class MineQueryField extends FieldLoader {

  public static MineQueryField expression() {
    return new MineQueryField(true);
  }

  public static MineQueryField selector() {
    return new MineQueryField(false);
  }

  private MineQueryField(boolean isExpression) {
    this.isExpression = isExpression;
  };

  private final boolean isExpression;
  private int unfitnessMissing = 5;
  private int unfitnessMineQueryException = 3;

  private boolean toEnd = false;

  /**
   * 设置这个 Field 的在各个情况的不符合指数。
   *
   * @param unfitnessMissing 如果这个 Field 不存在，默认值是 5。
   * @param unfitnessMineQueryException 如果运行 MineQuery 时发生异常，默认值是 3。
   * @return
   */
  public MineQueryField withUnfitnessScaleAt(int unfitnessMissing,int unfitnessMineQueryException) {
    this.unfitnessMissing = unfitnessMissing;
    this.unfitnessMineQueryException = unfitnessMineQueryException;
    return this;
  }

  public MineQueryField toEnd() {
    this.toEnd = true;
    return this;
  }

  @Override
  public void load(ParseData data) {
    if (data.peek() == null) {
      data.unfit(unfitnessMissing, MessageFormat.format("第 {0} 个参数应该是一个{1}。", data.getLastParamPos() + 1, this.isExpression ? "表达式" : "选择器"));
      return;
    }
    int parentheses = 0;
    int brackets = 0;
    boolean inQuote = false;
    boolean escape = false;
    StringBuilder sb = new StringBuilder();
    boolean isFirst = true;
    do {
      if (!isFirst) {
        data.incConcat();
      } else {
        isFirst = false;
      }
      String str = data.next();
      sb.append(str).append(' ');
      for (char ch : str.toCharArray()) {
        if (escape) {
          escape = false;
          continue;
        }
        switch (ch) {
          case '(':
            parentheses++;
            break;
          case ')':
            parentheses--;
            break;
          case '[':
            brackets++;
            break;
          case ']':
            brackets--;
            break;
          case '"':
            inQuote = !inQuote;
            break;
          case '\\':
            escape = true;
            break;
        }
      }
    } while (data.peek() != null && (toEnd || !(parentheses <= 0 && brackets <= 0 && !inQuote)));
    ASTNode node;
    try {
      ASTParser parser = new ASTParser(sb.toString());
      if (isExpression) {
        node = parser.parseToExpression();
      } else {
        node = parser.parseToSelector();
      }
    } catch(MineQuerySyntaxException ex) {
      data.unfit(unfitnessMineQueryException, ex.getFullMessage());
      return;
    }
    data.putResult(node);
  }

}
