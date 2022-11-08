package lmp.discord.chatMessageAdapters;

import lmp.LatchDiscord;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.joda.time.PeriodType;

import static lmp.LatchDiscord.*;

public class SendAFKMessageIfMentionLatch extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent e){
        messageChannel = e.getChannel();
        messageContents = e.getMessage().getContentRaw();
        jda = LatchDiscord.getJDA();
        if (messageContents.toLowerCase().contains("<@460463941542215691>")) {
            FileConfiguration configCfg = Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME);
            if (configCfg.getBoolean("isLatchAFK")) {
                org.joda.time.LocalDateTime currentLocalDateTime = new org.joda.time.LocalDateTime();
                String endTimeString = configCfg.getString("returnTime");
                org.joda.time.Period p = new org.joda.time.Period(currentLocalDateTime, org.joda.time.LocalDateTime.parse(endTimeString), PeriodType.yearMonthDayTime());
                int days = p.getDays();
                int hours = p.getHours();
                int minutes = p.getMinutes();
                int hoursOfTheDay = hours % 24;
                int minutesOfTheHour = minutes % 60;
                messageChannel.sendMessage(configCfg.getString("afkMessage") + " He will return in " + days + " days | " + hoursOfTheDay + " hours | " + minutesOfTheHour + " minutes").queue();
            }
        }
    }
}
