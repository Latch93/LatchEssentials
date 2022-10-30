package lmp;

import io.donatebot.api.DBClient;
import lmp.api.Api;
import lmp.commands.*;
import lmp.configurations.*;
import lmp.constants.Constants;
import lmp.constants.ServerCommands;
import lmp.constants.YmlFileNames;
import lmp.customItems.*;
import lmp.customRecipes.CustomRecipes;
import lmp.discord.privateMessageAdapters.ChestProtect;
import lmp.listeners.FurnaceBurnEvents.DisableAutoSmeltStickFromBurning;
import lmp.listeners.asyncPlayerChatEvents.BroadcastServerChatToDiscord;
import lmp.listeners.autosort.inventoryMoveItemEvents.SendItemToOutputChestFromMasterChestEvent;
import lmp.listeners.discord.BanAndLogBanChestThief;
import lmp.listeners.entityDeathEvents.DisableEntityCrammingEvent;
import lmp.listeners.entityDeathEvents.LogVillagerDeathEvent;
import lmp.listeners.playerAdvancementDoneEvents.PlayerAdvancementEvent;
import lmp.listeners.playerCommandPreprocessEvents.LogPlayerBanFromServerCommandEvent;
import lmp.listeners.playerDeathEvents.BroadcastDeathMessageToDiscordEvent;
import lmp.listeners.playerDeathEvents.DeathByJerryEvent;
import lmp.listeners.playerDeathEvents.HardcoreDeathEvent;
import lmp.listeners.playerDeathEvents.WithdrawPlayerMoneyOnDeathEvent;
import lmp.listeners.playerInteractEntityEvents.GiveJerryArmorEvent;
import lmp.listeners.playerJoinEvents.*;
import lmp.listeners.playerMoveEvents.DenyMoveForUnlinkedPlayerEvent;
import lmp.listeners.playerQuitEvents.BankLogoutEvent;
import lmp.listeners.playerQuitEvents.BroadcastPlayerQuitMessageToDiscordEvent;
import lmp.listeners.playerQuitEvents.StopTwitchBotOnPlayerLogoutEvent;
import lmp.listeners.playerQuitEvents.TurnOffXPFarmOnPlayerLogoutEvent;
import lmp.runnable.LMPTimer;
import lmp.runnable.PingTabListRunnable;
import lmp.tabComplete.BackpackTabComplete;
import lmp.tabComplete.LMPCommandTabComplete;
import lmp.tabComplete.LatchTwitchBotTabComplete;
import lmp.tabComplete.PlayerShopsTabComplete;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.luckperms.api.LuckPerms;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.eclipse.egit.github.core.client.GitHubClient;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;


public class Main extends JavaPlugin implements Listener {
    public static final boolean GLOBAL_TESTING = false;
    public static Logger log;

