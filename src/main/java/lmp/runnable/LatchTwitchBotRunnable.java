package lmp.runnable;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.chat.events.channel.DonationEvent;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import com.github.twitch4j.pubsub.events.ChannelBitsEvent;
import com.github.twitch4j.pubsub.events.FollowingEvent;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;
import lmp.RandomTeleport;
import lmp.api.Api;
import lmp.constants.Constants;
import lmp.constants.YmlFileNames;
import lmp.twitch.LatchTwitchBot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class LatchTwitchBotRunnable implements Runnable {
    LatchTwitchBot bot;
    String twitchName;
    String oauthToken;
    String minecraftName;
    TwitchClient twitchClient;
    String channelID;

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
                    .withDefaultEventHandler(SimpleEventHandler.class)
                    .build();
            twitchClient.getChat().joinChannel(twitchName);
            twitchClient.getChat().sendMessage(twitchName, "Your TwitchBot is now enabled");
            twitchClient.getChat().getEventManager().onEvent(ChannelMessageEvent.class, e -> {
                try {
                    twitchChatResponseMessage(e);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            if (channelID != null) {
                this.twitchClient.getPubSub().listenForFollowingEvents(credential, channelID);
                this.twitchClient.getPubSub().listenForChannelPointsRedemptionEvents(credential, channelID);
                this.twitchClient.getPubSub().listenForSubscriptionEvents(credential, channelID);
                this.twitchClient.getPubSub().listenForCheerEvents(credential, channelID);
                this.twitchClient.getEventManager().onEvent(FollowingEvent.class, e -> Objects.requireNonNull(Bukkit.getPlayer(minecraftName)).sendMessage("[" + ChatColor.DARK_PURPLE + "Twitch" + ChatColor.WHITE + " | " + ChatColor.GOLD + "NEW FOLLOW" + ChatColor.WHITE + "]" + ChatColor.DARK_GRAY + " » " + ChatColor.AQUA + e.getData().getUsername() + ChatColor.GREEN + " just followed you!!!"));
                this.twitchClient.getEventManager().onEvent(SubscriptionEvent.class, e -> Objects.requireNonNull(Bukkit.getPlayer(minecraftName)).sendMessage("[" + ChatColor.DARK_PURPLE + "Twitch" + ChatColor.WHITE + " | " + ChatColor.GOLD + "NEW SUB" + ChatColor.WHITE + "]" + ChatColor.DARK_GRAY + " » " + ChatColor.AQUA + e.getUser().getName() + ChatColor.GREEN + " just subscribed to you!!!"));
                this.twitchClient.getEventManager().onEvent(ChannelBitsEvent.class, e -> Objects.requireNonNull(Bukkit.getPlayer(minecraftName)).sendMessage("[" + ChatColor.DARK_PURPLE + "Twitch" + ChatColor.WHITE + " | " + ChatColor.GOLD + "BITS" + ChatColor.WHITE + "]" + ChatColor.DARK_GRAY + " » " + ChatColor.AQUA + e.getData().getUserName() + ChatColor.GREEN + " donated " + ChatColor.GOLD + e.getData().getBitsUsed() + ChatColor.GREEN + " bits."));
                this.twitchClient.getEventManager().onEvent(RewardRedeemedEvent.class, e -> displayChannelPointRedemptionMessage(e, minecraftName));
                this.twitchClient.getEventManager().onEvent(DonationEvent.class, e -> Objects.requireNonNull(Bukkit.getPlayer(minecraftName)).sendMessage("[" + ChatColor.DARK_PURPLE + "Twitch" + ChatColor.WHITE + " | " + ChatColor.GOLD + "DONATION" + ChatColor.WHITE + "]" + ChatColor.DARK_GRAY + " » " + ChatColor.AQUA + e.getUser().getName() + ChatColor.GREEN + " donated " + ChatColor.GOLD + e.getAmount() + ChatColor.GREEN + " dollars. Message:" + e.getMessage()));
            }

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

    public void displayChannelPointRedemptionMessage(RewardRedeemedEvent e, String minecraftName) {
        long delay = 0;
        if (this.minecraftName.equalsIgnoreCase("latch93")) {
            String rewardTitle = e.getRedemption().getReward().getTitle();
            if (rewardTitle.equalsIgnoreCase("Mining Fatigue")) {
                giveMiningFatigue(Bukkit.getPlayer(minecraftName));
            }
//            if (rewardTitle.equalsIgnoreCase("Rename an item in Latch's Inventory")){
//                playRenameItemMessage(e, Bukkit.getPlayer(minecraftName));
//            }
            if (rewardTitle.equalsIgnoreCase("$10,000 on LMP")) {
                Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> giveViewerMoney(e));
            }
            if (rewardTitle.equalsIgnoreCase("Random Teleport Latch")) {
                teleportLatch(Bukkit.getPlayer(minecraftName));
            }
            if (rewardTitle.equalsIgnoreCase("Delete Latch's Item in Main Hand")) {
                Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> deleteItemInMainHand(Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(Api.getMinecraftIDFromTwitchName(Constants.SERVER_OWNER_MINECRAFT_NAME))))));
            }
            if (rewardTitle.equalsIgnoreCase("Play Creeper Sound")) {
                Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> playCreeperSound(Bukkit.getPlayer(UUID.fromString(Api.getMinecraftIDFromTwitchName(twitchName)))));
                delay = 100L;
            }
        }
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), new Runnable() {
            @Override
            public void run() {
                Objects.requireNonNull(Bukkit.getPlayer(minecraftName)).sendMessage("[" + ChatColor.DARK_PURPLE + "Twitch" + ChatColor.WHITE + " | " + ChatColor.GOLD + "CHANNEL POINTS" + ChatColor.WHITE + "]" + ChatColor.DARK_GRAY + " » " + ChatColor.AQUA + e.getRedemption().getUser().getDisplayName() + ChatColor.GREEN + " redeemed " +
                        ChatColor.GOLD + e.getRedemption().getReward().getTitle() + ChatColor.GREEN + " for " + ChatColor.GOLD + e.getRedemption().getReward().getCost() + ChatColor.GREEN + " channel points");
            }
        }, delay);

    }

    public void giveMiningFatigue(Player player) {
        Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 3000, 1)));
    }

    //    public void playRenameItemMessage(RewardRedeemedEvent e, Player player){
