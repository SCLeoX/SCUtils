package net.minegeck.plugins.scutils.commands;

import net.minegeck.plugins.scutils.PlayerInterlayer;
import net.minegeck.plugins.scutils.minequery.PlayerCollection;
import net.minegeck.plugins.utils.Annotations;
import net.minegeck.plugins.utils.BoxedMessage;
import net.minegeck.plugins.utils.commandparser.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class CommandClass extends SCUtilsCommand {

  public static final Format format = Format.createFormat(new Case()
    .kase(new Case()
      .tag("modify")
      .kase(new Case()
        .field(ConstField.expect("add").ignoreCase())
        .func("为一个或多个玩家添加一个类")
        .tag("add")
      )
      .kase(new Case()
        .field(ConstField.expect("remove").ignoreCase())
        .func("为一个或多个玩家删除一个类")
        .tag("remove")
      )
      .kase(new Case()
        .field(ConstField.expect("toggle").ignoreCase())
        .func("为一个或多个玩家切换一个类的状态")
        .tag("toggle")
      )
      .merge(new Case()
        .optional(new Case()
          .field("玩家", PlayerField.any())
          .tag("player-given")
        )
        .merge(new Case()
          .field("类名", StringField.any())
        )
      )
    )
    .kase(new Case()
      .func("列出一个玩家的所有类")
      .optional(new Case()
        .field(ConstField.expect("list").ignoreCase())
      )
      .merge(new Case()
        .tag("list")
        .optional(new Case()
          .field("玩家", PlayerField.one())
          .tag("player-given")
        )
      )
    )
  );

  @Override
  public Format getFormat() {
    return format;
  }

  @Override
  public String getBasicFunction() {
    return "修改或查看玩家的类";
  }

  @Override
  public String getBasicFormat() {
    return "/class";
  }

  @Override
  public boolean onCommand(CommandSender cs, Command cmnd, String alias, String[] args) {
    ParseResult result = format.parse(args);
    if (result.tellCommandSenderIfFailed(cs, alias)) {
      return false;
    }
    PlayerCollection players;
    if (result.isBranch("player-given")) {
      players = result.nextPlayerCollection();
    } else if (cs instanceof Player) {
      players = new PlayerCollection(PlayerInterlayer.getInstanceFor((Player) cs));
    } else {
      BoxedMessage.sendTo(cs, 'c', "§c§l只限于玩家\n\n§c只有被玩家使用时才可以忽略参数玩家。");
      return false;
    }
    if (result.isBranch("list")) {
      StringBuilder sb = new StringBuilder();
      PlayerInterlayer player = players.iterator().next();
      sb.append("§b§l玩家 ").append(player.getID()).append(" 的所有类\n");
      for (String klass : player.getClasses()) {
        sb.append("\n §e- §l").append(klass);
      }
      BoxedMessage.sendTo(cs, 'b', sb.toString());
    } else if (result.isBranch("modify")) {
      String klass = result.nextString();
      StringBuilder msg = new StringBuilder();
      int success = 0;
      int failed = 0;
      if (result.isBranch("add")) {
        for (PlayerInterlayer player : players) {
          if (!player.hasClass(klass)) {
            player.getClasses().add(klass);
            msg.append("\n §a- §l成功为玩家 ").append(player.getID()).append(" 添加类 ").append(klass).append("。");
            player.save();
            success++;
          } else {
            msg.append("\n §c- §l无法为玩家 ").append(player.getID()).append(" 添加类 ").append(klass).append(", 他原来就有了。");
            failed++;
          }
        }
      } else if (result.isBranch("remove")) {
        for (PlayerInterlayer player : players) {
          if (player.hasClass(klass)) {
            player.getClasses().remove(klass);
            msg.append("\n §a- §l成功为玩家 ").append(player.getID()).append(" 删除类 ").append(klass).append("。");
            player.save();
            success++;
          } else {
            msg.append("\n §c- §l无法为玩家 ").append(player.getID()).append(" 删除类 ").append(klass).append(", 他原来并没有这个类。");
            failed++;
          }
        }
      } else if (result.isBranch("toggle")) {
        for (PlayerInterlayer player : players) {
          if (player.hasClass(klass)) {
            player.getClasses().remove(klass);
            msg.append("\n §a- §l成功为玩家 ").append(player.getID()).append(" 删除类 ").append(klass).append("。");
          } else {
            player.getClasses().add(klass);
            msg.append("\n §a- §l成功为玩家 ").append(player.getID()).append(" 添加类 ").append(klass).append("。");
          }
          player.save();
          success++;
        }
      }
      if (success >= 1) {
        if (failed == 0) {
          BoxedMessage.sendTo(cs, 'a', "§a§l操作成功\n" + msg.toString() + "\n\n§a共影响了 " + success + " 个玩家。");
        } else {
          BoxedMessage.sendTo(cs, '6', "§6§l操作未完全成功\n" + msg.toString() + "\n\n§6共影响了 " + success + " 个玩家。");
        }
      } else {
        BoxedMessage.sendTo(cs, 'c', "§c§l操作失败\n" + msg.toString() + "\n\n§c没有任何一个玩家被影响。");
      }
    }
    return true;
  }

}