package discord_text;

import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class MobileSpawner {
    public static void disableSpawnerMobChange(PlayerInteractEvent event){
        if (event.getAction().toString().equals("RIGHT_CLICK_BLOCK") &&
                event.getPlayer().getInventory().getItemInMainHand().getType().toString().contains("SPAWN_EGG") &&
                    Objects.requireNonNull(event.getClickedBlock()).getType().toString().equals("SPAWNER")){
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.RED + "Unable to change mob in spawner");
        }
    }
    public static void setSpawnerOnBreak(BlockBreakEvent event){
        if(event.getBlock().getState() instanceof CreatureSpawner) {
            Map<Enchantment, Integer> itemEnchants = event.getPlayer().getInventory().getItemInMainHand().getEnchantments();
            Iterator it = itemEnchants.entrySet().iterator();
            int i = 0;
            while (it.hasNext()) {
                // get the pair
                Map.Entry pair = (Map.Entry)it.next();
                if (pair.getKey().toString().contains("silk_touch") && pair.getValue().toString().equals("2")){
                    EntityType t = ((CreatureSpawner) event.getBlock().getState()).getSpawnedType();
                    ItemStack item = new ItemStack(Material.SPAWNER);
                    ItemMeta meta = item.getItemMeta();
                    assert meta != null;
                    meta.setLore(Collections.singletonList(t.name()));
                    if (t.name().contains("_")){
                        String[] arr = t.name().split("_");
                        meta.setDisplayName(WordUtils.capitalizeFully(arr[0]) + " " + WordUtils.capitalizeFully(arr[1]) + " Spawner");
                    } else {
                        meta.setDisplayName(WordUtils.capitalizeFully(t.name()) + " Spawner");
                    }
                    item.setItemMeta(meta);
                    event.getPlayer().getInventory().addItem(item);
                    break;
                }
            }


        }
    }

    public static void setSpawnerOnPlace(BlockPlaceEvent event, Economy econ){
        if(event.getItemInHand().getItemMeta().getLore() != null && event.getItemInHand().getType() == Material.SPAWNER) {
            OfflinePlayer player = null;
            for (OfflinePlayer offlinePlayer : Bukkit.getWhitelistedPlayers()){
                if (event.getPlayer().getName().equalsIgnoreCase(offlinePlayer.getName())){
                    player = offlinePlayer;
                }
            }
            if (econ.getBalance(player) >= 5000) {
                EntityType e = EntityType.valueOf(event.getItemInHand().getItemMeta().getLore().get(0));
                CreatureSpawner cs = (CreatureSpawner) event.getBlock().getState();
                cs.setSpawnedType(e);
                cs.update();
                econ.withdrawPlayer(player, 5000);
            } else {
                event.getPlayer().sendMessage(ChatColor.RED + "You need at least $5000 to place a spawner");
                event.setCancelled(true);
            }
        }
    }
}
