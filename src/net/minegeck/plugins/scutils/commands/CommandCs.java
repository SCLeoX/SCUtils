package net.minegeck.plugins.scutils.commands;

import net.minegeck.plugins.scutils.Main;
import net.minegeck.plugins.utils.Annotations;
import net.minegeck.plugins.utils.BoxedMessage;
import net.minegeck.plugins.utils.commandparser.*;
import net.minegeck.plugins.utils.confighelper.ConfigableStringList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.text.MessageFormat;
import java.util.Map;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class CommandCs extends SCUtilsCommand {

  public static final Format format = Format.createFormat(new Case()
    .kase(new Case()
      .field(ConstField.expect("list").ignoreCase())
      .func("列出所有可用的命令序列。")
      .tag("list-all")
    )
    .kase(new Case()
      .field(ConstField.expect("delete").ignoreCase())
      .field("序列名", StringField.any().identifier())
      .tag("delete")
      .func("删除一个命令序列")
    )
    .kase(new Case()
      .field(ConstField.expect("create").ignoreCase())
      .field("序列名", StringField.any().identifier())
      .tag("create")
      .func("创建一个命令序列")
    )
    .kase(new Case()
      .field(ConstField.expect("insert").ignoreCase())
      .field("序列名", StringField.any().identifier())
      .field("位置", IntField.natural())
      .field("命令", StringField.any().unlimit())
      .tag("insert")
      .func("在一个命令序列的指定命令之后插入一条命令")
    )
    .kase(new Case()
      .field(ConstField.expect("remove").ignoreCase())
      .field("序列名", StringField.any().identifier())
      .field("位置", IntField.postive())
      .tag("remove")
      .func("从一个命令序列中删除一个命令")
    )
    .kase(new Case()
      .field(ConstField.expect("replace").ignoreCase())
      .field("序列名", StringField.any().identifier())
      .field("位置", IntField.natural())
      .field("命令", StringField.any().unlimit())
      .tag("replace")
      .func("替换一个命令序列中的命令")
    )
    .kase(new Case()
      .field(ConstField.expect("append").ignoreCase())
      .field("序列名", StringField.any().identifier())
      .field("命令", StringField.any().unlimit())
      .tag("append")
      .func("在一个命令序列中追加一个命令")
    )
    .kase(new Case()
      .field(ConstField.expect("list").ignoreCase())
      .field("序列名", StringField.any().identifier())
      .tag("list")
      .func("列出一个命令序列中的所有命令")
    )
  );

  @Override
  public Format getFormat() {
    return format;
  }

  @Override
  public String getBasicFunction() {
    return "操作命令序列";
  }

  @Override
  public String getBasicFormat() {
    return "/cs";
  }

  @Override
  public boolean onCommand(CommandSender cs, Command cmnd, String alias, String[] args) {
    ParseResult result = format.parse(args);
    if (result.tellCommandSenderIfFailed(cs, alias)) {
      return false;
    }
    if (result.isBranch("list-all")) {
      StringBuilder sb = new StringBuilder();
      sb.append("§a§l所有的命令序列\n");
      for (Map.Entry<String, ConfigableStringList> entry : Main.configRoot.commandSequences.entrySet()) {
        sb.append("\n §e- §l").append(entry.getKey()).append(" §e(§l").append(entry.getValue().size()).append("§e)");
      }
      BoxedMessage.sendTo(cs, 'a', sb.toString());
    } else if (result.isBranch("delete")) {
      String seqName = result.nextString();
      if (Main.configRoot.commandSequences.containsKey(seqName)) {
        Main.configRoot.commandSequences.remove(seqName);
        Main.config.saveAll();
        BoxedMessage.sendTo(cs, 'a', MessageFormat.format("§a§l删除成功\n\n§a命令序列 §e§l{0} §a已被删除。", seqName));
      } else {
        BoxedMessage.sendTo(cs, 'c', MessageFormat.format("§c§l删除失败\n\n§c无法找到命令序列 §e§l{0}§c。", seqName));
      }
    } else if (result.isBranch("create")) {
      String seqName = result.nextString();
      if (Main.configRoot.commandSequences.containsKey(seqName)) {
        BoxedMessage.sendTo(cs, 'c', MessageFormat.format("§c§l命令序列已存在\n\n§c命令序列 §e§l{0}§c 事先已存在。", seqName));
        return false;
      }
      Main.configRoot.commandSequences.put(seqName, new ConfigableStringList());
      Main.config.saveAll();
      BoxedMessage.sendTo(cs, 'a', MessageFormat.format("§a§l命令序列创建成功\n\n§a命令序列 §e§l{0}§a 已创建。", seqName));
    } else if (result.isBranch("insert")) {
      String seqName = result.nextString();
      int pos = result.nextInt();
      String cmd = result.nextString();
      if (!Main.configRoot.commandSequences.containsKey(seqName)) {
        BoxedMessage.sendTo(cs, 'c', MessageFormat.format("§c§l命令序列不存在\n\n§c无法找到命令序列 §e§l{0}§c。请使用 §e§l/cs create {0} §c来创建。", seqName));
        return false;
      }
      ConfigableStringList sequence = Main.configRoot.commandSequences.get(seqName);
      sequence.add(pos, cmd);
      Main.config.saveAll();
      BoxedMessage.sendTo(cs, 'a', "§a§l操作成功\n\n§a命令已插入。");
    } else if (result.isBranch("remove")) {
      String seqName = result.nextString();
      int pos = result.nextInt();
      if (!Main.configRoot.commandSequences.containsKey(seqName)) {
        BoxedMessage.sendTo(cs, 'c', MessageFormat.format("§c§l命令序列不存在\n\n§c无法找到命令序列 §e§l{0}§c。请使用 §e§l/cs create {0} §c来创建。", seqName));
        return false;
      }
      ConfigableStringList sequence = Main.configRoot.commandSequences.get(seqName);
      if (sequence.size() < pos) {
        BoxedMessage.sendTo(cs, 'c', MessageFormat.format("§c§l提供的位置过大\n\n§c命令序列 §e§l{0} §c一共也只有 §e§l{1} §c个命令。", seqName, sequence.size()));
        return false;
      }
      sequence.remove(pos - 1);
      Main.config.saveAll();
      BoxedMessage.sendTo(cs, 'a', "§a§l操作成功\n\n§a命令已删除。");
    } else if (result.isBranch("replace")) {
      String seqName = result.nextString();
      int pos = result.nextInt();
      String cmd = result.nextString();
      if (!Main.configRoot.commandSequences.containsKey(seqName)) {
        BoxedMessage.sendTo(cs, 'c', MessageFormat.format("§c§l命令序列不存在\n\n§c无法找到命令序列 §e§l{0}§c。请使用 §e§l/cs create {0} §c来创建。", seqName));
        return false;
      }
      ConfigableStringList sequence = Main.configRoot.commandSequences.get(seqName);
      if (sequence.size() < pos) {
        BoxedMessage.sendTo(cs, 'c', MessageFormat.format("§c§l提供的位置过大\n\n§c命令序列 §e§l{0} §c一共也只有 §e§l{1} §c个命令。", seqName, sequence.size()));
        return false;
      }
      sequence.set(pos - 1, cmd);
      Main.config.saveAll();
      BoxedMessage.sendTo(cs, 'a', "§a§l操作成功\n\n§a命令已替换。");
    } else if (result.isBranch("append")) {
      String seqName = result.nextString();
      String cmd = result.nextString();
      if (!Main.configRoot.commandSequences.containsKey(seqName)) {
        BoxedMessage.sendTo(cs, 'c', MessageFormat.format("§c§l命令序列不存在\n\n§c无法找到命令序列 §e§l{0}§c。请使用 §e§l/cs create {0} §c来创建。", seqName));
        return false;
      }
      ConfigableStringList sequence = Main.configRoot.commandSequences.get(seqName);
      sequence.add(cmd);
      Main.config.saveAll();
      BoxedMessage.sendTo(cs, 'a', "§a§l操作成功\n\n§a命令已添加。");
    } else if (result.isBranch("list")) {
      String seqName = result.nextString();
      if (!Main.configRoot.commandSequences.containsKey(seqName)) {
        BoxedMessage.sendTo(cs, 'c', MessageFormat.format("§c§l命令序列不存在\n\n§c无法找到命令序列 §e§l{0}§c。请使用 §e§l/cs create {0} §c来创建。", seqName));
        return false;
      }
      ConfigableStringList sequence = Main.configRoot.commandSequences.get(seqName);
      StringBuilder sb = new StringBuilder();
      sb.append("§a§l命令列表\n");
      int i = 1;
      for (String cmd : sequence) {
        sb.append("\n §e").append(i++).append(": /§l").append(cmd);
      }
      BoxedMessage.sendTo(cs, 'a', sb.toString());
    }
    return true;
  }

}
