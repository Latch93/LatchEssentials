package discord.PlayerShops;

import discord.Constants;
import discord.Main;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

public class PlayerShops {
    public static void savePlayerShop(InventoryCloseEvent e) throws IOException {
        UUID playerUUID = e.getPlayer().getUniqueId();
        String[] arr = e.getView().getTitle().split(Constants.YML_POSSESSIVE_PLAYER_SHOP);
        String playerName = arr[0];
        FileConfiguration playerShopCfg = Main.getFileConfiguration(Main.playerShopFile);
        for (int i = 0; i < e.getInventory().getSize(); i++){
            if (e.getInventory().getItem(i) != null){
                String itemName = Objects.requireNonNull(e.getInventory().getItem(i)).getType().toString();
                int itemAmount = Objects.requireNonNull(e.getInventory().getItem(i)).getAmount();
                playerShopCfg.set(Constants.YML_PLAYERS + playerName + Constants.YML_SLOTS + i + ".material", itemName);
                playerShopCfg.set(Constants.YML_PLAYERS + playerName + Constants.YML_SLOTS + i + ".amount", itemAmount);
                ItemMeta im = Objects.requireNonNull(e.getInventory().getItem(i)).getItemMeta();
                assert im != null;
                playerShopCfg.set(Constants.YML_PLAYERS + playerName + Constants.YML_SLOTS + i + ".displayName", im.getDisplayName());
                Map<Enchantment, Integer> enchants;
                if (itemName.equalsIgnoreCase("ENCHANTED_BOOK")){
                    EnchantmentStorageMeta meta =(EnchantmentStorageMeta) e.getInventory().getItem(i).getItemMeta();
                    enchants = meta.getStoredEnchants();
                    Iterator it = enchants.entrySet().iterator();
                    int count = 0;
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry)it.next();
                        String holder = pair.getKey().toString().replace("Enchantment[minecraft:", "");
                        holder = holder.replace(" ", "");
                        holder = holder.replace("]", "");
                        String[] arrd = holder.split(",");
                        playerShopCfg.set(Constants.YML_PLAYERS + playerName + Constants.YML_SLOTS + i + Constants.YML_ENCHANTS +count + ".enchant", arrd[0]);
                        playerShopCfg.set(Constants.YML_PLAYERS + playerName + Constants.YML_SLOTS + i + Constants.YML_ENCHANTS +count + ".level", pair.getValue());
                        count++;
                    }

                } else {
                    enchants = Objects.requireNonNull(e.getInventory().getItem(i)).getEnchantments();
                    Iterator it = enchants.entrySet().iterator();
                    int count = 0;
                    while (it.hasNext()) {
                        // get the pair
                        Map.Entry pair = (Map.Entry)it.next();
                        // using WordUtils.capitalize to produce a nice output like "Durability" instead of "DURABILITY"
                        // the pair's key would be the Enchantment object and the value would be the level in the map.
                        // you can probably use some util online if you wanna convert that int to a roman number
                        Enchantment enchantment = (Enchantment)  pair.getKey();
                        playerShopCfg.set(playerUUID + Constants.YML_SLOTS + i + Constants.YML_ENCHANTS +count + ".enchant", enchantment.getKey().getKey());
                        playerShopCfg.set(playerUUID + Constants.YML_SLOTS + i + Constants.YML_ENCHANTS +count + ".level", pair.getValue());
                        count++;
                    }
                }
            } else {
                playerShopCfg.set(Constants.YML_PLAYERS + playerName + Constants.YML_SLOTS + i, null);
            }
        }
        playerShopCfg.save(Main.playerShopFile);
    }

