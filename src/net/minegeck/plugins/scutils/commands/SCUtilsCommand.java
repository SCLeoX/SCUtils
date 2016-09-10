package net.minegeck.plugins.scutils.commands;

import net.minegeck.plugins.utils.Annotations;
import net.minegeck.plugins.utils.commandparser.Format;
import org.bukkit.command.CommandExecutor;

import java.util.ArrayList;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public abstract class SCUtilsCommand implements CommandExecutor {
  private static final ArrayList<SCUtilsCommand> records = new ArrayList<>();
  public static ArrayList<SCUtilsCommand> getRecords() {
    return records;
  }
  public SCUtilsCommand record() {
    records.add(this);
    return this;
  }
  public abstract Format getFormat();
  public abstract String getBasicFunction();
  public abstract String getBasicFormat();
}
