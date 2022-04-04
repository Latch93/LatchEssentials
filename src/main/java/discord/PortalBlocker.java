package discord;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerPortalEvent;

public class PortalBlocker {
    public static void portalBlocker(PlayerPortalEvent event){
        Player player = event.getPlayer();
        Location playerLocation = event.getPlayer().getLocation();
        if (player.getWorld().equals(Bukkit.getWorld("world"))){
            if (playerLocation.getBlockX() >= 9973 && playerLocation.getBlockX() <= 9976) {
                if (playerLocation.getBlockY() >= 64 && playerLocation.getBlockY() <= 68) {
                    if (playerLocation.getBlockZ() >=  9984 && playerLocation.getBlockZ() <= 9987) {
                        int playerLevel = player.getLevel();
                        if (playerLevel < 15){
                            event.setCancelled(true);
                            player.sendMessage(ChatColor.RED + "You need to have at least 15 experience levels to use this portal");
                        }
                    }
                }
            }
        }
    }
}
