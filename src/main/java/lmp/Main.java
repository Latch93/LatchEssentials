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
import lmp.DiscordText.LMPCommand;
import lmp.DiscordText.LMPCommandTabComplete;
import lmp.DiscordText.LMPConfig;
import lmp.LatchTwitchBot.LatchTwitchBotCommand;
import lmp.LatchTwitchBot.LatchTwitchBotConfig;
import lmp.LatchTwitchBot.LatchTwitchBotTabComplete;
import lmp.PlayerShops.PlayerShops;
import lmp.PlayerShops.PlayerShopsCommand;
import lmp.PlayerShops.PlayerShopsInventoryConfig;
import lmp.PlayerShops.PlayerShopsTabComplete;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeEqualityPredicate;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.util.Tristate;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.eclipse.egit.github.core.client.GitHubClient;

import javax.security.auth.login.LoginException;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static lmp.LatchDiscord.jda;
import static org.bukkit.Bukkit.getOnlinePlayers;


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
    public static DBClient dbClient;
    public static Plugin coreProtect;
    public static CoreProtectAPI coreProtectAPI;
    public static GitHubClient githubClient;
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
        Objects.requireNonNull(this.getCommand("adsc")).setExecutor(new DiscordAdminChatCommand());
        Objects.requireNonNull(this.getCommand("dcmsg")).setExecutor(new MessageDiscordUserFromServerCommand());

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
        addExperienceStorageBottleRecipe();
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
        githubClient = new GitHubClient().setOAuth2Token(Api.getFileConfiguration(Api.getConfigFile(Constants.YML_CONFIG_FILE_NAME)).getString("githubOauthToken"));


    }
    public static void getDonations(){
        String[] statuses = {"Completed", "Reversed", "Refunded"};

        CompletableFuture<Donation[]> future = dbClient.getNewDonations(statuses);
// Non-blocking Example
        CompletableFuture.runAsync(() -> {
            FileConfiguration donationsCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_DONATION_FILE_NAME));
            // Array of Donation objects
            List<Donation> list = null;
            try {
                list = Arrays.asList(future.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }

            List<Donation> sorted = list.stream()
                    .sorted(Comparator.comparing(Donation::getDate))
                    .collect(Collectors.toList());
            if (donationsCfg.getInt("numberOfDonations") < sorted.size()) {
                int previousDonationCount = donationsCfg.getInt("numberOfDonations");
                int currentDonationCount = sorted.size();
                //for (Donation donation : sorted) {
//                            String discordID = "123";
//                            if (sorted.get(i).getSellerCustoms().get("What is your Discord User ID?") != null && !sorted.get(i).getSellerCustoms().get("What is your Discord User ID?").isEmpty()){
//                                discordID = sorted.get(i).getSellerCustoms().get("What is your Discord User ID?");
//                            }
                int donationCountDifference = currentDonationCount - previousDonationCount;
                for (int i = 0; i < donationCountDifference; i++) {
                    Donation donation = sorted.get(previousDonationCount);
                    String transactionId = donation.getTransactionID();
                    String buyerID = donation.getBuyerID();
                    String buyerEmail = donation.getBuyerEmail();
                    String productID = donation.getProductID();
                    String price = donation.getPrice();
                    Boolean isRecurring = donation.getRecurring();
                    Date purchaseDate = donation.getDate();
                    donationsCfg.set(Constants.YML_DONATIONS + transactionId + ".transactionID", transactionId);
                    donationsCfg.set(Constants.YML_DONATIONS + transactionId + ".buyerID", buyerID);
                    donationsCfg.set(Constants.YML_DONATIONS + transactionId + ".buyerEmail", buyerEmail);
                    donationsCfg.set(Constants.YML_DONATIONS + transactionId + ".roleID", donation.getRoleID());
                    donationsCfg.set(Constants.YML_DONATIONS + transactionId + ".productID", productID);
                    donationsCfg.set(Constants.YML_DONATIONS + transactionId + ".price", price);
                    donationsCfg.set(Constants.YML_DONATIONS + transactionId + ".isRecurring", isRecurring);
                    donationsCfg.set(Constants.YML_DONATIONS + transactionId + ".date", purchaseDate);
                    Player latch = null;
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.getUniqueId().toString().equalsIgnoreCase(Constants.LATCH_MINECRAFT_ID)) {
                            latch = player;
                        }
                    }
                    assert latch != null;
                    if (Bukkit.getServer().getPlayer(Constants.LATCH_MINECRAFT_ID) != null) {
                        if (productID.equals(Constants.SPAWN_MOB_PRODUCT_ID) && Double.parseDouble(price) > 4.99) {
                            EntityType mobToSpawn = EntityType.CREEPER;
                            Player finalLatch = latch;
                            if (donation.getSellerCustoms().get("Enter the Mob Name") != null) {
                                mobToSpawn = EntityType.valueOf(donation.getSellerCustoms().get("Enter the Mob Name").toUpperCase());
                            }
                            Location latchLocation = finalLatch.getLocation();
                            EntityType finalMobToSpawn = mobToSpawn;
                            Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> Objects.requireNonNull(latchLocation.getWorld()).spawnEntity(latchLocation, finalMobToSpawn));
                            Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> finalLatch.sendMessage("creepy bois"));
                        }
                        if (productID.equals(Constants.KILL_LATCH_PRODUCT_ID) && Double.parseDouble(price) > 9.99) {
                            Player finalLatch = latch;
                            Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> finalLatch.setHealth(0));
                            Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> finalLatch.sendMessage("You ded"));
                        }
                        if (productID.equals(Constants.PLAY_CREEPER_SOUND_PRODUCT_ID) && Double.parseDouble(price) > 1.49) {
                            Player finalLatch = latch;
                            Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> finalLatch.playSound(finalLatch.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 1, 0));
                        }
                        if (productID.equals(Constants.BLINDNESS_EFFECT_PRODUCT_ID) && Double.parseDouble(price) > 1.49) {
                            Player finalLatch = latch;
                            Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> finalLatch.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 6000, 1)));
                        }
                    }
                    previousDonationCount++;
                }
                donationsCfg.set("numberOfDonations", sorted.size());
                try {
                    donationsCfg.save(Api.getConfigFile(Constants.YML_DONATION_FILE_NAME));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
                            //                            donationsCfg.set(Constants.YML_DONATIONS + transactionId + ".discordID", discordID);
//                            if (productID.equalsIgnoreCase(Constants.DONOR_PRODUCT_ID) && !discordID.equalsIgnoreCase("123")){
//                                String donorRoleName = "";
//                                String minecraftID = Api.getMinecraftIdFromDCid(discordID);
//                                if (Double.parseDouble(price) < 5.00){
//                                    donorRoleName = "Donor";
//                                    Api.addPlayerToPermissionGroup(minecraftID, "donor");
//                                    LatchDiscord.getJDA().getGuildById(Constants.GUILD_ID).addRoleToMember(UserSnowflake.fromId(discordID), Objects.requireNonNull(jda.getRoleById("994064302866714765"))).queue();
//                                } else if (Double.parseDouble(price) > 9.99) {
//                                    donorRoleName = "Donor+";
//                                    Api.addPlayerToPermissionGroup(minecraftID, "donor+");
//                                    LatchDiscord.getJDA().getGuildById(Constants.GUILD_ID).addRoleToMember(UserSnowflake.fromId(discordID), Objects.requireNonNull(jda.getRoleById("994064735194599494"))).queue();
//                                } else {
//                                    donorRoleName = "Donor++";
//                                    Api.addPlayerToPermissionGroup(minecraftID, "donor++");
//                                    LatchDiscord.getJDA().getGuildById(Constants.GUILD_ID).addRoleToMember(UserSnowflake.fromId(discordID), Objects.requireNonNull(jda.getRoleById("994064561399398480"))).queue();
//                                }
//                                OfflinePlayer minecraftMember = Bukkit.getOfflinePlayer(UUID.fromString(minecraftID));
//                                donationsCfg.set(Constants.YML_MEMBERS + discordID + ".discordName", LatchDiscord.getJDA().getGuildById(Constants.GUILD_ID).getMemberById(discordID).getUser().getName());
//                                donationsCfg.set(Constants.YML_MEMBERS + discordID + ".discordID", discordID);
//                                donationsCfg.set(Constants.YML_MEMBERS + discordID + ".minecraftName", minecraftMember.getName());
//                                donationsCfg.set(Constants.YML_MEMBERS + discordID + ".minecraftID", minecraftID);
//                                donationsCfg.set(Constants.YML_MEMBERS + discordID + ".donorRole", donorRoleName);
//                                donationsCfg.set(Constants.YML_MEMBERS + discordID + ".donorPurchaseDate", purchaseDate);
//                                DateTime dateTime = new DateTime(purchaseDate);
//                                donationsCfg.set(Constants.YML_MEMBERS + discordID + ".donorEndDate", dateTime.plusDays(30).getMillis());
//                                donationsCfg.set(Constants.YML_MEMBERS + discordID + ".purchases.donations." + transactionId, transactionId);
//                            }


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
        Api.turnOffXPFarmOnPlayerLogoff(event);
    }

    @EventHandler
    public static void advancementDoneEvent(PlayerAdvancementDoneEvent e) throws IOException {
        if (Boolean.FALSE.equals(getIsParameterInTesting("advancementDoneEvent"))) {
            Advancements.setPlayerAdvancementOnCompletion(e);
            Advancements.showAdvancementInDiscord(e);
            Advancements.broadcastAdvancement(e);
        }
    }

    @EventHandler
    public static void onPlayerDeath(PlayerDeathEvent e) throws IOException {
        if (Boolean.FALSE.equals(getIsParameterInTesting("onPlayerDeath"))) {
            EmbedBuilder eb = new EmbedBuilder();
            String worldPrefix = "[LMP] - ";
            if (e.getEntity().getWorld().getName().equalsIgnoreCase("hardcore")){
                worldPrefix = "[Hardcore] - ";
            }
            eb.setTitle(worldPrefix + e.getDeathMessage());
            eb.setColor(new Color(0xE1922E00, true));
            eb.setThumbnail("https://minotar.net/avatar/" + e.getEntity().getName() + ".png?size=5");
            TextChannel minecraftChatChannel = LatchDiscord.getJDA().getTextChannelById(Constants.MINECRAFT_CHAT_CHANNEL_ID);
            assert minecraftChatChannel != null;
            minecraftChatChannel.sendMessageEmbeds(eb.build()).queue();
            if (Objects.requireNonNull(e.getDeathMessage()).contains("Super Jerry")) {
                LatchDiscord.getJDA().getTextChannelById(Constants.DISCORD_STAFF_CHAT_CHANNEL_ID).sendMessage("<@" + Api.getDiscordIdFromMCid(e.getEntity().getUniqueId().toString()) + "> - AKA: " + e.getEntity().getName() + " tried to hurt Super Jerry.").queue();
            }
            if (e.getEntity().getKiller() == null && Boolean.TRUE.equals(Api.getFileConfiguration(Api.getConfigFile(Constants.YML_CONFIG_FILE_NAME)).getBoolean("doesPlayerLoseMoneyOnDeath"))){
                DecimalFormat df = new DecimalFormat("0.00");
                double playerBalance = Api.getEconomy().getBalance(Api.getOfflinePlayerFromPlayer(e.getEntity()));
                double percentToRemove = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_CONFIG_FILE_NAME)).getDouble("deathBalancePercentage");
                double amountToRemove = (playerBalance / 100.00) * percentToRemove;
                Api.getEconomy().withdrawPlayer(Api.getOfflinePlayerFromPlayer(e.getEntity()), amountToRemove);
                e.getEntity().sendMessage(ChatColor.YELLOW + "You lost " + ChatColor.RED + "$" + df.format(amountToRemove) + ChatColor.YELLOW + " because you died.");
            }
            if (e.getEntity().getWorld().getName().contains("hardcore")){
                FileConfiguration hardcoreCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_HARDCORE_FILE_NAME));
                Player player = e.getEntity();
                String uuid = player.getUniqueId().toString();
                if (Boolean.TRUE.equals(hardcoreCfg.getBoolean(uuid + ".isAlive"))){
                    hardcoreCfg.set(uuid + ".isAlive", false);
                    hardcoreCfg.save(Api.getConfigFile(Constants.YML_HARDCORE_FILE_NAME));
                }
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
            String worldPrefix = "[LMP] - ";
            if (e.getPlayer().getWorld().getName().equalsIgnoreCase("hardcore")){
                worldPrefix = "[Hardcore] - ";
            }
            if(Boolean.TRUE.equals(Api.isPlayerInvisible(e.getPlayer().getUniqueId().toString()))){
                Objects.requireNonNull(jda.getTextChannelById(Constants.DISCORD_STAFF_CHAT_CHANNEL_ID)).sendMessage(worldPrefix + Api.convertMinecraftMessageToDiscord(e.getPlayer().getDisplayName(), e.getMessage())).queue();
                for (Player player : getOnlinePlayers()){
                    if (player.hasPermission("group.jr-mod")){
                        player.sendMessage("[" + ChatColor.LIGHT_PURPLE + "Mod Chat" + ChatColor.WHITE + "] - " + ChatColor.GOLD + e.getPlayer().getDisplayName() + ChatColor.WHITE + " » " + ChatColor.AQUA + e.getMessage());
                    }
                }
                e.setCancelled(true);
            } else {
                TextChannel minecraftChatChannel = jda.getTextChannelById(Constants.MINECRAFT_CHAT_CHANNEL_ID);
                assert minecraftChatChannel != null;
                minecraftChatChannel.sendMessage(worldPrefix + Api.convertMinecraftMessageToDiscord(e.getPlayer().getDisplayName(), e.getMessage())).queue();
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
    public void onCommandEvent(PlayerCommandPreprocessEvent event) throws IOException, ExecutionException, InterruptedException {
        if (Boolean.FALSE.equals(getIsParameterInTesting("onCommandEvent"))) {
            LatchDiscord.logPlayerBan(event, null);
            if (event.getMessage().equalsIgnoreCase("/back")){
                Api.denyBackIntoBossArena(event);
                Api.denyBackIntoXPFarm(event);
            }
            if (!event.getPlayer().isOp() && event.getPlayer().getWorld().getName().equalsIgnoreCase("hardcore")){
                event.setCancelled(Api.denyCommandInHardcore(event));
            }
            Api.denyCommandUseInXPFarm(event);
        }
        if (!event.getPlayer().getWorld().getName().contains("hardcore") && event.getMessage().equalsIgnoreCase("/lmp hardcore")){
            if(Boolean.TRUE.equals(Api.doesPlayerHavePermission(event.getPlayer().getUniqueId().toString(), "hardcore"))){
                Api.addPlayerToHardcoreList(event);
                Api.teleportHardcorePlayerToLastLocation(event.getPlayer());
            } else {
                event.getPlayer().sendMessage(ChatColor.RED + "You must purchase a hardcore season pass for " + ChatColor.GOLD + "$3 USD.");
            }
        }
        if (event.getPlayer().getWorld().getName().contains("hardcore") && event.getMessage().equalsIgnoreCase("/spawn")){
            Api.setHardcorePlayerLocation(event);
        }
    }

    @EventHandler
    public void onTargetChange(EntityTargetEvent e){
        if (e.getEntity().getType().equals(EntityType.WOLF)){
            File bloodmoonLMPFile = new File("plugins/Bloodmoon", "lmp.yml");
            FileConfiguration bloodmoonLMPCfg = Api.getFileConfiguration(bloodmoonLMPFile);
            List<String> currentHordeList = bloodmoonLMPCfg.getStringList("hordeMobs");
            if (e.getTarget() != null && currentHordeList.contains(e.getTarget().getUniqueId().toString())){
                e.setCancelled(true);
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
            RandomItem.getRandomItem1(event);
            RandomItem.getRandomItem2(event);
            RandomItem.getRandomItem3(event);
            try {
                QuickSmelt.quickSmelt(event.getPlayer(), Api.getEconomy(), event);
                QuickBrew.quickBrew(event.getPlayer(), Api.getEconomy(), event);
            } catch (NullPointerException ignored){

            }
            MobileSpawner.disableSpawnerMobChange(event);
            SlimeChunkFinder.isSlimeChunk(event);
            setMasterAndSortChests(event);
            if (event.getAction().toString().equals("RIGHT_CLICK_BLOCK") && Objects.requireNonNull(event.getClickedBlock()).getType().equals(Material.CRIMSON_BUTTON)){
                BossBattle.startBossBattle(event);
            }
            if (event.getClickedBlock() != null && event.getClickedBlock().getType().equals(Material.BEDROCK)){
                if (Boolean.TRUE.equals(event.getPlayer().getInventory().getItemInMainHand().hasItemMeta()) && Boolean.TRUE.equals(Objects.requireNonNull(event.getPlayer().getInventory().getItemInMainHand().getItemMeta()).hasLore())){
                    if (Objects.requireNonNull(event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getLore()).get(0).equalsIgnoreCase("Bedrock Breaker")){
                        Block bedrockToBreak = event.getClickedBlock();
                        double playerBalance = Api.getEconomy().getBalance(Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId()));
                        if (playerBalance >= Api.getFileConfiguration(Api.getConfigFile(Constants.YML_CONFIG_FILE_NAME)).getDouble("bedrockBreakerCost")){
                            DecimalFormat df = new DecimalFormat("0.00");
                            bedrockToBreak.setType(Material.AIR);
                            Api.getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId()), Api.getFileConfiguration(Api.getConfigFile(Constants.YML_CONFIG_FILE_NAME)).getDouble("bedrockBreakerCost"));
                            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + Constants.YML_YOUR_NEW_BALANCE_IS + ChatColor.GOLD + "$" + df.format(Api.getEconomy().getBalance(Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId())))));
                        } else {
                            event.getPlayer().sendMessage(ChatColor.RED + "You need at least " + ChatColor.GOLD + "$" + Api.getFileConfiguration(Api.getConfigFile(Constants.YML_CONFIG_FILE_NAME)).getDouble("bedrockBreakerCost") + ChatColor.RED + " to break a block of bedrock.");
                        }
                    }
                }
            }
            Api.denyOpenChestDuringBossBattle(event);
            CustomPortals.setBlockToNetherPortal(event);
            XPFarm.teleportPlayerToXPFarm(event);
            XPFarm.teleportPlayerToSpawn(event);
            if (Boolean.TRUE.equals(LMPCommand.isPlayerHoldingXPStorageBottle(event.getPlayer()) && (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)))){
                event.getPlayer().sendMessage(ChatColor.RED + "You should be more careful. Don't throw away your Experience Storage Bottle. Switch to another item to be safe");
                event.setCancelled(true);
            }
        }
        if (!event.getPlayer().isOp() && event.getPlayer().getWorld().getName().equalsIgnoreCase("hardcore")){
            FileConfiguration hardcoreCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_HARDCORE_FILE_NAME));
            if (hardcoreCfg.getString(event.getPlayer().getUniqueId().toString()) == null || Boolean.FALSE.equals(hardcoreCfg.getBoolean(event.getPlayer().getUniqueId().toString() + "." + "isAlive") )) {
                    event.getPlayer().setGameMode(GameMode.SURVIVAL);
            }
            event.setCancelled(Api.denyInteractInHardcore(event.getPlayer()));
        }
    }
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e){
        FileConfiguration configCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_CONFIG_FILE_NAME));
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
    public void onPlayerInventoryClick(InventoryClickEvent e) throws ExecutionException, InterruptedException {
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
                    if (playerHealth <= 18.00){
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
    public void onBlockBreak(BlockBreakEvent event) throws IOException, ExecutionException, InterruptedException {
        event.setCancelled(Api.cancelJrModEvent(event.getPlayer().getUniqueId()));
        if (Boolean.FALSE.equals(getIsParameterInTesting("onBlockBreak"))){
            Api.cancelEventsInPreviousSeason(event.getPlayer().getWorld().getName(), event.getPlayer().getName(), event, null, null, null);
            FarmMoney.rewardMoneyFromCrops(event, Api.getEconomy());
            MobileSpawner.setSpawnerOnBreak(event);
            if (!Api.doesPlayerHavePermission(event.getPlayer().getUniqueId().toString(), "helper")){
                Api.blockBreakLog(event);
            }
        }
        if (event.getBlock().getType().equals(Material.CREEPER_HEAD)) {
            FileConfiguration creeperCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_CREEPERS_B_GONE_FILE_NAME));
            String creeperHeadLocation = event.getBlock().getLocation().toString();
            List<String> creeperLocationList = new ArrayList<>();
            if (!creeperCfg.getStringList("locations").isEmpty()){
                creeperLocationList = creeperCfg.getStringList("locations");
            }
            int indexToDelete = -1;
            for (int i = 0; i < creeperLocationList.size(); i++){
                if (creeperLocationList.get(i).equalsIgnoreCase(creeperHeadLocation)){
                    indexToDelete = i;
                }
            }
            if (indexToDelete != -1){
                 creeperLocationList.remove(indexToDelete);
            }
            creeperCfg.set("locations", creeperLocationList);
            creeperCfg.save(Api.getConfigFile(Constants.YML_CREEPERS_B_GONE_FILE_NAME));

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
    public void onEntitySpawn(EntitySpawnEvent e){
        List<String> doNotSpawnList = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_CONFIG_FILE_NAME)).getStringList("lightLevelMobs");
        if (doNotSpawnList.contains(e.getEntity().getType().toString())){
            if (e.getLocation().getBlock().getLightLevel() > 7){
                File bloodmoonFile = new File("plugins/Bloodmoon/world", "config.yml");
                FileConfiguration bloodmoonCfg = Api.getFileConfiguration(bloodmoonFile);
                if (Boolean.FALSE.equals(bloodmoonCfg.get("enabled"))){
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
            if (event.getItemInHand().getItemMeta() != null && event.getItemInHand().getItemMeta().getLore() != null){
                if (event.getItemInHand().getItemMeta().getLore().get(0).equalsIgnoreCase("Creepers BGone")){
                    FileConfiguration creeperCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_CREEPERS_B_GONE_FILE_NAME));
                    List<String> creeperLocationList = new ArrayList<>();
                    if (!creeperCfg.getStringList("locations").isEmpty()){
                        creeperLocationList = creeperCfg.getStringList("locations");
                    }
                    creeperLocationList.add(event.getBlock().getLocation().toString());
                    creeperCfg.set("locations", creeperLocationList);
                    creeperCfg.save(Api.getConfigFile(Constants.YML_CREEPERS_B_GONE_FILE_NAME));
                }
            }
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
    public void onPlayerChestItemRemove(InventoryClickEvent event) throws ExecutionException, InterruptedException, IOException {
        if (Boolean.FALSE.equals(getIsParameterInTesting("onPlayerChestItemRemove"))) {
            LatchDiscord.banPlayerStealing(event);
        }
        try {
            if (Boolean.FALSE.equals(getIsParameterInTesting("chestProtect"))) {
                if (event.getWhoClicked().getLocation().getWorld().getName().equalsIgnoreCase("world")) {
                    if (event.getClickedInventory() != null && event.getClickedInventory().getHolder() != null && event.getWhoClicked().getLocation().getWorld().getName().equalsIgnoreCase("world") && (event.getClickedInventory().getHolder() instanceof DoubleChest || event.getClickedInventory().getHolder() instanceof Chest || event.getClickedInventory().getHolder() instanceof ShulkerBox || event.getClickedInventory().getHolder() instanceof Barrel)) {
                        OfflinePlayer olp = null;
                        if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
                            FileConfiguration chestProtectCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_CHEST_PROTECT_FILE_NAME));
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
                                        ChestProtect.setApprovedAllPlayer(Api.getDiscordIdFromMCid(olp.getUniqueId().toString()), clicker.getName(), Api.getFileConfiguration(Api.getConfigFile(Constants.YML_CHEST_PROTECT_FILE_NAME)));
                                    } else {
                                        if (Boolean.FALSE.equals(Api.doesPlayerHavePermission(olp.getUniqueId().toString(), "chests"))) {
                                            boolean isApprovedPlayer = false;
                                            if (chestProtectCfg.contains(Constants.YML_PLAYERS + olp.getUniqueId().toString() + ".approvedPlayers")) {
                                                List<String> approvedPlayers = chestProtectCfg.getStringList(Constants.YML_PLAYERS + olp.getUniqueId().toString() + ".approvedPlayers");
                                                if (approvedPlayers.contains(clicker.getUniqueId().toString())) {
                                                    isApprovedPlayer = true;
                                                }
                                            }
                                            if (chestProtectCfg.contains(Constants.YML_PLAYERS + olp.getUniqueId().toString() + "." + event.getClickedInventory().getLocation().toString())) {
                                                List<String> approvedChestPlayers = chestProtectCfg.getStringList(Constants.YML_PLAYERS + olp.getUniqueId().toString() + "." + event.getClickedInventory().getLocation().toString());
                                                if (approvedChestPlayers.contains(clicker.getUniqueId().toString())) {
                                                    isApprovedPlayer = true;
                                                }
                                            }
                                            if (Boolean.FALSE.equals(isApprovedPlayer)) {
                                                try {
                                                    net.dv8tion.jda.api.entities.User chestOwner = LatchDiscord.getJDA().getGuildById(Constants.GUILD_ID).getMemberById(Api.getDiscordIdFromMCid(olp.getUniqueId().toString())).getUser();
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
        } catch (IllegalArgumentException ignored){}
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

    public static void addExperienceStorageBottleRecipe(){
        ItemStack xpStorageBottle = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta im = xpStorageBottle.getItemMeta();
        assert im != null;
        im.setDisplayName(ChatColor.GOLD + "Experience Storage Bottle");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("Experience Storage Bottle");
        lore.add("XP: 0");
        lore.add("To add experience, hold in hand and run /lmp xpDeposit [amount]");
        lore.add("To withdraw experience, hold in hand and run /lmp xpWithdraw [amount]");
        im.setLore(lore);
        xpStorageBottle.setItemMeta(im);
        NamespacedKey xpStorageBottleKey = new NamespacedKey(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), "EXPERIENCE_BOTTLE");
        ShapedRecipe xpStorageBottleRecipe = new ShapedRecipe(xpStorageBottleKey, xpStorageBottle);
        xpStorageBottleRecipe.shape("DBD", "BAB", "EBE");
        xpStorageBottleRecipe.setIngredient('E', Material.EMERALD);
        xpStorageBottleRecipe.setIngredient('B', Material.EXPERIENCE_BOTTLE);
        xpStorageBottleRecipe.setIngredient('D', Material.DIAMOND);
        xpStorageBottleRecipe.setIngredient('A', Material.ANCIENT_DEBRIS);
        Bukkit.addRecipe(xpStorageBottleRecipe);
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

