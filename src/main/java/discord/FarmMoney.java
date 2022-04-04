package discord;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockBreakEvent;

public class FarmMoney {
    public static void rewardMoneyFromCrops(BlockBreakEvent event, Economy econ){
        OfflinePlayer player = null;
        for (OfflinePlayer offlinePlayer : Bukkit.getWhitelistedPlayers()){
            if (event.getPlayer().getName().equalsIgnoreCase(offlinePlayer.getName())){
                player = offlinePlayer;
            }
        }
        if (event.getBlock().getBlockData().toString().contains("age=7")){
            if (event.getBlock().getType().equals(Material.WHEAT) ){
                econ.depositPlayer(player, .50);
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + Constants.YML_YOUR_NEW_BALANCE_IS + ChatColor.GOLD + "$" + econ.getBalance(player)));
            }
            if (event.getBlock().getType().equals(Material.POTATO)) {
                econ.depositPlayer(player, .50);
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + Constants.YML_YOUR_NEW_BALANCE_IS + ChatColor.GOLD + "$" + econ.getBalance(player)));
            }
            if (event.getBlock().getType().equals(Material.CARROT)) {
                econ.depositPlayer(player, .50);
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + Constants.YML_YOUR_NEW_BALANCE_IS + ChatColor.GOLD + "$" + econ.getBalance(player)));
            }
        }
        if (event.getBlock().getBlockData().toString().contains("age=3")) {
            if (event.getBlock().getType().equals(Material.BEETROOTS)) {
                econ.depositPlayer(player, .75);
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + Constants.YML_YOUR_NEW_BALANCE_IS + ChatColor.GOLD + "$" + econ.getBalance(player)));
            }
            if (event.getBlock().getType().equals(Material.NETHER_WART)) {
                econ.depositPlayer(player, 1.50);
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + Constants.YML_YOUR_NEW_BALANCE_IS + ChatColor.GOLD + "$" + econ.getBalance(player)));
            }
            if (event.getBlock().getType().equals(Material.SWEET_BERRY_BUSH)) {
                econ.depositPlayer(player, 1.25);
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + Constants.YML_YOUR_NEW_BALANCE_IS + ChatColor.GOLD + "$" + econ.getBalance(player)));
            }
        }

        if (event.getBlock().getType().equals(Material.MELON)) {
            if (event.getBlock().getRelative(BlockFace.EAST).getType().equals(Material.ATTACHED_MELON_STEM) ||
                    event.getBlock().getRelative(BlockFace.WEST).getType().equals(Material.ATTACHED_MELON_STEM) ||
                        event.getBlock().getRelative(BlockFace.SOUTH).getType().equals(Material.ATTACHED_MELON_STEM) ||
                            event.getBlock().getRelative(BlockFace.NORTH).getType().equals(Material.ATTACHED_MELON_STEM)){
                econ.depositPlayer(player, 2);
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + Constants.YML_YOUR_NEW_BALANCE_IS + ChatColor.GOLD + "$" + econ.getBalance(player)));
            }

        }
        if (event.getBlock().getType().equals(Material.PUMPKIN)) {
            if (event.getBlock().getRelative(BlockFace.EAST).getType().equals(Material.ATTACHED_PUMPKIN_STEM) ||
                    event.getBlock().getRelative(BlockFace.WEST).getType().equals(Material.ATTACHED_PUMPKIN_STEM) ||
                        event.getBlock().getRelative(BlockFace.SOUTH).getType().equals(Material.ATTACHED_PUMPKIN_STEM) ||
                            event.getBlock().getRelative(BlockFace.NORTH).getType().equals(Material.ATTACHED_PUMPKIN_STEM)){
                econ.depositPlayer(player, 2);
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + Constants.YML_YOUR_NEW_BALANCE_IS + ChatColor.GOLD + "$" + econ.getBalance(player)));
            }
        }
    }
}
