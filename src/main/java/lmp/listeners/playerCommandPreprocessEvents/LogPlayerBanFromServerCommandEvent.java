package lmp.listeners.playerCommandPreprocessEvents;

import lmp.LatchDiscord;
import lmp.Main;
import lmp.api.Api;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class LogPlayerBanFromServerCommandEvent implements Listener {
    private final JDA jda = LatchDiscord.getJDA();
    public LogPlayerBanFromServerCommandEvent(Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void logPlayerBanFromServer(PlayerCommandPreprocessEvent e) {
        String command = e.getMessage().split(" ")[0];
        if (command.equalsIgnoreCase("/ban") || command.equalsIgnoreCase("/tempban") ){
            try {
                TextChannel banLogChannel = jda.getTextChannelById(lmp.Constants.BAN_LOG_CHANNEL_ID);
                assert banLogChannel != null;
                StringBuilder banReason = new StringBuilder();
                String[] banMessage = e.getMessage().split(" ");
                String minecraftId = e.getPlayer().getUniqueId().toString();
                String playerBannedName = "";
                if (command.equalsIgnoreCase("/ban")) {
                    try {
                        playerBannedName = banMessage[1];
                        for (int i = 2; i <= banMessage.length - 1; i++) {
                            banReason.append(banMessage[i]).append(" ");
                        }
                        banLogChannel.sendMessage("<@" + Api.getDiscordIdFromMCid(minecraftId) + ">" + " banned " + banMessage[1] + " | Reason: " + banReason + " | Discord Username: <@" + Api.getDiscordIdFromMCid(Api.getMinecraftIdFromMinecraftName(playerBannedName)) + ">").queue();
                    } catch (NullPointerException | ArrayIndexOutOfBoundsException error) {
                        Main.log.warning("<@" + Api.getDiscordIdFromMCid(minecraftId) + ">" + " banned " + banMessage[1] + " | Discord Username: <@" + Api.getDiscordIdFromMCid(Api.getMinecraftIdFromMinecraftName(playerBannedName)) + "> | Error: " + error.getMessage());
                    }
                } else if (command.equalsIgnoreCase("/tempban")) {
                    try {
                        playerBannedName = banMessage[1];
                        for (int i = 3; i <= banMessage.length - 1; i++) {
                            banReason.append(banMessage[i]).append(" ");
                        }
                        if (banReason.isEmpty()){
                            banReason = new StringBuilder("None Given");
                        }
                        banLogChannel.sendMessage("<@" + Api.getDiscordIdFromMCid(minecraftId) + ">" + " temp banned " + banMessage[1] + " | Length: " + banMessage[2] + " | Reason: " + banReason + " | Discord Username: <@" + Api.getDiscordIdFromMCid(Api.getMinecraftIdFromMinecraftName(playerBannedName)) + ">").queue();
                    } catch (NullPointerException | ArrayIndexOutOfBoundsException error) {
                        Main.log.warning("<@" + Api.getDiscordIdFromMCid(minecraftId) + ">" + " temp banned " + banMessage[1] + " | Discord Username: <@" + Api.getDiscordIdFromMCid(Api.getMinecraftIdFromMinecraftName(playerBannedName)) + "> | Error: " + error.getMessage());
                    }
                }
            } catch (ArrayIndexOutOfBoundsException error){
                Main.log.warning("Error: " + e.getPlayer().getDisplayName() + " tried to ban someone.");
            }
        }
    }
}

//            if (messageFromDiscordConsole == null) {
//                if (banMessage[0].equalsIgnoreCase("/ban")) {
//                    try {
//                        for (int i = 2; i <= banMessage.length - 1; i++) {
//                            banReason.append(banMessage[i]).append(" ");
//                        }
//
//                        banLogChannel.sendMessage(Api.getDiscordNameFromMCid(minecraftId) + " banned " + playerBannedName + " | Reason: " + banReason + " | Discord Username: <@" + Api.getDiscordIdFromMCid(Api.getMinecraftIdFromMinecraftName(playerBannedName)) + ">").queue();
//                    } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
//                        banLogChannel.sendMessage(Api.getDiscordNameFromMCid(minecraftId) + " banned " + playerBannedName + " | Discord Username: <@" + Api.getDiscordIdFromMCid(Api.getMinecraftIdFromMinecraftName(playerBannedName)) + ">").queue();
//                    }
//                }
//                if (banMessage[0].equalsIgnoreCase("/tempban")) {
//                    try {
//                        for (int i = 2; i <= banMessage.length - 1; i++) {
//                            banReason.append(banMessage[i]).append(" ");
//                        }
//                        banLogChannel.sendMessage(Api.getDiscordNameFromMCid(minecraftId) + " temp banned " + playerBannedName + " | Reason: " + banReason + " | Discord Username: <@" + Api.getDiscordIdFromMCid(Api.getMinecraftIdFromMinecraftName(playerBannedName)) + ">").queue();
//                    } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
//                        banLogChannel.sendMessage(Api.getDiscordNameFromMCid(minecraftId) + " temp banned " + playerBannedName + " | Discord Username: <@" + Api.getDiscordIdFromMCid(Api.getMinecraftIdFromMinecraftName(playerBannedName)) + ">").queue();
//                    }
//                }
//            }
//        }
//}
