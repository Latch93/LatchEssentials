package lmp.listeners.playerQuitEvents;

import lmp.LatchDiscord;
import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.awt.*;
import java.util.Objects;

public class BroadcastPlayerQuitMessageToDiscordEvent implements Listener {

    public BroadcastPlayerQuitMessageToDiscordEvent(Main plugin) { plugin.getServer().getPluginManager().registerEvents(this, plugin);}
    private final JDA jda = LatchDiscord.getJDA();

    @EventHandler
    private void sendPlayerLogoutMessage(PlayerQuitEvent onPlayerQuitEvent) {
        FileConfiguration enabledEventsCfg = Api.getFileConfiguration(YmlFileNames.YML_ENABLED_EVENTS_FILE_NAME);
        if (enabledEventsCfg.getBoolean("broadcastPlayerQuitMessageToDiscord")) {
            try {
                EmbedBuilder eb = new EmbedBuilder();
                FileConfiguration whitelistCfg = Api.getFileConfiguration(YmlFileNames.YML_WHITELIST_FILE_NAME);
                String discordUserName = Objects.requireNonNull(Objects.requireNonNull(LatchDiscord.getJDA().getGuildById(lmp.Constants.GUILD_ID)).getMemberById(Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(whitelistCfg.getString(lmp.Constants.YML_PLAYERS + onPlayerQuitEvent.getPlayer().getUniqueId() + ".discordId")))))).getUser().getName();
                if (!onPlayerQuitEvent.getPlayer().hasPermission("dt.leaveVanish")) {
                    eb.setThumbnail("https://minotar.net/avatar/" + onPlayerQuitEvent.getPlayer().getName() + ".png?size=50");
                    eb.setTitle(lmp.Constants.DISCORD_USERNAME_LABEL + discordUserName + lmp.Constants.MINECRAFT_USERNAME_LABEL + onPlayerQuitEvent.getPlayer().getName() + " \nDisconnected from the server", null);
                    eb.setColor(new Color(0xD0FF3F3F, true));
                    TextChannel minecraftChannel = LatchDiscord.getJDA().getTextChannelById(lmp.Constants.MINECRAFT_CHAT_CHANNEL_ID);
                    assert minecraftChannel != null;
                    minecraftChannel.sendMessageEmbeds(eb.build()).queue();
                } else {
                    TextChannel modChannel = LatchDiscord.getJDA().getTextChannelById(lmp.Constants.DISCORD_STAFF_CHAT_CHANNEL_ID);
                    assert modChannel != null;
                    modChannel.sendMessage(":x: » " + discordUserName + " left the server.").queue();
                }
            } catch (NullPointerException e) {
                TextChannel minecraftChannel = LatchDiscord.getJDA().getTextChannelById(lmp.Constants.MINECRAFT_CHAT_CHANNEL_ID);
                assert minecraftChannel != null;
                minecraftChannel.sendMessage(":x: » " + onPlayerQuitEvent.getPlayer().getName() + " left the server.").queue();
            }
        }
    }
}
