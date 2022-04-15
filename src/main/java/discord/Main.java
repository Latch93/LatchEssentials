package discord;

import discord.AutoMiner.AutoMinerConfig;
import discord.Backbacks.BackPackCommand;
import discord.Backbacks.BackPackInventoryConfig;
import discord.Backbacks.BackPacks;
import discord.Backbacks.BackpackTabComplete;
import discord.Bank.Bank;
import discord.Bank.BankConfig;
import discord.DiscordText.DiscordTextCommand;
import discord.DiscordText.DiscordTextConfig;
import discord.PlayerShops.PlayerShops;
import discord.PlayerShops.PlayerShopsCommand;
import discord.PlayerShops.PlayerShopsInventoryConfig;
import discord.PlayerShops.PlayerShopsTabComplete;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main extends JavaPlugin implements Listener {
    public static final boolean IS_TESTING = true;
    public static Economy econ = null;

    public static AutoMinerConfig autoMinerCfgm;

    // Whitelist Config
    public static WhitelistConfig whitelistCfgm;
    public static File whitelistFile;
    public static FileConfiguration whitelistCfg;
    // Player Shop Config
    public static PlayerShopsInventoryConfig playerShopsInventoryCfgm;
    public static File playerShopFile;
    public static FileConfiguration playerShopCfg;
    // Bank Config
    public static BankConfig bankCfgm;
    public static File bankFile;
    public static FileConfiguration bankCfg;
    // Back Pack Config
    public static BackPackInventoryConfig backPackInventoryCfgm;
    public static File backPackFile;
    public static FileConfiguration backPackCfg;
    // Discord Text Config
    public static DiscordTextConfig discordTextConfigCfgm;
    public static File discordTextFile;
    public static FileConfiguration discordTextCfg;
    // Advancement Config
    public static AdvancementConfig advancementConfigCfgm;
    public static File advancementFile;
    public static FileConfiguration advancementCfg;

    @Override
    public void onEnable() {
        getLogger().info("discord_text is enabled");
        getServer().getPluginManager().registerEvents(this, this);
        setupEconomy();
        loadBackpackManager();
        loadPlayerShopsManager();
        loadBankManager();
        loadAutoMinerManager();
        loadWhitelistManager();
        loadDiscordTextConfigManager();
        loadAdvancementConfigManager();
        whitelistFile = getConfigFile(Constants.YML_WHITELIST_FILE_NAME);
        whitelistCfg = getFileConfiguration(whitelistFile);
        playerShopFile = getConfigFile(Constants.YML_PLAYER_SHOP_FILE_NAME);
        playerShopCfg = getFileConfiguration(playerShopFile);
        bankFile = getConfigFile(Constants.YML_BANK_FILE_NAME);
        bankCfg = getFileConfiguration(bankFile);
        backPackFile = getConfigFile(Constants.YML_BACK_PACK_FILE_NAME);
        backPackCfg = getFileConfiguration(backPackFile);
        discordTextFile = getConfigFile(Constants.YML_DISCORD_TEXT_FILE_NAME);
        discordTextCfg = getFileConfiguration(discordTextFile);
        advancementFile = getConfigFile(Constants.YML_ADVANCEMENT_FILE_NAME);
        advancementCfg = getFileConfiguration(advancementFile);
        try {
            new LatchDiscord();
        } catch (LoginException e) {
            e.printStackTrace();
        }
        Advancements.setAdvancements();
        // Backpack Command
        Objects.requireNonNull(this.getCommand("bp")).setExecutor(new BackPackCommand());
        Objects.requireNonNull(this.getCommand("bp")).setTabCompleter(new BackpackTabComplete());

        // Player Shop Command
//        Objects.requireNonNull(this.getCommand("ps")).setExecutor(new PlayerShopsCommand());
//        Objects.requireNonNull(this.getCommand("ps")).setTabCompleter(new PlayerShopsTabComplete());

        // Discord Text Command
        Objects.requireNonNull(this.getCommand("dt")).setExecutor(new DiscordTextCommand());

        // Auto Miner Commands
//        Objects.requireNonNull(this.getCommand("am")).setExecutor(new AutoMinerCommand());
//        Objects.requireNonNull(this.getCommand("am")).setTabCompleter(new AutoMinerTabComplete());

    }


    @Override
    public void onDisable() {
        getLogger().info("discord_text is disabled");
        LatchDiscord.sendServerStoppedMessage();
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent e) throws IOException {
        LatchDiscord.sendPlayerOnJoinMessage(e);
        LatchDiscord.setChannelDescription(false);
        Bank.setLoginTime(e);
        Bank.getPlayerBalance(e.getPlayer());
        Bank.setPlayerBalanceInConfigOnLogin(e.getPlayer());
        Advancements.setPlayerCompletedAdvancementsOnLogin(e.getPlayer());
//        for (Player onlinePlayer : Bukkit.getOnlinePlayers()){
//            if (onlinePlayer.hasPermission("group.mod") && event.getPlayer().hasPermission("group.mod") && event.getPlayer().getName().equals(onlinePlayer.getName()) ){
//               onlinePlayer.sendMessage(ChatColor.GREEN + "[" + ChatColor.WHITE + "+" + ChatColor.GREEN + "]" + ChatColor.GOLD + event.getPlayer().getName() + ChatColor.GREEN + " joined the server");
//            }
//            if (!event.getPlayer().hasPermission("group.mod") && event.getPlayer().getName().equals(onlinePlayer.getName())){
//                onlinePlayer.sendMessage(ChatColor.GREEN + "[" + ChatColor.WHITE + "+" + ChatColor.GREEN + "] " + ChatColor.GOLD + event.getPlayer().getName() + ChatColor.GREEN + " joined the server");
//            }
//        }
    }

    @EventHandler
    public static void advancement(PlayerAdvancementDoneEvent e) throws IOException {
        Advancements.setPlayerAdvancementOnCompletion(e);
        Advancements.showAdvancementInDiscord(e);
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent event) throws IOException {
        LatchDiscord.sendPlayerLogoutMessage(event);
        LatchDiscord.setChannelDescription(true);
        Bank.setLogoutTime(event);
        Bank.setPlayerSessionSecondsPlayed(event);
        Bank.getPlayerBalance(event.getPlayer());
        Bank.setPlayerBalanceWithInterest(event.getPlayer());
//        for (Player onlinePlayer : Bukkit.getOnlinePlayers()){
//            if (onlinePlayer.hasPermission("group.mod") && event.getPlayer().hasPermission("group.mod")){
//                onlinePlayer.sendMessage(ChatColor.GREEN + "[" + ChatColor.RED + "-" + ChatColor.GREEN + "] " + ChatColor.GOLD + event.getPlayer().getName() + ChatColor.GREEN + " left the server");
//            }
//            if (!event.getPlayer().hasPermission("group.mod")){
//                onlinePlayer.sendMessage(ChatColor.GREEN + "[" + ChatColor.RED + "-" + ChatColor.GREEN + "] " + ChatColor.GOLD + event.getPlayer().getName() + ChatColor.GREEN + " left the server");
//            }
//        }
    }

        @EventHandler
        public static void onPlayerDeath(PlayerDeathEvent e){
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(e.getDeathMessage());
            eb.setColor(new Color(0xE1922E00, true));
            eb.setThumbnail("https://minotar.net/avatar/" + e.getEntity().getName() + ".png?size=5");
            TextChannel minecraftChatChannel = LatchDiscord.getJDA().getTextChannelById(Constants.MINECRAFT_CHAT_CHANNEL_ID);
            assert minecraftChatChannel != null;
            minecraftChatChannel.sendMessageEmbeds(eb.build()).queue();
        }

    @EventHandler
    public void onCommandEvent(PlayerCommandPreprocessEvent event) {
        LatchDiscord.logPlayerBan(event, null);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        try {
            RandomItem.getRandomItem(event);
            QuickSmelt.quickSmelt(event.getPlayer(), econ, event);
            QuickBrew.quickBrew(event.getPlayer(), econ, event);
            MobileSpawner.disableSpawnerMobChange(event);
            SlimeChunkFinder.isSlimeChunk(event);
        } catch (NullPointerException | IOException ignored){}
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) throws IOException {
        Player player = (Player) e.getPlayer();
        if (e.getView().getTitle().equals(player.getName() + Constants.YML_POSSESSIVE_BACKPACK)){
            BackPacks.saveCustomInventory(e, backPackFile);
        }
        //PlayerShops.removeLoreFromSellerInventory(e, playerShopFile);
    }

    @EventHandler
    public void onPlayerInventoryClick(InventoryClickEvent e) throws IOException {
        cancelEventsInPreviousSeason(e.getWhoClicked().getWorld().getName(), e.getWhoClicked().getName(), null, null, e, null);
        Player player = (Player) e.getWhoClicked();
        String invTitle = e.getView().getTitle();
//        if (invTitle.equals(player.getName() + Constants.YML_POSSESSIVE_PLAYER_SHOP) && e.getCurrentItem() != null){
//            PlayerShops.itemWorthNotSet(e, player, getFileConfiguration(playerShopFile));
//        } else if (invTitle.contains(Constants.YML_POSSESSIVE_PLAYER_SHOP) && e.getCurrentItem() != null ) {
//            PlayerShops.purchaseItemFromPlayer(e, econ, player);
//        }
    }

    public static int getWhitelistedPlayerCount(){
        return Bukkit.getWhitelistedPlayers().size();
    }



    public static OfflinePlayer getPlayerFromOfflinePlayer(Player player){
        OfflinePlayer offlinePlayer = null;
        for (OfflinePlayer olp : Bukkit.getWhitelistedPlayers()) {
            if (olp.getName().equalsIgnoreCase(player.getName())) {
                offlinePlayer = olp;
            }
        }
        return offlinePlayer;
    }

    public static File getConfigFile(String fileName){
        Main plugin = getPlugin(Main.class);
        return new File(plugin.getDataFolder(), fileName + ".yml");
    }

    public static FileConfiguration getFileConfiguration(File file){
        return YamlConfiguration.loadConfiguration(file);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        cancelEventsInPreviousSeason(event.getPlayer().getWorld().getName(), event.getPlayer().getName(), event, null, null, null);
        FarmMoney.rewardMoneyFromCrops(event, econ);
        MobileSpawner.setSpawnerOnBreak(event);
//        Location chestLocation = new Location(Bukkit.getWorld("world"), 10000, 68, 10004);
//        Chest chest = (Chest) chestLocation.getBlock().getState();
//        chest.setCustomName("AutoMiner Chest");
//        ItemStack im = new ItemStack(event.getBlock().getType(), 1);
//        Inventory inv = chest.getInventory();
//        inv.addItem(im);

    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        cancelEventsInPreviousSeason(event.getPlayer().getWorld().getName(), event.getPlayer().getName(), null, event, null, null);
        MobileSpawner.setSpawnerOnPlace(event, econ);
        //AutoMiner.mineBlocks(event);
    }

    @EventHandler
    public void onPlayerPortalUse(PlayerPortalEvent event) {
        cancelEventsInPreviousSeason(event.getPlayer().getWorld().getName(), event.getPlayer().getName(), null, null, null, event);
        PortalBlocker.portalBlocker(event);
    }

    @EventHandler
    public void onPlayerChestItemRemove(InventoryClickEvent event) {
       LatchDiscord.banPlayerStealing(event);
    }

    // Economy Setup
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

    // Configuration File Managers
    public static void loadBackpackManager() {
        backPackInventoryCfgm = new BackPackInventoryConfig();
        backPackInventoryCfgm.setup();
    }

    public static void loadPlayerShopsManager() {
        playerShopsInventoryCfgm = new PlayerShopsInventoryConfig();
        playerShopsInventoryCfgm.setup();
    }

    public static void loadBankManager() {
        bankCfgm = new BankConfig();
        bankCfgm.setup();
    }

    public static void loadAutoMinerManager(){
        autoMinerCfgm = new AutoMinerConfig();
        autoMinerCfgm.setup();
    }

    public static void loadWhitelistManager(){
        whitelistCfgm = new WhitelistConfig();
        whitelistCfgm.setup();
    }

    public static void loadDiscordTextConfigManager(){
        discordTextConfigCfgm = new DiscordTextConfig();
        discordTextConfigCfgm.setup();
    }

    public static void loadAdvancementConfigManager(){
        advancementConfigCfgm = new AdvancementConfig();
        advancementConfigCfgm.setup();
    }

    public static FileConfiguration loadConfig(String fileName) {
        File discordTextFile = getConfigFile(fileName);
        return YamlConfiguration.loadConfiguration(discordTextFile);
    }

    public static void cancelEventsInPreviousSeason(String worldName, String player, BlockBreakEvent blockBreakEvent, BlockPlaceEvent blockPlaceEvent, InventoryClickEvent inventoryClickEvent, PlayerPortalEvent playerPortalEvent){
        if (worldName.equalsIgnoreCase("season1")) {
            if (player == null || !player.equalsIgnoreCase("Latch93")){
                if (blockBreakEvent != null) {
                    blockBreakEvent.setCancelled(true);
                } else if (blockPlaceEvent != null) {
                    blockPlaceEvent.setCancelled(true);
                } else if (inventoryClickEvent != null) {
                    inventoryClickEvent.setCancelled(true);
                } else if (playerPortalEvent != null) {
                    playerPortalEvent.setCancelled(true);
                }
            }

        }
}

}

