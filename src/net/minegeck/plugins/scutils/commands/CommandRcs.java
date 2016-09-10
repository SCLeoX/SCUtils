package net.minegeck.plugins.scutils.commands;

import net.minegeck.plugins.scutils.CommandConverter;
import net.minegeck.plugins.scutils.Main;
import net.minegeck.plugins.utils.Annotations;
import net.minegeck.plugins.utils.BoxedMessage;
import net.minegeck.plugins.utils.commandparser.*;
import net.minegeck.plugins.utils.confighelper.ConfigableStringList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.text.MessageFormat;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class CommandRcs extends SCUtilsCommand {

  public static final Format format = Format.createFormat(new Case()
    .func("以自己的身份运行一个命令序列")
    .optional(new Case()
      .field(ConstField.expect("console").ignoreCase())
      .tag("use-console")
      .func("以 Console 的身份运行一个命令序列")
    )
    .merge(new Case()
      .field("序列名", StringField.any().identifier())
    )
  );

  @Override
  public Format getFormat() {
    return format;
  }

  @Override
  public String getBasicFunction() {
    return "运行一个命令序列";
  }

  @Override
  public String getBasicFormat() {
    return "/rcs";
  }

  @Override
  public boolean onCommand(CommandSender cs, Command cmnd, String alias, String[] args) {
    ParseResult result = format.parse(args);
    if (result.tellCommandSenderIfFailed(cs, alias)) {
      return false;
    }
    String seqName = result.nextString();
    if (!Main.configRoot.commandSequences.containsKey(seqName)) {
      BoxedMessage.sendTo(cs, 'c', MessageFormat.format("§c§l命令序列不存在\n\n§c无法找到命令序列 §e§l{0}§c。请使用 §e§l/cs create {0} §c来创建。", seqName));
      return false;
    }
    ConfigableStringList sequence = Main.configRoot.commandSequences.get(seqName);
    int i = 0;
    CommandSender sender = result.isBranch("use-console") ? Bukkit.getConsoleSender() : cs;
    for (String cmd : sequence) {
      i++;
      CommandConverter.ConvertResult cResult = CommandConverter.convert(cmd);
      cResult.run(cs);
      if (!cResult.isCancelEvent()) {
        Bukkit.dispatchCommand(sender, cmd);
      }
    }
    BoxedMessage.sendTo(cs, 'a', MessageFormat.format("§a§l命令序列已执行\n\n§a总计执行了 §e§l{0} §a个命令。", i));
    return true;
  }
}
