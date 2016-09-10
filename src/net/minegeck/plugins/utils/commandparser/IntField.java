package net.minegeck.plugins.utils.commandparser;

import net.minegeck.plugins.utils.Annotations;

import java.text.MessageFormat;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class IntField extends FieldLoader {
  public static IntField any() {
    return new IntField(Integer.MIN_VALUE, Integer.MAX_VALUE, "整数");
  }
  public static IntField postive() {
    return new IntField(1, Integer.MAX_VALUE, "正整数");
  }
  public static IntField negative() {
    return new IntField(Integer.MIN_VALUE, -1, "负整数");
  }
  public static IntField natural() {
    return new IntField(0, Integer.MAX_VALUE, "自然数");
  }
  public static IntField ranged(int min, int max) {
    return new IntField(min, max, MessageFormat.format("范围在 {0} - {1} 的整数", min, max));
  }
  public static IntField greaterThan(int min) {
    return new IntField(min + 1, Integer.MAX_VALUE, MessageFormat.format("大于 {0} 的整数", min));
  }
  public static IntField lessThan(int max) {
    return new IntField(Integer.MIN_VALUE, max - 1, MessageFormat.format("小于 {0} 的整数", max));
  }
  public static IntField greaterOrEqualThan(int min) {
    return new IntField(min, Integer.MAX_VALUE, MessageFormat.format("大于或等于 {0} 的整数", min));
  }
  public static IntField lessOrEqualThan(int max) {
    return new IntField(Integer.MIN_VALUE, max, MessageFormat.format("小于或等于 {0} 的整数", max));
  }
  private final int min;
  private final int max;
  private final String type;
  private int unfitnessMissing = 10;
  private int unfitnessNotInteger = 10;
  private int unfitnessRange = 2;
  private IntField(int min, int max, String type) {
    this.min = min;
    this.max = max;
    this.type = type;
  }
  private void unfit(int unfitness, ParseData data) {
    data.unfit(unfitness, MessageFormat.format("第 {0} 个参数需要是{1}。", data.getLastParamPos(), this.type));
  };

  /**
   * 设置这个 Field 的在各个情况的不符合指数。
   * @param unfitnessMissing 如果这个 Field 不存在，默认值是 5。
   * @param unfitnessNotInteger 如果这个 Field 不是整数，默认值是 5。
   * @param unfitnessRange 如果这个 Field 不在指定的范围内，默认值是 2。
   * @return
   */
  public IntField withUnfitnessScaleAt(int unfitnessMissing, int unfitnessNotInteger, int unfitnessRange) {
    this.unfitnessMissing = unfitnessMissing;
    this.unfitnessNotInteger = unfitnessNotInteger;
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
    int num;
    try {
      num = Integer.valueOf(raw);
    } catch(NumberFormatException ex) {
      this.unfit(unfitnessNotInteger, data);
      return;
    }
    if (num < min || num > max) {
      this.unfit(unfitnessRange, data);
      return;
    }
    data.putResult(num);
  }
}
