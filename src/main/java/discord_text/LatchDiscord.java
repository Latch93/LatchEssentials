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
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.units.qual.C;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LatchDiscord extends ListenerAdapter implements Listener {


    public static final Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME);
    public static String username = "";
    public static String userId = "";
    private long channelId = 0;
    private long authorId = 0;
    public static final JDABuilder jdaBuilder = JDABuilder.createDefault(Constants.DISCORD_BOT_TOKEN);
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
            if (userId.equals(Constants.SERVER_OWNER_ID) && message.equalsIgnoreCase(Constants.CLEAR_COMMAND)) {
                clearMessages(channel, messageId);
            }
            if (userId.equals(Constants.SERVER_OWNER_ID) && message.equalsIgnoreCase(Constants.CLEAR_ALL_COMMAND)) {
                clearAllMessages(channel, messageId);
            }

            if (userId.equals(Constants.SERVER_OWNER_ID) && message.contains(Constants.CLEAR_ALL_USER_MESSAGES_COMMAND)) {
                String[] arr = event.getMessage().getContentRaw().split(Constants.CLEAR_ALL_USER_MESSAGES_COMMAND);
                String userId = arr[1].replace(" ", "");
                clearAllUserMessages(channel, messageId, userId);
            }
            if (channel.getId().equals(setTestingChannel(Constants.STAFF_APPLICATION_CHANNEL_ID)) && message.equalsIgnoreCase(Constants.STAFF_APPLY_COMMAND)) {
                channel.deleteMessageById(messageId).queue();
                String finalStaffAppSubmittedChannelId = Constants.STAFF_APP_SUBMITTED_CHANNEL_ID;
                event.getAuthor().openPrivateChannel().flatMap(privateChannel -> {
                    TextChannel applicationSubmittedChannel = jda.getTextChannelById(finalStaffAppSubmittedChannelId);
                    event.getJDA().addEventListener(new Feedback(privateChannel, event.getAuthor(), applicationSubmittedChannel));
                    return privateChannel.sendMessage("Please enter your application information line by line. \n Press enter after each question response. \n 1.) How old are you?");
                }).queue();
            }
            if (setTestingChannel(Constants.WHITELIST_CHANNEL_ID).equalsIgnoreCase(channel.getId()) && !userId.equalsIgnoreCase(Constants.LATCH93BOT_USER_ID)){
                runWhitelistTest(channel, username, messageId, message, setTestingChannel(Constants.WHITELIST_CHANNEL_ID));
            }
            if (Constants.PLAYER_SHOP_SEARCH_CHANNEL_ID.equalsIgnoreCase(channel.getId())){
                File configFile = new File(Main.getPlugin(Main.class).getDataFolder(), "playerShops.yml");
                FileConfiguration configCfg = YamlConfiguration.loadConfiguration(configFile);
                if (message.toLowerCase().contains(Constants.SEARCH_PLAYER_SHOP_COMMAND)) {
                    String[] arr = message.split(Constants.SEARCH_PLAYER_SHOP_COMMAND);
                    String itemToSearch = arr[1].replace(" ", "").toUpperCase();
                    String playerName = "";
                    int totalAmount = 0;
                    boolean isItemAvailable = false;
                    ArrayList<String> playersArr = new ArrayList<>();
                    ArrayList<Integer> totalAmountArr = new ArrayList<>();
                    for (String player : configCfg.getConfigurationSection("players").getKeys(false)) {
                        if (configCfg.isSet("players." + player)) {
                            totalAmount = 0;
                            for (String slot : configCfg.getConfigurationSection("players." + player + ".slots").getKeys(false)) {
                                if (Objects.requireNonNull(configCfg.getString("players." + player + ".slots." + slot + ".material")).equalsIgnoreCase(itemToSearch)) {
                                    isItemAvailable = true;
                                    playerName = player;
                                    totalAmount += totalAmount + Integer.parseInt(Objects.requireNonNull(configCfg.getString("players." + player + ".slots." + slot + ".amount")));
                                }
                            }
                            if (!playerName.equals("")){
                                playersArr.add(playerName);
                            }
                            if (totalAmount != 0){
                                totalAmountArr.add(totalAmount);
                            }
                        }
                    }
                    if (Boolean.TRUE.equals(isItemAvailable)) {
                        int counter = 0;
                        for (String p : playersArr){
                            channel.sendMessage(p + " has " + totalAmountArr.get(counter) + " " + itemToSearch + "(s) in their shop for $" + configCfg.getString("players." + p + ".itemWorth." + itemToSearch) + " per item.").queue();
                            counter++;
                        }
                    } else {
                        channel.sendMessage( "No one has " + itemToSearch + "s in their shop").queue();
                    }
                }
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
        String userID = event.getUserId();
        if (channel.getId().equalsIgnoreCase(Constants.RULES_CHANNEL_ID) && event.getMessageId().equalsIgnoreCase(Constants.RULES_CHANNEL_MESSAGE_ID)){
            event.getGuild().addRoleToMember(userID, Objects.requireNonNull(jda.getRoleById(Constants.MEMBER_ROLE_ID))).queue();
        }
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event){
        MessageChannel channel = event.getChannel();
        String userID = event.getUserId();
        if (channel.getId().equalsIgnoreCase(Constants.RULES_CHANNEL_ID) && event.getMessageId().equalsIgnoreCase(Constants.RULES_CHANNEL_MESSAGE_ID)){
            event.getGuild().removeRoleFromMember(userID, Objects.requireNonNull(jda.getRoleById(Constants.MEMBER_ROLE_ID))).queue();
        }
    }

    public void clearMessages(MessageChannel channel, String messageId){
        channel.deleteMessageById(messageId).queue();
        MessageHistory history = MessageHistory.getHistoryFromBeginning(channel).complete();
        List<Message> messageHistory = history.getRetrievedHistory();
        for(Message message1 : messageHistory){
            String currentMessage = message1.getContentRaw();
            if (currentMessage.equalsIgnoreCase(Constants.USERNAME_DOES_NOT_EXIST_MESSAGE) || currentMessage.equalsIgnoreCase(Constants.USER_EXISTS_ON_WHITELIST_MESSAGE) || currentMessage.equalsIgnoreCase(Constants.ADDED_TO_WHITELIST_MESSAGE)){
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

    public void clearAllUserMessages(MessageChannel channel, String messageId, String userID){
        channel.deleteMessageById(messageId).queue();
        MessageHistory history = MessageHistory.getHistoryFromBeginning(channel).complete();
        List<Message> messageHistory = history.getRetrievedHistory();
        for(Message message : messageHistory) {
            if (message.getAuthor().getId().equalsIgnoreCase(userID)){
                channel.deleteMessageById(message.getId()).queue();
            }
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
                channel.sendMessage(Constants.USERNAME_DOES_NOT_EXIST_MESSAGE).queue();
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
            channel.sendMessage(Constants.USER_EXISTS_ON_WHITELIST_MESSAGE).queue();
        }
        if (Boolean.FALSE.equals(isPlayerWhitelisted)){
            channel.sendMessage(Constants.ADDED_TO_WHITELIST_MESSAGE).queue();
            try {
                Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist add " + message));
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public static String setTestingChannel(String channelID){
        String channelId = channelID;
        if (Boolean.TRUE.equals(Main.IS_TESTING)){
            channelId = Constants.TEST_CHANNEL_ID;
        } return channelId;
    }

    @Override
    public void onReady(ReadyEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("LMP Server has started", null);
        eb.setColor(new Color(0xE10233E5, true));
        eb.setThumbnail("https://raw.githubusercontent.com/Latch93/DiscordText/master/src/main/resources/lmp_discord_image.png");
        TextChannel minecraftChannel = jda.getTextChannelById(setTestingChannel(Constants.MINECRAFT_CHAT_CHANNEL_ID));
        assert minecraftChannel != null;
        minecraftChannel.sendMessageEmbeds(eb.build()).queue();
    }

    public static void sendServerStoppedMessage() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("LMP Server has stopped", null);
        eb.setColor(new Color(0xE15C0000, true));
        eb.setThumbnail("https://raw.githubusercontent.com/Latch93/DiscordText/master/src/main/resources/lmp_discord_image.png");
        TextChannel minecraftChannel = jda.getTextChannelById(setTestingChannel(Constants.MINECRAFT_CHAT_CHANNEL_ID));
        assert minecraftChannel != null;
        minecraftChannel.sendMessageEmbeds(eb.build()).queue();
    }

    public static void sendPlayerOnJoinMessage(PlayerJoinEvent onPlayerJoinEvent) {
        String discordUserName = getDiscordUserName(onPlayerJoinEvent.getPlayer().getName());
        EmbedBuilder eb = new EmbedBuilder();
        eb.setThumbnail("https://minotar.net/avatar/" + onPlayerJoinEvent.getPlayer().getName() + ".png?size=50");
        if (Boolean.TRUE.equals(onPlayerJoinEvent.getPlayer().hasPlayedBefore())){
            eb.setTitle(Constants.DISCORD_USERNAME_LABEL+ discordUserName + Constants.MINECRAFT_USERNAME_LABEL + onPlayerJoinEvent.getPlayer().getName() + " \nJoined the server", null);
        } else {
            eb.setTitle(Constants.DISCORD_USERNAME_LABEL + discordUserName + Constants.MINECRAFT_USERNAME_LABEL + onPlayerJoinEvent.getPlayer().getName() + " \nJoined the server for the first time", null);
        }
        eb.setColor(new Color(0xE134E502, true));
        TextChannel minecraftChannel = jda.getTextChannelById(setTestingChannel(Constants.MINECRAFT_CHAT_CHANNEL_ID));
        assert minecraftChannel != null;
        if (!onPlayerJoinEvent.getPlayer().hasPermission("dt.joinVanish")){
            minecraftChannel.sendMessageEmbeds(eb.build()).queue();
        } else {
            TextChannel modChannel = jda.getTextChannelById(setTestingChannel(Constants.MOD_LOGIN_CHANNEL));
            assert modChannel != null;
            modChannel.sendMessageEmbeds(eb.build()).queue();
        }
    }

    public static void sendPlayerLogoutMessage(PlayerQuitEvent onPlayerQuitEvent) {
        EmbedBuilder eb = new EmbedBuilder();
        String discordUserName = getDiscordUserName(onPlayerQuitEvent.getPlayer().getName());
        eb.setThumbnail("https://minotar.net/avatar/" + onPlayerQuitEvent.getPlayer().getName()+ ".png?size=50");
        eb.setTitle(Constants.DISCORD_USERNAME_LABEL + discordUserName + Constants.MINECRAFT_USERNAME_LABEL  + onPlayerQuitEvent.getPlayer().getName() +" \nDisconnected from the server", null);
        eb.setColor(new Color(0xD0FF3F3F, true));
        TextChannel minecraftChannel = jda.getTextChannelById(setTestingChannel(Constants.MINECRAFT_CHAT_CHANNEL_ID));
        assert minecraftChannel != null;
        if (!onPlayerQuitEvent.getPlayer().hasPermission("dt.leaveVanish")){
            minecraftChannel.sendMessageEmbeds(eb.build()).queue();
        } else {
            TextChannel modChannel = jda.getTextChannelById(setTestingChannel(Constants.MOD_LOGIN_CHANNEL));
            assert modChannel != null;
            modChannel.sendMessageEmbeds(eb.build()).queue();
        }
    }

    public static String getDiscordUserName(String playerName){
        TextChannel whitelistChannel = jda.getTextChannelById(Constants.WHITELIST_CHANNEL_ID);
        assert whitelistChannel != null;
        MessageHistory history = MessageHistory.getHistoryFromBeginning(whitelistChannel).complete();
        List<Message> messageHistory = history.getRetrievedHistory();
        String discordUserName = "";
        for (Message message : messageHistory){
            if (playerName.equalsIgnoreCase(message.getContentRaw())){
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
        TextChannel testChannel = jda.getTextChannelById(Constants.TEST_CHANNEL_ID);
        String maxPlayerCount = String.valueOf(Bukkit.getServer().getMaxPlayers());
        assert testChannel != null;
        testChannel.getManager().setTopic("Online Players: " + onlinePlayerCount + "/" + maxPlayerCount).queue();
    }

    public static void logPlayerMessage(AsyncPlayerChatEvent event){
        TextChannel testChannel = jda.getTextChannelById(Constants.TEST_CHANNEL_ID);
        assert testChannel != null;
        String discordUsername = getDiscordUserName(event.getPlayer().getName());
        testChannel.sendMessage("DUName: " + discordUsername + " | MCUName: " + event.getPlayer().getDisplayName() + " » " + event.getMessage()).queue();
    }

    public static void banPlayerStealing(InventoryClickEvent event){
        String playerName = event.getWhoClicked().getName();
        Location chestLocation = new Location(event.getWhoClicked().getWorld(), 9997, 68, 10004);

        String chestMaterial = "";

        try {
            chestMaterial = Objects.requireNonNull(event.getClickedInventory()).getType().toString();
            if (chestMaterial.equalsIgnoreCase("CHEST") && chestLocation.equals(event.getClickedInventory().getLocation())){
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "ban " +playerName + " stole from bigboi's chest");
                String discordUserName = getDiscordUserName(playerName);
                TextChannel announcementChannel = jda.getTextChannelById(setTestingChannel(Constants.ANNOUNCEMENT_CHANNEL_ID));
                assert announcementChannel != null;
                announcementChannel.sendMessage(discordUserName + " will be banned :). Their MC username is: " + playerName).queue();
            }
        } catch (NullPointerException ignored){

        }

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