//        Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Config.PLUGIN_NAME)), () -> player.sendMessage(ChatColor.GOLD + e.getRedemption().getUser().getDisplayName() + ChatColor.DARK_GRAY + " » " + ChatColor.AQUA + " " +
//                e.getRedemption().getReward().toString()));
//    }
    public void giveViewerMoney(RewardRedeemedEvent e) {
        String minecraftID = Api.getMinecraftIDFromTwitchName(e.getRedemption().getUser().getDisplayName());
        if (minecraftID != null) {
            Api.givePlayerMoney(minecraftID, 10000);
            twitchClient.getChat().sendMessage(twitchName, e.getRedemption().getUser().getDisplayName() + ": You have been rewarded $10,000 on LMP!!!");
        } else {
            twitchClient.getChat().sendMessage(twitchName, "Error: You may not be linked on LMP. Please link your accounts with this format -> !lmp link [discordUserID]");
        }
    }

    public void teleportLatch(Player player) {
        Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> {
            try {
                RandomTeleport.teleportPlayerRandomly(player, 0, 1000000);
            } catch (ExecutionException | InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void deleteItemInMainHand(Player player) {
        player.getInventory().setItemInMainHand(null);
        player.updateInventory();
    }

    public void playCreeperSound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 1, 0);
    }

    public void twitchChatResponseMessage(ChannelMessageEvent e) throws IOException {
        Objects.requireNonNull(Bukkit.getPlayer(minecraftName)).sendMessage("[" + ChatColor.DARK_PURPLE + "Twitch" + ChatColor.WHITE + " | " + ChatColor.GOLD + e.getUser().getName() + ChatColor.WHITE + "]" + ChatColor.DARK_GRAY + " » " + ChatColor.AQUA + e.getMessage());
        if (minecraftName.equalsIgnoreCase("latch93")) {
            linkTwitchAccount(e);
            isPlayerLinked(e);
        }
    }

    public void isPlayerLinked(ChannelMessageEvent e) {
        if (e.getMessage().equalsIgnoreCase("!check")) {
            String minecraftID = Api.getMinecraftIDFromTwitchName(e.getUser().getName());
            if (minecraftID != null) {
                twitchClient.getChat().sendMessage(twitchName, e.getUser().getName() + ": Your accounts are linked and you can safely claim Crowd Control Rewards.");
            } else {
                twitchClient.getChat().sendMessage(twitchName, "Error: You may not be linked on LMP. Please link your accounts with this format -> !lmp link [discordUserID]");
            }
        }

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
