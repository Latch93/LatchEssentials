package discord;

import discord.Backbacks.BackPackCommand;
import discord.Backbacks.BackPackInventoryConfig;
import discord.Backbacks.BackpackTabComplete;
import discord.Backbacks.Inventories;
import discord.Bank.Bank;
import discord.Bank.BankConfig;
import discord.Configurations.AdvancementConfig;
import discord.Configurations.LotteryConfig;
import discord.LatchTwitchBot.LatchTwitchBotCommand;
import discord.LatchTwitchBot.LatchTwitchBotConfig;
import discord.Configurations.WhitelistConfig;
import discord.DiscordText.DiscordTextCommand;
import discord.DiscordText.DiscordTextConfig;
import discord.LatchTwitchBot.LatchTwitchBotTabComplete;
import discord.PlayerShops.PlayerShops;
import discord.PlayerShops.PlayerShopsCommand;
import discord.PlayerShops.PlayerShopsInventoryConfig;
import discord.PlayerShops.PlayerShopsTabComplete;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;

import java.awt.*;
import java.io.IOException;

import java.util.Objects;


public class Main extends JavaPlugin implements Listener {
    public static final boolean GLOBAL_TESTING = false;
    //public static final AutoMinerConfig autoMinerCfgm = new AutoMinerConfig();
    // Whitelist Config
    private static WhitelistConfig whitelistCfgm;
    // Player Shop Config
    private static PlayerShopsInventoryConfig playerShopsInventoryCfgm;
    // Bank Config
    private static BankConfig bankCfgm;
    // Back Pack Config
    private static BackPackInventoryConfig backPackInventoryCfgm;
    // Discord Text Config
    private static DiscordTextConfig discordTextConfigCfgm;
    // Advancement Config
    private static AdvancementConfig advancementConfigCfgm;
    // Lottery Config
    private static LotteryConfig lotteryConfigCfgm;
    // Twitch Config
    private static LatchTwitchBotConfig twitchBotCfgm;

    @Override
    public void onEnable() {
        getLogger().info("discord_text is enabled");
        getServer().getPluginManager().registerEvents(this, this);
        try {
            new LatchDiscord();
        } catch (LoginException e) {
            e.printStackTrace();
        }
        whitelistCfgm = new WhitelistConfig();
        playerShopsInventoryCfgm = new PlayerShopsInventoryConfig();
        bankCfgm = new BankConfig();
        backPackInventoryCfgm = new BackPackInventoryConfig();
        discordTextConfigCfgm = new DiscordTextConfig();
        advancementConfigCfgm = new AdvancementConfig();
        lotteryConfigCfgm = new LotteryConfig();
        twitchBotCfgm = new LatchTwitchBotConfig();
        Api.setupEconomy(getServer().getPluginManager().getPlugin("Vault"));
        loadAllConfigManagers();

        Advancements.setAdvancements();
        // Backpack Command
        Objects.requireNonNull(this.getCommand("bp")).setExecutor(new BackPackCommand());
        Objects.requireNonNull(this.getCommand("bp")).setTabCompleter(new BackpackTabComplete());

        // Player Shop Command
        Objects.requireNonNull(this.getCommand("ps")).setExecutor(new PlayerShopsCommand());
        Objects.requireNonNull(this.getCommand("ps")).setTabCompleter(new PlayerShopsTabComplete());

        // Discord Text Command
        Objects.requireNonNull(this.getCommand("dt")).setExecutor(new DiscordTextCommand());

        // Discord Staff Chat Command
        Objects.requireNonNull(this.getCommand("dtsc")).setExecutor(new DiscordStaffChatCommand());

        // Twitch Bot Command
        Objects.requireNonNull(this.getCommand("twitch")).setExecutor(new LatchTwitchBotCommand());
        Objects.requireNonNull(this.getCommand("twitch")).setTabCompleter(new LatchTwitchBotTabComplete());

        // Auto Miner Commands
//        Objects.requireNonNull(this.getCommand("am")).setExecutor(new AutoMinerCommand());
//        Objects.requireNonNull(this.getCommand("am")).setTabCompleter(new AutoMinerTabComplete());

    }

