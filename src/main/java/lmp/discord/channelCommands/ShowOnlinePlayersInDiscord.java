package lmp.discord.channelCommands;

import lmp.Constants;
import lmp.api.Api;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;

import static lmp.LatchDiscord.messageChannelID;
import static lmp.LatchDiscord.messageContents;

public class ShowOnlinePlayersInDiscord extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent e){
        // Gets online players and sends it to Discord Chat
        messageContents = e.getMessage().getContentRaw();
        messageChannelID = e.getChannel().getId();
        ArrayList<String> onlinePlayers = new ArrayList<>();
        if (messageContents != null && messageContents.equalsIgnoreCase(lmp.Constants.ONLINE_COMMAND) && (messageChannelID.equals(Constants.DISCORD_STAFF_CHAT_CHANNEL_ID) || messageChannelID.equals(Constants.MINECRAFT_CHAT_CHANNEL_ID))) {
            EmbedBuilder eb = new EmbedBuilder();
            StringBuilder onlinePlayerMessage = new StringBuilder();
            int count = 1;
            for (Player player : Bukkit.getOnlinePlayers()) {
                File playerDataFile = new File("plugins/Essentials/userdata", player.getUniqueId() + ".yml");
                FileConfiguration playerDataCfg = YamlConfiguration.loadConfiguration(playerDataFile);
                String afk = "";
                if (Boolean.TRUE.equals(playerDataCfg.getBoolean("afk"))) {
                    afk = "(AFK)";
                }
                if (messageChannelID.equals(Constants.MINECRAFT_CHAT_CHANNEL_ID)) {
                    if (!Api.isPlayerInvisible(player.getUniqueId().toString())) {
                        onlinePlayerMessage.append(Api.convertMinecraftMessageToDiscord(null, count + ".) " + player.getDisplayName())).append(" ").append(afk).append("\n");
                        onlinePlayers.add(player.getDisplayName());
                        count++;
                    }
                } else if (messageChannelID.equals(Constants.DISCORD_STAFF_CHAT_CHANNEL_ID)) {
                    if (Boolean.TRUE.equals(Api.isPlayerInvisible(player.getUniqueId().toString()))) {
                        onlinePlayerMessage.append(Api.convertMinecraftMessageToDiscord(null, count + ".) " + player.getDisplayName())).append(" (Invisible) ").append(afk).append("\n");
                    } else {
                        onlinePlayerMessage.append(Api.convertMinecraftMessageToDiscord(null, count + ".) " + player.getDisplayName())).append(" ").append(afk).append("\n");
                    }
                    onlinePlayers.add(player.getDisplayName());
                    count++;
                }

            }
            eb.setColor(new Color(0xC6D13EFF, true));
            eb.setTitle("Online Players: " + onlinePlayers.size() + "/35");
            eb.setDescription(onlinePlayerMessage.toString());
            if (messageChannelID.equals(Constants.DISCORD_STAFF_CHAT_CHANNEL_ID) || messageChannelID.equals(Constants.MINECRAFT_CHAT_CHANNEL_ID)){
                e.getChannel().sendMessageEmbeds(eb.build()).queue();
            }
        }
    }
}
