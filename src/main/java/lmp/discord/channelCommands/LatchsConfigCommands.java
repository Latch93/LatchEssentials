package lmp.discord.channelCommands;

import lmp.api.Api;
import lmp.constants.YmlFileNames;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.util.Arrays;

public class LatchsConfigCommands extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        MessageChannel channel = e.getChannel();
        String channelID = channel.getId();
        String message = e.getMessage().getContentRaw();
        if (channelID.equalsIgnoreCase(lmp.Constants.TEST_CHANNEL_ID)){
            FileConfiguration configCfg = Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME);
            if (message.toLowerCase().contains("!setjoinmessage")) {
                configCfg.set("joinMessage", Arrays.toString(message.split(" ", 2)));
                try {
                    configCfg.save(Api.getConfigFile(YmlFileNames.YML_CONFIG_FILE_NAME));
                    channel.sendMessage("Join message has been updated.").queue();
                } catch (IOException error) {
                    error.printStackTrace();
                }
            }
            if (message.toLowerCase().contains("!returntime")) {
                String[] messageArr = message.split(" ");
                configCfg.set("returnTime", messageArr[1]);
                try {
                    configCfg.save(Api.getConfigFile(YmlFileNames.YML_CONFIG_FILE_NAME));
                    channel.sendMessage("Return time has been set.").queue();
                } catch (IOException error) {
                    error.printStackTrace();
                }
            }
            if (message.toLowerCase().contains("!message")) {
                configCfg.set("afkMessage", Arrays.toString(message.split(" ", 2)));
                try {
                    configCfg.save(Api.getConfigFile(YmlFileNames.YML_CONFIG_FILE_NAME));
                    channel.sendMessage("AFK message has been set.").queue();
                } catch (IOException error) {
                    error.printStackTrace();
                }
            }

            if (message.toLowerCase().contains("!status")) {
                String[] messageArr = message.split(" ");
                configCfg.set("isLatchAFK", Boolean.parseBoolean(messageArr[1]));
                try {
                    configCfg.save(Api.getConfigFile(YmlFileNames.YML_CONFIG_FILE_NAME));
                    channel.sendMessage("AFK status has been set to " + messageArr[1]).queue();
                } catch (IOException error) {
                    error.printStackTrace();
                }
            }
        }
    }
}
