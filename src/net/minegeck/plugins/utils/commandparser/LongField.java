package net.minegeck.plugins.utils.commandparser;

import net.minegeck.plugins.utils.Annotations;

import java.text.MessageFormat;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class LongField extends FieldLoader {
  public static LongField any() {
    return new LongField(Long.MIN_VALUE, Long.MAX_VALUE, "长整数");
  }
  public static LongField postive() {
    return new LongField(1, Long.MAX_VALUE, "正长整数");
  }
  public static LongField negative() {
    return new LongField(Long.MIN_VALUE, -1, "负长整数");
  }
  public static LongField natural() {
    return new LongField(0, Long.MAX_VALUE, "自然数");
  }
  public static LongField ranged(long min, long max) {
    return new LongField(min, max, MessageFormat.format("范围在 {0} - {1} 的长整数", min, max));
  }
  public static LongField greaterThan(long min) {
    return new LongField(min + 1, Long.MAX_VALUE, MessageFormat.format("大于 {0} 的长整数", min));
  }
  public static LongField lessThan(long max) {
    return new LongField(Long.MIN_VALUE, max - 1, MessageFormat.format("小于 {0} 的长整数", max));
  }
  public static LongField greaterOrEqualThan(long min) {
    return new LongField(min, Long.MAX_VALUE, MessageFormat.format("大于或等于 {0} 的长整数", min));
  }
  public static LongField lessOrEqualThan(long max) {
    return new LongField(Long.MIN_VALUE, max, MessageFormat.format("小于或等于 {0} 的长整数", max));
  }
  private final long min;
  private final long max;
  private final String type;
  private int unfitnessMissing = 10;
  private int unfitnessNotLong = 10;
  private int unfitnessRange = 2;
  private LongField(long min, long max, String type) {
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
   * @param unfitnessNotLong 如果这个 Field 不是长整数，默认值是 5。
   * @param unfitnessRange 如果这个 Field 不在指定的范围内，默认值是 2。
   * @return
   */
  public LongField withUnfitnessScaleAt(int unfitnessMissing, int unfitnessNotLong, int unfitnessRange) {
    this.unfitnessMissing = unfitnessMissing;
    this.unfitnessNotLong = unfitnessNotLong;
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
    long num;
    try {
      num = Long.valueOf(raw);
    } catch(NumberFormatException ex) {
      this.unfit(unfitnessNotLong, data);
      return;
    }
    if (num < min || num > max) {
      this.unfit(unfitnessRange, data);
      return;
    }
    data.putResult(num);
  }
}
