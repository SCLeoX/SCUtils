package net.minegeck.plugins.scutils;

import net.minegeck.plugins.scutils.minequery.MineQuerySyntaxException;
import net.minegeck.plugins.scutils.minequery.PlayerCollection;
import net.minegeck.plugins.scutils.minequery.ast.*;
import net.minegeck.plugins.utils.Annotations;
import net.minegeck.plugins.utils.BoxedMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class CommandConverter {
  public static class ConvertResult {

    private boolean cancelEvent;

    public boolean isCancelEvent() {
      return cancelEvent;
    }

    public void setCancelEvent(boolean cancelEvent) {
      this.cancelEvent = cancelEvent;
    }

    private boolean converted;

    public void setConverted(boolean converted) {
      this.converted = converted;
    }

    public void setMsg(String msg) {
      this.msg = msg;
    }

    private char msgColor;

    public char getMsgColor() {
      return msgColor;
    }

    public void setMsgColor(char msgColor) {
      this.msgColor = msgColor;
    }

    public boolean isConverted() {
      return converted;
    }

    private String msg;

    public String getMsg() {
      return msg;
    }

    private final ArrayList<String> subCommands = new ArrayList<>();

    public ArrayList<String> getSubCommands() {
      return subCommands;
    }

    public void run(CommandSender cs) {
      if (this.msg != null) {
        BoxedMessage.sendTo(cs, this.msgColor, this.msg);
      }
      if (this.cancelEvent && this.converted) {
        for (String cmd : this.subCommands) {
          Bukkit.dispatchCommand(cs, cmd);
        }
      }
    }

  }
  public static ConvertResult convert(String input) {
    int firstSpace = input.indexOf(" ");
    if (Main.current.getCommand(input.substring(0, firstSpace == -1 ? input.length() : firstSpace)) != null || !input.contains("$")) {
      ConvertResult result = new ConvertResult();
      result.setConverted(false);
      result.setCancelEvent(false);
      return result;
    }
    String content;
    {
      int start = input.indexOf("$");
      int i = start;
      int parentheses = 0;
      boolean quoted = false;
      boolean escaped = false;
      char ch;
      int length = input.length();
      do {
        i++;
        if (i >= length) {
          break;
        }
        ch = input.charAt(i);
        if (escaped) {
          continue;
        }
        switch (ch) {
          case '(':
            parentheses++;
            break;
          case ')':
            parentheses--;
            break;
          case '"':
            quoted = !quoted;
            break;
          case '\\':
            escaped = true;
            break;
        }
      } while (parentheses > 0 || ch != ' ');
      content = input.substring(start, i);
      input = input.substring(0, start) + '$' + input.substring(i);
    }
    ASTSelector selector;
    ArrayList<ASTExpression> expressions = new ArrayList<>();
    ArrayList<String> items = new ArrayList<>();
    PlayerCollection players;
    String last;
    try {
      selector = new ASTParser(content).parseToSelector();
      int i = 0;
      int start;
      int end = -2;
      while ((start = input.indexOf("{%", i)) != -1) {
        int findEnd = input.indexOf("%}", start + 2);
        if (findEnd == -1) {
          break;
        }
        end = findEnd;
        items.add(input.substring(i, start));
        expressions.add(new ASTParser(input.substring(start + 2, end)).parseToExpression());
        i = end + 2;
      }
      last = input.substring(end + 2);
      players = selector.select();
    } catch (MineQuerySyntaxException | MineQueryRuntimeException ex) {
      ConvertResult result = new ConvertResult();
      result.setConverted(false);
      result.setMsgColor('c');
      result.setMsg("§c§lMineQuery 解析失败\n\n§c" + ex.getMessage());
      result.setCancelEvent(true);
      return result;
    }
    ConvertResult convertResult = new ConvertResult();
    StringBuilder msgs = new StringBuilder();
    int success = 0;
    int failed = 0;
    for (PlayerInterlayer player : players) {
      StringBuilder sb = new StringBuilder();
      int size = expressions.size();
      boolean f = false;
      for (int i = 0; i < size; i++) {
        sb.append(items.get(i));
        AnyType result;
        try {
          result = expressions.get(i).eval(player.getIdentifierProvider());
        } catch(MineQueryRuntimeException ex) {
          failed++;
          msgs.append("\n §c- §l").append("在计算玩家 ").append(player.getID()).append(" 的第 ").append(i + 1).append(" 个变量时, MineQuery 执行失败: ").append(ex.getMessage());
          f = true;
          break;
        }
        if (!(result instanceof StringCastable)) {
          msgs.append("\n §c- §l").append("在计算玩家 ").append(player.getID()).append(" 的第 ").append(i + 1).append(" 个变量时, 其运算结果 [").append(result.toString()).append("] 无法被转换为字符串。");
          failed++;
          f = true;
          break;
        }
        sb.append(((StringCastable) result).castToString().get());
      }
      if (!f) {
        String cmd = sb.append(last).toString().replaceAll("\\$", player.getID());
        success++;
        msgs.append("\n §a- §l").append("为玩家 ").append(player.getID()).append(" 执行 /").append(cmd);
        convertResult.getSubCommands().add(cmd);
      }
    }
    if (success >= 1) {
      if (failed == 0) {
        convertResult.setMsg("§a§l操作成功\n" + msgs.toString() + "\n\n§a共执行了 " + success + " 个命令。");
        convertResult.setMsgColor('a');
      } else {
        convertResult.setMsg("§6§l操作未完全成功\n" + msgs.toString() + "\n\n§6共执行了 " + success + " 个命令。");
        convertResult.setMsgColor('6');
      }
    } else {
      convertResult.setMsg("§c§l操作失败\n" + msgs.toString() + "\n\n§c共执行了 " + success + " 个命令。");
      convertResult.setMsgColor('c');
    }
    convertResult.setConverted(true);
    convertResult.setCancelEvent(true);
    return convertResult;
  }
}
