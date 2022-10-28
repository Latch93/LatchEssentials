package lmp.listeners.playerCommandPreprocessEvents;

import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.io.File;

public class DenyBackCommandIntoBossArenaEvent implements Listener {

    public DenyBackCommandIntoBossArenaEvent(Main plugin) { plugin.getServer().getPluginManager().registerEvents(this, plugin);}

    @EventHandler
    private void denyBackIntoBossArena(PlayerCommandPreprocessEvent e) {
        String command = e.getMessage().split(" ")[0];
        if (command.equalsIgnoreCase("/back")){
            Player player = e.getPlayer();
            File playerDataFile = new File("plugins/Essentials/userdata", player.getUniqueId() + ".yml");
            FileConfiguration playerDataCfg = YamlConfiguration.loadConfiguration(playerDataFile);
            Location lastLocation = new Location(Bukkit.getWorld("world"), playerDataCfg.getDouble("lastlocation.x"), playerDataCfg.getDouble("lastlocation.y"), playerDataCfg.getDouble("lastlocation.z"));
            if (player.getWorld().equals(Bukkit.getWorld("world"))) {
                FileConfiguration bossCfg = Api.getFileConfiguration(YmlFileNames.YML_BOSS_FILE_NAME);
                if (lastLocation.getBlockX() >= bossCfg.getInt("minArenaX") && lastLocation.getBlockX() <= -2798) {
                    if (lastLocation.getBlockY() >= 57 && lastLocation.getBlockY() <= 83) {
                        if (lastLocation.getBlockZ() >= 32884 && lastLocation.getBlockZ() <= 33023) {
                            e.setCancelled(true);
                            player.sendMessage(ChatColor.RED + "You can't use /back into the arena. Use /warp coliseum to collect your things.");
                        }
                    }
                }
            }
        }
    }
}
