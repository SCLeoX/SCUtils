package net.minegeck.plugins.scutils.minequery;

import net.minegeck.plugins.utils.Annotations;

import java.text.MessageFormat;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class MineQuerySyntaxException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final String source;
  private final int position;

  public MineQuerySyntaxException(String source, int position, String message, Throwable cause) {
    super(message, cause);
    this.source = source;
    this.position = position;
  }

  public String getFullMessage() {
    return MessageFormat.format("MineQuery 语法解析失败: {0}位于位置: {1}。", this.getMessage(), this.position);
  }

}
