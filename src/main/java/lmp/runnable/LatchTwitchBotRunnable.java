package lmp.runnable;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.events.channel.*;
import com.github.twitch4j.common.events.domain.EventChannel;
import com.github.twitch4j.common.events.domain.EventUser;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.pubsub.domain.CommunityGoalContribution;
import com.github.twitch4j.pubsub.domain.CommunityPointsGoal;
import com.github.twitch4j.pubsub.events.ChannelBitsEvent;
import com.github.twitch4j.pubsub.events.CommunityGoalContributionEvent;
import com.github.twitch4j.pubsub.events.FollowingEvent;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;
import lmp.DonationClaimRewards;
import lmp.RandomTeleport;
import lmp.api.Api;
import lmp.constants.Constants;
import lmp.constants.YmlFileNames;
import lmp.twitch.LatchTwitchBot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.bukkit.Bukkit.getServer;

public class LatchTwitchBotRunnable implements Runnable {
    LatchTwitchBot bot;
    String twitchName;
    String oauthToken;
    String minecraftName;
    TwitchClient twitchClient;
    String channelID;
    TwitchChat twitchChat;

    public static int i = 10; // Task will run 10 times.
    public static BukkitTask task = null;
    public LatchTwitchBotRunnable(String twitchName, String oauthToken, String minecraftName, String channelID) {
        this.twitchName = twitchName.toLowerCase();
        this.oauthToken = oauthToken.toLowerCase();
        this.minecraftName = minecraftName;
        this.channelID = channelID;
    }

