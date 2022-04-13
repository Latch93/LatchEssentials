package discord.Backbacks;

import discord.Constants;
import discord.Main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class BackPacks {
    public static void saveBackPack(InventoryCloseEvent e, File configFile) throws IOException {
        FileConfiguration inventoryCfg = Main.getFileConfiguration(configFile);
        UUID playerUUID = e.getPlayer().getUniqueId();
        String playerName = e.getPlayer().getName();
        for (int i = 0; i < e.getInventory().getSize(); i++){
            if (e.getInventory().getItem(i) != null){
                String itemName = Objects.requireNonNull(e.getInventory().getItem(i)).getType().toString();
                String itemAmount = String.valueOf(Objects.requireNonNull(e.getInventory().getItem(i)).getAmount());
                inventoryCfg.set(playerUUID + Constants.YML_SLOTS + i + ".material", itemName);
                inventoryCfg.set(playerUUID + Constants.YML_SLOTS + i + ".amount", itemAmount);
                ItemMeta im = Objects.requireNonNull(e.getInventory().getItem(i)).getItemMeta();
                assert im != null;
                inventoryCfg.set(playerUUID + Constants.YML_SLOTS + i + ".displayName", im.getDisplayName());
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
                        String[] arr = holder.split(",");
                        inventoryCfg.set(playerUUID + Constants.YML_SLOTS + i + Constants.YML_ENCHANTS +count + ".enchant", arr[0]);
                        inventoryCfg.set(playerUUID + Constants.YML_SLOTS + i + Constants.YML_ENCHANTS +count + ".level", pair.getValue());
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
                        inventoryCfg.set(playerUUID + Constants.YML_SLOTS + i + Constants.YML_ENCHANTS +count + ".enchant", enchantment.getKey().getKey());
                        inventoryCfg.set(playerUUID + Constants.YML_SLOTS + i + Constants.YML_ENCHANTS +count + ".level", pair.getValue());
                        count++;
                    }
                }
            } else {
                inventoryCfg.set(playerUUID + Constants.YML_SLOTS + i, null);
            }
        }
        if (inventoryCfg.get(playerUUID + Constants.YML_SIZE) == null){
            inventoryCfg.set(playerUUID + Constants.YML_SIZE, e.getInventory().getSize());
        } else {
            inventoryCfg.set(playerUUID + Constants.YML_SIZE, inventoryCfg.getInt(playerUUID + Constants.YML_SIZE));
        }
        inventoryCfg.set(playerUUID + ".name", playerName);
        inventoryCfg.save(configFile);
    }

    public static void setInventoryWhenOpened(Player player, File configFile, int slots, String invTitle){
        FileConfiguration inventoryConfig = Main.getFileConfiguration(configFile);
        Inventory inv;
        if (inventoryConfig.get(player.getUniqueId() + Constants.YML_SIZE) != null) {
            //int numberOfSlots = inventoryConfig.getInt(player.getUniqueId() + Constants.YML_SIZE);
            System.out.println("fgasd: " + slots);
            inv = Bukkit.createInventory(null, slots, invTitle);
            if ((inventoryConfig.get(player.getUniqueId() + ".slots") != null)){
                for(String users : inventoryConfig.getConfigurationSection(player.getUniqueId() + ".slots").getKeys(false)) {
                    ItemStack is = new ItemStack(Material.valueOf(inventoryConfig.getString(player.getUniqueId() + Constants.YML_SLOTS + users + ".material")),  Integer.parseInt(inventoryConfig.getString(player.getUniqueId() + Constants.YML_SLOTS + users + ".amount")));
                    ItemMeta im = is.getItemMeta();
                    if (inventoryConfig.isSet(player.getUniqueId() + Constants.YML_SLOTS + users + ".enchants")) {
                        for(String test : inventoryConfig.getConfigurationSection(player.getUniqueId() + Constants.YML_SLOTS + users + ".enchants").getKeys(false)) {
                            assert im != null;
                            if (inventoryConfig.getString(player.getUniqueId() + Constants.YML_SLOTS + users + Constants.YML_ENCHANTS + test + ".enchant") != null && inventoryConfig.getInt(player.getUniqueId() + Constants.YML_SLOTS + users + Constants.YML_ENCHANTS + test + ".level") != 0 ){
                                if (is.getType().equals(Material.ENCHANTED_BOOK)){
                                    EnchantmentStorageMeta esm = (EnchantmentStorageMeta) im;
                                    esm.addStoredEnchant(Objects.requireNonNull(Enchantment.getByKey(NamespacedKey.minecraft(Objects.requireNonNull(inventoryConfig.getString(player.getUniqueId() + Constants.YML_SLOTS + users + Constants.YML_ENCHANTS + test + ".enchant"))))), inventoryConfig.getInt(player.getUniqueId() + Constants.YML_SLOTS + users + Constants.YML_ENCHANTS + test + ".level"), true);
                                    is.setItemMeta(esm);
                                } else {
                                    im.addEnchant(Objects.requireNonNull(Enchantment.getByKey(NamespacedKey.minecraft(Objects.requireNonNull(inventoryConfig.getString(player.getUniqueId() + Constants.YML_SLOTS + users + Constants.YML_ENCHANTS + test + ".enchant"))))), inventoryConfig.getInt(player.getUniqueId() + Constants.YML_SLOTS + users + Constants.YML_ENCHANTS + test + ".level"), true );
                                }
                            }
                        }
                    }
                    assert im != null;
                    if (inventoryConfig.getString(player.getUniqueId() + Constants.YML_SLOTS + users + ".displayName") != null ){
                        im.setDisplayName(inventoryConfig.getString(player.getUniqueId() + Constants.YML_SLOTS + users + ".displayName"));
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
