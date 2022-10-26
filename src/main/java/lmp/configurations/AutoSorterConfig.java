package lmp.configurations;

import lmp.Main;
import lmp.constants.YmlFileNames;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class AutoSorterConfig {
    private static final Main plugin = getPlugin(Main.class);

    // Set up autoSorter.yml configuration file
    public void setup() {
        FileConfiguration autoSorterCfg;
        File autoSorterFile;
        // if the DiscordText folder does not exist, create the DiscordText folder
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        autoSorterFile = new File(plugin.getDataFolder(), YmlFileNames.YML_AUTO_SORTER_FILE_NAME + ".yml");
        autoSorterCfg = YamlConfiguration.loadConfiguration(autoSorterFile);
        //if the autoSorter.yml does not exist, create it
        if (!autoSorterFile.exists()) {
            try {
                autoSorterCfg.save(autoSorterFile);

            } catch (IOException e) {
                Main.log.info("Could not create the " + YmlFileNames.YML_AUTO_SORTER_FILE_NAME + ".yml file");
            }
        }
    }
}
