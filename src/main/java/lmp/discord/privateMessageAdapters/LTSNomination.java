package lmp.discord.privateMessageAdapters;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LTSNomination extends ListenerAdapter {
    private static final List<String> nominationAnswers = new ArrayList<>();
    public final long channelId;
    public final long authorId;
    public final MessageChannel LTSNominationChannel;
    int counter = 1;

    public LTSNomination(MessageChannel channel, User author, MessageChannel LTSNominationChannel) {
        this.channelId = channel.getIdLong();
        this.authorId = author.getIdLong();
        this.LTSNominationChannel = LTSNominationChannel;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (event.getAuthor().getIdLong() != this.authorId) return;
        if (event.getChannel().getIdLong() != this.channelId) return;
        List<String> ltsNominationQuestions = new LinkedList<>();
        ltsNominationQuestions.add("1.) What is your Custom Head Nomination? Please paste the URL Link.");
        ltsNominationQuestions.add("2.) What category of Overpowered item do you nominate? Armor/Tool/Weapon");
        ltsNominationQuestions.add("3.) What type of item is your nomination? Chestplate/Bow/Pickaxe");
        ltsNominationQuestions.add("4.) What enchantments would you like to see on your item?");
        ltsNominationQuestions.add("5.) What is the name of your Overpowered nomination?");
        ltsNominationQuestions.add("6.) What block do you nominate?");
        ltsNominationQuestions.add("7.) What additional block or head do you nominate, if any?");
        nominationAnswers.add(event.getMessage().getContentRaw());
        if (counter <= ltsNominationQuestions.size() - 1) {
            event.getChannel().sendMessage(ltsNominationQuestions.get(counter)).queue();
        }
        counter++;
        if (counter == ltsNominationQuestions.size() + 1) {
            counter = 1;
            event.getChannel().sendMessage("\n-------------------\nThank you for nominating items for the LTS :smile:").queue();

            int appCounter = 0;
            StringBuilder application = new StringBuilder();
            application.append("Nomination Submitted: [Username :").append(event.getAuthor().getName()).append("] \n");
            for (String str : nominationAnswers) {
                application.append(ltsNominationQuestions.get(appCounter)).append(" ---> ").append(str).append("\n");
                appCounter++;
            }
            application.append("-------------------");
            LTSNominationChannel.sendMessage(application).queue();
            nominationAnswers.clear();
            event.getJDA().removeEventListener(this);
        }
    }
}
