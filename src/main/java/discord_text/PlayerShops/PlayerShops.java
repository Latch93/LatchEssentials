package discord_text.PlayerShops;

import discord_text.Constants;
import discord_text.Main;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PlayerShops {
    public static void savePlayerShop(InventoryCloseEvent e) throws IOException {
        File configFile = new File(Main.getPlugin(Main.class).getDataFolder(), "playerShops.yml");
        FileConfiguration configCfg = YamlConfiguration.loadConfiguration(configFile);
        UUID playerUUID = e.getPlayer().getUniqueId();
        String[] arr = e.getView().getTitle().split(Constants.YML_POSSESSIVE_PLAYER_SHOP);
        String playerName = arr[0];
        for (int i = 0; i < e.getInventory().getSize(); i++){
            if (e.getInventory().getItem(i) != null){
                String itemName = Objects.requireNonNull(e.getInventory().getItem(i)).getType().toString();
                String itemAmount = String.valueOf(Objects.requireNonNull(e.getInventory().getItem(i)).getAmount());
                configCfg.set("players." + playerName + Constants.YML_SLOTS + i + ".material", itemName);
                configCfg.set("players." + playerName + Constants.YML_SLOTS + i + ".amount", itemAmount);
                ItemMeta im = Objects.requireNonNull(e.getInventory().getItem(i)).getItemMeta();
                assert im != null;
                configCfg.set("players." + playerName + Constants.YML_SLOTS + i + ".displayName", im.getDisplayName());
                Map<Enchantment, Integer> enchants = Objects.requireNonNull(e.getInventory().getItem(i)).getEnchantments();
                Iterator it = enchants.entrySet().iterator();
                int count = 0;
                while (it.hasNext()) {
                    // get the pair
                    Map.Entry pair = (Map.Entry)it.next();
                    // using WordUtils.capitalize to produce a nice output like "Durability" instead of "DURABILITY"
                    // the pair's key would be the Enchantment object and the value would be the level in the map.
                    // you can probably use some util online if you wanna convert that int to a roman number
                    Enchantment enchantment = (Enchantment)  pair.getKey();
                    configCfg.set("players." + playerName + Constants.YML_SLOTS + i + ".enchants." +count + ".enchant", enchantment.getKey().getKey());
                    configCfg.set("players." + playerName + Constants.YML_SLOTS + i + ".enchants." +count + ".level", pair.getValue());
                    count++;
                }
            } else {
                configCfg.set("players." + playerName + Constants.YML_SLOTS + i, null);
            }
        }
        configCfg.save(configFile);
    }

    public static Inventory updateInventory(Inventory inv, Player player) throws IOException {
        File configFile = new File(Main.getPlugin(Main.class).getDataFolder(), "playerShops.yml");
        FileConfiguration configCfg = YamlConfiguration.loadConfiguration(configFile);
        String playerName = player.getName();
        for (int i = 0; i < inv.getSize(); i++){
            if (inv.getItem(i) != null){
                String itemName = Objects.requireNonNull(inv.getItem(i)).getType().toString();
                String itemAmount = String.valueOf(Objects.requireNonNull(inv.getItem(i)).getAmount());
                configCfg.set("players." + playerName + Constants.YML_SLOTS + i + ".material", itemName);
                configCfg.set("players." + playerName + Constants.YML_SLOTS + i + ".amount", itemAmount);
                ItemMeta im = Objects.requireNonNull(inv.getItem(i)).getItemMeta();
                assert im != null;
                configCfg.set("players." + playerName + Constants.YML_SLOTS + i + ".displayName", im.getDisplayName());
                Map<Enchantment, Integer> enchants = Objects.requireNonNull(inv.getItem(i)).getEnchantments();
                Iterator it = enchants.entrySet().iterator();
                int count = 0;
                while (it.hasNext()) {
                    // get the pair
                    Map.Entry pair = (Map.Entry)it.next();
                    // using WordUtils.capitalize to produce a nice output like "Durability" instead of "DURABILITY"
                    // the pair's key would be the Enchantment object and the value would be the level in the map.
                    // you can probably use some util online if you wanna convert that int to a roman number
                    Enchantment enchantment = (Enchantment)  pair.getKey();
                    configCfg.set("players." + playerName + Constants.YML_SLOTS + i + ".enchants." +count + ".enchant", enchantment.getKey().getKey());
                    configCfg.set("players." + playerName + Constants.YML_SLOTS + i + ".enchants." +count + ".level", pair.getValue());
                    count++;
                }
            } else {
                configCfg.set("players." + playerName + Constants.YML_SLOTS + i, null);
            }
        }
        configCfg.save(configFile);
        return inv;
    }

    public static void illegalPlayerShopItems(InventoryClickEvent e, Player player){
        if (e.getCurrentItem().getType().toString().contains("SHULKER_BOX") || e.getCurrentItem().getType().toString().contains("PLAYER_HEAD") ){
            e.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Unable to sell " + ChatColor.GOLD + e.getCurrentItem().getType() + "'s" + ChatColor.RED + " in player shops at this time");
        } else if (!e.getCurrentItem().getEnchantments().isEmpty()) {
            e.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Unable to sell enchanted items in player shops at this time.");
        }
    }
    public static void itemWorthNotSet(InventoryClickEvent e, Player player, FileConfiguration playerShopCfg){
        if (!playerShopCfg.isSet("players." + player.getName() + ".itemWorth." + Objects.requireNonNull(e.getCurrentItem()).getType()) && !playerShopCfg.isSet("players." + player.getName() + ".itemWorth." + Objects.requireNonNull(e.getCurrentItem()).getType() + "-" + e.getCurrentItem().getEnchantments())) {
            e.setCancelled(true);
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "You need to set this item's worth with " + ChatColor.AQUA + "/ps setworth [amount]" + ChatColor.RED + " before you can add it to your shop.");
        }
    }

    public static void sd(InventoryClickEvent e, Economy econ, Player player) throws IOException {
        OfflinePlayer offlineBuyer = null;
        OfflinePlayer offlineSeller = null;
        double buyerBalance = 0;
        if (Objects.requireNonNull(e.getClickedInventory()).getSize() == 27) {
            String[] arr = e.getView().getTitle().split(Constants.YML_POSSESSIVE_PLAYER_SHOP);
            String sellerShopPlayerName = arr[0];
            for (OfflinePlayer olp : Bukkit.getWhitelistedPlayers()) {
                Player temp = (Player) e.getWhoClicked();
                if (temp.getName().equalsIgnoreCase(olp.getName())) {
                    offlineBuyer = olp;
                }
                if (sellerShopPlayerName.equalsIgnoreCase(olp.getName())) {
                    offlineSeller = olp;
                }
            }
            buyerBalance = econ.getBalance(offlineBuyer);
            File configFile = new File(Main.getPlugin(Main.class).getDataFolder(), "playerShops.yml");
            FileConfiguration configCfg = YamlConfiguration.loadConfiguration(configFile);
            int itemCost = 0;
            if (configCfg.isSet("players." + offlineSeller.getName() + ".itemWorth." + e.getCurrentItem().getType() + "-" + e.getCurrentItem().getEnchantments())){
                itemCost = configCfg.getInt("players." + offlineSeller.getName() + ".itemWorth." + e.getCurrentItem().getType() + "-" + e.getCurrentItem().getEnchantments());
            } else if (configCfg.isSet("players." + offlineSeller.getName() + ".itemWorth." + e.getCurrentItem().getType())){
                itemCost = configCfg.getInt("players." + offlineSeller.getName() + ".itemWorth." + e.getCurrentItem().getType());
            }
            int totalAmount = e.getCurrentItem().getAmount();
            if (e.getClick().toString().equalsIgnoreCase("LEFT")){
                if (buyerBalance < itemCost){
                    player.sendMessage(ChatColor.RED + "Not enough money to buy this item.");
                } else {
                    player.getInventory().addItem(new ItemStack(e.getCurrentItem().getType(), 1));
                    e.getClickedInventory().setItem(e.getSlot(), new ItemStack(e.getCurrentItem().getType(), e.getCurrentItem().getAmount() - 1));
                    econ.withdrawPlayer(offlineBuyer, itemCost);
                    econ.depositPlayer(offlineSeller, itemCost);
                    player.updateInventory();
                }
                e.setCancelled(true);
            } else if (e.getClick().toString().equalsIgnoreCase("RIGHT")){
                if (e.getCurrentItem().getAmount() >= 10) {
                    int itemTotalCost = itemCost * 10;
                    if (buyerBalance < itemTotalCost){
                        player.sendMessage(ChatColor.RED + "Not enough money to buy these 10 items.");
                    } else {
                        player.getInventory().addItem(new ItemStack(e.getCurrentItem().getType(), 10));
                        e.getClickedInventory().setItem(e.getSlot(), new ItemStack(e.getCurrentItem().getType(), e.getCurrentItem().getAmount() - 10));
                        econ.withdrawPlayer(offlineBuyer, itemTotalCost);
                        econ.depositPlayer(offlineSeller, itemTotalCost);
                        player.updateInventory();
                    }
                }
                e.setCancelled(true);
            } else {
                if (buyerBalance < (totalAmount * itemCost)){
                    player.sendMessage(ChatColor.RED + "Not enough money to buy whole item stack.");
                } else {
                    int itemTotalCost = itemCost * totalAmount;
                    player.getInventory().addItem(new ItemStack(e.getCurrentItem().getType(), totalAmount));
                    e.getClickedInventory().setItem(e.getSlot(), new ItemStack(e.getCurrentItem().getType(), e.getCurrentItem().getAmount() - totalAmount));
                    econ.withdrawPlayer(offlineBuyer, itemTotalCost);
                    econ.depositPlayer(offlineSeller, itemTotalCost);
                    player.updateInventory();
                }
                e.setCancelled(true);
            }
        } else {
            e.setCancelled(true);
        }
    }
}
