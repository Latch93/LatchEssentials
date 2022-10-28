package lmp.listeners.playerJoinEvents;

import lmp.Advancements;
import lmp.Constants;
import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;
import java.util.Iterator;

public class SetPlayerCompletedAdvancementsOnLoginEvent implements Listener {

    public SetPlayerCompletedAdvancementsOnLoginEvent(Main plugin) { plugin.getServer().getPluginManager().registerEvents(this, plugin);}

    @EventHandler
    private void setPlayerCompletedAdvancementsOnLogin(PlayerJoinEvent e) throws IOException {
        Player player = e.getPlayer();
        FileConfiguration advancementCfg = Api.loadConfig(YmlFileNames.YML_ADVANCEMENT_FILE_NAME);
        Iterator<Advancement> advancements = Bukkit.getServer().advancementIterator();
        String playerName = player.getName();
        int totalCompletedAdvancementCount = 0;
        while (advancements.hasNext()) {
            String bukkitAdvancement = advancements.next().getKey().toString();
            for (lmp.Advancement latchAdvancement : Advancements.getAdvancements()) {
                if (bukkitAdvancement.equalsIgnoreCase(latchAdvancement.getID())) {
                    AdvancementProgress progress = player.getAdvancementProgress(advancements.next());
                    if (Boolean.TRUE.equals(progress.isDone())) {
                        totalCompletedAdvancementCount++;
                    }
                }
            }
        }
        advancementCfg.set(Constants.YML_PLAYERS + playerName + ".name", playerName);
        advancementCfg.set(Constants.YML_PLAYERS + playerName + ".advancementCount", totalCompletedAdvancementCount);
        advancementCfg.set("totalAdvancements", Advancements.getAdvancements().size());
        advancementCfg.save(Api.getConfigFile(YmlFileNames.YML_ADVANCEMENT_FILE_NAME));
    }
}
