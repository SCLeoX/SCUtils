package net.minegeck.plugins.scutils.minequery.token;

import net.minegeck.plugins.utils.Annotations;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public enum TokenType {

  PUNCTUATION("标点符号"),

  ID_SELECTOR("ID 选择器"),

  CLASS_SELECTOR("类选择器"),

  QUERY_SELECTOR("查询选择器"),

  ANONYMOUS_QUERY_SELECTOR("匿名查询选择器"),

  LIMITER("限制器"),

  IDENTIFIER("标识符"),

  OPERATOR("操作符"),

  NUMBER("数字"),

  STRING("字符串");

  private final String name;
  private TokenType(String name) {
    this.name = name;
  }
  public String getName() {
    return this.name;
  }
}
