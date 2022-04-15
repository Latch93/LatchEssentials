package discord.Backbacks;

import discord.Constants;
import discord.Main;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BackPacks {
    public static void saveCustomInventory(InventoryCloseEvent e, File configFile) throws IOException {
        FileConfiguration inventoryCfg = Main.getFileConfiguration(configFile);
        UUID playerUUID = e.getPlayer().getUniqueId();
        String playerName = e.getPlayer().getName();
        for (OfflinePlayer olp : Bukkit.getWhitelistedPlayers()){
            if (inventoryCfg.isSet(olp.getName() + Constants.YML_SIZE) && e.getView().getTitle().contains(olp.getName())){
                playerName = olp.getName();
            }
        }
        for (int i = 0; i < e.getInventory().getSize(); i++){
            if (e.getInventory().getItem(i) != null){
                ItemStack itemStack = e.getInventory().getItem(i);
                ItemMeta im =  Objects.requireNonNull(e.getInventory().getItem(i)).getItemMeta();
                if (im != null){
                    im.setLore(null);
                }
                inventoryCfg.set(playerName + Constants.YML_SLOTS + i, e.getInventory().getItem(i));
            } else {
                inventoryCfg.set(playerName + Constants.YML_SLOTS + i, null);
            }
        }
        if (inventoryCfg.get(playerName + Constants.YML_SIZE) == null){
            inventoryCfg.set(playerName + Constants.YML_SIZE, e.getInventory().getSize());
        } else {
            inventoryCfg.set(playerName + Constants.YML_SIZE, inventoryCfg.getInt(playerName + Constants.YML_SIZE));
        }
        inventoryCfg.set(playerName + ".name", playerName);
        inventoryCfg.save(configFile);
    }

    public static Inventory setInventoryWhenOpened(Player player, String fileName, int slots, String invTitle, String playerShopToOpen){
        FileConfiguration inventoryConfig = Main.loadConfig(fileName);
        Inventory inv = null;
        String playerName = "";
        if (playerShopToOpen == null || playerShopToOpen.equalsIgnoreCase(player.getName())){
            playerName = player.getName();
        } else {
            playerName = playerShopToOpen;
        }
        if (inventoryConfig.get(playerName + Constants.YML_SIZE) != null) {
            inv = Bukkit.createInventory(null, slots, invTitle);
            if ((inventoryConfig.get(playerName + ".slots") != null)){
                for(String users : inventoryConfig.getConfigurationSection(playerName + ".slots").getKeys(false)) {
                    ItemStack is = inventoryConfig.getItemStack(playerName + Constants.YML_SLOTS + users);
                    inv.setItem(Integer.parseInt(users), is);
                }
            }
        } else if (invTitle.toLowerCase().contains("backpack")){
            player.sendMessage(ChatColor.RED + "You need to purchase a backpack before you use this command");
        } else {
            inv = Bukkit.createInventory(player, 27, invTitle);
        }
        return inv;
    }

    public static Inventory setLoreInPlayerShop(String playerShopToOpen, Inventory inv, String playerName){
        FileConfiguration playerShopCfg = Main.loadConfig(Constants.YML_PLAYER_SHOP_FILE_NAME);
        for (int i = 0; i < inv.getSize(); i++){
            if (inv.getItem(i) != null){
                ItemStack itemStack = inv.getItem(i);
                assert itemStack != null;
                int itemStackAmount = itemStack.getAmount();
                itemStack.setAmount(1);
                int totalAmount = itemStack.getAmount();
                int itemWorth;
                ItemMeta im = Objects.requireNonNull(inv.getItem(i)).getItemMeta();
                if (im != null && !im.getDisplayName().isEmpty()){
                    itemWorth = playerShopCfg.getInt(playerShopToOpen + ".itemWorth." + Objects.requireNonNull(itemStack.getItemMeta()).getDisplayName());
                } else {
                    itemWorth = playerShopCfg.getInt(playerShopToOpen + ".itemWorth." + itemStack);
                }
                List<String> loreList = new ArrayList<>();
                itemStack.setAmount(1);
                itemStack.setAmount(itemStackAmount);
                int totalWorth = totalAmount * itemWorth;
                if (!playerShopToOpen.equalsIgnoreCase(playerName)){
                    loreList.add(ChatColor.GREEN + "Cost per item " + ChatColor.GOLD + "$" + itemWorth);
                    loreList.add(ChatColor.GREEN + "Left click to purchase " + ChatColor.GOLD + "1" + ChatColor.GREEN + " item.");
                    if (totalAmount > 9) {
                        loreList.add(ChatColor.GREEN + "Right click to purchase " + ChatColor.GOLD + "10" + ChatColor.GREEN + " items. Total cost: " + ChatColor.GOLD + "$" + Math.multiplyExact(10,itemWorth));
                    }
                    loreList.add(ChatColor.GREEN + "Middle click to purchase all items. Total cost: " + ChatColor.GOLD + "$" + totalWorth);
                } else {
                    loreList.add(ChatColor.GREEN + "Cost per item: " + ChatColor.GOLD + "$" + itemWorth );
                }
                assert im != null;
                im.setLore(loreList);
                itemStack.setItemMeta(im);
            }
        }
        return inv;
    }

    public static int getItemWorth(ItemStack itemStack, String playerShopToOpen){
        FileConfiguration playerShopCfg = Main.loadConfig(Constants.YML_PLAYER_SHOP_FILE_NAME);
        int itemWorth;
        ItemMeta im = Objects.requireNonNull(itemStack.getItemMeta());
        im.setLore(null);
        itemStack.setItemMeta(im);
        if (!im.getDisplayName().isEmpty()){
            itemWorth = playerShopCfg.getInt(playerShopToOpen + ".itemWorth." + Objects.requireNonNull(itemStack.getItemMeta()).getDisplayName());
        } else {
            itemWorth = playerShopCfg.getInt(playerShopToOpen + ".itemWorth." + itemStack);
        }
        return itemWorth;
    }

}
