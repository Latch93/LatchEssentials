package lmp;


import lmp.api.Api;
import lmp.constants.Constants;
import lmp.constants.YmlFileNames;
import lmp.discord.SendServerStartedMessageToDiscordEvent;
import lmp.discord.channelCommands.GeneralDiscordCommands;
import lmp.discord.channelCommands.LatchsConfigCommands;
import lmp.discord.channelCommands.SendLTSNominationForm;
import lmp.discord.channelCommands.ShowOnlinePlayersInDiscord;
import lmp.discord.chatMessageAdapters.*;
import lmp.discord.guildMemberJoinAdapters.NewGuildMemberJoin;
import lmp.discord.guildMemberRoleRemoveAdapters.RemoveServerRoleOnGuildMemberRemove;
import lmp.discord.guildRemoveAdapters.SetWhiteListOnMemberLeaveGuildEvent;
import lmp.discord.privateMessageAdapters.StaffApplication;
import lmp.discord.privateMessageAdapters.UnbanRequest;
import lmp.discord.reactionAdapters.AddDiscordRoleOnMemberMessageReactEvent;
import lmp.discord.reactionAdapters.RemoveDiscordRoleOnMemberMessageReactEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class LatchDiscord extends ListenerAdapter implements Listener {


    public static final Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME);

    public static String senderDiscordUsername = "";
    public static String senderDiscordUserID = "";
    public static User senderDiscordUser;
    public static Member senderDiscordMember;
    public static Message message;
    public static String messageContents;
    public static String messageID;
    public static Mentions mentionedChannelsList;
    public static List<User> mentionedUsersList;
    public static MessageChannel messageChannel;
    public static String messageChannelID;
    public static JDA jda;
    public static final JDABuilder jdaBuilder = JDABuilder.createDefault(Constants.DISCORD_BOT_TOKEN)
            .setChunkingFilter(ChunkingFilter.ALL) // enable member chunking for all guilds
            .setMemberCachePolicy(MemberCachePolicy.ALL) // ignored if chunking enabled
            .enableIntents(GatewayIntent.GUILD_MEMBERS)
            .enableIntents(GatewayIntent.MESSAGE_CONTENT);

    public LatchDiscord() throws LoginException {
        startBot();
        jda.addEventListener(this);
    }
    private void startBot() {
        jda = jdaBuilder.build();
        jda.addEventListener(new GeneralDiscordCommands());
        jda.addEventListener(new SendServerStartedMessageToDiscordEvent());
        jda.addEventListener(new AddDiscordRoleOnMemberMessageReactEvent());
        jda.addEventListener(new RemoveDiscordRoleOnMemberMessageReactEvent());
        jda.addEventListener(new SetWhiteListOnMemberLeaveGuildEvent());
        jda.addEventListener(new LogPlayerBanFromDiscordConsole());
        jda.addEventListener(new BanPlayerForTypingTheNWordInDiscordChat());
        jda.addEventListener(new RemoveServerRoleOnGuildMemberRemove());
        jda.addEventListener(new NewGuildMemberJoin());
        jda.addEventListener(new LogGithubIssue());
        jda.addEventListener(new LatchsConfigCommands());
        jda.addEventListener(new ShowOnlinePlayersInDiscord());
        jda.addEventListener(new TestChannelChatEvent());
        jda.addEventListener(new SendLTSNominationForm());
        jda.addEventListener(new SendAFKMessageIfMentionLatch());
    }

    public static JDA getJDA() {
        return jda;
    }
    public static Message getMessage(){return message;}

    public static String convertDiscordMessageToServer(Member member, String message, String senderName, Boolean isReply, Message repliedMessage) {
        int count = 0;
        String highestRole = "Member";
        ChatColor colorCode;
        for (Role role : Objects.requireNonNull(member).getRoles()) {
            if (role.getPosition() >= count) {
                count = role.getPosition();
                highestRole = role.getName();
            }
        }
        if (highestRole.equalsIgnoreCase("Owner")) {
            colorCode = ChatColor.GOLD;
        } else if (highestRole.toLowerCase().contains("admin")) {
            colorCode = ChatColor.RED;
        } else if (highestRole.toLowerCase().contains("mod")) {
            colorCode = ChatColor.LIGHT_PURPLE;
        } else if (highestRole.toLowerCase().contains("builder")) {
            colorCode = ChatColor.BLUE;
        } else {
            colorCode = ChatColor.GREEN;
        }
        String finalMessage;
        if (isReply) {
            finalMessage = ChatColor.WHITE + "[" + ChatColor.AQUA + "Discord" + ChatColor.WHITE + " | " + colorCode + highestRole + ChatColor.WHITE + "] " + senderName + ChatColor.GRAY + " » " + ChatColor.WHITE + "Replied to " + ChatColor.GOLD + repliedMessage.getAuthor().getName() +
                    ChatColor.GRAY + " » " + ChatColor.GREEN + "'" + repliedMessage.getContentRaw() + "'" + ChatColor.GRAY + " » " + ChatColor.WHITE + message;
        } else {
            finalMessage = ChatColor.WHITE + "[" + ChatColor.AQUA + "Discord" + ChatColor.WHITE + " | " + colorCode + highestRole + ChatColor.WHITE + "] " + senderName + " » " + message;
        }

        return finalMessage;
    }

    public static void sendServerStoppedMessage() {
        DateTime dt = new DateTime();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("MMM-dd-yyyy hh:mm:ssa z");
        String dtStr = fmt.print(dt);
        FileConfiguration configCfg = Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME);
        configCfg.set("serverStopTime", dtStr);
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("LMP Server has stopped", null);
        eb.setColor(new Color(0xE15C0000, true));
        eb.setDescription(dtStr);
        eb.setThumbnail("https://raw.githubusercontent.com/Latch93/DiscordText/master/src/main/resources/lmp_discord_image.png");
        TextChannel minecraftChannel = jda.getTextChannelById(lmp.Constants.MINECRAFT_CHAT_CHANNEL_ID);
        assert minecraftChannel != null;
        minecraftChannel.sendMessageEmbeds(eb.build()).queue();
    }

    public static void setDiscordId() throws IOException {
        FileConfiguration whitelistCfg = Api.getFileConfiguration(YmlFileNames.YML_WHITELIST_FILE_NAME);
        List<Member> members = Objects.requireNonNull(jda.getGuildById(lmp.Constants.GUILD_ID)).getMembers();
        for (String playerName : Objects.requireNonNull(whitelistCfg.getConfigurationSection("players")).getKeys(false)) {
            for (Member member : members) {
                if (member.getUser().getName().equalsIgnoreCase(whitelistCfg.getString("players." + playerName + ".discordName"))) {
                    whitelistCfg.set("players." + playerName + ".discordId", member.getId());
                    whitelistCfg.set("players." + playerName + ".joinedTime", null);
                    whitelistCfg.set("players." + playerName + ".joinTime", member.getTimeJoined().toLocalDateTime().toString());
                }
            }
            if (!whitelistCfg.isSet("players." + playerName + ".discordId")) {
                whitelistCfg.set("players." + playerName, null);
            }
        }
        whitelistCfg.save(Api.getConfigFile(YmlFileNames.YML_WHITELIST_FILE_NAME));
    }

    public static String getDiscordUserId(String discordUserName) {
        String discordUserId = "";
        for (Member member : Objects.requireNonNull(jda.getGuildById(lmp.Constants.GUILD_ID)).getMembers()) {
            if (discordUserName.equalsIgnoreCase(member.getUser().getName())) {
                discordUserId = member.getId();
            }
        }
        return discordUserId;
    }
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        messageChannel = e.getChannel();
        message = e.getMessage();
        if (e.getMember() != null) {
            senderDiscordUsername = Objects.requireNonNull(e.getMember().getUser().getName());
            senderDiscordUserID = Objects.requireNonNull(e.getMember().getId());
        }
        mentionedChannelsList = message.getMentions();
        mentionedUsersList = message.getMentions().getUsers();
        messageID = e.getMessageId();
        messageContents = e.getMessage().getContentRaw();
        senderDiscordUsername = e.getAuthor().getName();
        senderDiscordMember = e.getMember();
        messageChannelID = e.getChannel().getId();
        senderDiscordUser = e.getAuthor();
        jda = getJDA();
        for (GuildChannel guildChannel : mentionedChannelsList.getChannels()) {
            messageContents = messageContents.replace(guildChannel.getId(), guildChannel.getName());
        }
        for (User user : mentionedUsersList) {
            messageContents = messageContents.replace(user.getId(), user.getName());
        }
        if (Boolean.FALSE.equals(Main.getIsParameterInTesting("global"))) {
            try {
                // Get online players and include vanished players
                // List all blocks mined by a player
//                if (messageChannel.getId().equals(Constants.DISCORD_STAFF_CHAT_CHANNEL_ID) && messageContents.toLowerCase().contains("!searchblocks")) {
//                    String[] arr = e.getMessage().getContentRaw().split("!searchBlocks");
//                    String userID = Api.getMinecraftIdFromDCid(arr[1]);
//                    FileConfiguration
//                    clearAllUserMessages(messageChannel, messageID, userID);
//                }
                // get the
                if ((messageChannel.getId().equalsIgnoreCase(lmp.Constants.MINECRAFT_CHAT_CHANNEL_ID) && !e.getAuthor().getId().equals(lmp.Constants.LATCH93BOT_USER_ID))) {
                    if (e.getMessage().getReferencedMessage() != null) {
                        Bukkit.broadcastMessage(convertDiscordMessageToServer(senderDiscordMember, messageContents, senderDiscordUsername, true, e.getMessage().getReferencedMessage()));
                    } else {
                        Bukkit.broadcastMessage(convertDiscordMessageToServer(senderDiscordMember, messageContents, senderDiscordUsername, false, null));
                    }
                }
                if ((messageChannel.getId().equalsIgnoreCase(lmp.Constants.MINECRAFT_CHAT_CHANNEL_ID) || messageChannel.getId().equalsIgnoreCase(lmp.Constants.DISCORD_STAFF_CHAT_CHANNEL_ID))) {
                    if (messageContents.toLowerCase().contains("!searchdiscord")) {
                        String[] messageArr = messageContents.split(" ");
                        try {
                            String discordUserName = Api.getDiscordNameFromMCid(Api.getMinecraftIdFromMinecraftName(messageArr[1]));
                            messageChannel.sendMessage(messageArr[1] + "'s Discord username is: " + discordUserName).queue();

                        } catch (IllegalArgumentException error) {
                            messageChannel.sendMessage("That player does not have a discord account linked to their minecraft account. Maybe try again.\nCommand usage: !searchDiscord [minecraftName]").queue();
                        }
                    } else if (messageContents.toLowerCase().contains("!searchminecraft")) {
                        String[] messageArr = messageContents.split(" ");
                        try {

                                if (messageArr.length > 0) {
                                    String minecraftUsername = Bukkit.getOfflinePlayer(UUID.fromString(Api.getMinecraftIdFromDCid(messageArr[1]))).getName();
                                    messageChannel.sendMessage(Objects.requireNonNull(jda.getUserById(messageArr[1])).getName() + "'s Minecraft username is: " + minecraftUsername).queue();
                                }
                        } catch (IllegalArgumentException error) {
                            messageChannel.sendMessage("That player does not have a minecraft account linked to their discord account. Maybe try again.\nCommand usage: !searchMinecraft [discordID]").queue();
                        }
                    }
                }
                // Sends staff application to member
                if (messageChannel.getId().equals(lmp.Constants.STAFF_APPLICATION_CHANNEL_ID) && messageContents.equalsIgnoreCase(lmp.Constants.STAFF_APPLY_COMMAND)) {
                    messageChannel.deleteMessageById(messageID).queue();
                    e.getAuthor().openPrivateChannel().flatMap(privateChannel -> {
                        TextChannel applicationSubmittedChannel = jda.getTextChannelById(lmp.Constants.STAFF_APP_SUBMITTED_CHANNEL_ID);
                        e.getJDA().addEventListener(new StaffApplication(privateChannel, e.getAuthor(), applicationSubmittedChannel));
                        return privateChannel.sendMessage("\nResponsibilities as a Jr. Mod:\n*** Watch new players on the server while in vanish.\n*** Ensure new players are following the rules, i.e., not x-raying, griefing, stealing or being a jerk.\n" +
                                "Please enter your application information line by line.\nPress enter after each question response.\n" +
                                "1.) How old are you?");
                    }).queue();
                }
                if (messageChannel.getId().equals(lmp.Constants.PIXELMON_CHANNEL_ID) && messageContents.contains("!startPixelmon")) {
                    ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "startPixelmon.bat");
                    File dir = new File("E:\\Pixelmon");
                    pb.directory(dir);
                    pb.start();
                }
                // Sends unban request to member
                if (messageChannel.getId().equals(lmp.Constants.UNBAN_REQUEST_CHANNEL_ID) && messageContents.equalsIgnoreCase(lmp.Constants.UNBAN_REQUEST)) {
                    messageChannel.deleteMessageById(messageID).queue();
                    e.getAuthor().openPrivateChannel().flatMap(privateChannel -> {
                        TextChannel unbanRequestSubmittedChannel = jda.getTextChannelById(lmp.Constants.UNBAN_REQUEST_COMPLETE_CHANNEL_ID);
                        e.getJDA().addEventListener(new UnbanRequest(privateChannel, e.getAuthor(), unbanRequestSubmittedChannel));
                        return privateChannel.sendMessage("Please enter your unban form line by line. \n Press enter after each question response. \n 1.) What is your Minecraft username?");
                    }).queue();
                }
                if (lmp.Constants.SEARCH_CHANNEL_ID.equalsIgnoreCase(messageChannel.getId())) {
                    File configFile = new File(Main.getPlugin(Main.class).getDataFolder(), "playerShops.yml");
                    FileConfiguration configCfg = YamlConfiguration.loadConfiguration(configFile);
                    if (messageContents.toLowerCase().contains("searchall")) {
                        int count = 0;
                        for (String player : configCfg.getKeys(false)) {
                            if (configCfg.isSet(player + ".slots")) {
                                for (String slot : configCfg.getConfigurationSection(player + ".slots").getKeys(false)) {
                                    ItemStack is = configCfg.getItemStack(player + lmp.Constants.YML_SLOTS + slot);
                                    assert is != null;
                                    String iso = new ItemStack(is.getType(), 1).toString();
                                    String itemString = count + " has " + is.getAmount() + " " + is.getType() + " in their shop for $" + configCfg.getDouble(player + ".itemworth." + iso) + " per " + is.getType();
                                    e.getAuthor().openPrivateChannel().flatMap(dm -> dm.sendMessage(itemString)).queue();
                                }
                            }
                            count++;
                        }
                    }
                }
                // Searches the player shops and returns if items are in a player's shop
                if (lmp.Constants.SEARCH_CHANNEL_ID.equalsIgnoreCase(messageChannel.getId())) {
                    File configFile = new File(Main.getPlugin(Main.class).getDataFolder(), "playerShops.yml");
                    FileConfiguration configCfg = YamlConfiguration.loadConfiguration(configFile);
                    if (messageContents.toLowerCase().contains(lmp.Constants.SEARCH_PLAYER_SHOP_COMMAND)) {
                        String[] arr = messageContents.split(lmp.Constants.SEARCH_PLAYER_SHOP_COMMAND);
                        String itemToSearch = arr[1].replace(" ", "").toUpperCase();
                        String playerName = "";
                        boolean isItemAvailable = false;

                        for (String player : configCfg.getKeys(false)) {
                            int totalAmount = 0;
                            if (configCfg.isSet(player)) {
                                if (configCfg.isSet(player + ".slots")) {
                                    for (String slot : configCfg.getConfigurationSection(player + ".slots").getKeys(false)) {
                                        if (configCfg.getItemStack(player + lmp.Constants.YML_SLOTS + slot) != null) {
                                            ItemStack is = configCfg.getItemStack(player + lmp.Constants.YML_SLOTS + slot);
                                            if (itemToSearch.equalsIgnoreCase(is.getType().toString()) || itemToSearch.toLowerCase().contains("everything")) {
                                                totalAmount += is.getAmount();
                                            }
                                        }
                                    }
                                    if (totalAmount != 0) {
                                        isItemAvailable = true;
                                        if (!itemToSearch.equalsIgnoreCase("spawn_egg")) {
                                            ItemStack is = new ItemStack(Material.valueOf(itemToSearch.toUpperCase()), 1);
                                            if (configCfg.getInt(player + ".itemWorth." + is) != 0) {
                                                messageChannel.sendMessage(Bukkit.getOfflinePlayer(UUID.fromString(player)).getName() + " has " + totalAmount + " " + itemToSearch + "(s) in their shop for $" + configCfg.getDouble(player + ".itemWorth." + is) + " per item.").queue();
                                            } else {
                                                messageChannel.sendMessage(Bukkit.getOfflinePlayer(UUID.fromString(player)).getName() + " has " + totalAmount + " " + itemToSearch + "(s) in their shop.").queue();
                                            }
                                        } else {
                                            messageChannel.sendMessage(Bukkit.getOfflinePlayer(UUID.fromString(player)).getName() + " has " + totalAmount + " " + itemToSearch + "(s) in their shop.").queue();
                                        }
                                    }

                                }
                            }
                        }
                        if (Boolean.FALSE.equals(isItemAvailable)) {
                            messageChannel.sendMessage("No one has " + itemToSearch + "s in their shop").queue();
                        }
                    }
                }
            } catch (NullPointerException | IOException error) {
                error.printStackTrace();
            }
        }
        if (messageChannel.getId().equalsIgnoreCase(lmp.Constants.DISCORD_STAFF_CHAT_CHANNEL_ID) && !e.getAuthor().getId().equalsIgnoreCase(lmp.Constants.LATCH93BOT_USER_ID)) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("group.jr-mod")) {
                    if (e.getMessage().getReferencedMessage() != null) {
                        player.sendMessage("[" + ChatColor.LIGHT_PURPLE + "Mod-Chat" + ChatColor.WHITE + "]-" + convertDiscordMessageToServer(senderDiscordMember, messageContents, senderDiscordUsername, true, e.getMessage().getReferencedMessage()));
                    } else {
                        player.sendMessage("[" + ChatColor.LIGHT_PURPLE + "Mod-Chat" + ChatColor.WHITE + "]-" + convertDiscordMessageToServer(senderDiscordMember, messageContents, senderDiscordUsername, false, null));
                    }
                }
            }
        }
        if (messageChannel.getId().equalsIgnoreCase(lmp.Constants.ADMIN_CHANNEL_ID) && !e.getAuthor().getId().equalsIgnoreCase(lmp.Constants.LATCH93BOT_USER_ID)) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("group.admin")) {
                    if (e.getMessage().getReferencedMessage() != null) {
                        player.sendMessage("[" + ChatColor.DARK_PURPLE + "Admin-Chat" + ChatColor.WHITE + "]-" + convertDiscordMessageToServer(senderDiscordMember, messageContents, senderDiscordUsername, true, e.getMessage().getReferencedMessage()));
                    } else {
                        player.sendMessage("[" + ChatColor.DARK_PURPLE + "Admin-Chat" + ChatColor.WHITE + "]-" + convertDiscordMessageToServer(senderDiscordMember, messageContents, senderDiscordUsername, false, null));
                    }
                }
            }
        }
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        Api.messageInConsole(ChatColor.GOLD + Objects.requireNonNull(event.getMember()).getUser().getName() + ChatColor.RED + " left the discord.");
        TextChannel byebyeDiscordChat = jda.getTextChannelById(lmp.Constants.BYE_BYE_DISCORD_CHANNEL_ID);
        assert byebyeDiscordChat != null;
        String filePath = Api.getMainPlugin().getDataFolder().getPath();
        String folderName = "/gifs";
        int gifCount = Objects.requireNonNull(new File(filePath + folderName).list()).length;
        Random rand = new Random();
        int n = rand.nextInt(gifCount);
        byebyeDiscordChat.sendMessage(event.getMember().getUser().getName() + " left Discord. ").addFiles(FileUpload.fromData(new File(filePath + folderName + "/bye_bye" + n + ".gif"))).queue();
    }


