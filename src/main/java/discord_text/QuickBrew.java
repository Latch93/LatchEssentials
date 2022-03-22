package discord_text;

import javafx.application.Platform;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.Arrays;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class QuickBrew {
    public static void quickBrew(Player player, Economy econ, PlayerInteractEvent event){
        if (event.getPlayer().getInventory().getItemInMainHand().getEnchantments().toString().contains("aqua") && event.getAction().toString().equals("LEFT_CLICK_BLOCK") && event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.STICK) && event.getClickedBlock().getType().equals(Material.BREWING_STAND)){
            OfflinePlayer offlinePlayer = null;
            for (OfflinePlayer p : Bukkit.getWhitelistedPlayers()){
                if (event.getPlayer().getUniqueId().equals(p.getUniqueId())){
                    offlinePlayer = event.getPlayer();
                }
            }
            double playerBalance = econ.getBalance(player);
            if (playerBalance >= 50){
                BrewingStand brewingStand = (BrewingStand) Objects.requireNonNull(event.getClickedBlock()).getState();
                if (brewingStand.getInventory().getIngredient() == null){
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "No items available to brew"));
                } else if (brewingStand.getBrewingTime() == 0) {
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Unable to brew ingredients"));
                } else {
                    brewingStand.setBrewingTime(1);
                    brewingStand.update(true);
                    econ.withdrawPlayer(offlinePlayer, 50);
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "Your potion(s) are brewed. Remaining balance: " + ChatColor.GOLD + "$" + econ.getBalance(offlinePlayer)));
                }
            } else {
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("You don't have enough money to quick brew"));
            }
        }
    }
}
