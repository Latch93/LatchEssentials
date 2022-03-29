package discord_text;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

public class QuickSmelt {
    public static void quickSmelt(Player player, Economy econ, PlayerInteractEvent event){
        if (event.getPlayer().getInventory().getItemInMainHand().getEnchantments().toString().contains("mending") && event.getAction().toString().equals("LEFT_CLICK_BLOCK") && event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.STICK) && event.getClickedBlock().getType().equals(Material.FURNACE)){
            double totalExp = ExperienceManager.getTotalXP(player.getLevel());
            double totalXPToNextLevel = ExperienceManager.getTotalXpRequiredForNextLevel(player.getLevel());
            double totalPlayerXPCalculated = Math.round((totalXPToNextLevel * player.getExp()) + totalExp);
            if (totalPlayerXPCalculated >= 5){
                Furnace furnace = (Furnace) Objects.requireNonNull(event.getClickedBlock()).getState();
                if (furnace.getInventory().getSmelting() == null){
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "No items available to smelt"));
                } else if (furnace.getInventory().getResult().getAmount() == 64) {
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Smelted items in furnace are full. Please remove them to use Quick Smelt Stick"));
                } else if (furnace.getCookTime() == 0) {
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Error has occurred. Double check furnace contents"));
                } else {
                    furnace.setCookTimeTotal(80);
                    furnace.setCookTime((short) 79);
                    furnace.update(true);
                    player.setLevel(ExperienceManager.getPlayerLevel(event, 5));
                    player.setExp(ExperienceManager.getPlayerXP(event));

                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "Smelted " + ChatColor.GOLD + "1 " + Objects.requireNonNull(furnace.getInventory().getResult()).getType() + ChatColor.GREEN + " --- Remaining XP: " + ChatColor.GOLD + (totalPlayerXPCalculated - 5)));
                }
            } else {
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("You don't have enough XP to quick smelt"));
            }
        }
    }
}
