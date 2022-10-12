package lmp;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class IssueTracker extends ListenerAdapter {
    public final long channelId;
    public final User author;
    public final long authorId;
    public final MessageChannel issueTrackerChannel;
    public final String issueType;
    int counter = 0;
    private static final List<String> issueAnswers = new ArrayList<>();

    public IssueTracker(MessageChannel channel, User author, MessageChannel issueTrackerChannel, String issueType) {
        this.channelId = channel.getIdLong();
        this.author = author;
        this.issueTrackerChannel = issueTrackerChannel;
        this.authorId = author.getIdLong();
        this.issueType = issueType;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (event.getAuthor().getIdLong() != this.authorId) return;
        if (event.getChannel().getIdLong() != this.channelId) return;
        List<String> issueTrackerQuestions = new LinkedList<>();
        issueTrackerQuestions.add("1.) Please answer why are here today in detail.");
        issueTrackerQuestions.add("2.) What additional information, if any, would you like to provide? Enter 'No' if you have nothing else to add");
        issueAnswers.add(event.getMessage().getContentRaw());
        counter++;
        if (counter == 1) {
            event.getChannel().sendMessage(issueTrackerQuestions.get(1)).queue();
        }
        if (counter == 2){
            event.getChannel().sendMessage("\n-------------------\nThanks for submitting your " + issueType + ". Latch will review this " + issueType + " when he gets the chance. :smile:").queue();
            StringBuilder issue = new StringBuilder();
            issue.append(issueType).append(" Submitted --- [Username :").append(author.getName()).append("] \n");
            issue.append(issueAnswers.get(0)).append("\n");
            if (!issueAnswers.get(1).equalsIgnoreCase("no")){
                issue.append(issueAnswers.get(1));
            }
            issue.append("\n-------------------");
            issueTrackerChannel.sendMessage(issue).queue();
            GitHubClient githubClient = new GitHubClient().setOAuth2Token("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");
            IssueService issueService = new IssueService(githubClient);
            Issue githubIssue = new Issue();
            githubIssue.setTitle(issueType.toUpperCase() + ": DC[" + author.getName() + "]");
            StringBuilder issueBody = new StringBuilder();
            issueBody.append(issueAnswers.get(0));
            if (!issueAnswers.get(1).equalsIgnoreCase("no")){
                issueBody.append("\n").append(issueAnswers.get(1));
            }
            List<Label> labelList = new ArrayList<>();
            Label label = new Label();
            label.setName(issueType);
            labelList.add(label);
            githubIssue.setLabels(labelList);
            githubIssue.setBody(String.valueOf(issueBody));
            try {
                issueService.createIssue("Latch93", "DiscordText", githubIssue);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            issueAnswers.clear();
            event.getJDA().removeEventListener(this);
        }
    }
}
