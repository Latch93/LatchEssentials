package lmp.listeners.playerInteractEvents;

import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class EndBoatRace implements Listener {

    public EndBoatRace(Main plugin) { plugin.getServer().getPluginManager().registerEvents(this, plugin);}

    @EventHandler
    public void endBoatRace(PlayerInteractEvent e) throws IOException {
        FileConfiguration boatRaceCfg = Api.getFileConfiguration(YmlFileNames.YML_BOAT_RACE_FILE_NAME);
        Player player = e.getPlayer();
        if (Boolean.TRUE.equals(boatRaceCfg.getBoolean("isRaceActive")) && Objects.requireNonNull(boatRaceCfg.getString("racer.uuid")).equalsIgnoreCase(player.getUniqueId().toString()) && e.getClickedBlock() != null && e.getClickedBlock().getType().equals(Material.OAK_BUTTON)) {
            int buttonX = boatRaceCfg.getInt("end.x");
            int buttonY = boatRaceCfg.getInt("end.y");
            int buttonZ = boatRaceCfg.getInt("end.z");
            int buttonClickedX = e.getClickedBlock().getX();
            int buttonClickedY = e.getClickedBlock().getY();
            int buttonClickedZ = e.getClickedBlock().getZ();
            if (buttonX == buttonClickedX && buttonY == buttonClickedY && buttonZ == buttonClickedZ) {
                long startTime = boatRaceCfg.getLong("startTime");
                long endTime = DateTime.now().getMillis();
                long lapTime = endTime - startTime;
                int lapTimeMinutes = (int) TimeUnit.MILLISECONDS.toMinutes(lapTime);
                int lapTimeSeconds = (int) (TimeUnit.MILLISECONDS.toSeconds(lapTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(lapTime)));
                int lapTimeMilliseconds = (int) (TimeUnit.MILLISECONDS.toMillis(lapTime) - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(lapTime)));
                String timeStamp = String.format("%02d:%02d:%02d", lapTimeMinutes, lapTimeSeconds, lapTimeMilliseconds);
                player.sendMessage(ChatColor.GREEN + "Final Time: " + ChatColor.GOLD + timeStamp);
                player.sendMessage(ChatColor.GREEN + "Finished the lap in " + lapTimeMinutes + " minutes, " + lapTimeSeconds + " seconds, " + lapTimeMilliseconds + " milliseconds");
                boatRaceCfg.set("isRaceActive", false);
                List<String> pastRacerUUIDList = boatRaceCfg.getStringList("pastTimesUUIDList");
                List<String> pastRacerTimeList = boatRaceCfg.getStringList("pastTimesTimeList");
                pastRacerTimeList.add(String.valueOf(lapTime));
                pastRacerUUIDList.add(player.getUniqueId().toString());



                boatRaceCfg.save(Api.getConfigFile(YmlFileNames.YML_BOAT_RACE_FILE_NAME));
            }
        }
    }
}
