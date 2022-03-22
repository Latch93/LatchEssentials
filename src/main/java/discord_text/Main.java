package discord_text;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.util.Objects;

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
    public void onPull(PlayerInteractEvent event) {
        try {
            RandomItem.getRandomItem(event);
            QuickSmelt.quickSmelt(event.getPlayer(), econ, event);
            QuickBrew.quickBrew(event.getPlayer(), econ, event);
        } catch (NullPointerException ignored){

        }


    }

    @EventHandler
    public void onPlayerChestItemRemove(InventoryClickEvent event) {
        LatchDiscord.banPlayerStealing(event);
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


}

