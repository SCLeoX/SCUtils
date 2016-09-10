package net.minegeck.plugins.scutils.listeners;

import net.minegeck.plugins.scutils.PlayerInterlayer;
import net.minegeck.plugins.utils.Annotations;
import net.minegeck.plugins.utils.BoxedMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static org.bukkit.event.EventPriority.HIGHEST;
import static org.bukkit.event.EventPriority.MONITOR;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class PlayerCacheRemoverListener implements Listener {
  @EventHandler(priority = MONITOR, ignoreCancelled = true)
  public void onPlayerQuit(PlayerQuitEvent event) {
    PlayerInterlayer.removeInstanceFor(event.getPlayer());
  }

}
