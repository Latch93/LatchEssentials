package discord_text;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Objects;

public class LatchDiscord extends ListenerAdapter implements Listener {


    public static final Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("discord_text");
    public static final String DISCORD_BOT_TOKEN = "OTUwNDc4ODc1NDg4NTc5NjU1.YiZgbg.z_UZPZ9wixwxyk0WjZsJvCwrGbk";
    public static final String USERNAME_DOES_NOT_EXIST_MESSAGE = "Username does not exist. Please try again.";
    public static final String ADDED_TO_WHITELIST_MESSAGE = "You were added to the whitelist. Happy Mining!!!";
    public static final String USER_EXISTS_ON_WHITELIST_MESSAGE = "You are already added to the whitelist. :smile:";
    public static final String RULES_CHANNEL_MESSAGE_ID = "950601046575706143";
    public static final String WHITELIST_CHANNEL_ID = "950605346676801546";
    public static final String TEST_CHANNEL_ID = "950920437976694795";
    public static final String MEMBER_ROLE_ID = "628708160479166485";
    public static final String STAFF_APPLICATION_CHANNEL_ID = "635277380511858699";
    public static final String APPLICATION_SUBMITTED_CHANNEL_ID = "951562494344847390";
    public static final String MINECRAFT_CHAT_CHANNEL_ID = "627209350888554542";
    public static final String CLEAR_COMMAND = "!clear";
    public static final String STAFF_APPLY_COMMAND = "!apply";
    public static final String CLEAR_ALL_COMMAND = "!pineapple";
    public static final String SERVER_OWNER_NAME = "latch93";
    public static final String SERVER_OWNER_ID = "460463941542215691";
    public static String username = "";
    public static String userId = "";
    public static String staffAppChannelId = "";
    public static String staffAppSubmittedChannelId = "";
    public static String whitelistChannelId = "";
    private long channelId = 0;
    private long authorId = 0;
    public static boolean isTesting = true;
    public static final JDABuilder jdaBuilder = JDABuilder.createDefault(DISCORD_BOT_TOKEN);
    public static JDA jda = null;

