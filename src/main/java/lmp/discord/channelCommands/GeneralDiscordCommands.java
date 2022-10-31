package lmp.discord.channelCommands;

import lmp.Constants;
import lmp.Main;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.util.Objects;

import static lmp.LatchDiscord.*;

public class GeneralDiscordCommands extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        if (senderDiscordMember != null && !senderDiscordUserID.equalsIgnoreCase(Constants.LATCH93BOT_USER_ID)) {
            if (messageContents.equalsIgnoreCase("!discordID")) {
                messageChannel.sendMessage("Hi " + senderDiscordUsername + "! Your Discord UserID is " + senderDiscordUserID).queue();
            }
            if (messageContents.equalsIgnoreCase("!joinTime")) {
                messageChannel.sendMessage(senderDiscordUsername + " joined on " + senderDiscordMember.getTimeJoined().toString().split("T")[0]).queue();
            }
            if (messageContents.equalsIgnoreCase("!link")) {
                senderDiscordUser.openPrivateChannel().flatMap(dm -> dm.sendMessage("IP = latch.ddns.net\n" +
                        "Java Port Number = 60\n" +
                        "Bedrock Port Number = 19132\n" +
                        "Run the following command in your minecraft client chat after you join:\n" +
                        "/lmp link " + e.getAuthor().getId())).queue(null, new ErrorHandler()
                        .handle(ErrorResponse.CANNOT_SEND_TO_USER,
                                (ex) -> Main.log.warning("Cannot send link message to " + senderDiscordUsername)));
                messageChannel.sendMessage(senderDiscordUsername + " --- Check your Discord for a private message from my bot containing your link command. <:LatchPOG:957363669388386404>").queue();
            } else if (!senderDiscordMember.getRoles().toString().contains("Member")) {
                messageChannel.sendMessage("Type !link in this channel to get your link command and the Server IP dm'd to you by my bot.").queue();
                Objects.requireNonNull(jda.getGuildById(lmp.Constants.GUILD_ID)).addRoleToMember(UserSnowflake.fromId(senderDiscordUserID), Objects.requireNonNull(jda.getRoleById(lmp.Constants.MEMBER_ROLE_ID))).queue();
            }
        }
    }
}
