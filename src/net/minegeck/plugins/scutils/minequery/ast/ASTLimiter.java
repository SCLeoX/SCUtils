package net.minegeck.plugins.scutils.minequery.ast;

import net.minegeck.plugins.scutils.PlayerInterlayer;
import net.minegeck.plugins.scutils.minequery.PlayerCollection;
import net.minegeck.plugins.utils.Annotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class ASTLimiter extends ASTNode {

  @Override
  public ASTNodeType getType() {
    return ASTNodeType.LIMITER;
  }

  public enum ASTLimiterType {
    TOP("顶部") {
      @Override
      public PlayerCollection limit(PlayerCollection previous, int count) {
        int pSize = previous.size();
        if (pSize < count) {
          count = pSize;
        }
        PlayerCollection result = new PlayerCollection();
        Iterator<PlayerInterlayer> iter = previous.iterator();
        for (int i = 0; i < count; i++) {
          result.add(iter.next());
        }
        return result;
      }
    },

    RANDOM("随机") {
      @Override
      public PlayerCollection limit(PlayerCollection previous, int count) {
        ArrayList<PlayerInterlayer> players = new ArrayList<>(previous);
        Collections.shuffle(players);
        if (players.size() < count) {
          count = previous.size();
        }
        PlayerCollection result = new PlayerCollection();
        Iterator<PlayerInterlayer> iter = players.iterator();
        for (int i = 0; i < count; i++) {
          result.add(iter.next());
        }
        return result;
      }
    },

    BOTTOM("底部") {
      @Override
      public PlayerCollection limit(PlayerCollection previous, int count) {
        ArrayList<PlayerInterlayer> players = new ArrayList<>(previous);
        Collections.shuffle(players);
        if (players.size() < count) {
          count = previous.size();
        }
        PlayerCollection result = new PlayerCollection();
        Iterator<PlayerInterlayer> iter = new LinkedList<>(previous).descendingIterator();
        for (int i = 0; i < count; i++) {
          result.add(iter.next());
        }
        return result;
      }
    },

    ;

    private final String name;
    private ASTLimiterType(String name) {
      this.name = name;
    }
    public abstract PlayerCollection limit(PlayerCollection previous, int count);
    public String getName() {
      return this.name;
    }
  }

  public static ASTLimiterType getLimiterType(String name) {
    switch(name.toLowerCase()) {
      case "top":
        return ASTLimiterType.TOP;
      case "random":
        return ASTLimiterType.RANDOM;
      case "bottom":
        return ASTLimiterType.BOTTOM;
      default:
        return null;
    }
  }

  private final ASTLimiterType limiterType;

  public ASTLimiterType getLimiterType() {
    return limiterType;
  }

  public int getCount() {
    return count;
  }

  private final int count;
  public ASTLimiter(ASTLimiterType limiterType, int count) {
    this.limiterType = limiterType;
    this.count = count;
  }

  public PlayerCollection limit(PlayerCollection previous) {
    return this.limiterType.limit(previous, count);
  }

  @Override
  public void getString(StringBuilder sb, int indent, int indentEachLevel) {
    super.getString(sb, indent, indentEachLevel);
    indent++;
    sb.append(
      new String(new char[indent * indentEachLevel]).replace("\0", " "))
      .append("限制到 ")
      .append(this.limiterType.getName())
      .append(" 的 ")
      .append(this.count)
      .append(" 个玩家\n");
  }
}
