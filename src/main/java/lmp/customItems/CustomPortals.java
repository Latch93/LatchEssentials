package lmp.customItems;

import lmp.api.Api;
import lmp.constants.YmlFileNames;
import org.bukkit.Axis;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Orientable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class CustomPortals {
    public static void setBlockToNetherPortal(PlayerInteractEvent e){
        Player player = e.getPlayer();
        if (e.getClickedBlock() != null && Boolean.TRUE.equals(isItemInHandPortalStick(player.getInventory().getItemInMainHand()))) {
            FileConfiguration configCfg = Api.getFileConfiguration(Api.getConfigFile(YmlFileNames.YML_CONFIG_FILE_NAME));

            Material blockToChange = Material.valueOf(configCfg.getString("netherPortalBlockToChange"));
            if (e.getClickedBlock().getType().equals(blockToChange)) {
                double netherPortalPlaceCost = configCfg.getDouble("portalStickCost");
                if (netherPortalPlaceCost <= Api.getEconomy().getBalance(Api.getOfflinePlayerFromPlayer(player))) {
                    Api.getEconomy().withdrawPlayer(Api.getOfflinePlayerFromPlayer(player), netherPortalPlaceCost);
                    e.getClickedBlock().setType(Material.NETHER_PORTAL);
                    e.getPlayer().sendMessage(ChatColor.GREEN + "You placed a nether portal. You remaining balance is " + ChatColor.GOLD + "$" + Api.getEconomy().getBalance(Api.getOfflinePlayerFromPlayer(player)));
                } else {
                    player.sendMessage(ChatColor.RED + "You need at least " + ChatColor.GOLD + "$" + netherPortalPlaceCost + ChatColor.RED + " to place a nether portal here.");
                }


            } else if (e.getClickedBlock().getType().equals(Material.NETHER_PORTAL)){
                if (Boolean.TRUE.equals(e.getPlayer().isSneaking())){
                    BlockData blockData = e.getClickedBlock().getBlockData();
                    if(blockData instanceof Orientable) {
                        Orientable orientation = (Orientable) blockData;
                        orientation.setAxis(Axis.Z);
                    }
                    e.getClickedBlock().setBlockData(blockData);
                } else {
                    BlockData blockData = e.getClickedBlock().getBlockData();
                    if(blockData instanceof Orientable) {
                        Orientable orientation = (Orientable) blockData;
                        orientation.setAxis(Axis.X);
                    }
                    e.getClickedBlock().setBlockData(blockData);
                }
            } else {
                player.sendMessage(ChatColor.RED + "Block to set to nether portal must be " + blockToChange + ".");
            }
        }
    }

    public static boolean isItemInHandPortalStick(ItemStack is){
        return is != null && is.getItemMeta() != null && is.getItemMeta().getLore() != null && "Custom Nether Portal Stick".equalsIgnoreCase(is.getItemMeta().getLore().get(0));

    }
}
