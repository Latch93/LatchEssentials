package lmp.listeners.discord;

import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Objects;

public class BanAndLogBanChestThief implements Listener {

    public BanAndLogBanChestThief(Main plugin) { plugin.getServer().getPluginManager().registerEvents(this, plugin);}

    @EventHandler
    public static void banPlayerStealing(InventoryClickEvent event) {
        FileConfiguration enabledEventsCfg = Api.getFileConfiguration(YmlFileNames.YML_ENABLED_EVENTS_FILE_NAME);
        if (enabledEventsCfg.getBoolean("banPlayerFromTakingFromBanChest")) {
            String playerName = event.getWhoClicked().getName();
            FileConfiguration configCfg = Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME);
            Location chestLocation = new Location(event.getWhoClicked().getWorld(), configCfg.getDouble("banChest.x"), configCfg.getDouble("banChest.y"), configCfg.getDouble("banChest.z"));
            String chestMaterial = "";
            try {
                if (event.getClickedInventory() != null) {
                    chestMaterial = Objects.requireNonNull(event.getClickedInventory()).getType().toString();
                    if (chestMaterial.equalsIgnoreCase("CHEST") && chestLocation.equals(event.getClickedInventory().getLocation())) {
                        if (event.getCurrentItem() != null && !event.getWhoClicked().hasPermission("group.mod")) {
                            Api.banAndLogBigBoiThief((Player) event.getWhoClicked(), event.getCurrentItem(), false);
                        }
                    }
                }
            } catch (NullPointerException error) {
                Main.log.warning("Error occurred when: " + playerName + " stole from BigBoi's chest. ");
            }
        }

    }
}
