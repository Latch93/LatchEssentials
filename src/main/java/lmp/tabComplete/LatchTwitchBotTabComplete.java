package lmp.tabComplete;

import lmp.constants.ServerCommands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class LatchTwitchBotTabComplete implements TabCompleter {
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> twitchBotCommandList = new ArrayList<>();
        twitchBotCommandList.add(ServerCommands.ADD_BOT_COMMAND);
        twitchBotCommandList.add(ServerCommands.STOP_BOT_COMMAND);
        twitchBotCommandList.add(ServerCommands.START_BOT_COMMAND);
        twitchBotCommandList.add(ServerCommands.HELP_COMMAND);
        twitchBotCommandList.add(ServerCommands.SEND_TWITCH_MESSAGE_COMMAND);
        twitchBotCommandList.add(ServerCommands.ADD_CHANNEL_ID);
        return StringUtil.copyPartialMatches(args[0], twitchBotCommandList, new ArrayList<>());

    }
}
