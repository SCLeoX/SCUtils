package net.minegeck.plugins.scutils;

import net.minegeck.plugins.scutils.minequery.PlayerCollection;
import net.minegeck.plugins.scutils.minequery.ast.*;
import net.minegeck.plugins.utils.Annotations;
import net.minegeck.plugins.utils.confighelper.Config;
import net.minegeck.plugins.utils.confighelper.ConfigableMap;
import net.minegeck.plugins.utils.confighelper.ConfigableStringList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
@Config.CustomConfigable
/**
 * 我尽可能将所有与 Bukkit 相关的操作集成到这个类中，这样 MineQuery 解析器可以相对来说更独立。
 */
public class PlayerInterlayer {

  // -------------------- 静态 --------------------

  private final static HashMap<String, PlayerInterlayer> cache = new HashMap<>();
  public static PlayerInterlayer getInstanceFor(Player player) {
    if (player == null) {
      return null;
    }
    String pnl = player.getName().toLowerCase();
    if (cache.containsKey(pnl)) {
      return cache.get(pnl);
    }
    Config datafile = Config.loadInDataFolder(Main.current, "data/" + pnl + ".yml");
    datafile.addSectionBind("数据", PlayerInterlayer.class);
    datafile.saveAllDefaults();
    PlayerInterlayer pi = (PlayerInterlayer) datafile.load("数据", true);
    pi.init(player, datafile);
    cache.put(pnl, pi);
    return pi;
  }
  public static boolean removeInstanceFor(Player player) {
    if (player == null) {
      return false;
    }
    String pnl = player.getName().toLowerCase();
    if (!cache.containsKey(pnl)) {
      return false;
    }
    cache.remove(pnl);
    return true;
  }
  public static PlayerInterlayer getPlayerByID(String id) {
    Player player = Bukkit.getPlayer(id);
    if (player == null) {
      return null;
    }
    if (player.getName().equals(id)) {
      return PlayerInterlayer.getInstanceFor(player);
    } else {
      return null;
    }
  }
  public static PlayerCollection getPlayersByClass(String klass) {
    PlayerCollection pc = getOnlinePlayers();
    Iterator<PlayerInterlayer> iter = pc.iterator();
    while (iter.hasNext()) {
      PlayerInterlayer player = iter.next();
      if (!player.hasClass(klass)) {
        iter.remove();
      }
    }
    return pc;
  }
  public static PlayerInterlayer getPlayerByName(String name) {
    return PlayerInterlayer.getInstanceFor(Bukkit.getPlayer(name));
  }
  public static PlayerCollection getOnlinePlayers() {
    try {
      Method method = Bukkit.class.getDeclaredMethod("getOnlinePlayers", new Class[0]);
      Object result = method.invoke(Bukkit.getServer(), new Object[0]);
      List<Player> playersArray;
      if (result instanceof Player[]) {
        playersArray = Arrays.asList((Player[]) result);
      } else {
        playersArray = (List) result;
      }
      PlayerCollection pc = new PlayerCollection();
      for (Player player : playersArray) {
        pc.add(PlayerInterlayer.getInstanceFor(player));
      }
      return pc;
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | IllegalArgumentException ex) {
      Main.logger.log(Level.SEVERE, "反射获取玩家失败。");
      ex.printStackTrace();
      return null;
    }
  }

  // -------------------- PlayerIdentifierProvider --------------------

  public class PlayerIdentifierProvider implements IdentifierProvider {

    private final PlayerInterlayer player;

    public PlayerIdentifierProvider(PlayerInterlayer player) {
      this.player = player;
    }

