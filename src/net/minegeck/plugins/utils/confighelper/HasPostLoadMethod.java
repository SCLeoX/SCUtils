package net.minegeck.plugins.utils.confighelper;

import net.minegeck.plugins.utils.Annotations;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public interface HasPostLoadMethod {
  public void postLoad();
}
