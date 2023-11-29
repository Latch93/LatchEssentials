package lmp.listeners.playerItemMendEvent;

import lmp.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemMendEvent;

import java.io.IOException;

public class CancelMendingEXPGainEvent implements Listener {

    public CancelMendingEXPGainEvent(Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void cancelMendingOnEXPGain(PlayerItemMendEvent e) throws IOException {
        e.setCancelled(true);
    }
}
