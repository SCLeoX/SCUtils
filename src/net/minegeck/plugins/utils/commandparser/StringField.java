package net.minegeck.plugins.utils.commandparser;

import net.minegeck.plugins.utils.Annotations;

import java.text.MessageFormat;
import java.util.regex.Pattern;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class StringField extends FieldLoader {
  public static StringField any() {
    return new StringField(1, Integer.MAX_VALUE, "字符串");
  }
  public static StringField ranged(int minLength, int maxLength) {
    return new StringField(minLength, maxLength, MessageFormat.format("长度在 {0} - {1} 位的字符串", minLength, maxLength));
  }
  public static StringField longerThan(int minLength) {
    return new StringField(minLength + 1, Integer.MAX_VALUE, MessageFormat.format("长度大于 {0} 的字符串", minLength));
  }
  public static StringField lessThan(int maxLength) {
    return new StringField(1, maxLength - 1, MessageFormat.format("长度小于 {0} 的字符串", maxLength));
  }
  public static StringField longerOrEqualThan(int minLength) {
    return new StringField(minLength, Integer.MAX_VALUE, MessageFormat.format("长度大于或等于 {0} 的字符串", minLength));
  }
  public static StringField shorterOrEqualThan(int maxLength) {
    return new StringField(1, maxLength, MessageFormat.format("长度小于或等于 {0} 的字符串", maxLength));
  }
  private final int minLength;
  private final int maxLength;
  private final String type;
  private int unfitnessMissing = 10;
  private int unfitnessRange = 2;
  private Pattern regex;
  private boolean limited = true;
  private String readUtil = null;

  public StringField matches(String regex) {
    this.regex = Pattern.compile(regex);
    return this;
  }

  public StringField unlimit() {
    this.limited = false;
    return this;
  }

  public StringField util(String readUtil) {
    this.readUtil = readUtil;
    return this;
  }

  private static final Pattern identifierRegex = Pattern.compile("^[a-zA-Z][a-zA-Z0-9:_]*$");

  public StringField identifier() {
    this.regex = identifierRegex;
    return this;
  }

  private StringField(int minLength, int maxLength, String type) {
    this.minLength = minLength;
    this.maxLength = maxLength;
    this.type = type;
  }
  private void unfit(int unfitness, ParseData data) {
    if (regex == null) {
      data.unfit(unfitness, MessageFormat.format("第 {0} 个参数需要是{1}。", data.getLastParamPos(), this.type));
    } else {
      data.unfit(unfitness, MessageFormat.format("第 {0} 个参数需要是{1}, 且需要符合正则表达式 {2}。", data.getLastParamPos(), this.type, this.regex.pattern()));
    }
  };

  /**
   * 设置这个 Field 的在各个情况的不符合指数。
   * @param unfitnessMissing 如果这个 Field 不存在，默认值是 10。
   * @param unfitnessRange 如果这个 Field 不在指定的范围内，默认值是 2。
   * @return
   */
  public StringField withUnfitnessScaleAt(int unfitnessMissing, int unfitnessRange) {
    this.unfitnessMissing = unfitnessMissing;
    this.unfitnessRange = unfitnessRange;
    return this;
  }

  @Override
  public void load(ParseData data) {
    String str = data.next();
    if (str == null) {
      this.unfit(unfitnessMissing, data);
      return;
    }
    if (!this.limited) {
      StringBuilder sb = new StringBuilder();
      sb.append(str);
      String part;
      while ((part = data.next()) != null && (part == null ? this.readUtil != null : !part.equals(this.readUtil))) {
        data.incConcat();
        sb.append(' ').append(part);
      }
      data.next();
      str = sb.toString();
    }
    int length = str.length();
    if (length < minLength || length > maxLength) {
      this.unfit(unfitnessRange, data);
      return;
    }
    if (regex != null && !regex.matcher(str).matches()) {
      this.unfit(unfitnessRange, data);
      return;
    }
    data.putResult(str);
  }
}
