package lmp.listeners.autosort.inventoryMoveItemEvents;

import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SendItemToOutputChestFromMasterChestEvent implements Listener {

    public SendItemToOutputChestFromMasterChestEvent(Main plugin) { plugin.getServer().getPluginManager().registerEvents(this, plugin);}

    @EventHandler
    private void sendItemToOutputChestFromMasterChestEvent(InventoryMoveItemEvent e) {
        try {
            Location destinationLocation = e.getDestination().getLocation();
            String playerName = "";
            boolean isSameChest = false;
            FileConfiguration autoSorterCfg = Api.getFileConfiguration(YmlFileNames.YML_AUTO_SORTER_FILE_NAME);
            for (String player : autoSorterCfg.getKeys(false)) {
                Location masterChestLocation = new Location(Bukkit.getWorld("world"), autoSorterCfg.getDouble(player + ".masterChest.x"), autoSorterCfg.getDouble(player + ".masterChest.y"), autoSorterCfg.getDouble(player + ".masterChest.z"));
                if (destinationLocation.equals(masterChestLocation)) {
                    playerName = player;
                    isSameChest = true;
                    break;
                }
            }
            if (Boolean.TRUE.equals(isSameChest)) {
                Inventory masterChestInventory = e.getDestination();
                int count = 0;
                boolean itemsSent = false;
                for (ItemStack is : masterChestInventory) {
                    if (is != null && autoSorterCfg.isSet(playerName + "." + is.getType())) {
                        double chestToX = autoSorterCfg.getDouble(playerName + "." + is.getType() + ".x");
                        double chestToY = autoSorterCfg.getDouble(playerName + "." + is.getType() + ".y");
                        double chestToZ = autoSorterCfg.getDouble(playerName + "." + is.getType() + ".z");
                        Location chestSortLocation = new Location(Bukkit.getWorld("world"), chestToX, chestToY, chestToZ);
                        Material chest = chestSortLocation.getBlock().getType();
                        if (chest.equals(Material.CHEST)) {
                            Chest sortChest = (Chest) chestSortLocation.getBlock().getState();
                            sortChest.getInventory().addItem(is);
                            masterChestInventory.setItem(count, new ItemStack(Material.AIR, 1));
                            itemsSent = true;
                        }
                    }
                    count++;
                }
            }

        } catch (NullPointerException ignored) {

        }
    }
}
