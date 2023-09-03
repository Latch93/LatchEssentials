package lmp.listeners.playerMoveEvents;

import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class FreezeLatchOnTwitchRewardClaim implements Listener {

    public FreezeLatchOnTwitchRewardClaim(Main plugin) {plugin.getServer().getPluginManager().registerEvents(this, plugin);}

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent e) {
        FileConfiguration configCfg = Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME);
        if (configCfg.getBoolean("freezeLatch")) {
            Player player = e.getPlayer();
            if (player.getName().equalsIgnoreCase("Latch93"))
                e.setCancelled(true);
        }
    }
}
