package discord.LatchTwitchBot;
import com.cavariux.latchtwitch.Chat.Channel;
import com.cavariux.latchtwitch.Chat.User;
import com.cavariux.latchtwitch.Core.TwitchBot;
import discord.Api;
import discord.Constants;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;

public class LatchTwitchBot extends TwitchBot {

    LatchTwitchBot bot;
    public LatchTwitchBot (String twitchUserName, String oauthToken, String minecraftUsername) throws IOException {
        FileConfiguration twitchCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_TWITCH_FILE_NAME));
        twitchCfg.set(Constants.YML_PLAYERS + twitchUserName.toLowerCase() + ".twitchUsername", twitchUserName.toLowerCase());
        twitchCfg.set(Constants.YML_PLAYERS + twitchUserName.toLowerCase()+ ".minecraftUsername", minecraftUsername);
        twitchCfg.set(Constants.YML_PLAYERS + twitchUserName.toLowerCase() + ".oauthToken", oauthToken);
        twitchCfg.save(Api.getConfigFile(Constants.YML_TWITCH_FILE_NAME));
        this.setUsername(twitchUserName.toLowerCase());
        this.setOauth_Key(oauthToken);
        this.setClientID(Api.getFileConfiguration(Api.getConfigFile(Constants.YML_CONFIG_FILE_NAME)).getString("clientId"));
    }
    public void setBot(LatchTwitchBot bot){
        this.bot = bot;
    }

    public LatchTwitchBot getTwitchBot(){
        return this.bot;
    }
}