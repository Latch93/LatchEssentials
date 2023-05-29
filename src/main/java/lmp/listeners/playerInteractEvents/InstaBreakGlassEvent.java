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
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

public class InstaBreakGlassEvent implements Listener {

    public InstaBreakGlassEvent(Main plugin) { plugin.getServer().getPluginManager().registerEvents(this, plugin);}

    @EventHandler
    public void BreakGlassInstantly(PlayerInteractEvent event){
        ArrayList<Material> glassBlocksThatCanBreakList = new ArrayList<>();
        glassBlocksThatCanBreakList.add(Material.GLASS);
        glassBlocksThatCanBreakList.add(Material.TINTED_GLASS);
        glassBlocksThatCanBreakList.add(Material.GRAY_STAINED_GLASS);
        glassBlocksThatCanBreakList.add(Material.GREEN_STAINED_GLASS);
        glassBlocksThatCanBreakList.add(Material.BLACK_STAINED_GLASS);
        glassBlocksThatCanBreakList.add(Material.BLUE_STAINED_GLASS);
        glassBlocksThatCanBreakList.add(Material.BROWN_STAINED_GLASS);
        glassBlocksThatCanBreakList.add(Material.CYAN_STAINED_GLASS);
        glassBlocksThatCanBreakList.add(Material.LIGHT_BLUE_STAINED_GLASS);
        glassBlocksThatCanBreakList.add(Material.LIGHT_GRAY_STAINED_GLASS);
        glassBlocksThatCanBreakList.add(Material.LIME_STAINED_GLASS);
        glassBlocksThatCanBreakList.add(Material.MAGENTA_STAINED_GLASS);
        glassBlocksThatCanBreakList.add(Material.ORANGE_STAINED_GLASS);
        glassBlocksThatCanBreakList.add(Material.PURPLE_STAINED_GLASS);
        glassBlocksThatCanBreakList.add(Material.RED_STAINED_GLASS);
        glassBlocksThatCanBreakList.add(Material.WHITE_STAINED_GLASS);
        glassBlocksThatCanBreakList.add(Material.YELLOW_STAINED_GLASS);
        glassBlocksThatCanBreakList.add(Material.PINK_STAINED_GLASS);
        if (event.getClickedBlock() != null && glassBlocksThatCanBreakList.contains(event.getClickedBlock().getType())) {
            if (Boolean.TRUE.equals(event.getPlayer().getInventory().getItemInMainHand().hasItemMeta()) && Boolean.TRUE.equals(Objects.requireNonNull(event.getPlayer().getInventory().getItemInMainHand().getItemMeta()).hasLore())) {
                if (Objects.requireNonNull(event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getLore()).get(0).equalsIgnoreCase("Glass Breaker")) {
                    Block glassToBreak = event.getClickedBlock();
                    double playerBalance = Api.getEconomy().getBalance(Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId()));
                    if (playerBalance >= Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME).getDouble("glassBreakerCost")) {
                        DecimalFormat df = new DecimalFormat("0.00");
                        Api.getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId()), Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME).getDouble("glassBreakerCost"));
                        event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + lmp.Constants.YML_YOUR_NEW_BALANCE_IS + ChatColor.GOLD + "$" + df.format(Api.getEconomy().getBalance(Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId())))));
                        event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), new ItemStack(event.getClickedBlock().getType(), 1));
                        glassToBreak.setType(Material.AIR);
                    } else {
                        event.getPlayer().sendMessage(ChatColor.RED + "You need at least " + ChatColor.GOLD + "$" + Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME).getDouble("glassBreakerCost") + ChatColor.RED + " to break a block of glass.");
                    }
                }
            }
        }
    }
}
