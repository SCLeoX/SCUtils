package net.minegeck.plugins.scutils.minequery.ast;

import net.minegeck.plugins.utils.Annotations;
import net.minegeck.plugins.utils.confighelper.Config;
import net.minegeck.plugins.utils.confighelper.ConfigSLController;
import net.minegeck.plugins.utils.confighelper.ConfigSLFailedException;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.Type;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
@Config.CustomConfigable(SLController = AnyType.class)
public abstract class AnyType<T> implements ConfigSLController {

  public static void saveToConfig(Object value, ConfigurationSection sectionPrev, String key) {}

  public static Object loadFromConfig(Type targetType, ConfigurationSection sectionPrev, String key) {
    ConfigurationSection section = sectionPrev.getConfigurationSection(key);
    String type = section.getString("类型");
    switch (type) {
      case "布尔型":
        return new BooleanType(section.getBoolean("值"));
      case "字符串型":
        return new StringType(section.getString("值"));
      case "双精度小数型":
        return new NumberType(section.getDouble("值"));
      default:
        throw new ConfigSLFailedException("不知道的类型 " + type);
    }
  }

  public static void saveDefaults(Type targetType, ConfigurationSection sectionPrev, String key) {}

  public enum RealType {

    STRING("字符串"),

    NUMBER("数字"),

    BOOLEAN("布尔值");

    private final String name;

    public String getName() {
      return name;
    }
    private RealType(String name) {
      this.name = name;
    }
  }
  public abstract RealType getRealType();
  public abstract T get();
  public abstract void set(T value);
}
