package lmp.LatchTwitchBot;

import lmp.Constants;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class LatchTwitchBotTabComplete implements TabCompleter {
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> twitchBotCommandList = new ArrayList<>();
        twitchBotCommandList.add(Constants.ADD_BOT_COMMAND);
        twitchBotCommandList.add(Constants.STOP_BOT_COMMAND);
        twitchBotCommandList.add(Constants.START_BOT_COMMAND);
        twitchBotCommandList.add(Constants.HELP_COMMAND);
        twitchBotCommandList.add(Constants.SEND_TWITCH_MESSAGE_COMMAND);
        twitchBotCommandList.add(Constants.ADD_CHANNEL_ID);
        return StringUtil.copyPartialMatches(args[0], twitchBotCommandList, new ArrayList<>());

    }
}
