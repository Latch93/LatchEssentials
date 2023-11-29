package lmp.listeners.playerCommandPreprocessEvents;

import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.io.IOException;

public class CancelCommandsOfRacer implements Listener {

    public CancelCommandsOfRacer(Main plugin) { plugin.getServer().getPluginManager().registerEvents(this, plugin);}

    @EventHandler
    public void cancelCommandsOfRacer(PlayerCommandPreprocessEvent e) throws IOException {
        FileConfiguration boatRaceCfg = Api.getFileConfiguration(YmlFileNames.YML_BOAT_RACE_FILE_NAME);
        Boolean isRaceActive = boatRaceCfg.getBoolean("isRaceActive");
        if (Boolean.TRUE.equals(isRaceActive)) {
            String playerUUID = e.getPlayer().getUniqueId().toString();
            String racerID = boatRaceCfg.getString("racer.uuid");
            assert racerID != null;
            if (racerID.equalsIgnoreCase(playerUUID)) {
                e.getPlayer().sendMessage(ChatColor.RED + "You can't use commands while you race");
                e.setCancelled(true);
            }
        }
    }
}