    @Override
    public void onDisable() {
        Api.stopAllTwitchBots(LatchTwitchBotCommand.twitchBotList);
        getLogger().info("discord_text is disabled");
        LatchDiscord.stopBot();
        if (Boolean.FALSE.equals(getIsParameterInTesting("onDisable"))) {
            LatchDiscord.sendServerStoppedMessage();
        }
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent e) throws IOException {
        if (Boolean.FALSE.equals(getIsParameterInTesting("onLogin"))) {
            LatchDiscord.sendPlayerOnJoinMessage(e);
            LatchDiscord.setChannelDescription(false);
            Bank.setLoginTime(e);
            Bank.getPlayerBalance(e.getPlayer());
            Bank.setPlayerBalanceInConfigOnLogin(e.getPlayer());
            Advancements.setPlayerCompletedAdvancementsOnLogin(e.getPlayer());
        }
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent event) throws IOException {
        if (Boolean.FALSE.equals(getIsParameterInTesting("onLogout"))) {
            LatchDiscord.sendPlayerLogoutMessage(event);
            LatchDiscord.setChannelDescription(true);
            Bank.setLogoutTime(event);
            Bank.setPlayerSessionSecondsPlayed(event);
            Bank.getPlayerBalance(event.getPlayer());
            Bank.setPlayerBalanceWithInterest(event.getPlayer());
            Api.stopTwitchBot(LatchTwitchBotCommand.twitchBotList, event.getPlayer());
        }
    }

    @EventHandler
    public static void advancementDoneEvent(PlayerAdvancementDoneEvent e) throws IOException {
        if (Boolean.FALSE.equals(getIsParameterInTesting("advancementDoneEvent"))) {
            Advancements.setPlayerAdvancementOnCompletion(e);
            Advancements.showAdvancementInDiscord(e);
        }
    }

