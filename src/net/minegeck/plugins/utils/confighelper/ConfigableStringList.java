package net.minegeck.plugins.utils.confighelper;

import net.minegeck.plugins.utils.Annotations;
import net.minegeck.plugins.utils.confighelper.Config.CustomConfigable;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
@CustomConfigable(SLController = ConfigableStringList.class)
public final class ConfigableStringList extends ArrayList<String> implements ConfigSLController {
  private static final long serialVersionUID = 1L;
  public static void saveToConfig(Object v, ConfigurationSection section, String key) {
    ConfigableStringList value = (ConfigableStringList) v;
    section.set(key, value);
  }
  public static Object loadFromConfig(Type targetType, ConfigurationSection section, String key) {
    List<String> list = section.getStringList(key);
    if (list == null) {
      return new ConfigableStringList();
    }
    ConfigableStringList target = new ConfigableStringList();
    for (String item : list) {
      target.add(item);
    }
    return target;
  }
  public static void saveDefaults(Type targetType, ConfigurationSection section, String key) {
    section.addDefault(key, new String[] {});
  }
}
