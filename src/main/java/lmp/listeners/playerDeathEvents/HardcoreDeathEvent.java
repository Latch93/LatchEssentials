package lmp.listeners.playerDeathEvents;

import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.io.IOException;

public class HardcoreDeathEvent implements Listener {

    public HardcoreDeathEvent(Main plugin) {plugin.getServer().getPluginManager().registerEvents(this, plugin);}

    @EventHandler
    private void setHardcorePlayerConfigOnDeath(PlayerDeathEvent e) throws IOException {
        if (e.getEntity().getWorld().getName().contains("hardcore")) {
            FileConfiguration hardcoreCfg = Api.getFileConfiguration(YmlFileNames.YML_HARDCORE_FILE_NAME);
            Player player = e.getEntity();
            String uuid = player.getUniqueId().toString();
            if (Boolean.TRUE.equals(hardcoreCfg.getBoolean(uuid + ".isAlive"))) {
                hardcoreCfg.set(uuid + ".isAlive", false);
                hardcoreCfg.save(Api.getConfigFile(YmlFileNames.YML_HARDCORE_FILE_NAME));
            }
        }
    }
}
