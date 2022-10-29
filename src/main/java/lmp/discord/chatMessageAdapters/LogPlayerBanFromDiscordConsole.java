package lmp.discord.chatMessageAdapters;

import lmp.LatchDiscord;
import lmp.Main;
import lmp.api.Api;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class LogPlayerBanFromDiscordConsole extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        String channelId = e.getChannel().getId();
        String message = e.getMessage().getContentRaw();
        String[] banMessageArr = message.split(" ");
        if (lmp.Constants.DISCORD_CONSOLE_CHANNEL_ID.equalsIgnoreCase(channelId)) {
            TextChannel banLogChannel = LatchDiscord.getJDA().getTextChannelById(lmp.Constants.BAN_LOG_CHANNEL_ID);
            assert banLogChannel != null;
            String staffWhoBannedName = e.getAuthor().getName();
            String staffWhoBannedDiscordID = e.getAuthor().getId();
            StringBuilder banReason = new StringBuilder();
            String playerBannedName = "N/A";
            if (banMessageArr.length > 1){
                playerBannedName = banMessageArr[1];
            }
            if (banMessageArr[0].equalsIgnoreCase("ban")) {
                try {
                    for (int i = 2; i <= banMessageArr.length - 1; i++) {
                        banReason.append(banMessageArr[i]).append(" ");
                    }
                    banLogChannel.sendMessage("<@" + staffWhoBannedDiscordID + ">" + " banned " + banMessageArr[1] + " | Reason: " + banReason + " | Discord Username: <@" + Api.getDiscordIdFromMCid(Api.getMinecraftIdFromMinecraftName(playerBannedName)) + ">").queue();
                } catch (NullPointerException | ArrayIndexOutOfBoundsException error) {
                    Main.log.warning(staffWhoBannedName + " tried to ban someone. Error: " + error.getMessage());
                }
            } else if (banMessageArr[0].equalsIgnoreCase("tempban")) {
                try {
                    for (int i = 3; i <= banMessageArr.length - 1; i++) {
                        banReason.append(banMessageArr[i]).append(" ");
                    }
                    banLogChannel.sendMessage("<@" + staffWhoBannedDiscordID + ">" + " temp banned " + banMessageArr[1] + " | Reason: " + banReason + " | Discord Username: <@" + Api.getDiscordIdFromMCid(Api.getMinecraftIdFromMinecraftName(playerBannedName)) + ">").queue();
                } catch (NullPointerException | ArrayIndexOutOfBoundsException error) {
                    Main.log.warning(staffWhoBannedName + " tried to tempban someone. Error: " + error.getMessage());
                }
            }
        }
    }
}
