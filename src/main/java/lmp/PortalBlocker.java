package lmp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerPortalEvent;

import java.util.Objects;

public class PortalBlocker {
    public static void portalBlocker(PlayerPortalEvent event){
        Player player = event.getPlayer();
        Location playerLocation = event.getPlayer().getLocation();
        FileConfiguration configCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_CONFIG_FILE_NAME));
        if (player.getWorld().equals(Bukkit.getWorld("world"))){
            if (playerLocation.getBlockX() >= configCfg.getDouble("lesserX") && playerLocation.getBlockX() <= configCfg.getDouble("greaterX")) {
                if (playerLocation.getBlockY() >= configCfg.getDouble("lesserY") && playerLocation.getBlockY() <= configCfg.getDouble("greaterY")) {
                    if (playerLocation.getBlockZ() >=  configCfg.getDouble("lesserZ") && playerLocation.getBlockZ() <= configCfg.getDouble("greaterZ")) {
                        int playerLevel = player.getLevel();
                        if (playerLevel < configCfg.getInt("playerLevel")){
                            event.setCancelled(true);
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(configCfg.getString("levelMessage"))));
                        }
                    }
                }
            }
        }
    }
}
