package lmp.commands;

import lmp.Constants;
import lmp.api.Api;
import lmp.constants.ServerCommands;
import lmp.constants.YmlFileNames;
import lmp.runnable.LatchTwitchBotRunnable;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LatchTwitchBotCommand implements CommandExecutor {
    public static List<LatchTwitchBotRunnable> twitchBotList = new ArrayList<>();

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        FileConfiguration twitchCfg = Api.getFileConfiguration(YmlFileNames.YML_TWITCH_FILE_NAME);
        try {
            if (args[0].equalsIgnoreCase(ServerCommands.ADD_BOT_COMMAND) && args[1] != null && args[2] != null) {
                String twitchUsername = args[1].toLowerCase();
                twitchCfg.set(Constants.YML_PLAYERS + twitchUsername + ".twitchUsername", twitchUsername);
                twitchCfg.set(Constants.YML_PLAYERS + twitchUsername + ".minecraftUsername", player.getName().toLowerCase());
                twitchCfg.set(Constants.YML_PLAYERS + twitchUsername + ".oauthToken", args[2].toLowerCase());
                try {
                    twitchCfg.save(Api.getConfigFile(YmlFileNames.YML_TWITCH_FILE_NAME));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                player.sendMessage(ChatColor.GREEN + "Your twitch bot credentials have been saved. Run " + ChatColor.AQUA + "/twitch start " + ChatColor.GREEN + "to connect to your twitch chat.");
                Api.messageInConsole(ChatColor.GOLD + player.getName() + ChatColor.GREEN + " added their credentials for twitch bot.");
            } else if (args[0].equalsIgnoreCase(ServerCommands.START_BOT_COMMAND)) {
                FileConfiguration cfg = Api.getFileConfiguration(YmlFileNames.YML_TWITCH_FILE_NAME);
//                for(String user : cfg.getConfigurationSection("players").getKeys(false)) {
//                    String twitchUsername = twitchCfg.getString(Constants.YML_PLAYERS + user + ".twitchUsername");
//                    String minecraftUsername = twitchCfg.getString(Constants.YML_PLAYERS + user + ".minecraftUsername");
//                    String oauthToken = twitchCfg.getString(Constants.YML_PLAYERS + user + ".oauthToken");
//                    LatchTwitchBotRunnable twitchBot =  new LatchTwitchBotRunnable(twitchUsername, oauthToken, minecraftUsername, null);
//                    twitchBot.run();
//                    twitchBotList.add(twitchBot);
//                }
                if (Api.getTwitchUsername(player.getName()) != null) {
                    String twitchUsername = twitchCfg.getString(Constants.YML_PLAYERS + Api.getTwitchUsername(player.getName()).toLowerCase() + ".twitchUsername");
                    String minecraftUsername = twitchCfg.getString(Constants.YML_PLAYERS + Api.getTwitchUsername(player.getName()).toLowerCase() + ".minecraftUsername");
                    String oauthToken = twitchCfg.getString(Constants.YML_PLAYERS + Api.getTwitchUsername(player.getName()) + ".oauthToken");
                    String channelID = null;
                    if (twitchCfg.isSet(Constants.YML_PLAYERS + Api.getTwitchUsername(player.getName()) + ".channelID")) {
                        channelID = String.valueOf(twitchCfg.getInt(Constants.YML_PLAYERS + Api.getTwitchUsername(player.getName()) + ".channelID"));
                    }
                    assert twitchUsername != null;
                    assert oauthToken != null;
                    assert minecraftUsername != null;
                    LatchTwitchBotRunnable twitchBot = new LatchTwitchBotRunnable(twitchUsername, oauthToken, minecraftUsername, channelID);
                    twitchBot.run();
                    twitchBotList.add(twitchBot);


                } else {
                    player.sendMessage(ChatColor.YELLOW + "Invalid twitch bot credentials. Go to " + ChatColor.AQUA + "https://twitchapps.com/tmi/" + ChatColor.YELLOW + " and copy the oauth token. For more info, type " + ChatColor.AQUA + "/twitch help");
                }
            } else if (args[0].equalsIgnoreCase(ServerCommands.STOP_BOT_COMMAND)) {
                Api.stopTwitchBot(twitchBotList, player);
            } else if (args[0].equalsIgnoreCase("stopall")) {
                Api.stopAllTwitchBots(twitchBotList);
                player.sendMessage(ChatColor.GREEN + "All TwitchBots have been " + ChatColor.RED + "terminated.");
            } else if (args[0].equalsIgnoreCase(ServerCommands.HELP_COMMAND)) {
                player.sendMessage(ChatColor.WHITE + "1.) " + ChatColor.GREEN + "You need to get your Twitch oauth to use the Twitch bot.");
                player.sendMessage(ChatColor.WHITE + "2.) " + ChatColor.GREEN + "You can get your Twitch oauth from this link -> " + ChatColor.AQUA + "https://twitchtokengenerator.com/");
                player.sendMessage(ChatColor.WHITE + "3.) " + ChatColor.GREEN + "Click on " + ChatColor.AQUA + "Bot Chat Token" + ChatColor.GREEN + " button");
                player.sendMessage(ChatColor.WHITE + "4.) " + ChatColor.GREEN + "Click the " + ChatColor.AQUA + "Authorize" + ChatColor.GREEN + " button");
                player.sendMessage(ChatColor.WHITE + "5.) " + ChatColor.GREEN + "Scroll down to the " + ChatColor.AQUA + "Available Token Scopes" + ChatColor.GREEN + " section and click the " + ChatColor.AQUA + "Select All" + ChatColor.GREEN + " button");
                player.sendMessage(ChatColor.WHITE + "6.) " + ChatColor.GREEN + "Click the " + ChatColor.AQUA + "Generate Token!" + ChatColor.GREEN + " button");
                player.sendMessage(ChatColor.WHITE + "7.) " + ChatColor.GREEN + "Copy the string of characters in the " + ChatColor.AQUA + "Access Token" + ChatColor.GREEN + " box");
                player.sendMessage(ChatColor.WHITE + "8.) " + ChatColor.GREEN + "To add a Twitch bot, type " + ChatColor.AQUA + "/twitch addBot [twitchUsername] [oauthToken]");
                player.sendMessage(ChatColor.WHITE + "9.) " + ChatColor.GREEN + "To start your Twitch bot, type " + ChatColor.AQUA + "/twitch run");
                player.sendMessage(ChatColor.WHITE + "10.) " + ChatColor.GREEN + "Before you log off the server for the day, you should stop your Twitch bot.");
                player.sendMessage(ChatColor.WHITE + "11.) " + ChatColor.GREEN + "To do this, type " + ChatColor.AQUA + "/twitch stop");
                player.sendMessage(ChatColor.WHITE + "12.) " + ChatColor.GREEN + "You can download a Chrome extension to get your twitch channelId. It is a string of numbers. Example: 161082218");
                player.sendMessage(ChatColor.WHITE + "13.) " + ChatColor.GREEN + "If you want to get Follow/Subscriber/Bits/GoLive event messages in minecraft, you need to add your channelID to your bot credentials.");
                player.sendMessage(ChatColor.WHITE + "14.) " + ChatColor.GREEN + "To add your channelID to your bot credentials, type " + ChatColor.AQUA + "/twitch addChannelId [channelId]");
            } else if (args[0].equalsIgnoreCase(ServerCommands.SEND_TWITCH_MESSAGE_COMMAND)) {
                Iterator<LatchTwitchBotRunnable> iter = twitchBotList.iterator();
                StringBuilder messageString = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    messageString.append(args[i]).append(" ");
                }
                while (iter.hasNext()) {
                    LatchTwitchBotRunnable runBot = iter.next();
                    if (runBot.getMinecraftName().equalsIgnoreCase(player.getName())) {
                        runBot.getTwitchClient().getChat().sendMessage(runBot.getTwitchName(), String.valueOf(messageString));
                    }
                }
            } else if (args[0].equalsIgnoreCase(ServerCommands.ADD_CHANNEL_ID)) {
                if (args[1] != null) {
                    twitchCfg.set(Constants.YML_PLAYERS + Api.getTwitchUsername(player.getName()) + ".channelID", args[1]);
                    twitchCfg.save(Api.getConfigFile(YmlFileNames.YML_TWITCH_FILE_NAME));
                    player.sendMessage(ChatColor.GREEN + "Your Twitch Channel ID has been added to your Twitch Bot. Run " + ChatColor.AQUA + "/twitch start " + ChatColor.GREEN + "to connect to your twitch chat.");

                } else {
                    player.sendMessage(ChatColor.RED + "Must enter in a channelID. Use command like this -> " + ChatColor.AQUA + "/twitch addChannelId [channelId]");
                }
            }
        } catch (ArrayIndexOutOfBoundsException | IOException e) {
            player.sendMessage(ChatColor.RED + "Invalid command. Please type -> " + ChatColor.AQUA + "/twitch help");
        }

        return false;
    }
}
