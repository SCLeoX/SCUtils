package net.minegeck.plugins.scutils.minequery.ast;

import net.minegeck.plugins.utils.Annotations;
import net.minegeck.plugins.utils.confighelper.Config;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
@Config.CustomConfigable
public class BooleanType extends AnyType<Boolean> implements BooleanCastable, NumberCastable, StringCastable {

  @Config.ConfigableProperty(Key = "类型")
  public static final String type = "布尔型";

  @Config.ConfigableProperty(Key = "值")
  public boolean value;

  public BooleanType(boolean value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return this.value ? "True" : "False";
  }

  @Override
  public RealType getRealType() {
    return AnyType.RealType.BOOLEAN;
  }

  @Override
  public Boolean get() {
    return this.value;
  }

  @Override
  public void set(Boolean value) {
    this.value = value;
  }

  @Override
  public BooleanType castToBoolean() {
    return this;
  }

  @Override
  public NumberType castToNumber() {
    return this.value ? new NumberType(1) : new NumberType(0);
  }

  @Override
  public StringType castToString() {
    return this.value ? new StringType("True") : new StringType("False");
  }

}
