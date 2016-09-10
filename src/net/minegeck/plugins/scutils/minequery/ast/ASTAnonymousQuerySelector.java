package net.minegeck.plugins.scutils.minequery.ast;

import net.minegeck.plugins.scutils.PlayerInterlayer;
import net.minegeck.plugins.scutils.minequery.PlayerCollection;
import net.minegeck.plugins.utils.Annotations;

import java.util.Collection;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class ASTAnonymousQuerySelector extends ASTSelector {
  private final Collection<ASTSelector> selectors;
  public ASTAnonymousQuerySelector(Collection<ASTSelector> selectors) {
    this.selectors = selectors;
  }

  @Override
  public ASTNodeType getType() {
    return ASTNodeType.ANONYMOUS_QUERY_SELECTOR;
  }

  @Override
  public PlayerCollection select(PlayerCollection previous) {
    PlayerCollection collection = new PlayerCollection();
    if (selectors.isEmpty()) {
      return PlayerInterlayer.getOnlinePlayers();
    }
    for (ASTSelector selector : selectors) {
      collection.unionWith(selector.select(previous));
    }
    return collection;
  }

  @Override
  public void getString(StringBuilder sb, int indent, int indentEachLevel) {
    super.getString(sb, indent, indentEachLevel);
    indent++;
    sb.append(new String(new char[indent * indentEachLevel]).replace("\0", " ")).append("符合以下任意一个条件的玩家\n");
    for (ASTSelector selector : selectors) {
      selector.getString(sb, indent, indentEachLevel);
    }
  }

}
