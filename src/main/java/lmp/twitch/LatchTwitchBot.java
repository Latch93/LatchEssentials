package lmp.twitch;

import com.cavariux.latchtwitch.Core.TwitchBot;
import lmp.Constants;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;

public class LatchTwitchBot extends TwitchBot {

    LatchTwitchBot bot;
    public LatchTwitchBot (String twitchUserName, String oauthToken, String minecraftUsername) throws IOException {
        FileConfiguration twitchCfg = Api.getFileConfiguration(Api.getConfigFile(YmlFileNames.YML_TWITCH_FILE_NAME));
        twitchCfg.set(Constants.YML_PLAYERS + twitchUserName.toLowerCase() + ".twitchUsername", twitchUserName.toLowerCase());
        twitchCfg.set(Constants.YML_PLAYERS + twitchUserName.toLowerCase()+ ".minecraftUsername", minecraftUsername);
        twitchCfg.set(Constants.YML_PLAYERS + twitchUserName.toLowerCase() + ".oauthToken", oauthToken);
        twitchCfg.save(Api.getConfigFile(YmlFileNames.YML_TWITCH_FILE_NAME));
        this.setUsername(twitchUserName.toLowerCase());
        this.setOauth_Key(oauthToken);
        this.setClientID(Api.getFileConfiguration(Api.getConfigFile(YmlFileNames.YML_CONFIG_FILE_NAME)).getString("clientId"));
    }
    public void setBot(LatchTwitchBot bot){
        this.bot = bot;
    }

    public LatchTwitchBot getTwitchBot(){
        return this.bot;
    }
}