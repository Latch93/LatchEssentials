package lmp.listeners.playerInteractEvents;

import lmp.Constants;
import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.IOException;

public class RespawnAnchorSetEvent implements Listener {

    public RespawnAnchorSetEvent(Main plugin) { plugin.getServer().getPluginManager().registerEvents(this, plugin);}

    @EventHandler
    public void setRespawnAnchorPoint(PlayerInteractEvent e) throws IOException {
        if (e.getClickedBlock() != null && e.getClickedBlock().getType().equals(Material.RESPAWN_ANCHOR)) {
            FileConfiguration playerBedLocationCfg = Api.getFileConfiguration(YmlFileNames.YML_PLAYER_BED_LOCATION_FILE_NAME);
            playerBedLocationCfg.set(Constants.YML_PLAYERS + e.getPlayer().getUniqueId().toString() + "." + e.getPlayer().getWorld().getName() + ".location", e.getClickedBlock().getLocation());
            playerBedLocationCfg.save(Api.getConfigFile(YmlFileNames.YML_PLAYER_BED_LOCATION_FILE_NAME));
        }
    }
}
