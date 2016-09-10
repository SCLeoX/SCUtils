package net.minegeck.plugins.scutils.minequery.ast;

import net.minegeck.plugins.scutils.PlayerInterlayer;
import net.minegeck.plugins.scutils.minequery.PlayerCollection;
import net.minegeck.plugins.utils.Annotations;

import java.text.MessageFormat;
import java.util.Iterator;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class ASTAttributeSelector extends ASTSelector {

  private final ASTExpression criteria;

  public ASTAttributeSelector(ASTExpression criteria) {
    this.criteria = criteria;
  }

  @Override
  public ASTNodeType getType() {
    return ASTNodeType.ATTRIBUTE_SELECTOR;
  }

  @Override
  public PlayerCollection select(PlayerCollection previous) {
    if (previous == null) {
      previous = PlayerInterlayer.getOnlinePlayers();
    }
    Iterator<PlayerInterlayer> iter = previous.iterator();
    while (iter.hasNext()) {
      PlayerInterlayer player = iter.next();
      AnyType result = criteria.eval(player.getIdentifierProvider());
      if (!(result instanceof BooleanCastable)) {
        throw new MineQueryRuntimeException(MessageFormat.format("无法将值 [{0}] 作为属性选择器的判据。", result.get().toString()));
      }
      if (!((BooleanCastable) result).castToBoolean().get()) {
        iter.remove();
      }
    }
    return previous;
  }

  @Override
  public void getString(StringBuilder sb, int indent, int indentEachLevel) {
    super.getString(sb, indent, indentEachLevel);
    indent++;
    sb.append(new String(new char[indent * indentEachLevel]).replace("\0", " "))
      .append("若这个玩家的信息能使以下表达式运算结果为真\n");
    this.criteria.getString(sb, indent, indentEachLevel);
  }

}
