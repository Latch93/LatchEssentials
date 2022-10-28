package lmp.listeners.playerDeathEvents;

import lmp.Constants;
import lmp.LatchDiscord;
import lmp.Main;
import lmp.api.Api;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Objects;

public class DeathByJerryEvent implements Listener {

    public DeathByJerryEvent(Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void sendDeathByJerryMessageToDiscord(PlayerDeathEvent e){
        if (Objects.requireNonNull(e.getDeathMessage()).contains("Super Jerry")) {
            Player player = e.getEntity();
            Objects.requireNonNull(LatchDiscord.getJDA().getTextChannelById(Constants.DISCORD_STAFF_CHAT_CHANNEL_ID)).sendMessage("<@" + Api.getDiscordIdFromMCid(player.getUniqueId().toString()) + "> - AKA: " + player.getName() + " tried to hurt Super Jerry.").queue();
        }
    }
}
