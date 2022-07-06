package lmp.LatchTwitchBot;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import com.github.twitch4j.pubsub.events.ChannelBitsEvent;
import com.github.twitch4j.pubsub.events.FollowingEvent;

import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;
import lmp.Constants;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Objects;

public class LatchTwitchBotRunnable implements Runnable {
        LatchTwitchBot bot;
        String twitchName;
        String oauthToken;
        String minecraftName;
        TwitchClient twitchClient;
        String channelID;
        public LatchTwitchBotRunnable(String twitchName, String oauthToken, String minecraftName, String channelID){
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
                twitchClient.getChat().getEventManager().onEvent(ChannelMessageEvent.class, event -> Objects.requireNonNull(Bukkit.getPlayer(minecraftName)).sendMessage("[" + ChatColor.DARK_PURPLE + "Twitch" + ChatColor.WHITE + " | " + ChatColor.GOLD + event.getUser().getName() + ChatColor.WHITE + "]" + ChatColor.DARK_GRAY + " » " + ChatColor.AQUA + event.getMessage()));
                if (channelID != null){
                    twitchClient.getPubSub().listenForFollowingEvents(credential, channelID);
                    twitchClient.getPubSub().listenForChannelPointsRedemptionEvents(credential, channelID);
                    twitchClient.getPubSub().listenForSubscriptionEvents(credential, channelID);
                    twitchClient.getPubSub().listenForCheerEvents(credential, channelID);
                    twitchClient.getEventManager().onEvent(FollowingEvent.class, e -> Objects.requireNonNull(Bukkit.getPlayer(minecraftName)).sendMessage("[" + ChatColor.DARK_PURPLE + "Twitch" + ChatColor.WHITE + " | " + ChatColor.GOLD + "NEW FOLLOW" + ChatColor.WHITE + "]" + ChatColor.DARK_GRAY + " » " + ChatColor.AQUA + e.getData().getUsername() + ChatColor.GREEN + " just followed you!!!"));
                    twitchClient.getPubSub().getEventManager().onEvent(SubscriptionEvent.class, e -> Objects.requireNonNull(Bukkit.getPlayer(minecraftName)).sendMessage("[" + ChatColor.DARK_PURPLE + "Twitch" + ChatColor.WHITE + " | " + ChatColor.GOLD + "NEW SUB" + ChatColor.WHITE + "]" + ChatColor.DARK_GRAY + " » " + ChatColor.AQUA + e.getUser().getName() + ChatColor.GREEN + " just subscribed to you!!!"));
                    twitchClient.getPubSub().getEventManager().onEvent(ChannelBitsEvent.class, e -> Objects.requireNonNull(Bukkit.getPlayer(minecraftName)).sendMessage("[" + ChatColor.DARK_PURPLE + "Twitch" + ChatColor.WHITE + " | " + ChatColor.GOLD + "BITS" + ChatColor.WHITE + "]" + ChatColor.DARK_GRAY + " » " + ChatColor.AQUA + e.getData().getUserName() + ChatColor.GREEN + " donated " +ChatColor.GOLD + e.getData().getBitsUsed() + ChatColor.GREEN + " bits."));
                    twitchClient.getPubSub().getEventManager().onEvent(RewardRedeemedEvent.class, e -> Objects.requireNonNull(Bukkit.getPlayer(minecraftName)).sendMessage("[" + ChatColor.DARK_PURPLE + "Twitch" + ChatColor.WHITE + " | " + ChatColor.GOLD + "CHANNEL POINTS" + ChatColor.WHITE + "]" + ChatColor.DARK_GRAY + " » " + ChatColor.AQUA + e.getRedemption().getUser().getDisplayName() + ChatColor.GREEN + " redeemed " +
                            ChatColor.GOLD + e.getRedemption().getReward().getTitle() + ChatColor.GREEN + " for " + ChatColor.GOLD + e.getRedemption().getReward().getCost() + ChatColor.GREEN + " channel points"));
                }
                Objects.requireNonNull(Bukkit.getPlayer(minecraftName)).sendMessage(ChatColor.GREEN + "Your TwitchBot is now enabled");
                if (Boolean.TRUE.equals(Thread.currentThread().isInterrupted())){
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

    public String getChannelID(){
        return this.channelID;
    }


    public void setChannelID(String channelID){
        this.channelID = channelID;
    }
    public TwitchClient getTwitchClient(){
        return  this.twitchClient;
    }

    public void setTwitchClient(TwitchClient twitchClient){
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
