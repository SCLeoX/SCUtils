package net.minegeck.plugins.scutils.minequery.ast;

import net.minegeck.plugins.scutils.minequery.PlayerCollection;
import net.minegeck.plugins.utils.Annotations;

import java.util.ArrayList;
import java.util.Collection;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class ASTSelectorUnit extends ASTSelector {
  private final Collection<ASTSelector> selectors;
  private final Collection<ASTLimiter> limiters;

  public ASTSelectorUnit(ArrayList<ASTSelector> selectors, ArrayList<ASTLimiter> limiters) {
    this.selectors = selectors;
    this.limiters = limiters;
  }

  @Override
  public ASTNodeType getType() {
    return ASTNodeType.SELECTOR_UNIT;
  }

  @Override
  public PlayerCollection select(PlayerCollection previous) {
    for (ASTSelector selector : selectors) {
      previous = selector.select(previous);
    }
    for (ASTLimiter limiter : limiters) {
      previous = limiter.limit(previous);
    }
    return previous;
  }

  @Override
  public void getString(StringBuilder sb, int indent, int indentEachLevel) {
    super.getString(sb, indent, indentEachLevel);
    indent++;
    sb.append(new String(new char[indent * indentEachLevel]).replace("\0", " ")).append("符合以下所有条件的玩家\n");
    for (ASTSelector selector : selectors) {
      selector.getString(sb, indent + 1, indentEachLevel);
    }
    sb.append(new String(new char[indent * indentEachLevel]).replace("\0", " ")).append("将结果依次送入以下限制器中\n");
    for (ASTLimiter limiter : limiters) {
      limiter.getString(sb, indent + 1, indentEachLevel);
    }
  }

}
