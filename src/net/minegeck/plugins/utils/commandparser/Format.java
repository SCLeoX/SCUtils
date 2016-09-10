package net.minegeck.plugins.utils.commandparser;

import net.minegeck.plugins.utils.Annotations;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class Format {
  public static Format createFormat(Case kase) {
    return new Format(kase);
  }
  private final Case kase;
  private Format(Case kase) {
    this.kase = kase;
  }
  public TraversalResult traversal() {
    TraversalResult result = new TraversalResult();
    this.kase.traversal(new FormatWalker(), result);
    return result;
  }
  public ParseResult parse(String[] args, int startAt) {
    return new ParseResult(this.kase.load(new ParseData(args, startAt)));
  }
  public ParseResult parse(String[] args) {
    return parse(args, 0);
  }

}
