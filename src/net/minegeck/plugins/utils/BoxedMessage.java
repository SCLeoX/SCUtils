package net.minegeck.plugins.utils;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class BoxedMessage {
  public static void sendTo(CommandSender cs, char boxColor, String msg) {
    if (msg.substring(msg.length() - 1).equals("\n")) {
      msg = msg.substring(0, msg.length() - 1);
    }
    if (cs instanceof BlockCommandSender) {
      return;
    }
    String hLine;
    if (cs instanceof Player) {
      hLine = new StringBuilder()
        .append("§")
        .append(boxColor)
        .append("§m                                                                                \n")
        .toString();
    } else {
      hLine = new StringBuilder()
        .append("§")
        .append(boxColor)
        .append("--------------------------------------------------------------------------------\n")
        .toString();
    }
    String[] lines = new StringBuilder()
      .append(hLine)
      .append(msg)
      .append('\n')
      .append(hLine)
      .toString().split("\n");
    for (String line : lines) {
      cs.sendMessage(line);
    }
  }
}
