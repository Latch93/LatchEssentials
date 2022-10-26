package lmp.configurations;

import lmp.Main;
import lmp.constants.YmlFileNames;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class AdvancementConfig {
    private static final Main plugin = getPlugin(Main.class);

    // Set up advancement.yml configuration file
    public void setup() {
        FileConfiguration advancementCfg;
        File advancementFile;
        // if the DiscordText folder does not exist, create the DiscordText folder
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        advancementFile = new File(plugin.getDataFolder(), YmlFileNames.YML_ADVANCEMENT_FILE_NAME + ".yml");
        advancementCfg = YamlConfiguration.loadConfiguration(advancementFile);
        //if the advancement.yml does not exist, create it
        if (!advancementFile.exists()) {
            try {
                advancementCfg.save(advancementFile);

            } catch (IOException e) {
                Main.log.info("Could not create the " + YmlFileNames.YML_ADVANCEMENT_FILE_NAME + ".yml file");
            }
        }
    }
}
