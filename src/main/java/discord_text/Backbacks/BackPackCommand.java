package discord_text.Backbacks;

import discord_text.Constants;
import discord_text.Main;
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
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class BackPackCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = null;
        Economy econ = Main.getEconomy();
        if (sender instanceof Player){
            player = (Player) sender;
            File configFile = new File(Main.getPlugin(Main.class).getDataFolder(), "playerBackpack.yml");
            FileConfiguration configCfg = YamlConfiguration.loadConfiguration(configFile);
            OfflinePlayer offlinePlayer = null;
            for (OfflinePlayer olp : Bukkit.getWhitelistedPlayers()){
                if (player.getName().equalsIgnoreCase(olp.getName())){
                    offlinePlayer = olp;
                }
            }
            if (args.length == 1){
                if (args[0].equalsIgnoreCase(Constants.BACKPACK_UPGRADE_COMMAND)) {
                    if (configCfg.get(player.getUniqueId() + Constants.YML_SIZE) != null){
                        int numberOfSlots = configCfg.getInt(player.getUniqueId() + Constants.YML_SIZE);
                        double balance = econ.getBalance(offlinePlayer);
                        if (numberOfSlots == 9){
                            if (balance >= configCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_2)){
                                configCfg.set(player.getUniqueId() + Constants.YML_SIZE, 18);
                                player.sendMessage(ChatColor.GREEN + Constants.YML_BACKPACK_LEVEL_IS_NOW + ChatColor.GOLD + "2" + ChatColor.GREEN + Constants.YML_OUT_OF +ChatColor.GOLD + " 6" +ChatColor.GREEN + Constants.YML_BACKPACK_NOW_HOLDS + ChatColor.GOLD + "18 " + ChatColor.GREEN + Constants.YML_ITEMS);
                                econ.withdrawPlayer(offlinePlayer, configCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_2));
                            } else {
                                player.sendMessage(ChatColor.RED + Constants.YML_YOU_NEED_AT_LEAST + ChatColor.GOLD + "$" + configCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_2) + ChatColor.RED + Constants.YML_TO_UPGRADE_YOUR_BACKPACK);
                            }
                        } else if (numberOfSlots == 18){
                            if (balance >= configCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_3)) {
                                configCfg.set(player.getUniqueId() + Constants.YML_SIZE, 27);
                                player.sendMessage(ChatColor.GREEN + Constants.YML_BACKPACK_LEVEL_IS_NOW + ChatColor.GOLD + "3" + ChatColor.GREEN + Constants.YML_OUT_OF +ChatColor.GOLD + " 6" +ChatColor.GREEN + Constants.YML_BACKPACK_NOW_HOLDS + ChatColor.GOLD + "27 " + ChatColor.GREEN + Constants.YML_ITEMS);
                                econ.withdrawPlayer(offlinePlayer, configCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_3));
                            } else {
                                player.sendMessage(ChatColor.RED + Constants.YML_YOU_NEED_AT_LEAST + ChatColor.GOLD + "$" + configCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_3) + ChatColor.RED + Constants.YML_TO_UPGRADE_YOUR_BACKPACK);
                            }
                        } else if (numberOfSlots == 27){
                            if (balance >= configCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_4)) {
                                configCfg.set(player.getUniqueId() + Constants.YML_SIZE, 36);
                                player.sendMessage(ChatColor.GREEN + Constants.YML_BACKPACK_LEVEL_IS_NOW + ChatColor.GOLD + "4" + ChatColor.GREEN + Constants.YML_OUT_OF +ChatColor.GOLD + " 6" +ChatColor.GREEN + Constants.YML_BACKPACK_NOW_HOLDS + ChatColor.GOLD + "36 " + ChatColor.GREEN + Constants.YML_ITEMS);
                                econ.withdrawPlayer(offlinePlayer, configCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_4));
                            } else {
                                player.sendMessage(ChatColor.RED + Constants.YML_YOU_NEED_AT_LEAST + ChatColor.GOLD + "$" + configCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_4) + ChatColor.RED + Constants.YML_TO_UPGRADE_YOUR_BACKPACK);
                            }
                        } else if (numberOfSlots == 36){
                            if (balance >= configCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_5)) {
                                configCfg.set(player.getUniqueId() + Constants.YML_SIZE, 45);
                                player.sendMessage(ChatColor.GREEN + Constants.YML_BACKPACK_LEVEL_IS_NOW + ChatColor.GOLD + "5" + ChatColor.GREEN + Constants.YML_OUT_OF +ChatColor.GOLD + " 6" +ChatColor.GREEN + Constants.YML_BACKPACK_NOW_HOLDS + ChatColor.GOLD + "45 " + ChatColor.GREEN + Constants.YML_ITEMS);
                                econ.withdrawPlayer(offlinePlayer, configCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_5));
                            } else {
                                player.sendMessage(ChatColor.RED + Constants.YML_YOU_NEED_AT_LEAST + ChatColor.GOLD + "$" + configCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_5) + ChatColor.RED + Constants.YML_TO_UPGRADE_YOUR_BACKPACK);
                            }
                        } else if (numberOfSlots == 45){
                            if (balance >= configCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_6)){
                                configCfg.set(player.getUniqueId() + Constants.YML_SIZE, 54);
                                player.sendMessage(ChatColor.GREEN + Constants.YML_BACKPACK_LEVEL_IS_NOW + ChatColor.GOLD + "6" + ChatColor.GREEN + Constants.YML_OUT_OF +ChatColor.GOLD + " 6" +ChatColor.GREEN + Constants.YML_BACKPACK_NOW_HOLDS + ChatColor.GOLD + "54 " + ChatColor.GREEN + Constants.YML_ITEMS);
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
                }
                if (args[0].equalsIgnoreCase(Constants.BACKPACK_BUY_COMMAND)){
                    double playerBalance = econ.getBalance(offlinePlayer);
                    if (configCfg.get(player.getUniqueId() + Constants.YML_SIZE) == null){
                        if (playerBalance >= configCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_1)){
                            configCfg.set(player.getUniqueId() + Constants.YML_SIZE, 9);
                            player.sendMessage(ChatColor.GREEN + "You just purchased a " + ChatColor.GOLD + "Level 1 Backpack" + ChatColor.GREEN + ". You can store " + ChatColor.GOLD + "9" + ChatColor.GREEN + " items.");
                        } else {
                            player.sendMessage(ChatColor.RED + Constants.YML_YOU_NEED_AT_LEAST + ChatColor.GOLD + "$" + configCfg.getInt(Constants.YML_BACKPACK_LEVEL_COST_LEVEL_1) + ChatColor.RED + " to purchase a backpack.");
                        }
                    } else {
                        player.sendMessage(ChatColor.GREEN + "You already own a backpack.");
                    }
                }
                if (args[0].equalsIgnoreCase(Constants.OPEN_COMMAND)){
                    Inventory inv;
                    if (configCfg.get(player.getUniqueId() + Constants.YML_SIZE) != null) {
                        int numberOfSlots = configCfg.getInt(player.getUniqueId() + Constants.YML_SIZE);
                        inv = Bukkit.createInventory(null, numberOfSlots, player.getName() + "'s Backpack");
                        if ((configCfg.get(player.getUniqueId() + ".slots") != null)){
                            for(String users : configCfg.getConfigurationSection(player.getUniqueId() + ".slots").getKeys(false)) {
                                ItemStack is = new ItemStack(Material.valueOf(configCfg.getString(player.getUniqueId() + ".slots." + users + ".material")),  Integer.parseInt(configCfg.getString(player.getUniqueId() + ".slots." + users + ".amount")));
                                ItemMeta im = is.getItemMeta();
                                if (configCfg.isSet(player.getUniqueId() + ".slots." + users + ".enchants")) {
                                    for(String test : configCfg.getConfigurationSection(player.getUniqueId() + ".slots." + users + ".enchants").getKeys(false)) {
                                        assert im != null;
                                        if (configCfg.getString(player.getUniqueId() + ".slots." + users + ".enchants." + test + ".enchant") != null && configCfg.getInt(player.getUniqueId() + ".slots." + users + ".enchants." + test + ".level") != 0 ){
                                            im.addEnchant(Objects.requireNonNull(Enchantment.getByKey(NamespacedKey.minecraft(Objects.requireNonNull(configCfg.getString(player.getUniqueId() + ".slots." + users + ".enchants." + test + ".enchant"))))), configCfg.getInt(player.getUniqueId() + ".slots." + users + ".enchants." + test + ".level"), true);
                                        }
                                    }
                                    is.setItemMeta(im);
                                }
                                assert im != null;
                                if (configCfg.getString(player.getUniqueId() + ".slots." + users + ".displayName") != null ){
                                    im.setDisplayName(configCfg.getString(player.getUniqueId() + ".slots." + users + ".displayName"));
                                    is.setItemMeta(im);
                                }
                                inv.setItem(Integer.parseInt(users), is);
                            }
                        }
                        Objects.requireNonNull(player.getPlayer()).openInventory(inv);
                    } else {
                        player.sendMessage(ChatColor.RED + "You need to purchase a backpack before you use this command");
                    }
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
