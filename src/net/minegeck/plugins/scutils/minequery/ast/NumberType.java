package net.minegeck.plugins.scutils.minequery.ast;

import net.minegeck.plugins.utils.Annotations;
import net.minegeck.plugins.utils.confighelper.Config;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
@Config.CustomConfigable
public class NumberType extends AnyType<Double> implements NumberCastable, StringCastable {

  @Config.ConfigableProperty(Key = "类型")
  public static final String type = "双精度小数型";

  @Config.ConfigableProperty(Key = "值")
  public double value;

  public NumberType(double value) {
    this.value = value;
  }

  @Override
  public String toString() {
    if (this.value % 1 == 0) {
      return String.valueOf((int) this.value);
    } else {
      return String.valueOf(this.value);
    }
  }

  @Override
  public RealType getRealType() {
    return AnyType.RealType.NUMBER;
  }

  @Override
  public Double get() {
    return this.value;
  }

  @Override
  public void set(Double value) {
    this.value = value;
  }

  @Override
  public NumberType castToNumber() {
    return this;
  }

  @Override
  public StringType castToString() {
    return new StringType(this.toString());
  }

}
