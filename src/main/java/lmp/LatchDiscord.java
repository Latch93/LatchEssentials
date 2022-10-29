package lmp;


import lmp.api.Api;
import lmp.constants.Constants;
import lmp.constants.YmlFileNames;
import lmp.discord.SendServerStartedMessageToDiscordEvent;
import lmp.discord.channelCommands.GeneralDiscordCommands;
import lmp.discord.channelCommands.LatchsConfigCommands;
import lmp.discord.chatMessageAdapters.BanPlayerForTypingTheNWordInDiscordChat;
import lmp.discord.chatMessageAdapters.LogGithubIssue;
import lmp.discord.chatMessageAdapters.LogPlayerBanFromDiscordConsole;
import lmp.discord.guildMemberJoinAdapters.NewGuildMemberJoin;
import lmp.discord.guildMemberRoleRemoveAdapters.RemoveServerRoleOnGuildMemberRemove;
import lmp.discord.guildRemoveAdapters.SetWhiteListOnMemberLeaveGuildEvent;
import lmp.discord.privateMessageAdapters.LTSNomination;
import lmp.discord.privateMessageAdapters.StaffApplication;
import lmp.discord.privateMessageAdapters.UnbanRequest;
import lmp.discord.reactionAdapters.AddDiscordRoleOnMemberMessageReactEvent;
import lmp.discord.reactionAdapters.RemoveDiscordRoleOnMemberMessageReactEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
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
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class LatchDiscord extends ListenerAdapter implements Listener {


    public static final Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME);

    public static String username = "";
    public static String userId = "";
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
        jda.addEventListener(new SendServerStartedMessageToDiscordEvent());
        jda.addEventListener(new AddDiscordRoleOnMemberMessageReactEvent());
        jda.addEventListener(new RemoveDiscordRoleOnMemberMessageReactEvent());
        jda.addEventListener(new SetWhiteListOnMemberLeaveGuildEvent());
        jda.addEventListener(new LogPlayerBanFromDiscordConsole());
        jda.addEventListener(new BanPlayerForTypingTheNWordInDiscordChat());
        jda.addEventListener(new RemoveServerRoleOnGuildMemberRemove());
        jda.addEventListener(new NewGuildMemberJoin());
        jda.addEventListener(new LogGithubIssue());
        jda.addEventListener(new GeneralDiscordCommands());
        jda.addEventListener(new LatchsConfigCommands());
    }

    public static JDA getJDA() {
        return jda;
    }

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
    public void onMessageReceived(MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();
        if (event.getMember() != null) {
            username = Objects.requireNonNull(event.getMember().getUser().getName());
            userId = Objects.requireNonNull(event.getMember().getId());
        }
        Mentions mentionedChannelsList = event.getMessage().getMentions();
        List<User> mentionedUsersList = event.getMessage().getMentions().getUsers();
        String messageId = event.getMessageId();
        String message = event.getMessage().getContentRaw();
        String senderName = event.getAuthor().getName();
        Member messageSender = event.getMember();
        for (GuildChannel guildChannel : mentionedChannelsList.getChannels()) {
            message = message.replace(guildChannel.getId(), guildChannel.getName());
        }
        for (User user : mentionedUsersList) {
            message = message.replace(user.getId(), user.getName());
        }
        if (Boolean.FALSE.equals(Main.getIsParameterInTesting("global"))) {
            try {
                // Gets online players
                if (channel.getId().equals(lmp.Constants.MINECRAFT_CHAT_CHANNEL_ID) && message.equalsIgnoreCase(lmp.Constants.ONLINE_COMMAND)) {
                    ArrayList<String> onlinePlayers = new ArrayList<>();
                    EmbedBuilder eb = new EmbedBuilder();
                    TextChannel minecraftChannel = jda.getTextChannelById(lmp.Constants.MINECRAFT_CHAT_CHANNEL_ID);
                    assert minecraftChannel != null;
                    StringBuilder onlinePlayerMessage = new StringBuilder();
                    int count = 1;
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        File playerDataFile = new File("plugins/Essentials/userdata", player.getUniqueId() + ".yml");
                        FileConfiguration playerDataCfg = YamlConfiguration.loadConfiguration(playerDataFile);
                        if (Boolean.FALSE.equals(Api.isPlayerInvisible(player.getUniqueId().toString()))) {
                            String afk = "";
                            if (Boolean.TRUE.equals(playerDataCfg.getBoolean("afk"))) {
                                afk = "(AFK)";
                            }
                            onlinePlayerMessage.append(Api.convertMinecraftMessageToDiscord(null, count + ".) " + player.getDisplayName()) + " " + afk + "\n");
                            onlinePlayers.add(player.getDisplayName());
                            count++;
                        }
                    }

                    if (!onlinePlayers.isEmpty()) {
                        eb.setColor(new Color(0xC6D13EFF, true));
                        eb.setTitle("Online Players: " + onlinePlayers.size() + "/35");
                        eb.setDescription(onlinePlayerMessage.toString());
                    } else {
                        eb.setColor(new Color(0xC6D5042E, true));
                        eb.setTitle("Online Players: 0/35 players");
                    }
                    minecraftChannel.sendMessageEmbeds(eb.build()).queue();
                }
                // Get online players and include vanished players
                if (channel.getId().equals(lmp.Constants.DISCORD_STAFF_CHAT_CHANNEL_ID) && message.equalsIgnoreCase(lmp.Constants.ONLINE_COMMAND)) {
                    ArrayList<String> onlinePlayers = new ArrayList<>();
                    EmbedBuilder eb = new EmbedBuilder();
                    TextChannel minecraftChannel = jda.getTextChannelById(lmp.Constants.DISCORD_STAFF_CHAT_CHANNEL_ID);
                    assert minecraftChannel != null;
                    StringBuilder onlinePlayerMessage = new StringBuilder();
                    int count = 1;
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        File playerDataFile = new File("plugins/Essentials/userdata", player.getUniqueId() + ".yml");
                        FileConfiguration playerDataCfg = YamlConfiguration.loadConfiguration(playerDataFile);
                        String afk = "";
                        if (Boolean.TRUE.equals(playerDataCfg.getBoolean("afk"))) {
                            afk = "(AFK)";
                        }
                        if (Boolean.TRUE.equals(Api.isPlayerInvisible(player.getUniqueId().toString()))) {
                            onlinePlayerMessage.append(Api.convertMinecraftMessageToDiscord(null, count + ".) " + player.getDisplayName()) + " (Invisible) " + afk + "\n");
                            onlinePlayers.add(player.getDisplayName());
                            count++;
                        } else {
                            onlinePlayerMessage.append(Api.convertMinecraftMessageToDiscord(null, count + ".) " + player.getDisplayName()) + " " + afk + "\n");
                            onlinePlayers.add(player.getDisplayName());
                            count++;
                        }
                    }

                    if (!onlinePlayers.isEmpty()) {
                        eb.setColor(new Color(0xC6D13EFF, true));
                        eb.setTitle("Online Players: " + onlinePlayers.size());
                        eb.setDescription(onlinePlayerMessage.toString());
                    } else {
                        eb.setColor(new Color(0xC6D5042E, true));
                        eb.setTitle("No Players Online");
                    }
                    minecraftChannel.sendMessageEmbeds(eb.build()).queue();
                }
                // List all blocks mined by a player
//                if (channel.getId().equals(Constants.DISCORD_STAFF_CHAT_CHANNEL_ID) && message.toLowerCase().contains("!searchblocks")) {
//                    String[] arr = event.getMessage().getContentRaw().split("!searchBlocks");
//                    String userID = Api.getMinecraftIdFromDCid(arr[1]);
//                    FileConfiguration
//                    clearAllUserMessages(channel, messageId, userID);
//                }
                // get the
                if ((channel.getId().equalsIgnoreCase(lmp.Constants.MINECRAFT_CHAT_CHANNEL_ID) && !event.getAuthor().getId().equals(lmp.Constants.LATCH93BOT_USER_ID))) {
                    if (event.getMessage().getReferencedMessage() != null) {
                        Bukkit.broadcastMessage(convertDiscordMessageToServer(messageSender, message, senderName, true, event.getMessage().getReferencedMessage()));
                    } else {
                        Bukkit.broadcastMessage(convertDiscordMessageToServer(messageSender, message, senderName, false, null));
                    }
                }
                if ((channel.getId().equalsIgnoreCase(lmp.Constants.MINECRAFT_CHAT_CHANNEL_ID) || channel.getId().equalsIgnoreCase(lmp.Constants.DISCORD_STAFF_CHAT_CHANNEL_ID))) {
                    if (message.toLowerCase().contains("!searchdiscord")) {
                        String[] messageArr = message.split(" ");
                        try {
                            String discordUserName = Api.getDiscordNameFromMCid(Api.getMinecraftIdFromMinecraftName(messageArr[1]));
                            channel.sendMessage(messageArr[1] + "'s Discord username is: " + discordUserName).queue();

                        } catch (IllegalArgumentException e) {
                            channel.sendMessage("That player does not have a discord account linked to their minecraft account. Maybe try again.\nCommand usage: !searchDiscord [minecraftName]").queue();
                        }
                    } else if (message.toLowerCase().contains("!searchminecraft")) {
                        String[] messageArr = message.split(" ");
                        try {
                            String minecraftUsername = Bukkit.getOfflinePlayer(UUID.fromString(Api.getMinecraftIdFromDCid(messageArr[1]))).getName();
                            channel.sendMessage(jda.getUserById(messageArr[1]).getName() + "'s Minecraft username is: " + minecraftUsername).queue();

                        } catch (IllegalArgumentException e) {
                            channel.sendMessage("That player does not have a minecraft account linked to their discord account. Maybe try again.\nCommand usage: !searchMinecraft [discordID]").queue();
                        }
                    }
                }
                if (channel.getId().equals(lmp.Constants.TEST_CHANNEL_ID) && message.equalsIgnoreCase("claim")) {
                    FileConfiguration claimFileCfg = Api.getFileConfiguration(YmlFileNames.YML_USER_DONATION_REWARD_FILE_NAME);
                    for (String user : Objects.requireNonNull(claimFileCfg.getConfigurationSection("players")).getKeys(false)) {
                        if (!Api.getMinecraftIDOfLinkedPlayersInDiscord().contains(user)) {
                            claimFileCfg.set(lmp.Constants.YML_PLAYERS + user, null);
                        }
                    }
                    claimFileCfg.save(Api.getConfigFile(YmlFileNames.YML_USER_DONATION_REWARD_FILE_NAME));
                }
                if (channel.getId().equals(lmp.Constants.TEST_CHANNEL_ID) && message.contains("cong")) {
                    event.getAuthor().openPrivateChannel().flatMap(dm -> dm.sendMessage("Welcome to LMP (Latch Multiplayer)\n" +
                            "Users on PS4|XBox|Switch|Mobile are able to join\n" +
                            "Please read the rules in the Rules channel and react to the rules message (thumbUp)\n" +
                            "Doing this assigns you the member role and access to all of the channels.\n" +
                            "You can get the IP in the server-ip channel.\n" +
                            "For Bedrock|Console users, you need to download the mobile app Bedrock Together:\n" +
                            "Android: https://play.google.com/store/apps/details?id=pl.extollite.bedrocktogetherapp&hl=en_US&gl=US\n" +
                            "iOS: https://apps.apple.com/us/app/bedrocktogether/id1534593376\n" +
                            "When you first join the server, you won't be able to move because you need to link your Discord account with your Minecraft Account.\n" +
                            "In order to link accounts and move around the server freely, go to the General channel in the Discord Server and type the following command -> !link\n" +
                            "My bot will generate a command for you run on your minecraft client chat.\n" +
                            "Copy and paste this command or type it out, its formatted like this -> /lmp link [discordID]\n" +
                            "I link accounts so you can use the features I coded into the game.\n" +
                            "View our wiki with commands and other information here -> https://github.com/Latch93/DiscordText/wiki/Server-Commands\n" +
                            "If you have any questions, feel free to post them in the Discord and Happy Mining!!!")).queue();
                }
                if (channel.getId().equals(lmp.Constants.TEST_CHANNEL_ID) && message.contains("pog")) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
                    //Here you say to java the initial timezone. This is the secret
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    //Will print in UTC
                    FileConfiguration whitelistCfg = Api.getFileConfiguration(YmlFileNames.YML_WHITELIST_FILE_NAME);
                    for (String user : whitelistCfg.getConfigurationSection("players").getKeys(false)) {
                        Main.log.info(sdf.format(calendar.getTime()));
                        sdf.setTimeZone(TimeZone.getTimeZone(whitelistCfg.getString("players." + user + ".ip-info.timezoneName")));
                        Main.log.info("Time for " + whitelistCfg.getString("players." + user + ".discordName") + " is -> " + sdf.format(calendar.getTime()));
                    }
                    //Here you set to your timezone
                    //Will print on your default Timezone
                }

                // Sends LTS Nomination Form to member
                if (channel.getId().equals(lmp.Constants.LTS_NOMINEE_CHANNEL_ID) && message.equalsIgnoreCase(lmp.Constants.LTS_NOMINATION_COMMAND)) {
                    channel.deleteMessageById(messageId).queue();
                    if (message.equalsIgnoreCase(lmp.Constants.LTS_NOMINATION_COMMAND)) {
                        event.getAuthor().openPrivateChannel().flatMap(privateChannel -> {
                            TextChannel applicationSubmittedChannel = jda.getTextChannelById(lmp.Constants.LTS_NOMINEE_CHANNEL_ID);
                            event.getJDA().addEventListener(new LTSNomination(privateChannel, event.getAuthor(), applicationSubmittedChannel));
                            return privateChannel.sendMessage("Please enter your nominations line by line. \n" +
                                    "URL Link to Custom Mob Heads -> https://minecraft-heads.com/custom-heads\n" +
                                    "Copy and paste the URL link to the head you want to nominate for the first response.\n" +
                                    "Press enter after each question response.\n" +
                                    "1.) What is your Custom Head Nomination? Please paste the URL Link.");
                        }).queue();
                    }
                }
                // Sends staff application to member
                if (channel.getId().equals(lmp.Constants.STAFF_APPLICATION_CHANNEL_ID) && message.equalsIgnoreCase(lmp.Constants.STAFF_APPLY_COMMAND)) {
                    channel.deleteMessageById(messageId).queue();
                    event.getAuthor().openPrivateChannel().flatMap(privateChannel -> {
                        TextChannel applicationSubmittedChannel = jda.getTextChannelById(lmp.Constants.STAFF_APP_SUBMITTED_CHANNEL_ID);
                        event.getJDA().addEventListener(new StaffApplication(privateChannel, event.getAuthor(), applicationSubmittedChannel));
                        return privateChannel.sendMessage("\nResponsibilities as a Jr. Mod:\n*** Watch new players on the server while in vanish.\n*** Ensure new players are following the rules, i.e., not x-raying, griefing, stealing or being a jerk.\n" +
                                "Please enter your application information line by line.\nPress enter after each question response.\n" +
                                "1.) How old are you?");
                    }).queue();
                }

                if (channel.getId().equals(lmp.Constants.TEST_CHANNEL_ID) && message.equalsIgnoreCase("!mark")) {
                    TextChannel byebyeDiscordChat = jda.getTextChannelById(lmp.Constants.TEST_CHANNEL_ID);
                    assert byebyeDiscordChat != null;
                    String filePath = Api.getMainPlugin().getDataFolder().getPath();
                    String folderName = "/gifs";


                    int gifCount = Objects.requireNonNull(new File(filePath + folderName).list()).length;
                    Random rand = new Random();
                    int n = rand.nextInt(gifCount);
                    byebyeDiscordChat.sendMessage(event.getMember().getUser().getName() + " left Discord. ").addFiles(FileUpload.fromData(new File(filePath + folderName + "/bye_bye" + n + ".gif"))).queue();
                }
                if (channel.getId().equals(lmp.Constants.TEST_CHANNEL_ID) && message.equalsIgnoreCase("!gifs")) {
                    ThreadChannel gifThread = jda.getThreadChannelById("1034135259060650075");
                    FileConfiguration gifCfg = Api.getFileConfiguration(YmlFileNames.YML_GIF_LIST_FILE_NAME);
                    List<String> giflinkList = new ArrayList<>();
                    List<Message> messageList;
                    assert gifThread != null;
                    messageList = gifThread.getIterableHistory().takeAsync(50) // Collect 1000 messages
                            .thenApply(ArrayList::new)
                            .get();
                    Collections.reverse(messageList);

                    for (Message gifMessage : messageList) {
                        if (gifMessage.getAttachments().isEmpty()) {
                            giflinkList.add(gifMessage.getContentRaw());
                        } else {
                            Main.log.info("attach: " + gifMessage.getAttachments().get(0).getUrl());
                            giflinkList.add(gifMessage.getAttachments().get(0).getUrl());
                        }
                    }
                    gifCfg.set("gifList", giflinkList);
                    gifCfg.save(Api.getConfigFile(YmlFileNames.YML_GIF_LIST_FILE_NAME));
                    int count = 0;
                    for (String gifURL : giflinkList) {
                        byte[] b = new byte[1];
                        URL url = new URL(gifURL);
                        URLConnection urlConnection = url.openConnection();
                        urlConnection.connect();
                        DataInputStream di = new DataInputStream(urlConnection.getInputStream());

                        String filePath = Api.getMainPlugin().getDataFolder().getPath();
                        String fileName = "/gifs/bye_bye" + count + ".gif";
                        FileOutputStream fo = new FileOutputStream(filePath + fileName);
                        while (-1 != di.read(b, 0, 1)) {
                            fo.write(b, 0, 1);
                        }
                        di.close();
                        fo.close();
                        count++;
                    }
                }
                if (channel.getId().equals(lmp.Constants.TEST_CHANNEL_ID) && message.equalsIgnoreCase("!compare")) {
                    List<String> keep = Files.readAllLines(Paths.get("C:/Users/Latch/Downloads/keep.txt"));
                    List<String> tableIDS = Files.readAllLines(Paths.get("C:/Users/Latch/Desktop/sqlt_data_1_2015_01.txt"));
                    List<String> delete = new ArrayList<>();
                    List<String> inTable = new ArrayList<>();
                    int count = 0;
                    FileConfiguration test = Api.getFileConfiguration("deleteList");
                    final Set<String> setToReturn = new HashSet<>();
                    final Set<String> set1 = new HashSet<>();
                    for (String yourInt : tableIDS) {
                        if (!set1.add(yourInt)) {
                            setToReturn.add(yourInt);
                        }
                        count++;
                        Main.log.info("Count: " + count);
                    }
                    test.set("keepList", setToReturn);
                    test.save(Api.getConfigFile("deleteList"));
                }
                if (channel.getId().equals(lmp.Constants.PIXELMON_CHANNEL_ID) && message.contains("!startPixelmon")) {
                    ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "startPixelmon.bat");
                    File dir = new File("E:\\Pixelmon");
                    pb.directory(dir);
                    pb.start();
                }
                // Sends unban request to member
                if (channel.getId().equals(lmp.Constants.UNBAN_REQUEST_CHANNEL_ID) && message.equalsIgnoreCase(lmp.Constants.UNBAN_REQUEST)) {
                    channel.deleteMessageById(messageId).queue();
                    event.getAuthor().openPrivateChannel().flatMap(privateChannel -> {
                        TextChannel unbanRequestSubmittedChannel = jda.getTextChannelById(lmp.Constants.UNBAN_REQUEST_COMPLETE_CHANNEL_ID);
                        event.getJDA().addEventListener(new UnbanRequest(privateChannel, event.getAuthor(), unbanRequestSubmittedChannel));
                        return privateChannel.sendMessage("Please enter your unban form line by line. \n Press enter after each question response. \n 1.) What is your Minecraft username?");
                    }).queue();
                }
                if (lmp.Constants.SEARCH_CHANNEL_ID.equalsIgnoreCase(channel.getId())) {
                    File configFile = new File(Main.getPlugin(Main.class).getDataFolder(), "playerShops.yml");
                    FileConfiguration configCfg = YamlConfiguration.loadConfiguration(configFile);
                    if (message.toLowerCase().contains("searchall")) {
                        String playerName = "";
                        int count = 0;
                        for (String player : configCfg.getKeys(false)) {
                            int totalAmount = 0;
                            if (configCfg.isSet(player + ".slots")) {
                                for (String slot : configCfg.getConfigurationSection(player + ".slots").getKeys(false)) {
                                    ItemStack is = configCfg.getItemStack(player + lmp.Constants.YML_SLOTS + slot);
                                    assert is != null;
                                    totalAmount += is.getAmount();
                                    String iso = new ItemStack(is.getType(), 1).toString();
                                    String itemString = count + " has " + is.getAmount() + " " + is.getType() + " in their shop for $" + configCfg.getDouble(player + ".itemworth." + iso) + " per " + is.getType();
                                    event.getAuthor().openPrivateChannel().flatMap(dm -> dm.sendMessage(itemString)).queue();
                                }
                            }
                            count++;
                        }
                    }
                }
                // Searches the player shops and returns if items are in a player's shop
                if (lmp.Constants.SEARCH_CHANNEL_ID.equalsIgnoreCase(channel.getId())) {
                    File configFile = new File(Main.getPlugin(Main.class).getDataFolder(), "playerShops.yml");
                    FileConfiguration configCfg = YamlConfiguration.loadConfiguration(configFile);
                    if (message.toLowerCase().contains(lmp.Constants.SEARCH_PLAYER_SHOP_COMMAND)) {
                        String[] arr = message.split(lmp.Constants.SEARCH_PLAYER_SHOP_COMMAND);
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
                                                channel.sendMessage(Bukkit.getOfflinePlayer(UUID.fromString(player)).getName() + " has " + totalAmount + " " + itemToSearch + "(s) in their shop for $" + configCfg.getDouble(player + ".itemWorth." + is) + " per item.").queue();
                                            } else {
                                                channel.sendMessage(Bukkit.getOfflinePlayer(UUID.fromString(player)).getName() + " has " + totalAmount + " " + itemToSearch + "(s) in their shop.").queue();
                                            }
                                        } else {
                                            channel.sendMessage(Bukkit.getOfflinePlayer(UUID.fromString(player)).getName() + " has " + totalAmount + " " + itemToSearch + "(s) in their shop.").queue();
                                        }
                                    }

                                }
                            }
                        }
                        if (Boolean.FALSE.equals(isItemAvailable)) {
                            channel.sendMessage("No one has " + itemToSearch + "s in their shop").queue();
                        }
                    }
                }
            } catch (NullPointerException | IOException e) {
                e.printStackTrace();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (channel.getId().equalsIgnoreCase(lmp.Constants.DISCORD_STAFF_CHAT_CHANNEL_ID) && !event.getAuthor().getId().equalsIgnoreCase(lmp.Constants.LATCH93BOT_USER_ID)) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("group.jr-mod")) {
                    if (event.getMessage().getReferencedMessage() != null) {
                        player.sendMessage("[" + ChatColor.LIGHT_PURPLE + "Mod-Chat" + ChatColor.WHITE + "]-" + convertDiscordMessageToServer(messageSender, message, senderName, true, event.getMessage().getReferencedMessage()));
                    } else {
                        player.sendMessage("[" + ChatColor.LIGHT_PURPLE + "Mod-Chat" + ChatColor.WHITE + "]-" + convertDiscordMessageToServer(messageSender, message, senderName, false, null));
                    }
                }
            }
        }
        if (channel.getId().equalsIgnoreCase(lmp.Constants.ADMIN_CHANNEL_ID) && !event.getAuthor().getId().equalsIgnoreCase(lmp.Constants.LATCH93BOT_USER_ID)) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("group.admin")) {
                    if (event.getMessage().getReferencedMessage() != null) {
                        player.sendMessage("[" + ChatColor.DARK_PURPLE + "Admin-Chat" + ChatColor.WHITE + "]-" + convertDiscordMessageToServer(messageSender, message, senderName, true, event.getMessage().getReferencedMessage()));
                    } else {
                        player.sendMessage("[" + ChatColor.DARK_PURPLE + "Admin-Chat" + ChatColor.WHITE + "]-" + convertDiscordMessageToServer(messageSender, message, senderName, false, null));
                    }
                }
            }
        }
        if (channel.getId().equalsIgnoreCase(lmp.Constants.TEST_CHANNEL_ID) && message.equalsIgnoreCase("plop")) {
            try {
                Api.setIsPlayerInDiscord();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (message.toLowerCase().contains("<@latch>")) {
            FileConfiguration configCfg = Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME);
            if (Boolean.TRUE.equals(configCfg.getBoolean("isLatchAFK"))) {
                org.joda.time.LocalDateTime currentLocalDateTime = new org.joda.time.LocalDateTime();
                String endTimeString = configCfg.getString("returnTime");
                //currentLocalDateTime, LocalDateTime.parse(endTimeString), PeriodType.yearMonthDayTime()
                org.joda.time.Period p = new org.joda.time.Period(currentLocalDateTime, org.joda.time.LocalDateTime.parse(endTimeString), PeriodType.yearMonthDayTime());
                int days = p.getDays();
                int hours = p.getHours();
                int minutes = p.getMinutes();
                int hoursOfTheDay = hours % 24;
                int minutesOfTheHour = minutes % 60;
                channel.sendMessage(configCfg.getString("afkMessage") + " He will return in " + days + " days | " + hoursOfTheDay + " hours | " + minutesOfTheHour + " minutes").queue();
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
