package net.minegeck.plugins.scutils.commands;

import net.minegeck.plugins.scutils.CommandConverter;
import net.minegeck.plugins.utils.Annotations;
import net.minegeck.plugins.utils.commandparser.Case;
import net.minegeck.plugins.utils.commandparser.Format;
import net.minegeck.plugins.utils.commandparser.ParseResult;
import net.minegeck.plugins.utils.commandparser.StringField;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class CommandExec extends SCUtilsCommand {

  public static final Format format = Format.createFormat(new Case()
    .func("运行一个或多个含有选择器的命令。多个命令用 \" | \" 区分，每个命令的选择器不会互相影响")
    .field("命令", StringField.any().unlimit())
  );

  @Override
  public Format getFormat() {
    return format;
  }

  @Override
  public String getBasicFunction() {
    return "执行处理并执行一个或多个含有选择器的命令";
  }

  @Override
  public String getBasicFormat() {
    return "/exec";
  }

  @Override
  public boolean onCommand(CommandSender cs, Command cmnd, String alias, String[] args) {
    ParseResult result = format.parse(args);
    if (result.tellCommandSenderIfFailed(cs, alias)) {
      return false;
    }
    String content = result.nextString();
    String[] subCmds = content.split("\\s\\|\\s");
    for (String cmd : subCmds) {
      CommandConverter.ConvertResult cResult = CommandConverter.convert(cmd);
      cResult.run(cs);
      if (!cResult.isCancelEvent()) {
        Bukkit.dispatchCommand(cs, cmd);
      }
    }
    return true;
  }
}
