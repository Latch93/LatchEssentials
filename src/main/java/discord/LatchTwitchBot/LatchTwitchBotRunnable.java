package discord.LatchTwitchBot;

import com.cavariux.latchtwitch.Chat.Channel;
import discord.Api;
import discord.Constants;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.util.Objects;

public class LatchTwitchBotRunnable implements Runnable {
        LatchTwitchBot bot;
        String twitchName;
        String oauthToken;
        String minecraftName;
        public LatchTwitchBotRunnable(String twitchName, String oauthToken, String minecraftName){
            this.twitchName = twitchName.toLowerCase();
            this.oauthToken = oauthToken.toLowerCase();
            this.minecraftName = minecraftName;
        }

        public void run() {
            Bukkit.getScheduler().runTaskAsynchronously(Bukkit.getPluginManager().getPlugin(Constants.PLUGIN_NAME), () -> {
                try {
                    this.bot = new LatchTwitchBot(twitchName, oauthToken, minecraftName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                assert this.bot != null;
                this.bot.connect();
                Channel channel = bot.joinChannel("#" + twitchName);
                this.bot.sendMessage("TwitchBot is enabled!", channel);
                this.bot.start();
                FileConfiguration twitchCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_TWITCH_FILE_NAME));
                try {
                    twitchCfg.save(Api.getConfigFile(Constants.YML_TWITCH_FILE_NAME));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

    public LatchTwitchBot getBot() {
        return this.bot;
    }

    public void setBot(LatchTwitchBot bot) {
        this.bot = bot;
    }

    public String getTwitchName() {
        return twitchName;
    }

    public void setTwitchName(String twitchName) {
        this.twitchName = twitchName;
    }

    public String getOauthToken() {
        return oauthToken;
    }

    public void setOauthToken(String oauthToken) {
        this.oauthToken = oauthToken;
    }

    public String getMinecraftName() {
        return minecraftName;
    }

    public void setMinecraftName(String minecraftName) {
        this.minecraftName = minecraftName;
    }
}
