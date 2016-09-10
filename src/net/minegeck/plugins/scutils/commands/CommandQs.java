package net.minegeck.plugins.scutils.commands;

import net.minegeck.plugins.scutils.Main;
import net.minegeck.plugins.scutils.PackedSelector;
import net.minegeck.plugins.scutils.minequery.MineQuerySyntaxException;
import net.minegeck.plugins.utils.Annotations;
import net.minegeck.plugins.utils.BoxedMessage;
import net.minegeck.plugins.utils.commandparser.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.text.MessageFormat;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class CommandQs extends SCUtilsCommand {

  public static final Format format = Format.createFormat(new Case()
    .kase(new Case()
      .optional(new Case()
        .field(ConstField.expect("list").ignoreCase())
      )
      .merge(new Case()
        .tag("list")
        .func("列出所有保存的查询选择器")
      )
    )
    .kase(new Case()
      .kase(new Case()
        .field(ConstField.expect("show").ignoreCase())
      )
      .kase(new Case()
        .field(ConstField.expect("showast").ignoreCase())
        .tag("ast-wanted")
      )
      .merge(new Case()
        .field("查询选择器名", StringField.any().identifier())
        .tag("show")
        .func("显示一个查询选择器的内容")
      )
    )
    .kase(new Case()
      .field(ConstField.expect("remove").ignoreCase())
      .field("查询选择器名", StringField.any().identifier())
      .tag("remove")
      .func("删除一个查询选择器")
    )
    .kase(new Case()
      .field(ConstField.expect("set").ignoreCase())
      .merge(new Case()
        .field("查询选择器名", StringField.any().identifier())
        .field("内容", StringField.any().unlimit())
        .tag("set")
        .func("设置一个查询选择器的内容")
      )
    )
  );

  @Override
  public Format getFormat() {
    return format;
  }

  @Override
  public String getBasicFunction() {
    return "操作保存的查询选择器";
  }

  @Override
  public String getBasicFormat() {
    return "/qs";
  }

  @Override
  public boolean onCommand(CommandSender cs, Command cmnd, String alias, String[] args) {
    ParseResult result = format.parse(args);
    if (result.tellCommandSenderIfFailed(cs, alias)) {
      return false;
    }
    if (result.isBranch("list")) {
      StringBuilder sb = new StringBuilder();
      sb.append("§b§l所有的查询选择器\n");
      for (String name : Main.configRoot.selectors.keySet()) {
        sb.append("\n §e- §l").append(name);
      }
      BoxedMessage.sendTo(cs, 'b', sb.toString());
    } else if (result.isBranch("show")) {
      String targetName = result.nextString();
      if (!Main.configRoot.selectors.containsKey(targetName)) {
        BoxedMessage.sendTo(cs, 'c', "§c§l查询选择器不存在\n\n§c找不到名为§e§l " + targetName + " §c的查询选择器。");
        return false;
      }
      PackedSelector pSelector = Main.configRoot.selectors.get(targetName);
      StringBuilder sb = new StringBuilder();
      if (result.isBranch("ast-wanted")) {
        sb.append("§b§l查询选择器 ").append(targetName).append(" 的 AST 如下\n\n§e").append(pSelector.getSelector().toString().replaceAll("\n", "\n§e"));
      } else {
        sb.append("§b§l查询选择器 ").append(targetName).append(" 的源码如下\n\n§e").append(pSelector.getSource());
      }
      BoxedMessage.sendTo(cs, 'b', sb.toString());
    } else if (result.isBranch("remove")) {
      String targetName = result.nextString();
      if (!Main.configRoot.selectors.containsKey(targetName)) {
        BoxedMessage.sendTo(cs, 'c', "§c§l查询选择器不存在\n\n§c找不到名为§e§l " + targetName + " §c的查询选择器。");
        return false;
      }
      Main.configRoot.selectors.remove(targetName);
      Main.config.saveAll();
      BoxedMessage.sendTo(cs, 'a', MessageFormat.format("§a§l删除成功\n\n§a已删除名为 §e§l{0} §a的查询选择器。", targetName));
    } else if (result.isBranch("set")) {
      String targetName = result.nextString();
      String content = result.nextString();
      PackedSelector pSelector;
      try {
        pSelector = new PackedSelector(content);
      } catch(MineQuerySyntaxException ex) {
        BoxedMessage.sendTo(cs, 'c', "§c§l查询选择器设置失败\n\n§c" + ex.getMessage());
        return false;
      }
      Main.configRoot.selectors.put(targetName, pSelector);
      Main.config.saveAll();
      BoxedMessage.sendTo(cs, 'a', MessageFormat.format("§a§l查询选择器设置成功\n\n§a已设置名为 §e§l{0} §a的查询选择器。", targetName));
    }
    return true;
  }

}
