package net.minegeck.plugins.utils.commandparser;

import net.minegeck.plugins.utils.Annotations;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public abstract class FieldLoader {
  public abstract void load(ParseData data);
  public String getFieldName() {
    return "<参数>";
  }
}
