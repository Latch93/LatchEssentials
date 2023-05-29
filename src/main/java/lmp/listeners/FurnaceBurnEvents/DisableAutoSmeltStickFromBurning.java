package lmp.listeners.FurnaceBurnEvents;

import lmp.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;

public class DisableAutoSmeltStickFromBurning implements Listener {

    public DisableAutoSmeltStickFromBurning(Main plugin) { plugin.getServer().getPluginManager().registerEvents(this, plugin);}

    @EventHandler(priority = EventPriority.HIGHEST)
    private void disableAutoSmeltAndBrewStickFromBurning(FurnaceBurnEvent e){
        if (e.getFuel().getItemMeta() != null && e.getFuel().getItemMeta().hasLore()){
            if (e.getFuel().getItemMeta().getLore().get(0).equalsIgnoreCase("QuickSmelt")) {
                e.setCancelled(true);
            }
        }
    }
}
