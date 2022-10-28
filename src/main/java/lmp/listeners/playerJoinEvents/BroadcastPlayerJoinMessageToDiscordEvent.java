package lmp.listeners.playerJoinEvents;

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
import org.bukkit.event.player.PlayerJoinEvent;

import java.awt.*;
import java.util.Objects;

public class BroadcastPlayerJoinMessageToDiscordEvent implements Listener {
    public BroadcastPlayerJoinMessageToDiscordEvent(Main plugin) { plugin.getServer().getPluginManager().registerEvents(this, plugin);}
    private final JDA jda = LatchDiscord.getJDA();

    @EventHandler
    private void sendPlayerJoinMessageToDiscord(PlayerJoinEvent onPlayerJoinEvent) {
        FileConfiguration enabledEventsCfg = Api.getFileConfiguration(YmlFileNames.YML_ENABLED_EVENTS_FILE_NAME);
        if (enabledEventsCfg.getBoolean("broadcastPlayerJoinMessageToDiscord")){
            try {
                FileConfiguration whitelistCfg = Api.getFileConfiguration(YmlFileNames.YML_WHITELIST_FILE_NAME);
                String discordUserName = Objects.requireNonNull(Objects.requireNonNull(jda.getGuildById(lmp.Constants.GUILD_ID)).getMemberById(Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(whitelistCfg.getString(lmp.Constants.YML_PLAYERS + onPlayerJoinEvent.getPlayer().getUniqueId() + ".discordId")))))).getUser().getName();
                EmbedBuilder eb = new EmbedBuilder();
                eb.setThumbnail("https://minotar.net/avatar/" + onPlayerJoinEvent.getPlayer().getName() + ".png?size=50");
                if (Boolean.TRUE.equals(onPlayerJoinEvent.getPlayer().hasPlayedBefore())) {
                    eb.setTitle(lmp.Constants.DISCORD_USERNAME_LABEL + discordUserName + lmp.Constants.MINECRAFT_USERNAME_LABEL + onPlayerJoinEvent.getPlayer().getName() + " \nJoined the server", null);
                } else {
                    eb.setTitle(lmp.Constants.DISCORD_USERNAME_LABEL + discordUserName + lmp.Constants.MINECRAFT_USERNAME_LABEL + onPlayerJoinEvent.getPlayer().getName() + " \nJoined the server for the first time", null);
                }
                eb.setColor(new Color(0xE134E502, true));
                TextChannel minecraftChannel = jda.getTextChannelById(lmp.Constants.MINECRAFT_CHAT_CHANNEL_ID);
                assert minecraftChannel != null;
                if (!onPlayerJoinEvent.getPlayer().hasPermission("dt.joinVanish")) {
                    minecraftChannel.sendMessageEmbeds(eb.build()).queue();
                } else {
                    TextChannel modChannel = jda.getTextChannelById(lmp.Constants.DISCORD_STAFF_CHAT_CHANNEL_ID);
                    assert modChannel != null;
                    modChannel.sendMessage(":white_check_mark: » " + discordUserName + " joined the server.").queue();
                }
            } catch (NullPointerException e) {
                TextChannel minecraftChannel = jda.getTextChannelById(lmp.Constants.MINECRAFT_CHAT_CHANNEL_ID);
                assert minecraftChannel != null;
                try {
                    minecraftChannel.sendMessage(":white_check_mark: » " + onPlayerJoinEvent.getPlayer().getName() + " joined the server.").queue();
                } catch (NullPointerException ignored) {

                }
            }
        }

    }
}
