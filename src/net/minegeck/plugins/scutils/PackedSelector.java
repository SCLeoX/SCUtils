package net.minegeck.plugins.scutils;

import net.minegeck.plugins.scutils.minequery.ast.ASTParser;
import net.minegeck.plugins.scutils.minequery.ast.ASTSelector;
import net.minegeck.plugins.utils.Annotations;
import net.minegeck.plugins.utils.confighelper.Config;
import net.minegeck.plugins.utils.confighelper.HasPostLoadMethod;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
@Config.CustomConfigable
public final class PackedSelector implements HasPostLoadMethod {

  @Config.ConfigableProperty(Key = "内容")
  public String source;

  @Config.ConfigableProperty(Key = "类型", DefaultString = "查询选择器")
  public String type;

  public String getSource() {
    return source;
  }

  private ASTSelector selector;

  public ASTSelector getSelector() {
    return selector;
  }

  public PackedSelector() {}
  public PackedSelector(String source) {
    this.source = source;
    this.postLoad();
  }

  @Override
  public void postLoad() {
    this.selector = new ASTParser(source).parseToSelector();
  }
}
