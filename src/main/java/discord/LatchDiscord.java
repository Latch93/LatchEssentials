package discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

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
    public static final JDABuilder jdaBuilder = JDABuilder.createDefault(Constants.DISCORD_BOT_TOKEN).setChunkingFilter(ChunkingFilter.ALL) // enable member chunking for all guilds
            .setMemberCachePolicy(MemberCachePolicy.ALL) // ignored if chunking enabled
            .enableIntents(GatewayIntent.GUILD_MEMBERS);
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
        if (Boolean.FALSE.equals(Main.IS_TESTING)){
            try {
                MessageChannel channel = event.getChannel();
                if (event.getMember() != null) {
                    username = Objects.requireNonNull(event.getMember().getUser().getName());
                    userId = Objects.requireNonNull(event.getMember().getId());
                }
                String messageId = event.getMessageId();
                String message = event.getMessage().getContentRaw();
                String senderName = event.getAuthor().getName();
                // If a user says the n word, then ban them
                if (message.toLowerCase().replace(" ", "").contains("nigger") || message.toLowerCase().replace(" ", "").contains("nigga")){
                    event.getMember().ban(0, "Used the n-word in discord").queue();
                }
                // Question mark
                if (userId.equals(Constants.SERVER_OWNER_ID) && message.equalsIgnoreCase(Constants.CLEAR_COMMAND)) {
                    clearMessages(channel, messageId);
                }
                // Clears all messages in the channel
                if (userId.equals(Constants.SERVER_OWNER_ID) && message.equalsIgnoreCase(Constants.CLEAR_ALL_COMMAND)) {
                    clearAllMessages(channel, messageId);
                }
                // Toggles whitelist on and off in #staff-whitelist channel
                if (channel.getId().equals(Constants.MOD_WHITELIST_CHANNEL_ID) && !userId.equals(Constants.LATCH93BOT_USER_ID)){
                    whitelistToggle(message, channel);
                }
                // Deletes all messages from a specified user
                if (userId.equals(Constants.SERVER_OWNER_ID) && message.contains(Constants.CLEAR_ALL_USER_MESSAGES_COMMAND)) {
                    String[] arr = event.getMessage().getContentRaw().split(Constants.CLEAR_ALL_USER_MESSAGES_COMMAND);
                    String userId = arr[1].replace(" ", "");
                    clearAllUserMessages(channel, messageId, userId);
                }
                if (channel.getId().equalsIgnoreCase(Constants.MINECRAFT_CHAT_CHANNEL_ID) && !event.getAuthor().getId().equals(Constants.LATCH93BOT_USER_ID)){
                    if (message.toLowerCase().contains("!searchuser")){
                        String[] messageArr = message.split(" ");
                        String discordUserName = getDiscordUserName(messageArr[1]);
                        channel.sendMessage(messageArr[1] + "'s Discord username is: " + discordUserName).queue();
                    } else {
                        int count = 0;
                        String highestRole = "Member";
                        ChatColor colorCode;
                        for (Player p : Bukkit.getOnlinePlayers()){
                            for (Role role : event.getMember().getRoles()){
                                if (role.getPosition() >= count){
                                    count = role.getPosition();
                                    highestRole = role.getName();
                                }
                            }
                            if (highestRole.equalsIgnoreCase("Owner")){
                                colorCode = ChatColor.GOLD;
                            } else if (highestRole.toLowerCase().contains("admin")){
                                colorCode = ChatColor.RED;
                            } else if (highestRole.toLowerCase().contains("mod")){
                                colorCode = ChatColor.LIGHT_PURPLE;
                            } else if (highestRole.toLowerCase().contains("builder")){
                                colorCode = ChatColor.BLUE;
                            } else {
                                colorCode = ChatColor.GREEN;
                            }
                            Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.AQUA + "Discord" + ChatColor.WHITE + " | " + colorCode + highestRole + ChatColor.WHITE + "] "  + senderName + " » " + message);
                        }
                    }
                }
                // Sends staff application to member
                if (channel.getId().equals(setTestingChannel(Constants.STAFF_APPLICATION_CHANNEL_ID)) && message.equalsIgnoreCase(Constants.STAFF_APPLY_COMMAND)) {
                    channel.deleteMessageById(messageId).queue();
                    event.getAuthor().openPrivateChannel().flatMap(privateChannel -> {
                        TextChannel applicationSubmittedChannel = jda.getTextChannelById(Constants.STAFF_APP_SUBMITTED_CHANNEL_ID);
                        event.getJDA().addEventListener(new StaffApplication(privateChannel, event.getAuthor(), applicationSubmittedChannel));
                        return privateChannel.sendMessage("Please enter your application information line by line. \n Press enter after each question response. \n 1.) How old are you?");
                    }).queue();
                }
                // Sends unban request to member
                if (channel.getId().equals(Constants.UNBAN_REQUEST_CHANNEL_ID) && message.equalsIgnoreCase(Constants.UNBAN_REQUEST)) {
                    channel.deleteMessageById(messageId).queue();
                    event.getAuthor().openPrivateChannel().flatMap(privateChannel -> {
                        TextChannel unbanRequestSubmittedChannel = jda.getTextChannelById(Constants.UNBAN_REQUEST_COMPLETE_CHANNEL_ID);
                        event.getJDA().addEventListener(new UnbanRequest(privateChannel, event.getAuthor(), unbanRequestSubmittedChannel));
                        return privateChannel.sendMessage("Please enter your unban form line by line. \n Press enter after each question response. \n 1.) What is your Minecraft username?");
                    }).queue();
                }
                // Check if username typed in whitelist channel is valid and whitelist if they are or send failed message
                if (Constants.WHITELIST_CHANNEL_ID.equalsIgnoreCase(channel.getId()) && !userId.equalsIgnoreCase(Constants.LATCH93BOT_USER_ID)){
                    runWhitelistTest(channel, username, messageId, message, Constants.WHITELIST_CHANNEL_ID);
                }
                // Updates the whitelist.yml with new members and connects discord and minecraft usernames. Command used in Text channel
                if (Constants.TEST_CHANNEL_ID.equalsIgnoreCase(channel.getId()) && message.equalsIgnoreCase(Constants.SET_WHITELIST_COMMAND)){
                    setWhitelistUsernames();
                }
                if (Constants.SEARCH_CHANNEL_ID.equalsIgnoreCase(channel.getId()) && message.equalsIgnoreCase(Constants.SEARCH_USER_COMMAND)){
                    String[] arr = message.split(Constants.SEARCH_USER_COMMAND);
                    String mcName = arr[1].replace(" ", "");
                    channel.sendMessage(mcName + "'s discord username is " + getDiscordUserName(mcName)).queue();
                }
                // Searches the player shops and returns if items are in a player's shop
                if (Constants.SEARCH_CHANNEL_ID.equalsIgnoreCase(channel.getId())){
                    File configFile = new File(Main.getPlugin(Main.class).getDataFolder(), "playerShops.yml");
                    FileConfiguration configCfg = YamlConfiguration.loadConfiguration(configFile);
                    if (message.toLowerCase().contains(Constants.SEARCH_PLAYER_SHOP_COMMAND)) {
                        String[] arr = message.split(Constants.SEARCH_PLAYER_SHOP_COMMAND);
                        String itemToSearch = arr[1].replace(" ", "").toUpperCase();
                        String playerName = "";
                        boolean isItemAvailable = false;
                        for (String player : configCfg.getConfigurationSection("players").getKeys(false)) {
                            int totalAmount = 0;
                            if (configCfg.isSet(Constants.YML_PLAYERS + player)) {
                                if (configCfg.isSet(Constants.YML_PLAYERS + player + ".slots")){
                                    for (String slot : configCfg.getConfigurationSection(Constants.YML_PLAYERS + player + ".slots").getKeys(false)) {
                                        if (Objects.requireNonNull(configCfg.getString(Constants.YML_PLAYERS + player + Constants.YML_SLOTS + slot + ".material")).equalsIgnoreCase(itemToSearch)) {
                                            if (!configCfg.isSet(Constants.YML_PLAYERS + player + Constants.YML_SLOTS + slot + ".enchants")){
                                                totalAmount += configCfg.getInt(Constants.YML_PLAYERS + player + Constants.YML_SLOTS + slot + ".amount");
                                            }
//                                        else {
//                                            channel.sendMessage(player + " has an enchanted " + itemToSearch + " in their shop for $" + configCfg.getString(Constants.YML_PLAYERS + player + ".itemWorth." +configCfg.getString(Constants.YML_PLAYERS + player + ".itemWorth."))).queue();
//                                        }
                                        }
                                    }
                                    if (totalAmount != 0){
                                        isItemAvailable = true;
                                        channel.sendMessage(player + " has " + totalAmount + " " + itemToSearch + "(s) in their shop for $" + configCfg.getInt(Constants.YML_PLAYERS + player + ".itemWorth." + itemToSearch) + " per item.").queue();
                                    }
                                }
                            }
                        }
                        if (Boolean.FALSE.equals(isItemAvailable)) {
                            channel.sendMessage( "No one has " + itemToSearch + "s in their shop").queue();
                        }
                    }
                }
                // Auto logs bans to #banned-logs if ban occurs in discord server console channel
                if (Constants.DISCORD_CONSOLE_CHANNEL_ID.equalsIgnoreCase(channel.getId())){
                    if (message.toLowerCase().contains("ban")){
                        logPlayerBan(null, event.getMessage());
                    }
                }
            } catch (NullPointerException | IOException e){
                e.printStackTrace();
            }
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
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        try {
            String minecraftUserName = getMinecraftUserName(Objects.requireNonNull(event.getMember()).getUser().getName());
            Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist remove " + minecraftUserName));
            removePlayerWhitelistMessageOnLeave(event.getMember().getUser().getName());
        } catch (NullPointerException e) {
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

    public static void whitelistToggle(String message, MessageChannel channel){
        try {
            Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist " + message));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (message.equalsIgnoreCase("on")){
            channel.sendMessage("Whitelist is on").queue();
        } else {
            channel.sendMessage("Whitelist is off").queue();
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
            if (Boolean.FALSE.equals(Main.IS_TESTING)) {
                channel.sendMessage(Constants.ADDED_TO_WHITELIST_MESSAGE).queue();
            }
            try {
                Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist add " + message));
                setWhitelistUsernames();
            } catch (NullPointerException | IOException e) {
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
            TextChannel modChannel = jda.getTextChannelById(setTestingChannel(Constants.MOD_LOGIN_CHANNEL_ID));
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
            TextChannel modChannel = jda.getTextChannelById(setTestingChannel(Constants.MOD_LOGIN_CHANNEL_ID));
            assert modChannel != null;
            modChannel.sendMessageEmbeds(eb.build()).queue();
        }
    }

    public static void setWhitelistUsernames() throws IOException {
        File whitelistFile = Main.whitelistFile;
        FileConfiguration whitelistCfg = Main.whitelistCfg;
        TextChannel whitelistChannel = jda.getTextChannelById(Constants.WHITELIST_CHANNEL_ID);
        assert whitelistChannel != null;
        MessageHistory history = MessageHistory.getHistoryFromBeginning(whitelistChannel).complete();
        List<Message> messageHistory = history.getRetrievedHistory();
        for (Message message : messageHistory) {
                whitelistCfg.set(Constants.YML_PLAYERS + message.getContentRaw() + ".discordName", message.getAuthor().getName());
                whitelistCfg.set(Constants.YML_PLAYERS + message.getContentRaw() + ".minecraftName", message.getContentRaw());
                whitelistCfg.set(Constants.YML_PLAYERS + message.getContentRaw() + ".joinedTime.month", message.getTimeCreated().getMonth().getValue());
                whitelistCfg.set(Constants.YML_PLAYERS + message.getContentRaw() + ".joinedTime.day", message.getTimeCreated().getDayOfYear());
                whitelistCfg.set(Constants.YML_PLAYERS + message.getContentRaw() + ".joinedTime.year", message.getTimeCreated().getYear());
        }
        whitelistCfg.save(whitelistFile);
    }

    public static void logPlayerBan(PlayerCommandPreprocessEvent event, Message messageFromDiscordConsole) {
        TextChannel banLogChannel = jda.getTextChannelById(Constants.BAN_LOG_CHANNEL_ID);
        assert banLogChannel != null;
        StringBuilder banReason = new StringBuilder();
        String[] banMessage = event.getMessage().split(" ");
        if (messageFromDiscordConsole == null){
            if (banMessage[0].equalsIgnoreCase("/ban")){
                try {
                    for (int i = 2; i <= banMessage.length - 1; i++) {
                        banReason.append(banMessage[i]).append(" ");
                    }
                    banLogChannel.sendMessage(getDiscordUserName(event.getPlayer().getName()) + " banned " + banMessage[1] + " | Reason: " + banReason + " | Discord Username: " + getDiscordUserId(getDiscordUserName(banMessage[1])) + ">").queue();
                } catch (NullPointerException | ArrayIndexOutOfBoundsException e){
                    banLogChannel.sendMessage(getDiscordUserName(event.getPlayer().getName()) + " banned " + banMessage[1] + " | Discord Username: " +  getDiscordUserId(getDiscordUserName(banMessage[1])) + ">").queue();
                }
            }
        } else {
            if (banMessage[0].equalsIgnoreCase("ban")){
                try {
                    for (int i = 2; i <= banMessage.length - 2; i++) {
                        banReason.append(banMessage[i]).append(" ");
                    }
                    banLogChannel.sendMessage(getDiscordUserName(event.getPlayer().getName()) + " banned " + banMessage[1] + " | Reason: " + banReason + " | Discord Username: " + getDiscordUserId(getDiscordUserName(banMessage[1])) + ">").queue();
                } catch (NullPointerException | ArrayIndexOutOfBoundsException e){
                    banLogChannel.sendMessage(getDiscordUserName(event.getPlayer().getName()) + " banned " + banMessage[1] + " | Discord Username: " + getDiscordUserId(getDiscordUserName(banMessage[1])) + ">").queue();
                }
            }
        }
    }

    public static void setDiscordUserNames() throws IOException {
        Guild guild = jda.getGuildById(Constants.GUILD_ID);
        Main.whitelistCfg.set("discordUsers", null);
        Main.whitelistCfg.save(Main.whitelistFile);
        for (Member member : guild.getMembers()) {
            String discordUserName = member.getUser().getName().replace(".", "");
            Main.whitelistCfg.set("discordUsers." + discordUserName + ".discordName", member.getUser().getName());
            if (!getMinecraftUserName(member.getUser().getName()).equals("")){
                Main.whitelistCfg.set("discordUsers." + discordUserName + ".minecraftName", getMinecraftUserName(member.getUser().getName()));
                Main.whitelistCfg.set("discordUsers." + discordUserName + ".delete", false);
            } else {
                Main.whitelistCfg.set("discordUsers." + discordUserName + ".delete", true);
            }
        }
        Main.whitelistCfg.save(Main.whitelistFile);
    }

    public static void purge(){
        ArrayList<String> discordMembers = new ArrayList<>();
        ArrayList<String> whitelistedPlayers = new ArrayList<>();
        for (OfflinePlayer player : Bukkit.getWhitelistedPlayers()){
            whitelistedPlayers.add(player.getName().toLowerCase());
            for (String minecraftUsername : Main.whitelistCfg.getConfigurationSection("players").getKeys(false)) {
                if (player.getName().equals(minecraftUsername)) {
                    discordMembers.add(minecraftUsername.toLowerCase());
                }
            }
        }
        ArrayList<String> test = new ArrayList<>();
        for (String d : whitelistedPlayers){
            if (!discordMembers.contains(d)){
                test.add(d);
            }
        }
        for (String d : test){
            Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist remove " + d));
        }

    }

    public static String getDiscordUserName(String playerName){
        String discordUserName = "";
        FileConfiguration whitelistCfg = Main.getFileConfiguration(Main.whitelistFile);
        for (String discordUName : whitelistCfg.getConfigurationSection(Constants.YML_PLAYERS).getKeys(false)) {
            if (playerName.equalsIgnoreCase(whitelistCfg.getString(Constants.YML_PLAYERS +  discordUName + ".minecraftName"))){
                discordUserName = whitelistCfg.getString(Constants.YML_PLAYERS +  discordUName + ".discordName");
            }
        }
        return discordUserName;
    }

    public void removePlayerWhitelistMessageOnLeave(String discordUserName){
        TextChannel whitelistChannel = jda.getTextChannelById(Constants.WHITELIST_CHANNEL_ID);
        assert whitelistChannel != null;
        MessageHistory history = MessageHistory.getHistoryFromBeginning(whitelistChannel).complete();
        List<Message> messageHistory = history.getRetrievedHistory();
        for (Message message : messageHistory){
            if (discordUserName.equalsIgnoreCase(message.getAuthor().getName())){
                message.delete().queue();
            }
        }
    }

    public static String getMinecraftUserName(String discordUserName){
        String minecraftUserName = "";
        for (String mcUsername : Main.whitelistCfg.getConfigurationSection(Constants.YML_PLAYERS).getKeys(false)) {
            if (discordUserName.equalsIgnoreCase(Main.whitelistCfg.getString(Constants.YML_PLAYERS +  mcUsername + ".discordName"))){
                minecraftUserName = Main.whitelistCfg.getString(Constants.YML_PLAYERS +  mcUsername + ".minecraftName");
            }
        }
        return minecraftUserName;
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
                TextChannel announcementChannel = jda.getTextChannelById(Constants.ANNOUNCEMENT_CHANNEL_ID);
                assert announcementChannel != null;
                announcementChannel.sendMessage("<@" + getDiscordUserId(discordUserName) +  "> will be banned :)- Their MC username is: " + playerName).queue();
                TextChannel banLogChannel = jda.getTextChannelById(Constants.BAN_LOG_CHANNEL_ID);
                assert banLogChannel != null;
                banLogChannel.sendMessage("Minecraft Username: " + playerName + " | Discord Username: <@" + getDiscordUserId(discordUserName) + "> | Reason: Stealing from spawn chest").queue();
            }
        } catch (NullPointerException ignored){

        }

    }


    public static String getDiscordUserId(String discordUserName){
        String discordUserId = "";
        for (Member member : jda.getGuildById(Constants.GUILD_ID).getMembers()){
            if (discordUserName.equalsIgnoreCase(member.getUser().getName())){
                discordUserId = member.getId();
            }
        }
        return discordUserId;
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
