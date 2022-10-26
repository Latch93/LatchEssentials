package lmp.configurations;

import lmp.Main;
import lmp.constants.YmlFileNames;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class LatchTwitchBotConfig {
    private static final Main plugin = getPlugin(Main.class);

    // Set up twitch.yml configuration file
    public void setup() {
        FileConfiguration twitchCfg;
        File twitchFile;
        // if the DiscordText folder does not exist, create the DiscordText folder
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        twitchFile = new File(plugin.getDataFolder(), YmlFileNames.YML_TWITCH_FILE_NAME + ".yml");
        twitchCfg = YamlConfiguration.loadConfiguration(twitchFile);
        //if the twitch.yml does not exist, create it
        if (!twitchFile.exists()) {
            try {
                twitchCfg.save(twitchFile);

            } catch (IOException e) {
                Main.log.info("Could not create the " + YmlFileNames.YML_TWITCH_FILE_NAME + ".yml file");
            }
        }
    }
}