package discord.Backbacks;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class BackpackTabComplete implements TabCompleter {
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> backpackCommandList = new ArrayList<>();
        backpackCommandList.add("buy");
        backpackCommandList.add("open");
        backpackCommandList.add("upgrade");
        return StringUtil.copyPartialMatches(args[0], backpackCommandList, new ArrayList<>());
    }

}
