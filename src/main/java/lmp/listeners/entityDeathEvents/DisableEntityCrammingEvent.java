package lmp.listeners.entityDeathEvents;

import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class DisableEntityCrammingEvent implements Listener {

    public DisableEntityCrammingEvent(Main plugin) { plugin.getServer().getPluginManager().registerEvents(this, plugin);}

    @EventHandler
    private void disableEntityDropsAndXPWhenCrammed(EntityDeathEvent e){
        FileConfiguration enabledEventsCfg = Api.getFileConfiguration(YmlFileNames.YML_ENABLED_EVENTS_FILE_NAME);
        if (enabledEventsCfg.getBoolean("disableDropsAndXPOnEntityCram") && e.getEntity().getLastDamageCause() != null && e.getEntity().getLastDamageCause().getCause().equals(EntityDamageEvent.DamageCause.CRAMMING)) {
            e.setDroppedExp(0);
            e.getDrops().clear();
        }
    }
}
