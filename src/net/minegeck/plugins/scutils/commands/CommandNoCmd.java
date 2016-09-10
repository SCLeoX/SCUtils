package net.minegeck.plugins.scutils.commands;

import net.minegeck.plugins.scutils.PlayerInterlayer;
import net.minegeck.plugins.scutils.minequery.PlayerCollection;
import net.minegeck.plugins.utils.Annotations;
import net.minegeck.plugins.utils.BoxedMessage;
import net.minegeck.plugins.utils.commandparser.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class CommandNoCmd extends SCUtilsCommand {

  public static final Format format = Format.createFormat(new Case()
    .kase(new Case()
      .field(ConstField.expect("on").ignoreCase())
      .func("使一个或多个玩家禁止使用命令")
      .tag("on")
    )
    .kase(new Case()
      .field(ConstField.expect("off").ignoreCase())
      .func("使一个或多个玩家取消禁止使用命令")
      .tag("off")
    )
    .kase(new Case()
      .field(ConstField.expect("toggle").ignoreCase())
      .func("使一个或多个玩家切换是否禁止使用命令")
      .tag("toggle")
    )
    .merge(new Case()
      .field("玩家", PlayerField.any())
      .optional(new Case()
        .field("原因", StringField.any().unlimit())
        .tag("reason-given")
      )
    )
  );

  @Override
  public Format getFormat() {
    return format;
  }

  @Override
  public String getBasicFunction() {
    return "禁止或解除禁止玩家使用命令";
  }

  @Override
  public String getBasicFormat() {
    return "/nocmd";
  }

  @Override
  public boolean onCommand(CommandSender cs, Command cmnd, String alias, String[] args) {
    ParseResult result = format.parse(args);
    if (result.tellCommandSenderIfFailed(cs, alias)) {
      return false;
    }
    PlayerCollection players = result.nextPlayerCollection();
    int success = 0;
    int failed = 0;
    StringBuilder sb = new StringBuilder();
    String reason = result.isBranch("reason-given") ? result.nextString() : "您已被管理员禁止使用命令。";
    if (result.isBranch("on")) {
      for (PlayerInterlayer player : players) {
        if (player.isNoCmd() && player.getNoCmdReason().equals(reason)) {
          sb.append("\n §c- §l无法禁止玩家 ").append(player.getID()).append(" 使用命令。该玩家之前就以相同的原因被禁止使用命令。");
          failed++;
          continue;
        }
        player.setNoCmd(true);
        player.setNoCmdReason(reason);
        sb.append("\n §a- §l成功禁止玩家 ").append(player.getID()).append(" 使用命令。");
        player.save();
        success++;
      }
    } else if (result.isBranch("off")) {
      for (PlayerInterlayer player : players) {
        if (!player.isNoCmd()) {
          sb.append("\n §c- §l无法解除禁止玩家 ").append(player.getID()).append(" 使用命令。该玩家之前并没有被禁止使用命令。");
          failed++;
          continue;
        }
        player.setNoCmd(false);
        sb.append("\n §a- §l成功解除禁止玩家 ").append(player.getID()).append(" 使用命令。");
        player.save();
        success++;
      }
    } else if (result.isBranch("toggle")) {
      for (PlayerInterlayer player : players) {
        if (player.isNoCmd()) {
          sb.append("\n §a- §l成功解除禁止玩家 ").append(player.getID()).append(" 使用命令。");
          player.setNoCmd(false);
        } else {
          sb.append("\n §a- §l成功禁止玩家 ").append(player.getID()).append(" 使用命令。");
          player.setNoCmd(true);
          player.setNoCmdReason(reason);
        }
        player.save();
        success++;
      }
    }
    if (success >= 1) {
      if (failed == 0) {
        BoxedMessage.sendTo(cs, 'a', "§a§l操作成功\n" + sb.toString() + "\n\n§a共影响了 " + success + " 个玩家。");
      } else {
        BoxedMessage.sendTo(cs, '6', "§6§l操作未完全成功\n" + sb.toString() + "\n\n§6共影响了 " + success + " 个玩家。");
      }
    } else {
      BoxedMessage.sendTo(cs, 'c', "§c§l操作失败\n" + sb.toString() + "\n\n§c没有任何一个玩家被影响。");
    }
    return true;
  }

}