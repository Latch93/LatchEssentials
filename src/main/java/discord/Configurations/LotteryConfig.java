package discord.Configurations;

import discord.Constants;
import discord.Main;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class LotteryConfig {
    private static final Main plugin = getPlugin(Main.class);
    // Set up lottery.yml configuration file
    public void setup(){
        FileConfiguration lotteryCfg;
        File lotteryFile;
        // if the DiscordText folder does not exist, create the DiscordText folder
        if(!plugin.getDataFolder().exists()){
            plugin.getDataFolder().mkdir();
        }
        lotteryFile = new File(plugin.getDataFolder(), Constants.YML_LOTTERY_FILE_NAME + ".yml");
        lotteryCfg = YamlConfiguration.loadConfiguration(lotteryFile);
        //if the lottery.yml does not exist, create it
        if(!lotteryFile.exists()){
            try {
                lotteryCfg.save(lotteryFile);
            }
            catch(IOException e){
                System.out.println(ChatColor.RED + "Could not create the " + Constants.YML_LOTTERY_FILE_NAME + ".yml file");
            }
        }
    }
}
