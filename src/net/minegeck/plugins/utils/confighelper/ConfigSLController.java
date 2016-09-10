package net.minegeck.plugins.utils.confighelper;

import net.minegeck.plugins.utils.Annotations;

/**
 * 代表一个存取控制器。
 * @author SCLeo
 */
@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public interface ConfigSLController {
  //实现本接口的所有类必须拥有以下静态方法！
  //public static void saveToConfig(Object value, ConfigurationSection sectionPrev, String key)
  //public static Object loadFromConfig(Type targetType, ConfigurationSection sectionPrev, String key)
  //public static void saveDefaults(Type targetType, ConfigurationSection sectionPrev, String key);
}
