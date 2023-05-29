package lmp.listeners.onLootGenerateEvents;

import lmp.Main;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AddMendingBookToGeneratedLoot implements Listener {
    public AddMendingBookToGeneratedLoot(Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInventoryOpen(LootGenerateEvent event) {
        List<String> enabledMendingLootWorlds = new ArrayList<>();
        enabledMendingLootWorlds.add("world");
        enabledMendingLootWorlds.add("world_nether");
        enabledMendingLootWorlds.add("world_the_end");

        if (enabledMendingLootWorlds.contains(event.getWorld().getName()) && event.getInventoryHolder() != null){
            for (int i = 0; i < event.getInventoryHolder().getInventory().getSize(); i++){
                if (event.getInventoryHolder().getInventory().getItem(i) == null){
                    ItemStack mendingBook = new ItemStack(Material.ENCHANTED_BOOK);
                    EnchantmentStorageMeta enchantmentMeta = (EnchantmentStorageMeta) mendingBook.getItemMeta();
                    assert enchantmentMeta != null;
                    enchantmentMeta.addStoredEnchant(Enchantment.MENDING, 1, true);
                    ItemMeta im = mendingBook.getItemMeta();
                    assert im != null;
                    Random r = new Random();
                    int bookSpawnPercentage = r.nextInt(100);
                    if (bookSpawnPercentage < 11) {
                        mendingBook.setItemMeta(enchantmentMeta);
                        event.getInventoryHolder().getInventory().setItem(i, mendingBook);
                    }
                    break;
                }
            }
        }
    }
}
