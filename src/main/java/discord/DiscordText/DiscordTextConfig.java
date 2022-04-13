package discord.DiscordText;

import discord.Constants;
import discord.Main;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class DiscordTextConfig {
    public static final Main plugin = getPlugin(Main.class);
    // Set up discordTextConfig.yml configuration file
    public void setup(){
        FileConfiguration discordTextCfg;
        File discordTextFile;
        // if the DiscordText folder does not exist, create the DiscordText folder
        if(!plugin.getDataFolder().exists()){
            plugin.getDataFolder().mkdir();
        }
        discordTextFile = new File(plugin.getDataFolder(), Constants.YML_DISCORD_TEXT_FILE_NAME + ".yml");
        discordTextCfg = YamlConfiguration.loadConfiguration(discordTextFile);
        //if the discordTextConfig.yml does not exist, create it
        if(!discordTextFile.exists()){
            try {
                discordTextCfg.set("randomItemGen.cost", 1000);
                discordTextCfg.set("randomItemGen.buttonLocation.x",10005);
                discordTextCfg.set("randomItemGen.buttonLocation.y",68);
                discordTextCfg.set("randomItemGen.buttonLocation.z",9998);
                discordTextCfg.set("randomItemGen.itemDropLocation.x",10006);
                discordTextCfg.set("randomItemGen.itemDropLocation.y",70);
                discordTextCfg.set("randomItemGen.itemDropLocation.z",9998);

                discordTextCfg.save(discordTextFile);
            }
            catch(IOException e){
                System.out.println(ChatColor.RED + "Could not create the discordTextConfig.yml file");
            }
        }
    }
}
