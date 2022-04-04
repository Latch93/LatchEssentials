package discord.Backbacks;

import discord.Constants;
import discord.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class BackPacks {
    public static void saveBackPack(InventoryCloseEvent e) throws IOException {
        File configFile = new File(Main.getPlugin(Main.class).getDataFolder(), "playerBackpack.yml");
        FileConfiguration configCfg = YamlConfiguration.loadConfiguration(configFile);
        UUID playerUUID = e.getPlayer().getUniqueId();
        String playerName = e.getPlayer().getName();
        for (int i = 0; i < e.getInventory().getSize(); i++){
            if (e.getInventory().getItem(i) != null){
                String itemName = Objects.requireNonNull(e.getInventory().getItem(i)).getType().toString();
                String itemAmount = String.valueOf(Objects.requireNonNull(e.getInventory().getItem(i)).getAmount());
                configCfg.set(playerUUID + Constants.YML_SLOTS + i + ".material", itemName);
                configCfg.set(playerUUID + Constants.YML_SLOTS + i + ".amount", itemAmount);
                ItemMeta im = Objects.requireNonNull(e.getInventory().getItem(i)).getItemMeta();
                assert im != null;
                configCfg.set(playerUUID + Constants.YML_SLOTS + i + ".displayName", im.getDisplayName());
                configCfg.set(playerUUID + Constants.YML_SLOTS + i + ".itemMeta", im.toString());
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
                    configCfg.set(playerUUID + Constants.YML_SLOTS + i + ".enchants." +count + ".enchant", enchantment.getKey().getKey());
                    configCfg.set(playerUUID + Constants.YML_SLOTS + i + ".enchants." +count + ".level", pair.getValue());
                    count++;
                }
            } else {
                configCfg.set(playerUUID + Constants.YML_SLOTS + i, null);
            }
        }
        if (configCfg.get(playerUUID + Constants.YML_SIZE) == null){
            configCfg.set(playerUUID + Constants.YML_SIZE, e.getInventory().getSize());
        } else {
            configCfg.set(playerUUID + Constants.YML_SIZE, configCfg.getInt(playerUUID + Constants.YML_SIZE));
        }
        configCfg.set(playerUUID + ".name", playerName);
        configCfg.save(configFile);
    }
}
