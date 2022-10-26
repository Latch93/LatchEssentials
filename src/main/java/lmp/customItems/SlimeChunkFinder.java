package lmp.customItems;

import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

public class SlimeChunkFinder {
    public static void isSlimeChunk(PlayerInteractEvent e){
        if (e.getPlayer().getInventory().getItemInMainHand().getType().toString().equalsIgnoreCase("SLIME_BLOCK") &&
            e.getPlayer().getInventory().getItemInMainHand().getEnchantments().toString().contains("MENDING")){
            try {
                if (Boolean.TRUE.equals(Objects.requireNonNull(e.getClickedBlock()).getLocation().getChunk().isSlimeChunk())){
                    e.getPlayer().sendMessage(ChatColor.GREEN + "You clicked in a Slime Chunk!!!");
                } else {
                    e.getPlayer().sendMessage(ChatColor.YELLOW + "You did not click in a Slime Chunk.");
                }
            } catch (NullPointerException err){
                e.getPlayer().sendMessage(ChatColor.RED + "You must click on a block, not air. Try Again.");
            }
            e.setCancelled(true);
        }
    }
}
