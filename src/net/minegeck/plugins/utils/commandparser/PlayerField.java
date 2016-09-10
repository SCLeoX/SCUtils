package net.minegeck.plugins.utils.commandparser;

import net.minegeck.plugins.scutils.PlayerInterlayer;
import net.minegeck.plugins.scutils.minequery.MineQuerySyntaxException;
import net.minegeck.plugins.scutils.minequery.PlayerCollection;
import net.minegeck.plugins.scutils.minequery.ast.ASTParser;
import net.minegeck.plugins.scutils.minequery.ast.ASTSelector;
import net.minegeck.plugins.scutils.minequery.ast.MineQueryRuntimeException;
import net.minegeck.plugins.utils.Annotations;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class PlayerField extends FieldLoader {

  public static PlayerField any() {
    return new PlayerField(1, Integer.MAX_VALUE, "任意数量个玩家");
  }

  public static PlayerField one() {
    return new PlayerField(1, 1, "一个玩家");
  }

  public static PlayerField atLeast(int min) {
    return new PlayerField(min, Integer.MAX_VALUE, MessageFormat.format("至少 {0} 个玩家", min));
  }

  public static PlayerField atMost(int max) {
    return new PlayerField(1, max, MessageFormat.format("至多 {0} 个玩家", max));
  }

  public static PlayerField ranged(int min, int max) {
    return new PlayerField(min, max, MessageFormat.format("{0} - {1} 个玩家", max));
  }

  private final int minPlayer;
  private final int maxPlayer;
  private final String type;

  private PlayerField(int minPlayer, int maxPlayer, String type) {
    this.minPlayer = minPlayer;
    this.maxPlayer = maxPlayer;
    this.type = type;
  };

  private int unfitnessMissing = 5;
  private int unfitnessPlayerNotExist = 4;
  private int unfitnessMineQueryException = 3;
  private int unfitnessCountNotCorrect = 1;

  /**
   * 设置这个 Field 的在各个情况的不符合指数。
   *
   * @param unfitnessMissing 如果这个 Field 不存在，默认值是 5。
   * @param unfitnessPlayerNotExist 如果这个玩家不存在，默认值是 4。
   * @param unfitnessMineQueryException 如果运行 MineQuery 时发生异常，默认值是 3。
   * @param unfitnessCountNotCorrect 如果选择的玩家数量不正确。
   * @return
   */
  public PlayerField withUnfitnessScaleAt(int unfitnessMissing, int unfitnessPlayerNotExist, int unfitnessMineQueryException, int unfitnessCountNotCorrect) {
    this.unfitnessMissing = unfitnessMissing;
    this.unfitnessPlayerNotExist = unfitnessPlayerNotExist;
    this.unfitnessMineQueryException = unfitnessMineQueryException;
    this.unfitnessCountNotCorrect = unfitnessCountNotCorrect;
    return this;
  }

  @Override
  public void load(ParseData data) {
    String first = data.peek();
    if (first == null) {
      data.next();
      data.unfit(unfitnessMissing, MessageFormat.format("第 {0} 个参数应选择{1}。", data.getLastParamPos(), type));
      return;
    }
    PlayerCollection players;
    if (!".#$[".contains(first.substring(0, 1))) {
      //玩家名
      Player player = Bukkit.getPlayer(first);
      data.next();
      if (player == null) {
        data.unfit(unfitnessPlayerNotExist, MessageFormat.format("对于第 {0} 个参数, 玩家 {1} 不存在。", data.getLastParamPos(), first));
        return;
      }
      players = new PlayerCollection();
      players.add(PlayerInterlayer.getInstanceFor(player));
    } else {
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
      } while (data.peek() != null && !(parentheses <= 0 && brackets <= 0 && !inQuote));
      ASTSelector selector;
      try {
        selector = new ASTParser(sb.toString()).parseToSelector();
      } catch(MineQuerySyntaxException ex) {
        data.unfit(unfitnessMineQueryException, ex.getFullMessage());
        return;
      }
      try {
        players = selector.select();
      } catch(MineQueryRuntimeException ex) {
        data.unfit(unfitnessMineQueryException, MessageFormat.format("对于第 {0} 个参数, MineQuery 执行失败: {1}", data.getLastParamPos(), ex.getMessage()));
        return;
      }
    }
    int size = players.size();
    if (size < minPlayer || size > maxPlayer) {
      data.unfit(unfitnessCountNotCorrect, MessageFormat.format("对于第 {0} 个参数, 选择了 {1} 个玩家。应该选择{2}。", data.getLastParamPos(), size, type));
      return;
    }
    data.putResult(players);
  }

}
