package net.minegeck.plugins.scutils.commands;

import net.minegeck.plugins.scutils.PlayerInterlayer;
import net.minegeck.plugins.scutils.minequery.PlayerCollection;
import net.minegeck.plugins.utils.Annotations;
import net.minegeck.plugins.utils.BoxedMessage;
import net.minegeck.plugins.utils.commandparser.Case;
import net.minegeck.plugins.utils.commandparser.Format;
import net.minegeck.plugins.utils.commandparser.ParseResult;
import net.minegeck.plugins.utils.commandparser.PlayerField;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class CommandSelect extends SCUtilsCommand {

  public static final Format format = Format.createFormat(new Case()
    .field("玩家选择器", PlayerField.any())
    .func("试验一个玩家选择器")
  );

  @Override
  public Format getFormat() {
    return format;
  }

  @Override
  public String getBasicFunction() {
    return "试验一个玩家选择器";
  }

  @Override
  public String getBasicFormat() {
    return "/select";
  }

  @Override
  public boolean onCommand(CommandSender cs, Command cmnd, String alias, String[] args) {
    ParseResult result = format.parse(args);
    if (result.tellCommandSenderIfFailed(cs, alias)) {
      return false;
    }
    PlayerCollection players = result.nextPlayerCollection();
    StringBuilder sb = new StringBuilder();
    sb.append("§b§l该选择器选择了以下玩家\n");
    for (PlayerInterlayer player : players) {
      sb.append("\n §e- §l").append(player.getID());
    }
    BoxedMessage.sendTo(cs, 'b', sb.toString());
    return true;
  }

}
