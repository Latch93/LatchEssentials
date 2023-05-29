package lmp.listeners.playerDeathEvents;

import lmp.Main;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.text.DecimalFormat;

public class SendPlayerDeathCoordinates implements Listener {

    public SendPlayerDeathCoordinates(Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void sendPlayerDeathCoordinatesEvent(PlayerDeathEvent e) {
        Player player = e.getEntity();
        Location deathLocation = player.getLocation();
        DecimalFormat df = new DecimalFormat("0.00");
        player.sendMessage(ChatColor.GREEN + "You died at these coordinates -> " + ChatColor.GOLD + df.format(deathLocation.getX()) + "/" + df.format(deathLocation.getY()) + "/" + df.format(deathLocation.getZ()));
    }
}