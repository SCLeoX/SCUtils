package net.minegeck.plugins.utils.confighelper;

import net.minegeck.plugins.utils.Annotations;
import net.minegeck.plugins.utils.TypeUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map.Entry;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public final class Config implements ConfigSLController {
  public class YamlConfigurationR extends YamlConfiguration {
    private class DumperOptionsR extends DumperOptions {
      @Override
      public void setAllowUnicode(boolean allowUnicode) {
        super.setAllowUnicode(true);
      }
    }
    public YamlConfigurationR() throws Exception {
      Field yamlOptionsField = YamlConfiguration.class.getDeclaredField("yamlOptions");
      yamlOptionsField.setAccessible(true);
      DumperOptionsR yamlOptions = new DumperOptionsR();
      yamlOptionsField.set(this, yamlOptions);

      Field yamlRepresenterField = YamlConfiguration.class.getDeclaredField("yamlRepresenter");
      yamlRepresenterField.setAccessible(true);
      YamlRepresenter yamlRepresenter = new YamlRepresenter();
      yamlRepresenterField.set(this, yamlRepresenter);

      Field yamlField = YamlConfiguration.class.getDeclaredField("yaml");
      yamlField.setAccessible(true);
      yamlField.set(this, new Yaml(new YamlConstructor(), yamlRepresenter, yamlOptions));
    }
  }
  public static Config loadInDataFolder(JavaPlugin plugin) {
    return loadInDataFolder(plugin, "config.yml");
  }
  public static Config loadInDataFolder(JavaPlugin plugin, String name) {
    File dataFolder = plugin.getDataFolder();
    if (!dataFolder.exists()) {
      dataFolder.mkdirs();
    }
    File container = new File(dataFolder, name).getParentFile();
    if (!container.exists()) {
      container.mkdirs();
    }
    return loadByFile(new File(dataFolder, name));
  }
  public static Config loadInWorkingPath(String name) {
    return loadByFile(new File(name));
  }
  public static Config loadByFile(File file) {
    try {
      if (!file.exists()) {
        file.createNewFile();
      }
    } catch (Exception ex) {
      throw new ConfigSLFailedException("配置文件创建失败。", ex);
    }
    return new Config(file);
  }

  private final File configFile;
  private YamlConfigurationR config;

  private void loadFromFile() {
    try {
      this.config = new YamlConfigurationR();
      this.config.load(configFile);
      this.config.options().copyDefaults(true);
    } catch(Exception ex) {
      throw new ConfigSLFailedException("配置文件读取失败。", ex);
    }
  }

  private Config(File configFile) {
    this.configFile = configFile;
    loadFromFile();
  }

  public YamlConfigurationR getConfig() {
    return this.config;
  }

  //区域分配
  private final HashMap<String, Type> sectionBindings = new HashMap<>();

  public Config addSectionBind(String path, Type klass) {
    sectionBindings.put(path, klass);
    if (this.config.getConfigurationSection(path) == null) {
      this.config.createSection(path);
    }
    return this;
  }

  //分布出去的实例
  private final HashMap<String, Object> dists = new HashMap<>();

  public Object load(String path, boolean keepTrace) {
    Object obj = Config.loadValueFromConfig(config, path, sectionBindings.get(path));
    if (keepTrace) {
      dists.put(path, obj);
    }
    return obj;
  }

  public void reload() {
    loadFromFile();
  }

  public void saveToFile() {
    try {
      this.config.save(configFile);
    } catch (IOException ex) {
      throw new ConfigSLFailedException("配置文件保存失败。", ex);
    }
  }

  public boolean saveAll() {
    try {
      for (Entry<String, Object> entry : dists.entrySet()) {
        saveValueToConfig(config, entry.getKey(), entry.getValue());
      }
      saveToFile();
      return true;
    } catch (ConfigSLFailedException ex) {
      System.out.println("配置文件保存失败。");
      ex.printStackTrace();
    }
    return false;
  }

  public void saveAllDefaults() {
    for (Entry<String, Type> entry : sectionBindings.entrySet()) {
      saveDefaultValue(config, entry.getKey(), entry.getValue());
    }
    try {
      this.config.save(configFile);
    } catch (IOException ex) {
      throw new ConfigSLFailedException("配置文件默认值保存失败。", ex);
    }
  }

  @Target(ElementType.FIELD)
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  public @interface ConfigableProperty {
    public String Key();
    public class NoDefault {}
    public byte DefaultByte() default 0;
    public short DefaultShort() default 0;
    public int DefaultInt() default 0;
    public long DefaultLong() default 0;
    public boolean DefaultBoolean() default false;
    public float DefaultFloat() default 0;
    public double DefaultDouble() default 0;
    public String DefaultString() default "";
    /*public byte[] DefaultByteList() default {};
    public short[] DefaultShortList() default {};
    public int[] DefaultIntList() default {};
    public long[] DefaultLongList() default {};
    public boolean[] DefaultBooleanList() default {};
    public float[] DefaultFloatList() default {};
    public double[] DefaultDoubleList() default {};
    public String[] DefaultStringList() default {};*/
  }

  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  public @interface CustomConfigable {
    public Class<? extends ConfigSLController> SLController() default Config.class;
  }

  public static void saveValueToConfig(ConfigurationSection section, String key, Object value) {
    if (value == null) {
      return;
    }
    Class<?> klass = value.getClass();
    CustomConfigable cc = klass.getAnnotation(CustomConfigable.class);
    Class<? extends ConfigSLController> slController;
    if (cc == null) {
      if (!AddonsLoader.addons.containsKey(klass)) {
        if (klass.equals(byte.class) || klass.equals(Byte.class)) {
          section.set(key, value);
        } else if (klass.equals(short.class) || klass.equals(Short.class)) {
          section.set(key, value);
        } else if (klass.equals(int.class) || klass.equals(Integer.class)) {
          section.set(key, value);
        } else if (klass.equals(long.class) || klass.equals(Long.class)) {
          section.set(key, value);
        } else if (klass.equals(boolean.class) || klass.equals(Boolean.class)) {
          section.set(key, value);
        } else if (klass.equals(float.class) || klass.equals(Float.class)) {
          section.set(key, value);
        } else if (klass.equals(double.class) || klass.equals(Double.class)) {
          section.set(key, value);
        } else if (klass.equals(String.class)) {
          section.set(key, value);
        } else {
          throw new ConfigSLFailedException("不支持写入配置文件的类型: " + klass.toString() + "。");
        }
        return;
      } else {
        slController = AddonsLoader.addons.get(klass);
      }
    } else {
      slController = cc.SLController();
    }
    Method saveToConfig;
    try {
      saveToConfig = slController.getDeclaredMethod("saveToConfig", Object.class, ConfigurationSection.class, String.class);
    } catch (NoSuchMethodException ex) {
      throw new ConfigSLFailedException("无法存储字段 " + key + "，其类型 " + klass + " 提供的存取控制器类 " + slController + " 中没有所需要的静态方法“saveToConfig”。", ex);
    }
    try {
      saveToConfig.invoke(null, value, section, key);
    } catch (IllegalAccessException ex) {
      throw new ConfigSLFailedException("无法存储字段 " + key + "，其类型 " + klass + " 提供的存取控制器类 " + slController + " 中的静态方法“saveToConfig”不是 public。", ex);
    } catch (IllegalArgumentException ex) {
      throw new ConfigSLFailedException("无法存储字段 " + key + "，其类型 " + klass + " 提供的存取控制器类 " + slController + " 中的静态方法“saveToConfig”的参数格式不正确。", ex);
    } catch (InvocationTargetException ex) {
      throw new ConfigSLFailedException("无法存储字段 " + key + "，其类型 " + klass + " 提供的存取控制器类 " + slController + " 中的方法“loadFromConfig”调用时发生了异常。", ex);
    }
  }

  public static void saveToConfig(Object value, ConfigurationSection sectionPrev, String key) {
    ConfigurationSection section = SectionHelper.getSection(sectionPrev, key);
    for (Field field : value.getClass().getDeclaredFields()) {
      ConfigableProperty cp = field.getAnnotation(ConfigableProperty.class);
      if (cp == null) {
        continue;
      }
      try {
        saveValueToConfig(section, cp.Key(), field.get(value));
      } catch (IllegalAccessException ex) {
        throw new ConfigSLFailedException("被存储的字段 " + field.getName() + " 不是 public。", ex);
      }
    }
  }

  public static Object loadValueFromConfig(ConfigurationSection section, String key, Type type) {
    Class<?> klass = TypeUtils.getClass(type);
    CustomConfigable cc = klass.getAnnotation(CustomConfigable.class);
    Class<? extends ConfigSLController> slController;
    if (cc == null) {
      if (!AddonsLoader.addons.containsKey(klass)) {
        try {
          if (klass.equals(byte.class) || klass.equals(Byte.class)) {
            return Byte.valueOf(section.get(key).toString());
          } else if (klass.equals(short.class) || klass.equals(Short.class)) {
            return Short.valueOf(section.get(key).toString());
          } else if (klass.equals(int.class) || klass.equals(Integer.class)) {
            return Integer.valueOf(section.get(key).toString());
          } else if (klass.equals(long.class) || klass.equals(Long.class)) {
            return Long.valueOf(section.get(key).toString());
          } else if (klass.equals(boolean.class) || klass.equals(Boolean.class)) {
            return Boolean.valueOf(section.get(key).toString());
          } else if (klass.equals(float.class) || klass.equals(Float.class)) {
            return Float.valueOf(section.get(key).toString());
          } else if (klass.equals(double.class) || klass.equals(Double.class)) {
            return Double.valueOf(section.get(key).toString());
          } else if (klass.equals(String.class)) {
            return (String) section.get(key);
          } else {
            throw new ConfigSLFailedException("不支持读取配置文件的类型: " + klass.toString() + "。");
          }
        } catch(ClassCastException | NumberFormatException ex) {
          throw new ConfigSLFailedException("某一配置文件中 " + section.getCurrentPath() + "." + key + " 格式配置错误。由于模型设计，我们无法获取具体是哪个配置文件，请见谅。", ex);
        } catch(NullPointerException ex) {
          throw new ConfigSLFailedException("某一配置文件中字段 " + section.getCurrentPath() + "." + key + " 丢失。由于模型设计，我们无法获取具体是哪个配置文件，请见谅。", ex);
        }
      } else {
        slController = AddonsLoader.addons.get(klass);
      }
    } else {
      slController = cc.SLController();
    }
    Method loadFromConfig;
    try {
      loadFromConfig = slController.getDeclaredMethod("loadFromConfig", Type.class, ConfigurationSection.class, String.class);
    } catch (NoSuchMethodException ex) {
      throw new ConfigSLFailedException("无法读取字段 " + key + "，其类型 " + klass + " 提供的存取控制器类 " + slController + " 中没有所需要的静态方法“loadFromConfig”。", ex);
    }
    try {
      return loadFromConfig.invoke(null, type, section, key);
    } catch (IllegalAccessException ex) {
      throw new ConfigSLFailedException("无法读取字段 " + key + "，其类型 " + klass + " 提供的存取控制器类 " + slController + " 中的静态方法“loadFromConfig”不是 public。", ex);
    } catch (IllegalArgumentException ex) {
      throw new ConfigSLFailedException("无法读取字段 " + key + "，其类型 " + klass + " 提供的存取控制器类 " + slController + " 中的静态方法“loadFromConfig”的参数格式不正确。", ex);
    } catch (InvocationTargetException ex) {
      throw new ConfigSLFailedException("无法读取字段 " + key + "，其类型 " + klass + " 提供的存取控制器类 " + slController + " 中的方法“loadFromConfig”调用时发生了异常。", ex);
    }
  }

  public static Object loadFromConfig(Type targetType, ConfigurationSection sectionPrev, String key) {
    ConfigurationSection section = SectionHelper.getSection(sectionPrev, key);
    Object target;
    Class<?> targetKlass = TypeUtils.getClass(targetType);
    try {
      target = targetKlass.newInstance();
    } catch (InstantiationException ex) {
      throw new ConfigSLFailedException("无法实例化类 " + targetKlass.toString() + " 因为其是抽象的或者是接口。", ex);
    } catch (IllegalAccessException ex) {
      throw new ConfigSLFailedException("无法实例化类 " + targetKlass.toString() + " 因为其所需要的构建器不是 public 的。", ex);
    }
    for (Field field : targetKlass.getDeclaredFields()) {
      ConfigableProperty cp = field.getAnnotation(ConfigableProperty.class);
      if (cp == null) {
        continue;
      }
      try {
        field.set(target, loadValueFromConfig(section, cp.Key(), field.getGenericType()));
      } catch (IllegalAccessException ex) {
        throw new ConfigSLFailedException("被读取的字段 " + field.getName() + " 不是 public。", ex);
      }
    }
    if (target instanceof HasPostLoadMethod) {
      ((HasPostLoadMethod) target).postLoad();
    }
    return target;
  }

  public static void saveDefaultValue(ConfigurationSection section, String key, Type type) {
    Class<?> klass = TypeUtils.getClass(type);
    CustomConfigable cc = klass.getAnnotation(CustomConfigable.class);
    Class<? extends ConfigSLController> slController;
    if (cc == null) {
      if (AddonsLoader.addons.containsKey(klass)) {
        slController = AddonsLoader.addons.get(klass);
      } else {
        return;
      }
    } else {
      slController = cc.SLController();
    }
    Method saveDefaults;
    try {
      saveDefaults = slController.getDeclaredMethod("saveDefaults", Type.class, ConfigurationSection.class, String.class);
    } catch (NoSuchMethodException ex) {
      throw new ConfigSLFailedException("无法保存字段 " + key + " 的默认值，其类型 " + klass + " 提供的存取控制器类 " + slController + " 中没有所需要的静态方法“saveDefaults”。", ex);
    }
    try {
      saveDefaults.invoke(null, type, section, key);
    } catch (IllegalAccessException ex) {
      throw new ConfigSLFailedException("无法保存字段 " + key + " 的默认值，其类型 " + klass + " 提供的存取控制器类 " + slController + " 中的静态方法“saveDefaults”不是 public。", ex);
    } catch (IllegalArgumentException ex) {
      throw new ConfigSLFailedException("无法保存字段 " + key + " 的默认值，其类型 " + klass + " 提供的存取控制器类 " + slController + " 中的静态方法“saveDefaults”的参数格式不正确。", ex);
    } catch (InvocationTargetException ex) {
      throw new ConfigSLFailedException("无法保存字段 " + key + " 的默认值，其类型 " + klass + " 提供的存取控制器类 " + slController + " 中的方法“saveDefaults”调用时发生了异常。", ex);
    }
  }

  public static void saveDefaults(Type targetType, ConfigurationSection sectionPrev, String key) {
    ConfigurationSection section = SectionHelper.getSection(sectionPrev, key);
    Class<?> targetKlass = TypeUtils.getClass(targetType);
    for (Field field : targetKlass.getDeclaredFields()) {
      ConfigableProperty cp = field.getAnnotation(ConfigableProperty.class);
      if (cp == null) {
        continue;
      }
      Class<?> klass = field.getType();
      if (klass.equals(byte.class) || klass.equals(Byte.class)) {
        section.addDefault(cp.Key(), cp.DefaultByte());
      } else if (klass.equals(short.class) || klass.equals(Short.class)) {
        section.addDefault(cp.Key(), cp.DefaultShort());
      } else if (klass.equals(int.class) || klass.equals(Integer.class)) {
        section.addDefault(cp.Key(), cp.DefaultInt());
      } else if (klass.equals(long.class) || klass.equals(Long.class)) {
        section.addDefault(cp.Key(), cp.DefaultLong());
      } else if (klass.equals(boolean.class) || klass.equals(Boolean.class)) {
        section.addDefault(cp.Key(), cp.DefaultBoolean());
      } else if (klass.equals(float.class) || klass.equals(Float.class)) {
        section.addDefault(cp.Key(), cp.DefaultFloat());
      } else if (klass.equals(double.class) || klass.equals(Double.class)) {
        section.addDefault(cp.Key(), cp.DefaultDouble());
      } else if (klass.equals(String.class)) {
        section.addDefault(cp.Key(), cp.DefaultString());
      } else {
        saveDefaultValue(section, cp.Key(), field.getGenericType());
      }
    }
  }
}