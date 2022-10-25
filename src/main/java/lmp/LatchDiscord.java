package lmp;


import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
import java.util.concurrent.TimeUnit;

public class LatchDiscord extends ListenerAdapter implements Listener {


    public static final Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME);
    public static String username = "";
    public static String userId = "";
    private long channelId = 0;
    private long authorId = 0;
    public static final JDABuilder jdaBuilder = JDABuilder.createDefault(Constants.DISCORD_BOT_TOKEN).setChunkingFilter(ChunkingFilter.ALL) // enable member chunking for all guilds
            .setMemberCachePolicy(MemberCachePolicy.ALL) // ignored if chunking enabled
            .enableIntents(GatewayIntent.GUILD_MEMBERS)
            .enableIntents(GatewayIntent.MESSAGE_CONTENT);
    public static JDA jda = null;

    static {
        jda = jdaBuilder.build();
    }

    public LatchDiscord() throws LoginException {
        startBot();
        jda.addEventListener(this);
    }


    private void startBot() {
        jda = jdaBuilder.build();
    }

    public static void stopBot() {
        jda.shutdown();
    }

    public static JDA getJDA(){
        return jda;
    }

    public void feedback(MessageChannel channel, User author) {
        this.channelId = channel.getIdLong();
        this.authorId = author.getIdLong();
    }

    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent e) {
        if (e.getRoles().get(0).getId().equalsIgnoreCase(Constants.ADMIN_ROLE_ID)){
            Api.addPlayerToPermissionGroup(Api.getMinecraftIdFromDCid(e.getUser().getId()), "admin");
        } else if (e.getRoles().get(0).getId().equalsIgnoreCase(Constants.MOD_ROLE_ID)){
            Api.addPlayerToPermissionGroup(Api.getMinecraftIdFromDCid(e.getUser().getId()), "mod");
        } else if (e.getRoles().get(0).getId().equalsIgnoreCase(Constants.JR_MOD_ROLE_ID)){
            Api.addPlayerToPermissionGroup(Api.getMinecraftIdFromDCid(e.getUser().getId()), "jr-mod");
        } else if (e.getRoles().get(0).getId().equalsIgnoreCase(Constants.HELPER_ROLE_ID)){
            Api.addPlayerToPermissionGroup(Api.getMinecraftIdFromDCid(e.getUser().getId()), "helper");
        } else if (e.getRoles().get(0).getId().equalsIgnoreCase(Constants.BUILDER_ROLE_ID)){
            Api.addPlayerToPermissionGroup(Api.getMinecraftIdFromDCid(e.getUser().getId()), "builder");
        } else if (e.getRoles().get(0).getId().equalsIgnoreCase(Constants.HARDCORE_ROLE_ID)){
            Api.addPlayerToPermissionGroup(Api.getMinecraftIdFromDCid(e.getUser().getId()), "hardcore");
        } else if (e.getRoles().get(0).getId().equalsIgnoreCase(Constants.DONOR_ROLE_ID)){
            Api.addPlayerToPermissionGroup(Api.getMinecraftIdFromDCid(e.getUser().getId()), "donor");
        } else if (e.getRoles().get(0).getId().equalsIgnoreCase(Constants.DONOR_PLUS_ROLE_ID)){
            Api.addPlayerToPermissionGroup(Api.getMinecraftIdFromDCid(e.getUser().getId()), "donor+");
        } else if (e.getRoles().get(0).getId().equalsIgnoreCase(Constants.DONOR_PLUS_PLUS_ROLE_ID)){
            Api.addPlayerToPermissionGroup(Api.getMinecraftIdFromDCid(e.getUser().getId()), "donor++");
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        event.getUser().openPrivateChannel().flatMap(dm -> dm.sendMessage("Welcome to LMP (Latch Multiplayer)\n" +
                "Users on PS4|XBox|Switch|Mobile are able to join\n" +
                "Type in the General Channel to get the member role - Just say Hi or introduce yourself :)\n" +
                "Doing this assigns you the member role and access to all of the channels.\n" +
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
        TextChannel generalChannel = jda.getTextChannelById(setTestingChannel(Constants.GENERAL_CHANNEL_ID));
        assert generalChannel != null;
        generalChannel.sendMessage("Welcome <@" + event.getUser().getId() + "> Glad to have you <:LatchPOG:957363669388386404>").queue();
    }

    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent e) {
        if (e.getRoles().get(0).getId().equalsIgnoreCase(Constants.ADMIN_ROLE_ID)){
            Api.removePlayerFromPermissionGroup(Api.getMinecraftIdFromDCid(e.getUser().getId()), "admin");
        } else if (e.getRoles().get(0).getId().equalsIgnoreCase(Constants.MOD_ROLE_ID)){
            Api.removePlayerFromPermissionGroup(Api.getMinecraftIdFromDCid(e.getUser().getId()), "mod");
        } else if (e.getRoles().get(0).getId().equalsIgnoreCase(Constants.JR_MOD_ROLE_ID)){
            Api.removePlayerFromPermissionGroup(Api.getMinecraftIdFromDCid(e.getUser().getId()), "jr-mod");
        } else if (e.getRoles().get(0).getId().equalsIgnoreCase(Constants.HELPER_ROLE_ID)){
            Api.removePlayerFromPermissionGroup(Api.getMinecraftIdFromDCid(e.getUser().getId()), "helper");
        } else if (e.getRoles().get(0).getId().equalsIgnoreCase(Constants.BUILDER_ROLE_ID)){
            Api.removePlayerFromPermissionGroup(Api.getMinecraftIdFromDCid(e.getUser().getId()), "builder");
        } else if (e.getRoles().get(0).getId().equalsIgnoreCase(Constants.HARDCORE_ROLE_ID)){
            Api.removePlayerFromPermissionGroup(Api.getMinecraftIdFromDCid(e.getUser().getId()), "hardcore");
        } else if (e.getRoles().get(0).getId().equalsIgnoreCase(Constants.DONOR_ROLE_ID)){
            Api.removePlayerFromPermissionGroup(Api.getMinecraftIdFromDCid(e.getUser().getId()), "donor");
        } else if (e.getRoles().get(0).getId().equalsIgnoreCase(Constants.DONOR_PLUS_ROLE_ID)){
            Api.removePlayerFromPermissionGroup(Api.getMinecraftIdFromDCid(e.getUser().getId()), "donor+");
        } else if (e.getRoles().get(0).getId().equalsIgnoreCase(Constants.DONOR_PLUS_PLUS_ROLE_ID)){
            Api.removePlayerFromPermissionGroup(Api.getMinecraftIdFromDCid(e.getUser().getId()), "donor++");
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();
        if (event.getMember() != null) {
            username = Objects.requireNonNull(event.getMember().getUser().getName());
            userId = Objects.requireNonNull(event.getMember().getId());
        }
        Mentions mentionedChannelsList;
        mentionedChannelsList = event.getMessage().getMentions();
        List<User> mentionedUsersList;
        mentionedUsersList = event.getMessage().getMentions().getUsers();
        String messageId = event.getMessageId();
        String message = event.getMessage().getContentRaw();
        String senderName = event.getAuthor().getName();
        Member messageSender = event.getMember();
        mentionedChannelsList.getChannels();
        for (GuildChannel guildChannel : mentionedChannelsList.getChannels()){
            message = message.replace(guildChannel.getId(), guildChannel.getName());
        }
        for (User user : mentionedUsersList){
            message = message.replace(user.getId(), user.getName());
        }
        if (Boolean.FALSE.equals(Main.getIsParameterInTesting("global"))){
            try {
                // If a user says the n word, then ban them
                if (message.toLowerCase().replace(" ", "").contains("nigger") || message.toLowerCase().replace(" ", "").contains("nigga")){
                    event.getMessage().delete().queue();
                    event.getMember().ban(30, TimeUnit.DAYS).queue();
                }
                // Question mark
//                if (userId.equals(Constants.SERVER_OWNER_ID) && message.equalsIgnoreCase(Constants.CLEAR_COMMAND)) {
//                    clearMessages(channel, messageId);
//                }
//                // Clears all messages in the channel
//                if (userId.equals(Constants.SERVER_OWNER_ID) && message.equalsIgnoreCase(Constants.CLEAR_ALL_COMMAND)) {
//                    clearAllMessages(channel, messageId);
//                }
                if (message.equalsIgnoreCase("!discordID")) {
                    channel.sendMessage("Hi " + senderName + "! Your Discord UserID is " + messageSender.getUser().getId()).queue();
                }
                // Gets online players
                if (channel.getId().equals(Constants.MINECRAFT_CHAT_CHANNEL_ID) && message.equalsIgnoreCase(Constants.ONLINE_COMMAND)){
                    ArrayList<String> onlinePlayers = new ArrayList<>();
                    EmbedBuilder eb = new EmbedBuilder();
                    TextChannel minecraftChannel = jda.getTextChannelById(setTestingChannel(Constants.MINECRAFT_CHAT_CHANNEL_ID));
                    assert minecraftChannel != null;
                    StringBuilder onlinePlayerMessage = new StringBuilder();
                    int count = 1;
                    for (Player player : Bukkit.getOnlinePlayers()){
                        File playerDataFile = new File("plugins/Essentials/userdata", player.getUniqueId() + ".yml");
                        FileConfiguration playerDataCfg = Api.getFileConfiguration(playerDataFile);
                        if (Boolean.FALSE.equals(Api.isPlayerInvisible(player.getUniqueId().toString()))){
                            String afk = "";
                            if (Boolean.TRUE.equals(playerDataCfg.getBoolean("afk"))){
                                afk = "(AFK)";
                            }
                            onlinePlayerMessage.append( Api.convertMinecraftMessageToDiscord(null, count + ".) " + player.getDisplayName()) + " " + afk + "\n");
                            onlinePlayers.add(player.getDisplayName());
                            count++;
                        }
                    }

                    if (!onlinePlayers.isEmpty()){
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
                if (channel.getId().equals(Constants.DISCORD_STAFF_CHAT_CHANNEL_ID) && message.equalsIgnoreCase(Constants.ONLINE_COMMAND)){
                    ArrayList<String> onlinePlayers = new ArrayList<>();
                    EmbedBuilder eb = new EmbedBuilder();
                    TextChannel minecraftChannel = jda.getTextChannelById(setTestingChannel(Constants.DISCORD_STAFF_CHAT_CHANNEL_ID));
                    assert minecraftChannel != null;
                    StringBuilder onlinePlayerMessage = new StringBuilder();
                    int count = 1;
                    for (Player player : Bukkit.getOnlinePlayers()){
                        File playerDataFile = new File("plugins/Essentials/userdata", player.getUniqueId() + ".yml");
                        FileConfiguration playerDataCfg = Api.getFileConfiguration(playerDataFile);
                        String afk = "";
                        if (Boolean.TRUE.equals(playerDataCfg.getBoolean("afk"))){
                            afk = "(AFK)";
                        }
                        if (Boolean.TRUE.equals(Api.isPlayerInvisible(player.getUniqueId().toString()))){
                            onlinePlayerMessage.append( Api.convertMinecraftMessageToDiscord(null, count + ".) " + player.getDisplayName()) + " (Invisible) " + afk + "\n");
                            onlinePlayers.add(player.getDisplayName());
                            count++;
                        } else {
                            onlinePlayerMessage.append( Api.convertMinecraftMessageToDiscord(null, count + ".) " + player.getDisplayName()) + " " + afk + "\n");
                            onlinePlayers.add(player.getDisplayName());
                            count++;
                        }
                    }

                    if (!onlinePlayers.isEmpty()){
                        eb.setColor(new Color(0xC6D13EFF, true));
                        eb.setTitle("Online Players: " + onlinePlayers.size());
                        eb.setDescription(onlinePlayerMessage.toString());
                    } else {
                        eb.setColor(new Color(0xC6D5042E, true));
                        eb.setTitle("No Players Online");
                    }
                    minecraftChannel.sendMessageEmbeds(eb.build()).queue();
                }
                // Deletes all messages from a specified user
                if (userId.equals(Constants.SERVER_OWNER_ID) && message.contains(Constants.CLEAR_ALL_USER_MESSAGES_COMMAND)) {
                    String[] arr = event.getMessage().getContentRaw().split(Constants.CLEAR_ALL_USER_MESSAGES_COMMAND);
                    String userID = arr[1].replace(" ", "");
                    clearAllUserMessages(channel, messageId, userID);
                }
                // List all blocks mined by a player
//                if (channel.getId().equals(Constants.DISCORD_STAFF_CHAT_CHANNEL_ID) && message.toLowerCase().contains("!searchblocks")) {
//                    String[] arr = event.getMessage().getContentRaw().split("!searchBlocks");
//                    String userID = Api.getMinecraftIdFromDCid(arr[1]);
//                    FileConfiguration
//                    clearAllUserMessages(channel, messageId, userID);
//                }
                // get the
                if ((channel.getId().equalsIgnoreCase(Constants.MINECRAFT_CHAT_CHANNEL_ID) && !event.getAuthor().getId().equals(Constants.LATCH93BOT_USER_ID))){
                    if (message.toLowerCase().contains("!searchdiscord")){
                        String[] messageArr = message.split(" ");
                        try{
                            String discordUserName = Api.getDiscordNameFromMCid(Api.getMinecraftIdFromMinecraftName(messageArr[1]));
                            channel.sendMessage(messageArr[1] + "'s Discord username is: " + discordUserName).queue();

                        } catch (IllegalArgumentException e){
                            channel.sendMessage("That player does not have a discord account linked to their minecraft account. Maybe try again.\nCommand usage: !searchDiscord [minecraftName]").queue();
                        }
                    } else if (message.toLowerCase().contains("!searchminecraft")){
                        String[] messageArr = message.split(" ");
                        try{
                            String minecraftUsername = Bukkit.getOfflinePlayer(UUID.fromString(Api.getMinecraftIdFromDCid(messageArr[1]))).getName();
                            channel.sendMessage(jda.getUserById(messageArr[1]).getName() + "'s Minecraft username is: " + minecraftUsername).queue();

                        } catch (IllegalArgumentException e){
                            channel.sendMessage("That player does not have a minecraft account linked to their discord account. Maybe try again.\nCommand usage: !searchMinecraft [discordID]").queue();
                        }
                    } else {
                        if (event.getMessage().getReferencedMessage() != null) {
                            Bukkit.broadcastMessage(convertDiscordMessageToServer(event, message, senderName, true, event.getMessage().getReferencedMessage()));
                        } else {
                            Bukkit.broadcastMessage(convertDiscordMessageToServer(event, message, senderName, false, null));
                        }
                    }
                }

                if (channel.getId().equals(Constants.TEST_CHANNEL_ID) && message.contains("cong")) {
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
                if (channel.getId().equals(Constants.TEST_CHANNEL_ID) && message.contains("pog")) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
                    //Here you say to java the initial timezone. This is the secret
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    //Will print in UTC
                    FileConfiguration whitelistCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_WHITELIST_FILE_NAME));
                    for(String user : whitelistCfg.getConfigurationSection("players").getKeys(false)) {
                        System.out.println(sdf.format(calendar.getTime()));
                        sdf.setTimeZone(TimeZone.getTimeZone(whitelistCfg.getString("players." + user + ".ip-info.timezoneName")));
                        System.out.println("Time for " + whitelistCfg.getString("players." + user + ".discordName") + " is -> " + sdf.format(calendar.getTime()));
                    }
                    //Here you set to your timezone
                    //Will print on your default Timezone
                }

                // Sends LTS Nomination Form to member
                if (channel.getId().equals(setTestingChannel(Constants.LTS_NOMINEE_CHANNEL_ID)) && message.equalsIgnoreCase(Constants.LTS_NOMINATION_COMMAND)) {
                    channel.deleteMessageById(messageId).queue();
                    if (message.equalsIgnoreCase(Constants.LTS_NOMINATION_COMMAND)){
                        event.getAuthor().openPrivateChannel().flatMap(privateChannel -> {
                            TextChannel applicationSubmittedChannel = jda.getTextChannelById(Constants.LTS_NOMINEE_CHANNEL_ID);
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
                if (channel.getId().equals(setTestingChannel(Constants.STAFF_APPLICATION_CHANNEL_ID)) && message.equalsIgnoreCase(Constants.STAFF_APPLY_COMMAND)) {
                    channel.deleteMessageById(messageId).queue();
                    event.getAuthor().openPrivateChannel().flatMap(privateChannel -> {
                        TextChannel applicationSubmittedChannel = jda.getTextChannelById(Constants.STAFF_APP_SUBMITTED_CHANNEL_ID);
                        event.getJDA().addEventListener(new StaffApplication(privateChannel, event.getAuthor(), applicationSubmittedChannel));
                        return privateChannel.sendMessage("\nResponsibilities as a Jr. Mod:\n*** Watch new players on the server while in vanish.\n*** Ensure new players are following the rules, i.e., not x-raying, griefing, stealing or being a jerk.\n" +
                                "Please enter your application information line by line.\nPress enter after each question response.\n" +
                                "1.) How old are you?");
                    }).queue();
                }
                ArrayList<String> issueCommandList = new ArrayList<>();
                issueCommandList.add("!submitissue");
                issueCommandList.add("!submitrequest");
                issueCommandList.add("!submitbug");
                issueCommandList.add("!submitsuggestion");
                if (channel.getId().equals(Constants.ISSUE_CHANNEL_ID) && issueCommandList.contains(message.toLowerCase())) {
                    String issueType = "Issue";
                    if (message.equalsIgnoreCase("!submitRequest")){
                        issueType = "Request";
                    } else if (message.equalsIgnoreCase("!submitBug")){
                        issueType = "Bug";
                    } else if (message.equalsIgnoreCase("!submitSuggestion")){
                        issueType = "Suggestion";
                    }
                    channel.deleteMessageById(messageId).queue();
                    channel.sendMessage(event.getAuthor().getName() + " --- Check your Discord DMs to complete your " + issueType + " submission").queue();
                    String finalIssueType = issueType;
                    event.getAuthor().openPrivateChannel().flatMap(privateChannel -> {
                        TextChannel issueCompleteChannel = jda.getTextChannelById(Constants.SUBMITTED_ISSUES_CHANNEL_ID);
                        event.getJDA().addEventListener(new IssueTracker(privateChannel, event.getAuthor(), issueCompleteChannel, finalIssueType));
                        return privateChannel.sendMessage("\nPLEASE READ\nThere will be 2 questions. Please put as much information in one message as you can.\n" +
                                "1.) Please answer why are here today in detail.");
                    }).queue();
                }
                if (channel.getId().equals(Constants.TEST_CHANNEL_ID) && message.equalsIgnoreCase("!mark")){
                    TextChannel byebyeDiscordChat = jda.getTextChannelById(setTestingChannel(Constants.TEST_CHANNEL_ID));
                    assert byebyeDiscordChat != null;
                    String filePath = Api.getMainPlugin().getDataFolder().getPath();
                    String folderName = "/gifs";


                    int gifCount = Objects.requireNonNull(new File(filePath + folderName).list()).length;
                    Random rand = new Random();
                    int n = rand.nextInt(gifCount);
                    byebyeDiscordChat.sendMessage(event.getMember().getUser().getName() + " left Discord. ").addFiles(FileUpload.fromData(new File(filePath+folderName + "/bye_bye" + n + ".gif" ))).queue();
                }
                if (channel.getId().equals(Constants.TEST_CHANNEL_ID) && message.equalsIgnoreCase("!gifs")){
                    ThreadChannel gifThread = jda.getThreadChannelById("1034135259060650075");
                    FileConfiguration gifCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_GIF_LIST_FILE));
                    List<String> giflinkList = new ArrayList<>();
                    List<Message> messageList;
                    assert gifThread != null;
                    messageList = gifThread.getIterableHistory().takeAsync(50) // Collect 1000 messages
                            .thenApply(ArrayList::new)
                            .get();
                    Collections.reverse(messageList);

                    for (Message gifMessage : messageList) {
                        if (gifMessage.getAttachments().isEmpty()){
                            giflinkList.add(gifMessage.getContentRaw());
                        } else {
                            System.out.println("attach: " + gifMessage.getAttachments().get(0).getUrl());
                            giflinkList.add(gifMessage.getAttachments().get(0).getUrl());
                        }
                    }
                    gifCfg.set("gifList", giflinkList);
                    gifCfg.save(Api.getConfigFile(Constants.YML_GIF_LIST_FILE));
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
                if (channel.getId().equals(Constants.TEST_CHANNEL_ID) && message.equalsIgnoreCase("!compare")){
                    List<String> keep = Files.readAllLines(Paths.get("C:/Users/Latch/Downloads/keep.txt"));
                    List<String> tableIDS = Files.readAllLines(Paths.get("C:/Users/Latch/Desktop/sqlt_data_1_2015_01.txt"));
                    List<String> delete = new ArrayList<>();
                    List<String> inTable = new ArrayList<>();
                    int count = 0;
                    FileConfiguration test = Api.getFileConfiguration(Api.getConfigFile("deleteList"));
                    final Set<String> setToReturn = new HashSet<>();
                    final Set<String> set1 = new HashSet<>();
                    for (String yourInt : tableIDS) {
                        if (!set1.add(yourInt)) {
                            setToReturn.add(yourInt);
                        }
                        count++;
                        System.out.println("Count: " + count);
                    }
                    test.set("keepList", setToReturn);
                    test.save(Api.getConfigFile("deleteList"));
                }
                if (channel.getId().equals(Constants.PIXELMON_CHANNEL_ID) && message.contains("!startPixelmon")) {
                    ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "startPixelmon.bat");
                    File dir = new File("E:\\Pixelmon");
                    pb.directory(dir);
                    pb.start();
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
                if (message.equalsIgnoreCase("!link")){
//                    if (!messageSender.getRoles().toString().contains("Member")){
//                        channel.sendMessage(username + " --- React to message containing the rules in the <#625996424554872842> channel to get the Member role and access to the server IP. Rerun the !link command once you've gotten the Member role.").queue();
//                    } else {
                    event.getAuthor().openPrivateChannel().flatMap(dm -> dm.sendMessage("IP = latch.ddns.net\n" +
                            "Java Port Number = 60\n" +
                            "Bedrock Port Number = 19132\n" +
                            "Run the following command in your minecraft client chat after you join:\n" +
                            "/lmp link " + event.getAuthor().getId())).queue();
                    channel.sendMessage(username + " --- Check your Discord for a private message from my bot containing your link command. <:LatchPOG:957363669388386404>").queue();
//                    }
                }
                if (Constants.SEARCH_CHANNEL_ID.equalsIgnoreCase(channel.getId())) {
                    File configFile = new File(Main.getPlugin(Main.class).getDataFolder(), "playerShops.yml");
                    FileConfiguration configCfg = YamlConfiguration.loadConfiguration(configFile);
                    if (message.toLowerCase().contains("searchall")) {
                        String playerName = "";
                        int count = 0;
                        for (String player : configCfg.getKeys(false)) {
                            int totalAmount = 0;
                            if (configCfg.isSet(player + ".slots")) {
                                for (String slot : configCfg.getConfigurationSection(player + ".slots").getKeys(false)) {
                                    ItemStack is = configCfg.getItemStack(player + Constants.YML_SLOTS + slot);
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
                if (Constants.SEARCH_CHANNEL_ID.equalsIgnoreCase(channel.getId())){
                    File configFile = new File(Main.getPlugin(Main.class).getDataFolder(), "playerShops.yml");
                    FileConfiguration configCfg = YamlConfiguration.loadConfiguration(configFile);
                    if (message.toLowerCase().contains(Constants.SEARCH_PLAYER_SHOP_COMMAND)) {
                        String[] arr = message.split(Constants.SEARCH_PLAYER_SHOP_COMMAND);
                        String itemToSearch = arr[1].replace(" ", "").toUpperCase();
                        String playerName = "";
                        boolean isItemAvailable = false;

                        for (String player : configCfg.getKeys(false)) {
                            int totalAmount = 0;
                            if (configCfg.isSet(player)) {
                                if (configCfg.isSet(player + ".slots")) {
                                    for (String slot : configCfg.getConfigurationSection(player + ".slots").getKeys(false)) {
                                        if (configCfg.getItemStack(player + Constants.YML_SLOTS + slot) != null) {
                                            ItemStack is = configCfg.getItemStack(player + Constants.YML_SLOTS + slot);
                                            if (itemToSearch.equalsIgnoreCase(is.getType().toString()) || itemToSearch.toLowerCase().contains("everything")) {
                                                totalAmount += is.getAmount();
                                            }
                                        }
                                    }
                                    if (totalAmount != 0) {
                                        isItemAvailable = true;
                                        if (!itemToSearch.equalsIgnoreCase("spawn_egg")){
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
                // Auto logs bans to #banned-logs if ban occurs in discord server console channel
                if (Constants.DISCORD_CONSOLE_CHANNEL_ID.equalsIgnoreCase(channel.getId())){
                    if (message.toLowerCase().contains("ban") || message.toLowerCase().contains("tempban")){
                        logPlayerBan(null, event.getMessage());
                    }
                }
            } catch (NullPointerException | IOException e){
                e.printStackTrace();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (channel.getId().equalsIgnoreCase(Constants.DISCORD_STAFF_CHAT_CHANNEL_ID) && !event.getAuthor().getId().equalsIgnoreCase(Constants.LATCH93BOT_USER_ID)){
            for (Player player : Bukkit.getOnlinePlayers()){
                if (player.hasPermission("group.jr-mod")){
                    if (event.getMessage().getReferencedMessage() != null){
                        player.sendMessage("[" + ChatColor.LIGHT_PURPLE + "Mod-Chat" + ChatColor.WHITE + "]-" + convertDiscordMessageToServer(event, message, senderName, true, event.getMessage().getReferencedMessage()));
                    } else {
                        player.sendMessage("[" + ChatColor.LIGHT_PURPLE + "Mod-Chat" + ChatColor.WHITE + "]-" + convertDiscordMessageToServer(event, message, senderName, false, null));
                    }
                }
            }
        }
        if (channel.getId().equalsIgnoreCase(Constants.ADMIN_CHANNEL_ID) && !event.getAuthor().getId().equalsIgnoreCase(Constants.LATCH93BOT_USER_ID)){
            for (Player player : Bukkit.getOnlinePlayers()){
                if (player.hasPermission("group.admin")){
                    if (event.getMessage().getReferencedMessage() != null){
                        player.sendMessage("[" + ChatColor.DARK_PURPLE + "Admin-Chat" + ChatColor.WHITE + "]-" + convertDiscordMessageToServer(event, message, senderName, true, event.getMessage().getReferencedMessage()));
                    } else {
                        player.sendMessage("[" + ChatColor.DARK_PURPLE + "Admin-Chat" + ChatColor.WHITE + "]-" + convertDiscordMessageToServer(event, message, senderName, false, null));
                    }
                }
            }
        }
        if (channel.getId().equalsIgnoreCase(Constants.GENERAL_CHANNEL_ID) && !messageSender.getRoles().toString().contains("Member")){
            channel.sendMessage("Type !link in this channel to get your link command and the Server IP dm'd to you by my bot.").queue();
            Objects.requireNonNull(jda.getGuildById(Constants.GUILD_ID)).addRoleToMember(UserSnowflake.fromId(messageSender.getUser().getId()), Objects.requireNonNull(jda.getRoleById(Constants.MEMBER_ROLE_ID))).queue();
        }
        if (channel.getId().equalsIgnoreCase(Constants.GENERAL_CHANNEL_ID) && message.equalsIgnoreCase("!joinTime")){
            channel.sendMessage(senderName + " joined on " + messageSender.getTimeJoined().toString().split("T")[0]).queue();
        }
        if (channel.getId().equalsIgnoreCase(Constants.TEST_CHANNEL_ID) && message.equalsIgnoreCase("plop")){
            try {
                Api.setIsPlayerInDiscord();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (channel.getId().equalsIgnoreCase(Constants.TEST_CHANNEL_ID) && message.toLowerCase().contains("!status")){
            String[] messageArr = message.split(" ");
            FileConfiguration configCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_CONFIG_FILE_NAME));
            configCfg.set("isLatchAFK", Boolean.parseBoolean(messageArr[1]));
            try {
                configCfg.save(Api.getConfigFile(Constants.YML_CONFIG_FILE_NAME));
                channel.sendMessage("AFK status has been set to " + messageArr[1]).queue();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (channel.getId().equalsIgnoreCase(Constants.TEST_CHANNEL_ID) && message.toLowerCase().contains("!message")){
            FileConfiguration configCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_CONFIG_FILE_NAME));
            configCfg.set("afkMessage", Arrays.toString(message.split(" ", 2)));
            try {
                configCfg.save(Api.getConfigFile(Constants.YML_CONFIG_FILE_NAME));
                channel.sendMessage("AFK message has been set.").queue();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (channel.getId().equalsIgnoreCase(Constants.TEST_CHANNEL_ID) && message.toLowerCase().contains("!returntime")){
            String[] messageArr = message.split(" ");
            FileConfiguration configCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_CONFIG_FILE_NAME));
            configCfg.set("returnTime", messageArr[1]);
            try {
                configCfg.save(Api.getConfigFile(Constants.YML_CONFIG_FILE_NAME));
                channel.sendMessage("Return time has been set.").queue();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (channel.getId().equalsIgnoreCase(Constants.TEST_CHANNEL_ID) && message.toLowerCase().contains("!setjoinmessage")){
            FileConfiguration configCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_CONFIG_FILE_NAME));
            configCfg.set("joinMessage", Arrays.toString(message.split(" ", 2)));
            try {
                configCfg.save(Api.getConfigFile(Constants.YML_CONFIG_FILE_NAME));
                channel.sendMessage("Join message has been updated.").queue();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (message.toLowerCase().contains("<@latch>")){
            FileConfiguration configCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_CONFIG_FILE_NAME));
            if (Boolean.TRUE.equals(configCfg.getBoolean("isLatchAFK"))){
                org.joda.time.LocalDateTime currentLocalDateTime = new org.joda.time.LocalDateTime();
                String endTimeString = configCfg.getString("returnTime");
                //currentLocalDateTime, LocalDateTime.parse(endTimeString), PeriodType.yearMonthDayTime()
                org.joda.time.Period p = new org.joda.time.Period(currentLocalDateTime,org.joda.time.LocalDateTime.parse(endTimeString), PeriodType.yearMonthDayTime());
                int days = p.getDays();
                int hours = p.getHours();
                int minutes = p.getMinutes();
                int hoursOfTheDay = hours % 24;
                int minutesOfTheHour = minutes % 60;
                channel.sendMessage(configCfg.getString("afkMessage") +" He will return in " + days + " days | " + hoursOfTheDay + " hours | " + minutesOfTheHour + " minutes").queue();
            }
        }

    }

    public static String convertDiscordMessageToServer(MessageReceivedEvent event, String message, String senderName, Boolean isReply, Message repliedMessage) {
        int count = 0;
        String highestRole = "Member";
        ChatColor colorCode;
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
        String finalMessage = "";
        if (!event.getAuthor().getId().equalsIgnoreCase(Constants.PIXELMON_BOT_ID)){
            if (Boolean.TRUE.equals(isReply)){
                finalMessage = ChatColor.WHITE + "[" + ChatColor.AQUA + "Discord" + ChatColor.WHITE + " | " + colorCode + highestRole + ChatColor.WHITE + "] "  + senderName + ChatColor.GRAY + " » " + ChatColor.WHITE + "Replied to " + ChatColor.GOLD + repliedMessage.getAuthor().getName() +
                        ChatColor.GRAY + " » " + ChatColor.GREEN + "'" + repliedMessage.getContentRaw() + "'" + ChatColor.GRAY + " » " + ChatColor.WHITE + message;
            } else {
                finalMessage = ChatColor.WHITE + "[" + ChatColor.AQUA + "Discord" + ChatColor.WHITE + " | " + colorCode + highestRole + ChatColor.WHITE + "] "  + senderName + " » " + message;
            }
        } else {
            message = message.replace("[Pixelmon] - ", "");
            ArrayList<String> systemMessages = new ArrayList<>();
            systemMessages.add("left pixelmon server");
            systemMessages.add("pixelmon server stopped!");
            systemMessages.add("pixelmon server starting...");
            systemMessages.add("joined pixelmon server");
            systemMessages.add("[death] - ");
            systemMessages.add("[advancement] - ");
            Boolean isSystemMessage = false;
            for (String systemMessage : systemMessages){
                if (message.toLowerCase().contains(systemMessage)){
                    isSystemMessage = true;
                }
            }
            if (Boolean.TRUE.equals(isSystemMessage)){
                finalMessage = ChatColor.WHITE + "[" + ChatColor.AQUA + "Pixelmon" + ChatColor.WHITE + "]"  + ChatColor.GRAY + " » " + ChatColor.WHITE + message;
            } else {
                String[] messageArr = message.split(" » ");
                senderName = messageArr[0];
                StringBuilder messageString = new StringBuilder();
                for (int i = 1; i < messageArr.length; i++){
                    messageString.append(messageArr[i]);
                }
                if (messageArr.length == 1){
                    finalMessage = ChatColor.WHITE + "[" + ChatColor.AQUA + "Pixelmon" + ChatColor.WHITE + "] "  + ChatColor.GREEN + senderName + ChatColor.GRAY + " » " + ChatColor.WHITE + message;
                } else {
                    finalMessage = ChatColor.WHITE + "[" + ChatColor.AQUA + "Pixelmon" + ChatColor.WHITE + "] "  + ChatColor.GREEN + senderName + ChatColor.GRAY + " » " + ChatColor.WHITE + messageString.toString();
                }
            }
        }

        return finalMessage;
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        Api.messageInConsole(ChatColor.GOLD + Objects.requireNonNull(event.getMember()).getUser().getName() + ChatColor.RED + " left the discord.");
        TextChannel byebyeDiscordChat = jda.getTextChannelById(setTestingChannel(Constants.BYE_BYE_DISCORD_CHANNEL_ID));
        assert byebyeDiscordChat != null;
        String filePath = Api.getMainPlugin().getDataFolder().getPath();
        String folderName = "/gifs";
        int gifCount = Objects.requireNonNull(new File(filePath + folderName).list()).length;
        Random rand = new Random();
        int n = rand.nextInt(gifCount);
        byebyeDiscordChat.sendMessage(event.getMember().getUser().getName() + " left Discord. ").addFiles(FileUpload.fromData(new File(filePath+folderName + "/bye_bye" + n + ".gif" ))).queue();
        String discordUserId = event.getMember().getUser().getId();
        String minecraftId = Api.getMinecraftIdFromDCid(discordUserId);
        FileConfiguration whitelistCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_WHITELIST_FILE_NAME));
        if (!minecraftId.isEmpty() && whitelistCfg.isSet(Constants.YML_PLAYERS + minecraftId + ".isPlayerInDiscord")){
            whitelistCfg.set(Constants.YML_PLAYERS + minecraftId + ".isPlayerInDiscord", false);
            try {
                whitelistCfg.save(Api.getConfigFile(Constants.YML_WHITELIST_FILE_NAME));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event){
        MessageChannel channel = event.getChannel();
        String userID = event.getUserId();
        if (channel.getId().equalsIgnoreCase(Constants.RULES_CHANNEL_ID) && event.getMessageId().equalsIgnoreCase(Constants.RULES_CHANNEL_MESSAGE_ID)){
            event.getGuild().addRoleToMember(UserSnowflake.fromId(userID), Objects.requireNonNull(jda.getRoleById(Constants.MEMBER_ROLE_ID))).queue();
        }
        if (channel.getId().equalsIgnoreCase(Constants.GET_ROLE_CHANNEL_ID)){
            // If Xbox
            if (event.getMessageId().equalsIgnoreCase(Constants.XBOX_MESSAGE_ID)){
                event.getGuild().addRoleToMember(UserSnowflake.fromId(userID), Objects.requireNonNull(jda.getRoleById(Constants.XBOX_ROLE_ID))).queue();
            }
            // If Playstation
            if (event.getMessageId().equalsIgnoreCase(Constants.PLAYSTATION_MESSAGE_ID)){
                event.getGuild().addRoleToMember(UserSnowflake.fromId(userID), Objects.requireNonNull(jda.getRoleById(Constants.PLAYSTATION_ROLE_ID))).queue();
            }
            // If Mobile
            if (event.getMessageId().equalsIgnoreCase(Constants.MOBILE_MESSAGE_ID)){
                event.getGuild().addRoleToMember(UserSnowflake.fromId(userID), Objects.requireNonNull(jda.getRoleById(Constants.MOBILE_ROLE_ID))).queue();
            }
            // If Java
            if (event.getMessageId().equalsIgnoreCase(Constants.JAVA_MESSAGE_ID)){
                event.getGuild().addRoleToMember(UserSnowflake.fromId(userID), Objects.requireNonNull(jda.getRoleById(Constants.JAVA_ROLE_ID))).queue();
            }
            // If He/Him
            if (event.getMessageId().equalsIgnoreCase(Constants.HE_HIM_MESSAGE_ID)){
                event.getGuild().addRoleToMember(UserSnowflake.fromId(userID), Objects.requireNonNull(jda.getRoleById(Constants.HE_HIM_ROLE_ID))).queue();
            }
            // If She/Her
            if (event.getMessageId().equalsIgnoreCase(Constants.SHE_HER_MESSAGE_ID)){
                event.getGuild().addRoleToMember(UserSnowflake.fromId(userID), Objects.requireNonNull(jda.getRoleById(Constants.SHE_HER_ROLE_ID))).queue();
            }
            // If He/Him
            if (event.getMessageId().equalsIgnoreCase(Constants.THEY_THEM_MESSAGE_ID)){
                event.getGuild().addRoleToMember(UserSnowflake.fromId(userID), Objects.requireNonNull(jda.getRoleById(Constants.THEY_THEM_ROLE_ID))).queue();
            }
            // If He/Him
            if (event.getMessageId().equalsIgnoreCase(Constants.HE_THEM_MESSAGE_ID)){
                event.getGuild().addRoleToMember(UserSnowflake.fromId(userID), Objects.requireNonNull(jda.getRoleById(Constants.HE_THEM_ROLE_ID))).queue();
            }
            // If He/Him
            if (event.getMessageId().equalsIgnoreCase(Constants.SHE_THEM_MESSAGE_ID)){
                event.getGuild().addRoleToMember(UserSnowflake.fromId(userID), Objects.requireNonNull(jda.getRoleById(Constants.SHE_THEM_ROLE_ID))).queue();
            }
            // If He/Him
            if (event.getMessageId().equalsIgnoreCase(Constants.ZE_HIR_MESSAGE_ID)){
                event.getGuild().addRoleToMember(UserSnowflake.fromId(userID), Objects.requireNonNull(jda.getRoleById(Constants.ZE_HIR_ROLE_ID))).queue();
            }
            if (event.getMessageId().equalsIgnoreCase(Constants.MEMBER_PLUS_MESSAGE_ID)){
                event.getGuild().addRoleToMember(UserSnowflake.fromId(userID), Objects.requireNonNull(jda.getRoleById(Constants.MEMBER_PLUS_ROLE_ID))).queue();
            }

        }
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event){
        MessageChannel channel = event.getChannel();
        String userID = event.getUserId();
        if (channel.getId().equalsIgnoreCase(Constants.RULES_CHANNEL_ID) && event.getMessageId().equalsIgnoreCase(Constants.RULES_CHANNEL_MESSAGE_ID)){
            event.getGuild().removeRoleFromMember(UserSnowflake.fromId(userID), Objects.requireNonNull(jda.getRoleById(Constants.MEMBER_ROLE_ID))).queue();
        }
        if (channel.getId().equalsIgnoreCase(Constants.GET_ROLE_CHANNEL_ID)){
            // If Xbox
            if (event.getMessageId().equalsIgnoreCase(Constants.XBOX_MESSAGE_ID)){
                event.getGuild().removeRoleFromMember(UserSnowflake.fromId(userID), Objects.requireNonNull(jda.getRoleById(Constants.XBOX_ROLE_ID))).queue();
            }
            // If Playstation
            if (event.getMessageId().equalsIgnoreCase(Constants.PLAYSTATION_MESSAGE_ID)){
                event.getGuild().removeRoleFromMember(UserSnowflake.fromId(userID), Objects.requireNonNull(jda.getRoleById(Constants.PLAYSTATION_ROLE_ID))).queue();
            }
            // If Mobile
            if (event.getMessageId().equalsIgnoreCase(Constants.MOBILE_MESSAGE_ID)){
                event.getGuild().removeRoleFromMember(UserSnowflake.fromId(userID), Objects.requireNonNull(jda.getRoleById(Constants.MOBILE_ROLE_ID))).queue();
            }
            // If Java
            if (event.getMessageId().equalsIgnoreCase(Constants.JAVA_MESSAGE_ID)){
                event.getGuild().removeRoleFromMember(UserSnowflake.fromId(userID), Objects.requireNonNull(jda.getRoleById(Constants.JAVA_ROLE_ID))).queue();
            }
            // If He/Him
            if (event.getMessageId().equalsIgnoreCase(Constants.HE_HIM_MESSAGE_ID)){
                event.getGuild().removeRoleFromMember(UserSnowflake.fromId(userID), Objects.requireNonNull(jda.getRoleById(Constants.HE_HIM_ROLE_ID))).queue();
            }
            // If She/Her
            if (event.getMessageId().equalsIgnoreCase(Constants.SHE_HER_MESSAGE_ID)){
                event.getGuild().removeRoleFromMember(UserSnowflake.fromId(userID), Objects.requireNonNull(jda.getRoleById(Constants.SHE_HER_ROLE_ID))).queue();
            }
            // If He/Him
            if (event.getMessageId().equalsIgnoreCase(Constants.THEY_THEM_MESSAGE_ID)){
                event.getGuild().removeRoleFromMember(UserSnowflake.fromId(userID), Objects.requireNonNull(jda.getRoleById(Constants.THEY_THEM_ROLE_ID))).queue();
            }
            // If He/Him
            if (event.getMessageId().equalsIgnoreCase(Constants.HE_THEM_MESSAGE_ID)){
                event.getGuild().removeRoleFromMember(UserSnowflake.fromId(userID), Objects.requireNonNull(jda.getRoleById(Constants.HE_THEM_ROLE_ID))).queue();
            }
            // If He/Him
            if (event.getMessageId().equalsIgnoreCase(Constants.SHE_THEM_MESSAGE_ID)){
                event.getGuild().removeRoleFromMember(UserSnowflake.fromId(userID), Objects.requireNonNull(jda.getRoleById(Constants.SHE_THEM_ROLE_ID))).queue();
            }
            // If He/Him
            if (event.getMessageId().equalsIgnoreCase(Constants.ZE_HIR_MESSAGE_ID)){
                event.getGuild().removeRoleFromMember(UserSnowflake.fromId(userID), Objects.requireNonNull(jda.getRoleById(Constants.ZE_HIR_ROLE_ID))).queue();
            }
            if (event.getMessageId().equalsIgnoreCase(Constants.MEMBER_PLUS_MESSAGE_ID)){
                event.getGuild().removeRoleFromMember(UserSnowflake.fromId(userID), Objects.requireNonNull(jda.getRoleById(Constants.MEMBER_PLUS_ROLE_ID))).queue();
            }

        }
    }

    public static void whitelistToggle(String message, MessageChannel channel){
        try {

            Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist " + message.toLowerCase()));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (message.toLowerCase().contains("on")){
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

    public static String setTestingChannel(String channelID){
        String channelId = channelID;
        if (Boolean.TRUE.equals(Main.getIsParameterInTesting("global"))){
            channelId = Constants.TEST_CHANNEL_ID;
        } return channelId;
    }

    @Override
    public void onReady(ReadyEvent event) {
        if (Boolean.FALSE.equals(Main.getIsParameterInTesting("onStart"))) {
            FileConfiguration configCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_CONFIG_FILE_NAME));
            DateTime dt = new DateTime();
            DateTimeFormatter fmt = DateTimeFormat.forPattern("MM-dd-yyyy HH:mm:ss");
            String dtStr = fmt.print(dt);
            configCfg.set("serverStartTime", dtStr);
            try {
                configCfg.save(Api.getConfigFile(Constants.YML_CONFIG_FILE_NAME));
            } catch (IOException e) {
                e.printStackTrace();
            }
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("LMP Server has started", null);
            eb.setColor(new Color(0xE10233E5, true));
            eb.setThumbnail("https://raw.githubusercontent.com/Latch93/DiscordText/master/src/main/resources/lmp_discord_image.png");
            TextChannel minecraftChannel = jda.getTextChannelById(setTestingChannel(Constants.MINECRAFT_CHAT_CHANNEL_ID));
            assert minecraftChannel != null;
            minecraftChannel.sendMessageEmbeds(eb.build()).queue();
        }
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
        try {
            FileConfiguration whitelistCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_WHITELIST_FILE_NAME));
            String discordUserName = Objects.requireNonNull(Objects.requireNonNull(jda.getGuildById(Constants.GUILD_ID)).getMemberById(Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(whitelistCfg.getString(Constants.YML_PLAYERS + onPlayerJoinEvent.getPlayer().getUniqueId() + ".discordId")))))).getUser().getName();
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
                TextChannel modChannel = jda.getTextChannelById(Constants.DISCORD_STAFF_CHAT_CHANNEL_ID);
                assert modChannel != null;
                modChannel.sendMessage(":white_check_mark: » " + discordUserName + " joined the server.").queue();
            }
        } catch (NullPointerException e){
            TextChannel minecraftChannel = jda.getTextChannelById(setTestingChannel(Constants.MINECRAFT_CHAT_CHANNEL_ID));
            assert minecraftChannel != null;
            try {
                minecraftChannel.sendMessage(":white_check_mark: » " + onPlayerJoinEvent.getPlayer().getName() + " joined the server.").queue();
            } catch (NullPointerException ignored){

            }

        }
    }

    public static void sendPlayerLogoutMessage(PlayerQuitEvent onPlayerQuitEvent) {
        try {
            EmbedBuilder eb = new EmbedBuilder();
            FileConfiguration whitelistCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_WHITELIST_FILE_NAME));
            String discordUserName = Objects.requireNonNull(Objects.requireNonNull(jda.getGuildById(Constants.GUILD_ID)).getMemberById(Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(whitelistCfg.getString(Constants.YML_PLAYERS + onPlayerQuitEvent.getPlayer().getUniqueId() + ".discordId")))))).getUser().getName();
            if (!onPlayerQuitEvent.getPlayer().hasPermission("dt.leaveVanish")){
                eb.setThumbnail("https://minotar.net/avatar/" + onPlayerQuitEvent.getPlayer().getName()+ ".png?size=50");
                eb.setTitle(Constants.DISCORD_USERNAME_LABEL + discordUserName + Constants.MINECRAFT_USERNAME_LABEL  + onPlayerQuitEvent.getPlayer().getName() +" \nDisconnected from the server", null);
                eb.setColor(new Color(0xD0FF3F3F, true));
                TextChannel minecraftChannel = jda.getTextChannelById(setTestingChannel(Constants.MINECRAFT_CHAT_CHANNEL_ID));
                assert minecraftChannel != null;
                minecraftChannel.sendMessageEmbeds(eb.build()).queue();
            } else {
                TextChannel modChannel = jda.getTextChannelById(Constants.DISCORD_STAFF_CHAT_CHANNEL_ID);
                assert modChannel != null;
                modChannel.sendMessage(":x: » " + discordUserName + " left the server.").queue();
            }
        } catch (NullPointerException e){
            TextChannel minecraftChannel = jda.getTextChannelById(setTestingChannel(Constants.MINECRAFT_CHAT_CHANNEL_ID));
            assert minecraftChannel != null;
            minecraftChannel.sendMessage(":x: » " + onPlayerQuitEvent.getPlayer().getName() + " left the server.").queue();
        }
    }

    public static void setDiscordId() throws IOException {
        FileConfiguration whitelistCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_WHITELIST_FILE_NAME));
        List<Member> members = Objects.requireNonNull(jda.getGuildById(Constants.GUILD_ID)).getMembers();
        for (String playerName : whitelistCfg.getConfigurationSection("players").getKeys(false)) {
            for (Member member : members){
                if (member.getUser().getName().equalsIgnoreCase(whitelistCfg.getString("players." + playerName + ".discordName"))){
                    whitelistCfg.set("players." + playerName + ".discordId", member.getId());
                    whitelistCfg.set("players." + playerName + ".joinedTime", null);
                    whitelistCfg.set("players." + playerName + ".joinTime", member.getTimeJoined().toLocalDateTime().toString());
                }
            }
            if (!whitelistCfg.isSet("players." + playerName + ".discordId")){
                whitelistCfg.set("players." + playerName, null);
            }
        }
        whitelistCfg.save(Api.getConfigFile(Constants.YML_WHITELIST_FILE_NAME));
    }

    public static void logPlayerBan(PlayerCommandPreprocessEvent event, Message messageFromDiscordConsole) {
        TextChannel banLogChannel = jda.getTextChannelById(Constants.BAN_LOG_CHANNEL_ID);
        assert banLogChannel != null;
        StringBuilder banReason = new StringBuilder();
        if (event != null){
            String[] banMessage = event.getMessage().split(" ");
            String minecraftId = event.getPlayer().getUniqueId().toString();
            String playerBannedName = "";
            try {
                playerBannedName = banMessage[1];
            } catch (ArrayIndexOutOfBoundsException ignore){

            }
            if (messageFromDiscordConsole == null){
                if (banMessage[0].equalsIgnoreCase("/ban")){
                    try {
                        for (int i = 2; i <= banMessage.length - 1; i++) {
                            banReason.append(banMessage[i]).append(" ");
                        }

                        banLogChannel.sendMessage(Api.getDiscordNameFromMCid(minecraftId) + " banned " + playerBannedName + " | Reason: " + banReason + " | Discord Username: <@" + Api.getDiscordIdFromMCid(Api.getMinecraftIdFromMinecraftName(playerBannedName)) + ">").queue();
                    } catch (NullPointerException | ArrayIndexOutOfBoundsException e){
                        banLogChannel.sendMessage(Api.getDiscordNameFromMCid(minecraftId) + " banned " + playerBannedName + " | Discord Username: <@" +  Api.getDiscordIdFromMCid(Api.getMinecraftIdFromMinecraftName(playerBannedName)) + ">").queue();
                    }
                }
                if (banMessage[0].equalsIgnoreCase("/tempban")){
                    try {
                        for (int i = 2; i <= banMessage.length - 1; i++) {
                            banReason.append(banMessage[i]).append(" ");
                        }
                        banLogChannel.sendMessage(Api.getDiscordNameFromMCid(minecraftId) + " temp banned " + playerBannedName + " | Reason: " + banReason + " | Discord Username: <@" + Api.getDiscordIdFromMCid(Api.getMinecraftIdFromMinecraftName(playerBannedName)) + ">").queue();
                    } catch (NullPointerException | ArrayIndexOutOfBoundsException e){
                        banLogChannel.sendMessage(Api.getDiscordNameFromMCid(minecraftId) + " temp banned " + playerBannedName + " | Discord Username: <@" +  Api.getDiscordIdFromMCid(Api.getMinecraftIdFromMinecraftName(playerBannedName)) + ">").queue();
                    }
                }
            } else {
                if (banMessage[0].equalsIgnoreCase("ban")){
                    try {
                        for (int i = 2; i <= banMessage.length - 2; i++) {
                            banReason.append(banMessage[i]).append(" ");
                        }
                        banLogChannel.sendMessage("<@" + Api.getDiscordIdFromMCid(minecraftId) + ">" + " banned " + banMessage[1] + " | Reason: " + banReason + " | Discord Username: <@" + Api.getDiscordIdFromMCid(Api.getMinecraftIdFromMinecraftName(playerBannedName)) + ">").queue();
                    } catch (NullPointerException | ArrayIndexOutOfBoundsException e){
                        banLogChannel.sendMessage("<@" + Api.getDiscordIdFromMCid(minecraftId) + ">" + " banned " + banMessage[1] + " | Discord Username: <@" + Api.getDiscordIdFromMCid(Api.getMinecraftIdFromMinecraftName(playerBannedName)) + ">").queue();
                    }
                }
                else if (banMessage[0].equalsIgnoreCase("tempban")){
                    try {
                        for (int i = 2; i <= banMessage.length - 2; i++) {
                            banReason.append(banMessage[i]).append(" ");
                        }
                        banLogChannel.sendMessage("<@" + Api.getDiscordIdFromMCid(minecraftId) + ">" + " temp banned " + banMessage[1] + " | Reason: " + banReason + " | Discord Username: <@" + Api.getDiscordIdFromMCid(Api.getMinecraftIdFromMinecraftName(playerBannedName)) + ">").queue();
                    } catch (NullPointerException | ArrayIndexOutOfBoundsException e){
                        banLogChannel.sendMessage("<@" + Api.getDiscordIdFromMCid(minecraftId) + ">" + " temp banned " + banMessage[1] + " | Discord Username: <@" + Api.getDiscordIdFromMCid(Api.getMinecraftIdFromMinecraftName(playerBannedName)) + ">").queue();
                    }
                }
            }
        }

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
//        FileConfiguration configCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_CONFIG_FILE_NAME));
//        DateTime currentTime = new DateTime();
//        DateTimeFormatter fmt = DateTimeFormat.forPattern("MM-dd-yyyy HH:mm:ss");
//        String current = fmt.print(currentTime);
//        DateTime startTime = fmt.parseDateTime(configCfg.getString("serverStartTime"));
//        DateTime now = fmt.parseDateTime(current);
//        Duration duration = new Duration(startTime, now);
//        minecraftChatChannel.getManager().setTopic("Online Players: " + count + "/" + maxPlayerCount + " | Server Uptime: " + duration.getStandardMinutes() + " minutes.").queue();
//    }

    public static void banPlayerStealing(InventoryClickEvent event){
        String playerName = event.getWhoClicked().getName();
        FileConfiguration configCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_CONFIG_FILE_NAME));
        Location chestLocation = new Location(event.getWhoClicked().getWorld(), configCfg.getDouble("banChest.x"), configCfg.getDouble("banChest.y"), configCfg.getDouble("banChest.z"));
        String chestMaterial = "";
        try {
            chestMaterial = Objects.requireNonNull(event.getClickedInventory()).getType().toString();
            if (chestMaterial.equalsIgnoreCase("CHEST") && chestLocation.equals(event.getClickedInventory().getLocation())){
                if (event.getCurrentItem() != null && !event.getWhoClicked().hasPermission("group.mod")) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tempban " + playerName + " 3d stole from bigboi's chest");
                    NewsChannel announcementChannel = jda.getNewsChannelById(Constants.ANNOUNCEMENT_CHANNEL_ID);
                    assert announcementChannel != null;
                    NewsChannel banLogChannel = jda.getNewsChannelById(Constants.BAN_LOG_CHANNEL_ID);
                    assert banLogChannel != null;
                    if (event.getCurrentItem().getType().equals(Material.BEACON)){
                        announcementChannel.sendMessage("<@" + Api.getDiscordIdFromMCid(event.getWhoClicked().getUniqueId().toString()) + "> GOT BEACON'd and will be temporarily banned for 3 days. :)- Their MC username is: " + playerName).queue();
                        banLogChannel.sendMessage("Minecraft Username: " + playerName + " | Discord Username: <@" + Api.getDiscordIdFromMCid(event.getWhoClicked().getUniqueId().toString()) + "> | Reason: They got BEACON'D ").queue();
                    } else {
                        announcementChannel.sendMessage("<@" + Api.getDiscordIdFromMCid(event.getWhoClicked().getUniqueId().toString()) + "> will be temporarily banned for 3 days. Reason: Stealing from BigBoi's Chest. They tried to steal " + event.getCurrentItem().getAmount() + " " + event.getCurrentItem().getType() + " :)- Their MC username is: " + playerName).queue();
                        banLogChannel.sendMessage("Minecraft Username: " + playerName + " | Discord Username: <@" + Api.getDiscordIdFromMCid(event.getWhoClicked().getUniqueId().toString()) + "> | Reason: Stealing from spawn chest | Item(s) stolen: " + event.getCurrentItem().getAmount() + " " + event.getCurrentItem().getType()).queue();

                    }
                }
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
