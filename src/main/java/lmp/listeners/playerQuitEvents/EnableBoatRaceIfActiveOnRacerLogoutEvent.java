package lmp.listeners.playerQuitEvents;

import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;

public class EnableBoatRaceIfActiveOnRacerLogoutEvent implements Listener {

    public EnableBoatRaceIfActiveOnRacerLogoutEvent(Main plugin) { plugin.getServer().getPluginManager().registerEvents(this, plugin);}

    @EventHandler
    private void enableBoatRaceIfActiveOnRacerLogoutEvent(PlayerQuitEvent e) throws IOException {
        FileConfiguration boatRaceCfg = Api.getFileConfiguration(YmlFileNames.YML_BOAT_RACE_FILE_NAME);
        Boolean isRaceActive = boatRaceCfg.getBoolean("isRaceActive");
        if (Boolean.TRUE.equals(isRaceActive)) {
            String playerUUID = e.getPlayer().getUniqueId().toString();
            String racerID = boatRaceCfg.getString("racer.uuid");
            assert racerID != null;
            if (racerID.equalsIgnoreCase(playerUUID)) {
                boatRaceCfg.set("isRaceActive", false);
                boatRaceCfg.save(Api.getConfigFile(YmlFileNames.YML_BOAT_RACE_FILE_NAME));
            }
        }
    }
}
