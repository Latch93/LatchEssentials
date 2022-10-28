package lmp.listeners.playerJoinEvents;

import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class SendJoinMessageEvent implements Listener {

    public SendJoinMessageEvent(Main plugin) { plugin.getServer().getPluginManager().registerEvents(this, plugin);}

    @EventHandler
    private void showJoinMessage(PlayerJoinEvent e) throws ExecutionException, InterruptedException {
        FileConfiguration configCfg = Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME);
        Player player = e.getPlayer();
        if (!Api.doesPlayerHavePermission(player.getUniqueId().toString(), "default") && Boolean.TRUE.equals(configCfg.getBoolean("showJoinMessage"))) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(configCfg.getString("joinMessage"))));
        }
    }

}
