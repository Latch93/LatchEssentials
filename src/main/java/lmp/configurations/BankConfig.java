package lmp.configurations;

import lmp.Main;
import lmp.constants.YmlFileNames;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class BankConfig {
    public static final Main plugin = getPlugin(Main.class);
    // Set up bank.yml configuration file
    public void setup(){
        FileConfiguration bankCfg;
        File bankFile;
        // if the DiscordText folder does not exist, create the DiscordText folder
        if(!plugin.getDataFolder().exists()){
            plugin.getDataFolder().mkdir();
        }
        bankFile = new File(plugin.getDataFolder(), YmlFileNames.YML_BANK_FILE_NAME + ".yml");
        bankCfg = YamlConfiguration.loadConfiguration(bankFile);
        //if the bank.yml does not exist, create it
        if(!bankFile.exists()){
            try {
                bankCfg.save(bankFile);
            }
            catch(IOException e){
                Main.log.info(ChatColor.RED + "Could not create the bank.yml file");
            }
        }
    }
}
