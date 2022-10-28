package lmp.listeners.discord.onReadyEvents;

import lmp.api.Api;
import lmp.constants.YmlFileNames;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.awt.*;
import java.io.IOException;

public class SendServerStartedMessageToDiscordEvent extends ListenerAdapter {


    @Override
    public void onReady(@NotNull ReadyEvent event) {
        FileConfiguration enabledEventsCfg = Api.getFileConfiguration(YmlFileNames.YML_ENABLED_EVENTS_FILE_NAME);
        if (enabledEventsCfg.getBoolean("broadcastServerStartedMessageToDiscord")) {
            FileConfiguration configCfg = Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME);
            DateTime dt = new DateTime();
            DateTimeFormatter fmt = DateTimeFormat.forPattern("MMM-dd-yyyy hh:mm:ssa z");
            String dtStr = fmt.print(dt);
            configCfg.set("serverStartTime", dtStr);
            try {
                configCfg.save(Api.getConfigFile(YmlFileNames.YML_CONFIG_FILE_NAME));
            } catch (IOException e) {
                e.printStackTrace();
            }
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("LMP Server has started.", null);
            eb.setDescription(dtStr);
            eb.setColor(new Color(0xE10233E5, true));
            eb.setThumbnail("https://raw.githubusercontent.com/Latch93/DiscordText/master/src/main/resources/lmp_discord_image.png");
            TextChannel minecraftChannel = event.getJDA().getTextChannelById(lmp.Constants.MINECRAFT_CHAT_CHANNEL_ID);
            assert minecraftChannel != null;
            minecraftChannel.sendMessageEmbeds(eb.build()).queue();
        }
    }

}