//    public static void setChannelDescription() {
//        TextChannel minecraftChatChannel = jda.getTextChannelById(Constants.MINECRAFT_CHAT_CHANNEL_ID);
//        String maxPlayerCount = String.valueOf(Bukkit.getServer().getMaxPlayers());
//        assert minecraftChatChannel != null;
//        int count = 0;
//        for (Player player : Bukkit.getOnlinePlayers()){
//            if (!Api.isPlayerInvisible(player.getUniqueId().toString())){
//                count++;
//            }
//        }
//        FileConfiguration configCfg = Api.getFileConfiguration(Api.getConfigFile(YmlFileNames.YML_CONFIG_FILE_NAME));
//        DateTime currentTime = new DateTime();
//        DateTimeFormatter fmt = DateTimeFormat.forPattern("MM-dd-yyyy HH:mm:ss");
//        String current = fmt.print(currentTime);
//        DateTime startTime = fmt.parseDateTime(configCfg.getString("serverStartTime"));
//        DateTime now = fmt.parseDateTime(current);
//        Duration duration = new Duration(startTime, now);
//        minecraftChatChannel.getManager().setTopic("Online Players: " + count + "/" + maxPlayerCount + " | Server Uptime: " + duration.getStandardMinutes() + " minutes.").queue();
//    }

    public void clearAllUserMessages(MessageChannel channel, String messageId, String userID) {
        channel.deleteMessageById(messageId).queue();
        MessageHistory history = MessageHistory.getHistoryFromBeginning(channel).complete();
        List<Message> messageHistory = history.getRetrievedHistory();
        for (Message message : messageHistory) {
            if (message.getAuthor().getId().equalsIgnoreCase(userID)) {
                channel.deleteMessageById(message.getId()).queue();
            }
        }
    }

}
