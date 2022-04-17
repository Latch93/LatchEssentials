package discord.PlayerShops;

import discord.Backbacks.Inventories;
import discord.Constants;
import discord.Main;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class PlayerShops {
    public static void itemWorthNotSet(InventoryClickEvent e, Player player, FileConfiguration playerShopCfg){
        ItemStack itemStack = e.getCurrentItem();
        assert itemStack != null;
        int totalItemAmount = itemStack.getAmount();
        itemStack.setAmount(1);
        String[] arr = e.getView().getTitle().split(Constants.YML_POSSESSIVE_PLAYER_SHOP);
        String sellerShopPlayerName = arr[0];
        itemStack.setItemMeta(Inventories.getItemWorthWithLore(player, itemStack, sellerShopPlayerName));
        if (!playerShopCfg.isSet(player.getName() + ".itemWorth." + itemStack)){
            e.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You need to set this item's worth with " + ChatColor.AQUA + "/ps setworth [amount]" + ChatColor.RED + " before you can add it to your shop.");
        }
        itemStack.setAmount(totalItemAmount);
    }

    public static void removeLoreFromSellerInventory(InventoryCloseEvent e, File playerShopFile) throws IOException {
        Player player = (Player) e.getPlayer();
        if (e.getView().getTitle().contains(Constants.YML_POSSESSIVE_PLAYER_SHOP)){
            Inventories.saveCustomInventory(e, playerShopFile);
            if (e.getView().getTitle().contains(e.getPlayer().getName())){
                for (int i = 0; i < 39; i++){
                    ItemStack itemStack = player.getInventory().getItem(i);
                    if (itemStack != null){
                        ItemMeta im = Inventories.setItemLore(e, i, true);
                        if (im != null){
                            itemStack.setItemMeta(im);
                        }
                        player.getInventory().setItem(i,itemStack);
                    }
                    player.updateInventory();

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
            assert offlineSeller != null;
            ItemStack ims = e.getCurrentItem();
            int itemAmount = ims.getAmount();
            ims.setAmount(1);
            ItemMeta im = Inventories.getItemWorthWithLore(player, ims, offlineSeller.getName() );
            ims.setItemMeta(im);
            int itemCost = playerShopCfg.getInt(offlineSeller.getName() + ".itemWorth." + ims);
            ims.setAmount(itemAmount);
            if (e.getClick().toString().equalsIgnoreCase("LEFT")){
                if (buyerBalance < itemCost){
                    player.sendMessage(ChatColor.RED + "Not enough money to buy this item.");
                } else {
                    ItemStack itemStack = e.getCurrentItem();
                    ItemStack test = e.getCurrentItem();
                    itemStack.setAmount(itemStack.getAmount() - 1);
                    e.getClickedInventory().setItem(e.getSlot(), itemStack);
                    test.setAmount(1);
                    player.getInventory().addItem(test);
                    econ.withdrawPlayer(offlineBuyer, itemCost);
                    econ.depositPlayer(offlineSeller, itemCost);
                    player.updateInventory();
                }
                e.setCancelled(true);
            } else if (e.getClick().toString().equalsIgnoreCase("RIGHT")){
                e.setCancelled(true);
                if (e.getCurrentItem().getAmount() >= 10) {
                    int itemTotalCost = itemCost * 10;
                    if (buyerBalance < itemTotalCost){
                        player.sendMessage(ChatColor.RED + "Not enough money to buy these 10 items.");
                    } else {
                        ItemStack imq = e.getCurrentItem();
                        ItemStack test = e.getCurrentItem();
                        imq.setAmount(imq.getAmount() - 10);
                        e.getClickedInventory().setItem(e.getSlot(), imq);
                        test.setAmount(10);
                        player.getInventory().addItem(test);
                        econ.withdrawPlayer(offlineBuyer, itemTotalCost);
                        econ.depositPlayer(offlineSeller, itemTotalCost);
                        player.updateInventory();
                    }
                }
                e.setCancelled(true);
            }
        } else {
            e.setCancelled(true);
        }
    }
}
