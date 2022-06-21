package lmp.AutoMiner;

import lmp.Main;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class AutoMinerConfig {
    private static final Main plugin = getPlugin(Main.class);
    // Set up autoMiner.yml configuration file
    public void setup(){
        FileConfiguration autoMinerCfg;
        File autoMinerFile;
        // if the DiscordText folder does not exist, create the DiscordText folder
        if(!plugin.getDataFolder().exists()){
            plugin.getDataFolder().mkdir();
        }
        autoMinerFile = new File(plugin.getDataFolder(), "autominer.yml");
        autoMinerCfg = YamlConfiguration.loadConfiguration(autoMinerFile);
        //if the autoMiner.yml does not exist, create it
        if(!autoMinerFile.exists()){
            try {
                autoMinerCfg.save(autoMinerFile);
            }
            catch(IOException e){
                System.out.println(ChatColor.RED + "Could not create the autoMiner.yml file");
            }
        }
    }
}
