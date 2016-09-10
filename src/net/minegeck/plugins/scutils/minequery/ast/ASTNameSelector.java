package net.minegeck.plugins.scutils.minequery.ast;

import net.minegeck.plugins.scutils.PlayerInterlayer;
import net.minegeck.plugins.scutils.minequery.PlayerCollection;
import net.minegeck.plugins.utils.Annotations;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class ASTNameSelector extends ASTSelector {

  private final String target;
  public ASTNameSelector(String target) {
    this.target = target;
  }

  @Override
  public ASTNodeType getType() {
    return ASTNodeType.NAME_SELECTOR;
  }

  @Override
  public PlayerCollection select(PlayerCollection previous) {
    PlayerInterlayer player = PlayerInterlayer.getPlayerByName(target);
    if (player == null) {
      return new PlayerCollection();
    } else {
      if (previous == null) {
        PlayerCollection collection = new PlayerCollection();
        collection.add(player);
        return collection;
      } else {
        if (previous.contains(player)) {
          PlayerCollection collection = new PlayerCollection();
          collection.add(player);
          return collection;
        } else {
          return new PlayerCollection();
        }
      }
    }
  }

  @Override
  public void getString(StringBuilder sb, int indent, int indentEachLevel) {
    super.getString(sb, indent, indentEachLevel);
    indent++;
    sb.append(new String(new char[indent * indentEachLevel]).replace("\0", " ")).append("名字是 ").append(this.target).append(" 的玩家\n");
  }

}
