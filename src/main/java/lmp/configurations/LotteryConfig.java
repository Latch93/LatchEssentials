package lmp.configurations;

import lmp.Main;
import lmp.constants.YmlFileNames;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class LotteryConfig {
    private static final Main plugin = getPlugin(Main.class);

    // Set up lottery.yml configuration file
    public void setup() {
        FileConfiguration lotteryCfg;
        File lotteryFile;
        // if the DiscordText folder does not exist, create the DiscordText folder
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        lotteryFile = new File(plugin.getDataFolder(), YmlFileNames.YML_LOTTERY_FILE_NAME + ".yml");
        lotteryCfg = YamlConfiguration.loadConfiguration(lotteryFile);
        //if the lottery.yml does not exist, create it
        if (!lotteryFile.exists()) {
            try {
                lotteryCfg.save(lotteryFile);
            } catch (IOException e) {
                Main.log.info("Could not create the " + YmlFileNames.YML_LOTTERY_FILE_NAME + ".yml file");
            }
        }
    }
}
