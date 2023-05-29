package lmp.listeners.playerInteractEvents;

import lmp.Main;
import lmp.api.Api;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.IOException;

public class TeleportPlayerToWorldFromHubButtons implements Listener {

    public TeleportPlayerToWorldFromHubButtons(Main plugin) { plugin.getServer().getPluginManager().registerEvents(this, plugin);}

    @EventHandler
    public void teleportPlayer(PlayerInteractEvent e) throws IOException {
        Api.teleportPlayerToWorldFromHub(e);
    }
}
