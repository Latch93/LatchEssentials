package lmp.listeners.entityDeathEvents;

import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.io.IOException;
import java.util.Date;

public class LogVillagerDeathEvent implements Listener {
    public LogVillagerDeathEvent(Main plugin) { plugin.getServer().getPluginManager().registerEvents(this, plugin);}

    @EventHandler
    private void logVillagerDeathEvent(EntityDeathEvent e) throws IOException {
        FileConfiguration enabledEventsCfg = Api.getFileConfiguration(YmlFileNames.YML_ENABLED_EVENTS_FILE_NAME);
        if (enabledEventsCfg.getBoolean("enableLoggingOfVillagerDeaths") && e.getEntity().getType().equals(EntityType.VILLAGER)) {
            FileConfiguration villagerHurtLog = Api.getFileConfiguration(YmlFileNames.YML_VILLAGER_HURT_LOG_FILE_NAME);
            Date date = new Date();
            Villager villager = (Villager) e.getEntity();
            String id = "other";
            if (e.getEntity().getKiller() != null) {
                id = e.getEntity().getKiller().getUniqueId().toString();
                villagerHurtLog.set(id + ".playerName", e.getEntity().getKiller().getName());
            }
            villagerHurtLog.set(id + "." + date + ".type", villager.getProfession().toString());
            villagerHurtLog.set(id + "." + date + ".location.x", e.getEntity().getLocation().getBlockX());
            villagerHurtLog.set(id + "." + date + ".location.y", e.getEntity().getLocation().getBlockY());
            villagerHurtLog.set(id + "." + date + ".location.z", e.getEntity().getLocation().getBlockZ());
            villagerHurtLog.save(Api.getConfigFile(YmlFileNames.YML_VILLAGER_HURT_LOG_FILE_NAME));
        }
    }
}
