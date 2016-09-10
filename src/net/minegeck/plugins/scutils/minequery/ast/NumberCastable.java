package net.minegeck.plugins.scutils.minequery.ast;

import net.minegeck.plugins.utils.Annotations;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public interface NumberCastable {
  public NumberType castToNumber();
}
