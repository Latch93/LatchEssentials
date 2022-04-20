package discord;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.block.BlockBreakEvent;

import java.text.DecimalFormat;

public class FarmMoney {
    public static void rewardMoneyFromCrops(BlockBreakEvent event, Economy econ){
        if (!event.getPlayer().getWorld().getName().equalsIgnoreCase("season1")){
            FileConfiguration mainConfig = Main.loadConfig(Constants.YML_DISCORD_TEXT_FILE_NAME);
            OfflinePlayer player = null;
            for (OfflinePlayer offlinePlayer : Bukkit.getWhitelistedPlayers()){
                if (event.getPlayer().getName().equalsIgnoreCase(offlinePlayer.getName())){
                    player = offlinePlayer;
                }
            }
            DecimalFormat df = new DecimalFormat("0.00");
            if (event.getBlock().getBlockData().toString().contains("age=7")){
                if (event.getBlock().getType().equals(Material.WHEAT) ){
                    econ.depositPlayer(player, mainConfig.getDouble("farmMoney.wheat"));
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + Constants.YML_YOUR_NEW_BALANCE_IS + ChatColor.GOLD + "$" + df.format(econ.getBalance(player))));
                }
                if (event.getBlock().getType().toString().contains("POTATO")) {
                    econ.depositPlayer(player, mainConfig.getDouble("farmMoney.potato"));
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + Constants.YML_YOUR_NEW_BALANCE_IS + ChatColor.GOLD + "$" + df.format(econ.getBalance(player))));
                }
                if (event.getBlock().getType().toString().contains("CARROT")) {
                    econ.depositPlayer(player, mainConfig.getDouble("farmMoney.carrot"));
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + Constants.YML_YOUR_NEW_BALANCE_IS + ChatColor.GOLD + "$" + df.format(econ.getBalance(player))));
                }
            }
            if (event.getBlock().getBlockData().toString().contains("age=3")) {
                if (event.getBlock().getType().toString().contains("BEETROOT")) {
                    econ.depositPlayer(player, mainConfig.getDouble("farmMoney.beetroot"));
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + Constants.YML_YOUR_NEW_BALANCE_IS + ChatColor.GOLD + "$" + df.format(econ.getBalance(player))));
                }
                if (event.getBlock().getType().equals(Material.NETHER_WART)) {
                    econ.depositPlayer(player, mainConfig.getDouble("farmMoney.netherwart"));
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + Constants.YML_YOUR_NEW_BALANCE_IS + ChatColor.GOLD + "$" + df.format(econ.getBalance(player))));
                }
                if (event.getBlock().getType().equals(Material.SWEET_BERRY_BUSH)) {
                    econ.depositPlayer(player, mainConfig.getDouble("farmMoney.sweet_berry_bush"));
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + Constants.YML_YOUR_NEW_BALANCE_IS + ChatColor.GOLD + "$" + df.format(econ.getBalance(player))));
                }
            }

            if (event.getBlock().getType().equals(Material.MELON)) {
                if (event.getBlock().getRelative(BlockFace.EAST).getType().equals(Material.ATTACHED_MELON_STEM) ||
                        event.getBlock().getRelative(BlockFace.WEST).getType().equals(Material.ATTACHED_MELON_STEM) ||
                        event.getBlock().getRelative(BlockFace.SOUTH).getType().equals(Material.ATTACHED_MELON_STEM) ||
                        event.getBlock().getRelative(BlockFace.NORTH).getType().equals(Material.ATTACHED_MELON_STEM)){
                    econ.depositPlayer(player, mainConfig.getDouble("farmMoney.melon"));
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + Constants.YML_YOUR_NEW_BALANCE_IS + ChatColor.GOLD + "$" + df.format(econ.getBalance(player))));
                }

            }
            if (event.getBlock().getType().equals(Material.PUMPKIN)) {
                if (event.getBlock().getRelative(BlockFace.EAST).getType().equals(Material.ATTACHED_PUMPKIN_STEM) ||
                        event.getBlock().getRelative(BlockFace.WEST).getType().equals(Material.ATTACHED_PUMPKIN_STEM) ||
                        event.getBlock().getRelative(BlockFace.SOUTH).getType().equals(Material.ATTACHED_PUMPKIN_STEM) ||
                        event.getBlock().getRelative(BlockFace.NORTH).getType().equals(Material.ATTACHED_PUMPKIN_STEM)){
                    econ.depositPlayer(player, mainConfig.getDouble("farmMoney.pumpkin"));
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + Constants.YML_YOUR_NEW_BALANCE_IS + ChatColor.GOLD + "$" + df.format(econ.getBalance(player))));
                }
            }
        }

    }
}
