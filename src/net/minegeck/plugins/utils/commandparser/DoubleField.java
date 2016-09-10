package net.minegeck.plugins.utils.commandparser;

import net.minegeck.plugins.utils.Annotations;

import java.text.MessageFormat;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class DoubleField extends FieldLoader {
  public static DoubleField any() {
    return new DoubleField(Double.NEGATIVE_INFINITY, true, Double.POSITIVE_INFINITY, true, "双精度小数");
  }
  public static DoubleField postive() {
    return new DoubleField(0, false, Double.POSITIVE_INFINITY, true, "正双精度小数");
  }
  public static DoubleField negative() {
    return new DoubleField(Double.NEGATIVE_INFINITY, true, 0, false, "负双精度小数");
  }
  public static DoubleField ranged(double min, double max) {
    return new DoubleField(min, true, max, true, MessageFormat.format("范围在 {0} - {1} 的双精度小数", min, max));
  }
  public static DoubleField greaterThan(double min) {
    return new DoubleField(min, false, Double.POSITIVE_INFINITY, true, MessageFormat.format("大于 {0} 的双精度小数", min));
  }
  public static DoubleField lessThan(double max) {
    return new DoubleField(Double.NEGATIVE_INFINITY, true, max, false, MessageFormat.format("小于 {0} 的双精度小数", max));
  }
  public static DoubleField greaterOrEqualThan(double min) {
    return new DoubleField(min, true, Double.POSITIVE_INFINITY, true, MessageFormat.format("大于或等于 {0} 的双精度小数", min));
  }
  public static DoubleField lessOrEqualThan(double max) {
    return new DoubleField(Double.NEGATIVE_INFINITY, true, max, true, MessageFormat.format("小于或等于 {0} 的双精度小数", max));
  }
  private final double min;
  private final boolean minInclude;
  private final double max;
  private final boolean maxInclude;
  private final String type;
  private int unfitnessMissing = 10;
  private int unfitnessNotDouble = 10;
  private int unfitnessRange = 2;
  private DoubleField(double min, boolean minInclude, double max, boolean maxInclude, String type) {
    this.min = min;
    this.minInclude = minInclude;
    this.max = max;
    this.maxInclude = maxInclude;
    this.type = type;
  }
  private void unfit(int unfitness, ParseData data) {
    data.unfit(unfitness, MessageFormat.format("第 {0} 个参数需要是{1}。", data.getLastParamPos(), this.type));
  };

  /**
   * 设置这个 Field 的在各个情况的不符合指数。
   * @param unfitnessMissing 如果这个 Field 不存在，默认值是 5。
   * @param unfitnessNotDouble 如果这个 Field 不是双精度小数，默认值是 5。
   * @param unfitnessRange 如果这个 Field 不在指定的范围内，默认值是 2。
   * @return
   */
  public DoubleField withUnfitnessScaleAt(int unfitnessMissing, int unfitnessNotDouble, int unfitnessRange) {
    this.unfitnessMissing = unfitnessMissing;
    this.unfitnessNotDouble = unfitnessNotDouble;
    this.unfitnessRange = unfitnessRange;
    return this;
  }

  @Override
  public void load(ParseData data) {
    String raw = data.next();
    if (raw == null) {
      this.unfit(unfitnessMissing, data);
      return;
    }
    double num;
    try {
      num = Double.valueOf(raw);
    } catch(NumberFormatException ex) {
      this.unfit(unfitnessNotDouble, data);
      return;
    }
    if ((minInclude ? (num < min) : (num <= min)) || (maxInclude ? (num > max) : (num >= max))) {
      this.unfit(unfitnessRange, data);
      return;
    }
    data.putResult(num);
  }
}
