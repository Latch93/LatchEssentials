package lmp.commands;

import lmp.Constants;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import lmp.listeners.playerJoinEvents.BankLoginEvent;
import lmp.listeners.playerQuitEvents.BankLogoutEvent;
import net.milkbowl.vault.economy.Economy;
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
import java.util.concurrent.ExecutionException;

public class HardcoreCommand implements CommandExecutor {


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        File hardcoreFile = Api.getConfigFile(YmlFileNames.YML_HARDCORE_FILE_NAME);
        FileConfiguration hardcoreCfg = Api.getFileConfiguration(YmlFileNames.YML_HARDCORE_FILE_NAME);
        Economy econ = Api.getEconomy();
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        assert player != null;
        double playerBalance = Api.getPlayerBalance(player);
        String playerUUID = player.getUniqueId().toString();
        try {
            if (args[0] != null) {
                if (args[0].equalsIgnoreCase("buy")){
                    if (playerBalance >= 1000000) {
                        if (hardcoreCfg.isSet(playerUUID)){
                            if (hardcoreCfg.getBoolean(playerUUID + ".doesPlayerHaveSeasonPass")){
                                player.sendMessage(ChatColor.RED + "You already own the current Hardcore Season Pass");
                            } else {
                                Api.addPlayerToPermissionGroup(playerUUID, "hardcore");
                                Api.takePlayerMoney(playerUUID, 1000000);
                                hardcoreCfg.set(playerUUID + ".isAlive", true);
                                hardcoreCfg.set(playerUUID + ".uuid", playerUUID);
                                hardcoreCfg.set(playerUUID + ".name", player.getName());
                                hardcoreCfg.set(playerUUID + ".doesPlayerHaveSeasonPass", true);
                                hardcoreCfg.save(Api.getConfigFile(YmlFileNames.YML_HARDCORE_FILE_NAME));
                                player.sendMessage(ChatColor.GREEN + "You've just bought this month's Hardcore Season Pass. To get to LMP Hardcore, type " + ChatColor.AQUA + "/lmp hardcore");
                            }
                        }
                        else {
                            Location hardcoreSpawnLocation = new Location(Bukkit.getWorld("hardcore"), -67.600, 65.5, 50.5, (float) -3.000, (float) 92.10);
                            Api.takePlayerMoney(playerUUID, 1000000);
                            Api.addPlayerToPermissionGroup(playerUUID, "hardcore");
                            hardcoreCfg.set(playerUUID + ".isAlive", true);
                            hardcoreCfg.set(playerUUID + ".uuid", playerUUID);
                            hardcoreCfg.set(playerUUID + ".name", player.getName());
                            hardcoreCfg.set(playerUUID + ".doesPlayerHaveSeasonPass", true);
                            hardcoreCfg.set(playerUUID + ".lastLocation", hardcoreSpawnLocation);
                            hardcoreCfg.save(Api.getConfigFile(YmlFileNames.YML_HARDCORE_FILE_NAME));
                            player.sendMessage(ChatColor.GREEN + "You've just bought this month's Hardcore Season Pass. To get to LMP Hardcore, type " + ChatColor.AQUA + "/lmp hardcore");
                        }
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "Hardcore Season passes cost " + ChatColor.AQUA + "$1,000,000" + ChatColor.YELLOW + " in-game currency.");
                    }
                } else if (args[0].equalsIgnoreCase("warp")){
                    if (!player.getWorld().getName().contains("hardcore")){
                        if (hardcoreCfg.getBoolean(playerUUID + ".doesPlayerHaveSeasonPass") && Api.doesPlayerHavePermission(playerUUID, "hardcore")) {
                            Api.teleportHardcorePlayerToLastLocation(player);
                        } else {
                            player.sendMessage(ChatColor.RED + "You must purchase a hardcore season pass for " + ChatColor.GOLD + "$3 USD. | $1,000,000 in game to warp");
                        }
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "You can't warp to LMP Hardcore when you are already there.");
                    }
                } else if (args[0].equalsIgnoreCase("afk")){
                    if (player.getWorld().getName().contains("hardcore")) {
                        BankLogoutEvent.setPlayerSessionTime(player);
                        BankLoginEvent.setPlayerLoginTime(player);
                        Api.givePlayerLuckPermPermission(player, "essentials.afk.kickexempt");
                        BankLogoutEvent.setPlayerSessionTime(player);
                        Api.setBankSessionToAFK(true, player);
                    } else {
                        player.sendMessage(ChatColor.RED + "You can only use this command in a Hardcore world.");
                    }
                }
            }

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException | IOException e) {
            player.sendMessage(ChatColor.RED + Constants.INVALID_PARAMETERS);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

}