    public static LuckPerms luckPerms;
    //public static final AutoMinerConfig autoMinerCfgm = new AutoMinerConfig();
    public static DBClient dbClient;
    public static CoreProtectAPI coreProtectAPI;
    public static GitHubClient githubClient;
    private PluginManager pm = getServer().getPluginManager();
    @Override
    public void onEnable() {
        log = getLogger();
        log.info(Constants.PLUGIN_NAME + " is enabled");
        if (Api.getFileConfiguration(YmlFileNames.YML_ENABLED_EVENTS_FILE_NAME).getBoolean("enableDiscord")) {
            try {
                new LatchDiscord();
            } catch (LoginException e) {
                e.printStackTrace();
            }
        }
        pm.registerEvents(this, this);
        new PlayerAdvancementEvent(this);
        new DeathByJerryEvent(this);
        new WithdrawPlayerMoneyOnDeathEvent(this);
        new BroadcastDeathMessageToDiscordEvent(this);
        new HardcoreDeathEvent(this);
        new DisableEntityCrammingEvent(this);
        new LogVillagerDeathEvent(this);
        new BroadcastPlayerJoinMessageToDiscordEvent(this);
        new SetPlayerCompletedAdvancementsOnLoginEvent(this);
        new UpdatePlayerWhitelistOnJoinEvent(this);
        new SendJoinMessageEvent(this);
        new SendHowToLinkMessageEvent(this);
        new BankLoginEvent(this);
        new BankLogoutEvent(this);
        new GiveJerryArmorEvent(this);
        new BroadcastPlayerQuitMessageToDiscordEvent(this);
        new TurnOffXPFarmOnPlayerLogoutEvent(this);
        new StopTwitchBotOnPlayerLogoutEvent(this);
        new BroadcastServerChatToDiscord(this);
        new LogPlayerBanFromServerCommandEvent(this);
        new DenyMoveForUnlinkedPlayerEvent(this);
        new SendItemToOutputChestFromMasterChestEvent(this);
        new BanAndLogBanChestThief(this);
        new DisableAutoSmeltStickFromBurning(this);

        Api.setupEconomy(getServer().getPluginManager().getPlugin("Vault"));
        loadAllConfigManagers();
        Advancements.setAdvancements();
        registerCommands();
        this.registerTasks();
        // Auto Miner Commands
//        Objects.requireNonNull(this.getCommand("am")).setExecutor(new AutoMinerCommand());
//        Objects.requireNonNull(this.getCommand("am")).setTabCompleter(new AutoMinerTabComplete());
        CustomRecipes.addCustomRecipes();
        if (Boolean.FALSE.equals(getIsParameterInTesting("runTimer"))) {
            LMPTimer.runTimer();
        }
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
        }
        dbClient = new DBClient("625983914049142786", "PlXK5QShFmmlb4q9qILoRc1lXqLsZVG72aOEnAiaaQi2oXfAI5X5EuIRLGBp1qa");
        Plugin coreProtect = getServer().getPluginManager().getPlugin("CoreProtect");
        assert coreProtect != null;
        coreProtectAPI = ((CoreProtect) coreProtect).getAPI();
        githubClient = new GitHubClient().setOAuth2Token(Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME).getString("githubOauthToken"));
    }

    @Override
    public void onDisable() {
        Api.stopAllTwitchBots(LatchTwitchBotCommand.twitchBotList);
        FileConfiguration enabledEventsCfg = Api.getFileConfiguration(YmlFileNames.YML_ENABLED_EVENTS_FILE_NAME);
        if (enabledEventsCfg.getBoolean("broadcastServerStoppedMessageToDiscord")) {
            LatchDiscord.sendServerStoppedMessage();
        }
        Api.stopDiscordBot();
        getLogger().info(Constants.PLUGIN_NAME + " is disabled");
    }

    public static boolean getIsParameterInTesting(String parameter) {
        return Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME).getBoolean("testingParameters." + parameter);
    }

    public static void loadAllConfigManagers() {
        new WhitelistConfig().setup();
        new PlayerShopsInventoryConfig().setup();
        new BankConfig().setup();
        new BackPackInventoryConfig().setup();
        new LMPConfig().setup();
        new AdvancementConfig().setup();
        new LotteryConfig().setup();
        new LatchTwitchBotConfig().setup();
        new AutoSorterConfig().setup();
        new BossConfig().setup();
    }


    public static LuckPerms getLuckPerms() {
        return luckPerms;
    }

    @EventHandler
    public void EntityDamageEvent(EntityDamageEvent e) {
        BossBattle.bossHurtEvent(e);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) throws IOException {
        BossBattle.bossBattleEnded(e);
    }

    public DBClient getDbClient() {
        return dbClient;
    }

    private void registerTasks() {
        FileConfiguration configCfg = Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME);
        Long delay = configCfg.getLong("tablist-delay");
        new PingTabListRunnable(this).runTaskTimerAsynchronously(this, delay * 20, delay * 20);
    }

    private void registerCommands() {
        // Server Command
        Objects.requireNonNull(this.getCommand(ServerCommands.LMP_COMMAND)).setExecutor(new LMPCommand());
        Objects.requireNonNull(this.getCommand(ServerCommands.LMP_COMMAND)).setTabCompleter(new LMPCommandTabComplete());
        // Backpack Command
        Objects.requireNonNull(this.getCommand(ServerCommands.BACKPACK_COMMAND)).setExecutor(new BackPackCommand());
        Objects.requireNonNull(this.getCommand(ServerCommands.BACKPACK_COMMAND)).setTabCompleter(new BackpackTabComplete());
        // Player Shop Command
        Objects.requireNonNull(this.getCommand(ServerCommands.PLAYER_SHOP_COMMAND)).setExecutor(new PlayerShopsCommand());
        Objects.requireNonNull(this.getCommand(ServerCommands.PLAYER_SHOP_COMMAND)).setTabCompleter(new PlayerShopsTabComplete());
        // Discord Staff Chat Command
        Objects.requireNonNull(this.getCommand(ServerCommands.DISCORD_STAFF_CHAT_COMMAND)).setExecutor(new DiscordStaffChatCommand());
        Objects.requireNonNull(this.getCommand(ServerCommands.DISCORD_ADMIN_CHAT_COMMAND)).setExecutor(new DiscordAdminChatCommand());
        Objects.requireNonNull(this.getCommand(ServerCommands.SERVER_TO_DISCORD_COMMAND)).setExecutor(new MessageDiscordUserFromServerCommand());
        // Twitch Bot Command
        Objects.requireNonNull(this.getCommand(ServerCommands.TWITCH_COMMAND)).setExecutor(new LatchTwitchBotCommand());
        Objects.requireNonNull(this.getCommand(ServerCommands.TWITCH_COMMAND)).setTabCompleter(new LatchTwitchBotTabComplete());
    }

    @EventHandler
    public void onCommandEvent(PlayerCommandPreprocessEvent event) throws IOException, ExecutionException, InterruptedException {
        if (Boolean.FALSE.equals(getIsParameterInTesting("onCommandEvent"))) {
            if (event.getMessage().equalsIgnoreCase("/back")) {
                Api.denyBackIntoXPFarm(event);
            }
            if (!event.getPlayer().isOp() && event.getPlayer().getWorld().getName().equalsIgnoreCase("hardcore")) {
                event.setCancelled(Api.denyCommandInHardcore(event));
            }
            Api.denyCommandUseInXPFarm(event);
        }
        if (!event.getPlayer().getWorld().getName().contains("hardcore") && event.getMessage().equalsIgnoreCase("/lmp hardcore")) {
            if (Boolean.TRUE.equals(Api.doesPlayerHavePermission(event.getPlayer().getUniqueId().toString(), "hardcore"))) {
                Api.addPlayerToHardcoreList(event);
                Api.teleportHardcorePlayerToLastLocation(event.getPlayer());
            } else {
                event.getPlayer().sendMessage(ChatColor.RED + "You must purchase a hardcore season pass for " + ChatColor.GOLD + "$3 USD.");
            }
        }
        if (event.getPlayer().getWorld().getName().contains("hardcore") && event.getMessage().equalsIgnoreCase("/spawn")) {
            Api.setHardcorePlayerLocation(event);
        }
    }

    @EventHandler
    public void onTargetChange(EntityTargetEvent e) {
        if (e.getEntity().getType().equals(EntityType.WOLF)) {
            File bloodmoonLMPFile = new File("plugins/Bloodmoon", "lmp.yml");
            FileConfiguration bloodmoonLMPCfg = YamlConfiguration.loadConfiguration(bloodmoonLMPFile);
            List<String> currentHordeList = bloodmoonLMPCfg.getStringList("hordeMobs");
            if (e.getTarget() != null && currentHordeList.contains(e.getTarget().getUniqueId().toString())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent e) {
        if (e.getChangedType().equals(Material.NETHER_PORTAL)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) throws IOException {
        if (Boolean.FALSE.equals(getIsParameterInTesting("onInteract"))) {
            RandomItem.getRandomItem1(event);
            RandomItem.getRandomItem2(event);
            RandomItem.getRandomItem3(event);
            try {
                QuickSmelt.quickSmelt(event.getPlayer(), Api.getEconomy(), event);
                QuickBrew.quickBrew(event.getPlayer(), Api.getEconomy(), event);
            } catch (NullPointerException ignored) {

            }
            MobileSpawner.disableSpawnerMobChange(event);
            SlimeChunkFinder.isSlimeChunk(event);
            setMasterAndSortChests(event);
            if (event.getAction().toString().equals("RIGHT_CLICK_BLOCK") && Objects.requireNonNull(event.getClickedBlock()).getType().equals(Material.CRIMSON_BUTTON)) {
                BossBattle.startBossBattle(event);
            }
            if (event.getClickedBlock() != null && event.getClickedBlock().getType().equals(Material.BEDROCK)) {
                if (Boolean.TRUE.equals(event.getPlayer().getInventory().getItemInMainHand().hasItemMeta()) && Boolean.TRUE.equals(Objects.requireNonNull(event.getPlayer().getInventory().getItemInMainHand().getItemMeta()).hasLore())) {
                    if (Objects.requireNonNull(event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getLore()).get(0).equalsIgnoreCase("Bedrock Breaker")) {
                        Block bedrockToBreak = event.getClickedBlock();
                        double playerBalance = Api.getEconomy().getBalance(Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId()));
                        if (playerBalance >= Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME).getDouble("bedrockBreakerCost")) {
                            DecimalFormat df = new DecimalFormat("0.00");
                            bedrockToBreak.setType(Material.AIR);
                            Api.getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId()), Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME).getDouble("bedrockBreakerCost"));
                            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + lmp.Constants.YML_YOUR_NEW_BALANCE_IS + ChatColor.GOLD + "$" + df.format(Api.getEconomy().getBalance(Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId())))));
                        } else {
                            event.getPlayer().sendMessage(ChatColor.RED + "You need at least " + ChatColor.GOLD + "$" + Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME).getDouble("bedrockBreakerCost") + ChatColor.RED + " to break a block of bedrock.");
                        }
                    }
                }
            }
            Api.denyOpenChestDuringBossBattle(event);
            CustomPortals.setBlockToNetherPortal(event);
            XPFarm.teleportPlayerToXPFarm(event);
            XPFarm.teleportPlayerToSpawn(event);
            if (Boolean.TRUE.equals(LMPCommand.isPlayerHoldingXPStorageBottle(event.getPlayer()) && (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)))) {
                event.getPlayer().sendMessage(ChatColor.RED + "You should be more careful. Don't throw away your Experience Storage Bottle. Switch to another item to be safe");
                event.setCancelled(true);
            }
        }
        if (!event.getPlayer().isOp() && event.getPlayer().getWorld().getName().equalsIgnoreCase("hardcore")) {
            FileConfiguration hardcoreCfg = Api.getFileConfiguration(YmlFileNames.YML_HARDCORE_FILE_NAME);
            if (hardcoreCfg.getString(event.getPlayer().getUniqueId().toString()) == null || Boolean.FALSE.equals(hardcoreCfg.getBoolean(event.getPlayer().getUniqueId().toString() + "." + "isAlive"))) {
                event.getPlayer().setGameMode(GameMode.SURVIVAL);
            }
            event.setCancelled(Api.denyInteractInHardcore(event.getPlayer()));
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        FileConfiguration configCfg = Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME);
        if (Boolean.FALSE.equals(configCfg.getBoolean("areSpawnersActive"))) {
            if (Objects.requireNonNull(e.getLocation().getWorld()).getName().equalsIgnoreCase("world") && e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) throws IOException {
        if (Boolean.FALSE.equals(getIsParameterInTesting("onInventoryClose"))) {
            Player player = (Player) e.getPlayer();
            if (e.getView().getTitle().equals(player.getName() + lmp.Constants.YML_POSSESSIVE_BACKPACK)) {
                Inventories.saveCustomInventory(e, Api.getConfigFile(YmlFileNames.YML_BACK_PACK_FILE_NAME));
            } else if (e.getView().getTitle().equals(player.getName() + lmp.Constants.YML_POSSESSIVE_PLAYER_SHOP)) {
                Inventories.saveCustomInventory(e, Api.getConfigFile(YmlFileNames.YML_PLAYER_SHOP_FILE_NAME));
            }
            PlayerShops.removeLoreFromSellerInventory(e, Api.getConfigFile(YmlFileNames.YML_PLAYER_SHOP_FILE_NAME));
            sendItemsInAutoSorter(e);
        }
    }

    @EventHandler
    public void onPlayerInventoryClick(InventoryClickEvent e) throws ExecutionException, InterruptedException {
        if (Boolean.FALSE.equals(getIsParameterInTesting("onPlayerInventoryClick"))) {
            Api.cancelEventsInPreviousSeason(e.getWhoClicked().getWorld().getName(), e.getWhoClicked().getName(), null, null, e, null);
            Player player = (Player) e.getWhoClicked();
            String invTitle = e.getView().getTitle();
            if (invTitle.equals(player.getName() + lmp.Constants.YML_POSSESSIVE_PLAYER_SHOP) && e.getCurrentItem() != null) {
                PlayerShops.itemWorthNotSet(e, player, Api.getFileConfiguration(YmlFileNames.YML_PLAYER_SHOP_FILE_NAME));
            } else if (invTitle.contains(lmp.Constants.YML_POSSESSIVE_PLAYER_SHOP) && e.getCurrentItem() != null) {
                PlayerShops.purchaseItemFromPlayer(e, Api.getEconomy(), player);
            }
        }
//        if (e.getClickedInventory() instanceof CraftingInventory && e.getSlot() == 0){
//            CraftingInventory craftingInventory = (CraftingInventory) e.getClickedInventory();
//            if (e.getCurrentItem() != null && e.getCurrentItem().getItemMeta() != null && e.getCurrentItem().getItemMeta().getLore() != null){
//                if (e.getCurrentItem().getItemMeta().getLore().get(0).toLowerCase().contains("compressed")){
//                    craftingInventory.setMatrix(null);
//                }
//            }
//        }
    }

    @EventHandler
    public void onAnvilInventoryOpen(PrepareAnvilEvent e) {
        Api.combineChestplateAndElytra(e);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            Player player = (Player) e.getDamager();
            ItemStack is = player.getInventory().getItemInMainHand();
            ItemMeta im = null;
            if (is.getItemMeta() != null) {
                im = is.getItemMeta();
            }
            if (im != null && im.getLore() != null) {
                if (im.getLore().get(0).equalsIgnoreCase("Blood Sucker")) {
                    double playerHealth = player.getHealth();
                    if (playerHealth <= 18.00) {
                        int min = 0;
                        int max = 10;
                        int random_int = (int) Math.floor(Math.random() * (max - min + 1) + min);
                        if (random_int > 7) {
                            player.setHealth(playerHealth + 2.00);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) throws IOException, ExecutionException, InterruptedException {
        event.setCancelled(Api.cancelJrModEvent(event.getPlayer().getUniqueId()));
        if (Boolean.FALSE.equals(getIsParameterInTesting("onBlockBreak"))) {
            Api.cancelEventsInPreviousSeason(event.getPlayer().getWorld().getName(), event.getPlayer().getName(), event, null, null, null);
            FarmMoney.rewardMoneyFromCrops(event, Api.getEconomy());
            MobileSpawner.setSpawnerOnBreak(event);
            if (!Api.doesPlayerHavePermission(event.getPlayer().getUniqueId().toString(), "helper")) {
                Api.blockBreakLog(event);
            }
        }
        if (event.getBlock().getType().equals(Material.CREEPER_HEAD)) {
            FileConfiguration creeperCfg = Api.getFileConfiguration(YmlFileNames.YML_CREEPERS_B_GONE_FILE_NAME);
            String creeperHeadLocation = event.getBlock().getLocation().toString();
            List<String> creeperLocationList = new ArrayList<>();
            if (!creeperCfg.getStringList("locations").isEmpty()) {
                creeperLocationList = creeperCfg.getStringList("locations");
            }
            int indexToDelete = -1;
            for (int i = 0; i < creeperLocationList.size(); i++) {
                if (creeperLocationList.get(i).equalsIgnoreCase(creeperHeadLocation)) {
                    indexToDelete = i;
                }
            }
            if (indexToDelete != -1) {
                creeperLocationList.remove(indexToDelete);
            }
            creeperCfg.set("locations", creeperLocationList);
            creeperCfg.save(Api.getConfigFile(YmlFileNames.YML_CREEPERS_B_GONE_FILE_NAME));

        }

//        Location chestLocation = new Location(Bukkit.getWorld("world"), 10000, 68, 10004);
//        Chest chest = (Chest) chestLocation.getBlock().getState();
//        chest.setCustomName("AutoMiner Chest");
//        ItemStack im = new ItemStack(event.getBlock().getType(), 1);
//        Inventory inv = chest.getInventory();
//        inv.addItem(im);
    }

    @EventHandler
    public void onAnimalBreed(EntityBreedEvent e) {
        if (Boolean.FALSE.equals(getIsParameterInTesting("onAnimalBreed"))) {
            Player player = (Player) e.getBreeder();
            String child = e.getEntity().getName();
        }
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent e) {
        List<String> doNotSpawnList = Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME).getStringList("lightLevelMobs");
        if (doNotSpawnList.contains(e.getEntity().getType().toString())) {
            if (e.getLocation().getBlock().getLightLevel() > 7) {
                File bloodmoonFile = new File("plugins/Bloodmoon/world", "config.yml");
                FileConfiguration bloodmoonCfg = YamlConfiguration.loadConfiguration(bloodmoonFile);
                if (Boolean.FALSE.equals(bloodmoonCfg.get("enabled"))) {
                    e.setCancelled(true);
                }
            }
        }
        Api.creeperBGone(e);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) throws IOException {
        event.setCancelled(Api.cancelJrModEvent(event.getPlayer().getUniqueId()));
        if (Boolean.FALSE.equals(getIsParameterInTesting("onBlockPlace"))) {
            Api.cancelEventsInPreviousSeason(event.getPlayer().getWorld().getName(), event.getPlayer().getName(), null, event, null, null);
            MobileSpawner.setSpawnerOnPlace(event, Api.getEconomy());
            Api.placeBlockLog(event);
        }
        if (event.getBlock().getType().equals(Material.CREEPER_HEAD)) {
            if (event.getItemInHand().getItemMeta() != null && event.getItemInHand().getItemMeta().getLore() != null) {
                if (event.getItemInHand().getItemMeta().getLore().get(0).equalsIgnoreCase("Creepers BGone")) {
                    FileConfiguration creeperCfg = Api.getFileConfiguration(YmlFileNames.YML_CREEPERS_B_GONE_FILE_NAME);
                    List<String> creeperLocationList = new ArrayList<>();
                    if (!creeperCfg.getStringList("locations").isEmpty()) {
                        creeperLocationList = creeperCfg.getStringList("locations");
                    }
                    creeperLocationList.add(event.getBlock().getLocation().toString());
                    creeperCfg.set("locations", creeperLocationList);
                    creeperCfg.save(Api.getConfigFile(YmlFileNames.YML_CREEPERS_B_GONE_FILE_NAME));
                }
            }
        }
        //AutoMiner.mineBlocks(event);
    }

    @EventHandler
    public void onPlayerPortalUse(PlayerPortalEvent event) {
        if (Boolean.FALSE.equals(getIsParameterInTesting("onPlayerPortalUse"))) {
            Api.cancelEventsInPreviousSeason(event.getPlayer().getWorld().getName(), event.getPlayer().getName(), null, null, null, event);
            PortalBlocker.portalBlocker(event);
        }
    }

    @EventHandler
    public void onPlayerChestItemRemove(InventoryClickEvent event) throws ExecutionException, InterruptedException, IOException {
        try {
            if (Boolean.FALSE.equals(getIsParameterInTesting("chestProtect"))) {
                if (Objects.requireNonNull(event.getWhoClicked().getLocation().getWorld()).getName().equalsIgnoreCase("world")) {
                    if (event.getClickedInventory() != null && event.getClickedInventory().getHolder() != null && event.getWhoClicked().getLocation().getWorld().getName().equalsIgnoreCase("world") && (event.getClickedInventory().getHolder() instanceof DoubleChest || event.getClickedInventory().getHolder() instanceof Chest || event.getClickedInventory().getHolder() instanceof ShulkerBox || event.getClickedInventory().getHolder() instanceof Barrel)) {
                        OfflinePlayer olp = null;
                        if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
                            FileConfiguration chestProtectCfg = Api.getFileConfiguration(YmlFileNames.YML_CHEST_PROTECT_FILE_NAME);
                            List<String> ignoreChestArr = chestProtectCfg.getStringList("ignoredChests");
                            Player clicker = (Player) event.getWhoClicked();
                            Block block = Bukkit.getWorld(event.getClickedInventory().getLocation().getWorld().getUID()).getBlockAt(event.getClickedInventory().getLocation());
                            CoreProtectAPI.ParseResult result = null;
                            if (!ignoreChestArr.contains(block.getLocation().toString())) {
                                List<String[]> lookup = coreProtectAPI.blockLookup(block, 20024000);
                                for (String[] value : lookup) {
                                    result = coreProtectAPI.parseResult(value);
                                    if (result.getActionString().equalsIgnoreCase("place")) {
                                        olp = Bukkit.getOfflinePlayer(UUID.fromString(Api.getMinecraftIdFromMinecraftName(result.getPlayer())));
                                        break;
                                    }
                                }
                                try {
                                    assert olp != null;
                                    if (clicker.getUniqueId().equals(olp.getUniqueId())) {
                                        ChestProtect.setApprovedAllPlayer(Api.getDiscordIdFromMCid(olp.getUniqueId().toString()), clicker.getName(), Api.getFileConfiguration(YmlFileNames.YML_CHEST_PROTECT_FILE_NAME));
                                    } else {
                                        if (Boolean.FALSE.equals(Api.doesPlayerHavePermission(olp.getUniqueId().toString(), "chests"))) {
                                            boolean isApprovedPlayer = false;
                                            if (chestProtectCfg.contains(lmp.Constants.YML_PLAYERS + olp.getUniqueId().toString() + ".approvedPlayers")) {
                                                List<String> approvedPlayers = chestProtectCfg.getStringList(lmp.Constants.YML_PLAYERS + olp.getUniqueId().toString() + ".approvedPlayers");
                                                if (approvedPlayers.contains(clicker.getUniqueId().toString())) {
                                                    isApprovedPlayer = true;
                                                }
                                            }
                                            if (chestProtectCfg.contains(lmp.Constants.YML_PLAYERS + olp.getUniqueId().toString() + "." + event.getClickedInventory().getLocation().toString())) {
                                                List<String> approvedChestPlayers = chestProtectCfg.getStringList(lmp.Constants.YML_PLAYERS + olp.getUniqueId().toString() + "." + event.getClickedInventory().getLocation().toString());
                                                if (approvedChestPlayers.contains(clicker.getUniqueId().toString())) {
                                                    isApprovedPlayer = true;
                                                }
                                            }
                                            if (Boolean.FALSE.equals(isApprovedPlayer)) {
                                                try {
                                                    net.dv8tion.jda.api.entities.User chestOwner = LatchDiscord.getJDA().getGuildById(lmp.Constants.GUILD_ID).getMemberById(Api.getDiscordIdFromMCid(olp.getUniqueId().toString())).getUser();
                                                    chestOwner.openPrivateChannel().flatMap(privateChannel -> {
                                                        LatchDiscord.getJDA().addEventListener(new ChestProtect(privateChannel, chestOwner, event.getCurrentItem().getType().toString(), clicker.getName(), block.getLocation(), block));
                                                        return privateChannel.sendMessage("Minecraft User Name: " + clicker.getName() + "\nDiscord User Name: " + Api.getDiscordNameFromMCid(Api.getMinecraftIdFromMinecraftName(clicker.getName())) + "\n" +
                                                                "Stole: " + event.getCurrentItem().getType().toString() + "\nLocation: " + block.getLocation().toString() + "\n" +
                                                                "ChestType: " + block.getType() +
                                                                "\n* Type -> !approveAll | If you are ok with this user accessing any of your chests and want to add them to your list of approved chest users\n" +
                                                                "* Type -> !approveChest | If you want to approve this user to be able to use only this chest\n" +
                                                                "* Type -> !deny | If are NOT ok with this user accessing your chests and want to report this player\n" +
                                                                "* Type -> !ignoreChest | If you no longer want to be notified if someone takes from this chest\n" +
                                                                "* Type -> !ignoreAll | If you no longer want to be notified if someone takes from any of your chests");
                                                    }).queue(null, new ErrorHandler()
                                                            .handle(ErrorResponse.CANNOT_SEND_TO_USER,
                                                                    (ex) -> Api.messageInConsole(ChatColor.GOLD + "[ChestProtect] - " + ChatColor.RED + "Failed to send message to " + chestOwner.getName())));
                                                } catch (NullPointerException ignore) {

                                                }

                                            }
                                        }
                                    }
                                } catch (NullPointerException ignored) {
                                }
                            }
                        }
                    }
                }
            }
        } catch (IllegalArgumentException ignored) {
        }
    }

    private void setMasterAndSortChests(PlayerInteractEvent event) throws IOException {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType().equals(Material.CHEST)) {
            if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta() != null &&
                    event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getLore() != null) {
                FileConfiguration autoSorterCfg = Api.getFileConfiguration(YmlFileNames.YML_AUTO_SORTER_FILE_NAME);
                String playerName = event.getPlayer().getName().toLowerCase();
                Location chestLocation = event.getClickedBlock().getLocation();
                double chestX = chestLocation.getX();
                double chestY = chestLocation.getY();
                double chestZ = chestLocation.getZ();
                String worldName = Objects.requireNonNull(chestLocation.getWorld()).getName();
                if (worldName.equalsIgnoreCase("world")) {
                    if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getLore().get(0).toLowerCase().contains("autosorter master stick")) {
                        autoSorterCfg.set(playerName + ".masterChest.x", chestX);
                        autoSorterCfg.set(playerName + ".masterChest.y", chestY);
                        autoSorterCfg.set(playerName + ".masterChest.z", chestZ);
                        event.getPlayer().sendMessage(ChatColor.GREEN + "Master Chest has been set.");
                    }
                    if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getLore().get(0).toLowerCase().contains("chest sorting stick")) {
                        Material itemToSort = event.getPlayer().getInventory().getItemInOffHand().getType();
                        if (itemToSort.equals(Material.AIR)) {
                            event.getPlayer().sendMessage(ChatColor.YELLOW + "Can't sort AIR. Place an item in your off hand and click on the chest again.");
                        } else {
                            autoSorterCfg.set(playerName + "." + itemToSort + ".x", chestX);
                            autoSorterCfg.set(playerName + "." + itemToSort + ".y", chestY);
                            autoSorterCfg.set(playerName + "." + itemToSort + ".z", chestZ);
                            event.getPlayer().sendMessage(ChatColor.GREEN + "Chest to sort " + ChatColor.GOLD + itemToSort + ChatColor.GREEN + " has been set.");
                        }
                    }
                }
                autoSorterCfg.save(Api.getConfigFile(YmlFileNames.YML_AUTO_SORTER_FILE_NAME));
            }
            Chest chest = (Chest) event.getClickedBlock().getState();
//            if (chest.getCustomName() != null){
//                String chestName = chest.getCustomName();
//                String[] chestNameArr = chestName.split("'s Chest");
//                String playerName = chestNameArr[0];
//
//            }
        }

    }

    private void sendItemsInAutoSorter(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        String playerName = player.getName().toLowerCase();
        Location chestLocation = e.getInventory().getLocation();
        FileConfiguration autoSorterCfg = Api.getFileConfiguration(YmlFileNames.YML_AUTO_SORTER_FILE_NAME);
        Location masterChestLocation = new Location(Bukkit.getWorld("world"), autoSorterCfg.getDouble(playerName + ".masterChest.x"), autoSorterCfg.getDouble(playerName + ".masterChest.y"), autoSorterCfg.getDouble(playerName + ".masterChest.z"));
        if (chestLocation != null && chestLocation.equals(masterChestLocation)) {
            Inventory masterChestInventory = e.getInventory();
            int count = 0;
            boolean itemsSent = false;
            for (ItemStack is : masterChestInventory) {
                if (is != null && autoSorterCfg.isSet(playerName + "." + is.getType())) {
                    double chestToX = autoSorterCfg.getDouble(playerName + "." + is.getType() + ".x");
                    double chestToY = autoSorterCfg.getDouble(playerName + "." + is.getType() + ".y");
                    double chestToZ = autoSorterCfg.getDouble(playerName + "." + is.getType() + ".z");
                    Location chestSortLocation = new Location(Bukkit.getWorld("world"), chestToX, chestToY, chestToZ);
                    Material chest = chestSortLocation.getBlock().getType();
                    if (chest.equals(Material.CHEST)) {
                        Chest sortChest = (Chest) chestSortLocation.getBlock().getState();
                        sortChest.getInventory().addItem(is);
                        masterChestInventory.setItem(count, new ItemStack(Material.AIR, 1));
                        itemsSent = true;
                    }
                }
                count++;
            }
            if (Boolean.TRUE.equals(itemsSent)) {
                player.sendMessage(ChatColor.GREEN + "Items sent to sorted chest(s).");
            }
        }
    }

