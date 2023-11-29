package lmp.listeners.playerInteractEvents;

import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.joda.time.DateTime;

import java.io.IOException;

public class StartBoatRace implements Listener {

    public StartBoatRace(Main plugin) { plugin.getServer().getPluginManager().registerEvents(this, plugin);}

    @EventHandler
    public void startBoatRace(PlayerInteractEvent e) throws IOException {
        FileConfiguration boatRaceCfg = Api.getFileConfiguration(YmlFileNames.YML_BOAT_RACE_FILE_NAME);
        Player player = e.getPlayer();
        if (e.getClickedBlock() != null && e.getClickedBlock().getType().equals(Material.OAK_BUTTON)) {
            if (Boolean.TRUE.equals(boatRaceCfg.getBoolean("isRaceActive"))){
                player.sendMessage(ChatColor.YELLOW + "Someone is using the track. Please wait your turn.");
            } else {
                int buttonX = boatRaceCfg.getInt("start.x");
                int buttonY = boatRaceCfg.getInt("start.y");
                int buttonZ = boatRaceCfg.getInt("start.z");
                int buttonClickedX = e.getClickedBlock().getX();
                int buttonClickedY = e.getClickedBlock().getY();
                int buttonClickedZ = e.getClickedBlock().getZ();
                if (buttonX == buttonClickedX && buttonY == buttonClickedY && buttonZ == buttonClickedZ) {
                    Location boatSpawnLocation = new Location(player.getWorld(), 31063, 63, 93085, -81, 14);
                    Boat b = (Boat) player.getWorld().spawnEntity(boatSpawnLocation, EntityType.BOAT);
                    b.addPassenger(player);
                    boatRaceCfg.set("racer.name", player.getName());
                    boatRaceCfg.set("racer.uuid", player.getUniqueId().toString());
                    boatRaceCfg.set("isRaceActive", true);
                    boatRaceCfg.set("startTime", DateTime.now().getMillis());
                    boatRaceCfg.save(Api.getConfigFile(YmlFileNames.YML_BOAT_RACE_FILE_NAME));
                    player.sendMessage(ChatColor.GREEN + "Go!!!");

                }
            }
        }
    }
}
