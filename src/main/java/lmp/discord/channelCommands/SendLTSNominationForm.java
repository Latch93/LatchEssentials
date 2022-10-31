package lmp.discord.channelCommands;

import lmp.discord.privateMessageAdapters.LTSNomination;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static lmp.LatchDiscord.*;

public class SendLTSNominationForm extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        messageChannel = e.getChannel();
        messageID = e.getMessageId();
        messageContents = e.getMessage().getContentRaw();
        senderDiscordUser = e.getAuthor();
        messageChannelID = e.getChannel().getId();
        jda = getJDA();
        // Sends LTS Nomination Form to member
        if (messageChannelID.equals(lmp.Constants.LTS_NOMINEE_CHANNEL_ID) && messageContents.equalsIgnoreCase(lmp.Constants.LTS_NOMINATION_COMMAND)) {
            messageChannel.deleteMessageById(messageID).queue();
            if (messageContents.equalsIgnoreCase(lmp.Constants.LTS_NOMINATION_COMMAND)) {
                senderDiscordUser.openPrivateChannel().flatMap(privateChannel -> {
                    TextChannel applicationSubmittedChannel = jda.getTextChannelById(lmp.Constants.LTS_NOMINEE_CHANNEL_ID);
                    jda.addEventListener(new LTSNomination(privateChannel, senderDiscordUser, applicationSubmittedChannel));
                    return privateChannel.sendMessage("""
                            Please enter your nominations line by line.\s
                            URL Link to Custom Mob Heads -> https://minecraft-heads.com/custom-heads
                            Copy and paste the URL link to the head you want to nominate for the first response.
                            Press enter after each question response.
                            1.) What is your Custom Head Nomination? Please paste the URL Link.""");
                }).queue();
            }
        }
    }
}
