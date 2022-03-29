package discord_text;

import discord_text.Backbacks.BackPackCommand;
import discord_text.Backbacks.BackPackInventoryConfig;
import discord_text.Backbacks.BackPacks;
import discord_text.Backbacks.BackpackTabComplete;
import discord_text.PlayerShops.PlayerShops;
import discord_text.PlayerShops.PlayerShopsCommand;
import discord_text.PlayerShops.PlayerShopsInventoryConfig;
import discord_text.PlayerShops.PlayerShopsTabComplete;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Main extends JavaPlugin implements Listener {
    public static final boolean IS_TESTING = false;
    public static Economy econ = null;
    public static BackPackInventoryConfig backPackInventoryCfgm;
    public static PlayerShopsInventoryConfig playerShopsInventoryCfgm;
    public static Inventory inv;

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
        loadBackpackManager();
        loadPlayerShopsManager();

        // Backpack Commands
        Objects.requireNonNull(this.getCommand("bp")).setExecutor(new BackPackCommand());
        Objects.requireNonNull(this.getCommand("bp")).setTabCompleter(new BackpackTabComplete());

        // Player Shop Commands
        Objects.requireNonNull(this.getCommand("ps")).setExecutor(new PlayerShopsCommand());
        Objects.requireNonNull(this.getCommand("ps")).setTabCompleter(new PlayerShopsTabComplete());

    }

    public static void loadBackpackManager() {
        backPackInventoryCfgm = new BackPackInventoryConfig();
        backPackInventoryCfgm.setup();
    }

    public static void loadPlayerShopsManager() {
        playerShopsInventoryCfgm = new PlayerShopsInventoryConfig();
        playerShopsInventoryCfgm.setup();
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
            MobileSpawner.disableSpawnerMobChange(event);
        } catch (NullPointerException ignored){

        }
    }


    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) throws IOException {
        Player player = (Player) e.getPlayer();
        if (e.getView().getTitle().equals(player.getName() + Constants.YML_POSSESSIVE_BACKPACK)){
            BackPacks.saveBackPack(e);
        }
        if (e.getView().getTitle().contains(Constants.YML_POSSESSIVE_PLAYER_SHOP)){
            PlayerShops.savePlayerShop(e);
        }
    }

    @EventHandler
    public void onPlayerInventoryClick(InventoryClickEvent e) throws IOException {
        Player player = (Player) e.getWhoClicked();
        File playerShopFile = new File(Main.getPlugin(Main.class).getDataFolder(), "playerShops.yml");
        FileConfiguration playerShopCfg = YamlConfiguration.loadConfiguration(playerShopFile);
        if (e.getView().getTitle().equals(player.getName() + Constants.YML_POSSESSIVE_BACKPACK)){
            if (e.getCurrentItem() != null){
                PlayerShops.illegalPlayerShopItems(e, player);
            }
        }
        if (e.getView().getTitle().equals(player.getName() + Constants.YML_POSSESSIVE_PLAYER_SHOP)){
            if (e.getCurrentItem() != null){
                PlayerShops.itemWorthNotSet(e, player, playerShopCfg);
                PlayerShops.illegalPlayerShopItems(e, player);
            }
        } else if (e.getView().getTitle().contains(Constants.YML_POSSESSIVE_PLAYER_SHOP)) {
            if (e.getCurrentItem() != null) {
                PlayerShops.sd(e, econ, player);
            }
        }
    }

    public static Inventory getCustomInventory(Player player){
        File configFile = new File(Main.getPlugin(Main.class).getDataFolder(), "playerBackpack.yml");
        FileConfiguration configCfg = YamlConfiguration.loadConfiguration(configFile);
        if (configCfg.get(player.getUniqueId() + ".slots") != null) {
            int numberOfSlots = configCfg.getInt(player.getUniqueId() + Constants.YML_SIZE);
            inv = Bukkit.createInventory(null, numberOfSlots, player.getName() + Constants.YML_POSSESSIVE_BACKPACK);
            for(String users : configCfg.getConfigurationSection(player.getUniqueId() + ".slots").getKeys(false)) {
                inv.setItem(Integer.parseInt(users), new ItemStack(Material.valueOf(configCfg.getString(player.getUniqueId() + ".slots." + users + ".material")), Integer.parseInt(configCfg.getString(player.getUniqueId() + ".slots." + users + ".amount"))));
            }
        }
        if (configCfg.get(player.getUniqueId() + Constants.YML_SIZE) != null){
            inv = Bukkit.createInventory(null, configCfg.getInt(player.getUniqueId() + Constants.YML_SIZE), player.getName() + Constants.YML_POSSESSIVE_BACKPACK);
        } else {
            inv = Bukkit.createInventory(null, 9, player.getName() + Constants.YML_POSSESSIVE_BACKPACK);
        }
        return inv;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        FarmMoney.rewardMoneyFromCrops(event, econ);
        MobileSpawner.setSpawnerOnBreak(event);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        MobileSpawner.setSpawnerOnPlace(event, econ);
    }

    @EventHandler
    public void onPlayerPortalUse(PlayerPortalEvent event) {
        PortalBlocker.portalBlocker(event);
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

    public static Economy getEconomy(){
        return econ;
    }

    public static void setEconomy(Economy value) {
        econ = value;
    }


}

