package lmp.Backbacks;

import lmp.Constants;
import lmp.Main;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class BackPackInventoryConfig {
    private static final Main plugin = getPlugin(Main.class);
    // Set up playerBackpack.yml configuration file
    public void setup(){
        FileConfiguration playerBackpackCfg;
        File playerBackpackFile;
        // if the DiscordText folder does not exist, create the DiscordText folder
        if(!plugin.getDataFolder().exists()){
            plugin.getDataFolder().mkdir();
        }
        playerBackpackFile = new File(plugin.getDataFolder(), "playerBackpack.yml");
        playerBackpackCfg = YamlConfiguration.loadConfiguration(playerBackpackFile);
        //if the playerBackpack.yml does not exist, create it
        if(!playerBackpackFile.exists()){
            try {
                playerBackpackCfg.save(playerBackpackFile);
            }
            catch(IOException e){
                System.out.println(ChatColor.RED + "Could not create the playerBackpack.yml file");
            }
        }
    }
}
