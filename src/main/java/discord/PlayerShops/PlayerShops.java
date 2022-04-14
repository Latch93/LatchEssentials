package discord.PlayerShops;

import discord.Backbacks.BackPacks;
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

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PlayerShops {
    public static void illegalPlayerShopItems(InventoryClickEvent e, Player player){
        if (e.getCurrentItem().getType().toString().contains("SHULKER_BOX") ){
            e.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Unable to sell " + ChatColor.GOLD + e.getCurrentItem().getType() + "'s" + ChatColor.RED + " in player shops at this time");
        }
    }
    public static void itemWorthNotSet(InventoryClickEvent e, Player player, FileConfiguration playerShopCfg){
        String itemWorthString = BackPacks.getItemWorthString(Objects.requireNonNull(e.getCurrentItem()));
        if (!playerShopCfg.isSet(player.getName() + ".itemWorth." + itemWorthString)) {
            e.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You need to set this item's worth with " + ChatColor.AQUA + "/ps setworth [amount]" + ChatColor.RED + " before you can add it to your shop.");
        }
    }

    public static void removeLoreFromSellerInventory(InventoryCloseEvent e, File playerShopFile) throws IOException {
        Player player = (Player) e.getPlayer();
        if (e.getView().getTitle().contains(Constants.YML_POSSESSIVE_PLAYER_SHOP)){
            BackPacks.saveCustomInventory(e, playerShopFile);
            if (e.getView().getTitle().contains(e.getPlayer().getName())){
                Inventory inv = player.getInventory();
                int slotCount = 0;
                for (ItemStack itemStack : inv){
                    if (itemStack != null && Boolean.TRUE.equals(itemStack.hasItemMeta())){
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        assert itemMeta != null;
                        if (itemMeta.getLore() != null && itemMeta.getLore().toString().contains("Cost per item")){
                            itemMeta.setLore(null);
                            itemStack.setItemMeta(itemMeta);
                            inv.setItem(slotCount, itemStack);
                        }
                    }
                    slotCount++;
                }
            }
        }
    }

    public static void purchaseItemFromPlayer(InventoryClickEvent e, Economy econ, Player player) {
        OfflinePlayer offlineBuyer = null;
        OfflinePlayer offlineSeller = null;
        double buyerBalance;
        FileConfiguration playerShopCfg = Main.loadConfig(Constants.YML_PLAYER_SHOP_FILE_NAME);
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
            assert offlineSeller != null;
            System.out.println("a: " + offlineSeller.getName() + ".itemWorth." + BackPacks.getItemWorthString(Objects.requireNonNull(e.getCurrentItem())));
            if (playerShopCfg.isSet(offlineSeller.getName() + ".itemWorth." + BackPacks.getItemWorthString(Objects.requireNonNull(e.getCurrentItem())))){
                itemCost = playerShopCfg.getInt(offlineSeller.getName() + ".itemWorth." + BackPacks.getItemWorthString(Objects.requireNonNull(e.getCurrentItem())));
            }
            int totalAmount = e.getCurrentItem().getAmount();
            ItemMeta itemMeta = e.getCurrentItem().getItemMeta();
            assert itemMeta != null;
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
