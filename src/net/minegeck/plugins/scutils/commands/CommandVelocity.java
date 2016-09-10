package net.minegeck.plugins.scutils.commands;

import net.minegeck.plugins.scutils.PlayerInterlayer;
import net.minegeck.plugins.scutils.minequery.EvalHelper;
import net.minegeck.plugins.scutils.minequery.PlayerCollection;
import net.minegeck.plugins.scutils.minequery.ast.MineQueryRuntimeException;
import net.minegeck.plugins.utils.Annotations;
import net.minegeck.plugins.utils.BoxedMessage;
import net.minegeck.plugins.utils.commandparser.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class CommandVelocity extends SCUtilsCommand {

  public static final Format format = Format.createFormat(new Case()
    .kase(new Case()
      .func("设置一个或多个玩家的运动速度")
      .optional(new Case()
        .field("玩家", PlayerField.any())
        .tag("player-given")
      )
      .merge(new Case()
        .kase(new Case()
          .field("X 速度", MineQueryField.expression())
          .field("Y 速度", MineQueryField.expression())
          .field("Z 速度", MineQueryField.expression())
        )
        .kase(new Case()
          .field("Y 速度", MineQueryField.expression())
          .tag("y-only")
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
    return "设置玩家的运动速度";
  }

  @Override
  public String getBasicFormat() {
    return "/velocity";
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
    } else {
      if (cs instanceof Player) {
        players = new PlayerCollection(PlayerInterlayer.getInstanceFor((Player) cs));
      } else {
        BoxedMessage.sendTo(cs, 'c', "§c§l只限于玩家\n\n§c只有被玩家使用时才可以忽略参数玩家。");
        return false;
      }
    }
    int success = 0;
    int failed = 0;
    StringBuilder msg = new StringBuilder();
    for (PlayerInterlayer player : players) {
      result.mark();
      boolean yOnly = result.isBranch("y-only");
      Player playerP = player.getPlayer();
      Vector oldSpeed = playerP.getVelocity();
      double targetX;
      double targetY;
      double targetZ;
      try {
        targetX = yOnly ? oldSpeed.getX() : EvalHelper.evalNextDouble(result, player);
        targetY = EvalHelper.evalNextDouble(result, player);
        targetZ = yOnly ? oldSpeed.getZ() : EvalHelper.evalNextDouble(result, player);
        playerP.setVelocity(new Vector(targetX, targetY, targetZ));
      } catch(MineQueryRuntimeException ex) {
        msg.append("\n §c- §l无法设置玩家 ").append(player.getID()).append(" 的速度，计算表达式时发生了异常: ").append(ex.getMessage());
        failed++;
        continue;
      } finally {
        result.toLastMark();
      }
      msg.append("\n §a- §l成功将玩家 ").append(player.getID()).append(" 的速度设置为 (").append(targetX).append(", ").append(targetY).append(", ").append(targetZ).append(")。");
      success++;
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
    return true;
  }

}
