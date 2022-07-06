package lmp;

import io.donatebot.api.DBClient;
import io.donatebot.api.Donation;
import lmp.Backbacks.BackPackCommand;
import lmp.Backbacks.BackPackInventoryConfig;
import lmp.Backbacks.BackpackTabComplete;
import lmp.Backbacks.Inventories;
import lmp.Bank.Bank;
import lmp.Bank.BankConfig;
import lmp.Configurations.*;
import lmp.LatchTwitchBot.LatchTwitchBotCommand;
import lmp.LatchTwitchBot.LatchTwitchBotConfig;
import lmp.DiscordText.LMPCommand;
import lmp.DiscordText.LMPConfig;
import lmp.LatchTwitchBot.LatchTwitchBotTabComplete;
import lmp.PlayerShops.*;
import io.ipgeolocation.api.IPGeolocationAPI;
import lmp.PlayerShops.LMPCommandTabComplete;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import net.dv8tion.jda.api.entities.UserSnowflake;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeEqualityPredicate;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.util.Tristate;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.joda.time.DateTime;

import javax.security.auth.login.LoginException;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static lmp.LatchDiscord.jda;


public class Main extends JavaPlugin implements Listener {
    public static final boolean GLOBAL_TESTING = false;
    public static LuckPerms luckPerms;
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
    private static LMPConfig discordTextConfigCfgm;
    // Advancement Config
    private static AdvancementConfig advancementConfigCfgm;
    // Lottery Config
    private static LotteryConfig lotteryConfigCfgm;
    // Twitch Config
    private static LatchTwitchBotConfig twitchBotCfgm;
    // AutoSorter Config
    private static AutoSorterConfig autoSorterCfgm;
    // Boss Config
    private static BossConfig bossCfgm;

    public static IPGeolocationAPI ipApi;
    public static DBClient dbClient;


