package discord.LatchTwitchBot;

import com.cavariux.latchtwitch.Chat.Channel;
import com.cavariux.latchtwitch.Core.TwitchBot;
import discord.Api;
import discord.Constants;
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
        FileConfiguration twitchCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_TWITCH_FILE_NAME));
        if (args[0].equalsIgnoreCase(Constants.ADD_BOT_COMMAND) && args[1] != null && args[2] != null ){
            String twitchUsername = args[1].toLowerCase();
            twitchCfg.set(Constants.YML_PLAYERS + twitchUsername + ".twitchUsername", twitchUsername);
            twitchCfg.set(Constants.YML_PLAYERS + twitchUsername + ".minecraftUsername", player.getName().toLowerCase());
            twitchCfg.set(Constants.YML_PLAYERS + twitchUsername + ".oauthToken", args[2].toLowerCase());
            try {
                twitchCfg.save(Api.getConfigFile(Constants.YML_TWITCH_FILE_NAME));
            } catch (IOException e) {
                e.printStackTrace();
            }
            player.sendMessage(ChatColor.GREEN + "Your twitch bot credentials have been saved. Run " + ChatColor.AQUA + "/twitch start " + ChatColor.GREEN + "to connect to your twitch chat.");
            Api.messageInConsole(ChatColor.GOLD + player.getName() + ChatColor.GREEN + " added their credentials for twitch bot.");
        } else if (args[0].equalsIgnoreCase(Constants.START_BOT_COMMAND)){
            if (Api.getTwitchUsername(player.getName()) != null){
                String twitchUsername = twitchCfg.getString(Constants.YML_PLAYERS + Api.getTwitchUsername(player.getName()) + ".twitchUsername");
                String minecraftUsername = twitchCfg.getString(Constants.YML_PLAYERS + Api.getTwitchUsername(player.getName()) + ".minecraftUsername");
                String oauthToken = twitchCfg.getString(Constants.YML_PLAYERS + Api.getTwitchUsername(player.getName()) + ".oauthToken");
                assert twitchUsername != null;
                assert oauthToken != null;
                assert minecraftUsername != null;
                LatchTwitchBotRunnable twitchBot =  new LatchTwitchBotRunnable(twitchUsername, oauthToken, minecraftUsername);
                twitchBot.run();
                twitchBotList.add(twitchBot);
            } else {
                player.sendMessage(ChatColor.YELLOW + "Invalid twitch bot credentials. Go to " + ChatColor.AQUA + "https://twitchapps.com/tmi/" + ChatColor.YELLOW + " and copy the oauth token. For more info, type " + ChatColor.AQUA + "/twitch help");
            }
        } else if (args[0].equalsIgnoreCase(Constants.STOP_BOT_COMMAND)){
                Iterator<LatchTwitchBotRunnable> iter = twitchBotList.iterator();
                while(iter.hasNext()){
                    LatchTwitchBotRunnable runBot = iter.next();
                    if (runBot.getMinecraftName().equalsIgnoreCase(player.getName())){
                        LatchTwitchBot bot = runBot.bot;
                        bot.stop();
                        player.sendMessage(ChatColor.GREEN + "Your TwitchBot has been " + ChatColor.RED + "terminated.");
                        Api.messageInConsole(ChatColor.RED + "Terminated " + ChatColor.GOLD + player.getName() + "'s " + ChatColor.RED + "TwitchBot.");
                        iter.remove();
                    }
                }
        } else if (args[0].equalsIgnoreCase("stopall")){
            Api.stopAllTwitchBots(twitchBotList);
            player.sendMessage(ChatColor.GREEN + "All TwitchBots have been " + ChatColor.RED + "terminated.");
        } else if (args[0].equalsIgnoreCase(Constants.HELP_COMMAND )) {
            player.sendMessage(ChatColor.WHITE + "1.) " + ChatColor.GREEN + "You need to get your Twitch oauth to use the Twitch bot.");
            player.sendMessage(ChatColor.WHITE + "2.) " + ChatColor.GREEN + "You can get your Twitch oauth from this link -> " + ChatColor.AQUA + "https://twitchapps.com/tmi/");
            player.sendMessage(ChatColor.WHITE + "3.) " + ChatColor.GREEN + "Copy the whole oauth token which includes the 'oauth:");
            player.sendMessage(ChatColor.WHITE + "4.) " + ChatColor.GREEN + "To add a Twitch bot, type " + ChatColor.AQUA + "/twitch addBot [twitchUsername] [oauthToken]");
            player.sendMessage(ChatColor.WHITE + "5.) " + ChatColor.GREEN + "To start your Twitch bot, type " + ChatColor.AQUA + "/twitch run");
            player.sendMessage(ChatColor.WHITE + "6.) " + ChatColor.GREEN + "Before you log off the server for the day, you should stop your Twitch bot.");
            player.sendMessage(ChatColor.WHITE + "7.) " + ChatColor.GREEN + "To do this, type " + ChatColor.AQUA + "/twitch stop");
        } else if (args[0].equalsIgnoreCase("sendMessage")){
            LatchTwitchBot bot = Api.getPlayerTwitchBot(twitchBotList, player.getName());
            Channel twitchChannel = bot.joinChannel(Api.getTwitchChannelName(player.getName()));
            StringBuilder messageString = new StringBuilder();
            for (int i = 1; i < args.length; i++){
                messageString.append(args[i]).append(" ");
            }
            bot.sendMessage(messageString, twitchChannel);
         }
        return false;
    }
}