    @EventHandler
    public static void onPlayerDeath(PlayerDeathEvent e){
        if (Boolean.FALSE.equals(getIsParameterInTesting("onPlayerDeath"))) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(e.getDeathMessage());
            eb.setColor(new Color(0xE1922E00, true));
            eb.setThumbnail("https://minotar.net/avatar/" + e.getEntity().getName() + ".png?size=5");
            TextChannel minecraftChatChannel = LatchDiscord.getJDA().getTextChannelById(Constants.MINECRAFT_CHAT_CHANNEL_ID);
            assert minecraftChatChannel != null;
            minecraftChatChannel.sendMessageEmbeds(eb.build()).queue();
        }
    }

    @EventHandler
    public void onPlayerChatEvent(AsyncPlayerChatEvent e){
        if (Boolean.FALSE.equals(getIsParameterInTesting("onPlayerChatEvent")) && Boolean.FALSE.equals(Api.isPlayerInvisible(e.getPlayer().getUniqueId().toString())) ){
            TextChannel minecraftChatChannel = LatchDiscord.jda.getTextChannelById(Constants.MINECRAFT_CHAT_CHANNEL_ID);
            assert minecraftChatChannel != null;
            minecraftChatChannel.sendMessage(Api.convertMinecraftMessageToDiscord(e.getPlayer().getDisplayName(), e.getMessage())).queue();
        }
        if(Boolean.TRUE.equals(Api.isPlayerInvisible(e.getPlayer().getUniqueId().toString()))){
            e.getPlayer().sendMessage(ChatColor.YELLOW + "You are invisible right now.");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onCommandEvent(PlayerCommandPreprocessEvent event) {
        if (Boolean.FALSE.equals(getIsParameterInTesting("onCommandEvent"))) {
            LatchDiscord.logPlayerBan(event, null);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) throws IOException {
        if (Boolean.FALSE.equals(getIsParameterInTesting("onInteract"))) {
            RandomItem.getRandomItem(event);
            QuickSmelt.quickSmelt(event.getPlayer(), Api.getEconomy(), event);
            QuickBrew.quickBrew(event.getPlayer(), Api.getEconomy(), event);
            MobileSpawner.disableSpawnerMobChange(event);
            SlimeChunkFinder.isSlimeChunk(event);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) throws IOException {
        if (Boolean.FALSE.equals(getIsParameterInTesting("onInventoryClose"))) {
            Player player = (Player) e.getPlayer();
            if (e.getView().getTitle().equals(player.getName() + Constants.YML_POSSESSIVE_BACKPACK)) {
                Inventories.saveCustomInventory(e, Api.getConfigFile(Constants.YML_BACK_PACK_FILE_NAME));
            } else if (e.getView().getTitle().equals(player.getName() + Constants.YML_POSSESSIVE_PLAYER_SHOP)) {
                Inventories.saveCustomInventory(e, Api.getConfigFile(Constants.YML_PLAYER_SHOP_FILE_NAME));
            }
            PlayerShops.removeLoreFromSellerInventory(e, Api.getConfigFile(Constants.YML_PLAYER_SHOP_FILE_NAME));
        }
    }

    @EventHandler
    public void onPlayerInventoryClick(InventoryClickEvent e){
        if (Boolean.FALSE.equals(getIsParameterInTesting("onPlayerInventoryClick"))) {
            Api.cancelEventsInPreviousSeason(e.getWhoClicked().getWorld().getName(), e.getWhoClicked().getName(), null, null, e, null);
            Player player = (Player) e.getWhoClicked();
            String invTitle = e.getView().getTitle();
            if (invTitle.equals(player.getName() + Constants.YML_POSSESSIVE_PLAYER_SHOP) && e.getCurrentItem() != null) {
                PlayerShops.itemWorthNotSet(e, player, Api.getFileConfiguration(Api.getConfigFile(Constants.YML_PLAYER_SHOP_FILE_NAME)));
            } else if (invTitle.contains(Constants.YML_POSSESSIVE_PLAYER_SHOP) && e.getCurrentItem() != null) {
                PlayerShops.purchaseItemFromPlayer(e, Api.getEconomy(), player);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        if (Boolean.FALSE.equals(getIsParameterInTesting("onBlockBreak"))){
            Api.cancelEventsInPreviousSeason(event.getPlayer().getWorld().getName(), event.getPlayer().getName(), event, null, null, null);
            FarmMoney.rewardMoneyFromCrops(event, Api.getEconomy());
            MobileSpawner.setSpawnerOnBreak(event);
        }
//        Location chestLocation = new Location(Bukkit.getWorld("world"), 10000, 68, 10004);
//        Chest chest = (Chest) chestLocation.getBlock().getState();
//        chest.setCustomName("AutoMiner Chest");
//        ItemStack im = new ItemStack(event.getBlock().getType(), 1);
//        Inventory inv = chest.getInventory();
//        inv.addItem(im);
    }

    @EventHandler
    public void onAnimalBreed(EntityBreedEvent e){
        if (Boolean.FALSE.equals(getIsParameterInTesting("onAnimalBreed"))) {
            Player player = (Player) e.getBreeder();
            String child = e.getEntity().getName();
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        if (Boolean.FALSE.equals(getIsParameterInTesting("onBlockPlace"))) {
            Api.cancelEventsInPreviousSeason(event.getPlayer().getWorld().getName(), event.getPlayer().getName(), null, event, null, null);
            MobileSpawner.setSpawnerOnPlace(event, Api.getEconomy());
        }
        //AutoMiner.mineBlocks(event);
    }

    @EventHandler
    public void onPlayerPortalUse(PlayerPortalEvent event) {
        if (Boolean.FALSE.equals(getIsParameterInTesting("onPlayerPortalUse"))){
            Api.cancelEventsInPreviousSeason(event.getPlayer().getWorld().getName(), event.getPlayer().getName(), null, null, null, event);
            PortalBlocker.portalBlocker(event);
        }
    }

    @EventHandler
    public void onPlayerChestItemRemove(InventoryClickEvent event) {
        if (Boolean.FALSE.equals(getIsParameterInTesting("onPlayerChestItemRemove"))) {
            LatchDiscord.banPlayerStealing(event);
        }
    }

    public static boolean getIsParameterInTesting(String parameter){
        return Api.getFileConfiguration(Api.getConfigFile(Constants.YML_CONFIG_FILE_NAME)).getBoolean("testingParameters." + parameter);
    }

    public static void loadAllConfigManagers(){
        backPackInventoryCfgm.setup();
        playerShopsInventoryCfgm.setup();
        bankCfgm.setup();
        //autoMinerCfgm.setup();
        whitelistCfgm.setup();
        discordTextConfigCfgm.setup();
        advancementConfigCfgm.setup();
        lotteryConfigCfgm.setup();
        twitchBotCfgm.setup();
    }


}

