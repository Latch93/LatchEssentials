package lmp.listeners.playerDeathEvents;

import lmp.LatchDiscord;
import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.awt.*;

public class BroadcastDeathMessageToDiscordEvent implements Listener {

    public BroadcastDeathMessageToDiscordEvent(Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void broadcastDeathMessageToDiscord(PlayerDeathEvent e){
        FileConfiguration enabledEventsCfg = Api.getFileConfiguration(YmlFileNames.YML_ENABLED_EVENTS_FILE_NAME);
        if (enabledEventsCfg.getBoolean("broadcastDeathMessageToDiscord") && Boolean.FALSE.equals(Api.isPlayerInvisible(e.getEntity().getUniqueId().toString()))){
            EmbedBuilder eb = new EmbedBuilder();
            String worldPrefix = "[Community] - ";
            if (e.getEntity().getWorld().getName().contains("hardcore")) {
                worldPrefix = "[Hardcore] - ";
            } else if (e.getEntity().getWorld().getName().contains("anarchy")) {
                worldPrefix = "[Anarchy] - ";
            } else if (e.getEntity().getWorld().getName().contains("classic")) {
                worldPrefix = "[Classic] - ";
            } else if (e.getEntity().getWorld().getName().contains("creative")) {
                worldPrefix = "[Creative] - ";
            } else if (e.getEntity().getWorld().getName().contains("OneBlock")) {
                worldPrefix = "[OneBlock] - ";
            } else if (e.getEntity().getWorld().getName().contains("Skyblock")) {
                worldPrefix = "[SkyBlock] - ";
            }
            eb.setTitle(worldPrefix + e.getDeathMessage());
            eb.setColor(new Color(0xE1922E00, true));
            eb.setThumbnail("https://minotar.net/avatar/" + e.getEntity().getName() + ".png?size=5");
            TextChannel minecraftChatChannel = LatchDiscord.getJDA().getTextChannelById(lmp.Constants.MINECRAFT_CHAT_CHANNEL_ID);
            assert minecraftChatChannel != null;
            minecraftChatChannel.sendMessageEmbeds(eb.build()).queue();
        }
    }
}
