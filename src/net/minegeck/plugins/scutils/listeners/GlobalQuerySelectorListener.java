package net.minegeck.plugins.scutils.listeners;

import net.minegeck.plugins.scutils.CommandConverter;
import net.minegeck.plugins.scutils.CommandConverter.ConvertResult;
import net.minegeck.plugins.utils.Annotations;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import static org.bukkit.event.EventPriority.HIGHEST;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class GlobalQuerySelectorListener implements Listener {
  @EventHandler(priority = HIGHEST, ignoreCancelled = true)
  public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
    if (!event.getPlayer().hasPermission("scutils.globalqueryselector")) {
      return;
    }
    ConvertResult result = CommandConverter.convert(event.getMessage().substring(1));
    result.run(event.getPlayer());
    event.setCancelled(result.isCancelEvent());
  }
}