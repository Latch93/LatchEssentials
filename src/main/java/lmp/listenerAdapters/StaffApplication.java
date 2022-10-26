package lmp.listenerAdapters;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class StaffApplication extends ListenerAdapter {
    private static final List<String> applicationAnswers = new ArrayList<>();
    public final long channelId;
    public final long authorId;
    public final MessageChannel applicationSubmittedChannel;
    int counter = 1;

    public StaffApplication(MessageChannel channel, User author, MessageChannel applicationSubmittedChannel) {
        this.channelId = channel.getIdLong();
        this.authorId = author.getIdLong();
        this.applicationSubmittedChannel = applicationSubmittedChannel;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (event.getAuthor().getIdLong() != this.authorId) return;
        if (event.getChannel().getIdLong() != this.channelId) return;
        List<String> applicationQuestions = new LinkedList<>();
        applicationQuestions.add("1.) How old are you?");
        applicationQuestions.add("2.) What country do you live in?");
        applicationQuestions.add("3.) What role are you applying for? Jr.Mod/Helper");
        applicationQuestions.add("4.) Have you been a staff member on another Minecraft or Discord server?");
        applicationQuestions.add("5.) Why do you think you would be a good staff member?");
        applicationQuestions.add("6.) About how many hours per week are you able to be active in Discord and the Minecraft server?");
        applicationQuestions.add("7.) Will you do your best to watch new players while in vanish to ensure they are following the rules?");
        applicationAnswers.add(event.getMessage().getContentRaw());
        if (counter <= applicationQuestions.size() - 1) {
            event.getChannel().sendMessage(applicationQuestions.get(counter)).queue();
        }
        counter++;
        if (counter == applicationQuestions.size() + 1) {
            counter = 1;
            event.getChannel().sendMessage("\n-------------------\nThanks for applying! Latch and his staff will review your information and if \n they approve AND is in need of new staff, he will reach out to you. :smile:").queue();
            int appCounter = 0;
            StringBuilder application = new StringBuilder();
            application.append("Application Submitted: [Username :").append(event.getAuthor().getName()).append("] \n");
            for (String str : applicationAnswers) {
                application.append(applicationQuestions.get(appCounter)).append(" ---> ").append(str).append("\n");
                appCounter++;
            }
            application.append("-------------------");
            applicationSubmittedChannel.sendMessage(application).queue();
            applicationAnswers.clear();
            event.getJDA().removeEventListener(this);
        }
    }
}
