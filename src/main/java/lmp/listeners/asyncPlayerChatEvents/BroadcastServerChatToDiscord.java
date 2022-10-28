package lmp.listeners.asyncPlayerChatEvents;

import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Objects;

import static lmp.LatchDiscord.jda;
import static org.bukkit.Bukkit.getOnlinePlayers;

public class BroadcastServerChatToDiscord implements Listener {
    public BroadcastServerChatToDiscord(Main plugin) { plugin.getServer().getPluginManager().registerEvents(this, plugin);}

    @EventHandler(priority = EventPriority.LOW)
    private void broadcastServerChatToDiscord(AsyncPlayerChatEvent e){
        FileConfiguration enabledEventsCfg = Api.getFileConfiguration(YmlFileNames.YML_ENABLED_EVENTS_FILE_NAME);
        if (enabledEventsCfg.getBoolean("broadcastServerMessagesToDiscord")){
            String worldName = e.getPlayer().getWorld().getName();
            if (Boolean.FALSE.equals(Api.isPlayerInvisible(e.getPlayer().getUniqueId().toString()))) {
                TextChannel minecraftChatChannel = jda.getTextChannelById(lmp.Constants.MINECRAFT_CHAT_CHANNEL_ID);
                assert minecraftChatChannel != null;
                minecraftChatChannel.sendMessage(Api.getPlayerChatWorldPrefix(worldName) + Api.convertMinecraftMessageToDiscord(e.getPlayer().getDisplayName(), e.getMessage())).queue();
            } else {
                Objects.requireNonNull(jda.getTextChannelById(lmp.Constants.DISCORD_STAFF_CHAT_CHANNEL_ID)).sendMessage(Api.getPlayerChatWorldPrefix(worldName) + Api.convertMinecraftMessageToDiscord(e.getPlayer().getDisplayName(), e.getMessage())).queue();
                for (Player player : getOnlinePlayers()) {
                    if (player.hasPermission("group.jr-mod")) {
                        player.sendMessage("[" + ChatColor.LIGHT_PURPLE + "Mod Chat" + ChatColor.WHITE + "] - " + ChatColor.GOLD + e.getPlayer().getDisplayName() + ChatColor.WHITE + " » " + ChatColor.AQUA + e.getMessage());
                    }
                }
                e.setCancelled(true);
            }

        }
    }
}