//    @EventHandler
//    public void onVillagerSpawn(EntitySpawnEvent e){
//        if (e.getEntityType().equals(EntityType.VILLAGER)){
//            Villager villager = (Villager) e.getEntity();
//            villager.setProfession(Villager.Profession.LIBRARIAN);
//            ArrayList<MerchantRecipe> mrl = new ArrayList<>();
//            ItemStack mendingBook = new ItemStack(Material.ENCHANTED_BOOK, 1);
//            EnchantmentStorageMeta esm = (EnchantmentStorageMeta) mendingBook.getItemMeta();
//            assert esm != null;
//            esm.addStoredEnchant(Enchantment.MENDING, 1, true);
//            mendingBook.setItemMeta(esm);
//// do stuff with the meta, check the javadocs for method
//            mendingBook.setItemMeta(esm);
//            MerchantRecipe recipe = new MerchantRecipe(mendingBook, 5, 25, true);
//            recipe.addIngredient(new ItemStack(Material.EMERALD, 1));
//            mrl.add(recipe);
//            villager.setRecipes(mrl);
//        }
//    }

//    @EventHandler
//    public void onLockCraft(PrepareItemCraftEvent event) {
//            Material blockToCompress = Material.COBBLESTONE;
//
//            boolean isCompressable = false;
//            for (int i = 0; i < event.getInventory().getMatrix().length; i++){
//                if (event.getInventory().getMatrix()[i] != null && event.getInventory().getMatrix()[i].getType().equals(blockToCompress) && event.getInventory().getMatrix()[i].getAmount() == 64){
//                    isCompressable = true;
//                }
//            }
//            if (Boolean.TRUE.equals(isCompressable)){
//                ItemStack compressedBlock = new ItemStack(blockToCompress, 1);
//                ItemMeta im = compressedBlock.getItemMeta();
//                ArrayList<String> lore = new ArrayList<>();
//                lore.add("Compressed " + compressedBlock.getType());
//                assert im != null;
//                im.setLore(lore);
//                compressedBlock.setItemMeta(im);
//                event.getInventory().setResult(compressedBlock);
//                ItemStack[] isa = new ItemStack[9];
//                for (int i = 0; i < event.getInventory().getMatrix().length; i++){
//                    isa[i] = new ItemStack(Material.AIR, 1);
//                }
//                event.getInventory().setMatrix(isa);
//            }
//    }
//
//    public void addCompressedBlockRecipe(){
//
//    }


}

