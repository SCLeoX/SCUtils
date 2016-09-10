package net.minegeck.plugins.scutils.minequery.ast;

import net.minegeck.plugins.scutils.PlayerInterlayer;
import net.minegeck.plugins.scutils.minequery.PlayerCollection;
import net.minegeck.plugins.utils.Annotations;

import java.util.Collection;
import java.util.Iterator;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class ASTClassSelector extends ASTSelector {

  private final String target;
  public ASTClassSelector(String target) {
    this.target = target;
  }

  @Override
  public ASTNodeType getType() {
    return ASTNodeType.CLASS_SELECTOR;
  }

  @Override
  public PlayerCollection select(PlayerCollection previous) {
    if (previous == null) {
      Collection<PlayerInterlayer> players = PlayerInterlayer.getPlayersByClass(target);
      if (players == null) {
        return new PlayerCollection();
      } else {
        return new PlayerCollection(players);
      }
    } else {
      Iterator<PlayerInterlayer> iter = previous.iterator();
      while(iter.hasNext()) {
        PlayerInterlayer player = iter.next();
        if (!player.hasClass(target)) {
          iter.remove();
        }
      }
      return previous;
    }
  }

  @Override
  public void getString(StringBuilder sb, int indent, int indentEachLevel) {
    super.getString(sb, indent, indentEachLevel);
    indent++;
    sb.append(new String(new char[indent * indentEachLevel]).replace("\0", " ")).append("类含有 ").append(this.target).append(" 的玩家\n");
  }

}
