package lmp;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
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
import lmp.listeners.discord.BanAndLogBanChestThief;
import lmp.listeners.entityDeathEvents.DisableEntityCrammingEvent;
import lmp.listeners.entityDeathEvents.LogVillagerDeathEvent;
import lmp.listeners.onInventoryOpenEvents.RemoveMendingFromVillagerTrade;
import lmp.listeners.onLootGenerateEvents.AddMendingBookToGeneratedLoot;
import lmp.listeners.playerAdvancementDoneEvents.PlayerAdvancementEvent;
import lmp.listeners.playerBedEnterEvents.SetPlayerBedLocation;
import lmp.listeners.playerBreakBlockEvents.BanThiefForBreakingBanChest;
import lmp.listeners.playerCommandPreprocessEvents.CancelCommandsOfRacer;
import lmp.listeners.playerCommandPreprocessEvents.LogPlayerBanFromServerCommandEvent;
import lmp.listeners.playerDeathEvents.*;
import lmp.listeners.playerFishCaughtEvents.GivePlayerMoneyWhenFishCaughtEvent;
import lmp.listeners.playerInteractEntityEvents.GiveJerryArmorEvent;
import lmp.listeners.playerInteractEvents.*;
import lmp.listeners.playerJoinEvents.*;
import lmp.listeners.playerMoveEvents.DenyMoveForUnlinkedPlayerEvent;
import lmp.listeners.playerMoveEvents.FreezeLatchOnTwitchRewardClaim;
import lmp.listeners.playerQuitEvents.*;
import lmp.listeners.playerRespawnEvents.SetPlayerLocationOnAnarchy;
import lmp.listeners.vehicleExitEvents.PreventRacerFromLeavingBoat;
import lmp.runnable.LMPTimer;
import lmp.runnable.PingTabListRunnable;
import lmp.tabComplete.*;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.luckperms.api.LuckPerms;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
import java.io.OutputStream;
import java.net.URI;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import static lmp.api.Api.getMainPlugin;


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
        new BanAndLogBanChestThief(this);
        new DisableAutoSmeltStickFromBurning(this);
        new SetPlayerWorldOfDeath(this);
        new SetPlayerLocationOnAnarchy(this);
        new SetPlayerBedLocation(this);
//        For Season 7
        new RemoveMendingFromVillagerTrade(this);
        new AddMendingBookToGeneratedLoot(this);
        new BedrockBreakerEvent(this);
        new InstaBreakGlassEvent(this);
        new GivePlayerMoneyWhenFishCaughtEvent(this);
        new SendPlayerDeathCoordinates(this);
        new RespawnAnchorSetEvent(this);
        new TeleportPlayerToWorldFromHubButtons(this);
        new DenyInteractForUnlinkedPlayerEvent(this);
        new AutoSellChest(this);
//        new AddBlockBrokenToDB(this);
        new BanThiefForBreakingBanChest(this);
        new FreezeLatchOnTwitchRewardClaim(this);
        new StartBoatRace(this);
        new PreventRacerFromLeavingBoat(this);
        new CancelCommandsOfRacer(this);
        new EnableBoatRaceIfActiveOnRacerLogoutEvent(this);
        new EndBoatRace(this);
