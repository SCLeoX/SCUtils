package net.minegeck.plugins.scutils.minequery;

import net.minegeck.plugins.utils.Annotations;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class MineQuerySyntaxExceptionBuilder {
  private final String source;
  private int position;
  private String message;
  private Throwable cause;
  public static MineQuerySyntaxExceptionBuilder source(String source) {
    return new MineQuerySyntaxExceptionBuilder(source);
  }
  public static MineQuerySyntaxExceptionBuilder trace(TraceInfo trace) {
    return new MineQuerySyntaxExceptionBuilder(trace.getSource()).at(trace.getPos());
  }
  public static MineQuerySyntaxExceptionBuilder unknownSource() {
    return new MineQuerySyntaxExceptionBuilder();
  }
  private MineQuerySyntaxExceptionBuilder(String source) {
    this.source = source;
  }
  private MineQuerySyntaxExceptionBuilder() {
    this("未知来源");
  }
  public MineQuerySyntaxExceptionBuilder at(int position) {
    this.position = position;
    return this;
  }
  public MineQuerySyntaxExceptionBuilder causedBy(String message) {
    this.message = message;
    return this;
  }
  public MineQuerySyntaxExceptionBuilder causedBy(Throwable cause) {
    this.cause = cause;
    return this;
  }
  public MineQuerySyntaxException done() {
    return new MineQuerySyntaxException(source, position, message, cause);
  }
}
