package net.minegeck.plugins.scutils.commands;

import net.minegeck.plugins.utils.Annotations;
import net.minegeck.plugins.utils.BoxedMessage;
import net.minegeck.plugins.utils.commandparser.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class CommandExplain extends SCUtilsCommand {

  public static final Format format = Format.createFormat(new Case()
    .kase(new Case()
      .kase(new Case()
        .field(ConstField.expect("sel").ignoreCase())
      )
      .kase(new Case()
        .field(ConstField.expect("selector").ignoreCase())
      )
      .merge(new Case()
        .field("选择器", MineQueryField.selector().toEnd())
        .func("分析一个 MineQuery 选择器")
        .tag("selector")
      )
    )
    .kase(new Case()
      .kase(new Case()
        .field(ConstField.expect("exp").ignoreCase())
      )
      .kase(new Case()
        .field(ConstField.expect("expression").ignoreCase())
      )
      .merge(new Case()
        .field("表达式", MineQueryField.expression().toEnd())
        .func("分析一个 MineQuery 表达式")
        .tag("expression")
      )
    )
  );

  @Override
  public Format getFormat() {
    return format;
  }

  @Override
  public String getBasicFunction() {
    return "分析一个 MineQuery 选择器或者表达式";
  }

  @Override
  public String getBasicFormat() {
    return "/explain";
  }

  @Override
  public boolean onCommand(CommandSender cs, Command cmnd, String alias, String[] args) {
    ParseResult result = format.parse(args);
    if (result.tellCommandSenderIfFailed(cs, alias)) {
      return false;
    }
    StringBuilder sb = new StringBuilder();
    sb.append("§b§l对于此");
    String target;
    if (result.isBranch("expression")) {
      target = "表达式";
    } else if (result.isBranch("selector")) {
      target = "选择器";
    } else {
      return false;
    }
    sb.append(target);
    sb.append("所构建的 AST 如下\n\n§e");
    sb.append(result.nextASTNode().toString().replaceAll("\n", "\n§e"));
    sb.append("\n§b§l目标根节点类型: ").append(target);
    sb.append("\n§b§l使用命令 §e§l/tokenize §b§l来查看令牌化的结果。");
    BoxedMessage.sendTo(cs, 'b', sb.toString());
    return true;
  }

}
