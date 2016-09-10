package net.minegeck.plugins.scutils.minequery.ast;

import net.minegeck.plugins.scutils.Main;
import net.minegeck.plugins.scutils.PackedSelector;
import net.minegeck.plugins.scutils.minequery.PlayerCollection;
import net.minegeck.plugins.utils.Annotations;

import java.text.MessageFormat;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class ASTQuerySelector extends ASTSelector {

  private final String name;

  public ASTQuerySelector(String name) {
    this.name = name;
  }

  @Override
  public PlayerCollection select(PlayerCollection previous) {
    PackedSelector pSelector = Main.configRoot.selectors.get(name);
    if (pSelector == null) {
      throw new MineQueryRuntimeException(MessageFormat.format("找不到名为 <{0}> 的查询选择器。", name));
    }
    return pSelector.getSelector().select(previous);
  }

  @Override
  public ASTNodeType getType() {
    return ASTNodeType.QUERY_SELECTOR;
  }

  @Override
  public void getString(StringBuilder sb, int indent, int indentEachLevel) {
    super.getString(sb, indent, indentEachLevel);
    indent++;
    sb.append(new String(new char[indent * indentEachLevel]).replace("\0", " "))
      .append("调用名为 ")
      .append(this.name)
      .append(" 的选择器\n");
  }

}
