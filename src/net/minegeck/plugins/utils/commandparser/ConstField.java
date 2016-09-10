package net.minegeck.plugins.utils.commandparser;

import net.minegeck.plugins.utils.StringSimilarity;

import java.text.MessageFormat;

public class ConstField extends FieldLoader {
  public static ConstField expect(String expect) {
    return new ConstField(expect);
  }

  private String expect;
  private boolean ignoreCase = false;
  private int unfitnessMissing = 10;
  private int unfitnessBase = 1;
  private int unfitnessExtra = 29;
  private ConstField(String expect) {
    this.expect = expect;
  }

  public ConstField ignoreCase() {
    this.ignoreCase = true;
    this.expect = this.expect.toLowerCase();
    return this;
  }

  /**
   * 设置这个 Field 的在各个情况的不符合指数。
   *
   * @param unfitnessMissing 当这个 Field 不存在时，默认是 10。
   * @param unfitnessBase 基础，默认是10。
   * @param unfitnessExtra 额外值，默认是90。
   * @return
   */
  public ConstField withUnfitnessScaleAt(int unfitnessMissing, int unfitnessBase, int unfitnessExtra) {
    this.unfitnessMissing = unfitnessMissing;
    this.unfitnessBase = unfitnessBase;
    this.unfitnessExtra = unfitnessExtra;
    return this;
  }

  @Override
  public String getFieldName() {
    return expect;
  }

  @Override
  public void load(ParseData data) {
    String input = data.next();
    if (input == null) {
      data.unfit(unfitnessMissing, MessageFormat.format("第 {0} 个参数应该是 {1}。", data.getLastParamPos(), this.expect));
      return;
    }
    boolean asExpected;
    if (this.ignoreCase) {
      input = input.toLowerCase();
    }
    asExpected = this.expect.equals(input);
    if (!asExpected) {
      double similarity = StringSimilarity.similarity(input, expect);
      data.unfit(
        (int) Math.round(unfitnessBase + unfitnessExtra * (1 - similarity)),
        MessageFormat.format("第 {0} 个参数应该是 {1}, 您填写了 {2}, 相似度为 {3}%。", data.getLastParamPos(), this.expect, input, Math.round(similarity * 100))
      );
    }
  }

}
