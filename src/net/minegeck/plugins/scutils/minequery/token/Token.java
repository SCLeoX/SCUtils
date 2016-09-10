package net.minegeck.plugins.scutils.minequery.token;

import net.minegeck.plugins.scutils.minequery.TraceInfo;
import net.minegeck.plugins.utils.Annotations;

import java.text.MessageFormat;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class Token {
  private final TokenType type;
  private final String content;
  private final TraceInfo trace;
  public Token(TokenType type, String content, TraceInfo trace) {
    this.type = type;
    this.content = content;
    this.trace = trace;
  }
  public boolean is(TokenType target) {
    return this.type.equals(target);
  }
  public boolean is(String content) {
    return this.content.equals(content);
  }
  public TokenType getType() {
    return this.type;
  }
  public String getContent() {
    return this.content;
  }
  public TraceInfo getTrace() {
    return this.trace;
  }
  @Override
  public String toString() {
    return MessageFormat.format("{0}: {1}", type.getName(), content);
  }
}
