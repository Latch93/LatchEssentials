package lmp.listeners.vehicleExitEvents;

import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;

import java.io.IOException;

public class PreventRacerFromLeavingBoat implements Listener {

    public PreventRacerFromLeavingBoat(Main plugin) { plugin.getServer().getPluginManager().registerEvents(this, plugin);}

    @EventHandler
    public void preventPlayerFromLeavingBoat(VehicleExitEvent e) throws IOException {
        FileConfiguration boatRaceCfg = Api.getFileConfiguration(YmlFileNames.YML_BOAT_RACE_FILE_NAME);
        Boolean isRaceActive = boatRaceCfg.getBoolean("isRaceActive");
        if (e.getVehicle().getType().equals(EntityType.BOAT) && Boolean.TRUE.equals(isRaceActive)) {
            String playerUUID = e.getExited().getUniqueId().toString();
            String racerID = boatRaceCfg.getString("racer.uuid");
            assert racerID != null;
            if (racerID.equalsIgnoreCase(playerUUID)) {
                e.setCancelled(true);
            }
        }
    }
}
