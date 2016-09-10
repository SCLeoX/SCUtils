package net.minegeck.plugins.scutils.commands;

import net.minegeck.plugins.scutils.Main;
import net.minegeck.plugins.utils.Annotations;
import net.minegeck.plugins.utils.BoxedMessage;
import net.minegeck.plugins.utils.StringJoiner;
import net.minegeck.plugins.utils.commandparser.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import java.text.MessageFormat;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class CommandHelp extends SCUtilsCommand {

  public static final Format format = Format.createFormat(new Case()
    .kase(new Case()
      .func("列出所有的命令")
      .tag("list")
      .optional(new Case()
        .field("页码", IntField.postive())
        .tag("page-given")
      )
    )
    .kase(new Case()
      .func("获得一个命令的详细信息")
      .field("命令", StringField.any())
      .tag("command")
    )
  );

  @Override
  public Format getFormat() {
    return format;
  }

  @Override
  public String getBasicFunction() {
    return "获得帮助信息";
  }

  @Override
  public String getBasicFormat() {
    return "/uhelp";
  }

  @Override
  public boolean onCommand(CommandSender cs, Command cmnd, String alias, String[] args) {
    ParseResult result = format.parse(args);
    if (result.tellCommandSenderIfFailed(cs, alias)) {
      return false;
    }
    if (result.isBranch("list")) {
      int page;
      if (result.isBranch("page-given")) {
        page = result.nextInt();
      } else {
        page = 1;
      }
      int count = SCUtilsCommand.getRecords().size();
      int maxPage = (int) Math.ceil((double) count / 16);
      if (page > maxPage) {
        BoxedMessage.sendTo(cs, 'c', MessageFormat.format("§c§l页码过大\n\n§c您给出的页码 §e§l{0} §c过大, 一共只有 §e§l{1} §c页。", page, maxPage));
        return false;
      }
      int startAt = (page - 1) * 14 + 1;
      int endAt = Math.min(page * 14, count);
      StringBuilder sb = new StringBuilder();
      sb.append("§b§l页码: §e§l").append(page).append("§6§l/").append(maxPage).append("§b§l - SCUtils v").append(Main.current.getDescription().getVersion()).append(" 作者 SCLeo\n\n");
      for (int i = startAt; i <= endAt; i++) {
        SCUtilsCommand cmd = SCUtilsCommand.getRecords().get(i - 1);
        sb.append("§e§l").append(cmd.getBasicFormat()).append(" §a").append(cmd.getBasicFunction()).append("\n");
      }
      sb.append("\n§b使用 /uhelp <页码> 来切换页码, 使用 /uhelp <命令> 查看完整帮助信息。");
      BoxedMessage.sendTo(cs, 'b', sb.toString());
    } else if (result.isBranch("command")) {
      String cmdStr = result.nextString();
      PluginCommand cmd = Main.current.getCommand(cmdStr);
      if (cmd == null) {
        BoxedMessage.sendTo(cs, 'c', MessageFormat.format("§c§l命令不存在\n\n§c没有找到命令 §e§l{0}§c。", cmdStr));
        return false;
      }
      CommandExecutor cmdExe = cmd.getExecutor();
      if (cmdExe == null) {
        BoxedMessage.sendTo(cs, 'c', MessageFormat.format("§c§l命令未加载\n\n§c命令 §e§l{0}§c 的处理器没有被加载或加载失败。", cmdStr));
        return false;
      }
      if (!(cmdExe instanceof SCUtilsCommand)) {
        BoxedMessage.sendTo(cs, 'c', "§c§l命令信息读取失败\n\n§c被读取的命令没有继承 §e§lSCUtilsCommand§c。");
        return false;
      }
      SCUtilsCommand scucmd = (SCUtilsCommand) cmdExe;
      TraversalResult tResult = scucmd.getFormat().traversal();
      StringBuilder sb = new StringBuilder();
      sb.append("§b§l关于命令 ").append(scucmd.getBasicFormat()).append("\n").append("§b别名: §l").append(StringJoiner.join(cmd.getAliases(), "§b, §l"));
      String lastFunction = null;
      for (FormatWalker walker : tResult.getWalkers()) {
        String newFunction = walker.getFunction();
        if (lastFunction == null ? newFunction != null : !lastFunction.equals(newFunction)) {
          sb.append("\n\n§a§l").append(newFunction);
          lastFunction = newFunction;
        }
        sb.append('\n').append("§e/").append(cmdStr).append("§e§l");
        for (String field : walker.getFieldNames()) {
          sb.append(' ').append(field);
        }
      }
      BoxedMessage.sendTo(cs, 'b', sb.toString());
    }
    return true;
  }

}
