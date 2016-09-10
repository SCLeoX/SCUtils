package net.minegeck.plugins.scutils.minequery.ast;

import net.minegeck.plugins.utils.Annotations;
import net.minegeck.plugins.utils.confighelper.Config;

import java.text.MessageFormat;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
@Config.CustomConfigable
public class StringType extends AnyType<String> implements StringCastable, NumberCastable {

  @Override
  public RealType getRealType() {
    return AnyType.RealType.STRING;
  }

  @Override
  public String toString() {
    return "\"" + this.value + "\"";
  }

  @Config.ConfigableProperty(Key = "类型")
  public static final String type = "字符串型";

  @Config.ConfigableProperty(Key = "值")
  public String value;

  public StringType(String value) {
    this.value = value;
  }

  @Override
  public String get() {
    return this.value;
  }

  @Override
  public void set(String value) {
    this.value = value;
  }

  @Override
  public NumberType castToNumber() {
    try {
      return new NumberType(Double.valueOf(value));
    } catch(NumberFormatException ex) {
      throw new MineQueryRuntimeException(MessageFormat.format("无法将字符串 [{0}] 转换为数字。", value));
    }
  }

  @Override
  public StringType castToString() {
    return this;
  }

}