    public void run() {
        Bukkit.getScheduler().runTaskAsynchronously(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> {
            OAuth2Credential credential = new OAuth2Credential("twitch", oauthToken);
            this.twitchClient = TwitchClientBuilder.builder()
                    .withEnableChat(true)
                    .withChatAccount(credential)
                    .withEnablePubSub(true)
                    .withDefaultAuthToken(credential)
                    .withEnableHelix(true)
                    .withDefaultEventHandler(SimpleEventHandler.class)
                    .build();
            this.twitchChat = twitchClient.getChat();
            this.twitchChat.joinChannel(twitchName);
            this.twitchChat.sendMessage(twitchName, "Your TwitchBot is now enabled");
            this.twitchChat.getEventManager().onEvent(ChannelMessageEvent.class, e -> {
                try {
                    twitchChatResponseMessage(e);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            if (channelID != null) {
                this.twitchClient.getPubSub().listenForFollowingEvents(null, channelID);
                this.twitchClient.getPubSub().listenForChannelPointsRedemptionEvents(null, channelID);
                this.twitchClient.getPubSub().listenForSubscriptionEvents(credential, channelID);
                this.twitchClient.getPubSub().listenForCheerEvents(credential, channelID);
                this.twitchClient.getPubSub().listenForChannelSubGiftsEvents(credential, channelID);
                this.twitchClient.getPubSub().listenForRaidEvents(null, channelID);
                this.twitchClient.getPubSub().listenForUserChannelPointsEvents(credential, channelID);
//                this.twitchClient.getPubSub().listenForPresenceEvents()
                this.twitchClient.getClientHelper().enableStreamEventListener(twitchName);
                this.twitchClient.getEventManager().onEvent(RaidEvent.class, e -> displayRaidEventMessage(e, minecraftName));
                this.twitchClient.getEventManager().onEvent(GiftSubscriptionsEvent.class, e -> displayNewGiftSubscriptionEventMessage(e, minecraftName));
                this.twitchClient.getEventManager().onEvent(FollowingEvent.class, e -> Objects.requireNonNull(Bukkit.getPlayer(minecraftName)).sendMessage("[" + ChatColor.DARK_PURPLE + "Twitch" + ChatColor.WHITE + " | " + ChatColor.GOLD + "NEW FOLLOW" + ChatColor.WHITE + "]" + ChatColor.DARK_GRAY + " » " + ChatColor.AQUA + e.getData().getUsername() + ChatColor.GREEN + " just followed you!!!"));
                this.twitchClient.getEventManager().onEvent(SubscriptionEvent.class, e -> displayNewSubscriptionEventMessage(e, minecraftName));
                this.twitchClient.getEventManager().onEvent(ChannelBitsEvent.class, e -> Objects.requireNonNull(Bukkit.getPlayer(minecraftName)).sendMessage("[" + ChatColor.DARK_PURPLE + "Twitch" + ChatColor.WHITE + " | " + ChatColor.GOLD + "BITS" + ChatColor.WHITE + "]" + ChatColor.DARK_GRAY + " » " + ChatColor.AQUA + e.getData().getUserName() + ChatColor.GREEN + " donated " + ChatColor.GOLD + e.getData().getBitsUsed() + ChatColor.GREEN + " bits."));
                this.twitchClient.getEventManager().onEvent(RewardRedeemedEvent.class, e -> displayChannelPointRedemptionMessage(e, minecraftName));
                this.twitchClient.getEventManager().onEvent(DonationEvent.class, e -> Objects.requireNonNull(Bukkit.getPlayer(minecraftName)).sendMessage("[" + ChatColor.DARK_PURPLE + "Twitch" + ChatColor.WHITE + " | " + ChatColor.GOLD + "DONATION" + ChatColor.WHITE + "]" + ChatColor.DARK_GRAY + " » " + ChatColor.AQUA + e.getUser().getName() + ChatColor.GREEN + " donated " + ChatColor.GOLD + e.getAmount() + ChatColor.GREEN + " dollars. Message:" + e.getMessage()));
                this.twitchClient.getEventManager().onEvent(ChannelGoLiveEvent.class, e -> broadcastGoLiveEvent(e, minecraftName));
                this.twitchClient.getEventManager().onEvent(CommunityGoalContributionEvent.class, e -> displayCommunityRewardPointsSpent(e, minecraftName));
//                this.twitchClient.getEventSocket().getEventManager().onEvent(FollowingEvent.class, e -> )

            }
            https://twitchapps.com/bits:read/tmi/bits:read


            Objects.requireNonNull(Bukkit.getPlayer(minecraftName)).sendMessage(ChatColor.GREEN + "Your TwitchBot is now enabled");
            if (Boolean.TRUE.equals(Thread.currentThread().isInterrupted())) {
                bot.stop();
            }

        });
    }

    public LatchTwitchBot getBot() {
        return this.bot;
    }

    public void setBot(LatchTwitchBot bot) {
        this.bot = bot;
    }

    /**
     * User Joins ChatChannel Event
     * @param event IRCMessageEvent
     */
    public void onChannnelClientJoinEvent(IRCMessageEvent event) {
        if(event.getCommandType().equals("JOIN") && event.getChannelName().isPresent() && event.getClientName().isPresent()) {
            // Load Info
            EventChannel channel = event.getChannel();
            EventUser user = event.getUser();

            // Dispatch Event
            if (channel != null && user != null) {
                Objects.requireNonNull(Bukkit.getPlayer(minecraftName)).sendMessage("JOIN 1: " + event.getUserName());
                this.twitchClient.getEventManager().publish(new ChannelJoinEvent(channel, user));
                Objects.requireNonNull(Bukkit.getPlayer(minecraftName)).sendMessage("JOIN 2: " + event.getUserName());

            }
        }
    }

    /**
     * User Leaves ChatChannel Event
     * @param event IRCMessageEvent
     */
    public void onChannnelClientLeaveEvent(IRCMessageEvent event) {
        if(event.getCommandType().equals("PART") && event.getChannelName().isPresent() && event.getClientName().isPresent()) {
            // Load Info
            EventChannel channel = event.getChannel();
            EventUser user = event.getUser();

            // Dispatch Event
            if (channel != null && user != null) {
                Objects.requireNonNull(Bukkit.getPlayer(minecraftName)).sendMessage("LEAVE 1: " + event.getUserName());
                this.twitchClient.getEventManager().publish(new ChannelLeaveEvent(channel, user));
                Objects.requireNonNull(Bukkit.getPlayer(minecraftName)).sendMessage("LEAVE 2: " + event.getUserName());
            }
        }
    }

    public void displayRaidEventMessage(RaidEvent e, String minecraftName){
        Bukkit.getScheduler().runTask(Objects.requireNonNull(getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> playRaidSound(Objects.requireNonNull(Bukkit.getPlayer(minecraftName))));
        Objects.requireNonNull(Bukkit.getPlayer(minecraftName)).sendMessage("[" + ChatColor.DARK_PURPLE + "Twitch" + ChatColor.WHITE + " | " + ChatColor.GOLD + "RAID" + ChatColor.WHITE + "] " + ChatColor.DARK_GRAY + " » " + ChatColor.GOLD + e.getRaider().getName() + ChatColor.AQUA + " raided your stream with " + ChatColor.GOLD + e.getViewers() + ChatColor.AQUA + " viewers!!!");
    }

    public void displayNewSubscriptionEventMessage(SubscriptionEvent e, String minecraftName){
        Bukkit.getScheduler().runTask(Objects.requireNonNull(getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> playNewSubscriberSound(Objects.requireNonNull(Bukkit.getPlayer(minecraftName))));
        Objects.requireNonNull(Bukkit.getPlayer(minecraftName)).sendMessage("[" + ChatColor.DARK_PURPLE + "Twitch" + ChatColor.WHITE + " | " + ChatColor.GOLD + "NEW SUB" + ChatColor.WHITE + "]" + ChatColor.DARK_GRAY + " » " + ChatColor.AQUA + e.getUser().getName() + ChatColor.GREEN + " just subscribed to you!!!");
    }

    public void displayNewGiftSubscriptionEventMessage(GiftSubscriptionsEvent e, String minecraftName){
        Bukkit.getScheduler().runTask(Objects.requireNonNull(getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> playNewGiftSubscriberSound(Objects.requireNonNull(Bukkit.getPlayer(minecraftName))));
        Objects.requireNonNull(Bukkit.getPlayer(minecraftName)).sendMessage("[" + ChatColor.DARK_PURPLE + "Twitch" + ChatColor.WHITE + " | " + ChatColor.GOLD + "NEW GIFT SUB" + ChatColor.WHITE + "]" + ChatColor.DARK_GRAY + " » " + ChatColor.AQUA + e.getUser().getName() + ChatColor.GREEN + " just gifted a subscription.!!!");
    }

    public void displayChannelPointRedemptionMessage(RewardRedeemedEvent e, String minecraftName) {
        long delay = 0;
        if (this.minecraftName.equalsIgnoreCase("latch93")) {
            String rewardTitle = e.getRedemption().getReward().getTitle();
            if (rewardTitle.equalsIgnoreCase("Mining Fatigue")) {
                giveMiningFatigue(Bukkit.getPlayer(minecraftName));
            }
            if (rewardTitle.equalsIgnoreCase("Random Positive Potion Effect")) {
                giveRandomPositivePotionEffect(Bukkit.getPlayer(minecraftName));
            }
            if (rewardTitle.equalsIgnoreCase("Random Negative Potion Effect")) {
                giveRandomNegativePotionEffect(Bukkit.getPlayer(minecraftName));
            }
            if (rewardTitle.equalsIgnoreCase("Rename an item in Latch's Inventory")){
                playRenameItemMessage(e, Bukkit.getPlayer(minecraftName));
            }
            if (rewardTitle.equalsIgnoreCase("RSpawn Random Hostile Mob")){
                spawnRandomMob(Bukkit.getPlayer(minecraftName));
            }
            if (rewardTitle.equalsIgnoreCase("$10,000 on LMP Community")) {
                Bukkit.getScheduler().runTask(Objects.requireNonNull(getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> giveViewerMoney(e, 10000));
            }
            if (rewardTitle.equalsIgnoreCase("$50,000 on LMP Community")) {
                Bukkit.getScheduler().runTask(Objects.requireNonNull(getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> giveViewerMoney(e, 50000));
            }
            if (rewardTitle.equalsIgnoreCase("$100,000 on LMP Community")) {
                Bukkit.getScheduler().runTask(Objects.requireNonNull(getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> giveViewerMoney(e, 100000));
            }
            if (rewardTitle.equalsIgnoreCase("Freeze Latch")) {
                Bukkit.getScheduler().runTask(Objects.requireNonNull(getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), this::freezeLatch);
            }
            if (rewardTitle.equalsIgnoreCase("Diamond Claim on LMP Community")) {
                String minecraftID = Api.getMinecraftIDFromTwitchName(e.getRedemption().getUser().getDisplayName());
                if (minecraftID != null) {
                    Bukkit.getScheduler().runTask(Objects.requireNonNull(getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> {
                        try {
                            DonationClaimRewards.addItemToClaimToPlayer(minecraftID, new ItemStack(Material.DIAMOND, 1));
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                    twitchClient.getChat().sendMessage(twitchName, e.getRedemption().getUser().getDisplayName() + ": a diamond has been added to your /lmp claim on LMP Communty!!!");
                } else {
                    twitchClient.getChat().sendMessage(twitchName, "Error: You may not be linked on LMP. Please link your accounts with this format -> !lmp link [discordUserID]");
                }
            }
            if (rewardTitle.equalsIgnoreCase("Netherite Ingot Claim on LMP Community")) {
                String minecraftID = Api.getMinecraftIDFromTwitchName(e.getRedemption().getUser().getDisplayName());
                if (minecraftID != null) {
                    Bukkit.getScheduler().runTask(Objects.requireNonNull(getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> {
                        try {
                            DonationClaimRewards.addItemToClaimToPlayer(minecraftID, new ItemStack(Material.NETHERITE_INGOT, 1));
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                    twitchClient.getChat().sendMessage(twitchName, e.getRedemption().getUser().getDisplayName() + ": a netherite ingot has been added to your /lmp claim on LMP Communty!!!");
                } else {
                    twitchClient.getChat().sendMessage(twitchName, "Error: You may not be linked on LMP. Please link your accounts with this format -> !lmp link [discordUserID]");
                }
            }
            if (rewardTitle.equalsIgnoreCase("Random Teleport Latch")) {
                teleportLatch(Bukkit.getPlayer(minecraftName));
            }
            if (rewardTitle.equalsIgnoreCase("Delete Latch's Item in Main Hand")) {
                Bukkit.getScheduler().runTask(Objects.requireNonNull(getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> deleteItemInMainHand(Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString("f4c77e52-de47-4174-8282-0d962d089301")))));
            }
            if (rewardTitle.equalsIgnoreCase("Play Creeper Sound")) {
                Bukkit.getScheduler().runTask(Objects.requireNonNull(getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> playCreeperSound(Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString("f4c77e52-de47-4174-8282-0d962d089301")))));
                delay = 100L;
            }
            if (rewardTitle.equalsIgnoreCase("Give Latch a Diamond")) {
                Bukkit.getScheduler().runTask(Objects.requireNonNull(getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> giveLatchDiamond(Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString("f4c77e52-de47-4174-8282-0d962d089301")))));
            }
            if (rewardTitle.equalsIgnoreCase("Launch Latch in the Air")) {
                Bukkit.getScheduler().runTask(Objects.requireNonNull(getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> launchLatchInTheAir(Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString("f4c77e52-de47-4174-8282-0d962d089301")))));
            }
        }
        getServer().getScheduler().scheduleSyncDelayedTask(Objects.requireNonNull(getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), new Runnable() {
            @Override
            public void run() {
                Objects.requireNonNull(Bukkit.getPlayer(minecraftName)).sendMessage("[" + ChatColor.DARK_PURPLE + "Twitch" + ChatColor.WHITE + " | " + ChatColor.GOLD + "CHANNEL POINTS" + ChatColor.WHITE + "]" + ChatColor.DARK_GRAY + " » " + ChatColor.AQUA + e.getRedemption().getUser().getDisplayName() + ChatColor.GREEN + " redeemed " +
                        ChatColor.GOLD + e.getRedemption().getReward().getTitle() + ChatColor.GREEN + " for " + ChatColor.GOLD + e.getRedemption().getReward().getCost() + ChatColor.GREEN + " channel points");
            }
        }, delay);

    }

    public void giveMiningFatigue(Player player) {
        Bukkit.getScheduler().runTask(Objects.requireNonNull(getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 1800, 1)));
    }

    public void giveRandomPositivePotionEffect(Player player) {
        ArrayList<PotionEffectType> positivePotionList = new ArrayList<>();
        positivePotionList.add(PotionEffectType.DAMAGE_RESISTANCE);
        positivePotionList.add(PotionEffectType.DOLPHINS_GRACE);
        positivePotionList.add(PotionEffectType.FIRE_RESISTANCE);
        positivePotionList.add(PotionEffectType.INCREASE_DAMAGE);
        positivePotionList.add(PotionEffectType.ABSORPTION);
        positivePotionList.add(PotionEffectType.FAST_DIGGING);
        positivePotionList.add(PotionEffectType.JUMP);
        positivePotionList.add(PotionEffectType.REGENERATION);
        positivePotionList.add(PotionEffectType.SATURATION);
        positivePotionList.add(PotionEffectType.SPEED);
        Random rand = new Random();

        int n = rand.nextInt(positivePotionList.size());
        PotionEffectType randomPotionEffect = positivePotionList.get(n);
        Bukkit.getScheduler().runTask(Objects.requireNonNull(getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> player.addPotionEffect(new PotionEffect(randomPotionEffect, 600, 1)));
    }

    public void giveRandomNegativePotionEffect(Player player) {
        ArrayList<PotionEffectType> negativePotionList = new ArrayList<>();
        negativePotionList.add(PotionEffectType.BAD_OMEN);
        negativePotionList.add(PotionEffectType.BLINDNESS);
        negativePotionList.add(PotionEffectType.WITHER);
        negativePotionList.add(PotionEffectType.WEAKNESS);
        negativePotionList.add(PotionEffectType.HUNGER);
        negativePotionList.add(PotionEffectType.POISON);
        negativePotionList.add(PotionEffectType.CONFUSION);
        Random rand = new Random();

        int n = rand.nextInt(negativePotionList.size());
        PotionEffectType randomPotionEffect = negativePotionList.get(n);
        Bukkit.getScheduler().runTask(Objects.requireNonNull(getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> player.addPotionEffect(new PotionEffect(randomPotionEffect, 600, 1)));
    }

    public void spawnRandomMob(Player player) {
        ArrayList<EntityType> entityList = new ArrayList<>();
        entityList.add(EntityType.CREEPER);
        entityList.add(EntityType.ZOMBIE);
        entityList.add(EntityType.STRAY);
        entityList.add(EntityType.CAVE_SPIDER);
        entityList.add(EntityType.SKELETON);
        entityList.add(EntityType.WITHER_SKELETON);
        entityList.add(EntityType.BLAZE);
        entityList.add(EntityType.GHAST);

        Random rand = new Random();

        int n = rand.nextInt(entityList.size());
        EntityType entityToSpawn = entityList.get(n);
        Bukkit.getScheduler().runTask(Objects.requireNonNull(getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> Objects.requireNonNull(player.getWorld()).spawnEntity(player.getLocation(), entityToSpawn));
    }

    public void playRenameItemMessage(RewardRedeemedEvent e, Player player){
        Bukkit.getScheduler().runTask(Objects.requireNonNull(getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> player.sendMessage(ChatColor.GOLD + e.getRedemption().getUser().getDisplayName() + ChatColor.DARK_GRAY + " » " + ChatColor.AQUA + " " +
                e.getRedemption().getReward().toString()));
    }
    public void giveViewerMoney(RewardRedeemedEvent e, double amount) {
        String minecraftID = Api.getMinecraftIDFromTwitchName(e.getRedemption().getUser().getDisplayName());
        if (minecraftID != null) {
            Api.givePlayerMoney(minecraftID, amount);
            twitchClient.getChat().sendMessage(twitchName, e.getRedemption().getUser().getDisplayName() + ": You have been rewarded $" + amount + " on LMP!!!");
        } else {
            twitchClient.getChat().sendMessage(twitchName, "Error: You may not be linked on LMP. Please link your accounts with this format -> !lmp link [discordUserID]");
        }
    }

    public void teleportLatch(Player player) {
        Bukkit.getScheduler().runTask(Objects.requireNonNull(getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> {
            try {
                RandomTeleport.teleportPlayerRandomly(player, 0, 100000, true);
            } catch (ExecutionException | InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void freezeLatch() {
        Bukkit.getScheduler().runTask(Objects.requireNonNull(getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> {
            FileConfiguration configCfg = Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME);
            configCfg.set("freezeLatch", true);
            Objects.requireNonNull(Bukkit.getPlayer(minecraftName)).sendMessage(ChatColor.GOLD + "You are frozen for 5 Seconds... Good Luck!!!");
            try {
                configCfg.save(Api.getConfigFile(YmlFileNames.YML_CONFIG_FILE_NAME));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
            scheduler.scheduleSyncDelayedTask(Objects.requireNonNull(getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), new Runnable() {
                @Override
                public void run() {
                    configCfg.set("freezeLatch", false);
                    try {
                        configCfg.save(Api.getConfigFile(YmlFileNames.YML_CONFIG_FILE_NAME));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Objects.requireNonNull(Bukkit.getPlayer(minecraftName)).sendMessage(ChatColor.GOLD + "You've been released.");
                }
            }, 100);
        });
    }

    public void deleteItemInMainHand(Player player) {
        player.getInventory().setItemInMainHand(null);
        player.updateInventory();
    }

    public void giveLatchDiamond(Player player) {
        player.getWorld().dropItem(player.getLocation(),new ItemStack(Material.DIAMOND, 1));
    }
    public void launchLatchInTheAir(Player player) {
        player.teleport(player.getLocation().add(0,20,0));
    }

    public void playCreeperSound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 1, 0);
    }

    public void playNewSubscriberSound(Player player) {
        player.playSound(player.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_0, 1, 0);
    }

    public void playNewGiftSubscriberSound(Player player) {
        player.playSound(player.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_1, 1, 0);
    }
    public void playRaidSound(Player player) {
        player.playSound(player.getLocation(), Sound.EVENT_RAID_HORN, 1, 0);
    }

    public void broadcastGoLiveEvent(ChannelGoLiveEvent e, String minecraftName){
        Bukkit.getScheduler().runTask(Objects.requireNonNull(getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () ->  Bukkit.broadcastMessage(ChatColor.GOLD + minecraftName + ChatColor.GREEN + " just went live streaming " + ChatColor.GOLD + e.getStream().getGameName() + ChatColor.GREEN + ". Go watch them at -> " + ChatColor.GOLD + "https://www.twitch.tv/" + e.getChannel().getName()));
    }
    public void twitchChatResponseMessage(ChannelMessageEvent e) throws IOException {
        Objects.requireNonNull(Bukkit.getPlayer(minecraftName)).sendMessage("[" + ChatColor.DARK_PURPLE + "Twitch" + ChatColor.WHITE + " | " + ChatColor.GOLD + e.getUser().getName() + ChatColor.WHITE + "]" + ChatColor.DARK_GRAY + " » " + ChatColor.AQUA + e.getMessage());
        if (minecraftName.equalsIgnoreCase("latch93")) {
            linkTwitchAccount(e);
            isPlayerLinked(e);
            replaceAdsMessageFromViewer(e);
//            outputUserEmail(e);
        }
    }

    public void isPlayerLinked(ChannelMessageEvent e) {
        if (e.getMessage().equalsIgnoreCase("!check")) {
            String minecraftID = Api.getMinecraftIDFromTwitchName(e.getUser().getName());
            if (minecraftID != null) {
                twitchClient.getChat().sendMessage(twitchName, e.getUser().getName() + ": Your accounts are linked and you can safely claim Crowd Control Rewards.");
            } else {
                twitchClient.getChat().sendMessage(twitchName, "Error: You may not be linked on LMP. Please get your link command by going into LMP Discord and type the following command into the General channel -> !linkTwitch");
            }
        }

    }

//    public void outputUserEmail(ChannelMessageEvent e) {
//        if (e.getMessage().equalsIgnoreCase("!list")) {
//            UserList resultList = this.twitchClient.getHelix().getUsers(null, null, null).execute();
//            resultList.getUsers().forEach(user -> {
//                Main.log.info(user.getEmail());
//            });
//        }
//
//    }

    public void replaceAdsMessageFromViewer(ChannelMessageEvent e) {
        if (e.getMessage().equalsIgnoreCase("ads")) {
            twitchClient.getChat().sendMessage(twitchName, e.getUser().getName() + ", if you subscribe to Latch on Twitch, you will no longer see ads. :)");
        }
    }

    public void displayCommunityRewardPointsSpent(CommunityGoalContributionEvent  e, String minecraftName){
        CommunityGoalContribution cbc = e.getContribution();
        CommunityPointsGoal cpg = e.getContribution().getGoal();
        Objects.requireNonNull(Bukkit.getPlayer(minecraftName)).sendMessage("[" + ChatColor.DARK_PURPLE + "Twitch" + ChatColor.WHITE + " | " + ChatColor.GOLD + cbc.getUser().getDisplayName() + ChatColor.WHITE + "]" + ChatColor.DARK_GRAY + " » " + ChatColor.AQUA + "Contributed " + ChatColor.GOLD + cbc.getAmount() + ChatColor.AQUA + " channel points to " + ChatColor.GOLD + cpg.getTitle() + ChatColor.AQUA + ". Total Contributed: " + ChatColor.GOLD + cbc.getTotalContribution());
        Objects.requireNonNull(Bukkit.getPlayer(minecraftName)).sendMessage("[" + ChatColor.DARK_PURPLE + "Twitch" + ChatColor.WHITE + " | " + ChatColor.GOLD + "Community Goal" + ChatColor.WHITE + "]" + ChatColor.DARK_GRAY + " » " + ChatColor.GOLD + cpg.getTitle() + ChatColor.AQUA + " currently at " + ChatColor.GOLD + cpg.getPointsContributed() + "/" + cpg.getGoalAmount());
    }

    public void linkTwitchAccount(ChannelMessageEvent e) {
        if (e.getMessage().toLowerCase().contains("!lmp link")) {
            FileConfiguration whitelistFile = Api.getFileConfiguration(YmlFileNames.YML_WHITELIST_FILE_NAME);
            try {
                String discordID = e.getMessage().split(" ")[2];
                String minecraftID = Api.getMinecraftIdFromDCid(discordID);
                if (!minecraftID.isEmpty()) {
                    whitelistFile.set("players." + minecraftID + ".twitchName", e.getUser().getName());
                    whitelistFile.save(Api.getConfigFile(YmlFileNames.YML_WHITELIST_FILE_NAME));
                    twitchClient.getChat().sendMessage(twitchName, e.getUser().getName() + "-> You successfully linked your Twitch username!!!");
                } else {
                    twitchClient.getChat().sendMessage(twitchName, "Error: You may not be linked on LMP. Please try again with this format -> !lmp link [discordUserID]");
                }
            } catch (ArrayIndexOutOfBoundsException err) {
                twitchClient.getChat().sendMessage(twitchName, "Error: Incorrect link format. Please try again with this format -> !lmp link [discordUserID]");
            } catch (NullPointerException err) {
                twitchClient.getChat().sendMessage(twitchName, "Error: You may not be linked on LMP. Please try again with this format -> !lmp link [discordUserID]");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public String getChannelID() {
        return this.channelID;
    }


    public void setChannelID(String channelID) {
        this.channelID = channelID;
    }

    public TwitchClient getTwitchClient() {
        return this.twitchClient;
    }

    public void setTwitchClient(TwitchClient twitchClient) {
        this.twitchClient = twitchClient;
    }

    public String getTwitchName() {
        return twitchName;
    }

    public void setTwitchName(String twitchName) {
        this.twitchName = twitchName;
    }

    public String getOauthToken() {
        return oauthToken;
    }

    public void setOauthToken(String oauthToken) {
        this.oauthToken = oauthToken;
    }

    public String getMinecraftName() {
        return minecraftName;
    }

    public void setMinecraftName(String minecraftName) {
        this.minecraftName = minecraftName;
    }
}
