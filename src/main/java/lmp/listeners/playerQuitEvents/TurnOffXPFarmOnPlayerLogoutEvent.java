package lmp.listeners.playerQuitEvents;

import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;

public class TurnOffXPFarmOnPlayerLogoutEvent implements Listener {
    public TurnOffXPFarmOnPlayerLogoutEvent(Main plugin) { plugin.getServer().getPluginManager().registerEvents(this, plugin);}

    @EventHandler
    private void turnOffXPFarmOnPlayerLogoff(PlayerQuitEvent e) throws IOException {
        FileConfiguration xpFarmCfg = Api.getFileConfiguration(YmlFileNames.YML_XP_FARM_FILE_NAME);
        if (Boolean.TRUE.equals(xpFarmCfg.get("isFarmInUse"))) {
            if (e.getPlayer().getUniqueId().toString().equalsIgnoreCase(xpFarmCfg.getString("playerIDUsingFarm"))) {
                xpFarmCfg.set("isFarmInUse", false);
                xpFarmCfg.save(Api.getConfigFile(YmlFileNames.YML_XP_FARM_FILE_NAME));
            }
        }
    }
}
