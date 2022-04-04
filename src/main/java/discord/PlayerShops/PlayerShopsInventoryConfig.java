package discord.PlayerShops;

import discord.Main;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class PlayerShopsInventoryConfig {
    private static final Main plugin = getPlugin(Main.class);
    // Set up playerBackpack.yml configuration file
    public void setup(){
        FileConfiguration playerShopsCfg;
        File playerShopsFile;
        // if the DiscordText folder does not exist, create the DiscordText folder
        if(!plugin.getDataFolder().exists()){
            plugin.getDataFolder().mkdir();
        }
        playerShopsFile = new File(plugin.getDataFolder(), "playerShops.yml");
        playerShopsCfg = YamlConfiguration.loadConfiguration(playerShopsFile);
        //if the playerBackpack.yml does not exist, create it
        if(!playerShopsFile.exists()){
            try {
                playerShopsCfg.save(playerShopsFile);

            }
            catch(IOException e){
                System.out.println(ChatColor.RED + "Could not create the playerShops.yml file");
            }
        }
    }
}
