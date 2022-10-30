package lmp.discord.chatMessageAdapters;

import lmp.api.Api;
import lmp.constants.YmlFileNames;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.joda.time.PeriodType;

import static lmp.LatchDiscord.messageChannel;
import static lmp.LatchDiscord.messageContents;

public class SendAFKMessageIfMentionLatch extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent e){
        if (messageContents.toLowerCase().contains("<@latch>")) {
            FileConfiguration configCfg = Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME);
            if (Boolean.TRUE.equals(configCfg.getBoolean("isLatchAFK"))) {
                org.joda.time.LocalDateTime currentLocalDateTime = new org.joda.time.LocalDateTime();
                String endTimeString = configCfg.getString("returnTime");
                //currentLocalDateTime, LocalDateTime.parse(endTimeString), PeriodType.yearMonthDayTime()
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
