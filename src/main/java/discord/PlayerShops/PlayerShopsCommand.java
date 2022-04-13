package discord.PlayerShops;

import discord.Backbacks.BackPacks;
import discord.Constants;
import discord.Main;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.*;

public class PlayerShopsCommand implements CommandExecutor {
    Inventory inv;
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Economy econ = Main.getEconomy();
        if (sender instanceof Player) {
            try {
                FileConfiguration playerShopCfg = Main.loadConfig(Constants.YML_PLAYER_SHOP_FILE_NAME);
                Player player = (Player) sender;
                String invTitle = player.getName() + "'s Shop";
                int invSize = 27;
                OfflinePlayer offlinePlayer = null;
                for (OfflinePlayer olp : Bukkit.getWhitelistedPlayers()) {
                    if (player.getName().equalsIgnoreCase(olp.getName())) {
                        offlinePlayer = olp;
                    }
                }
                UUID playerUUID = player.getUniqueId();
                String playerName = player.getName();
                if (args[0].equalsIgnoreCase(Constants.MY_SHOP_COMMAND)) {
                    BackPacks.setInventoryWhenOpened(player, Main.playerShopFile, invSize, invTitle);
//                    inv = Bukkit.createInventory(null, 27, player.getName() + "'s Shop");
//                    playerShopCfg.set(Constants.YML_PLAYERS + playerName + ".UUID", playerUUID.toString());
//                    try {
//                        playerShopCfg.save(Main.playerShopFile);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    if (playerShopCfg.isSet(Constants.YML_PLAYERS + playerName + ".slots")) {
//                        for (String users : playerShopCfg.getConfigurationSection(Constants.YML_PLAYERS + playerName + ".slots").getKeys(false)) {
//                            ItemStack is = new ItemStack(Material.valueOf(playerShopCfg.getString(Constants.YML_PLAYERS + playerName + Constants.YML_SLOTS + users + ".material")), Integer.parseInt(playerShopCfg.getString(Constants.YML_PLAYERS + playerName + Constants.YML_SLOTS + users + ".amount")));
//                            ItemMeta im = is.getItemMeta();
//                            if (playerShopCfg.isSet(Constants.YML_PLAYERS + playerName + Constants.YML_SLOTS + users + ".enchants")) {
//                                for (String test : playerShopCfg.getConfigurationSection(Constants.YML_PLAYERS + playerName + Constants.YML_SLOTS + users + ".enchants").getKeys(false)) {
//                                    assert im != null;
//                                    if (playerShopCfg.getString(Constants.YML_PLAYERS + playerName + Constants.YML_SLOTS + users + Constants.YML_ENCHANTS + test + ".enchant") != null && playerShopCfg.getInt(Constants.YML_PLAYERS + playerName + Constants.YML_SLOTS + users + Constants.YML_ENCHANTS + test + ".level") != 0) {
//                                        im.addEnchant(Objects.requireNonNull(Enchantment.getByKey(NamespacedKey.minecraft(Objects.requireNonNull(playerShopCfg.getString(Constants.YML_PLAYERS + playerName + Constants.YML_SLOTS + users + Constants.YML_ENCHANTS + test + ".enchant"))))), playerShopCfg.getInt(Constants.YML_PLAYERS + playerName + Constants.YML_SLOTS + users + Constants.YML_ENCHANTS + test + ".level"), true);
//                                    }
//                                }
//                                is.setItemMeta(im);
//                            }
//                            assert im != null;
//                            if (is.getEnchantments().isEmpty()) {
//                                if (playerShopCfg.isSet(Constants.YML_PLAYERS + playerName + ".itemWorth." + is.getType())) {
//                                    List<String> loreList = new ArrayList<>();
//                                    loreList.add(ChatColor.GREEN + "Cost per item " + ChatColor.GOLD + "$" + playerShopCfg.getInt(Constants.YML_PLAYERS + playerName + ".itemWorth." + is.getType()));
//                                    loreList.add(ChatColor.GREEN + "Left click to purchase " + ChatColor.GOLD + "1" + ChatColor.GREEN + " item.");
//                                    if (Integer.parseInt(Objects.requireNonNull(playerShopCfg.getString(Constants.YML_PLAYERS + playerName + Constants.YML_SLOTS + users + ".amount"))) > 9) {
//                                        loreList.add(ChatColor.GREEN + "Right click to purchase " + ChatColor.GOLD + "10" + ChatColor.GREEN + " items.");
//                                    }
//                                    im.setLore(loreList);
//                                    is.setItemMeta(im);
//                                }
//                            } else {
//                                if (playerShopCfg.isSet(Constants.YML_PLAYERS + playerName + ".itemWorth." + is.getType() + "-" + is.getEnchantments())) {
//                                    List<String> loreList = new ArrayList<>();
//                                    loreList.add(ChatColor.GREEN + "Cost per item " + ChatColor.GOLD + "$" + playerShopCfg.getInt(Constants.YML_PLAYERS + playerName + ".itemWorth." + is.getType() + "-" + is.getEnchantments()));
//                                    loreList.add(ChatColor.GREEN + "Left click to purchase " + ChatColor.GOLD + "1" + ChatColor.GREEN + " item.");
//                                    if (Integer.parseInt(Objects.requireNonNull(playerShopCfg.getString(Constants.YML_PLAYERS + playerName + Constants.YML_SLOTS + users + ".amount"))) > 9) {
//                                        loreList.add(ChatColor.GREEN + "Right click to purchase " + ChatColor.GOLD + "10" + ChatColor.GREEN + " items.");
//                                    }
//                                    im.setLore(loreList);
//                                    is.setItemMeta(im);
//                                }
//                            }
//                            inv.setItem(Integer.parseInt(users), is);
//                        }
//                    }
//
//                    player.openInventory(inv);
                }
                if (args[0].equalsIgnoreCase(Constants.SET_WORTH_COMMAND)) {
                    try {
                        int itemWorth = Integer.parseInt(args[1]);
                        if (player.getInventory().getItemInMainHand().getType().toString().contains("SHULKER_BOX") || player.getInventory().getItemInMainHand().getType().toString().contains("PLAYER_HEAD")) {
                            player.sendMessage(ChatColor.RED + "Unable to sell " + ChatColor.GOLD + player.getInventory().getItemInMainHand().getType().toString() + "'s" + ChatColor.RED + " in player shops");
                        } else {
//                            String material = player.getInventory().getItemInMainHand().getType().toString();
                            playerShopCfg.set(player.getUniqueId() + ".itemWorth." + player.getInventory().getItemInMainHand(), itemWorth);
//                            if (player.getInventory().getItemInMainHand().getEnchantments().isEmpty() && !material.equalsIgnoreCase("enchanted_book")) {
//                                playerShopCfg.set(Constants.YML_PLAYERS + playerName + ".itemWorth." + material, itemWorth);
//                            } else if (material.equalsIgnoreCase("enchanted_book")) {
//                                playerShopCfg.set(Constants.YML_PLAYERS + playerName + ".itemWorth." + player.getInventory().getItemInMainHand(), itemWorth);
//                            }
//                            else {
//                                playerShopCfg.set(Constants.YML_PLAYERS + playerName + ".itemWorth." + material + PlayerShops.getStringBuilder(player.getInventory().getItemInMainHand()), itemWorth);
//                            }
                            player.sendMessage(ChatColor.GREEN + "Set value of item to " + ChatColor.GOLD + "$" + itemWorth);
                        }
                        playerShopCfg.save(Main.playerShopFile);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "Error: Set item worth like this -> /ps setworth 10");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                if (args[0].equalsIgnoreCase(Constants.OPEN_COMMAND)) {
                    BackPacks.setInventoryWhenOpened(player, Main.playerShopFile, invSize, invTitle);
//                    try {
//                        if (args[1] != null) {
//                            String playerShopToOpen = args[1];
//                            inv = Bukkit.createInventory(null, 27, playerShopToOpen + "'s Shop");
//                            if (playerShopCfg.isSet(Constants.YML_PLAYERS + playerShopToOpen) && playerShopCfg.isSet(Constants.YML_PLAYERS + playerShopToOpen + ".slots")) {
//                                for (String slotNumber : playerShopCfg.getConfigurationSection(Constants.YML_PLAYERS + playerShopToOpen + ".slots").getKeys(false)) {
//                                    ItemStack is = new ItemStack(Material.valueOf(playerShopCfg.getString(Constants.YML_PLAYERS + playerShopToOpen + Constants.YML_SLOTS + slotNumber + ".material")), Integer.parseInt(playerShopCfg.getString(Constants.YML_PLAYERS + playerShopToOpen + Constants.YML_SLOTS + slotNumber + ".amount")));
//                                    ItemMeta im = is.getItemMeta();
//                                    if (playerShopCfg.isSet(Constants.YML_PLAYERS + playerShopToOpen + Constants.YML_SLOTS + slotNumber + ".enchants")) {
//                                        for (String test : playerShopCfg.getConfigurationSection(Constants.YML_PLAYERS + playerShopToOpen + Constants.YML_SLOTS + slotNumber + ".enchants").getKeys(false)) {
//                                            assert im != null;
//                                            if (playerShopCfg.getString(Constants.YML_PLAYERS + playerShopToOpen + Constants.YML_SLOTS + slotNumber + Constants.YML_ENCHANTS + test + ".enchant") != null && playerShopCfg.getInt(Constants.YML_PLAYERS + playerShopToOpen + Constants.YML_SLOTS + slotNumber + Constants.YML_ENCHANTS + test + ".level") != 0) {
//                                                im.addEnchant(Objects.requireNonNull(Enchantment.getByKey(NamespacedKey.minecraft(Objects.requireNonNull(playerShopCfg.getString(Constants.YML_PLAYERS + playerShopToOpen + Constants.YML_SLOTS + slotNumber + Constants.YML_ENCHANTS + test + ".enchant"))))), playerShopCfg.getInt(Constants.YML_PLAYERS + playerShopToOpen + Constants.YML_SLOTS + slotNumber + Constants.YML_ENCHANTS + test + ".level"), true);
//                                            }
//                                        }
//                                        is.setItemMeta(im);
//                                    }
//                                    assert im != null;
//                                    if (playerShopCfg.getString(Constants.YML_PLAYERS + playerShopToOpen + Constants.YML_SLOTS + slotNumber + ".displayName") != null) {
//                                        im.setDisplayName(playerShopCfg.getString(Constants.YML_PLAYERS + playerShopToOpen + Constants.YML_SLOTS + slotNumber + ".displayName"));
//                                        is.setItemMeta(im);
//                                    }
//                                    if (is.getEnchantments().isEmpty()) {
//                                        if (playerShopCfg.isSet(Constants.YML_PLAYERS + playerShopToOpen + ".itemWorth." + is.getType())) {
//                                            List<String> loreList = new ArrayList<>();
//                                            loreList.add(ChatColor.GREEN + "Cost per item " + ChatColor.GOLD + "$" + playerShopCfg.getInt(Constants.YML_PLAYERS + playerShopToOpen + ".itemWorth." + is.getType()));
//                                            loreList.add(ChatColor.GREEN + "Left click to purchase " + ChatColor.GOLD + "1" + ChatColor.GREEN + " item.");
//                                            if (Integer.parseInt(Objects.requireNonNull(playerShopCfg.getString(Constants.YML_PLAYERS + playerShopToOpen + Constants.YML_SLOTS + slotNumber + ".amount"))) > 9) {
//                                                loreList.add(ChatColor.GREEN + "Right click to purchase " + ChatColor.GOLD + "10" + ChatColor.GREEN + " items. Total cost: " + ChatColor.GOLD + "$" + (10 * playerShopCfg.getInt(Constants.YML_PLAYERS + playerShopToOpen + ".itemWorth." + is.getType())));
//                                            }
//                                            loreList.add(ChatColor.GREEN + "Middle click to purchase all items. Total cost: " + ChatColor.GOLD + "$" + (Integer.parseInt(playerShopCfg.getString(Constants.YML_PLAYERS + playerShopToOpen + Constants.YML_SLOTS + slotNumber + ".amount")) * playerShopCfg.getInt(Constants.YML_PLAYERS + playerShopToOpen + ".itemWorth." + is.getType())));
//                                            im.setLore(loreList);
//                                            is.setItemMeta(im);
//                                        }
//                                    } else {
//                                        if (playerShopCfg.isSet(Constants.YML_PLAYERS + playerShopToOpen + ".itemWorth." + is.getType() + PlayerShops.getStringBuilder(is))) {
//                                            List<String> loreList = new ArrayList<>();
//                                            loreList.add(ChatColor.GREEN + "Cost per item " + ChatColor.GOLD + "$" + playerShopCfg.getInt(Constants.YML_PLAYERS + playerShopToOpen + ".itemWorth." + is.getType() + PlayerShops.getStringBuilder(is)));
//                                            loreList.add(ChatColor.GREEN + "Left click to purchase " + ChatColor.GOLD + "1" + ChatColor.GREEN + " item.");
//                                            if (Integer.parseInt(Objects.requireNonNull(playerShopCfg.getString(Constants.YML_PLAYERS + playerShopToOpen + Constants.YML_SLOTS + slotNumber + ".amount"))) > 9) {
//                                                loreList.add(ChatColor.GREEN + "Right click to purchase " + ChatColor.GOLD + "10" + ChatColor.GREEN + " items.");
//                                            }
//                                            im.setLore(loreList);
//                                            is.setItemMeta(im);
//                                        }
//                                    }
//                                    inv.setItem(Integer.parseInt(slotNumber), is);
//                                }
//                                player.openInventory(inv);
//                            } else {
//                                player.sendMessage(ChatColor.RED + "That player shop does not exist.");
//                            }
//                        }
//                        try {
//                            playerShopCfg.save(Main.playerShopFile);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//
//                    } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException ignored) {
//
//                    }

                }
            } catch (IllegalArgumentException ignored) {
            }
        }
        return false;
    }
}