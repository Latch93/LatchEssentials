package lmp.tabComplete;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class HardcoreTabComplete implements TabCompleter {
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> hardcoreCommandList = new ArrayList<>();
        hardcoreCommandList.add("afk");
        hardcoreCommandList.add("buy");
        hardcoreCommandList.add("warp");
        return StringUtil.copyPartialMatches(args[0], hardcoreCommandList, new ArrayList<>());
    }

}
