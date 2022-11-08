package lmp.listeners.playerRespawnEvents;

import lmp.Constants;
import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Objects;

public class SetPlayerLocationOnAnarchy implements Listener {

    public SetPlayerLocationOnAnarchy(Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void setPlayerLocationOnRespawnOnAnarchy(PlayerRespawnEvent e){
        FileConfiguration whitelistCfg = Api.getFileConfiguration(YmlFileNames.YML_WHITELIST_FILE_NAME);
        FileConfiguration playerBedLocationCfg = Api.getFileConfiguration(YmlFileNames.YML_PLAYER_BED_LOCATION_FILE_NAME);
        if (Objects.requireNonNull(whitelistCfg.getString(Constants.YML_PLAYERS + e.getPlayer().getUniqueId().toString() + ".lastDeathWorld")).contains("anarchy")){
            Location playerAnarchyBedLocation = playerBedLocationCfg.getLocation(Constants.YML_PLAYERS + e.getPlayer().getUniqueId().toString() + ".anarchy.location");
            if (playerAnarchyBedLocation == null || !playerAnarchyBedLocation.getBlock().getType().toString().contains("BED")){
                Location anarchySpawnLocation = new Location(Bukkit.getWorld("anarchy"), 13.5, 63, -25.5, (float) -3.000, (float) 92.10);
                e.setRespawnLocation(anarchySpawnLocation);
            } else {
                e.setRespawnLocation(playerAnarchyBedLocation);
            }
        }
    }

}
