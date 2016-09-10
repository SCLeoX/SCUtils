package net.minegeck.plugins.scutils.minequery;

import net.minegeck.plugins.utils.Annotations;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class CharReader {
  private final String input;
  private int pos = 0;
  public CharReader(String input) {
    this.input = input;
  }
  public char next() {
    if (eof()) return ' ';
    return input.charAt(pos++);
  }
  public char peek() {
    if (eof()) return ' ';
    return input.charAt(pos);
  }
  public boolean eof() {
    return input.length() <= pos;
  }
  public int getPos() {
    return pos;
  }
  public void exception(String msg) {
    throw MineQuerySyntaxExceptionBuilder
      .source(input)
      .at(pos)
      .causedBy(msg)
      .done();
  }
  public void exception() {
    exception(null);
  }
  public String getSource() {
    return this.input;
  }
}
