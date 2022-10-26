package lmp.commands;

import lmp.Constants;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class AutoMinerCommand implements CommandExecutor {
    private static final File autoMinerFile = Api.getConfigFile(YmlFileNames.YML_AUTO_MINER_FILE_NAME);
    private static final FileConfiguration autoMinerCfg = YamlConfiguration.loadConfiguration(autoMinerFile);

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Economy econ = Api.getEconomy();
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        assert player != null;
        int distance = 0;
        try {
            distance = Integer.parseInt(args[1]);

            if (args[0].equalsIgnoreCase("setYDistance")) {
                autoMinerCfg.set(Constants.YML_PLAYERS + player.getName() + ".mine.y", distance);
            }
            if (args[0].equalsIgnoreCase("setXDistance")) {
                autoMinerCfg.set(Constants.YML_PLAYERS + player.getName() + ".mine.x", distance);
            }
            if (args[0].equalsIgnoreCase("setZDistance")) {
                autoMinerCfg.set(Constants.YML_PLAYERS + player.getName() + ".mine.z", distance);
            }
            autoMinerCfg.save(autoMinerFile);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException | IOException e) {
            player.sendMessage(ChatColor.RED + Constants.INVALID_PARAMETERS);
        }
        return false;
    }

}
