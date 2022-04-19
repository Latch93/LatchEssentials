package discord.Configurations;

import discord.Main;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class WhitelistConfig {
    private static final Main plugin = getPlugin(Main.class);
    // Set up autoMiner.yml configuration file
    public void setup(){
        FileConfiguration whitelistCfg;
        File whitelistFile;
        // if the DiscordText folder does not exist, create the DiscordText folder
        if(!plugin.getDataFolder().exists()){
            plugin.getDataFolder().mkdir();
        }
        whitelistFile = new File(plugin.getDataFolder(), "whitelist.yml");
        whitelistCfg = YamlConfiguration.loadConfiguration(whitelistFile);
        //if the whitelist.yml does not exist, create it
        if(!whitelistFile.exists()){
            try {
                whitelistCfg.save(whitelistFile);
            }
            catch(IOException e){
                System.out.println(ChatColor.RED + "Could not create the whitelist.yml file");
            }
        }
    }
}
