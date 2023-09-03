package lmp.customItems;


import lmp.Main;
import lmp.api.Api;
import net.coreprotect.CoreProtectAPI;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

import static lmp.Main.coreProtectAPI;

public class AutoSellChest implements Listener {

    public AutoSellChest(Main plugin) { plugin.getServer().getPluginManager().registerEvents(this, plugin);}

    @EventHandler
    public static void autoSellChest(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Economy econ = Api.getEconomy();
        File worthFile = new File("plugins/Essentials", "worth.yml");
        FileConfiguration worthCfg = YamlConfiguration.loadConfiguration(worthFile);
        ItemStack is = event.getPlayer().getInventory().getItemInMainHand();
        ItemMeta im = is.getItemMeta();
        if (im != null){
            if (im.getLore() != null){
                String autoSmeltLore = im.getLore().get(0);
                if (autoSmeltLore.equalsIgnoreCase("AutoSell")){
                    if (event.getAction().toString().equals("LEFT_CLICK_BLOCK") && event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.STICK) && Objects.requireNonNull(event.getClickedBlock()).getType().equals(Material.CHEST)) {
                        Block block = event.getClickedBlock();
                        CoreProtectAPI.ParseResult result = null;
                        List<String[]> lookup = coreProtectAPI.blockLookup(block, 20024000);
                        boolean hasOwnership = false;
                        for (String[] value : lookup) {
                            result = coreProtectAPI.parseResult(value);
                            if (result.getActionString().equalsIgnoreCase("place")) {
                                if (Api.getMinecraftIdFromMinecraftName(result.getPlayer()).equalsIgnoreCase(player.getUniqueId().toString())) {
                                    hasOwnership = true;
                                }
                            }
                        }
                        DecimalFormat df = new DecimalFormat("0.00");
                        if (Boolean.TRUE.equals(hasOwnership)) {
                            Chest chest = (Chest) Objects.requireNonNull(event.getClickedBlock()).getState();
                            Inventory inv = chest.getInventory();
                            double chestWorthTotal = 0;
                            int count = 0;
                            for (ItemStack item : inv) {
                                if (item != null) {
                                    String itemString = item.getType().toString().replace("_", "").toLowerCase();
                                    if (worthCfg.getDouble("worth." + itemString) != 0) {
                                        double itemStackValue = worthCfg.getDouble("worth." + itemString) * item.getAmount();
                                        chestWorthTotal =  chestWorthTotal + itemStackValue;
                                        String material = WordUtils.capitalize(item.getType().toString().replace("_", " "));
                                        int amount = item.getAmount();
                                        player.sendMessage(ChatColor.GREEN + "" + amount + " " + material + " = $" + df.format(itemStackValue));
                                        if (Boolean.TRUE.equals(player.isSneaking())) {
                                            inv.setItem(count, new ItemStack(Material.AIR, 0));
                                        }
                                    }
                                }
                                count++;
                            }
                            if (Boolean.TRUE.equals(player.isSneaking())) {
                                if (chestWorthTotal > 0) {
                                    Api.givePlayerMoney(player.getUniqueId().toString(), chestWorthTotal);
                                    player.sendMessage(ChatColor.GREEN + "You received " + ChatColor.GOLD + "$" + df.format(chestWorthTotal) + ChatColor.GREEN + " for selling items. Your total balance is now " + ChatColor.GOLD + "$" + df.format(Api.getPlayerBalance(player)));
                                }
                            } else {
                                player.sendMessage(ChatColor.GREEN + "Sneak and click to receive " + ChatColor.GOLD + "$" + df.format(chestWorthTotal) + ChatColor.GREEN + " for selling the items in this chest.");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Can't Sell!!! This is not your chest.");
                        }
                    }
                }
            }
        }
    }
}

