package discord.Backbacks;

import discord.Api;
import discord.Constants;
import discord.Main;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Objects;

public class BackPackCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = null;
        Economy econ = Api.getEconomy();
        if (sender instanceof Player){
            player = (Player) sender;
            String invTitle = player.getName() + "'s Backpack";
            FileConfiguration backPackCfg = Api.loadConfig(Constants.YML_BACK_PACK_FILE_NAME);
            OfflinePlayer offlinePlayer = null;
            for (OfflinePlayer olp : Bukkit.getWhitelistedPlayers()){
                if (player.getName().equalsIgnoreCase(olp.getName())){
                    offlinePlayer = olp;
                }
            }
            if (args.length == 1){
                if (args[0].equalsIgnoreCase(Constants.BACKPACK_UPGRADE_COMMAND)) {
                    if (backPackCfg.get(player.getName() + Constants.YML_SIZE) != null){
                        int numberOfSlots = backPackCfg.getInt(player.getName() + Constants.YML_SIZE);
                        double balance = econ.getBalance(offlinePlayer);
                        if (numberOfSlots == 9){
                            if (balance >= backPackCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_2)){
                                backPackCfg.set(player.getName() + Constants.YML_SIZE, 18);
                                player.sendMessage(ChatColor.GREEN + Constants.YML_BACKPACK_LEVEL_IS_NOW + ChatColor.GOLD + "2" + ChatColor.GREEN + Constants.YML_OUT_OF +ChatColor.GOLD + " 6" +ChatColor.GREEN + Constants.YML_BACKPACK_NOW_HOLDS + ChatColor.GOLD + "18 " + ChatColor.GREEN + Constants.YML_ITEMS);
                                econ.withdrawPlayer(offlinePlayer, backPackCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_2));
                            } else {
                                player.sendMessage(ChatColor.RED + Constants.YML_YOU_NEED_AT_LEAST + ChatColor.GOLD + "$" + backPackCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_2) + ChatColor.RED + Constants.YML_TO_UPGRADE_YOUR_BACKPACK);
                            }
                        } else if (numberOfSlots == 18){
                            if (balance >= backPackCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_3)) {
                                backPackCfg.set(player.getName() + Constants.YML_SIZE, 27);
                                player.sendMessage(ChatColor.GREEN + Constants.YML_BACKPACK_LEVEL_IS_NOW + ChatColor.GOLD + "3" + ChatColor.GREEN + Constants.YML_OUT_OF +ChatColor.GOLD + " 6" +ChatColor.GREEN + Constants.YML_BACKPACK_NOW_HOLDS + ChatColor.GOLD + "27 " + ChatColor.GREEN + Constants.YML_ITEMS);
                                econ.withdrawPlayer(offlinePlayer, backPackCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_3));
                            } else {
                                player.sendMessage(ChatColor.RED + Constants.YML_YOU_NEED_AT_LEAST + ChatColor.GOLD + "$" + backPackCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_3) + ChatColor.RED + Constants.YML_TO_UPGRADE_YOUR_BACKPACK);
                            }
                        } else if (numberOfSlots == 27){
                            if (balance >= backPackCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_4)) {
                                backPackCfg.set(player.getName() + Constants.YML_SIZE, 36);
                                player.sendMessage(ChatColor.GREEN + Constants.YML_BACKPACK_LEVEL_IS_NOW + ChatColor.GOLD + "4" + ChatColor.GREEN + Constants.YML_OUT_OF +ChatColor.GOLD + " 6" +ChatColor.GREEN + Constants.YML_BACKPACK_NOW_HOLDS + ChatColor.GOLD + "36 " + ChatColor.GREEN + Constants.YML_ITEMS);
                                econ.withdrawPlayer(offlinePlayer, backPackCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_4));
                            } else {
                                player.sendMessage(ChatColor.RED + Constants.YML_YOU_NEED_AT_LEAST + ChatColor.GOLD + "$" + backPackCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_4) + ChatColor.RED + Constants.YML_TO_UPGRADE_YOUR_BACKPACK);
                            }
                        } else if (numberOfSlots == 36){
                            if (balance >= backPackCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_5)) {
                                backPackCfg.set(player.getName() + Constants.YML_SIZE, 45);
                                player.sendMessage(ChatColor.GREEN + Constants.YML_BACKPACK_LEVEL_IS_NOW + ChatColor.GOLD + "5" + ChatColor.GREEN + Constants.YML_OUT_OF +ChatColor.GOLD + " 6" +ChatColor.GREEN + Constants.YML_BACKPACK_NOW_HOLDS + ChatColor.GOLD + "45 " + ChatColor.GREEN + Constants.YML_ITEMS);
                                econ.withdrawPlayer(offlinePlayer, backPackCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_5));
                            } else {
                                player.sendMessage(ChatColor.RED + Constants.YML_YOU_NEED_AT_LEAST + ChatColor.GOLD + "$" + backPackCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_5) + ChatColor.RED + Constants.YML_TO_UPGRADE_YOUR_BACKPACK);
                            }
                        } else if (numberOfSlots == 45){
                            if (balance >= backPackCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_6)){
                                backPackCfg.set(player.getName() + Constants.YML_SIZE, 54);
                                player.sendMessage(ChatColor.GREEN + Constants.YML_BACKPACK_LEVEL_IS_NOW + ChatColor.GOLD + "6" + ChatColor.GREEN + Constants.YML_OUT_OF +ChatColor.GOLD + " 6" +ChatColor.GREEN + Constants.YML_BACKPACK_NOW_HOLDS + ChatColor.GOLD + "54 " + ChatColor.GREEN + Constants.YML_ITEMS);
                                econ.withdrawPlayer(offlinePlayer, backPackCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_6));
                            } else {
                                player.sendMessage(ChatColor.RED + Constants.YML_YOU_NEED_AT_LEAST + ChatColor.GOLD + "$" + backPackCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_6) + ChatColor.RED + Constants.YML_TO_UPGRADE_YOUR_BACKPACK);
                            }
                        } else {
                            player.sendMessage(ChatColor.GREEN + "Congratulations!!! Your backpack is at max capacity!!!");
                        }
                    } else {
                        player.sendMessage(ChatColor.GREEN + "You need to purchase a backpack before you are able to upgrade.");
                    }
                }
                if (args[0].equalsIgnoreCase(Constants.BACKPACK_BUY_COMMAND)){
                    double playerBalance = econ.getBalance(offlinePlayer);
                    if (backPackCfg.get(player.getName() + Constants.YML_SIZE) == null){
                        if (playerBalance >= backPackCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_1)){
                            backPackCfg.set(player.getName() + Constants.YML_SIZE, 9);
                            player.sendMessage(ChatColor.GREEN + "You just purchased a " + ChatColor.GOLD + "Level 1 Backpack" + ChatColor.GREEN + ". You can store " + ChatColor.GOLD + "9" + ChatColor.GREEN + " items.");
                        } else {
                            player.sendMessage(ChatColor.RED + Constants.YML_YOU_NEED_AT_LEAST + ChatColor.GOLD + "$" + backPackCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_1) + ChatColor.RED + " to purchase a backpack.");
                        }
                    } else {
                        player.sendMessage(ChatColor.GREEN + "You already own a backpack.");
                    }
                }
                if (args[0].equalsIgnoreCase(Constants.OPEN_COMMAND)) {
                    try {
                        Objects.requireNonNull(player.getPlayer()).openInventory(Inventories.setInventoryWhenOpened(player, Constants.YML_BACK_PACK_FILE_NAME, backPackCfg.getInt(player.getName() + ".size"), invTitle, player.getName()));
                    } catch (NullPointerException ignored){

                    }
                }
            }
            try {
                backPackCfg.save(Api.getConfigFile(Constants.YML_BACK_PACK_FILE_NAME));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
