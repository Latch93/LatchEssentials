package lmp.commands;

import lmp.Constants;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import lmp.listeners.playerJoinEvents.BankLoginEvent;
import lmp.listeners.playerQuitEvents.BankLogoutEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class AnarchyCommand implements CommandExecutor {


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        File anarchyFile = Api.getConfigFile(YmlFileNames.YML_ANARCHY_FILE_NAME);
        FileConfiguration anarchyCfg = Api.getFileConfiguration(YmlFileNames.YML_ANARCHY_FILE_NAME);
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        assert player != null;
        String playerUUID = player.getUniqueId().toString();
        try {
            if (args[0] != null) {
                if (args[0].equalsIgnoreCase("warp")){
                    if (!anarchyCfg.isSet(playerUUID)){
                        Location anarchySpawnLocation = new Location(Bukkit.getWorld("anarchy"), 13.5, 63, -25.5, (float) -3.000, (float) 92.10);
                        anarchyCfg.set(playerUUID + ".uuid", playerUUID);
                        anarchyCfg.set(playerUUID + ".name", player.getName());
                        anarchyCfg.set(playerUUID + ".lastLocation", anarchySpawnLocation);
                        player.teleport(anarchySpawnLocation);
                        anarchyCfg.save(anarchyFile);
                    }
                    else {
                        Api.teleportAnarchyPlayerToLastLocation(player);
                    }
                } else if (args[0].equalsIgnoreCase("afk")){
                    if (player.getWorld().getName().contains("anarchy")) {
                        BankLogoutEvent.setPlayerSessionTime(player);
                        BankLoginEvent.setPlayerLoginTime(player);
                        Api.givePlayerLuckPermPermission(player, "essentials.afk.kickexempt");
                        Api.givePlayerLuckPermPermission(player, "essentials.sleepingignored");

                        BankLogoutEvent.setPlayerSessionTime(player);
                        Api.setBankSessionToAFK(true, player);
                    } else {
                        player.sendMessage(ChatColor.RED + "You can only use this command in an Anarchy world.");
                    }
                }

            }

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException | IOException e) {
            player.sendMessage(ChatColor.RED + Constants.INVALID_PARAMETERS);
        }
        return false;
    }

}