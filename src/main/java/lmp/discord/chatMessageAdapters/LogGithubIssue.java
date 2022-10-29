package lmp.discord.chatMessageAdapters;

import lmp.LatchDiscord;
import lmp.discord.privateMessageAdapters.IssueTracker;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;

public class LogGithubIssue extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        MessageChannel channel = e.getChannel();
        String message = e.getMessage().getContentRaw();
        User author = e.getAuthor();
        ArrayList<String> issueCommandList = new ArrayList<>();
        issueCommandList.add("!submitissue");
        issueCommandList.add("!submitrequest");
        issueCommandList.add("!submitbug");
        issueCommandList.add("!submitsuggestion");
        if (channel.getId().equals(lmp.Constants.ISSUE_CHANNEL_ID) && issueCommandList.contains(message.toLowerCase())) {
            String issueType = "Issue";
            if (message.equalsIgnoreCase("!submitRequest")) {
                issueType = "Request";
            } else if (message.equalsIgnoreCase("!submitBug")) {
                issueType = "Bug";
            } else if (message.equalsIgnoreCase("!submitSuggestion")) {
                issueType = "Suggestion";
            }
            channel.deleteMessageById(e.getMessageId()).queue();
            channel.sendMessage(e.getAuthor().getName() + " --- Check your Discord DMs to complete your " + issueType + " submission").queue();
            String finalIssueType = issueType;
            author.openPrivateChannel().flatMap(privateChannel -> {
                TextChannel issueCompleteChannel = LatchDiscord.getJDA().getTextChannelById(lmp.Constants.SUBMITTED_ISSUES_CHANNEL_ID);
                e.getJDA().addEventListener(new IssueTracker(privateChannel, author, issueCompleteChannel, finalIssueType));
                return privateChannel.sendMessage("""

                        PLEASE READ
                        There will be 2 questions. Please put as much information in one message as you can.
                        1.) Please answer why are here today in detail.""");
            }).queue();
        }
    }
}
