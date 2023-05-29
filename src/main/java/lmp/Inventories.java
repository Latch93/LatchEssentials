package lmp;

import lmp.api.Api;
import lmp.constants.YmlFileNames;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Inventories {
    public static void saveCustomInventory(InventoryCloseEvent e, File configFile) throws IOException {
        FileConfiguration inventoryCfg = YamlConfiguration.loadConfiguration(configFile);
        UUID playerUUID = e.getPlayer().getUniqueId();
        String playerName = e.getPlayer().getName();
        String[] arr = e.getView().getTitle().split("'");
        UUID titleUUID = UUID.fromString(Api.getMinecraftIdFromMinecraftName(arr[0]));
        for (String minecraftId : inventoryCfg.getKeys(false)) {
            if (inventoryCfg.isSet(minecraftId + Constants.YML_SIZE) && e.getView().getTitle().toLowerCase().contains(Objects.requireNonNull(Objects.requireNonNull(Bukkit.getOfflinePlayer(titleUUID).getName()).toLowerCase()))) {
                playerName = Bukkit.getOfflinePlayer(UUID.fromString(minecraftId)).getName();
            }
        }
        for (int i = 0; i < e.getInventory().getSize(); i++) {
            if (e.getInventory().getItem(i) != null) {
                ItemStack itemStack = e.getInventory().getItem(i);
                ItemMeta im = setItemLore(e, i, false);
                assert itemStack != null;
                itemStack.setItemMeta(im);
                inventoryCfg.set(titleUUID + Constants.YML_SLOTS + i, itemStack);
            } else {
                inventoryCfg.set(titleUUID + Constants.YML_SLOTS + i, null);
            }
        }
        if (inventoryCfg.get(titleUUID + Constants.YML_SIZE) == null) {
            inventoryCfg.set(titleUUID + Constants.YML_SIZE, e.getInventory().getSize());
        } else {
            inventoryCfg.set(titleUUID + Constants.YML_SIZE, inventoryCfg.getInt(titleUUID + Constants.YML_SIZE));
        }
        inventoryCfg.set(titleUUID + ".name", Bukkit.getOfflinePlayer(titleUUID).getName());
        inventoryCfg.set(titleUUID + ".isOpen", false);
        inventoryCfg.save(configFile);
    }



    @Nullable
    public static ItemMeta setItemLore(InventoryCloseEvent e, int i, boolean isPlayerShop) {
        ItemMeta im = null;
        Player player = (Player) e.getPlayer();
        Inventory inv = null;
        if (Boolean.TRUE.equals(isPlayerShop)) {
            inv = player.getInventory();
        } else {
            inv = e.getInventory();
        }
        if (inv.getItem(i) != null) {
            im = Objects.requireNonNull(inv.getItem(i)).getItemMeta();
            String playerSetLore = null;
            if (im != null) {
                if (im.getLore() != null) {
                    if (im.getLore().get(0).contains("Cost")) {
                        im.setLore(null);
                    } else {
                        playerSetLore = im.getLore().get(0);
                    }
                }
            }
            if (playerSetLore != null) {
                ArrayList<String> lore = new ArrayList<>();
                lore.add(playerSetLore);
                im.setLore(lore);
            }
        }

        return im;
    }

    public static ItemMeta getItemWorthWithLore(Player player, ItemStack itemStack, String sellerName) {
        ItemMeta im = null;
        Inventory inv = null;
        if (itemStack != null) {
            im = Objects.requireNonNull(itemStack).getItemMeta();
            String playerSetLore = null;
            if (im != null) {
                if (im.getLore() != null) {
                    if (im.getLore().get(0).contains("Cost")) {
                        im.setLore(null);
                    } else {
                        playerSetLore = im.getLore().get(0);
                    }
                }
            }
            if (playerSetLore != null) {
                ArrayList<String> lore = new ArrayList<>();
                lore.add(playerSetLore);
                im.setLore(lore);
            }
        }

        return im;
    }

    public static Inventory setInventoryWhenOpened(Player player, String fileName, int slots, String invTitle, String playerShopToOpen) throws IOException {
        FileConfiguration inventoryConfig = Api.loadConfig(fileName);
        Inventory inv = null;
        String playerName = "";
        String playerId = "";
        if (playerShopToOpen == null) {
            playerId = player.getUniqueId().toString();
        } else {
            playerId = Api.getMinecraftIdFromMinecraftName(playerShopToOpen);
        }
        if (inventoryConfig.get(playerId + Constants.YML_SIZE) != null) {
            inv = Bukkit.createInventory(null, slots, invTitle);
            if ((inventoryConfig.get(playerId + ".slots") != null)) {
                for (String users : inventoryConfig.getConfigurationSection(playerId + ".slots").getKeys(false)) {
                    inventoryConfig.set(playerId + ".isOpen", true);
                    inventoryConfig.save(fileName);
                    ItemStack is = inventoryConfig.getItemStack(playerId + Constants.YML_SLOTS + users);
                    inv.setItem(Integer.parseInt(users), is);
                }
            }
        } else if (invTitle.toLowerCase().contains("backpack")) {
            Main.log.info("test 1");
            player.sendMessage(ChatColor.RED + "You need to purchase a backpack before you use this command");
        } else {
            inv = Bukkit.createInventory(player, 27, invTitle);
        }
        return inv;
    }

    public static Inventory setBackpackInventoryWhenOpened(Player player, File file, int slots, String invTitle) throws IOException {
        FileConfiguration inventoryConfig = YamlConfiguration.loadConfiguration(file);
        Inventory inv = null;
        String playerName = "";
        String playerId = player.getUniqueId().toString();
        if (inventoryConfig.get(playerId + Constants.YML_SIZE) != null) {
            inv = Bukkit.createInventory(null, slots, invTitle);
            if ((inventoryConfig.get(playerId + ".slots") != null)) {
                for (String users : inventoryConfig.getConfigurationSection(playerId + ".slots").getKeys(false)) {
                    inventoryConfig.set(playerId + ".isOpen", true);
                    inventoryConfig.save(file);
                    ItemStack is = inventoryConfig.getItemStack(playerId + Constants.YML_SLOTS + users);
                    inv.setItem(Integer.parseInt(users), is);
                }
            }
        } else {
            player.sendMessage(ChatColor.RED + "You must purchase a backpack.");
        }
        return inv;
    }

    public static Inventory setLoreInPlayerShop(String playerShopToOpen, Inventory inv, String playerName) {
        FileConfiguration playerShopCfg = Api.loadConfig(YmlFileNames.YML_PLAYER_SHOP_FILE_NAME);
        OfflinePlayer playerShopToOpenPlayer = Bukkit.getOfflinePlayer(UUID.fromString(Api.getMinecraftIdFromMinecraftName(playerShopToOpen)));
        OfflinePlayer secondPlayerNameToCheckAgainst = Bukkit.getOfflinePlayer(UUID.fromString(Api.getMinecraftIdFromMinecraftName(playerName)));
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) != null) {
                ItemStack itemStack = inv.getItem(i);
                assert itemStack != null;
                int itemStackAmount = itemStack.getAmount();
                itemStack.setAmount(1);
                int totalAmount = itemStack.getAmount();
                int itemWorth;
                ItemMeta im = Objects.requireNonNull(inv.getItem(i)).getItemMeta();
                itemWorth = playerShopCfg.getInt(playerShopToOpenPlayer.getUniqueId() + ".itemWorth." + itemStack);
                List<String> loreList = new ArrayList<>();
                assert im != null;
                if (im.getLore() != null) {
                    loreList.add(im.getLore().get(0));
                }
                itemStack.setAmount(1);
                itemStack.setAmount(itemStackAmount);
                int totalWorth = totalAmount * itemWorth;
                if (!playerShopToOpenPlayer.getUniqueId().equals(secondPlayerNameToCheckAgainst.getUniqueId())) {
                    loreList.add(ChatColor.GREEN + "Cost per item " + ChatColor.GOLD + "$" + itemWorth);
                    loreList.add(ChatColor.GREEN + "Left click to purchase " + ChatColor.GOLD + "1" + ChatColor.GREEN + " item.");
                    if (totalAmount > 9) {
                        loreList.add(ChatColor.GREEN + "Right click to purchase " + ChatColor.GOLD + "10" + ChatColor.GREEN + " items. Total cost: " + ChatColor.GOLD + "$" + Math.multiplyExact(10, itemWorth));
                    }
                    //loreList.add(ChatColor.GREEN + "Middle click to purchase all items. Total cost: " + ChatColor.GOLD + "$" + totalWorth);
                } else {
                    loreList.add(ChatColor.GREEN + "Cost per item: " + ChatColor.GOLD + "$" + itemWorth);
                }
                assert im != null;
                im.setLore(loreList);
                itemStack.setItemMeta(im);
            }
        }
        return inv;
    }

    public static int getItemWorth(ItemStack itemStack, String playerShopToOpen) {
        FileConfiguration playerShopCfg = Api.loadConfig(YmlFileNames.YML_PLAYER_SHOP_FILE_NAME);
        int itemWorth;
        ItemMeta im = Objects.requireNonNull(itemStack.getItemMeta());
        im.setLore(null);
        itemStack.setItemMeta(im);
        itemWorth = playerShopCfg.getInt(Bukkit.getPlayer(playerShopToOpen).getUniqueId() + ".itemWorth." + itemStack);
        return itemWorth;
    }

}
