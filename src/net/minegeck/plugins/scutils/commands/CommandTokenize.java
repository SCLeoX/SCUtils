package net.minegeck.plugins.scutils.commands;

import net.minegeck.plugins.scutils.minequery.MineQuerySyntaxException;
import net.minegeck.plugins.scutils.minequery.token.Token;
import net.minegeck.plugins.scutils.minequery.token.Tokenizer;
import net.minegeck.plugins.utils.Annotations;
import net.minegeck.plugins.utils.BoxedMessage;
import net.minegeck.plugins.utils.commandparser.Case;
import net.minegeck.plugins.utils.commandparser.Format;
import net.minegeck.plugins.utils.commandparser.ParseResult;
import net.minegeck.plugins.utils.commandparser.StringField;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class CommandTokenize extends SCUtilsCommand {

  public static final Format format = Format.createFormat(new Case()
    .field("MineQuery 语句", StringField.any().unlimit())
    .func("获得一个 MineQuery 语句的令牌化结果")
  );

  @Override
  public Format getFormat() {
    return format;
  }

  @Override
  public String getBasicFunction() {
    return "获得一个 MineQuery 语句的令牌化结果";
  }

  @Override
  public String getBasicFormat() {
    return "/tokenize";
  }

  @Override
  public boolean onCommand(CommandSender cs, Command cmnd, String alias, String[] args) {
    ParseResult result = format.parse(args);
    if (result.tellCommandSenderIfFailed(cs, alias)) {
      return false;
    }
    StringBuilder sb = new StringBuilder();
    sb.append("§b§l对于该语句的令牌化结果如下\n");
    try {
      for (Token token : new Tokenizer(result.nextString()).tokenize().getList()) {
        sb.append("\n §e- §l").append(token.toString());
      }
      BoxedMessage.sendTo(cs, 'b', sb.toString());
      return true;
    } catch (MineQuerySyntaxException ex) {
      BoxedMessage.sendTo(cs, 'c', "§c§l令牌化失败\n\n§c" + ex.getMessage());
      return false;
    }
  }

}