//        new CustomRecipeMoveItemEvent(this);
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
//        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
//        try {
//            HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8001), 0);
//            server.createContext("/test", new MyHttpHandler());
//            server.setExecutor(threadPoolExecutor);
//            server.start();
//            log.info(" Server started on port 8001");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        HttpServer server = null;
//        try {
//            server = HttpServer.create(new InetSocketAddress(8002), 0);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        HttpContext context = server.createContext("/example");
//        context.setHandler(Main::handleRequest);
//        server.start();
        try {
            DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private static void handleRequest(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        printRequestInfo(exchange);
        String response = "This is the response at " + requestURI;
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private static void printRequestInfo(HttpExchange exchange) {
        System.out.println("-- headers --");
        Headers requestHeaders = exchange.getRequestHeaders();
        requestHeaders.entrySet().forEach(System.out::println);

        System.out.println("-- principle --");
        HttpPrincipal principal = exchange.getPrincipal();
        System.out.println(principal);

        System.out.println("-- HTTP method --");
        String requestMethod = exchange.getRequestMethod();
        System.out.println(requestMethod);

        System.out.println("-- query --");
        URI requestURI = exchange.getRequestURI();
        String query = requestURI.getQuery();
        System.out.println(query);
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
        // Anarchy Commands
        Objects.requireNonNull(this.getCommand(ServerCommands.HARDCORE_COMMAND)).setExecutor(new HardcoreCommand());
        Objects.requireNonNull(this.getCommand(ServerCommands.HARDCORE_COMMAND)).setTabCompleter(new HardcoreTabComplete());

        Objects.requireNonNull(this.getCommand(ServerCommands.ANARCHY_COMMAND)).setExecutor(new AnarchyCommand());
        Objects.requireNonNull(this.getCommand(ServerCommands.ANARCHY_COMMAND)).setTabCompleter(new AnarchyTabComplete());

    }

    @EventHandler
    public void onCommandEvent(PlayerCommandPreprocessEvent event) throws IOException, ExecutionException, InterruptedException {
        if (Boolean.FALSE.equals(getIsParameterInTesting("onCommandEvent"))) {
            if (event.getMessage().equalsIgnoreCase("/back")) {
                Api.denyBackIntoXPFarm(event);
            }
            String worldName = event.getPlayer().getWorld().getName();
            if (!event.getPlayer().isOp() && (worldName.contains("hardcore") || worldName.contains("anarchy") || worldName.contains("classic") || worldName.contains("OneBlock") || worldName.contains("Skyblock"))) {
                event.setCancelled(Api.denyCommandInMultiverseWorlds(event));
            }
        }
        if (event.getMessage().equalsIgnoreCase("/spawn") || event.getMessage().equalsIgnoreCase("/lmp hub")) {
            if (event.getPlayer().getWorld().getName().contains("hardcore") ) {
                Api.setHardcorePlayerLocation(event);
            }
            if (event.getPlayer().getWorld().getName().contains("anarchy") ) {
                Api.setAnarchyPlayerLocation(event);
            }
            if (event.getPlayer().getWorld().getName().contains("creative") ) {
                Api.setCreativePlayerLocation(event);
            }
            if (event.getPlayer().getWorld().getName().contains("classic") ) {
                Api.setClassicPlayerLocation(event);
            }
            if (event.getPlayer().getWorld().getName().contains("OneBlock") ) {
                Api.setOneBlockPlayerLocation(event);
                event.getPlayer().performCommand("/ob progress_bar false");
            }
            if (event.getPlayer().getWorld().getName().contains("Skyblock") ) {
                Api.setSkyBlockPlayerLocation(event);
            }
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
            if (event.getClickedBlock() != null && event.getClickedBlock().getType().equals(Material.OAK_BUTTON)) {
                RandomItem.getRandomItem1(event);
                RandomItem.getRandomItem2(event);
//            RandomItem.getRandomItem3(event);
            }
            try {
                QuickSmelt.quickSmelt(event.getPlayer(), Api.getEconomy(), event);
                QuickBrew.quickBrew(event.getPlayer(), Api.getEconomy(), event);
            } catch (NullPointerException ignored) {

            }
            MobileSpawner.disableSpawnerMobChange(event);
            SlimeChunkFinder.isSlimeChunk(event);
            if (event.getAction().toString().equals("RIGHT_CLICK_BLOCK") && Objects.requireNonNull(event.getClickedBlock()).getType().equals(Material.CRIMSON_BUTTON)) {
                BossBattle.startBossBattle(event);
            }
//            Api.denyOpenChestDuringBossBattle(event);
            CustomPortals.setBlockToNetherPortal(event);
            XPFarm.teleportPlayerToXPFarm(event);
            XPFarm.teleportPlayerToSpawn(event);
            if (Boolean.TRUE.equals(LMPCommand.isPlayerHoldingXPStorageBottle(event.getPlayer()) && (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)))) {
                event.getPlayer().sendMessage(ChatColor.RED + "You should be more careful. Don't throw away your Experience Storage Bottle. Switch to another item to be safe");
                event.setCancelled(true);
            }
        }
        if (!event.getPlayer().isOp() && event.getPlayer().getWorld().getName().contains("hardcore")) {
            FileConfiguration hardcoreCfg = Api.getFileConfiguration(YmlFileNames.YML_HARDCORE_FILE_NAME);
            if (hardcoreCfg.getBoolean(event.getPlayer().getUniqueId().toString() + ".isAlive")) {
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

//    @EventHandler
//    public void onRaidTriggerEvent(RaidFinishEvent e){
//        if (e.getWorld().getName().equalsIgnoreCase("world")) {
//            for (Player player : e.getWinners()) {
//                String connectionUrl = "jdbc:sqlserver://DESKTOP-CQKVEGP:1433;databaseName=lmp;trustServerCertificate=true;encrypt=true;username=minecraft;password=password";
//                Connection connection;
//                try {
//                    connection = DriverManager.getConnection(connectionUrl);
//                    String insertsql = " insert into raid_wins (minecraft_id, timestamp, minecraft_name)"
//                            + " values (?, ?, ?)";
//                    PreparedStatement preparedStmt = connection.prepareStatement(insertsql);
//                    preparedStmt.setString(1, player.getUniqueId().toString());
//                    preparedStmt.setTimestamp(2, new Timestamp(new java.util.Date().getTime()));
//                    preparedStmt.setString(3, player.getName());
//                    preparedStmt.execute();
//                    connection.close();
//                } catch (SQLException err) {
//                    throw new RuntimeException(err);
//                }
//                player.sendMessage(ChatColor.GREEN + "Congratulations on your Raid Win!!!");
//                player.sendMessage(ChatColor.GREEN + "You've increased your Raid Win score by 1!!!");
//            }
//        }
//    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) throws IOException {
        if (Boolean.FALSE.equals(getIsParameterInTesting("onInventoryClose"))) {
            Player player = (Player) e.getPlayer();
            if (e.getView().getTitle().equals(player.getName() + lmp.Constants.YML_POSSESSIVE_BACKPACK)) {
                Inventories.saveCustomInventory(e, new File(getMainPlugin().getDataFolder() + "/playerBackpacks/", Api.getMinecraftIdFromMinecraftName(e.getView().getTitle().split("'s Backpack")[0]) + ".yml"));
            } else if (e.getView().getTitle().equals(player.getName() + lmp.Constants.YML_POSSESSIVE_PLAYER_SHOP)) {
                Inventories.saveCustomInventory(e, Api.getConfigFile(YmlFileNames.YML_PLAYER_SHOP_FILE_NAME));
            }
            PlayerShops.removeLoreFromSellerInventory(e, Api.getConfigFile(YmlFileNames.YML_PLAYER_SHOP_FILE_NAME));
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

    @EventHandler(priority = EventPriority.LOWEST)
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
        if (event.getBlock().getType().equals(Material.CREEPER_HEAD) || event.getBlock().getType().equals(Material.CREEPER_WALL_HEAD)) {
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
        if (event.getBlock().getType().equals(Material.WITHER_SKELETON_SKULL) || event.getBlock().getType().equals(Material.WITHER_SKELETON_WALL_SKULL)) {
            FileConfiguration stopSpawnerCfg = Api.getFileConfiguration(YmlFileNames.YML_SPAWN_STOPPER_FILE_NAME);
            String stopSpawnerLocation = event.getBlock().getLocation().toString();
            List<String> spawnStopperLocationList = new ArrayList<>();
            if (!stopSpawnerCfg.getStringList("locations").isEmpty()) {
                spawnStopperLocationList = stopSpawnerCfg.getStringList("locations");
            }
            int indexToDelete = -1;
            for (int i = 0; i < spawnStopperLocationList.size(); i++) {
                if (spawnStopperLocationList.get(i).equalsIgnoreCase(stopSpawnerLocation)) {
                    indexToDelete = i;
                }
            }
            if (indexToDelete != -1) {
                spawnStopperLocationList.remove(indexToDelete);
            }
            stopSpawnerCfg.set("locations", spawnStopperLocationList);
            stopSpawnerCfg.save(Api.getConfigFile(YmlFileNames.YML_SPAWN_STOPPER_FILE_NAME));

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
        Api.spawnStopper(e);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) throws IOException {
        event.setCancelled(Api.cancelJrModEvent(event.getPlayer().getUniqueId()));
        if (Boolean.FALSE.equals(getIsParameterInTesting("onBlockPlace"))) {
            Api.cancelEventsInPreviousSeason(event.getPlayer().getWorld().getName(), event.getPlayer().getName(), null, event, null, null);
            MobileSpawner.setSpawnerOnPlace(event, Api.getEconomy());
            Api.placeBlockLog(event);
        }
        if (event.getBlock().getType().equals(Material.CREEPER_HEAD) || event.getBlock().getType().equals(Material.CREEPER_WALL_HEAD)) {
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
        if (event.getBlock().getType().equals(Material.WITHER_SKELETON_SKULL)) {
            if (event.getItemInHand().getItemMeta() != null && event.getItemInHand().getItemMeta().getLore() != null) {
                if (event.getItemInHand().getItemMeta().getLore().get(0).equalsIgnoreCase("SpawnerStop")) {
                    FileConfiguration creeperCfg = Api.getFileConfiguration(YmlFileNames.YML_SPAWN_STOPPER_FILE_NAME);
                    List<String> creeperLocationList = new ArrayList<>();
                    if (!creeperCfg.getStringList("locations").isEmpty()) {
                        creeperLocationList = creeperCfg.getStringList("locations");
                    }
                    creeperLocationList.add(event.getBlock().getLocation().toString());
                    creeperCfg.set("locations", creeperLocationList);
                    creeperCfg.save(Api.getConfigFile(YmlFileNames.YML_SPAWN_STOPPER_FILE_NAME));
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
                List<String> worldToProtectList = new ArrayList<>();
                worldToProtectList.add("world");
                worldToProtectList.add("world_nether");
                worldToProtectList.add("world_the_end");
                worldToProtectList.add("hardcore");
                worldToProtectList.add("hardcore_nether");
                worldToProtectList.add("hardcore_the_end");
                worldToProtectList.add("classic");
                worldToProtectList.add("classic_nether");
                worldToProtectList.add("classic_the_end");
                String chestWorld = Objects.requireNonNull(event.getWhoClicked().getLocation().getWorld()).getName();
                if (worldToProtectList.contains(chestWorld)) {
                    if (event.getClickedInventory() != null && event.getClickedInventory().getHolder() != null && worldToProtectList.contains(event.getWhoClicked().getLocation().getWorld().getName()) && (event.getClickedInventory().getHolder() instanceof DoubleChest || event.getClickedInventory().getHolder() instanceof Chest || event.getClickedInventory().getHolder() instanceof ShulkerBox || event.getClickedInventory().getHolder() instanceof Barrel)) {
                        OfflinePlayer olp = null;
                        if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
                            FileConfiguration chestProtectCfg = Api.getFileConfiguration(YmlFileNames.YML_CHEST_PROTECT_FILE_NAME);
                            List<String> ignoreChestArr = chestProtectCfg.getStringList("ignoredChests");
                            Player clicker = (Player) event.getWhoClicked();
                            Block block = Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(Objects.requireNonNull(event.getClickedInventory().getLocation()).getWorld()).getUID())).getBlockAt(event.getClickedInventory().getLocation());
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
                                        if (Api.getFileConfiguration(YmlFileNames.YML_CHEST_PROTECT_FILE_NAME).getStringList("players." + clicker.getUniqueId().toString() + ".approvedPlayers").isEmpty()) {
                                            ChestProtect.setApprovedAllPlayer(Api.getDiscordIdFromMCid(olp.getUniqueId().toString()), clicker.getName(), Api.getFileConfiguration(YmlFileNames.YML_CHEST_PROTECT_FILE_NAME));
                                        }
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

