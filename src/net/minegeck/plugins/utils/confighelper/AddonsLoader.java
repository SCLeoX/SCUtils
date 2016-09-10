package net.minegeck.plugins.utils.confighelper;

import net.minegeck.plugins.utils.Annotations;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class AddonsLoader {
  private static AddonsLoader INSTANCE;
  public static AddonsLoader getInstance(JavaPlugin plugin) {
    if (INSTANCE == null) {
      return INSTANCE = new AddonsLoader(plugin);
    } else {
      return INSTANCE;
    }
  }
  public static JavaPlugin plugin;
  private AddonsLoader(JavaPlugin targetPlugin) {
    plugin = targetPlugin;
  }
  public static HashMap<Class<?>, Class<? extends ConfigSLController>> addons = new HashMap<>();
  public AddonsLoader load(Class<?> workOn, Class<? extends ConfigSLController> sl) {
    addons.put(workOn, sl);
    return this;
  }
}
