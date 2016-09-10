package net.minegeck.plugins.utils.commandparser;

import net.minegeck.plugins.scutils.minequery.PlayerCollection;
import net.minegeck.plugins.scutils.minequery.ast.ASTNode;
import net.minegeck.plugins.utils.Annotations;
import net.minegeck.plugins.utils.BoxedMessage;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Stack;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class ParseResult {
  private final Stack<Integer> marks = new Stack<>();
  private final ParseData data;
  public void execute(CommandSender cs) {
    for (CaseProcessor processor : this.data.getCaseProcessors()) {
      if (!processor.caseMet(this, cs)) {
        break;
      }
    }
  }
  public boolean isFailed() {
    return !this.data.isFit();
  }
  public ArrayList<String> getUnfitnessMessages() {
    return this.data.getUnfitnessMessages();
  }
  public ArrayList<String> getFieldNames() {
    return this.data.getFieldNames();
  }
  public boolean tellCommandSenderIfFailed(CommandSender cs, String alias) {
    if (this.isFailed()) {
      StringBuilder sb = new StringBuilder()
        .append("§c§l抱歉, 命令解析失败\n\n")
        .append("§c您可能想用 §e/")
        .append(alias)
        .append("§l");
      ArrayList<String> fieldNames = this.getFieldNames();
      for (String str : fieldNames) {
        sb.append(' ').append(str);
      }
      sb.append("§c, 但是:");
      ArrayList<String> msgs = this.getUnfitnessMessages();
      for (String msg : msgs) {
        sb.append("\n §e- §l").append(msg);
      }
      BoxedMessage.sendTo(cs, 'c', sb.toString());
      return true;
    } else {
      return false;
    }
  }
  public ParseResult(ParseData data) {
    this.data = data;
  }
  public boolean isBranch(String tag) {
    return this.data.getBranchTags().contains(tag);
  }
  private int pointer = 0;
  public int nextInt() {
    return (int) this.data.getResults().get(pointer++);
  }
  public long nextLong() {
    return (long) this.data.getResults().get(pointer++);
  }
  public double nextDouble() {
    return (double) this.data.getResults().get(pointer++);
  }
  public boolean nextBoolean() {
    return (boolean) this.data.getResults().get(pointer++);
  }
  public String nextString() {
    return (String) this.data.getResults().get(pointer++);
  }
  public PlayerCollection nextPlayerCollection() {
    return (PlayerCollection) this.data.getResults().get(pointer++);
  }
  public ASTNode nextASTNode() {
    return (ASTNode) this.data.getResults().get(pointer++);
  }
  public void mark() {
    marks.add(pointer);
  }
  public void toLastMark() {
    pointer = marks.pop();
  }
}
