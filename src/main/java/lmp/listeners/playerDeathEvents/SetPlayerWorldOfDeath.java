package lmp.listeners.playerDeathEvents;

import lmp.Constants;
import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.io.IOException;

public class SetPlayerWorldOfDeath implements Listener {

    public SetPlayerWorldOfDeath(Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void setPlayerWorldOfDeath(PlayerDeathEvent e) throws IOException {
        FileConfiguration whitelistCfg = Api.getFileConfiguration(YmlFileNames.YML_WHITELIST_FILE_NAME);
        whitelistCfg.set(Constants.YML_PLAYERS + e.getEntity().getUniqueId().toString() + ".lastDeathWorld", e.getEntity().getWorld().getName());
        whitelistCfg.save(Api.getConfigFile(YmlFileNames.YML_WHITELIST_FILE_NAME));
    }
}
