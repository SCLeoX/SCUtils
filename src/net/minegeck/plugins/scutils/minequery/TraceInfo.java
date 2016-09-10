package net.minegeck.plugins.scutils.minequery;

import net.minegeck.plugins.utils.Annotations;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class TraceInfo {
  private final String source;
  private final int pos;
  public TraceInfo(String source, int pos) {
    this.source = source;
    this.pos = pos;
  }
  public String getSource() {
    return this.source;
  }
  public int getPos() {
    return this.pos;
  }
}
