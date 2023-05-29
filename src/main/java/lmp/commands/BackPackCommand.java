package lmp.commands;

import lmp.Constants;
import lmp.Inventories;
import lmp.api.Api;
import lmp.constants.ServerCommands;
import lmp.constants.YmlFileNames;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static lmp.api.Api.getMainPlugin;

public class BackPackCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = null;
        Economy econ = Api.getEconomy();
        if (sender instanceof Player) {
            player = (Player) sender;
            String invTitle = player.getName() + "'s Backpack";
            File playerBPFile = new File(getMainPlugin().getDataFolder() + "/playerBackpacks/", player.getUniqueId().toString() + ".yml");
            FileConfiguration backPackCfg = YamlConfiguration.loadConfiguration(playerBPFile);
            FileConfiguration configCfg = Api.loadConfig(YmlFileNames.YML_CONFIG_FILE_NAME);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase(ServerCommands.BACKPACK_UPGRADE_COMMAND)) {
                    if (backPackCfg.get(player.getUniqueId() + Constants.YML_SIZE) != null) {
                        int numberOfSlots = backPackCfg.getInt(player.getUniqueId() + Constants.YML_SIZE);
                        double balance = econ.getBalance(offlinePlayer);
                        if (numberOfSlots == 9) {
                            if (balance >= configCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_2)) {
                                backPackCfg.set(player.getUniqueId() + Constants.YML_SIZE, 18);
                                player.sendMessage(ChatColor.GREEN + Constants.YML_BACKPACK_LEVEL_IS_NOW + ChatColor.GOLD + "2" + ChatColor.GREEN + Constants.YML_OUT_OF + ChatColor.GOLD + " 6" + ChatColor.GREEN + Constants.YML_BACKPACK_NOW_HOLDS + ChatColor.GOLD + "18 " + ChatColor.GREEN + Constants.YML_ITEMS);
                                econ.withdrawPlayer(offlinePlayer, configCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_2));
                            } else {
                                player.sendMessage(ChatColor.RED + Constants.YML_YOU_NEED_AT_LEAST + ChatColor.GOLD + "$" + configCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_2) + ChatColor.RED + Constants.YML_TO_UPGRADE_YOUR_BACKPACK);
                            }
                        } else if (numberOfSlots == 18) {
                            if (balance >= configCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_3)) {
                                backPackCfg.set(player.getUniqueId() + Constants.YML_SIZE, 27);
                                player.sendMessage(ChatColor.GREEN + Constants.YML_BACKPACK_LEVEL_IS_NOW + ChatColor.GOLD + "3" + ChatColor.GREEN + Constants.YML_OUT_OF + ChatColor.GOLD + " 6" + ChatColor.GREEN + Constants.YML_BACKPACK_NOW_HOLDS + ChatColor.GOLD + "27 " + ChatColor.GREEN + Constants.YML_ITEMS);
                                econ.withdrawPlayer(offlinePlayer, configCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_3));
                            } else {
                                player.sendMessage(ChatColor.RED + Constants.YML_YOU_NEED_AT_LEAST + ChatColor.GOLD + "$" + configCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_3) + ChatColor.RED + Constants.YML_TO_UPGRADE_YOUR_BACKPACK);
                            }
                        } else if (numberOfSlots == 27) {
                            if (balance >= configCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_4)) {
                                backPackCfg.set(player.getUniqueId() + Constants.YML_SIZE, 36);
                                player.sendMessage(ChatColor.GREEN + Constants.YML_BACKPACK_LEVEL_IS_NOW + ChatColor.GOLD + "4" + ChatColor.GREEN + Constants.YML_OUT_OF + ChatColor.GOLD + " 6" + ChatColor.GREEN + Constants.YML_BACKPACK_NOW_HOLDS + ChatColor.GOLD + "36 " + ChatColor.GREEN + Constants.YML_ITEMS);
                                econ.withdrawPlayer(offlinePlayer, configCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_4));
                            } else {
                                player.sendMessage(ChatColor.RED + Constants.YML_YOU_NEED_AT_LEAST + ChatColor.GOLD + "$" + configCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_4) + ChatColor.RED + Constants.YML_TO_UPGRADE_YOUR_BACKPACK);
                            }
                        } else if (numberOfSlots == 36) {
                            if (balance >= configCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_5)) {
                                backPackCfg.set(player.getUniqueId() + Constants.YML_SIZE, 45);
                                player.sendMessage(ChatColor.GREEN + Constants.YML_BACKPACK_LEVEL_IS_NOW + ChatColor.GOLD + "5" + ChatColor.GREEN + Constants.YML_OUT_OF + ChatColor.GOLD + " 6" + ChatColor.GREEN + Constants.YML_BACKPACK_NOW_HOLDS + ChatColor.GOLD + "45 " + ChatColor.GREEN + Constants.YML_ITEMS);
                                econ.withdrawPlayer(offlinePlayer, configCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_5));
                            } else {
                                player.sendMessage(ChatColor.RED + Constants.YML_YOU_NEED_AT_LEAST + ChatColor.GOLD + "$" + configCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_5) + ChatColor.RED + Constants.YML_TO_UPGRADE_YOUR_BACKPACK);
                            }
                        } else if (numberOfSlots == 45) {
                            if (balance >= configCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_6)) {
                                backPackCfg.set(player.getUniqueId() + Constants.YML_SIZE, 54);
                                player.sendMessage(ChatColor.GREEN + Constants.YML_BACKPACK_LEVEL_IS_NOW + ChatColor.GOLD + "6" + ChatColor.GREEN + Constants.YML_OUT_OF + ChatColor.GOLD + " 6" + ChatColor.GREEN + Constants.YML_BACKPACK_NOW_HOLDS + ChatColor.GOLD + "54 " + ChatColor.GREEN + Constants.YML_ITEMS);
                                econ.withdrawPlayer(offlinePlayer, configCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_6));
                            } else {
                                player.sendMessage(ChatColor.RED + Constants.YML_YOU_NEED_AT_LEAST + ChatColor.GOLD + "$" + configCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_6) + ChatColor.RED + Constants.YML_TO_UPGRADE_YOUR_BACKPACK);
                            }
                        } else {
                            player.sendMessage(ChatColor.GREEN + "Congratulations!!! Your backpack is at max capacity!!!");
                        }
                    } else {
                        player.sendMessage(ChatColor.GREEN + "You need to purchase a backpack before you are able to upgrade.");
                    }
                } else if (args[0].equalsIgnoreCase(ServerCommands.BACKPACK_BUY_COMMAND)) {
                    double playerBalance = econ.getBalance(offlinePlayer);
                    if (backPackCfg.get(player.getUniqueId() + Constants.YML_SIZE) == null) {
                        if (playerBalance >= configCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_1)) {
                            backPackCfg.set(player.getUniqueId() + Constants.YML_SIZE, 9);
                            player.sendMessage(ChatColor.GREEN + "You just purchased a " + ChatColor.GOLD + "Level 1 Backpack" + ChatColor.GREEN + ". You can store " + ChatColor.GOLD + "9" + ChatColor.GREEN + " items.");
                            Api.takePlayerMoney(player.getUniqueId().toString(),configCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_1) );
                        } else {
                            player.sendMessage(ChatColor.RED + Constants.YML_YOU_NEED_AT_LEAST + ChatColor.GOLD + "$" + configCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_1) + ChatColor.RED + " to purchase a backpack.");
                        }
                    } else {
                        player.sendMessage(ChatColor.GREEN + "You already own a backpack.");
                    }
                } else if (args[0].equalsIgnoreCase(ServerCommands.OPEN_COMMAND)) {
                    try {
                        Objects.requireNonNull(player.getPlayer()).openInventory(Inventories.setBackpackInventoryWhenOpened(player, playerBPFile, backPackCfg.getInt(player.getUniqueId() + ".size"), invTitle));
                    } catch (NullPointerException | IOException ignored) {

                    }
                }
            }
            if (args.length == 2){
                if (args[0].equalsIgnoreCase("ticket")) {
                    if (args[1].equalsIgnoreCase("buy")) {
                        int slotTickets = backPackCfg.getInt(player.getUniqueId() + ".slotTickets");
                        double playerBalance = econ.getBalance(offlinePlayer);
                        if (playerBalance >= 35000) {
                            if (slotTickets == 9) {
                                player.sendMessage(ChatColor.RED + "You already have 9 slot tickets.");
                            } else {
                                slotTickets = slotTickets + 1;
                                backPackCfg.set(player.getUniqueId() + ".slotTickets", slotTickets);
                                Api.takePlayerMoney(player.getUniqueId().toString(), 35000);
                                player.sendMessage(ChatColor.GREEN + "You bought " + ChatColor.GOLD + 1 + ChatColor.GREEN + " slot ticket and now own " + ChatColor.GOLD + slotTickets + ChatColor.GREEN + " slot ticket(s).");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You need at least " + ChatColor.GOLD + "$35,000 " + ChatColor.RED + "to buy a backpack slot ticket.");
                        }
                    } else if (args[1].equalsIgnoreCase("check")) {
                        int slotTickets = backPackCfg.getInt(player.getUniqueId() + ".slotTickets");
                        player.sendMessage(ChatColor.GREEN + "You currently own " + ChatColor.GOLD + slotTickets + ChatColor.GREEN + " slot tickets.");
                    }
                }
//                else if (args[0].equalsIgnoreCase("player") && player.getName().equalsIgnoreCase("latch93") && args[1] != null){
//                    try {
//                        Player playerBPToOpen = Api.getPlayerFromOfflinePlayer(args[1]);
//                        Objects.requireNonNull(player.getPlayer()).openInventory(Inventories.setBackpackInventoryWhenOpened(playerBPToOpen, playerBPFile, backPackCfg.getInt(player.getUniqueId() + ".size"), invTitle, playerBPToOpen.getName()));
//                    } catch (IOException e) {
//                        Main.log.warning(e.getMessage());
//                    }
//                }
            }
            try {
                backPackCfg.save(playerBPFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