    static {
        try {
            jda = jdaBuilder.build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    public LatchDiscord() throws LoginException {
        startBot();
        jda.addEventListener(this);
    }

    private void startBot() {
        try {
            jda = jdaBuilder.build();
        }
        catch (LoginException e) {
            e.printStackTrace();
        }
    }

    public static JDA getJDA(){
        return jda;
    }
    public void feedback(MessageChannel channel, User author) {
        this.channelId = channel.getIdLong();
        this.authorId = author.getIdLong();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();

        try {
            if (event.getMember() != null) {
                username = Objects.requireNonNull(event.getMember().getUser().getName());
                userId = Objects.requireNonNull(event.getMember().getId());
            }
            String messageId = event.getMessageId();
            String message = event.getMessage().getContentRaw();
            if (userId.equals(SERVER_OWNER_ID) && message.equalsIgnoreCase(CLEAR_COMMAND)) {
                clearMessages(channel, messageId);
            }
            if (userId.equals(SERVER_OWNER_ID) && message.equalsIgnoreCase(CLEAR_ALL_COMMAND)) {
                clearAllMessages(channel, messageId);
            }
            if (channel.getId().equals(setTestingChannel(STAFF_APPLICATION_CHANNEL_ID)) && message.equalsIgnoreCase(STAFF_APPLY_COMMAND)) {
                channel.deleteMessageById(messageId).queue();
                String finalStaffAppSubmittedChannelId = staffAppSubmittedChannelId;
                event.getAuthor().openPrivateChannel().flatMap(privateChannel -> {
                    TextChannel applicationSubmittedChannel = jda.getTextChannelById(finalStaffAppSubmittedChannelId);
                    event.getJDA().addEventListener(new Feedback(privateChannel, event.getAuthor(), applicationSubmittedChannel));
                    return privateChannel.sendMessage("Please enter your application information line by line. \n Press enter after each question response. \n 1.) How old are you?");
                }).queue();
            }
            if (WHITELIST_CHANNEL_ID.equalsIgnoreCase(channel.getId()) && !username.equalsIgnoreCase("LatchDCBot")){
                runWhitelistTest(channel, username, messageId, message, WHITELIST_CHANNEL_ID);
            }

        } catch (NullPointerException e){
            e.printStackTrace();
        }

    }

    private void runWhitelistTest(MessageChannel channel, String username, String messageId, String message, String whitelistChannelId) {
        try {
                addPlayerToWhitelist(channel, username, message, messageId, whitelistChannelId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event){
        MessageChannel channel = event.getChannel();
        String channelName = channel.getName();
        String userId = event.getUserId();
        if (channelName.equalsIgnoreCase("rules") && event.getMessageId().equalsIgnoreCase(setTestingChannel(RULES_CHANNEL_MESSAGE_ID))){
            event.getGuild().addRoleToMember(userId, Objects.requireNonNull(jda.getRoleById(MEMBER_ROLE_ID))).queue();
        }
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event){
        MessageChannel channel = event.getChannel();
        String channelName = channel.getName();
        String userId = event.getUserId();
        if (channelName.equalsIgnoreCase("rules") && event.getMessageId().equalsIgnoreCase(setTestingChannel(RULES_CHANNEL_MESSAGE_ID))){
            event.getGuild().removeRoleFromMember(userId, Objects.requireNonNull(jda.getRoleById(MEMBER_ROLE_ID))).queue();
        }
    }

    public void clearMessages(MessageChannel channel, String messageId){
        channel.deleteMessageById(messageId).queue();
        MessageHistory history = MessageHistory.getHistoryFromBeginning(channel).complete();
        List<Message> messageHistory = history.getRetrievedHistory();
        for(Message message1 : messageHistory){
            String currentMessage = message1.getContentRaw();
            if (currentMessage.equalsIgnoreCase(USERNAME_DOES_NOT_EXIST_MESSAGE) || currentMessage.equalsIgnoreCase(USER_EXISTS_ON_WHITELIST_MESSAGE) || currentMessage.equalsIgnoreCase(ADDED_TO_WHITELIST_MESSAGE)){
                channel.deleteMessageById(message1.getId()).queue();
            }
        }
    }

    public void clearAllMessages(MessageChannel channel, String messageId){
        channel.deleteMessageById(messageId).queue();
        MessageHistory history = MessageHistory.getHistoryFromBeginning(channel).complete();
        List<Message> messageHistory = history.getRetrievedHistory();
        for(Message message : messageHistory) {
            channel.deleteMessageById(message.getId()).queue();
        }
    }

    public static void addPlayerToWhitelist(MessageChannel channel, String username, String message, String messageId, String currentChannelId ) throws IOException {
        URL url = null;
        int whitelistTest = 0;
        boolean isPlayerWhitelisted = false;
        if (currentChannelId.equalsIgnoreCase(channel.getId()) && !username.equalsIgnoreCase("LatchDCBot")){
            BufferedReader br = null;
            try {
                url = new URL("https://api.mojang.com/users/profiles/minecraft/" + message);
                br = new BufferedReader(new InputStreamReader(url.openStream()));
                String str = br.readLine();
                if (str != null){
                    whitelistTest = 1;
                    isPlayerWhitelisted = getWhitelistedPlayers(message, false);
                }
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            } finally {
                if (br != null){
                    br.close();
                }
            }

            if (whitelistTest == 0){
                channel.sendMessage(USERNAME_DOES_NOT_EXIST_MESSAGE).queue();
            } else {
                isPlayerWhitelisted(channel, messageId, isPlayerWhitelisted, message);
            }
        }
    }

    private static boolean getWhitelistedPlayers(String message, boolean isPlayerWhitelisted) {
        for (OfflinePlayer player : Bukkit.getWhitelistedPlayers()){
            if (message.equalsIgnoreCase(player.getName())){
                isPlayerWhitelisted = true;
            }
        }
        return isPlayerWhitelisted;
    }

    private static void isPlayerWhitelisted(MessageChannel channel, String messageId, boolean isPlayerWhitelisted, String message) {

        if (Boolean.TRUE.equals(isPlayerWhitelisted)){
            channel.deleteMessageById(messageId).queue();
            channel.sendMessage(USER_EXISTS_ON_WHITELIST_MESSAGE).queue();
        }
        if (Boolean.FALSE.equals(isPlayerWhitelisted)){
            channel.sendMessage(ADDED_TO_WHITELIST_MESSAGE).queue();
            try {
                Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin("DiscordText")), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist add " + message));
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public static String setTestingChannel(String channelID){
        String channelId = channelID;
        if (Boolean.TRUE.equals(isTesting)){
            channelId = TEST_CHANNEL_ID;
        } return channelId;
    }

    @Override
    public void onReady(ReadyEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("LMP Server has started", null);
        eb.setColor(new Color(0xE10233E5, true));
        eb.setThumbnail("https://raw.githubusercontent.com/Latch93/DiscordText/master/src/main/resources/lmp_discord_image.png");
        TextChannel minecraftChannel = jda.getTextChannelById(setTestingChannel(MINECRAFT_CHAT_CHANNEL_ID));
        assert minecraftChannel != null;
        minecraftChannel.sendMessageEmbeds(eb.build()).queue();
    }

    public static void sendServerStoppedMessage() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("LMP Server has stopped", null);
        eb.setColor(new Color(0xE15C0000, true));
        eb.setThumbnail("https://raw.githubusercontent.com/Latch93/DiscordText/master/src/main/resources/lmp_discord_image.png");
        TextChannel minecraftChannel = jda.getTextChannelById(setTestingChannel(MINECRAFT_CHAT_CHANNEL_ID));
        assert minecraftChannel != null;
        minecraftChannel.sendMessageEmbeds(eb.build()).queue();
    }

    public static void sendPlayerOnJoinMessage(PlayerJoinEvent onPlayerJoinEvent) {
        String discordUserName = getDiscordUserName(onPlayerJoinEvent, null, null);
        EmbedBuilder eb = new EmbedBuilder();
        eb.setThumbnail("https://minotar.net/avatar/" + onPlayerJoinEvent.getPlayer().getName() + ".png?size=50");
        if (Boolean.TRUE.equals(onPlayerJoinEvent.getPlayer().hasPlayedBefore())){
            eb.setTitle("Discord Username: " + discordUserName + "\nMinecraft Username: " + onPlayerJoinEvent.getPlayer().getName() + " \nJoined the server", null);
        } else {
            eb.setTitle("Discord Username: " + discordUserName + "\nMinecraft Username: " + onPlayerJoinEvent.getPlayer().getName() + " \nJoined the server for the first time", null);
        }
        eb.setColor(new Color(0xE134E502, true));
        TextChannel minecraftChannel = jda.getTextChannelById(setTestingChannel(MINECRAFT_CHAT_CHANNEL_ID));
        assert minecraftChannel != null;
        if (!onPlayerJoinEvent.getPlayer().hasPermission("dt.joinVanish")){
            minecraftChannel.sendMessageEmbeds(eb.build()).queue();
        }
    }

    public static void sendPlayerLogoutMessage(PlayerQuitEvent onPlayerQuitEvent) {
        EmbedBuilder eb = new EmbedBuilder();
        String discordUserName = getDiscordUserName(null, onPlayerQuitEvent, null);
        eb.setThumbnail("https://minotar.net/avatar/" + onPlayerQuitEvent.getPlayer().getName()+ ".png?size=50");
        eb.setTitle("Discord Username: " + discordUserName + "\nMinecraft Username: " + onPlayerQuitEvent.getPlayer().getName() +" \nDisconnected from the server", null);
        eb.setColor(new Color(0xD0FF3F3F, true));
        TextChannel minecraftChannel = jda.getTextChannelById(setTestingChannel(MINECRAFT_CHAT_CHANNEL_ID));
        assert minecraftChannel != null;
        if (!onPlayerQuitEvent.getPlayer().hasPermission("dt.leaveVanish")){
            minecraftChannel.sendMessageEmbeds(eb.build()).queue();
        }
    }

    public static String getDiscordUserName(PlayerJoinEvent onPlayerJoinEvent, PlayerQuitEvent onPlayerQuitEvent, AsyncPlayerChatEvent onPlayerMessageEvent){
        TextChannel whitelistChannel = jda.getTextChannelById(setTestingChannel(WHITELIST_CHANNEL_ID));
        assert whitelistChannel != null;
        MessageHistory history = MessageHistory.getHistoryFromBeginning(whitelistChannel).complete();
        List<Message> messageHistory = history.getRetrievedHistory();
        String minecraftUserName = "";
        String discordUserName = "";
        if (onPlayerJoinEvent != null){
            minecraftUserName = onPlayerJoinEvent.getPlayer().getName();
        } else if (onPlayerQuitEvent != null){
            minecraftUserName = onPlayerQuitEvent.getPlayer().getName();
        } else {
            minecraftUserName = onPlayerMessageEvent.getPlayer().getName();
        }
        for (Message message : messageHistory){
            if (minecraftUserName.equalsIgnoreCase(message.getContentRaw())){
                discordUserName = message.getAuthor().getName();
            }
        }
        return discordUserName;
    }

    public static int getOnlinePlayers(){
        return Bukkit.getOnlinePlayers().size();
    }

    public static void setChannelDescription(Boolean removeOnePlayer) {
        int onlinePlayerCount = getOnlinePlayers();
        if (Boolean.TRUE.equals(removeOnePlayer)){
            onlinePlayerCount--;
        }
        TextChannel testChannel = jda.getTextChannelById(TEST_CHANNEL_ID);
        String maxPlayerCount = String.valueOf(Bukkit.getServer().getMaxPlayers());
        assert testChannel != null;
        testChannel.getManager().setTopic("Online Players: " + onlinePlayerCount + "/" + maxPlayerCount).queue();
    }

    public static void logPlayerMessage(AsyncPlayerChatEvent event){
        TextChannel testChannel = jda.getTextChannelById(TEST_CHANNEL_ID);
        assert testChannel != null;
        String discordUsername = getDiscordUserName(null, null, event);
        testChannel.sendMessage("DUName: " + discordUsername + " | MCUName: " + event.getPlayer().getDisplayName() + " » " + event.getMessage()).queue();
    }


    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }
}
