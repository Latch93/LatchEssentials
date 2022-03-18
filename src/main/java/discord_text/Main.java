package discord_text;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;

public class Main extends JavaPlugin implements Listener {

    private static Economy econ = null;

    @Override
    public void onEnable() {
        getLogger().info("discord_text is enabled");
        try {
            new LatchDiscord();
        } catch (LoginException e) {
            e.printStackTrace();
        }
        getServer().getPluginManager().registerEvents(this, this);
        setupEconomy();
        this.getCommand("randomitem").setExecutor(new RandomItem());
    }

    @EventHandler
    public void test(){
        System.out.println("asda");
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

    public void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        econ = rsp.getProvider();
        setEconomy(econ);

    }

    public void setEconomy(Economy value) {
        econ = value;
    }
//    @EventHandler
//    public void onPlayerMessage(AsyncPlayerChatEvent event) {
//        LatchDiscord.logPlayerMessage(event);
//    }


}

