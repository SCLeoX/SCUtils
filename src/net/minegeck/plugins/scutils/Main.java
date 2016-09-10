package net.minegeck.plugins.scutils;

import net.minegeck.plugins.scutils.commands.*;
import net.minegeck.plugins.scutils.listeners.GlobalQuerySelectorListener;
import net.minegeck.plugins.scutils.listeners.NoCmdListener;
import net.minegeck.plugins.scutils.listeners.PlayerCacheRemoverListener;
import net.minegeck.plugins.utils.Annotations;
import net.minegeck.plugins.utils.confighelper.Config;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class Main extends JavaPlugin {

  public static Logger logger;
  public static Main current;
  public static Config config;
  public static ConfigRoot configRoot;

  @Override
  public void onEnable() {
    logger = this.getLogger();
    current = this;
    logger.info("加载配置文件...");
    config = Config.loadInDataFolder(this);
    config.addSectionBind("配置", ConfigRoot.class);
    config.saveAllDefaults();
    configRoot = (ConfigRoot) config.load("配置", true);
    logger.info("注册命令...");
    getCommand("scuclass"   ).setExecutor(new CommandClass   ().record());
    getCommand("scucs"      ).setExecutor(new CommandCs      ().record());
    getCommand("scudata"    ).setExecutor(new CommandData    ().record());
    getCommand("scuexec"    ).setExecutor(new CommandExec    ().record());
    getCommand("scuexplain" ).setExecutor(new CommandExplain ().record());
    getCommand("scuhelp"    ).setExecutor(new CommandHelp    ().record());
    getCommand("scunocmd"   ).setExecutor(new CommandNoCmd   ().record());
    getCommand("scuqs"      ).setExecutor(new CommandQs      ().record());
    getCommand("scurcs"     ).setExecutor(new CommandRcs     ().record());
    getCommand("scuselect"  ).setExecutor(new CommandSelect  ().record());
    getCommand("scutokenize").setExecutor(new CommandTokenize().record());
    getCommand("scuvelocity").setExecutor(new CommandVelocity().record());
    logger.info("注册监听器...");
    if (configRoot.enableGlobalQuerySelector) {
      getServer().getPluginManager().registerEvents(new GlobalQuerySelectorListener(), this);
    }
    getServer().getPluginManager().registerEvents(new NoCmdListener(), this);
    getServer().getPluginManager().registerEvents(new PlayerCacheRemoverListener(), this);
    logger.info("加载完成。");
  }

}
