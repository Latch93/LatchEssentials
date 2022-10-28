package lmp.listeners.playerInteractEntityEvents;

import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class GiveJerryArmorEvent implements Listener {

    public GiveJerryArmorEvent(Main plugin) { plugin.getServer().getPluginManager().registerEvents(this, plugin);}

    @EventHandler
    private void onPlayerInteractEntityEvent(PlayerInteractEntityEvent e) {
        FileConfiguration bossCfg = Api.getFileConfiguration(YmlFileNames.YML_BOSS_FILE_NAME);
        if (e.getRightClicked().getType() == EntityType.GIANT) {
            Monster monster = (Monster) e.getRightClicked();
            String boss = "bosses.zombieBoss";
            Objects.requireNonNull(monster.getEquipment()).setHelmet(new ItemStack(Objects.requireNonNull(bossCfg.getItemStack(boss + ".bossHelmet"))));
            Objects.requireNonNull(monster.getEquipment()).setChestplate(Objects.requireNonNull(bossCfg.getItemStack(boss + ".bossChestplate")));
            Objects.requireNonNull(monster.getEquipment()).setBoots(Objects.requireNonNull(bossCfg.getItemStack(boss + ".bossBoots")));
            Objects.requireNonNull(monster.getEquipment()).setLeggings(Objects.requireNonNull(bossCfg.getItemStack(boss + ".bossLeggings")));
            Objects.requireNonNull(monster.getEquipment()).setItemInMainHand(Objects.requireNonNull(bossCfg.getItemStack(boss + ".bossWeapon")));
        }
    }
}
