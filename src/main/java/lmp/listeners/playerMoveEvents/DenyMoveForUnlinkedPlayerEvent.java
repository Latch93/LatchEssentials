package lmp.listeners.playerMoveEvents;

import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.concurrent.ExecutionException;

public class DenyMoveForUnlinkedPlayerEvent implements Listener {

    public DenyMoveForUnlinkedPlayerEvent(Main plugin) {plugin.getServer().getPluginManager().registerEvents(this, plugin);}

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent e) throws ExecutionException, InterruptedException {
        FileConfiguration enabledEventsCfg = Api.getFileConfiguration(YmlFileNames.YML_ENABLED_EVENTS_FILE_NAME);

        if (enabledEventsCfg.getBoolean("disableMovementAndSendMessageToUnlinkedPlayer")) {
            if (Api.doesPlayerHavePermission(e.getPlayer().getUniqueId().toString(), "default")) {
                e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Go to Discord and type " + ChatColor.AQUA + "!link " + ChatColor.RED + " in the General channel"));
                e.setCancelled(true);
            }
        }
    }}
