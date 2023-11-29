package lmp.listeners.playerBreakBlockEvents;

import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BanThiefForBreakingBanChest implements Listener {

    public BanThiefForBreakingBanChest(Main plugin) { plugin.getServer().getPluginManager().registerEvents(this, plugin);}

    private void addBlockBrokenToDB(BlockBreakEvent e){
        Main.log.info("ASDASDKASDMNSADMNAS");
        FileConfiguration enabledEventsCfg = Api.getFileConfiguration(YmlFileNames.YML_ENABLED_EVENTS_FILE_NAME);
        if (enabledEventsCfg.getBoolean("banPlayerFromTakingFromBanChest")) {
            FileConfiguration configCfg = Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME);
            Location chestLocation = new Location(e.getBlock().getWorld(), configCfg.getDouble("banChest.x"), configCfg.getDouble("banChest.y"), configCfg.getDouble("banChest.z"));
            if (chestLocation.equals(e.getBlock().getLocation()) && !e.getPlayer().hasPermission("group.mod")){
                Api.banAndLogBigBoiThief(e.getPlayer(), e.getPlayer().getItemOnCursor(), true);
                e.setCancelled(true);
            }
        }
    }

}