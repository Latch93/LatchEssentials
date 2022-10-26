package lmp;

import lmp.api.Api;
import lmp.constants.YmlFileNames;
import net.dv8tion.jda.api.entities.Member;
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
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class PlayerShops {
    private PlayerShops() {
    }

    public static void itemWorthNotSet(InventoryClickEvent e, Player player, FileConfiguration playerShopCfg) {
        ItemStack itemStack = e.getCurrentItem();
        assert itemStack != null;
        int totalItemAmount = itemStack.getAmount();
        itemStack.setAmount(1);
        String[] arr = e.getView().getTitle().split(Constants.YML_POSSESSIVE_PLAYER_SHOP);
        String sellerShopPlayerName = arr[0];
        itemStack.setItemMeta(Inventories.getItemWorthWithLore(player, itemStack, sellerShopPlayerName));
        if (!playerShopCfg.isSet(player.getUniqueId() + ".itemWorth." + itemStack)) {
            e.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You need to set this item's worth with " + ChatColor.AQUA + "/ps setworth [amount]" + ChatColor.RED + " before you can add it to your shop.");
        }
        itemStack.setAmount(totalItemAmount);
    }

    public static void removeLoreFromSellerInventory(InventoryCloseEvent e, File playerShopFile) throws IOException {
        Player player = (Player) e.getPlayer();
        if (e.getView().getTitle().contains(Constants.YML_POSSESSIVE_PLAYER_SHOP)) {
            Inventories.saveCustomInventory(e, playerShopFile);
            if (e.getView().getTitle().contains(e.getPlayer().getName())) {
                for (int i = 0; i < 39; i++) {
                    ItemStack itemStack = player.getInventory().getItem(i);
                    if (itemStack != null) {
                        ItemMeta im = Inventories.setItemLore(e, i, true);
                        if (im != null) {
                            itemStack.setItemMeta(im);
                        }
                        player.getInventory().setItem(i, itemStack);
                    }
                    player.updateInventory();
                }
            }
        }
    }

    public static void purchaseItemFromPlayer(InventoryClickEvent e, Economy econ, Player player) throws ExecutionException, InterruptedException {
        OfflinePlayer offlineBuyer = null;
        OfflinePlayer offlineSeller = null;

        FileConfiguration playerShopCfg = Api.loadConfig(YmlFileNames.YML_PLAYER_SHOP_FILE_NAME);
        if (Boolean.TRUE.equals(e.isShiftClick())) {
            e.setCancelled(true);
        } else {
            if (Objects.requireNonNull(e.getClickedInventory()).getSize() == 27) {
                String[] arr = e.getView().getTitle().split(Constants.YML_POSSESSIVE_PLAYER_SHOP);
                String sellerShopPlayerName = arr[0];
                offlineBuyer = Bukkit.getOfflinePlayer(UUID.fromString(Api.getMinecraftIdFromMinecraftName(e.getWhoClicked().getName())));
                offlineSeller = Bukkit.getOfflinePlayer(UUID.fromString(Api.getMinecraftIdFromMinecraftName(sellerShopPlayerName)));
                ItemStack ims = e.getCurrentItem();
                assert ims != null;
                int itemAmount = ims.getAmount();
                ims.setAmount(1);
                ItemMeta im = Inventories.getItemWorthWithLore(player, ims, offlineSeller.getName());
                ims.setItemMeta(im);
                int itemCost = playerShopCfg.getInt(offlineSeller.getUniqueId() + ".itemWorth." + ims);
                ims.setAmount(itemAmount);
                if (e.getClick().toString().equalsIgnoreCase("LEFT")) {
                    leftClickPurchase(e, econ, player, offlineBuyer, offlineSeller, econ.getBalance(offlineBuyer), itemCost);
                } else if (e.getClick().toString().equalsIgnoreCase("RIGHT")) {
                    rightClickPurchase(e, econ, player, offlineBuyer, offlineSeller, econ.getBalance(offlineBuyer), itemCost);
                }
//                else if (e.getClick().toString().equalsIgnoreCase("MIDDLE")){
//                    middleClickPurchase(e, econ, player, offlineBuyer, offlineSeller, econ.getBalance(offlineBuyer), itemCost);
//                }
            } else {
                e.setCancelled(true);
            }
        }
    }

    private static void leftClickPurchase(InventoryClickEvent e, Economy econ, Player player, OfflinePlayer offlineBuyer, OfflinePlayer offlineSeller, double buyerBalance, int itemCost) throws ExecutionException, InterruptedException {
        if (buyerBalance < itemCost) {
            player.sendMessage(ChatColor.RED + "Not enough money to buy this item.");
        } else {
            if (Boolean.FALSE.equals(Api.doesPlayerHavePermission(offlineSeller.getUniqueId().toString(), "ignoreshop")) && Objects.requireNonNull(LatchDiscord.getJDA().getGuildById(Constants.GUILD_ID)).getMemberById(Api.getDiscordIdFromMCid(offlineSeller.getUniqueId().toString())) != null) {
                Member seller = Objects.requireNonNull(LatchDiscord.getJDA().getGuildById(Constants.GUILD_ID)).getMemberById(Api.getDiscordIdFromMCid(offlineSeller.getUniqueId().toString()));
                assert seller != null;
                seller.getUser().openPrivateChannel().queue((privateChannel -> privateChannel.sendMessage(offlineBuyer.getName() + " has bought 1 " + Objects.requireNonNull(e.getCurrentItem()).getType() + " for $" + itemCost).queue()));
            }
            setBuyerAndSellerInventories(e, econ, player, offlineBuyer, offlineSeller, itemCost, 1);
        }
        e.setCancelled(true);
    }

    private static void rightClickPurchase(InventoryClickEvent e, Economy econ, Player player, OfflinePlayer offlineBuyer, OfflinePlayer offlineSeller, double buyerBalance, int itemCost) throws ExecutionException, InterruptedException {
        e.setCancelled(true);
        if (Objects.requireNonNull(e.getCurrentItem()).getAmount() >= 10) {
            int itemTotalAmount = e.getCurrentItem().getAmount();
            int itemTotalCost = itemCost * itemTotalAmount;
            if (buyerBalance < itemTotalCost) {
                player.sendMessage(ChatColor.RED + "Not enough money to buy these " + itemTotalAmount + " items.");
            } else {
                if ((Boolean.FALSE.equals(Api.doesPlayerHavePermission(offlineSeller.getUniqueId().toString(), "ignoreshop")) && Objects.requireNonNull(LatchDiscord.getJDA().getGuildById(Constants.GUILD_ID)).getMemberById(Api.getDiscordIdFromMCid(offlineSeller.getUniqueId().toString())) != null)) {
                    Member seller = Objects.requireNonNull(LatchDiscord.getJDA().getGuildById(Constants.GUILD_ID)).getMemberById(Api.getDiscordIdFromMCid(offlineSeller.getUniqueId().toString()));
                    assert seller != null;
                    seller.getUser().openPrivateChannel().queue((privateChannel -> privateChannel.sendMessage(offlineBuyer.getName() + " has bought " + itemTotalAmount + " " + Objects.requireNonNull(e.getCurrentItem()).getType() + " for $" + itemTotalCost).queue()));
                }
                setBuyerAndSellerInventories(e, econ, player, offlineBuyer, offlineSeller, itemCost, itemTotalAmount);
            }
        }
        e.setCancelled(true);
    }

    private static void middleClickPurchase(InventoryClickEvent e, Economy econ, Player player, OfflinePlayer offlineBuyer, OfflinePlayer offlineSeller, double buyerBalance, int itemCost) throws ExecutionException, InterruptedException {
        e.setCancelled(true);
        if (Objects.requireNonNull(e.getCurrentItem()).getAmount() != 0) {
            int itemTotalCost = itemCost * 10;
            if (buyerBalance < itemTotalCost) {
                player.sendMessage(ChatColor.RED + "Not enough money to buy these 10 items.");
            } else {
                if ((Boolean.FALSE.equals(Api.doesPlayerHavePermission(offlineSeller.getUniqueId().toString(), "ignoreshop")) && Objects.requireNonNull(LatchDiscord.getJDA().getGuildById(Constants.GUILD_ID)).getMemberById(Api.getDiscordIdFromMCid(offlineSeller.getUniqueId().toString())) != null)) {
                    Member seller = Objects.requireNonNull(LatchDiscord.getJDA().getGuildById(Constants.GUILD_ID)).getMemberById(Api.getDiscordIdFromMCid(offlineSeller.getUniqueId().toString()));
                    assert seller != null;
                    seller.getUser().openPrivateChannel().queue((privateChannel -> privateChannel.sendMessage(offlineBuyer.getName() + " has bought 10 " + Objects.requireNonNull(e.getCurrentItem()).getType() + " for $" + itemTotalCost).queue()));
                }
                setBuyerAndSellerInventories(e, econ, player, offlineBuyer, offlineSeller, itemCost, 10);
            }
        }
        e.setCancelled(true);
    }

    private static void setBuyerAndSellerInventories(InventoryClickEvent e, Economy econ, Player player, OfflinePlayer offlineBuyer, OfflinePlayer offlineSeller, int itemCost, int amountSold) {
        ItemStack itemStack = e.getCurrentItem();
        ItemStack test = e.getCurrentItem();
        assert itemStack != null;
        itemStack.setAmount(itemStack.getAmount() - amountSold);
        Objects.requireNonNull(e.getClickedInventory()).setItem(e.getSlot(), itemStack);
        assert test != null;
        test.setAmount(amountSold);
        player.getInventory().addItem(test);
        econ.withdrawPlayer(offlineBuyer, itemCost);
        econ.depositPlayer(offlineSeller, itemCost);
        player.updateInventory();
    }
}
