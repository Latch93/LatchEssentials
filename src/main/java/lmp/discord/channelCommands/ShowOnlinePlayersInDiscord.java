package lmp.discord.channelCommands;

import lmp.Constants;
import lmp.api.Api;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang.StringUtils;
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
            e.getChannel().sendMessageEmbeds(eb.build()).queue();

        }
        if (messageContents != null && messageContents.equalsIgnoreCase(Constants.WORLD_LIST_COMMAND) &&  messageChannelID.equals(Constants.MINECRAFT_CHAT_CHANNEL_ID)) {
            EmbedBuilder eb = new EmbedBuilder();
            StringBuilder communityWorldList = new StringBuilder();
            communityWorldList.append("[Community] - ");
            StringBuilder classicWorldList = new StringBuilder();
            classicWorldList.append("[Classic] - ");
            StringBuilder anarchyWorldList = new StringBuilder();
            anarchyWorldList.append("[Anarchy] - ");
            StringBuilder hardcoreWorldList = new StringBuilder();
            hardcoreWorldList.append("[Hardcore] - ");
            StringBuilder creativeWorldList = new StringBuilder();
            creativeWorldList.append("[Creative] - ");
            StringBuilder oneBlockWorldList = new StringBuilder();
            oneBlockWorldList.append("[OneBlock] - ");
            StringBuilder skyBlockWorldList = new StringBuilder();
            skyBlockWorldList.append("[SkyBlock] - ");
            int communityCount = 0;
            int hardcoreCount = 0;
            int anarchyCount = 0;
            int creativeCount = 0;
            int classicCount = 0;
            int oneBlockCount = 0;
            int skyBlockCount = 0;
            for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
                if (!Api.isPlayerInvisible(onlinePlayer.getUniqueId().toString())) {
                    if (onlinePlayer.getWorld().getName().contains("anarchy")) {
                        anarchyWorldList.append(onlinePlayer.getName()).append(" | ");
                        anarchyCount++;
                    } else if (onlinePlayer.getWorld().getName().contains("hardcore")) {
                        hardcoreWorldList.append(onlinePlayer.getName()).append(" | ");
                        hardcoreCount++;
                    } else if (onlinePlayer.getWorld().getName().contains("creative")) {
                        creativeWorldList.append(onlinePlayer.getName()).append(" | ");
                        creativeCount++;
                    } else if (onlinePlayer.getWorld().getName().contains("classic")) {
                        classicWorldList.append(onlinePlayer.getName()).append(" | ");
                        classicCount++;
                    } else if (onlinePlayer.getWorld().getName().contains("OneBlock")) {
                        oneBlockWorldList.append(onlinePlayer.getName()).append(" | ");
                        oneBlockCount++;
                    } else if (onlinePlayer.getWorld().getName().contains("Skyblock")) {
                        skyBlockWorldList.append(onlinePlayer.getName()).append(" | ");
                        skyBlockCount++;
                    } else {
                        communityWorldList.append(onlinePlayer.getName()).append(" | ");
                        communityCount++;
                    }
                }
            }
            if (communityCount == 0) {
                communityWorldList.append("None");
            }
            if (anarchyCount == 0) {
                anarchyWorldList.append("None");
            }
            if (hardcoreCount == 0) {
                hardcoreWorldList.append("None");
            }
            if (creativeCount == 0) {
                creativeWorldList.append("None");
            }
            if (classicCount == 0) {
                classicWorldList.append("None");
            }
            if (oneBlockCount == 0) {
                oneBlockWorldList.append("None");
            }
            if (skyBlockCount == 0) {
                skyBlockWorldList.append("None");
            }
            String finalAnarchyMessage = String.valueOf(anarchyWorldList);
            String finalClassicMessage = String.valueOf(classicWorldList);
            String finalCommunityMessage = String.valueOf(communityWorldList);
            String finalCreativeMessage = String.valueOf(creativeWorldList);
            String finalHardcoreMessage = String.valueOf(hardcoreWorldList);
            String finalOneBlockMessage = String.valueOf(oneBlockWorldList);
            String finalSkyBlockMessage = String.valueOf(skyBlockWorldList);
            if (finalCommunityMessage.contains("|")) {
                finalCommunityMessage = StringUtils.substring(finalCommunityMessage, 0, finalCommunityMessage.length() - 3);
            }
            if (finalClassicMessage.contains("|")) {
                finalClassicMessage = StringUtils.substring(finalClassicMessage, 0, finalClassicMessage.length() - 3);
            }
            if (finalAnarchyMessage.contains("|")) {
                finalAnarchyMessage = StringUtils.substring(finalAnarchyMessage, 0, finalAnarchyMessage.length() - 3);
            }
            if (finalHardcoreMessage.contains("|")) {
                finalHardcoreMessage = StringUtils.substring(finalHardcoreMessage, 0, finalHardcoreMessage.length() - 3);
            }
            if (finalOneBlockMessage.contains("|")) {
                finalOneBlockMessage = StringUtils.substring(finalOneBlockMessage, 0, finalOneBlockMessage.length() - 3);
            }
            if (finalSkyBlockMessage.contains("|")) {
                finalSkyBlockMessage = StringUtils.substring(finalSkyBlockMessage, 0, finalSkyBlockMessage.length() - 3);
            }
            if (finalCreativeMessage.contains("|")) {
                finalCreativeMessage = StringUtils.substring(finalCreativeMessage, 0, finalCreativeMessage.length() - 3);
            }

            eb.setColor(new Color(0xC6D13EFF, true));
            eb.setTitle("Player World List:");
            eb.setDescription(finalAnarchyMessage + "\n" + finalClassicMessage + "\n" + finalCommunityMessage + "\n" + finalCreativeMessage + "\n" + finalHardcoreMessage + "\n" + finalOneBlockMessage + "\n" + finalSkyBlockMessage);
            e.getChannel().sendMessageEmbeds(eb.build()).queue();
        }
    }
}
