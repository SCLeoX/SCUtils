package net.minegeck.plugins.utils.confighelper;

import net.minegeck.plugins.utils.Annotations.Info;
import net.minegeck.plugins.utils.TypeUtils;
import net.minegeck.plugins.utils.confighelper.Config.CustomConfigable;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map.Entry;

@Info(
  作者 = "SCLeo",
  许可 = "GPLv3",
  注意 = "虽然这个类很短，但是我，SCLeo，从 2016.8.2 开始，花了数十小时在这个简单的类上。" +
        "第一个版本于 2016.8.4 日上午6时完工，彻夜未眠，特此纪念。届时，功能尚不齐全，但是核心问题已解决。已感动哭。")
@CustomConfigable(SLController = ConfigableMap.class)
public final class ConfigableMap<T> extends HashMap<String, T> implements ConfigSLController {
  private static final long serialVersionUID = 1L;
  public static void saveToConfig(Object v, ConfigurationSection sectionPrev, String key) {
    ConfigurationSection section = sectionPrev.createSection(key);
    ConfigableMap<?> value = (ConfigableMap<?>) v;
    for (Entry<String, ?> entry : value.entrySet()) {
      Config.saveValueToConfig(section, entry.getKey(), entry.getValue());
    }
  }
  public static Object loadFromConfig(Type targetType, ConfigurationSection sectionPrev, String key) {
    ConfigurationSection section = SectionHelper.getSection(sectionPrev, key);
    ConfigableMap<Object> target = new ConfigableMap<>();
    Type itemType = TypeUtils.getActualParameterizedType(targetType, 0);
    for (String subKey : section.getKeys(false)) {
      target.put(subKey, Config.loadValueFromConfig(section, subKey, itemType));
    }
    return target;
  }
  public static void saveDefaults(Type targetType, ConfigurationSection section, String key) {}
}