    @Override
    public void onEnable() {
        getLogger().info("discord_text is enabled");
        getServer().getPluginManager().registerEvents(this, this);
        if (Boolean.FALSE.equals(getIsParameterInTesting("startDiscord"))) {
            try {
                new LatchDiscord();
            } catch (LoginException e) {
                e.printStackTrace();
            }
        }
        whitelistCfgm = new WhitelistConfig();
        playerShopsInventoryCfgm = new PlayerShopsInventoryConfig();
        bankCfgm = new BankConfig();
        backPackInventoryCfgm = new BackPackInventoryConfig();
        discordTextConfigCfgm = new LMPConfig();
        advancementConfigCfgm = new AdvancementConfig();
        lotteryConfigCfgm = new LotteryConfig();
        twitchBotCfgm = new LatchTwitchBotConfig();
        autoSorterCfgm = new AutoSorterConfig();
        bossCfgm = new BossConfig();
        Api.setupEconomy(getServer().getPluginManager().getPlugin("Vault"));
        loadAllConfigManagers();

        Advancements.setAdvancements();
        // Backpack Command
        Objects.requireNonNull(this.getCommand("bp")).setExecutor(new BackPackCommand());
        Objects.requireNonNull(this.getCommand("bp")).setTabCompleter(new BackpackTabComplete());

        // Player Shop Command
        Objects.requireNonNull(this.getCommand("ps")).setExecutor(new PlayerShopsCommand());
        Objects.requireNonNull(this.getCommand("ps")).setTabCompleter(new PlayerShopsTabComplete());

        // Server Command
        Objects.requireNonNull(this.getCommand("lmp")).setExecutor(new LMPCommand());
        Objects.requireNonNull(this.getCommand("lmp")).setTabCompleter(new LMPCommandTabComplete());
        // Discord Staff Chat Command
        Objects.requireNonNull(this.getCommand("dtsc")).setExecutor(new DiscordStaffChatCommand());

        // Twitch Bot Command
        Objects.requireNonNull(this.getCommand("twitch")).setExecutor(new LatchTwitchBotCommand());
        Objects.requireNonNull(this.getCommand("twitch")).setTabCompleter(new LatchTwitchBotTabComplete());
        this.registerTasks();
        // Auto Miner Commands
//        Objects.requireNonNull(this.getCommand("am")).setExecutor(new AutoMinerCommand());
//        Objects.requireNonNull(this.getCommand("am")).setTabCompleter(new AutoMinerTabComplete());
        addRottenFleshToLeatherSmelt();
        addSculkSensorRecipe();
        addBundleRecipe();
        addLatchAppleRecipe();
        if (Boolean.FALSE.equals(getIsParameterInTesting("runTimer"))) {
            LMPTimer.runTimer();
        }
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
        }
        ipApi = new IPGeolocationAPI("07eecf88b7f2468e90fe0326af707d66");
        dbClient = new DBClient("625983914049142786", "PlXK5QShFmmlb4q9qILoRc1lXqLsZVG72aOEnAiaaQi2oXfAI5X5EuIRLGBp1qa");

    }
    public static void getDonations(){
        String[] statuses = {"Completed", "Reversed", "Refunded"};

        CompletableFuture<Donation[]> future = dbClient.getNewDonations(statuses);

// Non-blocking Example
        CompletableFuture.runAsync(() -> {
            try {
                FileConfiguration donationsCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_DONATION_FILE_NAME));
                // Array of Donation objects
                List<Donation> list = Arrays.asList(future.get());

                List<Donation> sorted = list.stream()
                        .sorted(Comparator.comparing(Donation::getDate))
                        .collect(Collectors.toList());
                try {
                    if (donationsCfg.getInt("numberOfDonations") < sorted.size()) {
                        int previousDonationCount = donationsCfg.getInt("numberOfDonations");
                        int currentDonationCount = sorted.size();
                        for (int i = previousDonationCount; i <= currentDonationCount - 1; i++) {
                            String discordID = "123";
                            if (sorted.get(i).getSellerCustoms().get("What is your Discord User ID?") != null && !sorted.get(i).getSellerCustoms().get("What is your Discord User ID?").isEmpty()){
                                discordID = sorted.get(i).getSellerCustoms().get("What is your Discord User ID?");
                            }
                            String transactionId = sorted.get(i).getTransactionID();
                            String buyerID = sorted.get(i).getBuyerID();
                            String buyerEmail = sorted.get(i).getBuyerEmail();
                            String productID = sorted.get(i).getProductID();
                            String price = sorted.get(i).getPrice();
                            Boolean isRecurring = sorted.get(i).getRecurring();
                            Date purchaseDate = sorted.get(i).getDate();
                            donationsCfg.set(Constants.YML_DONATIONS + transactionId + ".transactionID", transactionId);
                            donationsCfg.set(Constants.YML_DONATIONS + transactionId + ".buyerID", buyerID);
                            donationsCfg.set(Constants.YML_DONATIONS + transactionId + ".buyerEmail", buyerEmail);
                            donationsCfg.set(Constants.YML_DONATIONS + transactionId + ".roleID", sorted.get(i).getRoleID());
                            donationsCfg.set(Constants.YML_DONATIONS + transactionId + ".productID", productID);
                            donationsCfg.set(Constants.YML_DONATIONS + transactionId + ".price", price);
                            donationsCfg.set(Constants.YML_DONATIONS + transactionId + ".isRecurring", isRecurring);
                            donationsCfg.set(Constants.YML_DONATIONS + transactionId + ".date", purchaseDate);
                            donationsCfg.set(Constants.YML_DONATIONS + transactionId + ".discordID", discordID);
                            if (productID.equalsIgnoreCase(Constants.DONOR_PRODUCT_ID) && !discordID.equalsIgnoreCase("123")){
                                String donorRoleName = "";
                                String minecraftID = Api.getMinecraftIdFromDCid(discordID);
                                if (Double.parseDouble(price) < 5.00){
                                    donorRoleName = "Donor";
                                    Api.addPlayerToPermissionGroup(minecraftID, "donor");
                                    LatchDiscord.getJDA().getGuildById(Constants.GUILD_ID).addRoleToMember(UserSnowflake.fromId(discordID), Objects.requireNonNull(jda.getRoleById("994064302866714765"))).queue();
                                } else if (Double.parseDouble(price) > 9.99) {
                                    donorRoleName = "Donor+";
                                    Api.addPlayerToPermissionGroup(minecraftID, "donor+");
                                    LatchDiscord.getJDA().getGuildById(Constants.GUILD_ID).addRoleToMember(UserSnowflake.fromId(discordID), Objects.requireNonNull(jda.getRoleById("994064735194599494"))).queue();
                                } else {
                                    donorRoleName = "Donor++";
                                    Api.addPlayerToPermissionGroup(minecraftID, "donor++");
                                    LatchDiscord.getJDA().getGuildById(Constants.GUILD_ID).addRoleToMember(UserSnowflake.fromId(discordID), Objects.requireNonNull(jda.getRoleById("994064561399398480"))).queue();
                                }
                                OfflinePlayer minecraftMember = Bukkit.getOfflinePlayer(UUID.fromString(minecraftID));
                                donationsCfg.set(Constants.YML_MEMBERS + discordID + ".discordName", LatchDiscord.getJDA().getGuildById(Constants.GUILD_ID).getMemberById(discordID).getUser().getName());
                                donationsCfg.set(Constants.YML_MEMBERS + discordID + ".discordID", discordID);
                                donationsCfg.set(Constants.YML_MEMBERS + discordID + ".minecraftName", minecraftMember.getName());
                                donationsCfg.set(Constants.YML_MEMBERS + discordID + ".minecraftID", minecraftID);
                                donationsCfg.set(Constants.YML_MEMBERS + discordID + ".donorRole", donorRoleName);
                                donationsCfg.set(Constants.YML_MEMBERS + discordID + ".donorPurchaseDate", purchaseDate);
                                DateTime dateTime = new DateTime(purchaseDate);
                                donationsCfg.set(Constants.YML_MEMBERS + discordID + ".donorEndDate", dateTime.plusDays(30).getMillis());
                                donationsCfg.set(Constants.YML_MEMBERS + discordID + ".purchases.donations." + transactionId, transactionId);
                            }
                        }
                        donationsCfg.set("numberOfDonations", sorted.size());
                        donationsCfg.save(Api.getConfigFile(Constants.YML_DONATION_FILE_NAME));
                    }
                } catch (ArrayIndexOutOfBoundsException e ) {
                    System.out.println("ARRAYDONATIONERROR: " + e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }
    @EventHandler
    public void EntityDamageEvent(EntityDamageEvent e){
        BossBattle.bossHurtEvent(e);

    }

    @EventHandler
    public void EntityDeathEvent(EntityDeathEvent e) throws IOException {
        if (e.getEntity().getType().equals(EntityType.VILLAGER)){
            FileConfiguration villagerHurtLog = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_VILLAGER_HURT_LOG_FILE_NAME));
            Date date = new Date();
            Villager villager = (Villager) e.getEntity();
            String id = "other";
            if (e.getEntity().getKiller() != null) {
                id = e.getEntity().getKiller().getUniqueId().toString();
                villagerHurtLog.set(id + ".playerName", e.getEntity().getKiller().getName());
            }
            villagerHurtLog.set(id + "." + date + ".type", villager.getProfession().toString());
            villagerHurtLog.set(id + "." + date + ".location.x", e.getEntity().getLocation().getBlockX());
            villagerHurtLog.set(id + "." + date + ".location.y", e.getEntity().getLocation().getBlockY());
            villagerHurtLog.set(id + "." + date + ".location.z", e.getEntity().getLocation().getBlockZ());
            villagerHurtLog.save(Api.getConfigFile(Constants.YML_VILLAGER_HURT_LOG_FILE_NAME));
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) throws IOException {
        BossBattle.bossBattleEnded(e);
    }


    public DBClient getDbClient(){
        return dbClient;
    }

    private void registerTasks() {
        FileConfiguration configCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_CONFIG_FILE_NAME));
        Long delay = configCfg.getLong("tablist-delay");
        new PingTabList(this).runTaskTimerAsynchronously(this, delay * 20, delay * 20);
    }

    @Override
    public void onDisable() {
        if (Boolean.FALSE.equals(getIsParameterInTesting("onDisable"))) {
            Api.stopAllTwitchBots(LatchTwitchBotCommand.twitchBotList);
            getLogger().info("discord_text is disabled");
            LatchDiscord.stopBot();
            LatchDiscord.sendServerStoppedMessage();
        }
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent e) throws IOException {
        if (Boolean.FALSE.equals(getIsParameterInTesting("onLogin"))) {
            LatchDiscord.sendPlayerOnJoinMessage(e);
            Bank.setLoginTime(e);
            Bank.getPlayerBalance(e.getPlayer());
            Bank.setPlayerBalanceInConfigOnLogin(e.getPlayer());
            Bank.showLastSessionReward(e.getPlayer());
            Advancements.setPlayerCompletedAdvancementsOnLogin(e.getPlayer());
            Api.checkPlayerMemberStatus(e.getPlayer());
            Api.updateUserInfo(e.getPlayer());
        }
    }


    @EventHandler
    public void onLogout(PlayerQuitEvent event) throws IOException {
        if (Boolean.FALSE.equals(getIsParameterInTesting("onLogout"))) {
            LatchDiscord.sendPlayerLogoutMessage(event);
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
//            if (Objects.requireNonNull(e.getDeathMessage()).contains("Super Jerry")) {
//                LatchDiscord.getJDA().getTextChannelById(Constants.DISCORD_STAFF_CHAT_CHANNEL_ID).sendMessage("<@" + LatchDiscord.getDiscordUserId(LatchDiscord.getDiscordUserName(e.getEntity().getName())) + "> - AKA: " + e.getEntity().getName() + " tried to hurt Super Jerry.").queue();
//            }
            if (e.getEntity().getKiller() == null && Boolean.TRUE.equals(Api.getFileConfiguration(Api.getConfigFile(Constants.YML_CONFIG_FILE_NAME)).getBoolean("doesPlayerLoseMoneyOnDeath"))){
                DecimalFormat df = new DecimalFormat("0.00");
                double playerBalance = Api.getEconomy().getBalance(Api.getOfflinePlayerFromPlayer(e.getEntity()));
                double percentToRemove = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_CONFIG_FILE_NAME)).getDouble("deathBalancePercentage");
                double amountToRemove = (playerBalance / 100.00) * percentToRemove;
                Api.getEconomy().withdrawPlayer(Api.getOfflinePlayerFromPlayer(e.getEntity()), amountToRemove);
                e.getEntity().sendMessage(ChatColor.YELLOW + "You lost " + ChatColor.RED + "$" + df.format(amountToRemove) + ChatColor.YELLOW + " because you died.");
            }
        }

    }

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent e) {
        FileConfiguration bossCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_BOSS_FILE_NAME));
        if (e.getRightClicked().getType()==EntityType.GIANT) {
            Monster monster = (Monster) e.getRightClicked();
            String boss = "bosses.zombieBoss";
            Objects.requireNonNull(monster.getEquipment()).setHelmet(new ItemStack(Objects.requireNonNull(bossCfg.getItemStack(boss + ".bossHelmet"))));
            Objects.requireNonNull(monster.getEquipment()).setChestplate(Objects.requireNonNull(bossCfg.getItemStack(boss + ".bossChestplate")));
            Objects.requireNonNull(monster.getEquipment()).setBoots(Objects.requireNonNull(bossCfg.getItemStack(boss + ".bossBoots")));
            Objects.requireNonNull(monster.getEquipment()).setLeggings(Objects.requireNonNull(bossCfg.getItemStack(boss + ".bossLeggings")));
            Objects.requireNonNull(monster.getEquipment()).setItemInMainHand(Objects.requireNonNull(bossCfg.getItemStack(boss + ".bossWeapon")));
        }
    }

    @EventHandler
    public void onPlayerChatEvent(AsyncPlayerChatEvent e){
        if (Boolean.FALSE.equals(getIsParameterInTesting("onPlayerChatEvent"))){
            if(Boolean.TRUE.equals(Api.isPlayerInvisible(e.getPlayer().getUniqueId().toString()))){
                Objects.requireNonNull(jda.getTextChannelById(Constants.DISCORD_STAFF_CHAT_CHANNEL_ID)).sendMessage(Api.convertMinecraftMessageToDiscord(e.getPlayer().getDisplayName(), e.getMessage())).queue();
                for (Player player : Bukkit.getOnlinePlayers()){
                    if (player.hasPermission("group.jr-mod")){
                        player.sendMessage("[" + ChatColor.LIGHT_PURPLE + "DTSC" + ChatColor.WHITE + "] - " + ChatColor.GOLD + e.getPlayer().getDisplayName() + ChatColor.WHITE + " » " + ChatColor.AQUA + e.getMessage());
                    }
                }
                e.setCancelled(true);
            } else {
                TextChannel minecraftChatChannel = jda.getTextChannelById(Constants.MINECRAFT_CHAT_CHANNEL_ID);
                assert minecraftChatChannel != null;
                minecraftChatChannel.sendMessage(Api.convertMinecraftMessageToDiscord(e.getPlayer().getDisplayName(), e.getMessage())).queue();
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        if (Boolean.FALSE.equals(getIsParameterInTesting("linkingEnabled"))) {
            User user = luckPerms.getUserManager().getUser(e.getPlayer().getUniqueId());
            assert user != null;
            if (user.data().contains(InheritanceNode.builder("default").value(true).build(), NodeEqualityPredicate.EXACT ).equals(Tristate.TRUE)) {
                e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Go to Discord and type " + ChatColor.AQUA + "!link " + ChatColor.RED + " in the General channel"));
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onCommandEvent(PlayerCommandPreprocessEvent event) {
        if (Boolean.FALSE.equals(getIsParameterInTesting("onCommandEvent"))) {
            LatchDiscord.logPlayerBan(event, null);
            if (event.getMessage().equalsIgnoreCase("/back")){
                Api.denyBackIntoBossArena(event);
            }
        }
    }
    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent e){
        if(e.getChangedType().equals(Material.NETHER_PORTAL)){
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onInteract(PlayerInteractEvent event) throws IOException {
        if (Boolean.FALSE.equals(getIsParameterInTesting("onInteract"))) {
            RandomItem.getRandomItem(event);
            try {
                QuickSmelt.quickSmelt(event.getPlayer(), Api.getEconomy(), event);
                QuickBrew.quickBrew(event.getPlayer(), Api.getEconomy(), event);
            } catch (NullPointerException ignored){

            }
            MobileSpawner.disableSpawnerMobChange(event);
            SlimeChunkFinder.isSlimeChunk(event);
            setMasterAndSortChests(event);
            if (event.getAction().toString().equals("RIGHT_CLICK_BLOCK") && event.getClickedBlock().getType().equals(Material.CRIMSON_BUTTON)){
                BossBattle.startBossBattle(event);
            }
            //Api.denyOpenChestDuringBossBattle(event);
            CustomPortals.setBlockToNetherPortal(event);
        }
    }
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e){
        FileConfiguration configCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_CONFIG_FILE_NAME));
        if (Boolean.FALSE.equals(configCfg.getBoolean("areSpawnersActive"))) {
            if (e.getLocation().getWorld().toString().equalsIgnoreCase("world") && e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER)) {
                e.setCancelled(true);
            }
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
            sendItemsInAutoSorter(e);
        }
    }

//    @EventHandler
//    public void onPlayerBlockPlace(BlockPlaceEvent e) {
//        Block blockPlaced = e.getBlock();
//        if (e.getBlock().getType().equals(Material.CHEST)){
//            Chest chest = (Chest) e.getBlock().getState();
//            chest.setCustomName(e.getPlayer().getName() + "'s Chest");
//        }
//    }

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
    public void onAnvilInventoryOpen(PrepareAnvilEvent e){
        Api.combineChestplateAndElytra(e);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e){
        if (e.getDamager() instanceof Player) {
            Player player = (Player) e.getDamager();
            ItemStack is = player.getInventory().getItemInMainHand();
            ItemMeta im = null;
            if (is.getItemMeta() != null){
                im = is.getItemMeta();
            }
            if (im != null && im.getLore() != null){
                if (im.getLore().get(0).equalsIgnoreCase("Blood Sucker")){
                    double playerHealth = player.getHealth();
                    if (playerHealth < 20.00){
                        int min = 0;
                        int max = 10;
                        int random_int = (int) Math.floor(Math.random() * (max - min + 1) + min);
                        if (random_int > 7){
                            player.setHealth(playerHealth + 2.00);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) throws IOException {
        event.setCancelled(Api.cancelJrModEvent(event.getPlayer().getUniqueId()));
        if (Boolean.FALSE.equals(getIsParameterInTesting("onBlockBreak"))){
            Api.cancelEventsInPreviousSeason(event.getPlayer().getWorld().getName(), event.getPlayer().getName(), event, null, null, null);
            FarmMoney.rewardMoneyFromCrops(event, Api.getEconomy());
            MobileSpawner.setSpawnerOnBreak(event);
            Api.blockBreakLog(event);
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
    public void onBlockPlace(BlockPlaceEvent event) throws IOException {
        event.setCancelled(Api.cancelJrModEvent(event.getPlayer().getUniqueId()));
        if (Boolean.FALSE.equals(getIsParameterInTesting("onBlockPlace"))) {
            Api.cancelEventsInPreviousSeason(event.getPlayer().getWorld().getName(), event.getPlayer().getName(), null, event, null, null);
            MobileSpawner.setSpawnerOnPlace(event, Api.getEconomy());
            Api.placeBlockLog(event);
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
        autoSorterCfgm.setup();
        bossCfgm.setup();
    }

    public static void addRottenFleshToLeatherSmelt(){
        ItemStack leather = new ItemStack(Material.LEATHER);
        NamespacedKey leatherKey = new NamespacedKey(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), "LEATHER");
        float experience = 0.35f;
        int cookingTime = 200;
        FurnaceRecipe leatherRecipe = new FurnaceRecipe(leatherKey, leather, Material.ROTTEN_FLESH, experience, cookingTime);
        Bukkit.addRecipe(leatherRecipe);
    }

    public static void addSculkSensorRecipe(){
        ItemStack sculkSensor = new ItemStack(Material.SCULK_SENSOR);
        NamespacedKey sculkSensorKey = new NamespacedKey(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), "SCULK_SENSOR");
        ShapedRecipe sculkSensorRecipe = new ShapedRecipe(sculkSensorKey, sculkSensor);
        sculkSensorRecipe.shape("ECE", "EIE", "OOO");
        sculkSensorRecipe.setIngredient('E', Material.ENDER_EYE);
        sculkSensorRecipe.setIngredient('C', Material.END_CRYSTAL);
        sculkSensorRecipe.setIngredient('O', Material.OBSIDIAN);
        sculkSensorRecipe.setIngredient('I', Material.NETHERITE_INGOT);
        Bukkit.addRecipe(sculkSensorRecipe);
    }

    public static void addBundleRecipe(){
        ItemStack bundle = new ItemStack(Material.BUNDLE);
        NamespacedKey bundleKey = new NamespacedKey(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), "BUNDLE");
        ShapedRecipe bundleRecipe = new ShapedRecipe(bundleKey, bundle);
        bundleRecipe.shape("SRS", "RXR", "RRR");
        bundleRecipe.setIngredient('S', Material.STRING);
        bundleRecipe.setIngredient('R', Material.RABBIT_HIDE);
        bundleRecipe.setIngredient('X', Material.AIR);
        Bukkit.addRecipe(bundleRecipe);
    }

    public static void addLatchAppleRecipe(){
        ItemStack latchApple = new ItemStack(Material.ENCHANTED_GOLDEN_APPLE);
        ItemMeta im = latchApple.getItemMeta();
        assert im != null;
        im.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
        im.addEnchant(Enchantment.KNOCKBACK, 3, true);
        im.addEnchant(Enchantment.DAMAGE_ALL, 4, true);
        im.setDisplayName(ChatColor.GOLD + "Holy Latch Apple");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("Latch's own apple?");
        lore.add("What does it do?");
        im.setLore(lore);
        latchApple.setItemMeta(im);
        NamespacedKey latchAppleKey = new NamespacedKey(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), "ENCHANTED_GOLDEN_APPLE");
        ShapedRecipe latchAppleRecipe = new ShapedRecipe(latchAppleKey, latchApple);
        latchAppleRecipe.shape("GGG", "GAG", "GGG");
        latchAppleRecipe.setIngredient('G', Material.GOLD_BLOCK);
        latchAppleRecipe.setIngredient('A', Material.APPLE);
        Bukkit.addRecipe(latchAppleRecipe);
    }

    @EventHandler
    public static void onItemMove(InventoryMoveItemEvent e){
        try {
            Location destinationLocation = e.getDestination().getLocation();
            String playerName = "";
            boolean isSameChest = false;
            FileConfiguration autoSorterCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_AUTO_SORTER_FILE_NAME));
            for (String player : autoSorterCfg.getKeys(false)){
                Location masterChestLocation = new Location(Bukkit.getWorld("world"), autoSorterCfg.getDouble(player + ".masterChest.x"), autoSorterCfg.getDouble(player + ".masterChest.y"), autoSorterCfg.getDouble(player + ".masterChest.z"));
                if (destinationLocation.equals(masterChestLocation)){
                    playerName = player;
                    isSameChest = true;
                    break;
                }
            }
            if (Boolean.TRUE.equals(isSameChest)){
                Inventory masterChestInventory = e.getDestination();
                int count = 0;
                boolean itemsSent = false;
                for (ItemStack is : masterChestInventory){
                    if (is != null && autoSorterCfg.isSet(playerName + "." + is.getType())){
                        double chestToX = autoSorterCfg.getDouble(playerName + "." + is.getType() + ".x");
                        double chestToY = autoSorterCfg.getDouble(playerName + "." + is.getType() + ".y");
                        double chestToZ = autoSorterCfg.getDouble(playerName + "." + is.getType() + ".z");
                        Location chestSortLocation = new Location(Bukkit.getWorld("world"), chestToX, chestToY, chestToZ);
                        Material chest = chestSortLocation.getBlock().getType();
                        if (chest.equals(Material.CHEST)){
                            Chest sortChest = (Chest) chestSortLocation.getBlock().getState();
                            sortChest.getInventory().addItem(is);
                            masterChestInventory.setItem(count, new ItemStack(Material.AIR, 1));
                            itemsSent = true;
                        }
                    }
                    count++;
                }
            }

        } catch (NullPointerException ignored){

        }


    }

    private void setMasterAndSortChests(PlayerInteractEvent event) throws IOException {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType().equals(Material.CHEST)){
            if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta() != null &&
                    event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getLore() != null){
                FileConfiguration autoSorterCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_AUTO_SORTER_FILE_NAME));
                String playerName = event.getPlayer().getName().toLowerCase();
                Location chestLocation = event.getClickedBlock().getLocation();
                double chestX = chestLocation.getX();
                double chestY = chestLocation.getY();
                double chestZ = chestLocation.getZ();
                String worldName = Objects.requireNonNull(chestLocation.getWorld()).getName();
                if (worldName.equalsIgnoreCase("world")){
                    if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getLore().get(0).toLowerCase().contains("autosorter master stick")){
                        autoSorterCfg.set(playerName + ".masterChest.x", chestX);
                        autoSorterCfg.set(playerName + ".masterChest.y", chestY);
                        autoSorterCfg.set(playerName + ".masterChest.z", chestZ);
                        event.getPlayer().sendMessage(ChatColor.GREEN + "Master Chest has been set.");
                    }
                    if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getLore().get(0).toLowerCase().contains("chest sorting stick")){
                        Material itemToSort = event.getPlayer().getInventory().getItemInOffHand().getType();
                        if (itemToSort.equals(Material.AIR)){
                            event.getPlayer().sendMessage(ChatColor.YELLOW + "Can't sort AIR. Place an item in your off hand and click on the chest again.");
                        } else {
                            autoSorterCfg.set(playerName + "." + itemToSort + ".x", chestX);
                            autoSorterCfg.set(playerName + "." + itemToSort + ".y", chestY);
                            autoSorterCfg.set(playerName + "." + itemToSort + ".z", chestZ);
                            event.getPlayer().sendMessage(ChatColor.GREEN + "Chest to sort " + ChatColor.GOLD + itemToSort + ChatColor.GREEN + " has been set.");
                        }
                    }
                }
                autoSorterCfg.save(Api.getConfigFile(Constants.YML_AUTO_SORTER_FILE_NAME));
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
        FileConfiguration autoSorterCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_AUTO_SORTER_FILE_NAME));
        Location masterChestLocation = new Location(Bukkit.getWorld("world"), autoSorterCfg.getDouble(playerName + ".masterChest.x"), autoSorterCfg.getDouble(playerName + ".masterChest.y"), autoSorterCfg.getDouble(playerName + ".masterChest.z"));
        if (chestLocation != null && chestLocation.equals(masterChestLocation)){
            Inventory masterChestInventory = e.getInventory();
            int count = 0;
            boolean itemsSent = false;
            for (ItemStack is : masterChestInventory){
                if (is != null && autoSorterCfg.isSet(playerName + "." + is.getType())){
                    double chestToX = autoSorterCfg.getDouble(playerName + "." + is.getType() + ".x");
                    double chestToY = autoSorterCfg.getDouble(playerName + "." + is.getType() + ".y");
                    double chestToZ = autoSorterCfg.getDouble(playerName + "." + is.getType() + ".z");
                    Location chestSortLocation = new Location(Bukkit.getWorld("world"), chestToX, chestToY, chestToZ);
                    Material chest = chestSortLocation.getBlock().getType();
                    if (chest.equals(Material.CHEST)){
                        Chest sortChest = (Chest) chestSortLocation.getBlock().getState();
                        sortChest.getInventory().addItem(is);
                        masterChestInventory.setItem(count, new ItemStack(Material.AIR, 1));
                        itemsSent = true;
                    }
                }
                count++;
            }
            if (Boolean.TRUE.equals(itemsSent)){
                player.sendMessage(ChatColor.GREEN + "Items sent to sorted chest(s).");
            }
        }
    }

    public static LuckPerms getLuckPerms(){
        return luckPerms;
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

