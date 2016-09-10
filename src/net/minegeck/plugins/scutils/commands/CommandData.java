package net.minegeck.plugins.scutils.commands;

import net.minegeck.plugins.scutils.PlayerInterlayer;
import net.minegeck.plugins.scutils.minequery.PlayerCollection;
import net.minegeck.plugins.scutils.minequery.ast.ASTExpression;
import net.minegeck.plugins.scutils.minequery.ast.AnyType;
import net.minegeck.plugins.scutils.minequery.ast.MineQueryRuntimeException;
import net.minegeck.plugins.utils.Annotations;
import net.minegeck.plugins.utils.BoxedMessage;
import net.minegeck.plugins.utils.commandparser.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map.Entry;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class CommandData extends SCUtilsCommand {

  public static final Format format = Format.createFormat(new Case()
    .kase(new Case()
      .func("设置一个或多个玩家的某个数据")
      .tag("set")
      .field(ConstField.expect("set").ignoreCase())
      .optional(new Case()
        .field("玩家", PlayerField.any())
        .tag("player-given")
      )
      .merge(new Case()
        .field("变量名", StringField.any().identifier())
        .kase(new Case()
          .field(ConstField.expect("null"))
          .tag("set-to-null")
        )
        .kase(new Case()
          .field("值", MineQueryField.expression().toEnd())
        )
      )
    )
    .kase(new Case()
      .func("列出一个玩家的所有数据")
      .tag("list")
      .optional(new Case()
        .field(ConstField.expect("list").ignoreCase())
      )
      .merge(new Case()
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
    return "修改或查看玩家的数据";
  }

  @Override
  public String getBasicFormat() {
    return "/data";
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
    if (result.isBranch("set")) {
      String key = result.nextString();
      int success = 0;
      int failed = 0;
      StringBuilder msg = new StringBuilder();
      if (result.isBranch("set-to-null")) {
        for (PlayerInterlayer player : players) {
          if (player.getData().remove(key) != null) {
            player.save();
            msg.append("\n §a- §l为玩家 ").append(player.getID()).append(" 删除了字段为 ").append(key).append(" 的数据。");
            success++;
          } else {
            msg.append("\n §c- §l无法为玩家 ").append(player.getID()).append(" 删除字段为 ").append(key).append(" 的数据，字段不存在。");
            failed++;
          }
        }
      } else {
        ASTExpression expression = (ASTExpression) result.nextASTNode();
        for (PlayerInterlayer player : players) {
          AnyType value;
          try {
            value = expression.eval(player.getIdentifierProvider());
          } catch (MineQueryRuntimeException ex) {
            msg.append("\n §c- §l无法设置玩家 ").append(player.getID()).append(" 的字段为 ").append(key).append(" 的数据，计算表达式时发生了异常: ").append(ex.getMessage());
            failed++;
            continue;
          }
          player.getData().put(key, value);
          player.save();
          msg.append("\n §a- §l把玩家 ").append(player.getID()).append(" 的字段为 ").append(key).append(" 的数据设置为 ").append(value.toString()).append("。");
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
    } else if (result.isBranch("list")) {
      PlayerInterlayer player = players.iterator().next();
      StringBuilder sb = new StringBuilder();
      sb.append("§b§l玩家 ").append(player.getID()).append(" 的所有数据\n");
      for (Entry<String, AnyType> entry : player.getData().entrySet()) {
        sb.append("\n §e- §l").append(entry.getKey()).append("§e: §l(").append(entry.getValue().getRealType().getName()).append(") ").append(entry.getValue().toString());
      }
      BoxedMessage.sendTo(cs, 'b', sb.toString());
    }
    return true;
  }

}