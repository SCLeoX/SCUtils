package net.minegeck.plugins.utils.commandparser;

import net.minegeck.plugins.utils.Annotations;
import org.bukkit.command.CommandSender;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public interface CaseProcessor {
  public boolean caseMet(ParseResult result, CommandSender sender);
}
