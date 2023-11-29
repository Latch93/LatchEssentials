package lmp.listeners.playerInteractEvents;

import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.text.DecimalFormat;
import java.util.Objects;

public class BedrockBreakerEvent implements Listener {

    public BedrockBreakerEvent(Main plugin) { plugin.getServer().getPluginManager().registerEvents(this, plugin);}

    @EventHandler (
            priority = EventPriority.LOWEST
    )
    public void BreakBedrockWithBedRockBreaker(PlayerInteractEvent event){
        if (event.getClickedBlock() != null && event.getClickedBlock().getType().equals(Material.BEDROCK)) {
            if (Boolean.TRUE.equals(event.getPlayer().getInventory().getItemInMainHand().hasItemMeta()) && Boolean.TRUE.equals(Objects.requireNonNull(event.getPlayer().getInventory().getItemInMainHand().getItemMeta()).hasLore())) {
                if (Objects.requireNonNull(event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getLore()).get(0).equalsIgnoreCase("Bedrock Breaker")) {
                    Block bedrockToBreak = event.getClickedBlock();
                    double playerBalance = Api.getEconomy().getBalance(Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId()));
                    if (playerBalance >= Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME).getDouble("bedrockBreakerCost")) {
                        DecimalFormat df = new DecimalFormat("0.00");
                        bedrockToBreak.setType(Material.AIR);
                        Api.getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId()), Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME).getDouble("bedrockBreakerCost"));
                        event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + lmp.Constants.YML_YOUR_NEW_BALANCE_IS + ChatColor.GOLD + "$" + df.format(Api.getEconomy().getBalance(Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId())))));
                    } else {
                        event.getPlayer().sendMessage(ChatColor.RED + "You need at least " + ChatColor.GOLD + "$" + Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME).getDouble("bedrockBreakerCost") + ChatColor.RED + " to break a block of bedrock.");
                    }
                }
            }
        }
    }
}
