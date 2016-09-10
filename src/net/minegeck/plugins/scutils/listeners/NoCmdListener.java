package net.minegeck.plugins.scutils.listeners;

import net.minegeck.plugins.scutils.PlayerInterlayer;
import net.minegeck.plugins.utils.Annotations;
import net.minegeck.plugins.utils.BoxedMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import static org.bukkit.event.EventPriority.HIGHEST;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class NoCmdListener implements Listener {
  @EventHandler(priority = HIGHEST, ignoreCancelled = true)
  public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
    PlayerInterlayer player = PlayerInterlayer.getInstanceFor(event.getPlayer());
    if (player.isNoCmd()) {
      if (player.getPlayer().hasPermission("scutils.overridenocmd")) {
        BoxedMessage.sendTo(player.getPlayer(), 'b', "§b§l禁用命令被 Override\n\n§b您已被禁用命令, 由于 " + player.getNoCmdReason() + "但是因为您拥有 §e§lscutils.overridenocmd §b这个权限节点, 所以您的命令依然生效了。");
      } else {
        event.setCancelled(true);
        BoxedMessage.sendTo(player.getPlayer(), 'c', "§c§l命令被禁用\n\n§c" + player.getNoCmdReason());
      }
    }
  }

}
