package lmp.customItems;

import lmp.ExperienceManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

public class QuickBrew {
    public static void quickBrew(Player player, Economy econ, PlayerInteractEvent event) {
        if (event.getPlayer().getInventory().getItemInMainHand().getEnchantments().toString().contains("aqua") && event.getAction().toString().equals("LEFT_CLICK_BLOCK") && event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.STICK) && event.getClickedBlock().getType().equals(Material.BREWING_STAND)) {
            double totalExp = ExperienceManager.getTotalXP(player.getLevel());
            double totalXPToNextLevel = ExperienceManager.getTotalXpRequiredForNextLevel(player.getLevel());
            double totalPlayerXPCalculated = Math.round((totalXPToNextLevel * player.getExp()) + totalExp);
            if (totalPlayerXPCalculated >= 5) {
                BrewingStand brewingStand = (BrewingStand) Objects.requireNonNull(event.getClickedBlock()).getState();
                if (brewingStand.getInventory().getIngredient() == null) {
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "No items available to brew"));
                } else if (brewingStand.getBrewingTime() == 0) {
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Unable to brew ingredients"));
                } else {
                    brewingStand.setBrewingTime(1);
                    brewingStand.update(true);
                    player.setLevel(ExperienceManager.getPlayerLevel(player, 35));
                    player.setExp(ExperienceManager.getPlayerXP(event.getPlayer()));
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "Your potion(s) are brewed. Remaining XP: " + ChatColor.GOLD + (totalPlayerXPCalculated - 35)));
                }
            } else {
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("You don't have enough XP to quick brew"));
            }
        }
    }
}
