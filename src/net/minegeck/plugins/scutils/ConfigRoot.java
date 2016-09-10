package net.minegeck.plugins.scutils;

import net.minegeck.plugins.utils.Annotations;
import net.minegeck.plugins.utils.confighelper.Config;
import net.minegeck.plugins.utils.confighelper.ConfigableMap;
import net.minegeck.plugins.utils.confighelper.ConfigableStringList;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
@Config.CustomConfigable
public class ConfigRoot {

  @Config.ConfigableProperty(Key = "为所有命令启用 MineQuery 选择器", DefaultBoolean = true)
  public boolean enableGlobalQuerySelector;

  @Config.ConfigableProperty(Key = "存储的选择器")
  public ConfigableMap<PackedSelector> selectors;

  @Config.ConfigableProperty(Key = "命令序列")
  public ConfigableMap<ConfigableStringList> commandSequences;

}
