package discord_text.PlayerShops;

import discord_text.Constants;
import discord_text.Main;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PlayerShopsCommand implements CommandExecutor {
    Inventory inv;
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = null;
        Economy econ = Main.getEconomy();
        if (sender instanceof Player) {
            player = (Player) sender;
            File configFile = new File(Main.getPlugin(Main.class).getDataFolder(), "playerShops.yml");
            FileConfiguration configCfg = YamlConfiguration.loadConfiguration(configFile);
            OfflinePlayer offlinePlayer = null;
            for (OfflinePlayer olp : Bukkit.getWhitelistedPlayers()) {
                if (player.getName().equalsIgnoreCase(olp.getName())) {
                    offlinePlayer = olp;
                }
            }
            UUID playerUUID = player.getUniqueId();
            String playerName = player.getName();
            if (args[0].equalsIgnoreCase(Constants.MY_SHOP_COMMAND)){
                inv = Bukkit.createInventory(null, 27, player.getName() + "'s Shop");
                configCfg.set("players." + playerName + ".UUID", playerUUID.toString());
                try {
                    configCfg.save(configFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (configCfg.isSet("players." + playerName + ".slots")){
                    for(String users : configCfg.getConfigurationSection("players." + playerName + ".slots").getKeys(false)) {
                        ItemStack is = new ItemStack(Material.valueOf(configCfg.getString("players." + playerName + ".slots." + users + ".material")),  Integer.parseInt(configCfg.getString("players." + playerName + ".slots." + users + ".amount")));
                        ItemMeta im = is.getItemMeta();
                        if (configCfg.isSet("players." + playerName + ".slots." + users + ".enchants")) {
                            for(String test : configCfg.getConfigurationSection("players." + playerName + ".slots." + users + ".enchants").getKeys(false)) {
                                assert im != null;
                                if (configCfg.getString("players." + playerName + ".slots." + users + ".enchants." + test + ".enchant") != null && configCfg.getInt("players." + playerName + ".slots." + users + ".enchants." + test + ".level") != 0 ){
                                    im.addEnchant(Objects.requireNonNull(Enchantment.getByKey(NamespacedKey.minecraft(Objects.requireNonNull(configCfg.getString("players." + playerName + ".slots." + users + ".enchants." + test + ".enchant"))))), configCfg.getInt("players." + playerName + ".slots." + users + ".enchants." + test + ".level"), true);
                                }
                            }
                            is.setItemMeta(im);
                        }
                        assert im != null;
                        if (is.getEnchantments().isEmpty()){
                            if (configCfg.isSet("players." + playerName + ".itemWorth." + is.getType())){
                                List<String> loreList = new ArrayList<>();
                                loreList.add(ChatColor.GREEN + "Cost per item " + ChatColor.GOLD + "$" + configCfg.getInt("players." + playerName + ".itemWorth." + is.getType()));
                                loreList.add(ChatColor.GREEN + "Left click to purchase " + ChatColor.GOLD + "1" + ChatColor.GREEN + " item.");
                                if (Integer.parseInt(Objects.requireNonNull(configCfg.getString("players." + playerName + ".slots." + users + ".amount"))) > 9){
                                    loreList.add(ChatColor.GREEN + "Right click to purchase " + ChatColor.GOLD + "10" + ChatColor.GREEN + " items.");
                                }
                                im.setLore(loreList);
                                is.setItemMeta(im);
                            }
                        } else {
                            if (configCfg.isSet("players." + playerName + ".itemWorth." + is.getType() + "-" + is.getEnchantments())){
                                List<String> loreList = new ArrayList<>();
                                loreList.add(ChatColor.GREEN + "Cost per item " + ChatColor.GOLD + "$" + configCfg.getInt("players." + playerName + ".itemWorth." + is.getType() + "-" + is.getEnchantments()));
                                loreList.add(ChatColor.GREEN + "Left click to purchase " + ChatColor.GOLD + "1" + ChatColor.GREEN + " item.");
                                if (Integer.parseInt(Objects.requireNonNull(configCfg.getString("players." + playerName + ".slots." + users + ".amount"))) > 9){
                                    loreList.add(ChatColor.GREEN + "Right click to purchase " + ChatColor.GOLD + "10" + ChatColor.GREEN + " items.");
                                }
                                im.setLore(loreList);
                                is.setItemMeta(im);
                            }
                        }
                        inv.setItem(Integer.parseInt(users), is);
                    }
                }

                player.openInventory(inv);
            }
            if (args[0].equalsIgnoreCase(Constants.SET_WORTH_COMMAND)){
                try {
                    int itemWorth = Integer.parseInt(args[1]);
                    if (player.getInventory().getItemInMainHand().getType().toString().contains("SHULKER_BOX") || player.getInventory().getItemInMainHand().getType().toString().contains("PLAYER_HEAD") ){
                        player.sendMessage(ChatColor.RED + "Unable to sell " + ChatColor.GOLD + player.getInventory().getItemInMainHand().getType().toString() + "'s" + ChatColor.RED + " in player shops");
                    } else {
                        if (player.getInventory().getItemInMainHand().getEnchantments().isEmpty()){
                            configCfg.set("players." + playerName + ".itemWorth." + player.getInventory().getItemInMainHand().getType(), itemWorth);
                        } else {
                            configCfg.set("players." + playerName + ".itemWorth." + player.getInventory().getItemInMainHand().getType() + "-" + player.getInventory().getItemInMainHand().getEnchantments(), itemWorth);
                        }
                        player.sendMessage(ChatColor.GREEN + "Set value of item to " + ChatColor.GOLD + "$" + itemWorth);
                    }
                } catch (NumberFormatException e){
                    player.sendMessage(ChatColor.RED + "Error: Set item worth like this -> /ps setworth 10");
                }

            }
            if (args[0].equalsIgnoreCase(Constants.OPEN_COMMAND)){
                try {
                    if (args[1] != null){
                        String playerShopToOpen = args[1];
                        inv = Bukkit.createInventory(null, 27, playerShopToOpen + "'s Shop");
                        if (configCfg.isSet("players." + playerShopToOpen)){
                            if (configCfg.isSet("players." + playerShopToOpen + ".slots")){
                                for(String users : configCfg.getConfigurationSection("players." + playerShopToOpen + ".slots").getKeys(false)) {
                                    ItemStack is = new ItemStack(Material.valueOf(configCfg.getString("players." + playerShopToOpen + ".slots." + users + ".material")),  Integer.parseInt(configCfg.getString("players." + playerShopToOpen + ".slots." + users + ".amount")));
                                    ItemMeta im = is.getItemMeta();
                                    if (configCfg.isSet("players." + playerShopToOpen + ".slots." + users + ".enchants")) {
                                        for(String test : configCfg.getConfigurationSection("players." + playerShopToOpen + ".slots." + users + ".enchants").getKeys(false)) {
                                            assert im != null;
                                            if (configCfg.getString("players." + playerShopToOpen + ".slots." + users + ".enchants." + test + ".enchant") != null && configCfg.getInt("players." + playerShopToOpen + ".slots." + users + ".enchants." + test + ".level") != 0 ){
                                                im.addEnchant(Objects.requireNonNull(Enchantment.getByKey(NamespacedKey.minecraft(Objects.requireNonNull(configCfg.getString("players." + playerShopToOpen + ".slots." + users + ".enchants." + test + ".enchant"))))), configCfg.getInt("players." + playerShopToOpen + ".slots." + users + ".enchants." + test + ".level"), true);
                                            }
                                        }
                                        is.setItemMeta(im);
                                    }
                                    assert im != null;
                                    if (configCfg.getString("players." + playerShopToOpen + ".slots." + users + ".displayName") != null ){
                                        im.setDisplayName(configCfg.getString("players." + playerShopToOpen + ".slots." + users + ".displayName"));
                                        is.setItemMeta(im);
                                    }
                                    if (is.getEnchantments().isEmpty()){
                                        if (configCfg.isSet("players." + playerShopToOpen + ".itemWorth." + is.getType())){
                                            List<String> loreList = new ArrayList<>();
                                            loreList.add(ChatColor.GREEN + "Cost per item " + ChatColor.GOLD + "$" + configCfg.getInt("players." + playerShopToOpen + ".itemWorth." + is.getType()));
                                            loreList.add(ChatColor.GREEN + "Left click to purchase " + ChatColor.GOLD + "1" + ChatColor.GREEN + " item.");
                                            if (Integer.parseInt(Objects.requireNonNull(configCfg.getString("players." + playerShopToOpen + ".slots." + users + ".amount"))) > 9){
                                                loreList.add(ChatColor.GREEN + "Right click to purchase " + ChatColor.GOLD + "10" + ChatColor.GREEN + " items. Total cost: " + ChatColor.GOLD + "$" + (10 * configCfg.getInt("players." + playerShopToOpen + ".itemWorth." + is.getType())));
                                            }
                                            loreList.add(ChatColor.GREEN + "Middle click to purchase all items. Total cost: " + ChatColor.GOLD +"$" + (Integer.parseInt(configCfg.getString("players." + playerShopToOpen + ".slots." + users + ".amount")) * configCfg.getInt("players." + playerShopToOpen + ".itemWorth." + is.getType())));
                                            im.setLore(loreList);
                                            is.setItemMeta(im);
                                        }
                                    } else {
                                        if (configCfg.isSet("players." + playerShopToOpen + ".itemWorth." + is.getType() + "-" + is.getEnchantments())){
                                            List<String> loreList = new ArrayList<>();
                                            loreList.add(ChatColor.GREEN + "Cost per item " + ChatColor.GOLD + "$" + configCfg.getInt("players." + playerShopToOpen + ".itemWorth." + is.getType() + "-" + is.getEnchantments()));
                                            loreList.add(ChatColor.GREEN + "Left click to purchase " + ChatColor.GOLD + "1" + ChatColor.GREEN + " item.");
                                            if (Integer.parseInt(Objects.requireNonNull(configCfg.getString("players." + playerShopToOpen + ".slots." + users + ".amount"))) > 9){
                                                loreList.add(ChatColor.GREEN + "Right click to purchase " + ChatColor.GOLD + "10" + ChatColor.GREEN + " items.");
                                            }
                                            im.setLore(loreList);
                                            is.setItemMeta(im);
                                        }
                                    }
                                    inv.setItem(Integer.parseInt(users), is);
                                }
                            }
                            player.openInventory(inv);
                        } else {
                            player.sendMessage(ChatColor.RED + "That player shop does not exist.");
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException ignored){

                }

            }
            try {
                configCfg.save(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}