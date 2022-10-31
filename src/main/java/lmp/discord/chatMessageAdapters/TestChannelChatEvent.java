package lmp.discord.chatMessageAdapters;

import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static lmp.LatchDiscord.*;
public class TestChannelChatEvent extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent e){
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
        if (messageChannel.getId().equals(lmp.Constants.TEST_CHANNEL_ID) && messageContents.equalsIgnoreCase("!mark")) {
            TextChannel byebyeDiscordChat = jda.getTextChannelById(lmp.Constants.TEST_CHANNEL_ID);
            assert byebyeDiscordChat != null;
            String filePath = Api.getMainPlugin().getDataFolder().getPath();
            String folderName = "/gifs";


            int gifCount = Objects.requireNonNull(new File(filePath + folderName).list()).length;
            Random rand = new Random();
            int n = rand.nextInt(gifCount);
            byebyeDiscordChat.sendMessage(e.getMember().getUser().getName() + " left Discord. ").addFiles(FileUpload.fromData(new File(filePath + folderName + "/bye_bye" + n + ".gif"))).queue();
        }
        if (messageChannel.getId().equals(lmp.Constants.TEST_CHANNEL_ID) && messageContents.equalsIgnoreCase("!gifs")) {
            ThreadChannel gifThread = jda.getThreadChannelById("1034135259060650075");
            FileConfiguration gifCfg = Api.getFileConfiguration(YmlFileNames.YML_GIF_LIST_FILE_NAME);
            List<String> giflinkList = new ArrayList<>();
            List<Message> messageList;
            assert gifThread != null;
            try {
                messageList = gifThread.getIterableHistory().takeAsync(50) // Collect 1000 messages
                        .thenApply(ArrayList::new)
                        .get();
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }
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
            try {
                gifCfg.save(Api.getConfigFile(YmlFileNames.YML_GIF_LIST_FILE_NAME));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            int count = 0;
            for (String gifURL : giflinkList) {
                byte[] b = new byte[1];
                URL url;
                try {
                    url = new URL(gifURL);
                } catch (MalformedURLException ex) {
                    throw new RuntimeException(ex);
                }
                URLConnection urlConnection;
                try {
                    urlConnection = url.openConnection();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                try {
                    urlConnection.connect();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                DataInputStream di;
                try {
                    di = new DataInputStream(urlConnection.getInputStream());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                String filePath = Api.getMainPlugin().getDataFolder().getPath();
                String fileName = "/gifs/bye_bye" + count + ".gif";
                FileOutputStream fo;
                try {
                    fo = new FileOutputStream(filePath + fileName);
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
                while (true) {
                    try {
                        if (-1 == di.read(b, 0, 1)) break;
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    try {
                        fo.write(b, 0, 1);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                try {
                    di.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                try {
                    fo.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                count++;
            }
        }
        if (messageChannel.getId().equals(lmp.Constants.TEST_CHANNEL_ID) && messageContents.equalsIgnoreCase("!compare")) {
            List<String> tableIDS;
            try {
                tableIDS = Files.readAllLines(Paths.get("C:/Users/Latch/Desktop/sqlt_data_1_2015_01.txt"));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
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
            try {
                test.save(Api.getConfigFile("deleteList"));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        if (messageChannel.getId().equals(lmp.Constants.TEST_CHANNEL_ID) && messageContents.equalsIgnoreCase("claim")) {
            FileConfiguration claimFileCfg = Api.getFileConfiguration(YmlFileNames.YML_USER_DONATION_REWARD_FILE_NAME);
            for (String user : Objects.requireNonNull(claimFileCfg.getConfigurationSection("players")).getKeys(false)) {
                if (!Api.getMinecraftIDOfLinkedPlayersInDiscord().contains(user)) {
                    claimFileCfg.set(lmp.Constants.YML_PLAYERS + user, null);
                }
            }
            try {
                claimFileCfg.save(Api.getConfigFile(YmlFileNames.YML_USER_DONATION_REWARD_FILE_NAME));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        if (messageChannel.getId().equals(lmp.Constants.TEST_CHANNEL_ID) && messageContents.contains("cong")) {
            e.getAuthor().openPrivateChannel().flatMap(dm -> dm.sendMessage("""
                    Welcome to LMP (Latch Multiplayer)
                    Users on PS4|XBox|Switch|Mobile are able to join
                    Please read the rules in the Rules messageChannel and react to the rules messageContents (thumbUp)
                    Doing this assigns you the member role and access to all of the channels.
                    You can get the IP in the server-ip messageChannel.
                    For Bedrock|Console users, you need to download the mobile app Bedrock Together:
                    Android: https://play.google.com/store/apps/details?id=pl.extollite.bedrocktogetherapp&hl=en_US&gl=US
                    iOS: https://apps.apple.com/us/app/bedrocktogether/id1534593376
                    When you first join the server, you won't be able to move because you need to link your Discord account with your Minecraft Account.
                    In order to link accounts and move around the server freely, go to the General messageChannel in the Discord Server and type the following command -> !link
                    My bot will generate a command for you run on your minecraft client chat.
                    Copy and paste this command or type it out, its formatted like this -> /lmp link [discordID]
                    I link accounts so you can use the features I coded into the game.
                    View our wiki with commands and other information here -> https://github.com/Latch93/DiscordText/wiki/Server-Commands
                    If you have any questions, feel free to post them in the Discord and Happy Mining!!!""")).queue();
        }
        if (messageChannel.getId().equals(lmp.Constants.TEST_CHANNEL_ID) && messageContents.contains("pog")) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
            //Here you say to java the initial timezone. This is the secret
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            //Will print in UTC
            FileConfiguration whitelistCfg = Api.getFileConfiguration(YmlFileNames.YML_WHITELIST_FILE_NAME);
            for (String user : Objects.requireNonNull(whitelistCfg.getConfigurationSection("players")).getKeys(false)) {
                Main.log.info(sdf.format(calendar.getTime()));
                sdf.setTimeZone(TimeZone.getTimeZone(whitelistCfg.getString("players." + user + ".ip-info.timezoneName")));
                Main.log.info("Time for " + whitelistCfg.getString("players." + user + ".discordName") + " is -> " + sdf.format(calendar.getTime()));
            }
            //Here you set to your timezone
            //Will print on your default Timezone
        }
        if (messageChannel.getId().equalsIgnoreCase(lmp.Constants.TEST_CHANNEL_ID) && messageContents.equalsIgnoreCase("plop")) {
            try {
                Api.setIsPlayerInDiscord();
            } catch (IOException error) {
                error.printStackTrace();
            }
        }
    }
}
