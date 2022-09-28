package lmp;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.Objects;

public class QuickSmelt {
    public static void quickSmelt(Player player, Economy econ, PlayerInteractEvent event){
        if (event.getPlayer().getInventory().getItemInMainHand().getEnchantments().toString().contains("mending") && event.getAction().toString().equals("LEFT_CLICK_BLOCK") && event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.STICK) && event.getClickedBlock().getType().equals(Material.FURNACE)){
            double playerBalance = Api.getEconomy().getBalance(Api.getOfflinePlayerFromPlayer(player));

            if (playerBalance >= 5.0){
                Furnace furnace = (Furnace) Objects.requireNonNull(event.getClickedBlock()).getState();
                if (furnace.getInventory().getSmelting() == null){
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "No items available to smelt"));
                } else if (furnace.getInventory().getResult() != null && furnace.getInventory().getResult().getAmount() == 64) {
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Smelted items in furnace are full. Please remove them to use Quick Smelt Stick"));
                } else if (furnace.getCookTime() == 0) {
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Error has occurred. Double check furnace contents"));
                } else {
                    DecimalFormat df = new DecimalFormat("0.00");
                    if (Boolean.TRUE.equals(player.isSneaking())){
                        ItemStack result = furnace.getInventory().getResult();
                        int amountToSmelt = furnace.getInventory().getSmelting().getAmount();
                        if (playerBalance >= (amountToSmelt * 5)){
                            furnace.getInventory().setResult(new ItemStack(result.getType(), amountToSmelt + furnace.getInventory().getResult().getAmount()));
                            furnace.getInventory().setSmelting(null);
                            Api.getEconomy().withdrawPlayer(Api.getOfflinePlayerFromPlayer(player), 5 * amountToSmelt);
                            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "Smelted " + ChatColor.GOLD + amountToSmelt + " " + Objects.requireNonNull(furnace.getInventory().getResult()).getType() + ChatColor.GREEN + " --- Balance: " + ChatColor.GOLD + df.format(Api.getEconomy().getBalance(Api.getOfflinePlayerFromPlayer(player)))));
                        }
                    } else {
                        furnace.setCookTimeTotal(80);
                        furnace.setCookTime((short) 79);
                        furnace.update(true);
                        Api.getEconomy().withdrawPlayer(Api.getOfflinePlayerFromPlayer(player), 5);
                        event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "Smelted " + ChatColor.GOLD + "1 " + Objects.requireNonNull(furnace.getInventory().getResult()).getType() + ChatColor.GREEN + " --- Balance: " + ChatColor.GOLD + df.format(Api.getEconomy().getBalance(Api.getOfflinePlayerFromPlayer(player)))));
                    }
                }
            } else {
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("You don't have enough XP to quick smelt"));
            }
        }
    }
}
