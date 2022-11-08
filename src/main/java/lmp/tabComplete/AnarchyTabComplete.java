package lmp.tabComplete;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class AnarchyTabComplete implements TabCompleter {
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> anarchyCommandList = new ArrayList<>();
        anarchyCommandList.add("afk");
        anarchyCommandList.add("warp");
        return StringUtil.copyPartialMatches(args[0], anarchyCommandList, new ArrayList<>());
    }

}
