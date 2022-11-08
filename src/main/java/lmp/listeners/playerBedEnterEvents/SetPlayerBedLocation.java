package lmp.listeners.playerBedEnterEvents;

import lmp.Constants;
import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

import java.io.IOException;

public class SetPlayerBedLocation implements Listener {

    public SetPlayerBedLocation(Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void setPlayerBedLocation(PlayerBedEnterEvent e) throws IOException {
        FileConfiguration playerBedLocationCfg = Api.getFileConfiguration(YmlFileNames.YML_PLAYER_BED_LOCATION_FILE_NAME);
        playerBedLocationCfg.set(Constants.YML_PLAYERS + e.getPlayer().getUniqueId().toString() + "." + e.getPlayer().getWorld().getName() + ".location", e.getBed().getLocation());
        playerBedLocationCfg.save(Api.getConfigFile(YmlFileNames.YML_PLAYER_BED_LOCATION_FILE_NAME));
    }

}