//    public static ArrayList<String> getEnchantedMaterial(String searchedMaterial){
//        String material = "";
//        ArrayList<String> playerList = new ArrayList<>();
//        playerList.addAll(playerShopCfg.getConfigurationSection(Constants.YML_PLAYERS).getKeys(false));
//        int count1 = 0;
//        ArrayList<String> enchantStringArr = new ArrayList<>();
//
//        for (String enchantedMaterial : playerShopCfg.getConfigurationSection(Constants.YML_PLAYERS + playerList.get(count1) + ".itemWorth").getKeys(false)) {
//            if (enchantedMaterial.contains("Enchant")){
//                String[] holder = enchantedMaterial.split("-");
//                if (searchedMaterial.equalsIgnoreCase(holder[0])){
//                    String[] split = new String[holder.length - 1];
//                    for (int i = 1; i <= holder.length; i++){
//                        split = holder[i].split("\\|");
//                    }
//                    String[] yeet = new String[split.length];
//                    for (int j = 0; j < split.length; j++){
//                        yeet = split[j].split(":");
//                        enchantStringArr.add(yeet[j])
//                    }
//                }
//            }
//            count1++;
//        }
//        return enchantStringArr;
//    }
    public static Inventory updateInventory(Inventory inv, Player player) throws IOException {
        String playerName = player.getName();
        FileConfiguration playerShopCfg = Main.getFileConfiguration(Main.playerShopFile);
        for (int i = 0; i < inv.getSize(); i++){
            if (inv.getItem(i) != null){
                String itemName = Objects.requireNonNull(inv.getItem(i)).getType().toString();
                String itemAmount = String.valueOf(Objects.requireNonNull(inv.getItem(i)).getAmount());
                playerShopCfg.set(Constants.YML_PLAYERS + playerName + Constants.YML_SLOTS + i + ".material", itemName);
                playerShopCfg.set(Constants.YML_PLAYERS + playerName + Constants.YML_SLOTS + i + ".amount", itemAmount);
                ItemMeta im = Objects.requireNonNull(inv.getItem(i)).getItemMeta();
                assert im != null;
                playerShopCfg.set(Constants.YML_PLAYERS + playerName + Constants.YML_SLOTS + i + ".displayName", im.getDisplayName());
                Map<Enchantment, Integer> enchants = Objects.requireNonNull(inv.getItem(i)).getEnchantments();
                Iterator it = enchants.entrySet().iterator();
                int count = 0;
                while (it.hasNext()) {
                    // get the pair
                    Map.Entry pair = (Map.Entry)it.next();
                    Enchantment enchantment = (Enchantment)  pair.getKey();
                    playerShopCfg.set(Constants.YML_PLAYERS + playerName + Constants.YML_SLOTS + i + Constants.YML_ENCHANTS +count + ".enchant", enchantment.getKey().getKey());
                    playerShopCfg.set(Constants.YML_PLAYERS + playerName + Constants.YML_SLOTS + i + Constants.YML_ENCHANTS +count + ".level", pair.getValue());
                    count++;
                }
            } else {
                playerShopCfg.set(Constants.YML_PLAYERS + playerName + Constants.YML_SLOTS + i, null);
            }
        }
        playerShopCfg.save(Main.playerShopFile);
        return inv;
    }

    public static void illegalPlayerShopItems(InventoryClickEvent e, Player player){
        if (e.getCurrentItem().getType().toString().contains("SHULKER_BOX") || e.getCurrentItem().getType().toString().contains("PLAYER_HEAD") ){
            e.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Unable to sell " + ChatColor.GOLD + e.getCurrentItem().getType() + "'s" + ChatColor.RED + " in player shops at this time");
        }
    }
    public static void itemWorthNotSet(InventoryClickEvent e, Player player, FileConfiguration playerShopCfg){
        String material = e.getCurrentItem().getType().toString();
        System.out.println("---- " + Constants.YML_PLAYERS + player.getName() + ".itemWorth." + material + getStringBuilder(e.getCurrentItem()));
        if (!playerShopCfg.isSet(Constants.YML_PLAYERS + player.getName() + ".itemWorth." + Objects.requireNonNull(e.getCurrentItem()).getType()) && !playerShopCfg.isSet(Constants.YML_PLAYERS + player.getName() + ".itemWorth." + material + getStringBuilder(e.getCurrentItem()))) {
            e.setCancelled(true);
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "You need to set this item's worth with " + ChatColor.AQUA + "/ps setworth [amount]" + ChatColor.RED + " before you can add it to your shop.");
        }
    }

    @NotNull
    public static StringBuilder getStringBuilder(ItemStack im) {
        StringBuilder enchantString = new StringBuilder();
        int counter = 0;
        if (im.getType().equals(Material.ENCHANTED_BOOK)){
            Map<Enchantment, Integer> enchants;
            EnchantmentStorageMeta meta =(EnchantmentStorageMeta) im.getItemMeta();
            enchants = meta.getStoredEnchants();
            Iterator it = enchants.entrySet().iterator();
            int count = 0;
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                String holder = pair.getKey().toString().replace("Enchantment[minecraft:", "");
                holder = holder.replace(" ", "");
                holder = holder.replace("]", "");
                String[] arr = holder.split(",");
                if (count == 0){
                    enchantString.append("-Enchant:" + arr[0] + "|Level:"+pair.getValue());
                } else if (count > 0 && count < enchants.size() - 1){
                    enchantString.append("-Enchant:" + arr[0] + "|Level:"+pair.getValue() + "-");
                } else {
                    enchantString.append("Enchant:" + arr[0] + "|Level:"+pair.getValue());
                }
                count++;
            }
        } else {
            Set<Map.Entry<Enchantment, Integer>> enchants = im.getEnchantments().entrySet();
            int enchantSize = enchants.size();
            for (Map.Entry<Enchantment, Integer> enchant : enchants){
                if (counter == 0){
                    enchantString.append("-Enchant:" + enchant.getKey().getKey().getKey() + "|Level:"+enchant.getValue());
                } else if (counter > 0 && counter < enchantSize - 1){
                    enchantString.append("-Enchant:" + enchant.getKey().getKey().getKey() + "|Level:"+enchant.getValue() + "-");
                } else {
                    enchantString.append("Enchant:" + enchant.getKey().getKey().getKey() + "|Level:"+enchant.getValue());
                }
                counter++;
            }
        }
        return enchantString;
    }

    public static void sd(InventoryClickEvent e, Economy econ, Player player) throws IOException {
        OfflinePlayer offlineBuyer = null;
        OfflinePlayer offlineSeller = null;
        double buyerBalance = 0;
        FileConfiguration playerShopCfg = Main.getFileConfiguration(Main.playerShopFile);
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
            int itemCost = 0;
            if (playerShopCfg.isSet(Constants.YML_PLAYERS + offlineSeller.getName() + ".itemWorth." + e.getCurrentItem().getType()  + getStringBuilder(e.getCurrentItem()))){
                itemCost = playerShopCfg.getInt(Constants.YML_PLAYERS + offlineSeller.getName() + ".itemWorth." + e.getCurrentItem().getType() + getStringBuilder(e.getCurrentItem()));
            } else if (playerShopCfg.isSet(Constants.YML_PLAYERS + offlineSeller.getName() + ".itemWorth." + e.getCurrentItem().getType())){
                itemCost = playerShopCfg.getInt(Constants.YML_PLAYERS + offlineSeller.getName() + ".itemWorth." + e.getCurrentItem().getType());
            }
            int totalAmount = e.getCurrentItem().getAmount();
            ItemMeta itemMeta = e.getCurrentItem().getItemMeta();
            itemMeta.setLore(null);
            if (e.getClick().toString().equalsIgnoreCase("LEFT")){
                if (buyerBalance < itemCost){
                    player.sendMessage(ChatColor.RED + "Not enough money to buy this item.");
                } else {
                    ItemStack im = e.getCurrentItem();
                    ItemStack test = e.getCurrentItem();
                    im.setAmount(im.getAmount() - 1);
                    e.getClickedInventory().setItem(e.getSlot(), im);
                    test.setAmount(1);
                    test.setItemMeta(itemMeta);
                    player.getInventory().addItem(test);
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
                        ItemStack im = e.getCurrentItem();
                        ItemStack test = e.getCurrentItem();
                        im.setAmount(im.getAmount() - 10);
                        e.getClickedInventory().setItem(e.getSlot(), im);
                        test.setAmount(10);
                        test.setItemMeta(itemMeta);
                        player.getInventory().addItem(test);
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
                    ItemStack im = e.getCurrentItem();
                    ItemStack test = e.getCurrentItem();
                    im.setAmount(im.getAmount() - totalAmount);
                    e.getClickedInventory().setItem(e.getSlot(), im);
                    test.setAmount(totalAmount);
                    test.setItemMeta(itemMeta);
                    player.getInventory().addItem(test);
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
