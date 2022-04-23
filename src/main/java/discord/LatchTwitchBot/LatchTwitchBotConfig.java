package discord.LatchTwitchBot;

import discord.Constants;
import discord.Main;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class LatchTwitchBotConfig {
    private static final Main plugin = getPlugin(Main.class);
    // Set up twitch.yml configuration file
    public void setup(){
        FileConfiguration twitchCfg;
        File twitchFile;
        // if the DiscordText folder does not exist, create the DiscordText folder
        if(!plugin.getDataFolder().exists()){
            plugin.getDataFolder().mkdir();
        }
        twitchFile = new File(plugin.getDataFolder(), Constants.YML_TWITCH_FILE_NAME + ".yml");
        twitchCfg = YamlConfiguration.loadConfiguration(twitchFile);
        //if the twitch.yml does not exist, create it
        if(!twitchFile.exists()){
            try {
                twitchCfg.save(twitchFile);

            }
            catch(IOException e){
                System.out.println(ChatColor.RED + "Could not create the " + Constants.YML_TWITCH_FILE_NAME + ".yml file");
            }
        }
    }
}