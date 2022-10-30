package lmp.discord.channelCommands;

import lmp.Constants;
import lmp.LatchDiscord;
import lmp.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.util.Objects;

public class GeneralDiscordCommands extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        Message message = e.getMessage();
        String messageContent = message.getContentRaw();
        User discordUser = e.getAuthor();
        String discordUserName = discordUser.getName();
        String discordUserID = discordUser.getId();
        MessageChannel channel = e.getChannel();
        JDA jda = LatchDiscord.getJDA();
        Member discordMember = e.getMember();
        if (discordMember != null && !discordUserID.equalsIgnoreCase(Constants.LATCH93BOT_USER_ID)) {
            if (messageContent.equalsIgnoreCase("!discordID")) {
                channel.sendMessage("Hi " + discordUserName + "! Your Discord UserID is " + discordUserID).queue();
            }
            if (!discordMember.getRoles().toString().contains("Member")) {
                channel.sendMessage("Type !link in this channel to get your link command and the Server IP dm'd to you by my bot.").queue();
                Objects.requireNonNull(jda.getGuildById(lmp.Constants.GUILD_ID)).addRoleToMember(UserSnowflake.fromId(discordUserID), Objects.requireNonNull(jda.getRoleById(lmp.Constants.MEMBER_ROLE_ID))).queue();
            }
            if (messageContent.equalsIgnoreCase("!joinTime")) {
                channel.sendMessage(discordUserName + " joined on " + discordMember.getTimeJoined().toString().split("T")[0]).queue();
            }
            if (messageContent.equalsIgnoreCase("!link")) {
                discordUser.openPrivateChannel().flatMap(dm -> dm.sendMessage("IP = latch.ddns.net\n" +
                        "Java Port Number = 60\n" +
                        "Bedrock Port Number = 19132\n" +
                        "Run the following command in your minecraft client chat after you join:\n" +
                        "/lmp link " + discordUserID)).queue(null, new ErrorHandler()
                        .handle(ErrorResponse.CANNOT_SEND_TO_USER,
                                (ex) -> Main.log.warning("Cannot send link message to " + discordUserName)));
                channel.sendMessage(discordUserName + " --- Check your Discord for a private message from my bot containing your link command. <:LatchPOG:957363669388386404>").queue();
            }
        }
    }
}
