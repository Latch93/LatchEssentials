package lmp;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class UnbanRequest extends ListenerAdapter {
    public final long channelId;
    public final long authorId;
    public final MessageChannel unbanRequestChannel;
    int counter = 1;
    private static final List<String> unbanFormAnswers = new ArrayList<>();

    public UnbanRequest(MessageChannel channel, User author, MessageChannel unbanRequestChannel) {
        this.channelId = channel.getIdLong();
        this.authorId = author.getIdLong();
        this.unbanRequestChannel = unbanRequestChannel;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (event.getAuthor().getIdLong() != this.authorId) return;
        if (event.getChannel().getIdLong() != this.channelId) return;
        List<String> unbanFormQuestions = new LinkedList<>();
        unbanFormQuestions.add("1.) What is your Minecraft username?");
        unbanFormQuestions.add("2.) Why were you banned?");
        unbanFormQuestions.add("3.) Why do you think your ban should be revoked or shortened?");
        unbanFormAnswers.add(event.getMessage().getContentRaw());
        if (counter <= unbanFormQuestions.size()-1){
            event.getChannel().sendMessage(unbanFormQuestions.get(counter)).queue();
        }
        counter++;
        if (counter == unbanFormQuestions.size() + 1){
            counter = 1;
            event.getChannel().sendMessage("\n-------------------\nLatch will review your unban request and he \n will decide if you will be unbanned or not.").queue();
            int appCounter = 0;
            StringBuilder unbanFormCompleted = new StringBuilder();
            unbanFormCompleted.append("Unban Form:\n[Discord Username :").append(event.getAuthor().getName()).append("] \n");
            unbanFormCompleted.append("[Minecraft Username :").append(Bukkit.getOfflinePlayer(UUID.fromString(Api.getMinecraftIdFromDCid(event.getAuthor().getId()))).getName()).append("] \n");
            for (String str : unbanFormAnswers){
                unbanFormCompleted.append(unbanFormQuestions.get(appCounter)).append(" ---> ").append(str).append("\n");
                appCounter++;
            }
            unbanFormCompleted.append("-------------------");
            unbanRequestChannel.sendMessage(unbanFormCompleted).queue();
            unbanFormAnswers.clear();
            event.getJDA().removeEventListener(this);
        }
    }
}
