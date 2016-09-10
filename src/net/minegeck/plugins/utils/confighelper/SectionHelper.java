package net.minegeck.plugins.utils.confighelper;

import net.minegeck.plugins.utils.Annotations;
import org.bukkit.configuration.ConfigurationSection;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class SectionHelper {
  public static ConfigurationSection getSection(ConfigurationSection section, String path) {
    ConfigurationSection old = section.getConfigurationSection(path);
    if (old == null) {
      return section.createSection(path);
    } else {
      return old;
    }
  }
}
