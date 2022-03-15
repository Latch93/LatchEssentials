package discord_text;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;

public class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getLogger().info("discord_text is enabled");
        try {
            new LatchDiscord();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("discord_text is disabled");
        LatchDiscord.sendServerStoppedMessage();
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        LatchDiscord.sendPlayerOnJoinMessage(event);
        LatchDiscord.setChannelDescription(false);
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent event) {
        LatchDiscord.sendPlayerLogoutMessage(event);
        LatchDiscord.setChannelDescription(true);
    }

    @EventHandler
    public void onPlayerMessage(AsyncPlayerChatEvent event) {
        LatchDiscord.logPlayerMessage(event);
    }

}