    @Override
    public AnyType get(String key) {
      String namespace;
      String variable;
      if (key.contains(":")) {
        int splitterPos = key.indexOf(":");
        namespace = key.substring(0, splitterPos);
        variable = key.substring(splitterPos + 1);
      } else {
        namespace = "property";
        variable = key;
      }
      String fullname = namespace + ":" + variable;
      switch (namespace) {
        case "property": {
          variable = variable.toLowerCase();
          switch (variable) {
            case "level":
            case "lvl":
            case "lv":
              return new NumberType(this.player.getPlayer().getLevel());
            case "exp":
            case "xp":
              return new NumberType(this.player.getPlayer().getExp());
            case "flying":
            case "fly":
              return new BooleanType(this.player.getPlayer().isFlying());
            case "sleeping":
            case "sleep":
              return new BooleanType(this.player.getPlayer().isSleeping());
            case "op":
              return new BooleanType(this.player.getPlayer().isOp());
            case "canfly":
              return new BooleanType(this.player.getPlayer().getAllowFlight());
            case "gamemode":
              return new NumberType(this.player.getPlayer().getGameMode().getValue());
            case "name":
              return new StringType(this.player.getPlayer().getName());
            case "customname":
              return new StringType(this.player.getPlayer().getCustomName());
            case "displayname":
              return new StringType(this.player.getPlayer().getDisplayName());
            case "playerlistname":
              return new StringType(this.player.getPlayer().getPlayerListName());
            case "falldistance":
              return new NumberType(this.player.getPlayer().getFallDistance());
            case "food":
            case "foodlevel":
              return new NumberType(this.player.getPlayer().getFoodLevel());
            case "hp":
            case "health":
              return new NumberType(this.player.getPlayer().getHealth());
            case "walkspeed":
              return new NumberType(this.player.getPlayer().getWalkSpeed());
            case "flyspeed":
              return new NumberType(this.player.getPlayer().getFlySpeed());
            case "insidevehcile":
              return new BooleanType(this.player.getPlayer().isInsideVehicle());
            case "sneaking":
            case "sneak":
              return new BooleanType(this.player.getPlayer().isSneaking());
            case "sprinting":
            case "sprint":
            case "running":
            case "run":
              return new BooleanType(this.player.getPlayer().isSprinting());
            case "xpos":
            case "xposition":
              return new NumberType(this.player.getPlayer().getLocation().getX());
            case "ypos":
            case "yposition":
              return new NumberType(this.player.getPlayer().getLocation().getY());
            case "zpos":
            case "zposition":
              return new NumberType(this.player.getPlayer().getLocation().getZ());
            case "yaw":
              return new NumberType(this.player.getPlayer().getLocation().getYaw());
            case "pitch":
              return new NumberType(this.player.getPlayer().getLocation().getPitch());
            case "world":
            case "worldname":
              return new StringType(this.player.getPlayer().getLocation().getWorld().getName());
            case "xvel":
            case "xvelocity":
              return new NumberType(this.player.getPlayer().getVelocity().getX());
            case "yvel":
            case "yvelocity":
              return new NumberType(this.player.getPlayer().getVelocity().getY());
            case "zvel":
            case "zvelocity":
              return new NumberType(this.player.getPlayer().getVelocity().getZ());
            default:
              throw new MineQueryRuntimeException(MessageFormat.format("无法找到变量 {0}。", fullname));
          }
        } case "bukkit": {
          Player bPlayer = this.player.getPlayer();
          Method method;
          try {
            method = Player.class.getMethod(variable);
          } catch(NoSuchMethodException ex) {
            throw new MineQueryRuntimeException(MessageFormat.format("找不到方法 {0}。", variable), ex);
          }
          Object result;
          try {
            result = method.invoke(bPlayer);
          } catch(IllegalAccessException ex) {
            throw new MineQueryRuntimeException(MessageFormat.format("方法 {0} 不是 public 的。", variable), ex);
          } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            throw new MineQueryRuntimeException("MineQuery 内部错误。错误信息已经发送到服务器后台。汇报 Bug 时请务必带上。", ex);
          } catch (InvocationTargetException ex) {
            ex.printStackTrace();
            throw new MineQueryRuntimeException("Bukkit 内部错误。错误信息已经发送到服务器后台。汇报 Bug 时请务必带上。", ex);
          }
          if (result instanceof String) {
            return new StringType((String) result);
          } else if (result instanceof Character) {
            return new StringType(result.toString());
          } else if (result instanceof Float) {
            return new NumberType(Double.valueOf(result.toString()));
          } else if (result instanceof Double) {
            return new NumberType((Double) result);
          } else if (result instanceof Integer) {
            return new NumberType((Integer) result);
          } else if (result instanceof Long) {
            return new NumberType((Long) result);
          } else if (result instanceof Short) {
            return new NumberType((Short) result);
          } else if (result instanceof Byte) {
            return new NumberType((Byte) result);
          } else if (result instanceof Boolean) {
            return new BooleanType((Boolean) result);
          } else {
            throw new MineQueryRuntimeException("调用的方法返回值类型无效。");
          }
        } case "data": {
          AnyType result = this.player.getData().get(variable);
          if (result == null) {
            return new BooleanType(false);
          } else {
            return result;
          }
        } default:
          throw new MineQueryRuntimeException(MessageFormat.format("无法找到变量 {0}。", fullname));
      }
    }
  }

  // -------------------- 常规 --------------------

  private Player player;
  private Config dataFile;

  public void init(Player player, Config dataFile) {
    this.player = player;
    this.dataFile = dataFile;
  }

  @Config.ConfigableProperty(Key = "类")
  public ConfigableStringList classes = new ConfigableStringList();

  @Config.ConfigableProperty(Key = "数据")
  public ConfigableMap<AnyType> data = new ConfigableMap<>();

  @Config.ConfigableProperty(Key = "禁用命令")
  public boolean noCmd;

  public boolean isNoCmd() {
    return noCmd;
  }

  public void setNoCmd(boolean noCmd) {
    this.noCmd = noCmd;
  }

  public String getNoCmdReason() {
    return noCmdReason;
  }

  public void setNoCmdReason(String noCmdReason) {
    this.noCmdReason = noCmdReason;
  }

  @Config.ConfigableProperty(Key = "禁用命令原因")
  public String noCmdReason;

  public ConfigableMap<AnyType> getData() {
    return data;
  }

  public ConfigableStringList getClasses() {
    return classes;
  }

  public void save() {
    dataFile.saveAll();
  }

  public String getID() {
    return player.getName();
  }

  public boolean hasClass(String klass) {
    return classes.contains(klass);
  }

  public IdentifierProvider getIdentifierProvider() {
    return new PlayerIdentifierProvider(this);
  }

  public Player getPlayer() {
    return this.player;
  }

}
